package jp.rouh.mahjong.game;

/**
 * 対局の情報へアクセスするためのインターフェース。
 * @author Rouh
 * @version 1.0
 */
interface GameAccessor{

    /**
     * 対局開始時の持ち点を取得します。
     * @return 対局開始時の持ち点
     */
    int getDefaultScore();

    /**
     * 指定されたプレイヤーの順位を取得します。
     * <p>プレイヤーの順位はプレイヤーの持ち点で決定されます。
     * 同点のプレイヤーがいる場合は, 起家から見て上家側が優先的に順位付けされます。
     * @param player 対象プレイヤー
     * @return 対象プレイヤーの順位(1..4)
     */
    int getRankOf(GamePlayer player);
}
