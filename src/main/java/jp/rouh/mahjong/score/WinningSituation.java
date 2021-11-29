package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.Collection;
import java.util.List;

/**
 * 和了時の付帯状況を表すクラス。
 * @param roundWind        場風
 * @param seatWind         和了者の自風
 * @param upperPrisedTiles 表ドラ牌のリスト(ドラ表示牌ではなく, ドラ自体)
 * @param lowerPrisedTiles 裏ドラ牌のリスト(ドラ表示牌ではなく, ドラ自体)
 * @param characteristics  特殊条件のリスト
 */
public record WinningSituation(Wind roundWind, Wind seatWind, List<Tile> upperPrisedTiles, List<Tile> lowerPrisedTiles,
                               Collection<WinningCharacteristics> characteristics){

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException 特殊条件のリストに矛盾がある場合
     */
    public WinningSituation{
        WinningCharacteristics.validate(characteristics);
    }
}
