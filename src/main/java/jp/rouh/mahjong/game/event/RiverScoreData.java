package jp.rouh.mahjong.game.event;

/**
 * 流し満貫得点DTO
 * @author Rouh
 * @version 1.0
 */
public class RiverScoreData{
    private String handTypeName;
    private String handTypeGrade;
    private String scoreExpression;

    public RiverScoreData(){
    }

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

    public void setHandTypeName(String handTypeName){
        this.handTypeName = handTypeName;
    }

    public void setHandTypeGrade(String handTypeGrade){
        this.handTypeGrade = handTypeGrade;
    }

    public void setScoreExpression(String scoreExpression){
        this.scoreExpression = scoreExpression;
    }
}
