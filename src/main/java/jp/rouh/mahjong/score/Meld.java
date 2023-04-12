package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;
import jp.rouh.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 面子クラス。
 * <p>副露や暗槓によって作成される公開面子の表現, および
 * 役判定のために手牌を並べ替えて作成される門前面子の表現に用います。
 * @author Rouh
 * @version 1.0
 */
public class Meld implements HandComponent{
    private final List<Tile> sorted;
    private final List<Tile> base;
    private final Tile claimedNullable;
    private final Tile addedNullable;
    private final Side source;

    /**
     * 面子を作成します。
     * @param base 手牌から提供された構成牌
     */
    private Meld(List<Tile> base){
        this.base = base.stream().sorted().toList();
        this.claimedNullable = null;
        this.addedNullable = null;
        this.source = Side.SELF;
        this.sorted = this.base;
    }

    /**
     * 面子を作成します。
     * @param base 手牌から提供された構成牌
     * @param claimed 他家からの副露牌
     * @param source 副露元の相対方位
     */
    private Meld(List<Tile> base, Tile claimed, Side source){
        this.base = base.stream().sorted().toList();
        this.claimedNullable = claimed;
        this.addedNullable = null;
        this.source = source;
        this.sorted = Lists.added(base, claimed)
                .stream().sorted().toList();
    }

    /**
     * 面子を作成します。
     * @param base 手牌から提供された構成牌
     * @param claimed 他家からの副露牌
     * @param added 加槓宣言牌
     * @param source 副露元の相対方位
     */
    private Meld(List<Tile> base, Tile claimed, Tile added, Side source){
        this.base = base.stream().sorted().toList();
        this.claimedNullable = claimed;
        this.addedNullable = added;
        this.source = source;
        this.sorted = Lists.added(Lists.added(base, claimed), added)
                .stream().sorted().toList();
    }

    /**
     * 面子を構成する牌をグラフィック上の並びで取得します。
     * <p>このメソッドはビューの実装用です。
     * <p>例えば, 手牌中の[2, 4]に対して[3]をチーして成立した順子に対し
     * このメソッドを呼び出した場合, 返されるリストは[3, 2, 4]の順になります。
     * また, 加槓された牌は最後に追加されます。
     * @return 面子構成牌のリスト
     */
    public List<Tile> getTilesFormed(){
        if(isHandMeld()){
            return sorted;
        }
        if(isSelfQuad()){
            var formed = new ArrayList<>(base);
            Collections.swap(formed, 2, 3);
            return formed;
        }
        var formed = new ArrayList<>(base);
        switch(source){
            case LEFT -> formed.add(0, claimedNullable);
            case ACROSS -> formed.add(1, claimedNullable);
            case RIGHT -> formed.add(claimedNullable);
        }
        if(isAddQuad()){
            formed.add(addedNullable);
        }
        return formed;
    }

    /**
     * {@inheritDoc}
     * @return 面子の構成牌のリスト
     */
    @Override
    public List<Tile> getTilesSorted(){
        return sorted;
    }

    /**
     * 面子から三枚の構成牌を取得します。
     * <p>槓子の場合, 構成牌の一枚を無視した三枚の構成牌を返します。
     * @return 長さ3の面子構成牌のリスト
     */
    public List<Tile> getTilesTruncated(){
        return sorted.subList(0, 3);
    }

    /**
     * 副露元の相対方位を返します。
     * <p>副露されたものでない場合, {@code Side.SELF}を返します。
     * <p>暗槓の場合は, {@code Side.SELF}を返します。
     * <p>加槓の場合は, 元の明刻の副露元の相対方位を返します。
     * @return 副露元の相対方位
     */
    public Side getSourceSide(){
        return source;
    }

    /**
     * 副露元の相対方位を返します。
     * <p>副露されたものでない場合, {@code Side.SELF}を返します。
     * <p>暗槓もしくは加槓の場合は, {@code Side.SELF}を返します。
     * @return 副露元の相対位置
     */
    public Side getDirectSourceSide(){
        if(isAddQuad()){
            return Side.SELF;
        }
        return source;
    }

    /**
     * この面子が順子であるか検査します。
     * @return true 順子の場合
     *         false 順子でない場合
     */
    public boolean isStraight(){
        return !base.get(0).equalsIgnoreRed(base.get(1));
    }

