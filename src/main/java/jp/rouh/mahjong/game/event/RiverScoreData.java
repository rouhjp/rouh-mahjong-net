package jp.rouh.mahjong.game.event;

/**
 * 流し満貫得点DTO
 * @author Rouh
 * @version 1.0
 */
public class RiverScoreData{
    private final String handTypeName;
    private final String handTypeGrade;
    private final String scoreExpression;

    public RiverScoreData(String handTypeName, String handTypeGrade, String scoreExpression){
        this.handTypeName = handTypeName;
        this.handTypeGrade = handTypeGrade;
        this.scoreExpression = scoreExpression;
    }

    public String getHandTypeName(){
        return handTypeName;
    }

    public String getHandTypeGrade(){
        return handTypeGrade;
    }

    public String getScoreExpression(){
        return scoreExpression;
    }
}
