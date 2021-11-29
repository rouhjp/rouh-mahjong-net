package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 暗槓クラス。
 * <p>このクラスは面子{@link Meld}インターフェースの実装の一つです。
 * @author Rouh
 * @version 1.0
 */
class SelfQuad implements Meld{
    private final List<Tile> tiles;

    /**
     * 暗槓のコンストラクタ。
     * @param tiles 暗槓構成牌
     * @throws IllegalArgumentException 構成牌が不正の場合
     */
    SelfQuad(List<Tile> tiles){
        if(!Tiles.isQuad(tiles)){
            throw new IllegalArgumentException("invalid quad tiles: " + tiles);
        }
        this.tiles = tiles.stream().sorted().toList();
    }

    @Override
    public List<Tile> getTilesFormed(){
        var formed = new ArrayList<>(tiles);
        Collections.swap(formed, 2, 3);
        return formed;
    }

    @Override
    public List<Tile> getTilesSorted(){
        return tiles;
    }

    @Override
    public List<Tile> getTilesTruncated(){
        return tiles.subList(0, 3);
    }

    @Override
    public Side getSourceSide(){
        return Side.SELF;
    }

    @Override
    public boolean isSequence(){
        return false;
    }

    @Override
    public boolean isTriple(){
        return false;
    }

    @Override
    public boolean isQuad(){
        return true;
    }

    @Override
    public boolean isConcealed(){
        return true;
    }

    @Override
    public boolean isSelfQuad(){
        return true;
    }

    @Override
    public boolean isAddQuad(){
        return false;
    }

    @Override
    public boolean isCallQuad(){
        return false;
    }

    @Override
    public String toString(){
        return "SelfQuad" + tiles;
    }
}
