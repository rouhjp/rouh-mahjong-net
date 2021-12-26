package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

/**
 * 雀頭クラス。
 * @author Rouh
 * @version 1.0
 */
class Head implements HandComponent{
    private final List<Tile> tiles;

    /**
     * 与えられた牌のリストから雀頭オブジェクトを生成するコンストラクタ。
     * @param tiles 構成牌のリスト(長さ2)
     * @throws IllegalArgumentException 与えられたリストの長さが2以外の場合
     */
    Head(List<Tile> tiles){
        if(tiles.size()!=2){
            throw new IllegalArgumentException("invalid size of head: " + tiles.size());
        }
        this.tiles = tiles.stream().sorted().toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tile> getTilesSorted(){
        return tiles;
    }

    /**
     * 雀頭の符を取得します。
     *
     * <p>雀頭の符は, 以下のように計算されます。
     * <table>
     *     <tr><th>数牌</th><td>0</td></tr>
     *     <tr><th>客牌</th><td>0</td></tr>
     *     <tr><th>自風牌</th><td>2</td></tr>
     *     <tr><th>場風牌</th><td>2</td></tr>
     *     <tr><th>連風牌</th><td>4</td></tr>
     *     <tr><th>三元牌</th><td>2</td></tr>
     * </table>
     * @param seatWind  自風
     * @param roundWind 場風
     * @return 雀頭の符
     */
    int getHeadPoint(Wind seatWind, Wind roundWind){
        if(!isHonor()) return 0;
        if(isDragon()) return 2;
        int point = 0;
        if(isWind()){
            if(getFirst().equalsIgnoreRed(seatWind.toTile())) point += 2;
            if(getFirst().equalsIgnoreRed(roundWind.toTile())) point += 2;
        }
        return point;
    }

    @Override
    public String toString(){
        return tiles.toString();
    }
}
