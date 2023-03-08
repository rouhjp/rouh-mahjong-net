package jp.rouh.mahjong.game;

/**
 * 牌山生成インターフェース。
 * @author Rouh
 * @version 1.0
 */
@FunctionalInterface
interface WallGenerator{

    /**
     * 牌山を生成します。
     *
     * @param dice1 一つ目のサイコロの値
     * @param dice2 二つ目のサイコロの値
     * @return 牌山
     */
    Wall generate(int dice1, int dice2);

}
