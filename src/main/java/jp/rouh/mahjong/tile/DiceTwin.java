package jp.rouh.mahjong.tile;

/**
 * 2個ワンセットのサイコロ1組を表すクラス。
 * @param firstValue  一つ目のサイコロの目
 * @param secondValue 二つ目のサイコロの目
 * @author Rouh
 * @version 1.0
 */
public record DiceTwin(int firstValue, int secondValue) {

    /**
     * サイコロの目の合計値を取得します。
     * @return サイコロの目の合計値(2..12)
     */
    public int sumValue() {
        return firstValue + secondValue;
    }

    /**
     * ランダムな値の2個のサイコロ1組を新規に生成します。
     * @return サイコロ1組
     */
    public static DiceTwin newDices() {
        return new DiceTwin(Dices.rollDice(), Dices.rollDice());
    }
}
