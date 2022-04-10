package jp.rouh.util.net;

/**
 * メッセージ通信サーバを監視するリスナ。
 */
public interface MessageServerListener{

    /**
     * ユーザがサーバに接続した際の処理。
     * @param userId 接続ユーザのID
     * @param connection コネクション
     */
    void userConnected(String userId, MessageConnection connection);

    /**
     * ユーザがこのサーバに接続した際の処理。
     * @param userId 接続ユーザのID
     */
    void userDisconnected(String userId);

}
