package jp.rouh.mahjong.game.event;

/**
 * 対局に参加するプレイヤーの要件を規定するインターフェース。
 * <p>対局中の情報を受け取る{@link TableObserver}インターフェースと,
 * 対局中の入力を受け付ける{@link TableStrategy}インターフェースを
 * 統合したインターフェースです。
 *
 * @author Rouh
 * @version 1.0
 */
public interface TablePlayer extends TableObserver, TableStrategy{
}
