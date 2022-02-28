package jp.rouh.mahjong.game.event;

/**
 * 対局結果点数DTO。
 * @author Rouh
 * @version 1.0
 */
public class GameScoreData{
    private String name;
    private int score;
    private double resultPoint;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public double getResultPoint(){
        return resultPoint;
    }

    public void setResultPoint(double resultPoint){
        this.resultPoint = resultPoint;
    }
}
