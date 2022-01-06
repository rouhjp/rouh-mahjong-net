package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;

import java.util.List;

/**
 * 非定形通常役クラス。
 * <p>通常役{@link BasicHandType}のうち,
 * 特殊な面子構成の役を表します。
 * <p>該当する役は以下の通りです。
 * <ul>
 *     <li>七対子</li>
 * </ul>
 * @author Rouh
 * @version 1.0
 */
public enum IrregularFormatHandType implements BasicHandType{

    /**
     * 七対子
     */
    SEVEN_PAIR("七対子", 2){
        @Override
        boolean test(List<Tile> handTiles, Tile winningTile, ScoringContext context){
            return context.isSelfMade() && HandTiles.isSevenPairs(handTiles, winningTile);
        }
    };

    private final String text;
    private final int doubles;

    IrregularFormatHandType(String text, int doubles){
        this.text = text;
        this.doubles = doubles;
    }

    /**
     * 適合する非定形通常役をすべてリスト形式で取得する。
     * @param handTiles   純手牌
     * @param winningTile 和了牌
     * @param context     和了状況
     * @return 通常役のリスト
     */
    abstract boolean test(List<Tile> handTiles, Tile winningTile, ScoringContext context);

    @Override
    public int getDoubles(){
        return doubles;
    }

    @Override
    public String getName(){
        return text;
    }
}
