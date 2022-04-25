package jp.rouh.mahjong.net;

/**
 * ルームメンバ情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class RoomMemberData{
    private String name;
    private boolean ready;

    /**
     * 名前を取得します。
     * @return 名前
     */
    public String getName(){
        return name;
    }

    /**
     * 名前を設定します。
     * @param name 名前
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * 準備完了かどうかを取得します。
     * @return 準備完了かどうか
     */
    public boolean isReady(){
        return ready;
    }

    /**
     * 準備完了かどうかを設定します。
     * @param ready 準備完了かどうか
     */
    public void setReady(boolean ready){
        this.ready = ready;
    }
}
