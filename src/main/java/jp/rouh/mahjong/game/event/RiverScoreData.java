package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.score.HandType;

/**
 * 流し満貫得点DTO
 * @author Rouh
 * @version 1.0
 */
public class RiverScoreData{
    private final HandType handType;
    private final String scoreExpression;

    public RiverScoreData(HandType handType, String scoreExpression){
        this.handType = handType;
        this.scoreExpression = scoreExpression;
    }

    public HandType getHandType(){
        return handType;
    }

    public String getScoreExpression(){
        return scoreExpression;
    }
}
