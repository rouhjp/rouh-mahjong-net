package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 場決め時のプレイヤーの一時情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PlayerTempData{
    private String name;
    private Wind seatWind;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Wind getSeatWind(){
        return seatWind;
    }

    public void setSeatWind(Wind seatWind){
        this.seatWind = seatWind;
    }
}
