package jp.rouh.mahjong.score;

/**
 * 役インターフェース。
 *
 * <p>役は通常役、役満役、満貫役(流し満願)の三種類があります。
 * @author Rouh
 * @version 2.0
 */
public interface HandType{

    /**
     * 役の名称を取得します。
     * @return 役名
     */
    String getName();

    /**
     * 役が役満もしくは流し満願かどうか検査します。
     * @return true 役満の場合
     *         true 流し満貫の場合
     *         false 通常役の場合
     */
    boolean isLimit();

    /**
     * 飜数を取得します。
     *
     * <p>役満の場合は0を返します。
     * @return 飜数 通常役の場合
     *         0 役満の場合
     */
    int getDoubles();

    /**
     * 役満の倍数を取得します。
     *
     * <p>通常役の場合は0を返します。
     * @return 0 通常役の場合
     *         1 シングル役満の場合
     *         2 ダブル役満の場合
     */
    int getLimitMultiplier();


    default String getGradeCode(){
        if(isLimit()){
            return "";
        }
        return Integer.toString(getDoubles());
    }

}
