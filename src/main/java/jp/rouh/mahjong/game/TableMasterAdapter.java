package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 対局時の情報を各風のプレイヤーへ通知するアダプタクラス。
 * @author Rouh
 * @version 1.0
 */
interface TableMasterAdapter extends TableMaster{

    @Override
    TableObserver getPlayerAt(Wind wind);

    @Override
    default void gameStarted(List<ProfileData> players){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).gameStarted(players);
        }
    }

    @Override
    default void seatUpdated(Map<Wind, PlayerData> players){
        for(var eachWind:Wind.values()){
            var map = new HashMap<Side, PlayerData>();
            for(var side:Side.values()){
                map.put(side, players.get(side.of(eachWind)));
            }
            getPlayerAt(eachWind).seatUpdated(map);
        }
    }

    @Override
    default void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).roundStarted(wind, count, streak, deposit, last);
        }
    }

    @Override
    default void roundDrawn(DrawType drawType){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).roundDrawn(drawType);
        }
    }

    @Override
    default void handScoreNotified(List<HandScoreData> scores){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).handScoreNotified(scores);
        }
    }

    @Override
    default void riverScoreNotified(List<RiverScoreData> scores){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).riverScoreNotified(scores);
        }
    }

    @Override
    default void paymentNotified(Map<Wind, PaymentData> payments){
        for(var eachWind:Wind.values()){
            var map = new HashMap<Side, PaymentData>();
            for(var side:Side.values()){
                map.put(side, payments.get(side.of(eachWind)));
            }
            getPlayerAt(eachWind).paymentNotified(map);
        }
    }

    @Override
    default void roundFinished(){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).roundFinished();
        }
    }

    @Override
    default void diceRolled(Wind wind, int dice1, int dice2){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).diceRolled(wind.from(eachWind), dice1, dice2);
        }
    }

    @Override
    default void declared(Wind wind, Declaration declaration){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).declared(wind.from(eachWind), declaration);
        }
    }

    @Override
    default void readyBoneAdded(Wind wind){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).readyBoneAdded(wind.from(eachWind));
        }
    }

    @Override
    default void wallGenerated(){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).wallGenerated();
        }
    }

    @Override
    default void wallTileTaken(Wind wind, int column, int floor){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).wallTileTaken(wind.from(eachWind), column, floor);
        }
    }

    @Override
    default void wallTileRevealed(Wind wind, int column, Tile tile){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).wallTileRevealed(wind.from(eachWind), column, tile);
        }
    }

    @Override
    default void turnStarted(Wind wind){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).turnStarted(wind.from(eachWind));
        }
    }

    @Override
    default void handUpdated(Wind wind, List<Tile> wideTiles, boolean wide){
        getPlayerAt(wind).handUpdated(wideTiles, wide);
        for(var eachWind:wind.others()){
            getPlayerAt(eachWind).handUpdated(wind.from(eachWind), wideTiles.size(), wide);
        }
    }

    @Override
    default void handRevealed(Wind wind, List<Tile> wideTiles, boolean wide){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).handRevealed(wind.from(eachWind), wideTiles, wide);
        }
    }

    @Override
    default void riverTileAdded(Wind wind, Tile tile, boolean tilt){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).riverTileAdded(wind.from(eachWind), tile, tilt);
        }
    }

    @Override
    default void riverTileTaken(Wind wind){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).riverTileTaken(wind.from(eachWind));
        }
    }

    @Override
    default void tiltMeldAdded(Wind wind, Side tilt, List<Tile> tiles){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).tiltMeldAdded(wind.from(eachWind), tilt, tiles);
        }
    }

    @Override
    default void selfQuadAdded(Wind wind, List<Tile> tiles){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).selfQuadAdded(wind.from(eachWind), tiles);
        }
    }

    @Override
    default void meldTileAdded(Wind wind, int index, Tile tile){
        for(var eachWind:Wind.values()){
            getPlayerAt(eachWind).meldTileAdded(wind.from(eachWind), index, tile);
        }
    }
}
