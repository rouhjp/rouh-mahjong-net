package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;

import java.util.List;

/**
 * 門前面子クラス。
 * <p>このクラスは{@link Meld}インターフェースの実装で,
 * 副露や暗槓などの公開面子{@link CallMeld}ではない面子です。
 * すなわち, 和了後に純手牌を整形する過程で構成する面子です。
 * <p>門前面子が, ロン牌により成立した刻子である場合,
 * 明刻扱いになるため注意が必要です。
 * @author Rouh
 * @version 1.0
 */
class HandMeld implements Meld{
    private final List<Tile> sorted;
    private final boolean concealed;

    /**
     * 門前面子のコンストラクタ。
     * @param tiles     面子構成牌
     * @param concealed 暗面子かどうか
     * @throws IllegalArgumentException 面子構成が不正の場合
     */
    HandMeld(List<Tile> tiles, boolean concealed){
        if(!Tiles.isTriple(tiles) && !Tiles.isSequence(tiles)){
            throw new IllegalArgumentException("invalid hand meld tiles: " + tiles);
        }
        this.sorted = tiles.stream().sorted().toList();
        this.concealed = concealed;
    }

    @Override
    public List<Tile> getTilesFormed(){
        return sorted;
    }

    @Override
    public List<Tile> getTilesSorted(){
        return sorted;
    }

    @Override
    public List<Tile> getTilesTruncated(){
        return sorted;
    }

    @Override
    public Side getSourceSide(){
        return Side.SELF;
    }

    @Override
    public boolean isSequence(){
        return !sorted.get(0).equalsIgnoreRed(sorted.get(1));
    }

    @Override
    public boolean isTriple(){
        return sorted.get(0).equalsIgnoreRed(sorted.get(1));
    }

    @Override
    public boolean isQuad(){
        return false;
    }

    @Override
    public boolean isConcealed(){
        return concealed;
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
        return false;
    }

    @Override
    public String toString(){
        return "HandMeld" + getTilesSorted() + (isConcealed()? "":"X");
    }
}
