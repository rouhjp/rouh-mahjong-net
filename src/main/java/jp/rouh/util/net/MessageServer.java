package jp.rouh.util.net;

import java.io.IOException;
import java.io.Serializable;

/**
 * オブジェクト通信サーバ。
 * @author Rouh
 * @version 1.0
 */
public interface MessageServer{

    /**
     * サーバを監視するリスナを追加します。
     * @param listener リスナ
     */
    void addListener(MessageServerListener listener);

    /**
     * サーバを開始します。
     * @param port ポート番号
     * @throws IOException サーバの開始に失敗した場合
     */
    void start(int port) throws IOException;

    /**
     * サーバを終了します。
     */
    void close();

}
