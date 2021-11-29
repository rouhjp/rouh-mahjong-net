package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 副露面子クラス。
 * <p>このクラスは面子{@link Meld}インターフェースの実装の一つです。
 * @author Rouh
 * @version 1.0
 */
class CallMeld implements Meld{
    private final List<Tile> baseTiles;
    private final Tile claimedTile;
    private final Side source;

    CallMeld(List<Tile> baseTiles, Tile claimedTile, Side source){
        this.baseTiles = List.copyOf(baseTiles);
        this.claimedTile = claimedTile;
        this.source = source;
    }

    private List<Tile> getTiles(){
        var list = new ArrayList<>(baseTiles);
        list.add(claimedTile);
        return list;
    }

    @Override
    public List<Tile> getTilesFormed(){
        int tiltTileIndex = switch(source){
            case LEFT -> 0;
            case ACROSS -> 1;
            case RIGHT -> baseTiles.size();
            case SELF -> throw new AssertionError();
        };
        var formed = new ArrayList<Tile>(baseTiles.size() + 1);
        formed.addAll(baseTiles);
        formed.add(tiltTileIndex, claimedTile);
        return formed;
    }

    @Override
    public List<Tile> getTilesSorted(){
        var tiles = getTiles();
        Collections.sort(tiles);
        return tiles;
    }

    @Override
    public List<Tile> getTilesTruncated(){
        return getTilesSorted().subList(0, 3);
    }

    @Override
    public Side getSourceSide(){
        return source;
    }

    @Override
    public boolean isSequence(){
        return baseTiles.size()==2 && !baseTiles.get(0).equalsIgnoreRed(baseTiles.get(1));
    }

    @Override
    public boolean isTriple(){
        return baseTiles.size()==2 && baseTiles.get(0).equalsIgnoreRed(baseTiles.get(1));
    }

    @Override
    public boolean isQuad(){
        return baseTiles.size()==3;
    }

    @Override
    public boolean isConcealed(){
        return false;
    }

    @Override
    public boolean isSelfQuad(){
        return false;
    }

    @Override
    public boolean isAddQuad(){
        return false;
    }

    @Override
    public boolean isCallQuad(){
        return isQuad();
    }
}
