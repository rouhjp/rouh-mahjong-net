package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 対局中のプレイヤー情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PlayerData{
    private final String name;
    private final Wind orderWind;
    private final Wind seatWind;
    private final int score;
    private final int rank;

    /**
     * プレイヤー情報DTOのコンストラクタ。
     * @param name 名前
     * @param seatWind 風
     * @param score 持ち点
     * @param rank ランク
     */
    public PlayerData(String name, Wind orderWind, Wind seatWind, int score, int rank){
        this.name = name;
        this.orderWind = orderWind;
        this.seatWind = seatWind;
        this.score = score;
        this.rank = rank;
    }

    public String getName(){
        return name;
    }

    public Wind getOrderWind(){
        return orderWind;
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
}
