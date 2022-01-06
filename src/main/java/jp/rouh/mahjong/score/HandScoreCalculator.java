package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;

import java.util.List;

/**
 * 点数計算機能インターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface HandScoreCalculator{

    /**
     * 手牌と和了状況をもとに得点を算出します。
     * <p>複数の解釈が発生する場合は, 高点法に則り, 点数の最も高いものが採用されます。
     * @param handTiles   純手牌
     * @param openMelds   公開面子
     * @param winningTile 和了牌
     * @param context     和了状況
     * @return 得点
     * @throws IllegalArgumentException 手牌が完成形でない場合
     */
    HandScore calculate(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, ScoringContext context);

}
