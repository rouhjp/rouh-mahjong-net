package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.tile.Tile;

import java.util.List;
import java.util.Set;

/**
 * 打牌ボット。
 * <p>手牌と牌の残り枚数から捨て牌を選択することで手牌の完成を目指します。
 * @author Rouh
 * @version 1.0
 */
public interface DiscardingBot{

    /**
     * 捨て牌を選択します。
     * @param allTiles 手牌
     * @param counter 残り枚数カウンター
     * @return 捨て牌
     */
    Tile select(List<Tile> allTiles, TileCounter counter);

    /**
     * 立直可能時に立直宣言牌を選択します。
     * @param allTiles 手牌
     * @param readyTiles 立直宣言可能牌のセット
     * @param counter 残り枚数カウンター
     * @return 立直宣言牌
     */
    default Tile selectReady(List<Tile> allTiles, Set<Tile> readyTiles, TileCounter counter){
        var selected = select(allTiles, counter);
        if(readyTiles.contains(selected)){
            return selected;
        }else{
            return List.copyOf(readyTiles).get(0);
        }
    }

    /**
     * ボットの実装名を取得します。
     * @return 実装名
     */
    default String name(){
        return toString();
    }
}
