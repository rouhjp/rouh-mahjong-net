package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.score.HandType;
import jp.rouh.mahjong.score.Meld;
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
    private List<Meld> openMelds;
    private List<Tile> upperIndicators;
    private List<Tile> lowerIndicators;
    private List<HandType> handTypes;
    private String scoreExpression;
    private boolean tsumo;

    public void setHandTiles(List<Tile> handTiles){
        this.handTiles = handTiles;
    }

    public void setWinningTile(Tile winningTile){
        this.winningTile = winningTile;
    }

    public void setOpenMelds(List<Meld> openMelds){
        this.openMelds = openMelds;
    }

    public void setUpperIndicators(List<Tile> upperIndicators){
        this.upperIndicators = upperIndicators;
    }

    public void setLowerIndicators(List<Tile> lowerIndicators){
        this.lowerIndicators = lowerIndicators;
    }

    public void setHandTypes(List<HandType> handTypes){
        this.handTypes = handTypes;
    }

    public void setScoreExpression(String scoreExpression){
        this.scoreExpression = scoreExpression;
    }

    public void setTsumo(boolean tsumo){
        this.tsumo = tsumo;
    }

    public List<Tile> getHandTiles(){
        return handTiles;
    }

    public Tile getWinningTile(){
        return winningTile;
    }

    public List<Meld> getOpenMelds(){
        return openMelds;
    }

    public List<Tile> getUpperIndicators(){
        return upperIndicators;
    }

    public List<Tile> getLowerIndicators(){
        return lowerIndicators;
    }

    public List<HandType> getHandTypes(){
        return handTypes;
    }

    public String getScoreExpression(){
        return scoreExpression;
    }

    public boolean isTsumo(){
        return tsumo;
    }
}
