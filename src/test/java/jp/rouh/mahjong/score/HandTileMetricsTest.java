package jp.rouh.mahjong.score;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static jp.rouh.mahjong.score.HandTileMetrics.*;
import static jp.rouh.mahjong.tile.Tile.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandTileMetricsTest {

    @Nested
    class TestWinningCandidatesOf{

        @Test
        void testSingleHeadWait(){
            var handTiles = List.of(M1, M1, M1, WE, WE, WE, DW);
            var result = winningCandidatesOf(handTiles);
            assertTrue(result.contains(DW));
            assertEquals(Set.of(DW), result);
        }

        @Test
        void testEtherHeadWait(){
            var handTiles = List.of(M1, M2, M3, S4, S4, WE, WE);
            var result = winningCandidatesOf(handTiles);
            assertTrue(result.containsAll(List.of(S4, WE)));
            assertEquals(Set.of(S3, S4, S5, S5R, WE), result);
        }
    }
}
