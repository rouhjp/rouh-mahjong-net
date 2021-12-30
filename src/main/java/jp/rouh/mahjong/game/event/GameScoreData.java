package jp.rouh.mahjong.game.event;

public class GameScoreData{
    private final String name;
    private final int score;
    private final double resultPoint;
    public GameScoreData(String name, int score, double resultPoint){
        this.name = name;
        this.score = score;
        this.resultPoint = resultPoint;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }

    public double getResultPoint(){
        return resultPoint;
    }
}
