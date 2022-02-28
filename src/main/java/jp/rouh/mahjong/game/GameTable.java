package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.PlayerTempData;
import jp.rouh.mahjong.game.event.TableObserver;
import jp.rouh.mahjong.game.event.TableStrategy;
import jp.rouh.mahjong.tile.DiceTwin;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Wind;

import java.util.*;

import static jp.rouh.mahjong.tile.Wind.*;

/**
 * 対局テーブルクラス。
 * <p>プレイヤーを追加して親決めを行い, 対局を開始します。
 * @author Rouh
 * @version 1.0
 */
public class GameTable{
    private final List<Player> players = new ArrayList<>(4);

    /**
     * 参加プレイヤーを追加します。
     * @param name 追加するプレイヤーの名前(重複を排除しない)
     * @param strategy 追加するプレイヤーの戦略オブジェクト
     * @throws IllegalStateException 参加プレイヤーが既に4人存在する場合
     */
    public void addPlayer(String name, TableStrategy strategy){
        if(players.size()>=4){
            throw new IllegalStateException("room is already full");
        }
        players.add(new Player(name, strategy));
    }

    /**
     * 対局を開始します。
     * @throws IllegalStateException 参加プレイヤーの数が不正の場合
     */
    public void start(){
        if(players.size()!=4){
            throw new IllegalStateException("need more players to start: count="+players.size());
        }
        gameStarted();
        var playerMap = new HashMap<Wind, Player>();
        seatUpdated(EAST);
        // 仮東⇒仮親決め
        var firstRoll = DiceTwin.roll();
        diceRolled(EAST, firstRoll);
        var tmpDealerOrderWind = Side.of(firstRoll.sumValue()).of(EAST);
        seatUpdated(tmpDealerOrderWind);
        // 仮親⇒親決め
        var secondRoll = DiceTwin.roll();
        diceRolled(tmpDealerOrderWind, secondRoll);
        var dealerOrderWind = Side.of(secondRoll.sumValue()).of(tmpDealerOrderWind);
        seatUpdated(dealerOrderWind);
        for(var orderWind:Wind.values()){
            var seatWind = orderWind.from(dealerOrderWind).of(EAST);
            playerMap.put(seatWind, players.get(orderWind.ordinal()));
        }
        var preparedGame = new PreparedGame(playerMap, GameSpan.ofHalfGame());
        preparedGame.start();
    }

    /**
     * 各プレイヤーに対局開始を通知します。
     * @see TableObserver#gameStarted
     */
    private void gameStarted(){
        var profiles = players.stream().map(Player::getProfileData).toList();
        for(var wind:Wind.values()){
            players.get(wind.ordinal()).getStrategy().gameStarted(profiles);
        }
    }

    /**
     * 各プレイヤーに仮プレイヤー情報を通知します。
     * @see TableObserver#seatUpdated
     * @param dealerOrderWind 親の席風
     */
    private void seatUpdated(Wind dealerOrderWind){
        var map = new HashMap<Wind, PlayerTempData>();
        for(var orderWind:Wind.values()){
            var player = players.get(orderWind.ordinal());
            var seatWind = orderWind.from(dealerOrderWind).of(EAST);
            map.put(seatWind, player.getPlayerTempData(seatWind));
        }
        for(var orderWind:Wind.values()){
            var sideMap = new HashMap<Side, PlayerTempData>();
            for(var side:Side.values()){
                sideMap.put(side, map.get(side.of(orderWind)));
            }
            players.get(orderWind.ordinal()).getStrategy().temporarySeatUpdated(sideMap);
        }
    }

    /**
     * 各プレイヤーにサイコロが振られたことを通知します。
     * @see TableObserver#diceRolled
     * @param wind サイコロを振ったプレイヤーの席風
     * @param dices サイコロの値
     */
    private void diceRolled(Wind wind, DiceTwin dices){
        for(var orderWind:Wind.values()){
            var player = players.get(orderWind.ordinal());
            player.getStrategy().diceRolled(wind.from(orderWind), dices.firstValue(), dices.secondValue());
        }
    }
}
