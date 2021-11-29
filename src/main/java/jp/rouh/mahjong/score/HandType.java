package jp.rouh.mahjong.score;

/**
 * 役を表すインターフェース。
 * <p>役は得点の方法から以下の三種類に大別されます。
 * <ul>
 *   <li>通常役 ... 符と飜数で得点を計算します。</li>
 *   <li>役満役 ... 一倍や二倍など倍数で得点を計算します。</li>
 *   <li>流し満貫 ... 満貫固定となります。</li>
 * </ul>
 * @author Rouh
 * @version 1.0
 */
public interface HandType{

    /**
     * 役の名称を取得します。
     * @return 役名
     */
    String getText();

    /**
     * 役の等級を取得します。
     * @return 役の等級
     */
    HandTypeGrade getGrade();

    /**
     * この手が役満手かどうか検査します。
     * @return true 役満手の場合
     *         false 役満手でない場合
     */
    default boolean isLimit(){
        return getGrade().isLimit();
    }
}
