package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;

import java.util.List;

/**
 * 和了得点DTO。
 * @author Rouh
 * @version 1.0
 */
public class HandScoreData{
    private List<Tile> handTiles;
    private Tile winningTile;
    private List<List<Tile>> openMelds;
    private List<Side> meldTiltSides;
    private List<Tile> upperIndicators;
    private List<Tile> lowerIndicators;
    private List<String> handTypeNames;
    private List<String> handTypeGrades;
    private String scoreExpression;
    private boolean tsumo;

    public List<Tile> getHandTiles(){
        return handTiles;
    }

    public void setHandTiles(List<Tile> handTiles){
        this.handTiles = handTiles;
    }

    public Tile getWinningTile(){
        return winningTile;
    }

    public void setWinningTile(Tile winningTile){
        this.winningTile = winningTile;
    }

    public List<List<Tile>> getOpenMelds(){
        return openMelds;
    }

    public void setOpenMelds(List<List<Tile>> openMelds){
        this.openMelds = openMelds;
    }

    public List<Side> getMeldTiltSides(){
        return meldTiltSides;
    }

    public void setMeldTiltSides(List<Side> meldTiltSides){
        this.meldTiltSides = meldTiltSides;
    }

    public List<Tile> getUpperIndicators(){
        return upperIndicators;
    }

    public void setUpperIndicators(List<Tile> upperIndicators){
        this.upperIndicators = upperIndicators;
    }

    public List<Tile> getLowerIndicators(){
        return lowerIndicators;
    }

    public void setLowerIndicators(List<Tile> lowerIndicators){
        this.lowerIndicators = lowerIndicators;
    }

    public List<String> getHandTypeNames(){
        return handTypeNames;
    }

    public void setHandTypeNames(List<String> handTypeNames){
        this.handTypeNames = handTypeNames;
    }

    public List<String> getHandTypeGrades(){
        return handTypeGrades;
    }

    public void setHandTypeGrades(List<String> handTypeGrades){
        this.handTypeGrades = handTypeGrades;
    }

    public String getScoreExpression(){
        return scoreExpression;
    }

    public void setScoreExpression(String scoreExpression){
        this.scoreExpression = scoreExpression;
    }

    public boolean isTsumo(){
        return tsumo;
    }

    public void setTsumo(boolean tsumo){
        this.tsumo = tsumo;
    }
}
