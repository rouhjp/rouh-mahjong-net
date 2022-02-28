package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 和了時の精算情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PaymentData{
    private final Wind wind;
    private final String name;
    private final int scoreBefore;
    private final int scoreAfter;
    private final int rankBefore;
    private final int rankAfter;
    private PaymentData(Builder builder){
        this.wind = builder.wind;
        this.name = builder.name;
        this.scoreBefore = builder.scoreBefore;
        this.scoreAfter = builder.scoreAfter;
        this.rankBefore = builder.rankBefore;
        this.rankAfter = builder.rankAfter;
    }

    public static class Builder{
        private Wind wind;
        private String name;
        private int scoreBefore;
        private int scoreAfter;
        private int rankBefore;
        private int rankAfter;

        public Builder withWind(Wind wind){
            this.wind = wind;
            return this;
        }

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withScoreBefore(int score){
            this.scoreBefore = score;
            return this;
        }

        public Builder withScoreAfter(int score){
            this.scoreAfter = score;
            return this;
        }

        public Builder withRankBefore(int rank){
            this.rankBefore = rank;
            return this;
        }

        public Builder withRankAfter(int rank){
            this.rankAfter = rank;
            return this;
        }

        public PaymentData build(){
            return new PaymentData(this);
        }
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
        return scoreAfter - scoreBefore;
    }
}
