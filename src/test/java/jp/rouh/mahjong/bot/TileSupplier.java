package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;

import java.util.List;
import java.util.stream.Stream;

/**
 * 山牌の代わりにシャッフルされた牌のセットから牌を提供するテスト用牌提供クラス。
 * @author Rouh
 * @version 1.0
 */
class TileSupplier implements TileCounter{
    private final List<Tile> tileSet = Tiles.newShuffledTileSet();

    /**
     * 次の牌を取得します。
     * @return 牌
     * @throws IllegalStateException 137回以上このメソッドが呼ばれた場合
     */
    public Tile take(){
        if(tileSet.isEmpty()){
            throw new IllegalStateException("no tile left");
        }
        return tileSet.remove(0);
    }

    /**
     * {@link #take}メソッドをn回呼び出した結果をリストとして取得します。
     * @param n 呼び出し回数
     * @return 結果の牌のリスト
     */
    public List<Tile> take(int n){
        return Stream.generate(this::take).limit(n).toList();
    }

    @Override
    public int count(Tile tile){
        return (int)tileSet.stream().filter(t->t.equalsIgnoreRed(tile)).count();
    }
}
