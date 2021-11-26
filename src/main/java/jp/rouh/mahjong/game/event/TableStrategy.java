package jp.rouh.mahjong.game.event;

import java.util.List;

/**
 * 対局時の入力をプレイヤーへ求めるインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface TableStrategy {

    /**
     * 摸打中の行動の入力を求めます。
     * @param choices 選択可能な行動のリスト
     * @return 行動
     */
    TurnAction moveTurn(List<TurnAction> choices);

    /**
     * 他家の打牌時の行動の入力を求めます。
     * @param choices 選択可能な行動のリスト
     * @return 行動
     */
    CallAction moveCall(List<CallAction> choices);

}
