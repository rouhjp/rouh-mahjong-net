package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * 加槓クラス。
 * <p>このクラスは面子{@link Meld}インターフェースの実装の一つです。
 * @author Rouh
 * @version 1.0
 */
class AddQuad implements Meld{
    private final List<Tile> formed;
    private final Side source;

    /**
     * 加槓のコンストラクタ。
     * @param original       元となる面子
     * @param additionalTile 追加の牌
     * @throws IllegalArgumentException 元となる面子もしくは追加牌が不正の場合
     */
    AddQuad(Meld original, Tile additionalTile){
        if(!original.isTriple() || original.getSourceSide()==Side.SELF || !original.getFirst().equalsIgnoreRed(additionalTile)){
            throw new IllegalArgumentException("invalid add quad: " + original.getTilesFormed() + " " + additionalTile);
        }
        var formed = new ArrayList<>(original.getTilesFormed());
        formed.add(additionalTile);
        this.formed = formed;
        this.source = original.getSourceSide();
    }

    @Override
    public List<Tile> getTilesFormed(){
        return formed;
    }

    @Override
    public List<Tile> getTilesSorted(){
        return formed.stream().sorted().toList();
    }

    @Override
    public List<Tile> getTilesTruncated(){
        return formed;
    }

    @Override
    public Side getSourceSide(){
        return source;
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
        return false;
    }

    @Override
    public boolean isSelfQuad(){
        return false;
    }

    @Override
    public boolean isAddQuad(){
        return true;
    }

    @Override
    public boolean isCallQuad(){
        return false;
    }

    @Override
    public String toString(){
        return "AddQuad" + formed;
    }
}
