package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.game.event.PaymentData;
import jp.rouh.mahjong.game.event.PlayerData;
import jp.rouh.mahjong.game.event.HandScoreData;
import jp.rouh.mahjong.score.HandType;
import jp.rouh.mahjong.score.Meld;
import jp.rouh.mahjong.tile.Side;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static jp.rouh.mahjong.tile.Side.*;
import static jp.rouh.mahjong.tile.Tile.*;
import static jp.rouh.mahjong.tile.Wind.*;

public class TableViewManualTest2{

    private final TableViewPanel table;

    private TableViewManualTest2(){
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
                LEFT, new PlayerData("Guest", EAST, EAST, 25000, 1),
                SELF, new PlayerData("けもみみ", SOUTH, SOUTH, 24000, 4),
                RIGHT, new PlayerData("麻雀初心者", WEST, WEST, 25000, 2),
                ACROSS, new PlayerData("OvO", NORTH, NORTH, 25000, 3)
        ));
        table.handUpdated(List.of(P2, P2, P3, P3, P4, P4, P5, P5, P6, P6, P7, P8, DR, P7), true);
        table.handUpdated(LEFT, 13, false);
        table.handUpdated(RIGHT, 13, false);
        table.handUpdated(ACROSS, 13, false);
        table.readyBoneAdded(SELF);

        table.selfQuadAdded(ACROSS, List.of(DW, DW, DW, DW));

        Stream.of(S1, S2, S3, S4, S5, S6, S7, S8)
                .flatMap(t->Stream.of(t, t, t)).sorted()
                .forEach(t->Stream.of(Side.values()).forEach(side->table.riverTileAdded(side, t, false)));

        var scores = new ArrayList<HandScoreData>();
        var score1 = new HandScoreData();
        score1.setHandTiles(List.of(M7, M8, M9, P2, P2, S1, S2));
        score1.setWinningTile(S3);
        score1.setOpenMelds(List.of(
                Meld.ofCallTriple(List.of(DR, DR), DR, LEFT),
                Meld.ofCallSequence(List.of(P4, P5), P6)
        ));
        score1.setHandTypes(List.of(
                HandType.forName("飜牌 中")
        ));
        score1.setScoreExpression("30符1飜 1000点");
        score1.setUpperIndicators(List.of(P8));
        score1.setLowerIndicators(List.of());
        score1.setTsumo(false);
        scores.add(score1);
        var score2 = new HandScoreData();
        score2.setHandTiles(List.of(M4, M5, M6, P6, P7, P8, S3, S3, S4, S4, S7, S8, S9));
        score2.setWinningTile(S3);
        score2.setOpenMelds(List.of());
        score2.setHandTypes(List.of(
                HandType.forName("立直")
        ));
        score2.setScoreExpression("40符1飜 1300点");
        score2.setUpperIndicators(List.of(P8));
        score2.setLowerIndicators(List.of());
        score2.setTsumo(false);
        scores.add(score2);
        var payments = new HashMap<Side, PaymentData>();
//        payments.put(RIGHT, new PaymentData(EAST, "guest1", 24000, 21700, 3, 4));
//        payments.put(ACROSS, new PaymentData(SOUTH, "guest2", 26600, 28600, 1, 1));
//        payments.put(LEFT, new PaymentData(WEST, "guest3", 24000, 25300, 4, 2));
//        payments.put(SELF, new PaymentData(NORTH, "you", 24400, 24400, 2, 3));
        table.roundSettled(scores);
        table.paymentSettled(payments);

    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(TableViewManualTest2::new);
    }
}
