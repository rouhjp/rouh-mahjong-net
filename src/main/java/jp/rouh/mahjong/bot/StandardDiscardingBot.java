package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.util.FlexList;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 打牌ボットの実装。
 * @author Rouh
 * @version 1.0
 */
public enum StandardDiscardingBot implements DiscardingBot{

    /**
     * 数牌の中央部分を集めるように牌を選択する打牌ボット。
     */
    MIDDLE_SUIT_COLLECTOR{
        @Override
        public Tile select(List<Tile> allTiles, TileCounter counter){
            return allTiles.stream()
                    .max(Comparator.comparing(t->Math.abs(5 - t.suitNumber())))
                    .orElseThrow();
        }
    },

    /**
     * 手牌を立直にどれほど近いか評価し, 最も評価が高くなるように
     * 牌を選択する打牌ボット。
     */
    READY_SCORE_MAXIMIZER{
        @Override
        public Tile select(List<Tile> allTiles, TileCounter counter){
            return HandAnalyses.selectDiscardTileByHighestReadyScore(allTiles, counter);
        }

        @Override
        public Tile selectReady(List<Tile> allTiles, Set<Tile> readyTiles, TileCounter counter){
            var handTilesByReadyTile = readyTiles.stream()
                    .collect(Collectors.<Tile, Tile, List<Tile>>toMap(Function.identity(),
                            t->new FlexList<>(allTiles).removed(t)));
            return HandAnalyses.selectReadyTileByHighestWaitingTileCount(handTilesByReadyTile, counter);
        }
    }
}
