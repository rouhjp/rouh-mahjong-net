package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.TableStrategy;

/**
 * 対局に参加するプレイヤーの要件を規定するインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface TablePlayer{

    /**
     * プレイヤー名を取得します。
     * @return プレイヤー名
     */
    String getName();


    TableStrategy getStrategy();
}
