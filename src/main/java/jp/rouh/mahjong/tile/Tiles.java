package jp.rouh.mahjong.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static jp.rouh.mahjong.tile.Tile.*;

/**
 * 牌関連のユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
public final class Tiles{
    private Tiles(){
        throw new AssertionError("instantiate utility class");
    }

    private static final List<Tile> ORPHANS = Stream.of(Tile.values()).filter(Tile::isOrphan).toList();

    /**
     * 么九牌のリストを取得します。
     * @return 么九牌のリスト
     */
    public static List<Tile> orphans(){
        return ORPHANS;
    }

    /**
     * 与えられた牌に対して{@link Tile#equalsIgnoreRed}が適合する牌のリストを取得します。
     * <p>赤ドラ牌を持つ牌を与えた場合, その牌とその赤ドラ牌のリストが返されます。
     * <p>同様に赤ドラ牌を与えた場合, その牌とその非赤ドラ牌のリストが返されます。
     * <p>赤ドラ牌を持たない牌を与えた場合, その牌のみを含むリストが返されます。
     * @param tile 牌
     * @return 同種の牌のリスト
     */
    public static List<Tile> sameValuesOf(Tile tile){
        return switch(tile){
            case M5, M5R -> List.of(Tile.M5, M5R);
            case P5, P5R -> List.of(Tile.P5, P5R);
            case S5, S5R -> List.of(Tile.S5, Tile.S5R);
            default -> List.of(tile);
        };
    }

    /**
     * 与えられた牌に対応する赤ドラ牌が存在する場合, その赤ドラ牌を取得します。
     * <p>与えられた牌が赤ドラ牌, もしくは対応する赤ドラ牌がない牌である場合は
     * そのまま与えられた牌を返します。
     * @param tile 牌
     * @return 赤ドラ牌
     */
    public static Tile toPrisedRedIfExists(Tile tile){
        return switch(tile){
            case M5 -> M5R;
            case P5 -> P5R;
            case S5 -> S5R;
            default -> tile;
        };
    }

    private static Tile toNonPrisedRed(Tile tile){
        return switch(tile){
            case M5R -> Tile.M5;
            case P5R -> Tile.P5;
            case S5R -> Tile.S5;
            default -> tile;
        };
    }

    /**
     * 与えられた牌の順子を構成する上で周囲の牌をリスト形式で返却します。
     * 返却されるリストには, 必ず与えられた牌が含まれますが,
     * 与えられた牌が赤ドラ牌の場合は, 対応する通常牌が代わりに挿入されます。
     * 例えば, 赤五萬の周囲の牌は, [四萬, 五萬, 六萬] となります。
     * また, 九萬の周囲の牌は, [八萬, 九萬] となります。
     * また, 東の周囲の牌は, [東] となります。
     * @param tile 牌
     * @return 周囲の牌のリスト
     */
    public static List<Tile> aroundTilesOf(Tile tile){
        var list = new ArrayList<Tile>();
        if(tile.hasPrevious()){
            list.add(tile.previous());
        }
        list.add(toNonPrisedRed(tile));
        if(tile.hasNext()){
            list.add(tile.next());
        }
        return list;
    }

    /**
     * 与えられた牌と牌が対子・塔子を構成しうる近隣の牌であるか検査します。
     * 例えば, 一萬と一萬は塔子を構成し得るため検査に適合します。
     * 例えば, 一萬と三萬は塔子を構成し得るため検査に適合します。
     * 例えば, 一萬と四萬は対子・塔子いずれも構成し得ないため適合しません。
     * 例えば, 東と西は対子・塔子いずれも構成し得ないため適合しません。
     * @param a 1つ目の牌
     * @param b 2つ目の牌
     * @return true  近隣の牌である場合
     *         false 近隣の牌でない場合
     */
    public static boolean isNeighbour(Tile a, Tile b){
        if(a.isHonor() || b.isHonor()) return a.equals(b);
        return a.isSameTypeOf(b) &&
                Math.abs(a.tileNumber() - b.tileNumber())<3;
    }

    public static boolean isSequence(List<Tile> baseTiles, Tile claimedTile){
        var tiles = new ArrayList<>(baseTiles);
        tiles.add(claimedTile);
        return isSequence(tiles);
    }

    public static boolean isSequence(List<Tile> tiles){
        if(tiles.size()!=3) return false;
        var sorted = tiles.stream().sorted().toList();
        return sorted.get(1).isNextOf(sorted.get(0)) &&
                sorted.get(2).isNextOf(sorted.get(1));
    }

    public static boolean isTriple(List<Tile> baseTiles, Tile claimedTile){
        var tiles = new ArrayList<>(baseTiles);
        tiles.add(claimedTile);
        return isTriple(tiles);
    }

    public static boolean isTriple(List<Tile> tiles){
        if(tiles.size()!=3) return false;
        return tiles.get(1).equalsIgnoreRed(tiles.get(0)) &&
                tiles.get(2).equalsIgnoreRed(tiles.get(0));
    }

    public static boolean isQuad(List<Tile> baseTiles, Tile claimedTile){
        var tiles = new ArrayList<>(baseTiles);
        tiles.add(claimedTile);
        return isQuad(tiles);
    }

    public static boolean isQuad(List<Tile> tiles){
        if(tiles.size()!=4) return false;
        return tiles.get(1).equalsIgnoreRed(tiles.get(0)) &&
                tiles.get(2).equalsIgnoreRed(tiles.get(0)) &&
                tiles.get(3).equalsIgnoreRed(tiles.get(0));
    }

}
