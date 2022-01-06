package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

/**
 * 和了時のプレイヤーの情報へのアクセッサ・インターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface WinningPlayerAccessor{

    /**
     * 自風を取得します。
     * @return 自風
     */
    Wind getSeatWind();

    /**
     * 純手牌を取得します。
     * @return 純手牌
     */
    List<Tile> getHandTiles();

    /**
     * 公開面子を取得します。
     * @return 公開面子のリスト
     */
    List<Meld> getOpenMelds();

    /**
     * 副露(暗槓を除く)を行った回数を取得します。
     * @return 副露(暗槓を除く)を行った回数(0..4)
     */
    int getCallCount();

    /**
     * 立直状態かどうか検査します。
     * @return true 立直状態の場合
     *         false 立直状態でない場合
     */
    boolean isReady();

    /**
     * ダブル立直状態かどうか検査します。
     * <p>ダブル立直とは, 鳴きを挟まない局の第一巡での立直宣言を指し,
     * ダブル立直を行った場合は, その局の終了までダブル立直状態としてこの検査に適合する状態となります。
     * <p>この検査に適合する場合, 常に立直状態の検査{@link #isReady}に適合します。
     * @return true ダブル立直状態の場合
     *         false ダブル立直状態でない場合
     */
    boolean isFirstAroundReady();

    /**
     * 立直後第一巡であるかどうか検査します。
     * <p>立直後第一巡とは, 立直宣言打牌が完了してから,
     * 他家による副露もしくはカン宣言, 自家によるカン宣言, 次の自家の打牌の
     * いずれかが発生するまでの間を指します。
     * <p>この検査に適合する場合, 常に立直状態の検査{@link #isReady}に適合します。
     * @return true 立直後第一巡である場合
     *         false 立直後第一巡でない場合
     */
    boolean isReadyAround();

}
