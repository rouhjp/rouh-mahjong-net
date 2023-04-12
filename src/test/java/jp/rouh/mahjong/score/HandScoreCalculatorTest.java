package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;
import jp.rouh.mahjong.tile.Wind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static jp.rouh.mahjong.score.WinningCharacteristics.*;
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

    private ScoringContext simpleContext(Wind roundWind, Wind seatWind, WinningCharacteristics...characteristics){
        return new WinningSituation(roundWind, seatWind, List.of(), List.of(), List.of(characteristics));
    }

    private ScoringContext simpleContext(Wind wind, WinningCharacteristics...characteristics){
        return simpleContext(wind, wind, characteristics);
    }

    private ScoringContext simpleContext(WinningCharacteristics...characteristics){
        return simpleContext(EAST, characteristics);
    }

    @Test
    public void testBlessingOfHeaven(){
        var handType = calculator.forName("天和");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), P3, simpleContext(EAST, CONCEALED, FIRST_AROUND_WIN, TSUMO));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testBlessingOfEarth(){
        var handType = calculator.forName("地和");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), P3, simpleContext(WEST, CONCEALED, FIRST_AROUND_WIN, TSUMO));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testThirteenOrphans(){
        var handType = calculator.forName("国士無双");
        var handTiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, WN, DW, DG, DG);
        var result = calculator.calculate(handTiles, List.of(), DR, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testThirteenOrphans13Wait(){
        var handType = calculator.forName("国士無双十三面");
        var handTiles = List.of(M1, M9, P1, P9, S1, S9, WE, WS, WW, WN, DW, DG, DR);
        var result = calculator.calculate(handTiles, List.of(), DR, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNineGates(){
        var handType = calculator.forName("九蓮宝燈");
        var handTiles = List.of(M1, M1, M1, M2, M3, M4, M5, M5, M7, M8, M9, M9, M9);
        var result = calculator.calculate(handTiles, List.of(), M6, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNineGates9Wait(){
        var handType = calculator.forName("純正九蓮宝燈");
        var handTiles = List.of(M1, M1, M1, M2, M3, M4, M5, M6, M7, M8, M9, M9, M9);
        var result = calculator.calculate(handTiles, List.of(), M6, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testFourQuads(){
        var handType = calculator.forName("四槓子");
        var handTiles = List.of(M1);
        var openMelds = toMeld(List.of(M2, M2, M2, M2), List.of(P4, P4, P4, P4), List.of(WE, WE, WE, WE), List.of(DW, DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testBigThree(){
        var handType = calculator.forName("大三元");
        var handTiles = List.of(M1, M2, M3, WE, WE, DR, DR, DG, DG, DG);
        var openMelds = toMeld(List.of(DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, DR, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testSmallWind(){
        var handType1 = calculator.forName("小四喜");
        var handType2 = calculator.forName("大四喜");
        var handTiles = List.of(WE, WE, WE, WN, WN, S3, S3);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(WW, WW, WW));
        var result = calculator.calculate(handTiles, openMelds, S3, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testBigWind(){
        var handType1 = calculator.forName("小四喜");
        var handType2 = calculator.forName("大四喜");
        var handTiles = List.of(WE, WE, WE, WN, WN, S3, S3);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(WW, WW, WW));
        var result = calculator.calculate(handTiles, openMelds, WN, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertTrue(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testAllHonors(){
        var handType = calculator.forName("字一色");
        var handTiles = List.of(WE, WE, DG, DG);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(WW, WW, WW), List.of(DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, DG, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testAllTerminals(){
        var handType = calculator.forName("清老頭");
        var handTiles = List.of(M1, M1, M9, M9);
        var openMelds = toMeld(List.of(S1, S1, S1), List.of(P1, P1, P1), List.of(P9, P9, P9));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testAllGreens(){
        var handType = calculator.forName("緑一色");
        var handTiles = List.of(S2, S3, S4, S8, S8, DG, DG);
        var openMelds = toMeld(List.of(S2, S3, S4), List.of(S2, S3, S4));
        var result = calculator.calculate(handTiles, openMelds, S8, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testFourConcealedTriples(){
        var handType = calculator.forName("四暗刻");
        var handTiles = List.of(S1, S1, S1, S2, S2, S2, M1, M1, M1, M4, M4, P4, P4);
        var result = calculator.calculate(handTiles, List.of(), P4, simpleContext(CONCEALED, TSUMO));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotFourConcealedTriplesForRon(){
        var handType = calculator.forName("四暗刻");
        var handTiles = List.of(S1, S1, S1, S2, S2, S2, M1, M1, M1, M4, M4, P4, P4);
        var result = calculator.calculate(handTiles, List.of(), P4, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testFourConcealedTriples1Wait(){
        var handType = calculator.forName("四暗刻単騎");
        var handTiles = List.of(S1, S1, S1, S2, S2, S2, M1, M1, M1, M4, M4, M4, P4);
        var result = calculator.calculate(handTiles, List.of(), P4, simpleContext(CONCEALED, TSUMO));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testReady(){
        var handType1 = calculator.forName("立直");
        var handType2 = calculator.forName("両立直");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), P3, simpleContext(CONCEALED, READY));
        Assertions.assertTrue(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testDoubleReady(){
        var handType1 = calculator.forName("立直");
        var handType2 = calculator.forName("両立直");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), P3, simpleContext(CONCEALED, READY, FIRST_AROUND_READY));
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertTrue(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testOneShot(){
        var handType = calculator.forName("一発");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), P3, simpleContext(CONCEALED, READY, READY_AROUND_WIN));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testSelfPick(){
        var handType = calculator.forName("門前清自摸和");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), P3, simpleContext(CONCEALED, TSUMO));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotSelfPickWhenCalled(){
        var handType = calculator.forName("門前清自摸和");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, P3, simpleContext(TSUMO));
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testLastTileDraw(){
        var handType = calculator.forName("海底摸月");
        var handTiles = List.of(S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, P3, simpleContext(TSUMO, LAST_TILE_DRAW_WIN));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testLastTileGrab(){
        var handType = calculator.forName("河底撈魚");
        var handTiles = List.of(S3, S4, S5, S5, S6, S7, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, P3, simpleContext(LAST_TILE_CALL_WIN));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testQuadDraw(){
        var handType = calculator.forName("嶺上開花");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, P3, simpleContext(TSUMO, QUAD_TILE_DRAW_WIN));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testQuadGrab(){
        var handType = calculator.forName("槍槓");
        var handTiles = List.of(M1, M2, M3, S3, S4, S5, P1, P2, DW, DW);
        var openMelds = toMeld(List.of(WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, P3, simpleContext(QUAD_TILE_CALL_WIN));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testAllTriples(){
        var handType = calculator.forName("対々和");
        var handTiles = List.of(M1, M1, M2, M2);
        var openMelds = toMeld(List.of(S1, S1, S1), List.of(P2, P2, P2), List.of(M4, M4, M4));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testThreeConcealedTriples(){
        var handType = calculator.forName("三暗刻");
        var handTiles = List.of(M1, M1, M1, M3, M3, M3, P1, P1, P1, WS);
        var openMelds = toMeld(List.of(P5, P6, P7));
        var result = calculator.calculate(handTiles, openMelds, WS, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testThreeConcealedTriplesByDrawn(){
        var handType = calculator.forName("三暗刻");
        var handTiles = List.of(M1, M1, M1, M3, M3, M3, P1, P1, WS, WS);
        var openMelds = toMeld(List.of(P5, P6, P7));
        var result = calculator.calculate(handTiles, openMelds, WS, simpleContext(TSUMO));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotThreeConcealedTriplesByClaim(){
        var handType = calculator.forName("三暗刻");
        var handTiles = List.of(M1, M1, M1, M3, M3, M3, P1, P1, WS, WS);
        var openMelds = toMeld(List.of(P5, P6, P7));
        var result = calculator.calculate(handTiles, openMelds, WS, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNoPoint(){
        var handType = calculator.forName("平和");
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S2, S3, P4, P5, P6, WN, WN);
        var result = calculator.calculate(handTiles, List.of(), S4, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotNoPointWhenHeadIsDragons(){
        var handType = calculator.forName("平和");
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S2, S3, P4, P5, P6, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), S4, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotNoPointWhenHeadIsValuableWind(){
        var handType = calculator.forName("平和");
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S2, S3, P4, P5, P6, WE, WE);
        var result = calculator.calculate(handTiles, List.of(), S4, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotNoPointWhenWaitIsNotDoubleSide(){
        var handType = calculator.forName("平和");
        var handTiles = List.of(M1, M2, M3, M7, M8, M9, S1, S2, P4, P5, P6, WE, WE);
        var result = calculator.calculate(handTiles, List.of(), S3, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotNoPointWhenCalled(){
        var handType = calculator.forName("平和");
        var handTiles = List.of(M7, M8, M9, S2, S3, P4, P5, P6, WN, WN);
        var openMelds = toMeld(List.of(M2, M3, M4));
        var result = calculator.calculate(handTiles, openMelds, S4, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testHalfTerminalSets(){
        var handType1 = calculator.forName("門前混全帯么九");
        var handType2 = calculator.forName("混全帯么九");
        var handType3 = calculator.forName("門前純全帯么九");
        var handType4 = calculator.forName("純全帯么九");
        var handTiles = List.of(M1, M2, M3, M9, M9, M9, S2, S3, DW, DW, DW, DR, DR);
        var result = calculator.calculate(handTiles, List.of(), S1, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
        Assertions.assertFalse(result.getHandTypes().contains(handType3));
        Assertions.assertFalse(result.getHandTypes().contains(handType4));
    }

    @Test
    public void testCalledHalfTerminalSets(){
        var handType1 = calculator.forName("門前混全帯么九");
        var handType2 = calculator.forName("混全帯么九");
        var handType3 = calculator.forName("門前純全帯么九");
        var handType4 = calculator.forName("純全帯么九");
        var handTiles = List.of(M1, M2, M3, M9, M9, M9, S2, S3, DR, DR);
        var openMelds = toMeld(List.of(DW, DW, DW));
        var result = calculator.calculate(handTiles, openMelds, S1, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertTrue(result.getHandTypes().contains(handType2));
        Assertions.assertFalse(result.getHandTypes().contains(handType3));
        Assertions.assertFalse(result.getHandTypes().contains(handType4));
    }

    @Test
    public void testFullStraights(){
        var handType1 = calculator.forName("門前一気通貫");
        var handType2 = calculator.forName("一気通貫");
        var handTiles = List.of(S1, S2, S3, S4, S5, S6, S7, S8, S9, M1, M1, DW, DW);
        var result = calculator.calculate(handTiles, List.of(), M1, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testCalledFullStraights(){
        var handType1 = calculator.forName("門前一気通貫");
        var handType2 = calculator.forName("一気通貫");
        var handTiles = List.of(S1, S2, S3, S7, S8, S9, M1, M1, DW, DW);
        var openMelds = toMeld(List.of(S4, S5, S6));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertTrue(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testNotFullStraightsWhenStraightsAreSlided(){
        var handType1 = calculator.forName("門前一気通貫");
        var handType2 = calculator.forName("一気通貫");
        var handTiles = List.of(S1, S2, S3, S3, S4, S5, S5, S6, S7, S7, S8, S9, M1);
        var result = calculator.calculate(handTiles, List.of(), M1, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testNotFullStraightsForDifferentTileTypes(){
        var handType1 = calculator.forName("門前一気通貫");
        var handType2 = calculator.forName("一気通貫");
        var handTiles = List.of(M1, M2, M3, S4, S5, S6, P7, P8, P9, DW, DW, M1, M1);
        var result = calculator.calculate(handTiles, List.of(), M1, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testThreeColorStraights(){
        var handType1 = calculator.forName("門前三色同順");
        var handType2 = calculator.forName("三色同順");
        var handTiles = List.of(M3, M4, M5, M5, M6, M7, P3, P4, P5, S4, S5, WW, WW);
        var result = calculator.calculate(handTiles, List.of(), S3, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testCalledThreeColorStraights(){
        var handType1 = calculator.forName("門前三色同順");
        var handType2 = calculator.forName("三色同順");
        var handTiles = List.of(M3, M4, M5, M5, M6, M7, S4, S5, WW, WW);
        var openMelds = toMeld(List.of(P3, P4, P5));
        var result = calculator.calculate(handTiles, openMelds, S3, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertTrue(result.getHandTypes().contains(handType2));
    }

    @Test
    public void testThreeColorTriples(){
        var handType = calculator.forName("三色同刻");
        var handTiles = List.of(M1, M1, M2, M2);
        var openMelds = toMeld(List.of(S1, S1, S1), List.of(P1, P1, P1), List.of(P7, P8, P9));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testDualStraights(){
        var handType = calculator.forName("一盃口");
        var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, S1, S1, S1, S3, S4);
        var result = calculator.calculate(handTiles, List.of(), S5, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotDualStraightsWhenCalled(){
        var handType = calculator.forName("一盃口");
        var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, S3, S4);
        var openMelds = toMeld(List.of(M1, M1, M1));
        var result = calculator.calculate(handTiles, openMelds, S5, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testDoubleDualStraights(){
        var handType = calculator.forName("二盃口");
        var handTiles = List.of(M1, M1, M2, M2, M3, M3, M4, M4, S6, S6, S7, S7, S8);
        var result = calculator.calculate(handTiles, List.of(), S8, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotDoubleDualStraightsWhenCalled(){
        var handType = calculator.forName("二盃口");
        var handTiles = List.of(M1, M1, M2, M3, M4, S6, S6, S7, S7, S8);
        var openMelds = toMeld(List.of(M2, M3, M4));
        var result = calculator.calculate(handTiles, openMelds, S8, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testSevenPairs(){
        var handType = calculator.forName("七対子");
        var handTiles = List.of(M2, M2, M4, M4, S7, S7, S8, P2, P2, WE, WE, WN, WN);
        var result = calculator.calculate(handTiles, List.of(), S8, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNotSevenPairsWhenDoubleDualStraights(){
        var handType = calculator.forName("七対子");
        var handTiles = List.of(M2, M2, M3, M3, M4, M4, M5, M5, S6, S6, S7, S7, S8);
        var result = calculator.calculate(handTiles, List.of(), S8, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNoOrphans(){
        var handType = calculator.forName("断么九");
        var handTiles = List.of(M2, M2, M3, M3, M4, M4, S2, S3, S4, P5, P6, P7, P8);
        var result = calculator.calculate(handTiles, List.of(), P5, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testNoOrphansWhenCalled(){
        var handType = calculator.forName("断么九");
        var handTiles = List.of(M2, M3, M4, S2, S3, S4, P5, P6, P7, P8);
        var openMelds = toMeld(List.of(M2, M3, M4));
        var result = calculator.calculate(handTiles, openMelds, P5, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testHalfSingleColor(){
        var handType1 = calculator.forName("門前混一色");
        var handType2 = calculator.forName("混一色");
        var handType3 = calculator.forName("門前清一色");
        var handType4 = calculator.forName("清一色");
        var handTiles = List.of(M1, M1, M1, M2, M3, M3, M4, M4, M5, DW, DW, DR, DR);
        var result = calculator.calculate(handTiles, List.of(), DR, simpleContext(CONCEALED));
        Assertions.assertTrue(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
        Assertions.assertFalse(result.getHandTypes().contains(handType3));
        Assertions.assertFalse(result.getHandTypes().contains(handType4));
    }

    @Test
    public void testCalledHalfSingleColor(){
        var handType1 = calculator.forName("門前混一色");
        var handType2 = calculator.forName("混一色");
        var handType3 = calculator.forName("門前清一色");
        var handType4 = calculator.forName("清一色");
        var handTiles = List.of(M2, M3, M3, M4, M4, M5, DW, DW, DR, DR);
        var openMelds = toMeld(List.of(M1, M1, M1));
        var result = calculator.calculate(handTiles, openMelds, DR, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertTrue(result.getHandTypes().contains(handType2));
        Assertions.assertFalse(result.getHandTypes().contains(handType3));
        Assertions.assertFalse(result.getHandTypes().contains(handType4));
    }

    @Test
    public void testFullSingleColor(){
        var handType1 = calculator.forName("門前混一色");
        var handType2 = calculator.forName("混一色");
        var handType3 = calculator.forName("門前清一色");
        var handType4 = calculator.forName("清一色");
        var handTiles = List.of(M1, M1, M1, M2, M3, M3, M4, M4, M5, M8, M8, M9, M9);
        var result = calculator.calculate(handTiles, List.of(), M9, simpleContext(CONCEALED));
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
        Assertions.assertTrue(result.getHandTypes().contains(handType3));
        Assertions.assertFalse(result.getHandTypes().contains(handType4));
    }

    @Test
    public void testCalledFullSingleColor(){
        var handType1 = calculator.forName("門前混一色");
        var handType2 = calculator.forName("混一色");
        var handType3 = calculator.forName("門前清一色");
        var handType4 = calculator.forName("清一色");
        var handTiles = List.of(M2, M3, M3, M4, M4, M5, M8, M8, M9, M9);
        var openMelds = toMeld(List.of(M1, M1, M1));
        var result = calculator.calculate(handTiles, openMelds, M9, simpleContext());
        Assertions.assertFalse(result.getHandTypes().contains(handType1));
        Assertions.assertFalse(result.getHandTypes().contains(handType2));
        Assertions.assertFalse(result.getHandTypes().contains(handType3));
        Assertions.assertTrue(result.getHandTypes().contains(handType4));
    }

    @Test
    public void testThreeQuads(){
        var handType = calculator.forName("三槓子");
        var handTiles = List.of(M1, M2, M3, M4);
        var openMelds = toMeld(List.of(M8, M8, M8, M8), List.of(P4, P4, P4, P4), List.of(WE, WE, WE, WE));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testSmallThree(){
        var handType = calculator.forName("小三元");
        var handTiles = List.of(M1, M1, M2, M3, M4, DR, DR);
        var openMelds = toMeld(List.of(DW, DW, DW), List.of(DR, DR, DR));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testDragonWhite(){
        var handType = calculator.forName("飜牌 白");
        var handTiles = List.of(M1, M1, M2, M3, M4, WE, WE);
        var openMelds = toMeld(List.of(DW, DW, DW), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testDragonGreen(){
        var handType = calculator.forName("飜牌 發");
        var handTiles = List.of(M1, M1, M2, M3, M4, WE, WE);
        var openMelds = toMeld(List.of(DG, DG, DG), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testDragonRed(){
        var handType = calculator.forName("飜牌 中");
        var handTiles = List.of(M1, M1, M2, M3, M4, WE, WE);
        var openMelds = toMeld(List.of(DR, DR, DR), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext());
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testSeatWind(){
        var handType = calculator.forName("自風牌");
        var handTiles = List.of(M1, M1, M2, M3, M4, P8, P8);
        var openMelds = toMeld(List.of(WW, WW, WW), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext(EAST, WEST));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testRoundWind(){
        var handType = calculator.forName("場風牌");
        var handTiles = List.of(M1, M1, M2, M3, M4, P8, P8);
        var openMelds = toMeld(List.of(WS, WS, WS), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext(SOUTH, EAST));
        Assertions.assertTrue(result.getHandTypes().contains(handType));
    }

    @Test
    public void testRoundAndSeatWind(){
        var handType1 = calculator.forName("場風牌");
        var handType2 = calculator.forName("自風牌");
        var handTiles = List.of(M1, M1, M2, M3, M4, P8, P8);
        var openMelds = toMeld(List.of(WE, WE, WE), List.of(P1, P1, P1));
        var result = calculator.calculate(handTiles, openMelds, M1, simpleContext(EAST, EAST));
        Assertions.assertTrue(result.getHandTypes().contains(handType1));
        Assertions.assertTrue(result.getHandTypes().contains(handType2));
    }

}
