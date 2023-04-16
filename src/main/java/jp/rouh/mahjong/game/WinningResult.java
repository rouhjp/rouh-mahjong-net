package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.HandScoreData;
import jp.rouh.mahjong.game.event.RiverScoreData;
import jp.rouh.mahjong.score.*;
import jp.rouh.mahjong.tile.Tile;

import java.util.List;

class WinningResult{
    private final HandScore handScore;
    private final List<Tile> handTiles;
    private final List<Meld> openMelds;
    private final Tile winningTile;
    private final WinningSituation situation;

    public WinningResult(HandScore handScore, List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, WinningSituation situation){
        this.handScore = handScore;
        this.handTiles = handTiles;
        this.openMelds = openMelds;
        this.winningTile = winningTile;
        this.situation = situation;
    }

    public HandScore getHandScore(){
        return handScore;
    }

    HandScoreData getHandScoreData(){
        var openMeldTiles = openMelds.stream().map(Meld::getTilesSorted).toList();
        var meldTiltSides = openMelds.stream().map(Meld::getSourceSide).toList();
        var handTypeNames = handScore.getHandTypes().stream().map(HandType::getName).toList();
        var handTypeGrades = handScore.getHandTypes().stream().map(HandType::getGradeCode).toList();

        var data = new HandScoreData();
        data.setHandTiles(handTiles);
        data.setWinningTile(winningTile);
        data.setOpenMelds(openMeldTiles);
        data.setMeldTiltSides(meldTiltSides);
        data.setUpperIndicators(situation.getUpperIndicators());
        data.setLowerIndicators(situation.getLowerIndicators());
        data.setHandTypeNames(handTypeNames);
        data.setHandTypeGrades(handTypeGrades);
        data.setScoreExpression(handScore.getScoreExpression());
        data.setTsumo(situation.isTsumo());
        return data;
    }

    RiverScoreData getRiverScoreData(){
        var handTypeNames = handScore.getHandTypes().get(0).getName();
        var handTypeGrade = handScore.getHandTypes().get(0).getGradeCode();

        var data = new RiverScoreData();
        data.setHandTypeName(handTypeNames);
        data.setHandTypeGrade(handTypeGrade);
        data.setScoreExpression(handScore.getScoreExpression());
        return data;
    }
}
