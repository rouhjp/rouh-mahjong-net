package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 対局中のプレイヤー情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PlayerData{
    private String name;
    private Wind initialSeatWind;
    private Wind seatWind;
    private int score;
    private int rank;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Wind getInitialSeatWind(){
        return initialSeatWind;
    }

    public void setInitialSeatWind(Wind initialSeatWind){
        this.initialSeatWind = initialSeatWind;
    }

    public Wind getSeatWind(){
        return seatWind;
    }

    public void setSeatWind(Wind seatWind){
        this.seatWind = seatWind;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getRank(){
        return rank;
    }

    public void setRank(int rank){
        this.rank = rank;
    }
}
