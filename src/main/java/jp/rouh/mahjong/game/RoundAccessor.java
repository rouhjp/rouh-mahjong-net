package jp.rouh.mahjong.game;

import jp.rouh.mahjong.score.WinningRoundAccessor;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

/**
 * 局への参照を規定するインターフェース。
 *
 * @author Rouh
 * @version 1.0
 */
interface RoundAccessor extends WinningRoundAccessor{

    /**
     * イベント通知先を管理するオブジェクトへの参照を取得します。
     * @return イベント通知先
     */
    TableMaster getMaster();

    /**
     * 場風を取得します。
     * @return 場風
     */
    @Override
    Wind getRoundWind();

    /**
     * 局数を取得します。
     * @return 局数(1..4)
     */
    int getRoundCount();

    /**
     * 本場数を取得します。
     * @return 本場数(0..)
     */
    @Override
    int getRoundStreakCount();

    /**
     * 供託数を取得します。
     * @return 供託数(0..)
     */
    int getRoundDepositCount();

    /**
     * 立直の数を取得します。
     * @return 立直数(0..4)
     */
    int getReadyCount();

    /**
     * 局の供託とリーチの数の合計を取得します。
     * <p>和了プレイヤーはこの数*1000点を得ます。
     * @return 供託(その局の立直数を含む)
     */
    @Override
    default int getTotalDepositCount(){
        return getRoundDepositCount() + getReadyCount();
    }

    /**
     * 槓子の数を取得します。
     * @return 槓子数(0..5)
     */
    int getTotalQuadCount();

    /**
     * 局が一巡目か検査します。
     * <p>副露または槓子が発生した場合は検査に適合しません。
     * @return true  一巡目の場合
     *         false 二巡目以降の場合
     */
    @Override
    boolean isFirstAround();

    /**
     * 局が最終巡か検査します。
     * <p>最終巡とは残りツモ可能枚数が4未満の時を指します。
     * <p>最終巡では立直はできません。
     * @return true  最終巡の場合
     *         false 最終巡でない場合
     */
    boolean isLastAround();

    /**
     * 局の最終摸打か検査します。
     * <p>最終摸打とは残りツモ可能枚数が0の時を指します。
     * <p>最終摸打ではカンの宣言はできません。
     * 最終摸打から1つ前の摸打でカンが発生した場合, その摸打は最終摸打となります。
     * <p>最終摸打で, かつ嶺上ツモでないツモ牌(海底牌)で和了した場合, 海底摸月が付与されます。
     * <p>最終摸打の捨て牌は副露できません。
     * <p>最終摸打の捨て牌でロンが発生した場合は河底撈魚が付与されます。
     * @return true  最終摸打の場合
     *         false 最終摸打でない場合
     */
    @Override
    boolean isLastTurn();

    /**
     * ドラ表示牌をリスト形式で取得します。
     * @return ドラ表示牌のリスト
     */
    @Override
    List<Tile> getUpperIndicators();

    /**
     * 裏ドラ表示牌をリスト形式で取得します。
     * <p>裏ドラ表示牌が公開されていない場合は空のリストを取得します。
     * @return 裏ドラ表示牌のリスト
     */
    @Override
    List<Tile> getLowerIndicators();

}
