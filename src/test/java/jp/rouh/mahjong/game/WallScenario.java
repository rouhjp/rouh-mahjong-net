package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 山牌のシナリオ。
 * <p>テスト用に牌の順番を指定した山牌を生成するために用います。
 * <p>例えば天和が成立する状態をテストするために, {@link #setDistributionTiles}及び
 * {@link #setTurnDrawTile}メソッドを用いて完成形の14牌を設定しておき,
 * {@link #generateWall}メソッドで山牌を生成することで, 天和が成立するゲームを再現できます。
 * @author Rouh
 * @version 1.0
 */
public class WallScenario{
    private static final int[] TURN_DRAW_TILE_SIZE = {18, 18, 17, 17};
    private final List<Tile> tileSet;
    private final Tile[][] distributionTiles = new Tile[4][13];
    private final Tile[] turnDrawTiles = new Tile[70];
    private final Tile[] quadDrawTiles = new Tile[4];
    private final Tile[] lowerIndicatorTiles = new Tile[5];
    private final Tile[] upperIndicatorTiles = new Tile[5];

    /**
     * 牌のセットを指定してシナリオを生成します。
     * @param tileSet 牌のセット(136枚)
     */
    public WallScenario(List<Tile> tileSet){
        if(tileSet.size()!=136){
            throw new IllegalArgumentException("invalid size of tile set: "+tileSet.size());
        }
        this.tileSet = new ArrayList<>(tileSet);
    }

    /**
     * 指定の場風のプレイヤーの配牌を指定します。
     * @param wind 場風
     * @param tiles 配牌(~13枚)
     */
    public void setDistributionTiles(Wind wind, List<Tile> tiles){
        if(tiles.size()>13){
            throw new IllegalArgumentException("invalid size of distribution tiles: "+tiles.size());
        }
        for(int i = 0; i<tiles.size(); i++){
            var tile = tiles.get(i);
            if(distributionTiles[wind.ordinal()][i]!=null){
                throw new IllegalStateException("scenario conflicted: tile at "+wind+" distribution already set");
            }
            if(!tileSet.remove(tile)){
                throw new IllegalStateException("scenario conflicted: tile "+tile+" already in use");
            }
            distributionTiles[wind.ordinal()][i] = tile;
        }
    }

    /**
     * ツモ牌を指定します。
     * @param tiles ツモ牌(~70枚)
     */
    public void setTurnDrawTiles(List<Tile> tiles){
        if(tiles.size()>70){
            throw new IllegalArgumentException("invalid size of draw tiles: "+tiles.size());
        }
        for(int i = 0; i<tiles.size(); i++){
            var tile = tiles.get(i);
            if(turnDrawTiles[i]!=null){
                throw new IllegalStateException("scenario conflicted: tile at turn draw tiles "+i+" already set");
            }
            if(!tileSet.remove(tile)){
                throw new IllegalStateException("scenario conflicted: tile "+tile+" already in use");
            }
            turnDrawTiles[i] = tile;
        }
    }

    /**
     * ツモ牌を海底牌から逆順に指定します。
     * <p>ただしカンが発生した場合は海底牌がずれることに注意が必要です。
     * @param tiles ツモ牌(~70枚)
     */
    public void setLastTurnDrawTiles(List<Tile> tiles){
        if(tiles.size()>70){
            throw new IllegalArgumentException("invalid size of draw tiles: "+tiles.size());
        }
        for(int i = 0; i<tiles.size(); i++){
            var tile = tiles.get(i);
            if(turnDrawTiles[69 - i]!=null){
                throw new IllegalStateException("scenario conflicted: tile at turn draw tiles "+i+" already set");
            }
            if(!tileSet.remove(tile)){
                throw new IllegalStateException("scenario conflicted: tile "+tile+" already in use");
            }
            turnDrawTiles[69 - i] = tile;
        }
    }

    /**
     * 指定の場風のプレイヤーのツモ牌を指定します。
     * @param wind 場風
     * @param tiles ツモ可能牌(東南家の場合~18枚, 西北家の場合~17枚)
     */
    public void setTurnDrawTile(Wind wind, List<Tile> tiles){
        if(tiles.size()>TURN_DRAW_TILE_SIZE[wind.ordinal()]){
            throw new IllegalArgumentException("invalid size of draw tiles: "+tiles.size());
        }
        for(int i = 0; i<tiles.size(); i++){
            var tile = tiles.get(i);
            if(turnDrawTiles[wind.ordinal() + 4*i]!=null){
                throw new IllegalStateException("scenario conflicted: tile at turn draw tiles "+i+" already set");
            }
            if(!tileSet.remove(tile)){
                throw new IllegalStateException("scenario conflicted: tile "+tile+" already in use");
            }
            turnDrawTiles[wind.ordinal() + 4*i] = tile;
        }
    }

    /**
     * 嶺上牌を指定します。
     * @param tiles 嶺上牌(~4枚)
     */
    public void setQuadDrawTiles(List<Tile> tiles){
        if(tiles.size()>4){
            throw new IllegalArgumentException("invalid size of draw tiles: " + tiles.size());
        }
        for(int i = 0; i<tiles.size(); i++){
            var tile = tiles.get(i);
            if(quadDrawTiles[i]!=null){
                throw new IllegalStateException("scenario conflicted: tile at quad draw tile "+i+" already set");
            }
            if(!tileSet.remove(tile)){
                throw new IllegalStateException("scenario conflicted: tile "+tile+" already in use");
            }
            quadDrawTiles[i] = tile;
        }
    }

    /**
     * ドラ表示牌を指定します。
     * @param tiles ドラ表示牌(~5枚)
     */
    public void setUpperIndicatorTiles(List<Tile> tiles){
        if(tiles.size()>5){
            throw new IllegalArgumentException("invalid size of upper indicators: " + tiles.size());
        }
        for(int i = 0; i<tiles.size(); i++){
            var tile = tiles.get(i);
            if(upperIndicatorTiles[i]!=null){
                throw new IllegalStateException("scenario conflicted: tile at quad draw tile "+i+" already set");
            }
            if(!tileSet.remove(tile)){
                throw new IllegalStateException("scenario conflicted: tile "+tile+" already in use");
            }
            upperIndicatorTiles[i] = tile;
        }
    }

    /**
     * 裏ドラ表示牌を指定します。
     * @param tiles 裏ドラ表示牌(~5枚)
     */
    public void setLowerIndicatorTiles(List<Tile> tiles){
        if(tiles.size()>5){
            throw new IllegalArgumentException("invalid size of lower indicators: " + tiles.size());
        }
        for(int i = 0; i<tiles.size(); i++){
            var tile = tiles.get(i);
            if(lowerIndicatorTiles[i]!=null){
                throw new IllegalStateException("scenario conflicted: tile at quad draw tile "+i+" already set");
            }
            if(!tileSet.remove(tile)){
                throw new IllegalStateException("scenario conflicted: tile "+tile+" already in use");
            }
            lowerIndicatorTiles[i] = tile;
        }
    }

    /**
     * このシナリオから山牌を生成します。
     * <p>シナリオで定義されていない箇所はランダムで残りの牌が割り当てられます。
     * @param dice1 生成時の一つ目のサイコロの目(1..6)
     * @param dice2 生成時の二つ目のサイコロの目(1..6)
     * @return 山牌
     */
    public Wall generateWall(int dice1, int dice2){
        var remainingTiles = new ArrayList<>(tileSet);
        remainingTiles.sort(Comparator.naturalOrder());
        for(var wind:Wind.values()){
            for(int i = 0; i<distributionTiles[wind.ordinal()].length; i++){
                if(distributionTiles[wind.ordinal()][i]==null){
                    distributionTiles[wind.ordinal()][i] = remainingTiles.remove(0);
                }
            }
        }
        for(int i = 0; i<turnDrawTiles.length; i++){
            if(turnDrawTiles[i]==null){
                turnDrawTiles[i] = remainingTiles.remove(0);
            }
        }
        for(int i = 0; i<quadDrawTiles.length; i++){
            if(quadDrawTiles[i]==null){
                quadDrawTiles[i] = remainingTiles.remove(0);
            }
        }
        for(int i = 0; i<lowerIndicatorTiles.length; i++){
            if(lowerIndicatorTiles[i]==null){
                lowerIndicatorTiles[i] = remainingTiles.remove(0);
            }
        }
        for(int i = 0; i<upperIndicatorTiles.length; i++){
            if(upperIndicatorTiles[i]==null){
                upperIndicatorTiles[i] = remainingTiles.remove(0);
            }
        }
        assert remainingTiles.isEmpty();
        var wallTiles = new ArrayList<Tile>(136);
        for(int i = 0; i<3; i++){
            for(var wind:Wind.values()){
                for(int j = 0; j<4; j++){
                    int index = i*4 + j;
                    wallTiles.add(distributionTiles[wind.ordinal()][index]);
                }
            }
        }
        wallTiles.addAll(List.of(turnDrawTiles));
        for(int i = 0; i<5; i++){
            wallTiles.add(upperIndicatorTiles[4 - i]);
            wallTiles.add(lowerIndicatorTiles[4 - i]);
        }
        wallTiles.add(quadDrawTiles[2]);
        wallTiles.add(quadDrawTiles[3]);
        wallTiles.add(quadDrawTiles[0]);
        wallTiles.add(quadDrawTiles[1]);
        int diceSum = dice1 + dice2;
        int firstIndex = ((3 - diceSum%4)*34 + diceSum*2)%136;
        var shiftedWallTiles = new ArrayList<Tile>(136);
        shiftedWallTiles.addAll(wallTiles.subList(firstIndex, 136));
        shiftedWallTiles.addAll(wallTiles.subList(0, firstIndex));
        return new ArrayWall(shiftedWallTiles.toArray(new Tile[0]), diceSum);
    }
}
