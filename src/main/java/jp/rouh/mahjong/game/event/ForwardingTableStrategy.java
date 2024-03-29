package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;
import java.util.Map;

/**
 * プレイヤーの戦略{@link TableStrategy}を
 * 注入したオブジェクトに委譲するアダプタクラス。
 * @author Rouh
 * @version 1.0
 */
public abstract class ForwardingTableStrategy implements TableStrategy{
    private final TableStrategy strategy;

    /**
     * アダプタクラスのコンストラクタ。
     * @param strategy 委譲先オブジェクト
     */
    public ForwardingTableStrategy(TableStrategy strategy){
        this.strategy = strategy;
    }

    @Override
    public void gameStarted(List<ProfileData> players){
        strategy.gameStarted(players);
    }

    @Override
    public void gameFinished(List<GameScoreData> scores){
        strategy.gameFinished(scores);
    }

    @Override
    public void temporarySeatUpdated(Map<Side, PlayerTempData> players){
        strategy.temporarySeatUpdated(players);
    }

    @Override
    public void seatUpdated(Map<Side, PlayerData> players){
        strategy.seatUpdated(players);
    }

    @Override
    public void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){
        strategy.roundStarted(wind, count, streak, deposit, last);
    }

    @Override
    public void roundDrawn(DrawType drawType){
        strategy.roundDrawn(drawType);
    }

    @Override
    public void handScoreNotified(List<HandScoreData> scores){
        strategy.handScoreNotified(scores);
    }

    @Override
    public void riverScoreNotified(List<RiverScoreData> scores){
        strategy.riverScoreNotified(scores);
    }

    @Override
    public void paymentNotified(Map<Side, PaymentData> payments){
        strategy.paymentNotified(payments);
    }

    @Override
    public void roundFinished(){
        strategy.roundFinished();
    }

    @Override
    public void diceRolled(Side side, int dice1, int dice2){
        strategy.diceRolled(side, dice1, dice2);
    }

    @Override
    public void declared(Side side, Declaration declaration){
        strategy.declared(side, declaration);
    }

    @Override
    public void readyBoneAdded(Side side){
        strategy.readyBoneAdded(side);
    }

    @Override
    public void wallGenerated(){
        strategy.wallGenerated();
    }

    @Override
    public void wallTileTaken(Side side, int column, int floor){
        strategy.wallTileTaken(side, column, floor);
    }

    @Override
    public void wallTileRevealed(Side side, int column, Tile tile){
        strategy.wallTileRevealed(side, column, tile);
    }

    @Override
    public void turnStarted(Side side){
        strategy.turnStarted(side);
    }

    @Override
    public void handLocked(){
        strategy.handLocked();
    }

    @Override
    public void handUpdated(Side side, int size, boolean wide){
        strategy.handUpdated(side, size, wide);
    }

    @Override
    public void handUpdated(List<Tile> allTiles, boolean wide){
        strategy.handUpdated(allTiles, wide);
    }

    @Override
    public void handRevealed(Side side, List<Tile> allTiles, boolean wide){
        strategy.handRevealed(side, allTiles, wide);
    }

    @Override
    public void riverTileAdded(Side side, Tile tile, boolean tilt){
        strategy.riverTileAdded(side, tile, tilt);
    }

    @Override
    public void riverTileTaken(Side side){
        strategy.riverTileTaken(side);
    }

    @Override
    public void tiltMeldAdded(Side side, Side tilt, List<Tile> tiles){
        strategy.tiltMeldAdded(side, tilt, tiles);
    }

    @Override
    public void selfQuadAdded(Side side, List<Tile> tiles){
        strategy.selfQuadAdded(side, tiles);
    }

    @Override
    public void meldTileAdded(Side side, int index, Tile tile){
        strategy.meldTileAdded(side, index, tile);
    }

    @Override
    public TurnAction selectTurnAction(List<TurnAction> choices){
        return strategy.selectTurnAction(choices);
    }

    @Override
    public CallAction selectCallAction(List<CallAction> choices){
        return strategy.selectCallAction(choices);
    }
}
