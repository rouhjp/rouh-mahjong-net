package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;
import java.util.Map;

/**
 * プレイヤーの通知先{@link TableObserver}を
 * 注入したオブジェクトに委譲するアダプタクラス。
 * @author Rouh
 * @version 1.0
 */
public abstract class ForwardingTableObserver implements TableObserver{
    private final TableObserver observer;

    /**
     * アダプタクラスのコンストラクタ。
     * @param observer 委譲先オブジェクト
     */
    public ForwardingTableObserver(TableObserver observer){
        this.observer = observer;
    }

    @Override
    public void gameStarted(List<ProfileData> players){
        observer.gameStarted(players);
    }

    @Override
    public void gameFinished(List<GameScoreData> scores){
        observer.gameFinished(scores);
    }

    @Override
    public void temporarySeatUpdated(Map<Side, PlayerTempData> players){
        observer.temporarySeatUpdated(players);
    }

    @Override
    public void seatUpdated(Map<Side, PlayerData> players){
        observer.seatUpdated(players);
    }

    @Override
    public void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){
        observer.roundStarted(wind, count, streak, deposit, last);
    }

    @Override
    public void roundDrawn(DrawType drawType){
        observer.roundDrawn(drawType);
    }

    @Override
    public void handScoreNotified(List<HandScoreData> scores){
        observer.handScoreNotified(scores);
    }

    @Override
    public void riverScoreNotified(List<RiverScoreData> scores){
        observer.riverScoreNotified(scores);
    }

    @Override
    public void paymentNotified(Map<Side, PaymentData> payments){
        observer.paymentNotified(payments);
    }

    @Override
    public void roundFinished(){
        observer.roundFinished();
    }

    @Override
    public void diceRolled(Side side, int dice1, int dice2){
        observer.diceRolled(side, dice1, dice2);
    }

    @Override
    public void declared(Side side, Declaration declaration){
        observer.declared(side, declaration);
    }

    @Override
    public void readyBoneAdded(Side side){
        observer.readyBoneAdded(side);
    }

    @Override
    public void wallGenerated(){
        observer.wallGenerated();
    }

    @Override
    public void wallTileTaken(Side side, int column, int floor){
        observer.wallTileTaken(side, column, floor);
    }

    @Override
    public void wallTileRevealed(Side side, int column, Tile tile){
        observer.wallTileRevealed(side, column, tile);
    }

    @Override
    public void turnStarted(Side side){
        observer.turnStarted(side);
    }

    @Override
    public void handLocked(){
        observer.handLocked();
    }

    @Override
    public void handUpdated(Side side, int size, boolean wide){
        observer.handUpdated(side, size, wide);
    }

    @Override
    public void handUpdated(List<Tile> allTiles, boolean wide){
        observer.handUpdated(allTiles, wide);
    }

    @Override
    public void handRevealed(Side side, List<Tile> allTiles, boolean wide){
        observer.handRevealed(side, allTiles, wide);
    }

    @Override
    public void riverTileAdded(Side side, Tile tile, boolean tilt){
        observer.riverTileAdded(side, tile, tilt);
    }

    @Override
    public void riverTileTaken(Side side){
        observer.riverTileTaken(side);
    }

    @Override
    public void tiltMeldAdded(Side side, Side tilt, List<Tile> tiles){
        observer.tiltMeldAdded(side, tilt, tiles);
    }

    @Override
    public void selfQuadAdded(Side side, List<Tile> tiles){
        observer.selfQuadAdded(side, tiles);
    }

    @Override
    public void meldTileAdded(Side side, int index, Tile tile){
        observer.meldTileAdded(side, index, tile);
    }
}
