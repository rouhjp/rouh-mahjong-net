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
    private final List<Tile> handTiles;
    private final Tile winningTile;
    private final List<Meld> openMelds;
    private final List<Tile> upperIndicators;
    private final List<Tile> lowerIndicators;
    private final List<HandType> handTypes;
    private final String scoreExpression;
    private final boolean tsumo;

    private HandScoreData(Builder builder){
        this.handTiles = builder.handTiles;
        this.winningTile = builder.winningTile;
        this.openMelds = builder.openMelds;
        this.upperIndicators = builder.upperIndicators;
        this.lowerIndicators = builder.lowerIndicators;
        this.handTypes = builder.handTypes;
        this.scoreExpression = builder.scoreExpression;
        this.tsumo = builder.tsumo;
    }

    public static class Builder{
        private List<Tile> handTiles;
        private Tile winningTile;
        private List<Meld> openMelds;
        private List<Tile> upperIndicators;
        private List<Tile> lowerIndicators;
        private List<HandType> handTypes;
        private String scoreExpression;
        private boolean tsumo;

        public Builder withHandTiles(List<Tile> handTiles){
            this.handTiles = handTiles;
            return this;
        }

        public Builder withWinningTile(Tile winningTile){
            this.winningTile = winningTile;
            return this;
        }

        public Builder withOpenMelds(List<Meld> openMelds){
            this.openMelds = openMelds;
            return this;
        }

        public Builder withUpperIndicators(List<Tile> upperIndicators){
            this.upperIndicators = upperIndicators;
            return this;
        }

        public Builder withLowerIndicators(List<Tile> lowerIndicators){
            this.lowerIndicators = lowerIndicators;
            return this;
        }

        public Builder withHandTypes(List<HandType> handTypes){
            this.handTypes = handTypes;
            return this;
        }

        public Builder withScoreExpression(String scoreExpression){
            this.scoreExpression = scoreExpression;
            return this;
        }

        public Builder withTsumo(boolean tsumo){
            this.tsumo = tsumo;
            return this;
        }

        public HandScoreData build(){
            if(handTiles==null) throw new IllegalStateException("handTiles is null");
            if(winningTile==null) throw new IllegalStateException("winningTile is null");
            if(openMelds==null) throw new IllegalStateException("openMelds is null");
            if(upperIndicators==null) throw new IllegalStateException("upperIndicators is null");
            if(lowerIndicators==null) throw new IllegalStateException("lowerIndicators is null");
            if(handTypes==null) throw new IllegalStateException("handTypes is null");
            if(scoreExpression==null) throw new IllegalStateException("scoreExpression is null");
            return new HandScoreData(this);
        }

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
