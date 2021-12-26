package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

/**
 * 山の状態を通知するインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface WallObserver{

    /**
     * 指定された位置の牌が自摸されたことを通知します。
     * @param wind 牌の方向
     * @param column 牌の左からの位置(0..17)
     * @param floor 牌の段数(0..上段, 1..下段)
     */
    void tileTaken(Wind wind, int column, int floor);

    /**
     * 指定された位置の牌がめくられたことを通知します。
     * @param wind 牌の方向
     * @param column 牌の左からの位置(0..17)
     * @param tile めくられた牌
     */
    void tileRevealed(Wind wind, int column, Tile tile);
}
