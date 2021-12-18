package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.tile.Side;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static jp.rouh.mahjong.tile.Side.*;
import static jp.rouh.mahjong.tile.Tile.*;
import static jp.rouh.mahjong.tile.Wind.*;

public class TableViewManualTest1{

    private final TableViewPanel table;

    private TableViewManualTest1(){
        table = new TableViewFrame().getTable();
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{
            try{
                start();
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        executor.shutdown();
    }

    private void start(){
        table.roundStarted(EAST, 1, 5, 5, true);
        table.wallGenerated();
        table.seatUpdated(Map.of(
                LEFT, new PlayerData("おおらか", EAST, EAST, 25000, 1),
                SELF, new PlayerData("ほがらか", SOUTH, SOUTH, 24000, 4),
                RIGHT, new PlayerData("なごやか", WEST, WEST, 25000, 2),
                ACROSS, new PlayerData("さわやか", NORTH, NORTH, 25000, 3)
        ));
        var allTiles = List.of(P2, P2, P3, P3, P4, P4, P5, P5, P6, P6, P7, P8, DW, P7);
        table.handUpdated(allTiles, true);
        table.handUpdated(LEFT, 13, false);
        table.handUpdated(RIGHT, 13, false);
        table.handUpdated(ACROSS, 13, false);
        table.readyBoneAdded(SELF);

        table.selfQuadAdded(ACROSS, List.of(DW, DW, DW, DW));

        Stream.of(S1, S2, S3, S4, S5, S6, S7, S8)
                .flatMap(t->Stream.of(t, t, t)).sorted()
                .forEach(t->Stream.of(Side.values()).forEach(side->table.riverTileAdded(side, t, false)));

        var turnActions = new HashSet<TurnAction>();
        for(var tile:allTiles){
            turnActions.add(TurnAction.ofDiscard(tile));
        }
        turnActions.add(TurnAction.ofReadyAndDiscard(P5));
        turnActions.add(TurnAction.ofReadyAndDiscard(P8));
        turnActions.add(TurnAction.ofReadyAndDiscard(DW));
        var action = table.selectTurnAction(turnActions.stream().toList());

        if(action.type()==TurnActionType.READY_DISCARD){
            table.declared(SELF, Declaration.READY);
        }

    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(TableViewManualTest1::new);
    }
}
