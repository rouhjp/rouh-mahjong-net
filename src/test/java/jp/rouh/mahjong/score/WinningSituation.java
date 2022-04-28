package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

/**
 * 和了時の付帯状況を表すクラス。
 */
public class WinningSituation implements ScoringContext{
    private final Wind roundWind;
    private final Wind seatWind;
    private final List<Tile> upperPrisedTiles;
    private final List<Tile> lowerPrisedTiles;
    private final List<WinningCharacteristics> characteristics;


    WinningSituation(Wind round, Wind seat, List<Tile> upper, List<Tile> lower, List<WinningCharacteristics> characteristics){
        this.roundWind = round;
        this.seatWind = seat;
        this.upperPrisedTiles = upper;
        this.lowerPrisedTiles = lower;
        this.characteristics = characteristics;
        WinningCharacteristics.validate(characteristics);
    }

    @Override
    public Wind getRoundWind(){
        return roundWind;
    }

    @Override
    public Wind getSeatWind(){
        return seatWind;
    }

    @Override
    public boolean isTsumo(){
        return characteristics.contains(WinningCharacteristics.TSUMO);
    }

    @Override
    public boolean isSelfMade(){
        return characteristics.contains(WinningCharacteristics.CONCEALED);
    }

    @Override
    public boolean isReady(){
        return characteristics.contains(WinningCharacteristics.READY);
    }

    @Override
    public boolean isFirstAroundReady(){
        return characteristics.contains(WinningCharacteristics.FIRST_AROUND_READY);
    }

    @Override
    public boolean isFirstAroundWin(){
        return characteristics.contains(WinningCharacteristics.FIRST_AROUND_WIN);
    }

    @Override
    public boolean isReadyAroundWin(){
        return characteristics.contains(WinningCharacteristics.READY_AROUND_WIN);
    }

    @Override
    public boolean isLastTileGrabWin(){
        return characteristics.contains(WinningCharacteristics.LAST_TILE_CALL_WIN);
    }

    @Override
    public boolean isLastTileDrawWin(){
        return characteristics.contains(WinningCharacteristics.LAST_TILE_DRAW_WIN);
    }

    @Override
    public boolean isQuadTileGrabWin(){
        return characteristics.contains(WinningCharacteristics.QUAD_TILE_CALL_WIN);
    }

    @Override
    public boolean isQuadTileDrawWin(){
        return characteristics.contains(WinningCharacteristics.QUAD_TILE_DRAW_WIN);
    }

    @Override
    public List<Tile> getUpperPrisedTiles(){
        return upperPrisedTiles;
    }

    @Override
    public List<Tile> getLowerPrisedTiles(){
        return lowerPrisedTiles;
    }
}
