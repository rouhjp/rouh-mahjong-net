package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

import static jp.rouh.mahjong.score.WinningOption.*;

/**
 * 和了状況クラス。
 *
 * <p>和了の得点を計算するにあたって必要な手牌以外の情報を保持します。
 * これには, 場風, 自風, 放銃者の方向(ツモかどうかを判定可能),
 * @author Rouh
 * @version 2.0
 */
public class WinningSituation{
    private final Wind roundWind;
    private final Wind seatWind;
    private final Side supplierSide;
    private final List<Tile> upperIndicators;
    private final List<Tile> lowerIndicators;
    private final List<WinningOption> options;

    /**
     * 和了状況のインスタンスを生成します。
     * @param roundWind 場風
     * @param seatWind 自風
     * @param supplierSide 放銃者の方向(ツモの場合は{@code Side.SELF})
     * @param upperIndicators ドラ表示牌のリスト
     * @param lowerIndicators 裏ドラ表示牌のリスト
     * @param options 付帯状況のリスト
     */
    public WinningSituation(Wind roundWind, Wind seatWind, Side supplierSide,
                            List<Tile> upperIndicators, List<Tile> lowerIndicators, List<WinningOption> options){
        this.roundWind = roundWind;
        this.seatWind = seatWind;
        this.supplierSide = supplierSide;
        this.upperIndicators = List.copyOf(upperIndicators);
        this.lowerIndicators = List.copyOf(lowerIndicators);
        this.options = List.copyOf(options);
        validateOptions();
    }

    private static final List<List<WinningOption>> DEPENDENT_OPTIONS = List.of(
            List.of(FIRST_AROUND_READY, READY),
            List.of(READY_AROUND_WIN, READY)
    );

    private static final List<List<WinningOption>> CONTRADICT_OPTIONS = List.of(
            List.of(FIRST_AROUND_WIN, READY),
            List.of(FIRST_AROUND_WIN, FIRST_AROUND_READY),
            List.of(FIRST_AROUND_WIN, READY_AROUND_WIN),
            List.of(FIRST_AROUND_WIN, LAST_TILE_WIN),
            List.of(FIRST_AROUND_WIN, QUAD_TILE_WIN),
            List.of(FIRST_AROUND_WIN, QUAD_TURN_WIN),
            List.of(READY_AROUND_WIN, QUAD_TURN_WIN),
            List.of(LAST_TILE_WIN, QUAD_TILE_WIN),
            List.of(LAST_TILE_WIN, QUAD_TURN_WIN),
            List.of(QUAD_TILE_WIN, QUAD_TURN_WIN)
    );

    private void validateOptions(){
        for(var dependentOptions:DEPENDENT_OPTIONS){
            if(options.contains(dependentOptions.get(0)) && !options.contains(dependentOptions.get(1))){
                throw new IllegalArgumentException("invalid options: "+dependentOptions.get(0)+" cannot be applied without "+dependentOptions.get(1));
            }
        }
        for(var contradictOptions:CONTRADICT_OPTIONS){
            if(options.contains(contradictOptions.get(0)) && options.contains(contradictOptions.get(1))){
                throw new IllegalArgumentException("invalid options: "+contradictOptions.get(0)+" and "+contradictOptions.get(1)+" cannot be applied simultaneously");
            }
        }
    }

    /**
     * 和了者の場風を取得します。
     * @return 場風
     */
    public Wind getRoundWind(){
        return roundWind;
    }

    /**
     * 和了者の自風を取得します。
     * @return 自風
     */
    public Wind getSeatWind(){
        return seatWind;
    }

    /**
     * 和了者からみた放銃者の方向を取得します。
     * @return 放銃者の方向
     */
    public Side getSupplierSide(){
        return supplierSide;
    }

    /**
     * ドラ表示牌のリストを取得します。
     * @return ドラ表示牌のリスト
     */
    public List<Tile> getUpperIndicators(){
        return upperIndicators;
    }

    /**
     * 裏ドラ表示牌のリストを取得します。
     * @return ドラ表示牌のリスト
     */
    public List<Tile> getLowerIndicators(){
        return lowerIndicators;
    }

    /**
     * ドラのリストを取得します。
     * @return ドラのリスト
     */
    public List<Tile> getUpperPrisedTiles(){
        return upperIndicators.stream().map(Tile::indicates).toList();
    }

    /**
     * 裏ドラのリストを取得します。
     * @return 裏ドラのリスト
     */
    public List<Tile> getLowerPrisedTiles(){
        return lowerIndicators.stream().map(Tile::indicates).toList();
    }

    /**
     * 和了者が親かどうかを取得します。
     * @return true 親の場合
     *         false 子の場合
     */
    public boolean isDealer(){
        return seatWind==Wind.EAST;
    }

    /**
     * ツモ和了かどうかを取得します。
     * @return true ツモの場合
     *         false ロンの場合
     */
    public boolean isTsumo(){
        return supplierSide==Side.SELF;
    }

    /**
     * 立直済みかどうかを取得します。
     * @return true 立直の場合
     *         false 立直でない場合
     */
    public boolean isReady(){
        return options.contains(READY);
    }

    /**
     * ダブル立直済みかどうかを取得します。
     * @return true ダブル立直の場合
     *         false ダブル立直でない場合
     */
    public boolean isFirstAroundReady(){
        return options.contains(FIRST_AROUND_READY);
    }

    /**
     * 第一巡目での和了かどうかを取得します。
     * @return true 第一巡目での和了の場合
     *         false 第一巡目での和了でない場合
     */
    public boolean isFirstAroundWin(){
        return options.contains(FIRST_AROUND_WIN);
    }

    /**
     * 一発かどうかを取得します。
     * @return true 一発の場合
     *         false 一発でない場合
     */
    public boolean isReadyAroundWin(){
        return options.contains(READY_AROUND_WIN);
    }

    /**
     * 海底牌での和了かどうかを取得します。
     * @return true 海底牌での和了の場合
     *         false 海底牌での和了でない場合
     */
    public boolean isLastTileWin(){
        return options.contains(LAST_TILE_WIN);
    }

    /**
     * 槍槓和了かどうかを取得します。
     * @return true 槍槓和了の場合
     *         false 槍槓和了でない場合
     */
    public boolean isQuadTileWin(){
        return options.contains(QUAD_TILE_WIN);
    }

    /**
     * 嶺上牌ツモ和了かどうかを取得します。
     * @return true 嶺上牌ツモ和了の場合
     *         false 嶺上牌ツモ和了でない場合
     */
    public boolean isQuadTurnWin(){
        return options.contains(QUAD_TURN_WIN);
    }

}
