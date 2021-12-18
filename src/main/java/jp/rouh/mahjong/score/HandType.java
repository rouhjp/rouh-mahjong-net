package jp.rouh.mahjong.score;

import java.util.List;
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

    /**
     * 役の固有名をもとに役のインスタンスを検索し, 取得します。
     * @param uniqueName 固有名
     * @return 役
     */
    static HandType forName(String uniqueName){
        for(var handType:LimitHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:EnvironmentBasedHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:TileBasedHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:MeldBasedHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:IrregularFormatHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:FixedScoreHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        if(uniqueName.equals("ドラ")){
            return PrisedTileHandTypes.of(1);
        }
        if(uniqueName.matches("ドラ[0-9]+")){
            int count = Integer.parseInt(uniqueName.substring(2));
            return PrisedTileHandTypes.of(count);
        }
        if(uniqueName.equals("裏ドラ")){
            return PrisedTileHandTypes.ofHidden(1);
        }
        if(uniqueName.matches("裏ドラ[0-9]+")){
            int count = Integer.parseInt(uniqueName.substring(3));
            return PrisedTileHandTypes.ofHidden(count);
        }
        if(uniqueName.equals("赤ドラ")){
            return PrisedTileHandTypes.ofRed(1);
        }
        if(uniqueName.matches("赤ドラ[0-9]+")){
            int count = Integer.parseInt(uniqueName.substring(3));
            return PrisedTileHandTypes.ofRed(count);
        }
        throw new NoSuchElementException("hand type not found: "+uniqueName);
    }
}
