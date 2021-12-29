package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.DiceTwin;
import jp.rouh.mahjong.tile.Wind;
import jp.rouh.util.FlexMap;

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
    private static final int DEFAULT_SCORE = 25000;
    private final Map<Wind, GamePlayer> gamePlayers;
    private final GameSpan span;

    /**
     * コンストラクタ。
     * @param players プレイヤーの対局開始時の自風に対するマップ
     */
    PreparedGame(Map<Wind, Player> players, GameSpan span){
        this.gamePlayers = new FlexMap<>(players)
                .mapValue((wind, player)->new GamePlayer(this, player, wind));
        this.span = span;
    }

    void start(){
        var roundId = span.getFirstRoundSign();
        int streak = 0;
        int deposit = 0;
        var playerList = List.copyOf(gamePlayers.values());
        while(true){
            boolean last = span.isLastRound(roundId);
            var params = new RoundParameter(roundId, streak, deposit, last);
            var round = new Round(params, playerList);
            var dices = DiceTwin.roll();
            var result = round.start(dices.firstValue(), dices.secondValue());
            if(span.hasExtended()){
                if(playerList.stream().anyMatch(player->player.getScore()>=30000)){
                    //サドンデスによる終局
                    break;
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
                        break;
                    }
                }else{
                    if(playerList.stream().allMatch(player->player.getScore()<30000)){
                        span.extend();
                    }else{
                        //流局による終局
                        break;
                    }
                }
            }else if(playerList.stream().anyMatch(player->player.getScore()<0)){
                //飛びによる終局
                break;
            }
            roundId = result.isDealerAdvantage()? roundId:roundId.next();
            streak = result.isNonDealerVictory()? 0:streak + 1;
            deposit += result.isDrawn()? round.getReadyCount():0;
        }
    }

    @Override
    public int getDefaultScore(){
        return DEFAULT_SCORE;
    }

    @Override
    public int getRankOf(GamePlayer player){
        return gamePlayers.values().stream()
                .sorted(Comparator.comparing(GamePlayer::getScore).reversed()
                        .thenComparing(GamePlayer::getInitialSeatWind))
                .toList().indexOf(player) + 1;
    }
}
