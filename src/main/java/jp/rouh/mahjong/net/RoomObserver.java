package jp.rouh.mahjong.net;

import jp.rouh.mahjong.game.event.TableStrategy;

import java.util.List;

/**
 * 麻雀ルームの状態変更通知先インターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface RoomObserver extends TableStrategy{

    /**
     * 部屋の状態が更新されたことを通知します。
     * @param members 更新後のメンバー情報
     */
    void roomUpdated(List<RoomMemberData> members);

    /**
     * ゲームが開始されたことを通知します。
     */
    void gameStarted();

}
