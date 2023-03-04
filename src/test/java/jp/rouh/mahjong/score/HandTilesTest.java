package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static jp.rouh.mahjong.score.HandTiles.*;
import static jp.rouh.mahjong.score.HandTiles.waitingTargetsOf;
import static jp.rouh.mahjong.tile.Tile.*;
import static org.junit.jupiter.api.Assertions.*;

public class HandTilesTest{
    
    @Nested
    class TestIsNineTiles {

        @Test
        void test4tiles4kinds(){
            var handTiles = List.of(M1, M5, M5, M5, P1, P5, P5, P5, S1, S5, S5, S5, DW);
            assertFalse(isNineTiles(handTiles, M2));
        }

        @Test
        void test9Tiles8Kinds(){
            var handTiles = List.of(M1, M2, M3, M4, M5, M9, P1, P9, S1, S9, WE, WE, DW);
            assertFalse(isNineTiles(handTiles, M2));
        }

        @Test
        void test9Tiles9Kinds(){
            var handTiles = List.of(M1, M2, M3, M4, M5, M9, P1, P9, S1, S9, WE, WN, DW);
            assertTrue(isNineTiles(handTiles, M2));
        }

        @Test
        void test13Tiles5Kinds(){
            var handTiles = List.of(M1, M1, M1, P1, P1, P1, S1, S1, S1, WE, WE, DW, DW);
            assertFalse(isNineTiles(handTiles, M2));
        }

