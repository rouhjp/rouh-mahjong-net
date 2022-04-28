package jp.rouh.mahjong.score;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static jp.rouh.mahjong.tile.Tile.*;

@DisplayName("HandTilesクラステスト")
public class HandTilesTest{

    @Nested
    class ArrangeTest{

        @Test
        @DisplayName("大車輪型")
        void case1(){
            var handTiles = List.of(M2, M2, M3, M3, M4, M4, M5, M5R, M6, M6, M7, M7, M8);
            var result = HandTiles.arrange(handTiles, M8);
            assertEquals(
                    Set.of(
                            List.of(List.of(M2, M2), List.of(M3, M4, M5), List.of(M3, M4, M5R), List.of(M6, M7, M8), List.of(M6, M7, M8)),
                            List.of(List.of(M5, M5R), List.of(M2, M3, M4), List.of(M2, M3, M4), List.of(M6, M7, M8), List.of(M6, M7, M8)),
                            List.of(List.of(M8, M8), List.of(M2, M3, M4), List.of(M2, M3, M4), List.of(M5, M6, M7), List.of(M5R, M6, M7))
                    ), result);
        }

        @Test
        @DisplayName("三連刻型")
        void case2(){
            var handTiles = List.of(P1, P1, P1, P2, P2, P2, P3, P3, P3, S7, S8, S9, S1);
            var result = HandTiles.arrange(handTiles, S1);
            assertEquals(
                    Set.of(
                            List.of(List.of(S1, S1), List.of(P1, P1, P1), List.of(P2, P2, P2), List.of(P3, P3, P3), List.of(S7, S8, S9)),
                            List.of(List.of(S1, S1), List.of(P1, P2, P3), List.of(P1, P2, P3), List.of(P1, P2, P3), List.of(S7, S8, S9))
                    ), result);
        }

        @Test
        @DisplayName("一色四順型")
        void case3(){
            var handTiles = List.of(S1, S2, S2, S2, S2, S3, S3, S3, S3, S4, S4, S4, S4);
            var result = HandTiles.arrange(handTiles, S1);
            assertEquals(
                    Set.of(
                            List.of(List.of(S1, S1), List.of(S2, S3, S4), List.of(S2, S3, S4), List.of(S2, S3, S4), List.of(S2, S3, S4)),
                            List.of(List.of(S1, S1), List.of(S2, S2, S2), List.of(S2, S3, S4), List.of(S3, S3, S3), List.of(S4, S4, S4)),
                            List.of(List.of(S4, S4), List.of(S1, S2, S3), List.of(S1, S2, S3), List.of(S2, S3, S4), List.of(S2, S3, S4))
                    ), result);
        }

        @Test
        void case4(){
            var handTiles = List.of(S1);
            var result = HandTiles.arrange(handTiles, S1);
            assertEquals(Set.of(List.of(List.of(S1, S1))), result);
        }

        @Test
        void case5(){
            var handTiles = List.of(S1, S2, S3, S3);
            var result = HandTiles.arrange(handTiles, S2);
            assertEquals(Set.of(), result);
        }

    }
}