    /**
     * この面子が刻子であるか検査します。
     * @return true 刻子の場合
     *         false 刻子でない場合
     */
    public boolean isTriple(){
        return base.get(0).equalsIgnoreRed(base.get(1)) && sorted.size()==3;
    }

    /**
     * この面子が槓子であるか検査します。
     * @return true 槓子の場合
     *         false 槓子でない場合
     */
    public boolean isQuad(){
        return sorted.size()==4;
    }

    /**
     * この面子が暗面子かどうか検査します。
     * <p>この面子が暗槓であれば検査に適合します。
     * <p>この面子がロン和了で成立した面子である場合検査に適合しません。
     * @return true 門前の場合
     *         false 門前でない場合
     */
    public boolean isConcealed(){
        return claimedNullable==null;
    }

    /**
     * この面子が暗槓であるか検査します。
     * @return true 暗槓の場合
     *         false 暗槓でない場合
     */
    public boolean isSelfQuad(){
        return base.size()==4;
    }

    /**
     * この面子が加槓であるか検査します。
     * @return true 加槓の場合
     *         false 加槓でない場合
     */
    public boolean isAddQuad(){
        return addedNullable!=null;
    }

    /**
     * この面子が大明槓であるか検査します。
     * @return true 大明槓の場合
     *         false 大明槓でない場合
     */
    public boolean isCallQuad(){
        return base.size()==3 && claimedNullable!=null;
    }

    /**
     * この面子が公開面子でない, 点数計算時に並べ替えによって作成された面子か検査します。
     * @return true 公開面子でない場合
     *         false 公開面子の場合
     */
    public boolean isHandMeld(){
        return claimedNullable==null && !isSelfQuad();
    }

