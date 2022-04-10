package jp.rouh.util.net;

/**
 * メッセージ通信の受信を行うリスナ。
 * @author Rouh
 * @version 1.0
 */
public interface MessageListener{

    /**
     * メッセージを受信した際の処理。
     * @param message メッセージ
     */
    void received(String message);

}
