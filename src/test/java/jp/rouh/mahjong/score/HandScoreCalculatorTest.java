package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static jp.rouh.mahjong.score.WinningOption.*;
import static jp.rouh.mahjong.tile.Tile.*;
import static jp.rouh.mahjong.tile.Wind.*;

class HandScoreCalculatorTest{
    private final HandScoreCalculator calculator = new StandardHandScoreCalculator();

    @SafeVarargs
    private List<Meld> toMeld(List<Tile>...melds){
        return Stream.of(melds)
                .map(tiles->{
                    if(Tiles.isQuad(tiles)){
                        return Meld.ofCallQuad(tiles.subList(0, 3), tiles.get(3), Side.LEFT);
                    }
                    if(Tiles.isTriple(tiles)){
                        return Meld.ofCallTriple(tiles.subList(0, 2), tiles.get(2), Side.LEFT);
                    }
                    if(Tiles.isStraight(tiles)){
                        return Meld.ofCallStraight(tiles.subList(0, 2), tiles.get(2));
                    }
                    throw new IllegalArgumentException();
                }).toList();
    }

    private static final WinningSituation SIMPLE_SITUATION = new WinningSituation(SOUTH, WEST, Side.LEFT, List.of(), List.of(), List.of());


    @Test
    public void testBlessingOfHeaven(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var situation = new WinningSituation(EAST, EAST, Side.SELF, List.of(), List.of(), List.of(FIRST_AROUND_WIN));
        var result = calculator.calculate(handTiles, List.of(), P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("天和")));
    }

    @Test
    public void testBlessingOfEarth(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var situation = new WinningSituation(EAST, WEST, Side.SELF, List.of(), List.of(), List.of(FIRST_AROUND_WIN));
        var result = calculator.calculate(handTiles, List.of(), P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("地和")));
    }

    @Test
    public void testThirteenOrphans(){
        var handTiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, WN, DW, DG, DG);
        var result = calculator.calculate(handTiles, List.of(), DR, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("国士無双")));
    }

    @Test
    public void testThirteenOrphans13Wait(){
        var handTiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, WN, DW, DG, DR);
        var result = calculator.calculate(handTiles, List.of(), DR, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("国士無双十三面")));
    }

    @Test
    public void testNineGates(){
        var handTiles = List.of(M1, M1, M1, M2, M3, M4, M5, M5, M7, M8, M9, M9, M9);
        var result = calculator.calculate(handTiles, List.of(), M6, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("九蓮宝燈")));
    }

    @Test
    public void testNineGates9Wait(){
        var handTiles = List.of(M1, M1, M1, M2, M3, M4, M5, M6, M7, M8, M9, M9, M9);
        var result = calculator.calculate(handTiles, List.of(), M6, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("純正九蓮宝燈")));
    }

    @Test
    public void testFourQuads(){
        var handTiles = List.of(M1);
        var openMelds = toMeld(List.of(M2, M2, M2, M2), List.of(P4, P4, P4, P4), List.of(WE, WE, WE, WE), List.of(DW, DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("四槓子")));
    }

    @Test
    public void testBigThree(){
        var handTiles = List.of(M1, M2, M3, WE, WE, DR, DR, DG, DG, DG);
        var openMelds = toMeld(List.of(DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, DR, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("大三元")));
    }

    @Test
    public void testSmallWind(){
        var handTiles = List.of(WE, WE, WE, WN, WN, S3, S3);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(WW, WW, WW));
        var result = calculator.calculate(handTiles, openMelds, S3, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("小四喜")));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("大四喜")));
    }

    @Test
    public void testBigWind(){
        var handTiles = List.of(WE, WE, WE, WN, WN, S3, S3);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(WW, WW, WW));
        var result = calculator.calculate(handTiles, openMelds, WN, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("小四喜")));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("大四喜")));
    }

    @Test
    public void testAllHonors(){
        var handTiles = List.of(WE, WE, DG, DG);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(WW, WW, WW), List.of(DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, DG, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("字一色")));
    }

    @Test
    public void testAllTerminals(){
        var handTiles = List.of(M1, M1, M9, M9);
        var openMelds = toMeld(List.of(S1, S1, S1), List.of(P1, P1, P1), List.of(P9, P9, P9));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清老頭")));
    }

    @Test
    public void testAllGreens(){
        var handTiles = List.of(S2, S3, S4, S8, S8, DG, DG);
        var openMelds = toMeld(List.of(S2, S3, S4), List.of(S2, S3, S4));
        var result = calculator.calculate(handTiles, openMelds, S8, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("緑一色")));
    }

    @Test
    public void testFourConcealedTriples(){
        var handTiles = List.of(S1, S1, S1, S2, S2, S2, M1, M1, M1, M4, M4, P4, P4);
        var situation = new WinningSituation(EAST, EAST, Side.SELF, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, List.of(), P4, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("四暗刻")));
    }

    @Test
    public void testNotFourConcealedTriplesForRon(){
        var handTiles = List.of(S1, S1, S1, S2, S2, S2, M1, M1, M1, M4, M4, P4, P4);
        var situation = new WinningSituation(EAST, EAST, Side.LEFT, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, List.of(), P4, situation);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("四暗刻")));
    }

    @Test
    public void testFourConcealedTriples1Wait(){
        var handTiles = List.of(S1, S1, S1, S2, S2, S2, M1, M1, M1, M4, M4, M4, P4);
        var situation = new WinningSituation(EAST, EAST, Side.LEFT, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, List.of(), P4, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("四暗刻単騎")));
    }

    @Test
    public void testReady(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var situation = new WinningSituation(EAST, EAST, Side.LEFT, List.of(), List.of(), List.of(READY));
        var result = calculator.calculate(handTiles, List.of(), P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("立直")));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("両立直")));
    }

    @Test
    public void testDoubleReady(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var situation = new WinningSituation(EAST, EAST, Side.LEFT, List.of(), List.of(), List.of(READY, FIRST_AROUND_READY));
        var result = calculator.calculate(handTiles, List.of(), P3, situation);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("立直")));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("両立直")));
    }

    @Test
    public void testOneShot(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var situation = new WinningSituation(EAST, EAST, Side.LEFT, List.of(), List.of(), List.of(READY, READY_AROUND_WIN));
        var result = calculator.calculate(handTiles, List.of(), P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一発")));
    }

    @Test
    public void testSelfPick(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var situation = new WinningSituation(EAST, EAST, Side.SELF, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, List.of(), P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("門前清自摸和")));
    }

    @Test
    public void testNotSelfPickWhenCalled(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, P3, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("門前清自摸和")));
    }

    @Test
    public void testLastTileDraw(){
        var handTiles = List.of(S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE));
        var situation = new WinningSituation(EAST, EAST, Side.SELF, List.of(), List.of(), List.of(LAST_TILE_WIN));
        var result = calculator.calculate(handTiles, openMelds, P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("海底摸月")));
    }

    @Test
    public void testLastTileGrab(){
        var handTiles = List.of(S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE));
        var situation = new WinningSituation(EAST, EAST, Side.LEFT, List.of(), List.of(), List.of(LAST_TILE_WIN));
        var result = calculator.calculate(handTiles, openMelds, P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("河底撈魚")));
    }

    @Test
    public void testQuadDraw(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE, WE));
        var situation = new WinningSituation(EAST, EAST, Side.SELF, List.of(), List.of(), List.of(QUAD_TURN_WIN));
        var result = calculator.calculate(handTiles, openMelds, P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("嶺上開花")));
    }

    @Test
    public void testQuadGrab(){
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE));
        var situation = new WinningSituation(EAST, EAST, Side.ACROSS, List.of(), List.of(), List.of(QUAD_TILE_WIN));
        var result = calculator.calculate(handTiles, openMelds, P3, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("槍槓")));
    }

    @Test
    public void testAllTriples(){
        var handTiles = List.of(M1, M1, M2, M2);
        var openMelds = toMeld(List.of(S1, S1, S1), List.of(P2, P2, P2), List.of(M4, M4, M4));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("対々和")));
    }

    @Test
    public void testThreeConcealedTriples(){
        var handTiles = List.of(M1, M1, M1, M3, M3, M3, P1, P1, P1, WS);
        var openMelds = toMeld(List.of(P5, P6, P7));
        var situation = new WinningSituation(EAST, EAST, Side.ACROSS, List.of(), List.of(), List.of(QUAD_TILE_WIN));
        var result = calculator.calculate(handTiles, openMelds, WS, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三暗刻")));
    }

    @Test
    public void testThreeConcealedTriplesByDrawn(){
        var handTiles = List.of(M1, M1, M1, M3, M3, M3, P1, P1, WS, WS);
        var openMelds = toMeld(List.of(P5, P6, P7));
        var situation = new WinningSituation(EAST, EAST, Side.SELF, List.of(), List.of(), List.of(QUAD_TILE_WIN));
        var result = calculator.calculate(handTiles, openMelds, WS, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三暗刻")));
    }

    @Test
    public void testNotThreeConcealedTriplesByClaim(){
        var handTiles = List.of(M1, M1, M1, M3, M3, M3, P1, P1, WS, WS);
        var openMelds = toMeld(List.of(P5, P6, P7));
        var situation = new WinningSituation(EAST, EAST, Side.ACROSS, List.of(), List.of(), List.of(QUAD_TILE_WIN));
        var result = calculator.calculate(handTiles, openMelds, WS, situation);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三暗刻")));
    }

    @Test
    public void testNoPoint(){
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S2, S3, P4, P5, P6, WN, WN);
        var result = calculator.calculate(handTiles, List.of(), S4, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("平和")));
    }

    @Test
    public void testNotNoPointWhenHeadIsDragons(){
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S2, S3, P4, P5, P6, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), S4, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("平和")));
    }

    @Test
    public void testNotNoPointWhenHeadIsValuableWind(){
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S2, S3, P4, P5, P6, WE, WE);
        var situation = new WinningSituation(EAST, EAST, Side.LEFT, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, List.of(), S4, situation);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("平和")));
    }

    @Test
    public void testNotNoPointWhenWaitIsNotDoubleSide(){
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S1, S2, P4, P5, P6, P8, P8);
        var result = calculator.calculate(handTiles, List.of(), S3, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("平和")));
    }

    @Test
    public void testNotNoPointWhenCalled(){
        var handTiles = List.of(M7, M8, M9, S2, S3, P4, P5, P6, P8, P8);
        var openMelds = toMeld(List.of(M2, M3, M4));
        var result = calculator.calculate(handTiles, openMelds, S4, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("平和")));
    }

    @Test
    public void testHalfTerminalSets(){
        var handTiles = List.of(M1, M2, M3, M9, M9, M9, S2, S3, DW, DW, DW, DR, DR);
        var result = calculator.calculate(handTiles, List.of(), S1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混全帯么九") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混全帯么九") && type.getDoubles()==1));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("純全帯么九") && type.getDoubles()==3));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("純全帯么九") && type.getDoubles()==2));
    }

    @Test
    public void testCalledHalfTerminalSets(){
        var handTiles = List.of(M1, M2, M3, M9, M9, M9, S2, S3, DR, DR);
        var openMelds = toMeld(List.of(DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, S1, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混全帯么九") && type.getDoubles()==2));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混全帯么九") && type.getDoubles()==1));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("純全帯么九") && type.getDoubles()==3));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("純全帯么九") && type.getDoubles()==2));
    }

    @Test
    public void testFullStraights(){
        var handTiles = List.of(S1, S2, S3, S4, S5, S6, S7, S8, S9, M1, M1, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==1));
    }

    @Test
    public void testCalledFullStraights(){
        var handTiles = List.of(S1, S2, S3, S7, S8, S9, M1, M1, DW, DW);
        var openMelds = toMeld(List.of(S4, S5, S6));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==2));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==1));
    }

    @Test
    public void testNotFullStraightsWhenStraightsAreSlided(){
        var handTiles = List.of(S1, S2, S3, S3, S4, S5, S5, S6, S7, S7, S8, S9, M1);
        var result = calculator.calculate(handTiles, List.of(), M1, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==1));
    }

    @Test
    public void testNotFullStraightsForDifferentTileTypes(){
        var handTiles = List.of(M1, M2, M3, S4, S5, S6, P7, P8, P9, DW, DW, M1, M1);
        var result = calculator.calculate(handTiles, List.of(), M1, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一気通貫") && type.getDoubles()==1));
    }

    @Test
    public void testThreeColorStraights(){
        var handTiles = List.of(M3, M4, M5, M5, M6, M7, P3, P4, P5, S4, S5, WW, WW);
        var result = calculator.calculate(handTiles, List.of(), S3, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三色同順") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三色同順") && type.getDoubles()==1));
    }

    @Test
    public void testCalledThreeColorStraights(){
        var handTiles = List.of(M3, M4, M5, M5, M6, M7, S4, S5, WW, WW);
        var openMelds = toMeld(List.of(P3, P4, P5));
        var result = calculator.calculate(handTiles, openMelds, S3, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三色同順") && type.getDoubles()==2));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三色同順") && type.getDoubles()==1));
    }

    @Test
    public void testThreeColorTriples(){
        var handTiles = List.of(M1, M1, M2, M2);
        var openMelds = toMeld(List.of(S1, S1, S1), List.of(P1, P1, P1), List.of(P7, P8, P9));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三色同刻")));
    }

    @Test
    public void testDualStraights(){
        var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, S1, S1, S1, S3, S4);
        var result = calculator.calculate(handTiles, List.of(), S5, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一盃口")));
    }

    @Test
    public void testNotDualStraightsWhenCalled(){
        var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, S3, S4);
        var openMelds = toMeld(List.of(M1, M1, M1));
        var result = calculator.calculate(handTiles, openMelds, S5, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("一盃口")));
    }

    @Test
    public void testDoubleDualStraights(){
        var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, S6, S6, S7, S7, S8);
        var result = calculator.calculate(handTiles, List.of(), S8, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("二盃口")));
    }

    @Test
    public void testNotDoubleDualStraightsWhenCalled(){
        var handTiles = List.of(M1, M1, M2, M3, M4, S6, S6, S7, S7, S8);
        var openMelds = toMeld(List.of(M2, M3, M4));
        var result = calculator.calculate(handTiles, openMelds, S8, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("二盃口")));
    }

    @Test
    public void testSevenPairs(){
        var handTiles = List.of(M2, M2, M4, M4, S7, S7, S8, P2, P2, WE, WE, WN, WN);
        var result = calculator.calculate(handTiles, List.of(), S8, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("七対子")));
    }

    @Test
    public void testNotSevenPairsWhenDoubleDualStraights(){
        var handTiles = List.of(M2, M2, M3, M3, M4, M4, M5, M5, S6, S6, S7, S7, S8);
        var result = calculator.calculate(handTiles, List.of(), S8, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("七対子")));
    }

    @Test
    public void testNoOrphans(){
        var handTiles = List.of(M2, M2, M3, M3, M4, M4, S2, S3, S4, P5, P6, P7, P8);
        var result = calculator.calculate(handTiles, List.of(), P5, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("断么九")));
    }

    @Test
    public void testNoOrphansWhenCalled(){
        var handTiles = List.of(M2, M3, M4, S2, S3, S4, P5, P6, P7, P8);
        var openMelds = toMeld(List.of(M2, M3, M4));
        var result = calculator.calculate(handTiles, openMelds, P5, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("断么九")));
    }

    @Test
    public void testHalfSingleColor(){
        var handTiles = List.of(M1, M1, M1, M2, M3, M3, M4, M4, M5, DW, DW, DR, DR);
        var result = calculator.calculate(handTiles, List.of(), DR, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==3));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清一色") && type.getDoubles()==6));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清一色") && type.getDoubles()==5));
    }

    @Test
    public void testCalledHalfSingleColor(){
        var handTiles = List.of(M2, M3, M3, M4, M4, M5, DW, DW, DR, DR);
        var openMelds = toMeld(List.of(M1, M1, M1));
        var result = calculator.calculate(handTiles, openMelds, DR, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==3));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("門前清一色") && type.getDoubles()==6));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清一色") && type.getDoubles()==5));
    }

    @Test
    public void testFullSingleColor(){
        var handTiles = List.of(M1, M1, M1, M2, M3, M3, M4, M4, M5, M8, M8, M9, M9);
        var result = calculator.calculate(handTiles, List.of(), M9, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==3));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==2));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清一色") && type.getDoubles()==6));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清一色") && type.getDoubles()==5));
    }

    @Test
    public void testCalledFullSingleColor(){
        var handTiles = List.of(M2, M3, M3, M4, M4, M5, M8, M8, M9, M9);
        var openMelds = toMeld(List.of(M1, M1, M1));
        var result = calculator.calculate(handTiles, openMelds, M9, SIMPLE_SITUATION);
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==3));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("混一色") && type.getDoubles()==2));
        Assertions.assertFalse(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清一色") && type.getDoubles()==6));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("清一色") && type.getDoubles()==5));
    }

    @Test
    public void testThreeQuads(){
        var handTiles = List.of(M1, M2, M3, M4);
        var openMelds = toMeld(List.of(M8, M8, M8, M8), List.of(P4, P4, P4, P4), List.of(WE, WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("三槓子")));
    }

    @Test
    public void testSmallThree(){
        var handTiles = List.of(M1, M1, M2, M3, M4, DR, DR);
        var openMelds = toMeld(List.of(DW, DW, DW), List.of(DR, DR, DR));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("小三元")));
    }

    @Test
    public void testDragonWhite(){
        var handTiles = List.of(M1, M1, M2, M3, M4, WE, WE);
        var openMelds = toMeld(List.of(DW, DW, DW), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("飜牌 白")));
    }

    @Test
    public void testDragonGreen(){
        var handTiles = List.of(M1, M1, M2, M3, M4, WE, WE);
        var openMelds = toMeld(List.of(DG, DG, DG), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("飜牌 發")));
    }

    @Test
    public void testDragonRed(){
        var handTiles = List.of(M1, M1, M2, M3, M4, WE, WE);
        var openMelds = toMeld(List.of(DR, DR, DR), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, SIMPLE_SITUATION);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("飜牌 中")));
    }

    @Test
    public void testSeatWind(){
        var handTiles = List.of(M1, M1, M2, M3, M4, P8, P8);
        var openMelds = toMeld(List.of(WW, WW, WW), List.of(P1, P1, P1));
        var situation = new WinningSituation(EAST, WEST, Side.SELF, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, openMelds, M1, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("自風牌")));
    }

    @Test
    public void testRoundWind(){
        var handTiles = List.of(M1, M1, M2, M3, M4, P8, P8);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(P1, P1, P1));
        var situation = new WinningSituation(SOUTH, WEST, Side.SELF, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, openMelds, M1, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("場風牌")));
    }

    @Test
    public void testRoundAndSeatWind(){
        var handTiles = List.of(M1, M1, M2, M3, M4, P8, P8);
        var openMelds = toMeld(List.of(WE, WE, WE), List.of(P1, P1, P1));
        var situation = new WinningSituation(EAST, EAST, Side.SELF, List.of(), List.of(), List.of());
        var result = calculator.calculate(handTiles, openMelds, M1, situation);
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("場風牌")));
        Assertions.assertTrue(result.getHandTypes().stream().anyMatch(type->type.getName().equals("自風牌")));
    }

}
