package jp.rouh.mahjong.tile;

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
    EAST,

    /**
     * 南風
     */
    SOUTH,

    /**
     * 西風
     */
    WEST,

    /**
     * 北風
     */
    NORTH;

    private static final Wind[] VALUES = values();

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
    Wind shift(int n) {
        return VALUES[(ordinal() + n) % 4];
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
}
