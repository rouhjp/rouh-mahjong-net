package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Wind;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * プレイヤー間の支払い額を保持するクラス。
 * <h2>和了時の決済</h2>
 * <h3>和了手の精算</h3>
 * <p>和了時の決済では一般に, ロンの場合, 放銃者がその得点の全額を,
 * 親のツモの場合は, その他全ての子プレイヤーが各その得点の1/3を,
 * 子のツモの場合は, 親プレイヤーがその得点の1/2, その他の子プレイヤーが1/4を支払います。
 * <p>例外的なケースとして, 大明槓包及び役満包(大三元/大四喜/四槓子の完成責任払い)があります。
 * <p>大明槓包は, ツモ和了の場合でも, そのツモが大明槓による嶺上牌でのツモの時,
 * 大明槓元の牌を打牌したプレイヤーが放銃者と同等の扱いとなり, 全額を支払う責任が発生します。
 * 以下, 役満包の説明中に記述する放銃者には大明槓包のケースを含みます。
 * <p>役満包(大三元/大四喜/四槓子 責任払い)は, 面子系の役満の最後の面子を副露させた(役を完成させた)
 * プレイヤーが放銃者と同等の責任を追う規定です。例えば, 大三元の包では, 既に白および發の面子を
 * 副露しているプレイヤーに中の副露を許したプレイヤーは包が発生し, そのプレイヤーが大三元の手を
 * 和了した際に責任払いが生じます。
 * <p>役満包が発生した場合, ツモ和了の場合は包責任者が全額を,
 * ロン和了で, かつ包責任者と放銃者が一致する場合はそのプレイヤーが全額を,
 * ロン和了で, かつ包責任者と放銃者が一致しない場合は, 両プレイヤーが1/2を支払います。
 * <p>また, 役満包は完成させた役に対する支払責任のみとなるため, 複数の役満手の場合は
 * それらを一つ一つに分けて支払い額を決定する必要があります。
 * 例えば, 大三元および字一色の手が和了され, 南家が大三元の包責任者, 西家が放銃者である場合,
 * まず大三元について包責任者の南家と放銃者の西家がそれぞれ大三元の点数の1/2を支払います。
 * 次に字一色について, 包と無関係のため南家は支払いの責任はなく,
 * 西家は放銃者として字一色の点数の全額を支払います。
 * <p>さらに, 役満包は複数発生するケースが考えられます。
 * 大三元と四槓子の包が同時に発生したケース, および大四喜と四槓子の包が同時に発生したケースです。
 * 前者の具体的な例としては, 3度目の副露で大三元が確定し, 4度目の副露(大明槓)で四槓子が確定したケースです。
 * この場合も前述と同様, 各役満に対して包者および放銃者を考慮して支払い額を決定します
 * <p>支払額は, 100の位で切り上げになります。
 * このため, 和了手の得点と支払額の合計は必ずしも一致しないことになります。
 * 例えば, 子の7700点のツモについて, 親が1/2の3850点, 子が1/4の1925点を負担しますが,
 * 100の位で切り上げが発生するため, 親は3900点, 子2人は2000点を支払い, その合計額は7900点となります。
 * <h3>供託及び詰み符の精算</h3>
 * <p>和了者は和了手の決済で得た収入のほかに, 供託及びリーチ棒を取得します。
 * <p>詰み符は和了者が追加で得る本場数*300点の追加点です。
 * 和了手に対する支払い者が同様に詰み符の支払い責任を負います。
 * 和了手に対する支払い額は包の適用範囲や親の倍払いなどによって負担額は一律ではありませんが,
 * 詰み符の支払いは一律に同額を負担します。その際に100の位未満の端数が発生した場合は,
 * 和了手に対する支払い同様に切り上げた額を支払います。
 * 例えば, 1本場300点を包責任者と放銃者の二人が支払う場合,
 * 150点を切り上げた200点をそれぞれが支払います。
 * <p>ダブロンが発生した場合, 供託を二重に支給してしまうと
 * 開局時と終局時の得点の合計値が不一致となってしまうため注意が必要です。
 * 二度目の支払額を算出する際は{@link SettlementContext#getTotalDepositCount()}の値を0にする必要があります。
 * <h2>流局時の決済</h2>
 * <p>流局時はノーテン罰符の支払いが発生します。ノーテン罰符による点数移動は以下の通りです。
 * <ul>
 *   <li>全員がノーテンの場合, 点数移動は発生しません。</li>
 *   <li>聴牌のプレイヤーが一名の場合, ノーテンのプレイヤーが各1000点を支払い, 聴牌のプレイヤーが3000点を受け取ります。</li>
 *   <li>聴牌のプレイヤーが二名の場合, ノーテンのプレイヤーが各1500点を支払い, 聴牌のプレイヤーが各1500点を受け取ります。</li>
 *   <li>聴牌のプレイヤーが三名の場合, ノーテンのプレイヤーが3000点を支払い, 聴牌のプレイヤーが各1000点を受け取ります。</li>
 *   <li>全員が聴牌の場合, 点数移動は発生しません。</li>
 * </ul>
 * @author Rouh
 * @version 1.0
 */
public class Settlement{
    private final Map<Wind, Integer> payments;
    private final int streakCount;
    private final int depositCount;

    private Settlement(Map<Wind, Integer> payments, int streak, int deposit){
        this.payments = Map.copyOf(payments);
        this.streakCount = streak;
        this.depositCount = deposit;
    }

    /**
     * 指定した自風のプレイヤーの支払額を取得します。
     * <p>プレイヤーが点数を受取る場合は正の値を,
     * プレイヤーが点数を支払う場合は負の値を取得します。
     * @param wind 自風
     * @return 支払額
     */
    public int getIncomeOf(Wind wind){
        return payments.get(wind);
    }

    /**
     * この支払額に適用された(あるいは保留された)本場数を取得します。
     * @return 本場数
     */
    public int getStreakCount(){
        return streakCount;
    }

    /**
     * この支払額に適用された(あるいは保留された)供託数を取得します。
     * @return 供託数
     */
    public int getDepositCount(){
        return depositCount;
    }

    /**
     * 流局時の決済オブジェクトを取得します。
     * @param handReadies 聴牌のプレイヤーの自風リスト
     * @return 決済
     */
    public static Settlement ofDrawn(List<Wind> handReadies, int streak, int deposit){
        if(handReadies.isEmpty() || handReadies.size()==4){
            //0, 0, 0, 0
            return new Settlement(Map.of(Wind.EAST, 0, Wind.SOUTH, 0, Wind.WEST, 0, Wind.NORTH, 0), streak, deposit);
        }else{
            //+3000, -1000, -1000, -1000
            //+1500, +1500, -1500, -1500
            //+1000, +1000, +1000, -3000
            var payments = new HashMap<Wind, Integer>();
            for(var wind:Wind.values()){
                if(handReadies.contains(wind)){
                    payments.put(wind, 3000/handReadies.size());
                }else{
                    payments.put(wind, -1*(3000/(4 - handReadies.size())));
                }
            }
            return new Settlement(payments, streak, deposit);
        }
    }

    /**
     * 和了時の決済オブジェクトを取得します。
     * @param handScore 得点
     * @param sc 決済状況
     * @param wc 和了状況
     * @param openMelds 公開面子(成立順に順序が保たれている必要があります)
     * @return 決済
     */
    public static Settlement of(HandScore handScore, SettlementContext sc, WinningContext wc, List<Meld> openMelds){
        return of(handScore, sc, wc, new LinkedList<>(openMelds));
    }

    private static Settlement of(HandScore handScore, SettlementContext sc, WinningContext wc, LinkedList<Meld> openMelds){
        var paymentMap = Stream.of(Wind.values())
                .collect(Collectors.toMap(Function.identity(), wind->0));
        var suppliers = new HashSet<Wind>();
        var winner = wc.getSeatWind();
        var winningTileSupplier = (Wind)null;
        if(!wc.isTsumo()){
            //放銃責任
            winningTileSupplier = sc.getWinningSide().of(winner);
            suppliers.add(winningTileSupplier);
        }else if(wc.isQuadTileDrawWin() && openMelds.getLast().isCallQuad()){
            //大明槓責任
            winningTileSupplier = openMelds.getLast().getSourceSide().of(winner);
            suppliers.add(winningTileSupplier);
        }
        for(var subHandScore: handScore.disorganize()){
            //役満包責任
            var completingTileSupplier = (Wind)null;
            if(subHandScore.isHandLimit()){
                var handType = (LimitHandType)subHandScore.getHandTypes().get(0);
                switch(handType){
                    case BIG_THREE -> {
                        //大三元の構成面子がすべて公開面子にある場合
                        if(openMelds.stream().filter(Meld::isDragon).count()>=3){
                            var lastDragonMeld = IntStream.iterate(openMelds.size(), i -> i - 1)
                                    .mapToObj(openMelds::get).filter(Meld::isDragon).findFirst().orElseThrow();
                            //大三元の最後の公開面子が他家からの副露である場合
                            if(lastDragonMeld.isCallQuad()){
                                completingTileSupplier = lastDragonMeld.getSourceSide().of(winner);
                                suppliers.add(completingTileSupplier);
                            }
                        }
                    }
                    case BIG_WIND -> {
                        //大四喜の構成面子がすべて公開面子にある場合
                        if(openMelds.stream().filter(Meld::isWind).count()>=4){
                            var lastWindMeld = openMelds.getLast();
                            //大四喜の最後の公開面子が他家からの副露である場合
                            if(lastWindMeld.isCallQuad()){
                                completingTileSupplier = lastWindMeld.getSourceSide().of(winner);
                                suppliers.add(completingTileSupplier);
                            }
                        }
                    }
                    case FOUR_QUADS -> {
                        var lastQuad = openMelds.getLast();
                        //四槓子の最後の公開面子が他家からの副露である場合
                        if(lastQuad.isCallQuad()){
                            completingTileSupplier = lastQuad.getSourceSide().of(winner);
                            suppliers.add(completingTileSupplier);
                        }
                    }
                }
            }
            int score = subHandScore.getScore();
            paymentMap.merge(winner, score, Integer::sum);
            if(completingTileSupplier!=null){
                if(winningTileSupplier!=null){
                    //ロン 包あり or ツモ 大明槓あり 包あり
                    //包:放銃者:他=50:50:0
                    //包・放銃者:他:他=100:0:0
                    if(winningTileSupplier==completingTileSupplier){
                        paymentMap.merge(winningTileSupplier, -score, Integer::sum);
                    }else{
                        paymentMap.merge(winningTileSupplier, -1*ceil(score/2), Integer::sum);
                        paymentMap.merge(completingTileSupplier, -1*ceil(score/2), Integer::sum);
                    }
                }else{
                    //ツモ 包あり
                    //包:他:他=100:0:0
                    paymentMap.merge(completingTileSupplier, -score, Integer::sum);
                }
            }else{
                if(winningTileSupplier!=null){
                    //ロン or 大明槓あり
                    //放銃者:他:他=100:0:0
                    paymentMap.merge(winningTileSupplier, -score, Integer::sum);
                }else{
                    //ツモ
                    //子:子:子=33:33:33
                    //親:子:子=50:25:25
                    if(wc.isDealer()){
                        for(var side: Side.SELF.others()){
                            paymentMap.merge(side.of(winner), -1*ceil(score/3), Integer::sum);
                        }
                    }else{
                        for(var side: Side.SELF.others()){
                            if(side.of(winner)==Wind.EAST){
                                paymentMap.merge(side.of(winner), -1*ceil(score/2), Integer::sum);
                            }else{
                                paymentMap.merge(side.of(winner), -1*ceil(score/4), Integer::sum);
                            }
                        }
                    }
                }
            }
        }
        //供託・詰み符
        int depositScore = sc.getTotalDepositCount()*1000;
        int streakScore = sc.getRoundStreakCount()*300;
        paymentMap.merge(winner, depositScore, Integer::sum);
        paymentMap.merge(winner, streakScore, Integer::sum);
        if(suppliers.isEmpty()){
            for(var side:Side.SELF.others()){
                paymentMap.merge(side.of(winner), -1*ceil(streakScore/3), Integer::sum);
            }
        }else{
            for(var supplier:suppliers){
                paymentMap.merge(supplier, -1*ceil(streakScore/suppliers.size()), Integer::sum);
            }
        }
        return new Settlement(paymentMap, sc.getRoundStreakCount(), sc.getRoundStreakCount());
    }

    private static int ceil(int score){
        return (int)Math.ceil(score/100.)*100;
    }
}