    /**
     * この面子の符を計算し取得します。
     * <p>面子の符は以下のように計算されます。
     * <ul>
     *   <li>順子の場合は0/li>
     *   <li>刻子の場合は以下の表に従う</li>
     * </ul>
     * <table>
     *   <tr><th></th><th>中張</th><th>么九</th></tr>
     *   <tr><th>明刻</th><td>2</td><td>4</td></tr>
     *   <tr><th>暗刻</th><td>4</td><td>8</td></tr>
     *   <tr><th>明槓</th><td>8</td><td>16</td></tr>
     *   <tr><th>暗槓</th><td>16</td><td>32</td></tr>
     * </table>
     * @return 面子の符
     */
    PointType getMeldPointType(){
        return PointType.ofMeld(this);
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null || getClass()!=o.getClass()) return false;
        Meld meld = (Meld)o;
        return Objects.equals(base, meld.base)
                && claimedNullable==meld.claimedNullable
                && addedNullable==meld.addedNullable
                && source==meld.source;
    }

    @Override
    public int hashCode(){
        return Objects.hash(base, claimedNullable, addedNullable, source);
    }

    @Override
    public String toString(){
        var tiles = getTilesFormed();
        var firstTileRotated = source==Side.LEFT;
        var firstTile = (firstTileRotated?"(":"[")+tiles.get(0)+(firstTileRotated?")":"]");
        var secondTileRotated = source==Side.ACROSS;
        var secondTile = (secondTileRotated?"(":"[")+tiles.get(1)+(secondTileRotated?")":"]");
        var thirdTileRotated = (source==Side.RIGHT & isTriple());
        var thirdTile = (thirdTileRotated?"(":"[")+tiles.get(2)+(thirdTileRotated?")":"]");
        var fourthTileRotated = (source==Side.RIGHT & isQuad()) || (isAddQuad());
        var fourthTile = isQuad()?((fourthTileRotated?"(":"[")+tiles.get(3)+(fourthTileRotated?")":"]")):"";
        return (isConcealed()?"[":"(") + firstTile + secondTile + thirdTile + fourthTile + (isConcealed()?"]":")");
    }

    /**
     * カンによって暗槓を作成します。
     * @param tiles 手牌中から提供された構成牌(長さ4)
     * @throws IllegalArgumentException 構成牌が槓子を構成し得ない場合
     * @return 暗槓
     */
    public static Meld ofSelfQuad(List<Tile> tiles){
        if(!Tiles.isQuad(tiles)) throw new IllegalArgumentException("invalid tiles for self quad: " + tiles);
        return new Meld(tiles);
    }

    /**
     * カンによって加槓を作成します。
     * @param triple 元となる明刻
     * @param added 加槓宣言牌
     * @throws IllegalArgumentException 明刻以外の面子が指定された場合
     * @throws IllegalArgumentException 加槓宣言牌が指定された明刻に不適合の場合
     * @return 加槓
     */
    public static Meld ofAddQuad(Meld triple, Tile added){
        if(!triple.isTriple()) throw new IllegalArgumentException("invalid meld for add quad base: "+triple);
        if(!Tiles.isQuad(triple.sorted, added)) throw new IllegalArgumentException("invalid tiles for add quad: "+triple.sorted+" "+added);
        return new Meld(triple.base, triple.claimedNullable, added, triple.source);
    }

    /**
     * カンによって大明槓を作成します。
     * @param base 手牌中から提供された構成牌(長さ3)
     * @param claimed 副露牌
     * @param source 副露元の相対方位
     * @throws IllegalArgumentException 構成牌が槓子を構成し得ない場合
     * @throws IllegalArgumentException 副露元に自家が指定された場合
     * @return 大明槓
     */
    public static Meld ofCallQuad(List<Tile> base, Tile claimed, Side source){
        if(!Tiles.isQuad(base, claimed)) throw new IllegalArgumentException("invalid tiles for call quad: "+base+" "+claimed);
        if(source==Side.SELF) throw new IllegalArgumentException("invalid source side for call quad: "+Side.SELF);
        return new Meld(base, claimed, source);
    }

    /**
     * ポンによって明刻を作成します。
     * @param base 手牌中から提供された構成牌(長さ2)
     * @param claimed 副露牌
     * @param source 副露元の相対方位
     * @throws IllegalArgumentException 構成牌が刻子を構成し得ない場合
     * @throws IllegalArgumentException 副露元に自家が指定された場合
     * @return 明刻
     */
    public static Meld ofCallTriple(List<Tile> base, Tile claimed, Side source){
        if(!Tiles.isTriple(base, claimed)) throw new IllegalArgumentException("invalid tiles for call triple: "+base+" "+claimed);
        if(source==Side.SELF) throw new IllegalArgumentException("invalid source side for call triple: "+Side.SELF);
        return new Meld(base, claimed, source);
    }

    /**
     * チーによって明順を作成します。
     * @param base 手牌中から提供された構成牌(長さ2)
     * @param claimed 副露牌
     * @throws IllegalArgumentException 構成牌が順子を構成し得ない場合
     * @throws IllegalArgumentException 副露元に自家が指定された場合
     * @return 明順
     */
    public static Meld ofCallStraight(List<Tile> base, Tile claimed){
        if(!Tiles.isStraight(base, claimed)) throw new IllegalArgumentException("invalid tiles for call straight: "+base+" "+claimed);
        return new Meld(base, claimed, Side.LEFT);
    }

    /**
     * 点数計算のため, 手牌から暗順もしくは暗刻を作成します。
     * @param tiles 手牌中から提供された構成牌(長さ3)
     * @throws IllegalArgumentException 構成牌が刻子もしくは順子を構成し得ない場合
     * @throws IllegalArgumentException 副露元に自家が指定された場合
     * @return 面子
     */
    public static Meld ofHand(List<Tile> tiles){
        if(!Tiles.isTriple(tiles) && !Tiles.isStraight(tiles))
            throw new IllegalArgumentException("invalid tiles for meld: "+tiles);
        return new Meld(tiles);
    }

    /**
     * 点数計算のため, 手牌中の構成牌とロン牌から明順もしくは明刻を作成します。
     * @param base 手牌中から提供された構成牌
     * @param claimed ロン牌
     * @throws IllegalArgumentException 構成牌が刻子もしくは順子を構成し得ない場合
     * @throws IllegalArgumentException 副露元に自家が指定された場合
     * @return 面子
     */
    public static Meld ofHand(List<Tile> base, Tile claimed){
        if(!Tiles.isTriple(base, claimed) && !Tiles.isStraight(base, claimed))
            throw new IllegalArgumentException("invalid tiles for meld: " + base + " " + claimed);
        return new Meld(base, claimed, Side.SELF);
    }
}
