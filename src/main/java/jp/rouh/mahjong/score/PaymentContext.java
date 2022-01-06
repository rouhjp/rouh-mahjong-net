package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

/**
 * 和了時の決済に必要な決済状況を表すインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface PaymentContext{

    /**
     * 和了者の自風を取得します。
     * @return 自風
     */
    Wind getSeatWind();

    /**
     * 和了者が親であるか検査します。
     * @return true 和了者が親の場合
     *         false 和了者が親でない場合
     */
    default boolean isDealer(){
        return getSeatWind()==Wind.EAST;
    }

    /**
     * 和了プレイヤーから見た放銃者の相対位置を取得します。
     * @return 相対位置
     */
    Side getWinningSide();

    /**
     * 和了プレイヤーの公開面子を取得します。
     * @return 公開面子のリスト
     */
    List<Meld> getOpenMelds();

    /**
     * 和了牌が嶺上牌(嶺上開花)の場合, 槓子の副露元の相対位置を取得します。
     * <p>暗槓もしくは加槓の場合は{@code Side.SELF}が返されます。
     * <p>和了牌が嶺上牌でない場合は{@code Side.SELF}が返されます。
     * @return 自摸牌が嶺上牌の場合の槓子の副露元
     */
    default Side getQuadSide(){
        if(isQuadTileDrawWin()){
            var openMelds = getOpenMelds();
            var lastMeld = openMelds.get(openMelds.size() - 1);
            return lastMeld.getDirectSourceSide();
        }
        return Side.SELF;
    }

    /**
     * ツモ和了かどうか検査します。
     * @return true 壁牌ツモ和了の場合
     *         true 嶺上牌ツモ和了の場合
     *         false 打牌ロン和了の場合
     *         false 槍槓ロン和了の場合
     */
    default boolean isTsumo(){
        return getWinningSide()==Side.SELF;
    }

    /**
     * 和了牌が嶺上牌(嶺上開花)かどうか検査します。
     * <p>この検査に適合する場合{@link #isTsumo()}は必ず適合します。
     * @return true 嶺上牌での和了の場合
     *         false 嶺上牌での和了でない場合
     */
    boolean isQuadTileDrawWin();

    /**
     * 場の供託およびリーチ棒の合計値を取得します。
     * @return 場の供託およびリーチ棒の合計(0..)
     */
    int getTotalDepositCount();

    /**
     * 本場数を取得します。
     * @return 本場数(0..)
     */
    int getRoundStreakCount();

}
