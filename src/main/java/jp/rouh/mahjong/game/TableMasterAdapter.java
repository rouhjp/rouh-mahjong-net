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
abstract class TableMasterAdapter implements TableMaster{
//    private final ExecutorService executor = Executors.newWorkStealingPool();

    @Override
    public abstract TableObserver getPlayerAt(Wind wind);
    
    private void intercept(Runnable runnable){
//        var executor = Executors.newSingleThreadExecutor();
//        executor.submit(runnable);
//        executor.shutdown();
        runnable.run();
    }

    @Override
    public void gameStarted(List<ProfileData> players){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).gameStarted(players));
        }
    }

    @Override
    public void seatUpdated(Map<Wind, PlayerData> players){
        for(var eachWind:Wind.values()){
            var map = new HashMap<Side, PlayerData>();
            for(var side:Side.values()){
                map.put(side, players.get(side.of(eachWind)));
            }
            intercept(()->getPlayerAt(eachWind).seatUpdated(map));
        }
    }

    @Override
    public void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).roundStarted(wind, count, streak, deposit, last));
        }
    }

    @Override
    public void roundDrawn(DrawType drawType){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).roundDrawn(drawType));
        }
    }

    @Override
    public void roundSettled(List<ScoringData> scores){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).roundSettled(scores));
        }
    }

    @Override
    public void paymentSettled(Map<Wind, PaymentData> payments){
        for(var eachWind:Wind.values()){
            var map = new HashMap<Side, PaymentData>();
            for(var side:Side.values()){
                map.put(side, payments.get(side.of(eachWind)));
            }
            intercept(()->getPlayerAt(eachWind).paymentSettled(map));
        }
    }

    @Override
    public void diceRolled(Wind wind, int dice1, int dice2){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).diceRolled(wind.from(eachWind), dice1, dice2));
        }
    }

    @Override
    public void declared(Wind wind, Declaration declaration){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).declared(wind.from(eachWind), declaration));
        }
    }

    @Override
    public void readyBoneAdded(Wind wind){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).readyBoneAdded(wind.from(eachWind)));
        }
    }

    @Override
    public void wallGenerated(){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).wallGenerated());
        }
    }

    @Override
    public void wallTileTaken(Wind wind, int column, int floor){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).wallTileTaken(wind.from(eachWind), column, floor));
        }
    }

    @Override
    public void wallTileRevealed(Wind wind, int column, Tile tile){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).wallTileRevealed(wind.from(eachWind), column, tile));
        }
    }

    @Override
    public void handUpdated(Wind wind, List<Tile> wideTiles, boolean wide){
        intercept(()->getPlayerAt(wind).handUpdated(wideTiles, wide));
        for(var eachWind:wind.others()){
            intercept(()->getPlayerAt(eachWind).handUpdated(wind.from(eachWind), wideTiles.size(), wide));
        }
    }

    @Override
    public void handRevealed(Wind wind, List<Tile> wideTiles, boolean wide){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).handRevealed(wind.from(eachWind), wideTiles, wide));
        }
    }

    @Override
    public void riverTileAdded(Wind wind, Tile tile, boolean tilt){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).riverTileAdded(wind.from(eachWind), tile, tilt));
        }
    }

    @Override
    public void riverTileTaken(Wind wind){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).riverTileTaken(wind.from(eachWind)));
        }
    }

    @Override
    public void tiltMeldAdded(Wind wind, Side tilt, List<Tile> tiles){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).tiltMeldAdded(wind.from(eachWind), tilt, tiles));
        }
    }

    @Override
    public void selfQuadAdded(Wind wind, List<Tile> tiles){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).selfQuadAdded(wind.from(eachWind), tiles));
        }
    }

    @Override
    public void meldTileAdded(Wind wind, int index, Tile tile){
        for(var eachWind:Wind.values()){
            intercept(()->getPlayerAt(eachWind).meldTileAdded(wind.from(eachWind), index, tile));
        }
    }
}
