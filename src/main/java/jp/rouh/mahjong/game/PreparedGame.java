package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.DiceTwin;
import jp.rouh.mahjong.tile.Wind;
import jp.rouh.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static jp.rouh.mahjong.tile.Wind.EAST;

/**
 * 席決め後の対局。
 * @author Rouh
 * @version 1.0
 */
public class PreparedGame implements GameAccessor{
    private static final Logger LOG = LoggerFactory.getLogger(PreparedGame.class);
    private static final int DEFAULT_SCORE = 25000;
    private static final int RETURN_SCORE = 30000;
    private static final int BIG_RANK_SCORE = 20000;
    private static final int SMALL_RANK_SCORE = 10000;
    private final Map<Wind, GamePlayer> gamePlayers;
    private final GameSpan span;
    private final WallGenerator wallGenerator;

    /**
     * コンストラクタ。
     * @param players プレイヤーの対局開始時の自風に対するマップ
     * @param span 局スパン
     */
    PreparedGame(Map<Wind, Player> players, GameSpan span){
        this(players, span, null);
    }

    /**
     * コンストラクタ。
     * @param players プレイヤーの対局開始時の自風に対するマップ
     * @param span 局スパン
     * @param wallGenerator 牌山生成器
     */
    PreparedGame(Map<Wind, Player> players, GameSpan span, WallGenerator wallGenerator){
        this.gamePlayers = Maps.mapValue(players, (wind, player)->new GamePlayer(this, player, wind));
        this.span = span;
        this.wallGenerator = wallGenerator;
    }

    void start(){
        var roundId = span.getFirstRoundSign();
        int streak = 0;
        int deposit = 0;
        var playerList = List.copyOf(gamePlayers.values());
        boolean finished = false;
        while(!finished){
            boolean last = span.isLastRound(roundId);
            var params = new RoundParameter(roundId, streak, deposit, last);
            var round = wallGenerator==null?
                    new Round(params, playerList):
                    new Round(params, playerList, wallGenerator);
            var dices = DiceTwin.roll();
            var result = round.start(dices.firstValue(), dices.secondValue());
            if(span.hasExtended()){
                if(playerList.stream().anyMatch(player->player.getScore()>=30000)){
                    //サドンデスによる終局
                    finished = true;
                }
            }
            if(last){
                if(result.isDealerAdvantage()){
                    int count = roundId.count();
                    var dealer = playerList.stream()
                            .filter(player->player.getSeatWindAt(count)==EAST)
                            .findAny().orElseThrow();
                    if(dealer.getRank()==1 && dealer.getScore()>=30000){
                        //オーラス和了止めにより終局
                        finished = true;
                    }
                }else{
                    if(playerList.stream().allMatch(player->player.getScore()<30000)){
                        span.extend();
                    }else{
                        //流局による終局
                        finished = true;
                    }
                }
            }else if(playerList.stream().anyMatch(player->player.getScore()<0)){
                //飛びによる終局
                finished = true;
            }
            roundId = result.isDealerAdvantage()? roundId:roundId.next();
            streak = result.isNonDealerVictory()? 0:streak + 1;
            deposit = result.isDrawn()? (deposit + round.getReadyCount()):0;

            int sum = 0;
            for(var wind:Wind.values()){
                int score = gamePlayers.get(wind).getScore();
                LOG.info(gamePlayers.get(wind).getName()+" "+score);
                sum += score;
            }
            sum += deposit*1000;
            LOG.info("deposit="+deposit*1000);
            if(sum!=100000){
                LOG.error("invalid score sum: "+sum);
                throw new IllegalStateException("invalid score sum");
            }

        }
        var playerRanking = gamePlayers.values().stream()
                .sorted(Comparator.comparing(GamePlayer::getRank)).toList();
        if(deposit>0){
            playerRanking.get(0).applyScore(deposit*1000);
        }
        var resultScores = playerRanking.stream().map(GamePlayer::getResultScoreData).toList();
        for(var wind:Wind.values()){
            gamePlayers.get(wind).gameFinished(resultScores);
        }
    }

    @Override
    public int getDefaultScore(){
        return DEFAULT_SCORE;
    }

    @Override
    public int getReturnScore(){
        return RETURN_SCORE;
    }

    @Override
    public int getRankOf(GamePlayer player){
        return gamePlayers.values().stream()
                .sorted(Comparator.comparing(GamePlayer::getScore).reversed()
                        .thenComparing(GamePlayer::getInitialSeatWind))
                .toList().indexOf(player) + 1;
    }

    @Override
    public int getRankScore(int rank){
        return switch(rank){
            case 1-> BIG_RANK_SCORE;
            case 2-> SMALL_RANK_SCORE;
            case 3-> -1*SMALL_RANK_SCORE;
            case 4-> -1*BIG_RANK_SCORE;
            default-> throw new IllegalArgumentException("invalid rank "+rank);
        };
    }
}
