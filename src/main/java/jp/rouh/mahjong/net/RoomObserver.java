package jp.rouh.mahjong.net;

import jp.rouh.mahjong.game.event.TableStrategy;

import java.util.List;

public interface RoomObserver extends TableStrategy{

    /**
     * 部屋の状態が更新されたことを通知します。
     * @param members 更新後のメンバー情報
     */
    void roomUpdated(List<RoomMemberData> members);


    void gameStarted();

}
