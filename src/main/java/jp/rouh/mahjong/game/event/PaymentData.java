package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 和了時の精算情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PaymentData{
    private Wind wind;
    private String name;
    private int scoreBefore;
    private int scoreAfter;
    private int rankBefore;
    private int rankAfter;

    public PaymentData(){
    }

    public Wind getWind(){
        return wind;
    }

    public void setWind(Wind wind){
        this.wind = wind;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getScoreBefore(){
        return scoreBefore;
    }

    public void setScoreBefore(int scoreBefore){
        this.scoreBefore = scoreBefore;
    }

    public int getScoreAfter(){
        return scoreAfter;
    }

    public void setScoreAfter(int scoreAfter){
        this.scoreAfter = scoreAfter;
    }

    public int getRankBefore(){
        return rankBefore;
    }

    public void setRankBefore(int rankBefore){
        this.rankBefore = rankBefore;
    }

    public int getRankAfter(){
        return rankAfter;
    }

    public void setRankAfter(int rankAfter){
        this.rankAfter = rankAfter;
    }
}
