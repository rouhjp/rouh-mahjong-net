package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.TableStrategy;
import jp.rouh.mahjong.tile.Wind;

/**
 * 対局プレイヤーの情報にアクセスするためのインターフェース。
 * @author Rouh
 * @version 1.0
 */
interface GamePlayerAccessor extends TableStrategy{

    /**
     * ゲームプレイヤーの名前を取得します。
     * @return 名前
     */
    String getName();

    /**
     * 与えられた局数の局に参加している場合の自風を取得します。
     * @param roundCount 局数(1..4)
     * @return 自風
     */
    Wind getSeatWindAt(int roundCount);

    /**
     * ゲームプレイヤーの持ち点を取得します。
     * @return 点数
     */
    int getScore();

    /**
     * ゲームプレイヤーのランクを取得します。
     * <p>ランクは点数が高いプレイヤーから1位～4位となり,
     * 同点の場合はゲーム開始時の自摸巡が早いほうが上位となります。
     * @return ランク
     */
    int getRank();

    /**
     * ゲームプレイヤーに点数を設定します。
     * <p>受け取りであれば正の数を, 支払いであれば負の数を指定します。
     * @param score 点数
     */
    void applyScore(int score);

}
