package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.TileType;

import java.util.List;

/**
 * 面子形の手牌を構成する要素を表すインターフェース。
 * @author Rouh
 * @version 1.0
 */
interface HandComponent{

    /**
     * 要素を構成する牌をソートされたリスト形式で取得します。
     * @return 構成牌のリスト
     */
    List<Tile> getTilesSorted();

    /**
     * 指定された位置にある牌を取得します。
     * @param index 取得する牌の位置
     * @return 指定位置の牌
     * @throws IndexOutOfBoundsException 指定された位置が範囲外の場合
     */
    default Tile get(int index){
        return getTilesSorted().get(index);
    }

    /**
     * 構成牌の最初の牌を取得します。
     * @return 最初の牌
     */
    default Tile getFirst(){
        return get(0);
    }

    /**
     * 構成牌の最後の牌を取得します。
     * @return 最後の牌
     */
    default Tile getLast(){
        return get(getTilesSorted().size() - 1);
    }

    /**
     * 指定した牌がこの要素中に存在するか検査します。
     * <p>赤ドラかどうかは無視されます。
     * @param tile 対象牌
     * @return true 対象の牌が存在する場合
     *         false 対象の牌が存在しない場合
     */
    default boolean contains(Tile tile){
        return getTilesSorted().stream().anyMatch(tile::equalsIgnoreRed);
    }

    /**
     * この要素の牌の種類を取得します。
     * @return 牌の種類
     */
    default TileType getTileType(){
        return getFirst().tileType();
    }

    /**
     * この要素が么九牌を含むか検査します。
     * @return true 么九牌を含む場合
     *         false 么九牌を含まない場合
     */
    default boolean isTerminal(){
        return getFirst().isTerminal() || getLast().isTerminal();
    }

    /**
     * この要素が字牌で構成されているか検査します。
     * @return true 字牌で構成されている場合
     *         false 字牌で構成されていない場合
     */
    default boolean isHonor(){
        return getFirst().isHonor();
    }

    /**
     * この要素が老頭牌を含むか検査します。
     * @return true 老頭牌を含む場合
     *         false 老頭牌を含まない場合
     */
    default boolean isOrphan(){
        return isTerminal() || isHonor();
    }

    /**
     * この要素が三元牌を含むか検査します。
     * @return true 三元牌を含む場合
     *         false 三元牌を含まない場合
     */
    default boolean isDragon(){
        return getFirst().isDragon();
    }

    /**
     * この要素が風牌を含むか検査します。
     * @return true 風牌を含む場合
     *         false 風牌を含まない場合
     */
    default boolean isWind(){
        return getFirst().isWind();
    }
}
