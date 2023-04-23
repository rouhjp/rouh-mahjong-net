package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * ターン外行動の選択を管理するクラス。
 * <p>ある打牌に対するターン外行動は, 打牌者を除くプレイヤー3人に同時に選択を要求し,
 * 出揃った行動のうち最も優先度の高いものが選出される形で決定します。
 * このクラスではこの操作を管理します。
 * @author Rouh
 * @version 1.0
 */
public class CallActionMediator{
    private final Logger LOG = LoggerFactory.getLogger(CallActionMediator.class);
    private final List<Wind> winds;
    private final Map<Wind, TableStrategy> playerMap = new HashMap<>();
    private final Map<Wind, List<CallAction>> choicesMap = new HashMap<>();

    /**
     * 指定された風のプレイヤー間のターン外行動の選択を管理するインスタンスを生成します。
     * @param targets 対象プレイヤーの自風
     */
    public CallActionMediator(List<Wind> targets){
        this.winds = targets;
    }

    /**
     * プレイヤーの戦略オブジェクトを設定します。
     * @param playerSupplier プレイヤーの戦略オブジェクトの供給関数
     * @return このオブジェクトの参照
     */
    public CallActionMediator withPlayers(Function<Wind, TableStrategy> playerSupplier){
        for(var target:winds){
            playerMap.put(target, playerSupplier.apply(target));
        }
        return this;
    }

    /**
     * プレイヤーの選択肢を設定します。
     * @param choicesSupplier プレイヤーの選択肢の供給関数
     * @return このオブジェクトの参照
     */
    public CallActionMediator withChoices(Function<Wind, List<CallAction>> choicesSupplier){
        for(var target:winds){
            choicesMap.put(target, choicesSupplier.apply(target));
        }
        return this;
    }

    /**
     * 署名付きターン外行動
     * @param from この選択を行ったプレイヤーの自風
     * @param action 選択した行動
     */
    private record SignedCallAction(Wind from, CallAction action){}

    /**
     * 各プレイヤーの選択を確認するまで待機し, 最終的に適用される行動をマップ形式で取得します。
     * <p>返されるマップの形式は以下の通りです。
     * <ul>
     *   <li>ロンの選択が発生した場合は, 宣言者の自風と行動のマップ(サイズ1~3)が返されます。</li>
     *   <li>ポン・カン・チーの選択が発生した場合は, 宣言者の自風と行動のマップ(サイズ1)が返されます。</li>
     *   <li>全員がパスの場合は, 空のマップが返されます。</li>
     * </ul>
     * <p>この操作では以下の通り必要に応じ新規スレッドの開始や,
     * {@link TableStrategy#selectCallAction}メソッドを用いたプレイヤーへの選択要求を行います。
     * <ul>
     *   <li>全てのプレイヤーの選択肢がパスのみの場合, 新規スレッドを開始せず空のマップを返します。</li>
     *   <li>あるプレイヤーの選択肢がパスのみの場合, プレイヤーへの選択を要求せずにパスを返します。</li>
     *   <li>あるプレイヤーが選択を回答した時点で, 他プレイヤーがその行動よりも優先度の高い行動を取り得ない場合
     * 他プレイヤーへの選択要求スレッドを中断してマップを返します。</li>
     * </ul>
     * <p>このメソッドを呼び出す前にあらかじめ{@link #withPlayers}及び{@link #withChoices}を用いて
     * 必要なパラメータを設定しておく必要があります。
     * @throws IllegalStateException プレイヤーと選択肢が設定されていない場合
     * @return プレイヤーの自風と行動のマップ
     */
    public Map<Wind, CallAction> mediate(){
        if(playerMap.isEmpty() || choicesMap.isEmpty()) throw new IllegalStateException("parameter not initialized yet");
        for(var key:choicesMap.keySet()){
            LOG.info("choices({}): ", choicesMap.get(key));
        }

        var winds = playerMap.keySet();
        var highestPriorityMap = new HashMap<Wind, Integer>();
        for(var wind:winds){
            var highestPriority = choicesMap.get(wind).stream()
                    .mapToInt(CallAction::priority).max().orElse(CallAction.PASS_PRIORITY);
            highestPriorityMap.put(wind, highestPriority);
        }
        // return immediately if all player can select nothing but pass action
        if(highestPriorityMap.values().stream().allMatch(priority->priority==CallAction.PASS_PRIORITY)){
            return Map.of();
        }
        var executorService = Executors.newFixedThreadPool(playerMap.size());
        var completionService = new ExecutorCompletionService<SignedCallAction>(executorService);
        for(var wind:winds){
            var player = playerMap.get(wind);
            var choices = choicesMap.get(wind);
            // if player can select single action only, the action will be selected automatically
            completionService.submit(()->choices.size()==1?
                    new SignedCallAction(wind, choices.get(0)):
                    new SignedCallAction(wind, player.selectCallAction(choices)));
        }
        executorService.shutdown();
        try{
            var answerMap = new HashMap<Wind, CallAction>();
            for(int i = 0; i<winds.size(); i++){
                var answer = completionService.take().get();
                LOG.info("answer({}) {}", answer.from, answer.action);
                if(!choicesMap.get(answer.from).contains(answer.action)){
                    throw new IllegalArgumentException("illegal action has been detected: choices:"+choicesMap.get(answer.from)+" the choice:"+answer.action);
                }
                highestPriorityMap.remove(answer.from());
                if(answer.action().priority()!=CallAction.PASS_PRIORITY){
                    answerMap.entrySet().removeIf(entry->entry.getValue().priority()<answer.action.priority());
                    answerMap.put(answer.from, answer.action);
                    // return immediately if all remaining actions are lower priority
                    if(highestPriorityMap.values().stream().noneMatch(priority->priority>=answer.action().priority())){
                        executorService.shutdownNow();
                        LOG.info("answer interrupted");
                        return answerMap;
                    }
                }
            }
            return answerMap;
        }catch(InterruptedException | ExecutionException e){
            LOG.error("call action mediator interrupted", e);
            throw new RuntimeException(e);
        }
    }
}
