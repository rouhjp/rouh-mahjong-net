package jp.rouh.mahjong.game.event;

import java.util.List;

/**
 * 対局時の入力をプレイヤーへ求めるインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface TableStrategy extends TableObserver{

    /**
     * 摸打中の行動の入力を求めます。
     * @param choices 選択可能な行動のリスト
     * @return 行動
     */
    TurnAction selectTurnAction(List<TurnAction> choices);

    /**
     * 他家の打牌時の行動の入力を求めます。
     * @param choices 選択可能な行動のリスト
     * @return 行動
     */
    CallAction selectCallAction(List<CallAction> choices);

    /**
     * 次に進むことへの了承を求めます。
     * <p>処理の終了を以て了承とします。
     */
    default void acknowledge(){
        //pass
    }
}
