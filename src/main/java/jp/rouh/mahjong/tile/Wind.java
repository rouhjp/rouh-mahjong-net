package jp.rouh.mahjong.tile;

import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

/**
 * 風(方角)クラス。
 * @author Rouh
 * @version 1.0
 * @see Side
 */
public enum Wind {

    /**
     * 東風
     */
    EAST("東"),

    /**
     * 南風
     */
    SOUTH("南"),

    /**
     * 西風
     */
    WEST("西"),

    /**
     * 北風
     */
    NORTH("北");

    private static final Wind[] VALUES = values();

    private final String text;
    Wind(String text){
        this.text = text;
    }

    /**
     * 東南西北の順に習い、次の方角を返します。
     *
     * <p>例えば次のような等式が成り立ちます。
     * {@code EAST.next()==SOUTH }
     * {@code NORTH.next()==EAST }
     * @return 次の方角
     */
    public Wind next() {
        return shift(1);
    }

    /**
     * 東南西北の順に習い、次の方角を返します。
     *
     * <p>例えば次のような等式が成り立ちます。
     * {@code EAST.next()==SOUTH }
     * {@code NORTH.next()==EAST }
     * @return 次の方角
     */
    public Wind shift(int n) {
        return VALUES[(ordinal() + n) % 4];
    }

    /**
     * この方角以外の方角をリスト形式で返します。
     *
     * <p>例えば {@code SOUTH.others()} は, [EAST, WEST, NORTH]と等価です。
     * @return 残りの方角のリスト
     */
    public List<Wind> others(){
        return Stream.of(values()).filter(not(this::equals)).toList();
    }

    /**
     * この方角の, 引数で与えられた基準の方角からみた場合の相対位置を返します。
     *
     * <p>例えば以下の等式が成り立ちます。
     * {@code EAST.from(SOUTH)==Side.LEFT }
     * @param reference 基準の方角
     * @return 基準の方角からみたこの方角の相対位置
     * @see Side
     */
    public Side from(Wind reference) {
        return Side.of(this, reference);
    }

    /**
     * 対応する風牌を返します。
     * @return 対応する風牌
     */
    public Tile toTile() {
        return switch (this) {
            case EAST -> Tile.WE;
            case SOUTH -> Tile.WS;
            case WEST -> Tile.WW;
            case NORTH -> Tile.WN;
        };
    }

    /**
     * 漢字表記の文字列を取得します。
     * @return 風の漢字表記
     */
    public String getText(){
        return text;
    }
}