        @Test
        void test13Tiles13Kinds() {
            var handTiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, WN, DW, DG, DR);
            assertTrue(isNineTiles(handTiles, M2));
        }
    }

    @Nested
    class TestIsCompleted {

        @Test
        void testNotCompleted(){
            var handTiles = List.of(M1, M3, M5, S1, S3, S5, P1, P3, P5, DW, DW, DG, DR);
            assertFalse(isCompleted(handTiles, M9));
        }

        @Test
        void testSevenPairs(){
            var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, M5, M6, M6, M7, M7);
            assertTrue(isCompleted(handTiles, M5R));
        }

        @Test
        void testThirteenOrphans(){
            var handTiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, DW, DG, DR, DR);
            assertTrue(isCompleted(handTiles, WN));
        }

        @Test
        void testNineGates(){
            var handTiles = List.of(M1, M1, M1, M2, M3, M4, M5R, M6, M7, M9, M9, M9, M9);
            assertTrue(isCompleted(handTiles, M8));
        }
    }

    @Nested
    class TestIsCompletedSevenPairs {

        @Test
        void testNotSevenPairs(){
            var handTiles = List.of(M1, M1, M1, M3, M3, M3, M6, M6, M6, S1, S1, S1, P2);
            assertFalse(isCompletedSevenPairs(handTiles, P2));
        }

        @Test
        void testSevenPairs(){
            var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, M5, M6, M6, M7, M7);
            assertTrue(isCompletedSevenPairs(handTiles, M5R));
        }

        @Test
        void testNotSevenPairsForContainingDuplicatedPair(){
            var handTiles = List.of(M5, M5, M5, M5R, M6, M6, S1, S1, S2, S2, WE, DW, DW);
            assertFalse(isCompletedSevenPairs(handTiles, WE));
        }
    }

    @Nested
    class TestCompletedThirteenOrphans{

        @Test
        void testThirteenOrphans13Wait(){
            assertTrue(isCompletedThirteenOrphans(Tiles.orphans(), P9));
        }

        @Test
        void testThirteenOrphans1Wait(){
            var tiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, DW, DG, DR, DR);
            assertTrue(isCompletedThirteenOrphans(tiles, WN));
        }

        @Test
        void testNotThirteenOrphans1(){
            var tiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, DW, DG, DR, DR);
            assertFalse(isCompletedThirteenOrphans(tiles, DG));
        }

        @Test
        void testNotThirteenOrphans2(){
            var tiles = List.of(WE, WE, WS, WS, WW, WW, WN, WN, DW, DW, DG, DG, DR);
            assertFalse(isCompletedThirteenOrphans(tiles, DR));
        }
    }

    @Nested
    class TestIsCompletedMeldHand{

        private boolean isCompletedMeldHand(List<Tile> handTiles, Tile drawnTile){
            try {
                var method = HandTiles.class.getDeclaredMethod("isCompletedMeldHand", List.class, Tile.class);
                method.setAccessible(true);
                return (Boolean) method.invoke(null, handTiles, drawnTile);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void testCompleted(){
            var handTiles = List.of(M1, M2, M3, M4, M5, M6, M7, M8, M9, S1, S1, S2, S3);
            assertTrue(isCompletedMeldHand(handTiles, S1));
        }

        @Test
        void testNotCompleted(){
            var handTiles = List.of(M1, M2, M3, M4, M5, M6, M7, M8, M9, S1, S1, S3, S3);
            assertFalse(isCompletedMeldHand(handTiles, S4));
        }

        @Test
        void testCompletedStrippedSingleWait(){
            var handTiles = List.of(DW);
            assertTrue(isCompletedMeldHand(handTiles, DW));
        }
    }

    @Nested
    class TestIsHandReady {

        @Test
        void testHandReady(){
            var handTiles = List.of(M1, M2, M3, P1, P2, P3, S1, S2, S3, WE, WE, WN, WN);
            assertTrue(isHandReady(handTiles));
        }

        @Test
        void testNotHandReady(){
            var handTiles = List.of(M1, M2, M3, P1, P2, P3, S1, S2, S3, WE, WE, WN, DW);
            assertFalse(isHandReady(handTiles));
        }

        @Test
        void testNotHandReadyForWinningTileExhaustedInHandTiles(){
            var handTiles = List.of(M1, M1, M1, M1, M4, M4, M4, S1, S2, S3, WE, WE, WE);
            assertFalse(isHandReady(handTiles));
        }
    }

    @Nested
    class TestReadyTilesOf {

        @Test
        void testSingleReadyTiles(){
            var handTiles = List.of(M1, M1, M1, M2, M2, M2, M3, M3, M3, DW, DW, DR, DR);
            var expected = Set.of(WE);
            var result = readyTilesOf(handTiles, WE);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleReadyTiles(){
            var handTiles = List.of(M1, M1, M1, M2, M2, M3, M3, S1, S1, S2);
            var expected = Set.of(S1, M1);
            var result = readyTilesOf(handTiles, S3);
            assertEquals(expected, result);
        }

        @Test
        void testThirteenOrphans1Wait(){
            var handTiles = List.of(M1, M1, M9, M9, P1, P9, S1, S9, WE, WS, WW, WN, DW);
            var expected = Set.of(M1, M9);
            var result = readyTilesOf(handTiles, DR);
            assertEquals(expected, result);
        }

        @Test
        void testThirteenOrphans13Wait(){
            var expected = Set.copyOf(Tiles.orphans());
            var result = readyTilesOf(Tiles.orphans(), P1);
            assertEquals(expected, result);
        }

        @Test
        void testNineGates(){
            var handTiles = List.of(M1, M1, M1, M2, M3, M4, M5, M6, M7, M8, M9, M9, M9);
            var expected = Set.of(M1, M2, M3, M4, M5, M5R, M6, M7, M8, M9);
            var result = readyTilesOf(handTiles, M5R);
            assertEquals(expected, result);
        }

        @Test
        void testExhausted(){
            var handTiles = List.of(M1, M1, M1, M1, S3, S3, S3, S4, S4, S4, S5, S5, S5);
            var expected = Set.of(M1);
            var result = readyTilesOf(handTiles, WE);
            assertEquals(expected, result);
        }

    }

    @Nested
    class TestArrangeAll {

        @Test
        void testNotCompleted(){
            var handTiles = List.of(M1, M1, M2, M2, M3, M4, M6, M7, M8, M9);
            var expected = Set.of();
            var result = arrangeAll(handTiles, M5);
            assertEquals(expected, result);
        }

        @Test
        void testStraightsLinked(){
            var handTiles = List.of(M2, M2, M3, M3, M4, M4, M5, M5, M6, M6, M7, M7, M8);
            var expected = Set.of(
                    List.of(List.of(M2, M2), List.of(M3, M4, M5), List.of(M3, M4, M5), List.of(M6, M7, M8), List.of(M6, M7, M8)),
                    List.of(List.of(M5, M5), List.of(M2, M3, M4), List.of(M2, M3, M4), List.of(M6, M7, M8), List.of(M6, M7, M8)),
                    List.of(List.of(M8, M8), List.of(M2, M3, M4), List.of(M2, M3, M4), List.of(M5, M6, M7), List.of(M5, M6, M7))
            );
            var result = arrangeAll(handTiles, M8);
            assertEquals(expected, result);
        }

        @Test
        void testHeadAndStraightLinked(){
            var handTiles = List.of(M2, M2, M2, M3, M4, M5, M5, M5, DW, DW);
            var expected = Set.of(
                    List.of(List.of(M2, M2), List.of(M2, M3, M4), List.of(M5, M5, M5), List.of(DW, DW, DW)),
                    List.of(List.of(M5, M5), List.of(M2, M2, M2), List.of(M3, M4, M5), List.of(DW, DW, DW))
            );
            var result = arrangeAll(handTiles, DW);
            assertEquals(expected, result);
        }

        @Test
        void testStraightAndTripleLinked(){
            var handTiles = List.of(M1, M1, M1, M2, M2, M2, M3, M3, M3, M7, M8, M9, M9);
            var expected = Set.of(
                    List.of(List.of(M9, M9), List.of(M1, M1, M1), List.of(M2, M2, M2), List.of(M3, M3, M3), List.of(M7, M8, M9)),
                    List.of(List.of(M9, M9), List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(M7, M8, M9))
            );
            var result = arrangeAll(handTiles, M9);
            assertEquals(expected, result);
        }
    }
//
//    @Nested
//    class TestArrange {
//
//        private Optional<List<List<Tile>>> arrange(List<Tile> bodyTiles) {
//            try {
//                var method = HandTiles.class.getDeclaredMethod("arrange", List.class);
//                method.setAccessible(true);
//                @SuppressWarnings("unchecked")
//                var result = (Optional<List<List<Tile>>>) method.invoke(null, bodyTiles);
//                return result;
//            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        @Test
//        void testNotCompleted(){
//            var bodyTiles = List.of(M1, M2, M3, M4, M5, M6, M7, M8, M8);
//            var expected = Optional.empty();
//            var result = arrange(bodyTiles);
//            assertEquals(expected, result);
//        }
//
//        @Test
//        void testTriplesAndStraightAdjusted(){
//            var bodyTiles = List.of(M1, M2, M2, M2, M2, M3, M3, M4, M5);
//            var expected = Optional.of(List.of(List.of(M1, M2, M3), List.of(M2, M2, M2), List.of(M3, M4, M5)));
//            var result = arrange(bodyTiles);
//            assertEquals(expected, result);
//        }
//
//        @Test
//        void testDualStraight(){
//            var bodyTiles = List.of(M1, M1, M2, M2, M3, M3, M3, M4, M5);
//            var expected = Optional.of(List.of(List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(M3, M4, M5)));
//            var result = arrange(bodyTiles);
//            assertEquals(expected, result);
//        }
//    }
//
//    @Nested
//    class TestRearrangeAll {
//
//        private Set<List<List<Tile>>> rearrangeAll(List<List<Tile>> melds){
//            try {
//                var method = HandTiles.class.getDeclaredMethod("rearrangeAll", List.class);
//                method.setAccessible(true);
//                @SuppressWarnings("unchecked")
//                var result = (Set<List<List<Tile>>>) method.invoke(null, melds);
//                return result;
//            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
//                throw new RuntimeException(e);
//            }
//        }
//
//        @Test
//        void testNoConsecutiveMelds(){
//            var melds = List.of(List.of(M1, M1, M1), List.of(M2, M2, M2), List.of(M4, M4, M4), List.of(M5, M5, M5));
//            var expected = Set.of(melds);
//            var result = rearrangeAll(melds);
//            assertEquals(expected, result);
//        }
//
//        @Test
//        void testSingleConsecutiveMelds(){
//            var melds = List.of(List.of(M1, M1, M1), List.of(M2, M2, M2), List.of(M3, M3, M3), List.of(DW, DW, DW));
//            var expected = Set.of(melds,
//                    List.of(List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(DW, DW, DW)));
//            var result = rearrangeAll(melds);
//            assertEquals(expected, result);
//        }
//
//        @Test
//        void testMultipleConsecutiveMelds(){
//            var melds = List.of(List.of(M1, M1, M1), List.of(M2, M2, M2), List.of(M3, M3, M3), List.of(M4, M4, M4));
//            var expected = Set.of(melds,
//                    List.of(List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(M4, M4, M4)),
//                    List.of(List.of(M1, M1, M1), List.of(M2, M3, M4), List.of(M2, M3, M4), List.of(M2, M3, M4)));
//            var result = rearrangeAll(melds);
//            assertEquals(expected, result);
//        }
//    }

    @Nested
    class TestMeldComparator {

        private Comparator<List<Tile>> meldComparator(){
            try{
                var method = HandTiles.class.getDeclaredMethod("meldComparator");
                method.setAccessible(true);
                @SuppressWarnings("unchecked")
                var comparator = (Comparator<List<Tile>>) method.invoke(null);
                return comparator;
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        }

        @Test
        void testComparator(){
            var unsortedMelds = List.of(List.of(M1, M2, M3), List.of(M2, M2, M2), List.of(M3, M3), List.of(M1, M2, M3));
            var expected = List.of(List.of(M3, M3), List.of(M1, M2, M3), List.of(M1, M2, M3), List.of(M2, M2, M2));
            var result = unsortedMelds.stream().sorted(meldComparator()).toList();
            assertEquals(expected, result);
        }
    }

    @Nested
    class TestWaitingTargetsOf {

        @Test
        void testSingleHead(){
            assertEquals(Set.of(M1), waitingTargetsOf(List.of(M1, M1)));
        }

        @Test
        void testSingleHeadWithPrisedRed(){
            assertEquals(Set.of(M5, M5R), waitingTargetsOf(List.of(M5, M5)));
        }

        @Test
        void testDoubleSideStraight(){
            assertEquals(Set.of(M1, M4), waitingTargetsOf(List.of(M2, M3)));
        }

        @Test
        void testDoubleSideStraightWithPrisedRed(){
            assertEquals(Set.of(M2, M5, M5R), waitingTargetsOf(List.of(M3, M4)));
        }

        @Test
        void testSingleSideStraight(){
            assertEquals(Set.of(M7), waitingTargetsOf(List.of(M8, M9)));
        }

        @Test
        void testMiddleStraight(){
            assertEquals(Set.of(M2), waitingTargetsOf(List.of(M1, M3)));
        }

        @Test
        void testMiddleStraightWithPrisedRed(){
            assertEquals(Set.of(M5, M5R), waitingTargetsOf(List.of(M4, M6)));
        }
    }

    @Nested
    class TestReadyKanTargetsOf {

        @Test
        void testNoTargetsWithForbiddenConcealedKan(){
            var expected = Set.of();
            var handTiles = List.of(P1, P1, P1, P1, P2, P3, P7, P8, P9, P9, P9, P9, DW);
            var result = readyKanTargetsOf(handTiles);
            assertEquals(expected, result);
        }

        @Test
        void testTargetsWithForbiddenFormChangingKan(){
            var expected = Set.of(DW);
            var handTiles = List.of(P1, P1, P1, P2, P2, P2, P3, P3, P3, DW, DW, DW, DR);
            var result = readyKanTargetsOf(handTiles);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleTargets(){
            var expected = Set.of(P1, P3, P4, DW);
            var handTiles = List.of(P1, P1, P1, P3, P3, P3, P4, P4, P4, DW, DW, DW, DR);
            var result = readyKanTargetsOf(handTiles);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleTargetsWithPrisedRed1(){
            var expected = Set.of(P1, P3, P5, P5R, DW);
            var handTiles = List.of(P1, P1, P1, P3, P3, P3, P5, P5, P5, DW, DW, DW, DR);
            var result = readyKanTargetsOf(handTiles);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleTargetsWithPrisedRed2(){
            var expected = Set.of(P1, P3, P5, P5R, DW);
            var handTiles = List.of(P1, P1, P1, P3, P3, P3, P5, P5, P5R, DW, DW, DW, DR);
            var result = readyKanTargetsOf(handTiles);
            assertEquals(expected, result);
        }
    }

    @Nested
    class TestSelfKanTargetsOf {

        @Test
        void testNoTarget(){
            var expected = Set.of();
            var handTiles = List.of(P1, P2, P3, P4, P5, P6, P6);
            var result = selfKanTargetsOf(handTiles, P6);
            assertEquals(expected, result);
        }

        @Test
        void testSingleTarget(){
            var expected = Set.of(P3);
            var handTiles = List.of(P1, P2, P3, P3, P3, P3, DR);
            var result = selfKanTargetsOf(handTiles, DR);
            assertEquals(expected, result);
        }

        @Test
        void testSingleTargetWithPrisedRed(){
            var expected = Set.of(M5, M5R);
            var handTiles = List.of(P1, P2, M5, M5, M5, M5R, DR);
            var result = selfKanTargetsOf(handTiles, DR);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleTarget(){
            var expected = Set.of(M1, M2);
            var handTiles = List.of(M1, M1, M1, M1, M2, M2, M2, M2, DR, DR);
            var result = selfKanTargetsOf(handTiles, DR);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleTargetWithPrisedRed(){
            var expected = Set.of(M1, M5, M5R);
            var handTiles = List.of(M1, M1, M1, M1, M5, M5, M5, M5R, DR, DR);
            var result = selfKanTargetsOf(handTiles, DR);
            assertEquals(expected, result);
        }
    }

    @Nested
    class TestAddKanTargetsOf {

        @Test
        void testNoTarget(){
            var expected = Set.of();
            var handTiles = List.of(P1, P2, P3, DG, DG, DG, DR, DR, DR, DR);
            var melds = List.of(List.of(M1, M1, M1));
            var result = addKanTargetsOf(handTiles, DW, melds);
            assertEquals(expected, result);
        }

        @Test
        void testSingleTarget(){
            var expected = Set.of(M1);
            var handTiles = List.of(P1, P2, P3, DR);
            var melds = List.of(List.of(M1, M1, M1), List.of(M2, M2, M2), List.of(M3, M3, M3));
            var result = addKanTargetsOf(handTiles, M1, melds);
            assertEquals(expected, result);
        }

        @Test
        void testSingleTargetWithPrisedRed1(){
            var expected = Set.of(M5R);
            var handTiles = List.of(P1, P2, P3, DR);
            var melds = List.of(List.of(M1, M1, M1), List.of(M2, M2, M2), List.of(M5, M5, M5));
            var result = addKanTargetsOf(handTiles, M5R, melds);
            assertEquals(expected, result);
        }

        @Test
        void testSingleTargetWithPrisedRed2(){
            var expected = Set.of(M5);
            var handTiles = List.of(P1, P2, P3, DR);
            var melds = List.of(List.of(M1, M1, M1), List.of(M2, M2, M2), List.of(M5, M5, M5R));
            var result = addKanTargetsOf(handTiles, M5, melds);
            assertEquals(expected, result);
        }
    }

    @Nested
    class TestKanBasesOf {

        @Test
        void testNoBase(){
            var expected = Set.of();
            var result = kanBasesOf(List.of(M1, M1, M2, M2), M1);
            assertEquals(expected, result);
        }

        @Test
        void testSingleBase(){
            var expected = Set.of(List.of(M1, M1, M1));
            var result = kanBasesOf(List.of(M1, M1, M1, DR), M1);
            assertEquals(expected, result);
        }

        @Test
        void testSingleBaseWithPrisedRed(){
            var expected = Set.of(List.of(M5, M5, M5R));
            var result = kanBasesOf(List.of(M5, M5, M5R, DR), M5);
            assertEquals(expected, result);
        }
    }

    @Nested
    class TestPonBasesOf {

        @Test
        void testNoBase(){
            var expected = Set.of();
            var result = ponBasesOf(List.of(M1, M2, M3, DR), M2);
            assertEquals(expected, result);
        }

        @Test
        void testSingleBase1(){
            var expected = Set.of(List.of(DR, DR));
            var result = ponBasesOf(List.of(M1, M2, DR, DR), DR);
            assertEquals(expected, result);
        }

        @Test
        void testSingleBase2(){
            var expected = Set.of(List.of(DR, DR));
            var result = ponBasesOf(List.of(M1, DR, DR, DR), DR);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleBasesWithPrisedRed(){
            var expected = Set.of(List.of(M5, M5), List.of(M5, M5R));
            var result = ponBasesOf(List.of(M5, M5, M5R, DR), M5);
            assertEquals(expected, result);
        }
    }


    @Nested
    class TestChiBasesOf {

        @Test
        void testNoChiBase1() {
            var expected = Set.of();
            var result = chiBasesOf(List.of(M8, M9, P1, P2), P1);
            assertEquals(expected, result);
        }

        @Test
        void testNoChiBase2(){
            var expected = Set.of();
            var result = chiBasesOf(List.of(M1, M2, M3, M4, M5, DW, DW), M7);
            assertEquals(expected, result);
        }

        @Test
        void testNoChiBaseForAllHandTilesWillBeStraightSliding(){
            var expected = Set.of();
            var result = chiBasesOf(List.of(M1, M1, M2, M3), M4);
            assertEquals(expected, result);
        }

        @Test
        void testSingleBase(){
            var expected = Set.of(List.of(M1, M2));
            var result = chiBasesOf(List.of(M1, M1, M1, M2, M2, M2, M3, M3, M3, DW), M3);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleBases() {
            var expected = Set.of(List.of(M1, M2), List.of(M2, M4), List.of(M4, M5));
            var result = chiBasesOf(List.of(M1, M2, M4, M5, DW, DW, DW), M3);
            assertEquals(expected, result);
        }

        @Test
        void testMultipleBasesWithPrisedRed() {
            var expected = Set.of(List.of(M1, M2), List.of(M2, M4), List.of(M4, M5), List.of(M4, M5R));
            var result = chiBasesOf(List.of(M1, M2, M4, M5, M5R, DW, DW), M3);
            assertEquals(expected, result);
        }
    }
}
