package jp.rouh.mahjong.app.bot;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.game.TablePlayer;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;
import java.util.Map;

public class DiscardingBot implements TablePlayer{
    private final String name;
    public DiscardingBot(String name){
        this.name = name;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public TableStrategy getStrategy(){
        return new TableStrategy(){
            @Override
            public TurnAction selectTurnAction(List<TurnAction> choices){
                return choices.get(0);
            }

            @Override
            public CallAction selectCallAction(List<CallAction> choices){
                return choices.get(0);
            }

            @Override
            public void gameStarted(List<ProfileData> players){

            }

            @Override
            public void temporarySeatUpdated(Map<Side, PlayerTempData> players){

            }

            @Override
            public void seatUpdated(Map<Side, PlayerData> players){

            }

            @Override
            public void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){

            }

            @Override
            public void roundDrawn(DrawType drawType){

            }

            @Override
            public void roundSettled(List<ScoringData> scores){

            }

            @Override
            public void paymentSettled(Map<Side, PaymentData> payments){

            }

            @Override
            public void diceRolled(Side side, int dice1, int dice2){

            }

            @Override
            public void declared(Side side, Declaration declaration){

            }

            @Override
            public void readyBoneAdded(Side side){

            }

            @Override
            public void wallGenerated(){

            }

            @Override
            public void wallTileTaken(Side side, int column, int floor){

            }

            @Override
            public void wallTileRevealed(Side side, int column, Tile tile){

            }

            @Override
            public void turnStarted(Side side){

            }

            @Override
            public void handUpdated(Side side, int size, boolean wide){

            }

            @Override
            public void handUpdated(List<Tile> allTiles, boolean wide){

            }

            @Override
            public void handRevealed(Side side, List<Tile> allTiles, boolean wide){

            }

            @Override
            public void riverTileAdded(Side side, Tile tile, boolean tilt){

            }

            @Override
            public void riverTileTaken(Side side){

            }

            @Override
            public void tiltMeldAdded(Side side, Side tilt, List<Tile> tiles){

            }

            @Override
            public void selfQuadAdded(Side side, List<Tile> tiles){

            }

            @Override
            public void meldTileAdded(Side side, int index, Tile tile){

            }
        };
    }
}
