package jp.rouh.mahjong.game.event;

import java.util.List;

/**
 * 麻雀卓画面のインターフェース。
 * <p>ターン内及びターン外のプレイヤーの行動を{@link ActionInput}選択肢によって
 * ユーザに入力を求める処理を規定します。
 * @author Rouh
 * @version 1.0
 */
public interface TableView extends TableObserver{

    /**
     * 選択肢を表示し, ユーザの入力を受け付けます。
     * <p>プレイヤーが選択肢のうち一つを選択するまでスレッドをブロックして待機します。
     * @param choices 選択肢
     * @return プレイヤーの入力
     */
    ActionInput waitForInput(List<ActionInput> choices);

    /**
     * ユーザの確認完了の入力を受け付けます。
     * <p>プレイヤーが確認を終えるまでスレッドをブロックして待機します。
     */
    void waitForAcknowledge();

}
