package jp.rouh.util.net;

/**
 * メッセージ通信を行う通信のインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface MessageConnection{

    /**
     * 通信を受信するためのリスナを追加します。
     * @param listener リスナ
     */
    void addListener(MessageListener listener);

    /**
     * メッセージを送信します。
     * @param message メッセージ
     */
    void send(String message);

    /**
     * 通信を切断します。
     */
    void close();

}
