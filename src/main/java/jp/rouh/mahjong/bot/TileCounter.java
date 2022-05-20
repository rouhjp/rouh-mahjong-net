package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.tile.Tile;

/**
 * 牌の残り枚数をカウントするインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface TileCounter{

    /**
     * 指定の牌と同種の牌が山牌に何枚残っているか取得します。
     * @param tile 対象牌
     * @return 枚数(0..4)
     */
    int count(Tile tile);

}
