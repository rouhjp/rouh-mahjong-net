package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.PlayerTempData;
import jp.rouh.mahjong.tile.DiceTwin;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Wind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jp.rouh.mahjong.tile.Wind.*;

/**
 * 対局クラス。
 * @author Rouh
 * @version 1.0
 */
public class Game implements GameAccessor{
    private static final Logger LOG = LoggerFactory.getLogger(Game.class);
    private final Map<Wind, GamePlayer> gamePlayers;
    private final Wind initDealerOrderWind;

    public Game(TablePlayer p1, TablePlayer p2, TablePlayer p3, TablePlayer p4){
        this(Map.of(EAST, p1, SOUTH, p2, WEST, p3, NORTH, p4));
    }

    private Game(Map<Wind, TablePlayer> players){
        this.gamePlayers = Stream.of(Wind.values())
                .collect(Collectors.toMap(Function.identity(), wind->new GamePlayer(this, players.get(wind), wind)));
        this.initDealerOrderWind = drawInitialDealer();
    }

    public void start(){
        var gameSpan = GameSpan.ofHalfGame();
        var roundId = gameSpan.getFirstRoundSign();
        int streak = 0;
        int deposit = 0;
        var playerList = List.copyOf(gamePlayers.values());
        gameStarted();
        while(true){
            boolean last = gameSpan.isLastRound(roundId);
            LOG.info("--new round--");
            var round = new Round(playerList, roundId, streak, deposit, last);
            var result = round.start();
            LOG.info("--round finished--");
            if(gameSpan.hasExtended()){
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
                        gameSpan.extend();
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

    /**
     * 親決めを実施します。
     * 親決め完了時, 起家の席{@code initDealerOrderWind}が決まります。
     */
    private Wind drawInitialDealer(){
        // 仮東⇒仮親決め
        var firstDices = DiceTwin.roll();
        diceRolled(EAST, firstDices.firstValue(), firstDices.secondValue());
        var firstDiceDiffSide = Side.of(firstDices.sumValue());
        var tmpDealerOrderWind = firstDiceDiffSide.of(EAST);
        seatUpdated(tmpDealerOrderWind);
        // 仮親⇒親決め
        var secondDices = DiceTwin.roll();
        diceRolled(tmpDealerOrderWind, secondDices.firstValue(), secondDices.secondValue());
        var secondDiceDiffSide = Side.of(secondDices.sumValue()).of(firstDiceDiffSide);
        var dealerOrderWind = secondDiceDiffSide.of(EAST);
        seatUpdated(dealerOrderWind);
        return dealerOrderWind;
    }

    private void gameStarted(){
        var profiles = gamePlayers.values().stream().map(GamePlayer::getProfileData).toList();
        for(var eachWind:Wind.values()){
            gamePlayers.get(eachWind).gameStarted(profiles);
        }
    }

    private void seatUpdated(Wind dealerOrderWind){
        for(var orderWind:Wind.values()){
            var player = gamePlayers.get(orderWind);
            var seatWind = orderWind.from(dealerOrderWind).of(EAST);
            var map = new HashMap<Side, PlayerTempData>();
            for(var side:Side.values()){
                var eachOrderWind = side.of(orderWind);
                var eachPlayer = gamePlayers.get(eachOrderWind);
                var eachSeatWind = side.of(seatWind);
                map.put(side, eachPlayer.getPlayerTempData(eachSeatWind));
            }
            player.temporarySeatUpdated(map);
        }
    }

    /**
     * 各プレイヤーにサイコロ振り発生の通知を実施します。
     * @param wind サイを振ったプレイヤーの自風
     * @param d1 1つ目のサイコロの値
     * @param d2 2つ目のサイコロの値
     */
    private void diceRolled(Wind wind, int d1, int d2){
        for(var eachWind:Wind.values()){
            gamePlayers.get(eachWind).diceRolled(wind.from(eachWind), d1, d2);
        }
    }

    @Override
    public int getRankOf(Wind orderWind){
        return gamePlayers.values().stream()
                .sorted(Comparator.comparing(GamePlayer::getScore).reversed()
                        .thenComparing(player->getInitialSeatWindAt(player.getOrderWind())))
                .map(GamePlayer::getOrderWind)
                .toList().indexOf(orderWind) + 1;
    }

    @Override
    public Wind getInitialSeatWindAt(Wind orderWind){
        return orderWind.from(initDealerOrderWind).of(EAST);
    }
}
