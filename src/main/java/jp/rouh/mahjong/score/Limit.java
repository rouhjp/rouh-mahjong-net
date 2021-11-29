package jp.rouh.mahjong.score;

/**
 * 点数区分クラス。
 * @author Rouh
 * @version 1.0
 */
public enum Limit{

    /**
     * ナシ
     */
    EMPTY("", false),

    /**
     * 満貫
     */
    JACKPOT("満貫", false),

    /**
     * 跳満
     */
    GREATER_JACKPOT("跳満", false),

    /**
     * 倍満
     */
    DOUBLE_JACKPOT("倍満", false),

    /**
     * 三倍満
     */
    TRIPLE_JACKPOT("三倍満", false),

    /**
     * 数え役満
     */
    COUNT_LIMIT("役満", false),

    /**
     * 役満
     */
    HAND_LIMIT("役満", true),

    /**
     * 二倍役満
     */
    DOUBLE_HAND_LIMIT("二倍役満", true),

    /**
     * 三倍役満
     */
    TRIPLE_HAND_LIMIT("三倍役満", true),

    /**
     * 四倍役満
     */
    QUADRUPLE_HAND_LIMIT("四倍役満", true),

    /**
     * 五倍役満
     */
    QUINTUPLE_HAND_LIMIT("五倍役満", true),

    /**
     * 六倍役満
     */
    SEXTUPLE_HAND_LIMIT("六倍役満", true),

    /**
     * 七倍役満
     */
    SEPTUPLE_HAND_LIMIT("七倍役満", true),

    /**
     * 八倍役満
     */
    OCTUPLE_HAND_LIMIT("八倍役満", true);

    private final String text;
    private final boolean handLimit;

    Limit(String text, boolean handLimit){
        this.text = text;
        this.handLimit = handLimit;
    }

    /**
     * 点数区分名を文字列で返します。
     * @return 点数区分名
     */
    public String getText(){
        return text;
    }

    /**
     * 役満(数え役満を除く)かどうか検査します。
     * @return true 役満の場合
     *         false 役満でない場合
     */
    public boolean isHandLimit(){
        return handLimit;
    }

    /**
     * 点数区分無しかどうか検査します。
     * @return true 点数区分無しの場合
     *         false 点数区分が存在する場合
     */
    public boolean isEmpty(){
        return this==EMPTY;
    }

    /**
     * 指定した符と飜数に対応する点数区分を取得します。
     * @param point   符
     * @param doubles 飜
     * @return 点数区分
     */
    static Limit of(int point, int doubles){
        if(doubles>=13) return Limit.COUNT_LIMIT;
        if(doubles>=11) return Limit.TRIPLE_JACKPOT;
        if(doubles>=8) return Limit.DOUBLE_JACKPOT;
        if(doubles>=6) return Limit.GREATER_JACKPOT;
        if(doubles>=5) return Limit.JACKPOT;
        if(doubles>=4 && point>=40) return Limit.JACKPOT;
        if(doubles>=3 && point>=70) return Limit.JACKPOT;
        return Limit.EMPTY;
    }

    /**
     * 指定した倍数に対応する役満点数区分を取得します。
     * @param limitMultiplier 倍数
     * @return 点数区分
     * @throws IllegalArgumentException 倍数が0以下または9以上の場合
     */
    static Limit ofMultiplier(int limitMultiplier){
        return switch(limitMultiplier){
            case 1 -> HAND_LIMIT;
            case 2 -> DOUBLE_HAND_LIMIT;
            case 3 -> TRIPLE_HAND_LIMIT;
            case 4 -> QUADRUPLE_HAND_LIMIT;
            case 5 -> QUINTUPLE_HAND_LIMIT;
            case 6 -> SEXTUPLE_HAND_LIMIT;
            case 7 -> SEPTUPLE_HAND_LIMIT;
            case 8 -> OCTUPLE_HAND_LIMIT;
            default -> throw new IllegalArgumentException("invalid limit number: " + limitMultiplier);
        };
    }
}
