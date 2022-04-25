package jp.rouh.mahjong.net;

/**
 * 麻雀ルーム。
 * <p>クライアントがサーバへ呼び出すメソッドを規定します。
 * @author Rouh
 * @version 1.0
 */
public interface Room{

    /**
     * 自身の表示名を更新します。
     * @param name 名前
     */
    void notifyName(String name);

    /**
     * 自身の準備状態を更新します。
     * @param ready 準備状態
     */
    void notifyReady(boolean ready);

    /**
     * ゲーム開始を要求します。
     */
    void start();

}
