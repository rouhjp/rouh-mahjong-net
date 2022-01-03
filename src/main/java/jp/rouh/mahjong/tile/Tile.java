package jp.rouh.mahjong.tile;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 麻雀牌クラス。
 *
 * <p>牌の比較を行う場合は赤ドラ牌に注意が必要です。赤ドラ牌の可能性がある牌を比較する際は,
 * このインターフェースが提供する{@link #equalsIgnoreRed}メソッドを使用する必要があります。
 * このため, コレクションフレームワークでの重複の削除等の処理には注意が必要です。
 * @author Rouh
 * @version 1.0
 */
public enum Tile implements Comparable<Tile> {

    /**
     * 一萬
     */
    M1(0, 1, TileType.CHARACTERS),

    /**
     * 二萬
     */
    M2(1, 2, TileType.CHARACTERS),

    /**
     * 三萬
     */
    M3(2, 3, TileType.CHARACTERS),

    /**
     * 四萬
     */
    M4(3, 4, TileType.CHARACTERS),

    /**
     * 五萬
     */
    M5(4, 5, TileType.CHARACTERS),

    /**
     * 赤五萬
     */
    M5R(4, 5, TileType.CHARACTERS),

    /**
     * 六萬
     */
    M6(5, 6, TileType.CHARACTERS),

    /**
     * 七萬
     */
    M7(6, 7, TileType.CHARACTERS),

    /**
     * 八萬
     */
    M8(7, 8, TileType.CHARACTERS),

    /**
     * 九萬
     */
    M9(8, 9, TileType.CHARACTERS),

    /**
     * 一筒
     */
    P1(9, 1, TileType.CIRCLES),

    /**
     * 二筒
     */
    P2(10, 2, TileType.CIRCLES),

    /**
     * 三筒
     */
    P3(11, 3, TileType.CIRCLES),

    /**
     * 四筒
     */
    P4(12, 4, TileType.CIRCLES),

    /**
     * 五筒
     */
    P5(13, 5, TileType.CIRCLES),

    /**
     * 赤五筒
     */
    P5R(13, 5, TileType.CIRCLES),

    /**
     * 六筒
     */
    P6(14, 6, TileType.CIRCLES),

    /**
     * 七筒
     */
    P7(15, 7, TileType.CIRCLES),

    /**
     * 八筒
     */
    P8(16, 8, TileType.CIRCLES),

    /**
     * 九筒
     */
    P9(17, 9, TileType.CIRCLES),

    /**
     * 一索
     */
    S1(18, 1, TileType.BAMBOOS),

    /**
     * 二索
     */
    S2(19, 2, TileType.BAMBOOS),

    /**
     * 三索
     */
    S3(20, 3, TileType.BAMBOOS),

    /**
     * 四索
     */
    S4(21, 4, TileType.BAMBOOS),

    /**
     * 五索
     */
    S5(22, 5, TileType.BAMBOOS),

    /**
     * 赤五索
     */
    S5R(22, 5, TileType.BAMBOOS),

    /**
     * 六索
     */
    S6(23, 6, TileType.BAMBOOS),

    /**
     * 七索
     */
    S7(24, 7, TileType.BAMBOOS),

    /**
     * 八索
     */
    S8(25, 8, TileType.BAMBOOS),

    /**
     * 九索
     */
    S9(26, 9, TileType.BAMBOOS),

    /**
     * 東
     */
    WE(27, 0, TileType.WINDS),

    /**
     * 南
     */
    WS(28, 0, TileType.WINDS),

    /**
     * 西
     */
    WW(29, 0, TileType.WINDS),

    /**
     * 北
     */
    WN(30, 0, TileType.WINDS),

    /**
     * 白
     */
    DW(31, 0, TileType.DRAGONS),

    /**
     * 發
     */
    DG(32, 0, TileType.DRAGONS),

    /**
     * 中
     */
    DR(33, 0, TileType.DRAGONS);

    private static final List<Tile> GREENS = List.of(S2, S3, S4, S6, S8, DG);
    private static final List<Tile> REDS = List.of(M5R, P5R, S5R);
    private static final Tile[] SEQUENCE = {
            M1, M2, M3, M4, M5, M6, M7, M8, M9,
            P1, P2, P3, P4, P5, P6, P7, P8, P9,
            S1, S2, S3, S4, S5, S6, S7, S8, S9,
            WE, WS, WW, WN, DW, DG, DR
    };
    private final int tileNumber;
    private final int suitNumber;
    private final TileType type;

    Tile(int tileNumber, int suitNumber, TileType type) {
        this.tileNumber = tileNumber;
        this.suitNumber = suitNumber;
        this.type = type;
    }

    /**
     * 一萬から中までの牌の順序を表す数(0..135)を返します。
     * 序数はドラ表示牌の参照や理牌に用いられます。
     * @return 一萬から中までの牌の順序を表す数
     */
    public int tileNumber() {
        return tileNumber;
    }

    /**
     * この牌が数牌であれば牌の数(1..9)を返します。
     * この牌が字牌であれば0を返します。
     * @return 1..9 数牌の場合, 数牌の数値
     *         0    字牌の場合
     */
    public int suitNumber() {
        return suitNumber;
    }

    /**
     * この牌の牌の種類を取得します。
     * @return 牌の種類
     * @see TileType
     */
    public TileType tileType() {
        return type;
    }

    /**
     * この牌が萬子かどうか検査します。
     * @return true  萬子の場合
     *         false 萬子以外の場合
     */
    public boolean isCharacter() {
        return type == TileType.CHARACTERS;
    }

    /**
     * この牌が筒子かどうか検査します。
     * @return true  筒子の場合
     *         false 筒子以外の場合
     */
    public boolean isCircle() {
        return type == TileType.CIRCLES;
    }

    /**
     * この牌が索子かどうか検査します。
     * @return true  索子の場合
     *         false 索子以外の場合
     */
    public boolean isBamboo() {
        return type == TileType.BAMBOOS;
    }

    /**
     * この牌が風牌かどうか検査します。
     * @return true  風牌の場合
     *         false 風牌以外の場合
     */
    public boolean isWind() {
        return type == TileType.WINDS;
    }

    /**
     * この牌が三元牌かどうか検査します。
     * @return true  三元牌の場合
     *         false 三元牌以外の場合
     */
    public boolean isDragon() {
        return type == TileType.DRAGONS;
    }

    /**
     * この牌が字牌かどうか検査します。
     * @return true  字牌の場合
     *         false 字牌以外の場合
     */
    public boolean isHonor() {
        return suitNumber() == 0;
    }

    /**
     * この牌が老頭牌かどうか検査します。
     * @return true  老頭牌の場合
     *         false 老頭牌以外の場合
     */
    public boolean isTerminal() {
        return suitNumber() == 1 || suitNumber() == 9;
    }

    /**
     * この牌が么九牌かどうか検査します。
     * @return true  么九牌の場合
     *         false 么九牌以外の場合
     */
    public boolean isOrphan() {
        return isHonor() || isTerminal();
    }

    /**
     * この牌が緑一色の構成牌となり得るか検査します。
     * <p>この検査に適合する牌は二索, 三索, 四索, 六索, 八索, 發の6種です。
     * @return true  緑一色の構成牌の場合
     *         false 緑一色の構成牌以外の場合
     */
    public boolean isGreen() {
        return GREENS.contains(this);
    }

    /**
     * この牌に順子を構成する上で必要となる前の数牌が存在するか検査します。
     * @return true  順子の2..9番目の牌の場合
     *         false 順子の1番目の牌もしくは字牌の場合
     */
    public boolean hasPrevious() {
        return suitNumber() >= 2 && suitNumber() <= 9;
    }

    /**
     * この牌に順子を構成する上で必要となる次の数牌が存在するか検査します。
     * @return true  順子の1..8番目の牌の場合
     *         false 順子の9番目の牌もしくは字牌の場合
     */
    public boolean hasNext() {
        return suitNumber() >= 1 && suitNumber() <= 8;
    }

    /**
     * この牌が指定した牌の順子を構成する上で次に来る牌であるかどうか検査します。
     * 比較の際, 赤ドラかどうかは無視されます。
     * @param other 検査対象
     * @return true  指定した牌の次の牌の場合
     *         false 指定した牌の次の牌でない場合, 指定した牌に次の牌が存在しない場合
     */
    public boolean isNextOf(Tile other) {
        return other.hasNext() && other.next().equalsIgnoreRed(this);
    }

    /**
     * この牌が指定した牌の順子を構成する上で前に来る牌であるかどうか検査します。
     * 比較の際, 赤ドラかどうかは無視されます。
     * @param other 検査対象
     * @return true  指定した牌の前の牌の場合
     *         false 指定した牌の前の牌でない場合, 指定した牌に前の牌が存在しない場合
     */
    public boolean isPreviousOf(Tile other) {
        return other.hasPrevious() && other.previous().equalsIgnoreRed(this);
    }

    /**
     * この牌と指定した牌を赤ドラ牌かどうかを無視して等価か検査します。
     * 例えば, 赤五筒と五筒は検査に適合します。
     * また, 五筒と五筒の場合も検査に適合します。
     * @param other 検査対象
     * @return true  等価な場合
     *         false 等価でない場合
     */
    public boolean equalsIgnoreRed(Tile other) {
        return this.tileNumber() == other.tileNumber();
    }

    /**
     * この牌と指定した牌が同種の牌かどうかを検査します。
     * @param other 検査対象
     * @return true  同種の場合
     *         false 同種でない場合
     */
    public boolean isSameTypeOf(Tile other) {
        return this.tileType() == other.tileType();
    }

    /**
     * この牌が順子を構成する上で必要となる前の数牌を返します。
     * {@link #hasPrevious}メソッドで事前に例外の発生を防ぐことができます。
     * 例えば, 四筒に対してこのメソッドを呼んだ場合, 三筒が結果として返されます。
     * また, 一萬に対してこのメソッドを呼んだ場合, 前の数牌が存在しないため例外がスローされます。
     * @return 次の連続する数牌
     * @throws NoSuchElementException 字牌または九牌の場合
     */
    public Tile previous() {
        if (!hasPrevious()) throw new NoSuchElementException("previous tile of " + this);
        return SEQUENCE[(tileNumber + 33) % 34];
    }

    /**
     * この牌が順子を構成する上で必要となる次の数牌を返します。
     * {@link #hasNext}メソッドで事前に例外の発生を防ぐことができます。
     * 例えば, 四筒に対してこのメソッドを呼んだ場合, 五筒が結果として返されます。
     * また, 九萬に対してこのメソッドを呼んだ場合, 次の数牌が存在しないため例外がスローされます。
     * @return 次の連続する数牌
     * @throws NoSuchElementException 字牌または九牌の場合
     */
    public Tile next() {
        if (!hasNext()) throw new NoSuchElementException("next tile of " + this);
        return SEQUENCE[(tileNumber + 1) % 34];
    }

    /**
     * この牌がドラ表示牌の場合のドラ牌を返します。
     * 例えば, 四筒に対してこのメソッドを呼んだ場合, 五筒が結果として返されます。
     * また, 九萬に対してこのメソッドを呼んだ場合, 一筒が結果として返されます。
     * @return 次の牌
     */
    public Tile indicates() {
        return switch(this){
            case M9 -> M1;
            case P9 -> P1;
            case S9 -> S1;
            case DR -> DW;
            case WN -> WE;
            default -> SEQUENCE[(tileNumber + 1) % 34];
        };
    }

    /**
     * この牌が赤ドラ牌かどうか検査します。
     * @return true  赤ドラの場合
     *         false 赤ドラ以外の場合
     */
    public boolean isPrisedRed() {
        return REDS.contains(this);
    }
}
