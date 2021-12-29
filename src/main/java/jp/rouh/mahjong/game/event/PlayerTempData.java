package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 場決め時のプレイヤーの一時情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PlayerTempData{
    private final String name;
    private final Wind seatWind;

    /**
     * プレイヤー情報DTOのコンストラクタ。
     * @param name 名前
     * @param tempSeatWind 仮風
     */
    public PlayerTempData(String name, Wind tempSeatWind){
        this.name = name;
        this.seatWind = tempSeatWind;
    }

    public String getName(){
        return name;
    }

    public Wind getSeatWind(){
        return seatWind;
    }

    @Override
    public String toString(){
        return "player("+name+" "+seatWind+")";
    }
}
