package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.Collection;
import java.util.List;

/**
 * 和了時の付帯状況を表すクラス。
 */
public class WinningSituation{
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
}
