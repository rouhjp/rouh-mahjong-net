package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.Wind;

/**
 * ゲーム(対局)の情報へアクセスするためのインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface GameAccessor{

    /**
     * 指定された席順のプレイヤーのランクを取得します。
     * @param orderWind 席順
     * @return ランク(1..4)
     */
    int getRankOf(Wind orderWind);

    /**
     * 指定された席順のプレイヤーの東一局の自風を取得します。
     * @param orderWind 席順
     * @return 東一局の自風
     */
    Wind getInitialSeatWindAt(Wind orderWind);

}
