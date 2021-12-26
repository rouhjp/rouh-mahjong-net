package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 和了時の精算情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PaymentData {
    private Wind wind;
    private String name;
    private int scoreBefore;
    private int scoreAfter;
    private int rankBefore;
    private int rankAfter;
    public PaymentData(){

    }

    public void setWind(Wind wind){
        this.wind = wind;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setScoreBefore(int scoreBefore){
        this.scoreBefore = scoreBefore;
    }

    public void setScoreAfter(int scoreAfter){
        this.scoreAfter = scoreAfter;
    }

    public void setRankBefore(int rankBefore){
        this.rankBefore = rankBefore;
    }

    public void setRankAfter(int rankAfter){
        this.rankAfter = rankAfter;
    }

    public Wind getWind(){
        return wind;
    }

    public String getName(){
        return name;
    }

    public int getRankAfter(){
        return rankAfter;
    }

    public int getRankBefore(){
        return rankBefore;
    }

    public int getScoreAfter(){
        return scoreAfter;
    }

    public int getScoreBefore(){
        return scoreBefore;
    }

    public int getScoreApplied(){
        return scoreBefore - scoreAfter;
    }
}
