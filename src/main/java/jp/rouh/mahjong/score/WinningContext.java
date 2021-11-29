package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

/**
 * 点数計算に必要な和了状況を取得するインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface WinningContext{

    /**
     * 場風を取得します。
     * @return 場風
     */
    Wind getRoundWind();

    /**
     * 和了者の自風を取得します。
     * @return 自風
     */
    Wind getSeatWind();

    /**
     * 和了プレイヤーが親かどうか検査します。
     * @return true 和了プレイヤーが親の場合
     *         false 和了プレイヤーが子の場合
     */
    default boolean isDealer(){
        return getSeatWind()==Wind.EAST;
    }

    /**
     * ツモ和了かどうか検査します。
     * @return true 壁牌ツモ和了の場合
     *         true 嶺上牌ツモ和了の場合
     *         false 打牌ロン和了の場合
     *         false 槍槓ロン和了の場合
     */
    boolean isTsumo();

    /**
     * 門前和了かどうか検査します。
     * <p>ロン和了であっても副露(暗槓を含まない)がなければ門前となります。
     * @return true 門前の場合
     *         false 門前でない場合
     */
    boolean isSelfMade();

    /**
     * 和了プレイヤーが立直していたかどうか検査します。
     * @return true 立直の場合
     *         false 立直でない場合
     */
    boolean isReady();

    /**
     * 和了プレイヤーが鳴きを挟まない一巡目で立直(両立直)していたかどうか検査します。
     * <p>この検査に適合する場合{@link #isReady()}は必ず適合します。
     * @return true 鳴きを挟まない一巡目での立直の場合
     *         false 鳴きを挟まない一巡目での立直でない場合
     */
    boolean isFirstAroundReady();

    /**
     * この和了が鳴きを挟まない一巡目の和了(天和/地和)であるか検査します。
     * @return true 鳴きを挟まない一巡目の場合
     *         false 鳴きを挟まない一巡目でない場合
     */
    boolean isFirstAroundWin();

    /**
     * この和了が鳴きを挟まない立直後一巡目かどうか検査します。
     * <p>この検査に適合する場合{@link #isTsumo()}は必ず適合します。
     * @return true 鳴きを挟まない立直後一巡目の場合
     *         false 鳴きを挟まない立直後一巡目の場合
     */
    boolean isReadyAroundWin();

    /**
     * 和了牌が河底牌(河底撈魚)かどうか検査します。
     * <p>この検査に適合する場合{@link #isTsumo()}は必ず不適合になります。
     * @return true 河底牌での和了の場合
     *         false 河底牌での和了でない場合
     */
    boolean isLastTileGrabWin();

    /**
     * 和了牌が海底牌(海底摸月)かどうか検査します。
     * <p>海底牌は壁牌の最終ツモ牌であり, 嶺上牌は海底牌にはなり得ません。
     * このため最終摸打でのツモが必ずしも海底牌でのツモになるとは限りません。
     * <p>この検査に適合する場合{@link #isTsumo()}は必ず適合します。
     * @return true 海底牌での和了の場合
     *         false 海底牌での和了でない場合
     */
    boolean isLastTileDrawWin();

    /**
     * 和了牌が他家の槓子構成牌(槍槓)かどうか検査します。
     * <p>この検査に適合する場合{@link #isTsumo()}は必ず不適合になります。
     * @return true 他家の槓子構成牌での和了の場合
     *         false 他家の槓子構成牌での和了でない場合
     */
    boolean isQuadTileGrabWin();

    /**
     * 和了牌が嶺上牌(嶺上開花)かどうか検査します。
     * <p>この検査に適合する場合{@link #isTsumo()}は必ず適合します。
     * @return true 嶺上牌での和了の場合
     *         false 嶺上牌での和了でない場合
     */
    boolean isQuadTileDrawWin();

    /**
     * ドラのリストを取得します。
     * 裏ドラを含みません。裏ドラは{@link #getLowerPrisedTiles}にて取得します。
     * ドラ表示牌ではなく, ドラ自体であることに注意が必要です。
     * @return ドラのリスト
     */
    List<Tile> getUpperPrisedTiles();

    /**
     * 裏ドラのリストを取得します。
     * ドラ表示牌ではなく, ドラ自体であることに注意が必要です。
     * @return 裏ドラのリスト
     */
    List<Tile> getLowerPrisedTiles();

}
