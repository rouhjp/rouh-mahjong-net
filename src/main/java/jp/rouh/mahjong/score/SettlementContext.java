package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;

/**
 * 和了時の決済に必要な決済状況を表すインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface SettlementContext{

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

    /**
     * 和了プレイヤーから見た放銃者の相対位置を取得します。
     * @return 相対位置
     */
    Side getWinningSide();
}
