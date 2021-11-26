package jp.rouh.mahjong.tile;

/**
 * ある方角{@link Wind}に対する相対方位を表すクラス。
 * @author Rouh
 * @version 1.0
 * @see Wind
 */
public enum Side {

    /**
     * 自家
     */
    SELF,

    /**
     * 下家
     */
    RIGHT,

    /**
     * 対面
     */
    ACROSS,

    /**
     * 上家
     */
    LEFT;

    private static final Side[] VALUES = values();

    /**
     * 引数で与えられた方角から, この相対方位に位置する方角を返します。
     *
     * <p>例えば次のような等式が成り立ちます。
     * {@code RIGHT.of(Wind.EAST)==Wind.SOUTH }
     * @param reference 基準の方角
     * @return 基準の方角からみて相対方位にある方角
     * @see Wind
     */
    public Wind of(Wind reference) {
        return reference.shift(ordinal());
    }

    /**
     * 相対方位から, 引数で与えられた相対方位にある相対方位を返します。
     *
     * <p>例えば次のような等式が成り立ちます。
     * {@code RIGHT.of(RIGHT)==ACROSS }
     * @param side 合成する相対方位
     * @return 合成結果
     */
    public Side of(Side side) {
        return VALUES[(ordinal() + side.ordinal()) % 4];
    }

    /**
     * この相対方位が示す相手から見た自分の相対方位を返します。
     * @return 反転した相対方位
     */
    public Side fromOpposite() {
        return switch (this) {
            case SELF -> SELF;
            case RIGHT -> LEFT;
            case ACROSS -> ACROSS;
            case LEFT -> RIGHT;
        };
    }

    /**
     * 2つのサイコロが示す相対方位を返します。
     * @param d1 1つ目のサイコロの目の値(1..6)
     * @param d2 2つ目のサイコロの目の値(1..6)
     * @return RIGHT  サイコロの目の合計が 2, 6, 10 のとき
     *         ACROSS サイコロの眼の合計が 3, 7, 11 のとき
     *         LEFT   サイコロの眼の合計が 4, 8, 12 のとき
     *         SELF   サイコロの眼の合計が 5, 9 のとき
     * @throws IllegalArgumentException 与えられたサイコロの目が1~6の範囲外の場合
     */
    public static Side of(int d1, int d2) {
        if (d1 <= 0 || d2 <= 0 || d1 > 6 || d2 > 6)
            throw new IllegalArgumentException("invalid dice value: " + d1 + ", " + d2);
        return VALUES[(d1 + d2 - 1) % 4];
    }

    /**
     * 2つのサイコロが示す相対方位を返します。
     * @param diceSum 2つのサイコロの目の合計値(2..12)
     * @return RIGHT  サイコロの目の合計が 2, 6, 10 のとき
     *         ACROSS サイコロの眼の合計が 3, 7, 11 のとき
     *         LEFT   サイコロの眼の合計が 4, 8, 12 のとき
     *         SELF   サイコロの眼の合計が 5, 9 のとき
     * @throws IllegalArgumentException 与えられたサイコロの目の合計が2~12の範囲外の場合
     */
    public static Side of(int diceSum) {
        if (diceSum < 2 || diceSum > 12)
            throw new IllegalArgumentException("invalid dice value: " + diceSum);
        return VALUES[(diceSum - 1) % 4];
    }

    /**
     * 基準の方角から見た対象の方角の相対方位を取得します。
     *
     * <p>例えば以下の等式が成り立ちます。
     * {@code Side.of(EAST, SOUTH)==Side.LEFT }
     * @param target    対象の方角
     * @param reference 基準の方角
     * @return 基準の方角からみたこの方角の相対方位
     */
    static Side of(Wind target, Wind reference) {
        return VALUES[(4 + target.ordinal() - reference.ordinal()) % 4];
    }
}
