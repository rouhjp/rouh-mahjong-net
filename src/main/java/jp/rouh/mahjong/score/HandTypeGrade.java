package jp.rouh.mahjong.score;

/**
 * 役の等級を表すクラス。
 * @author Rouh
 * @version 1.0
 */
public enum HandTypeGrade{

    /**
     * 定義外
     * <p>流し満貫など
     */
    UNDEFINED(""),

    /**
     * 一飜
     */
    DOUBLES_1("1"),

    /**
     * 両飜
     */
    DOUBLES_2("2"),

    /**
     * 三飜
     */
    DOUBLES_3("3"),

    /**
     * 四飜
     */
    DOUBLES_4("4"),

    /**
     * 五飜
     */
    DOUBLES_5("5"),

    /**
     * 六飜
     */
    DOUBLES_6("6"),

    /**
     * シングル役満
     */
    LIMIT_SINGLE("S"),

    /**
     * ダブル役満
     */
    LIMIT_DOUBLE("W");

    private final String code;
    HandTypeGrade(String code){
        this.code = code;
    }

    /**
     * この等級が役満の等級かどうか検査します。
     * @return true 役満の場合
     *         false 役満でない場合
     */
    boolean isLimit(){
        return this==LIMIT_SINGLE || this==LIMIT_DOUBLE;
    }

    /**
     * この役の等級を一文字のコードで文字列化します。
     * <p>通常役であれば飜数を, シングル役満であればS, ダブル役満であればW,
     * 定義外であればスペースを返します。
     * @return コード
     */
    public String getCode(){
        return code;
    }

    /**
     * 飜数に対応する役の等級を取得します。
     * @param doubles 飜数
     * @return 等級
     */
    static HandTypeGrade ofDoubles(int doubles){
        return switch(doubles){
            case 1 -> DOUBLES_1;
            case 2 -> DOUBLES_2;
            case 3 -> DOUBLES_3;
            case 4 -> DOUBLES_4;
            case 5 -> DOUBLES_5;
            case 6 -> DOUBLES_6;
            default -> throw new AssertionError("undefined hand type grade: doubles=" + doubles);
        };
    }

    /**
     * 倍数に対応する役満の等級を取得します。
     * @param multiplier 倍数
     * @return 等級
     */
    static HandTypeGrade ofLimit(int multiplier){
        return switch(multiplier){
            case 1 -> LIMIT_SINGLE;
            case 2 -> LIMIT_DOUBLE;
            default -> throw new AssertionError("undefined hand type grade: multiplier=" + multiplier);
        };
    }
}
