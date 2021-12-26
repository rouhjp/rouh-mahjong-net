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
    private final int score;
    private final int rank;

    /**
     * プレイヤー情報DTOのコンストラクタ。
     * @param name 名前
     * @param tempSeatWind 仮風
     * @param score 持ち点
     */
    public PlayerTempData(String name, Wind tempSeatWind, int score){
        this.name = name;
        this.seatWind = tempSeatWind;
        this.score = score;
        this.rank = tempSeatWind.ordinal() + 1;
    }

    public String getName(){
        return name;
    }

    public Wind getSeatWind(){
        return seatWind;
    }

    public int getScore(){
        return score;
    }

    public int getRank(){
        return rank;
    }

    @Override
    public String toString(){
        return "player("+name+" "+seatWind+" "+score+" "+rank+")";
    }
}
