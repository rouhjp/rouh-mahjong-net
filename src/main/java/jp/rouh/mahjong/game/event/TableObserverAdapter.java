package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;
import java.util.Map;

public interface TableObserverAdapter extends TableObserver{

    @Override
    default void gameStarted(List<ProfileData> players){

    }

    @Override
    default void gameSettled(List<GameScoreData> scores){

    }

    @Override
    default void temporarySeatUpdated(Map<Side, PlayerTempData> players){

    }

    @Override
    default void seatUpdated(Map<Side, PlayerData> players){

    }

    @Override
    default void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){

    }

    @Override
    default void roundDrawn(DrawType drawType){

    }

    @Override
    default void handScoreNotified(List<HandScoreData> scores){

    }

    @Override
    default void riverScoreNotified(List<RiverScoreData> scores){

    }

    @Override
    default void paymentNotified(Map<Side, PaymentData> payments){

    }

    @Override
    default void roundFinished(){

    }

    @Override
    default void diceRolled(Side side, int dice1, int dice2){

    }

    @Override
    default void declared(Side side, Declaration declaration){

    }

    @Override
    default void readyBoneAdded(Side side){

    }

    @Override
    default void wallGenerated(){

    }

    @Override
    default void wallTileTaken(Side side, int column, int floor){

    }

    @Override
    default void wallTileRevealed(Side side, int column, Tile tile){

    }

    @Override
    default void turnStarted(Side side){

    }

    @Override
    default void handLocked(){

    }

    @Override
    default void handUpdated(Side side, int size, boolean wide){

    }

    @Override
    default void handUpdated(List<Tile> allTiles, boolean wide){

    }

    @Override
    default void handRevealed(Side side, List<Tile> allTiles, boolean wide){

    }

    @Override
    default void riverTileAdded(Side side, Tile tile, boolean tilt){

    }

    @Override
    default void riverTileTaken(Side side){

    }

    @Override
    default void tiltMeldAdded(Side side, Side tilt, List<Tile> tiles){

    }

    @Override
    default void selfQuadAdded(Side side, List<Tile> tiles){

    }

    @Override
    default void meldTileAdded(Side side, int index, Tile tile){

    }
}
