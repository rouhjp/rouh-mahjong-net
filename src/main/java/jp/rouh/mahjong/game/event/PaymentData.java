package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 和了時の精算情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PaymentData {
    private final Wind wind;
    private final String name;
    private final int scoreBefore;
    private final int scoreAfter;
    private final int rankBefore;
    private final int rankAfter;

    public PaymentData(Wind wind, String name, int scoreBefore, int scoreAfter, int rankBefore, int rankAfter){
        this.wind = wind;
        this.name = name;
        this.scoreBefore = scoreBefore;
        this.scoreAfter = scoreAfter;
        this.rankBefore = rankBefore;
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
