package jp.rouh.mahjong.score;

import java.util.NoSuchElementException;

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
    String getName();

    /**
     * 役の固有名を取得します。
     * <p>基本的に{@link #getName}と同じです。
     * <p>ただし食い下がりの発生する役は, 門前と食い下がりの役を
     * 区別するために, 門前の場合は先頭に「門前」を付けた役名を
     * 取得します。
     * @return 役名
     */
    default String getUniqueName(){
        return getName();
    }

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
