package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 対局中のプレイヤー情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class PlayerData{
    private final String name;
    private final Wind initialSeatWind;
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
    public PlayerData(String name, Wind initialWind, Wind seatWind, int score, int rank){
        this.name = name;
        this.initialSeatWind = initialWind;
        this.seatWind = seatWind;
        this.score = score;
        this.rank = rank;
    }

    public String getName(){
        return name;
    }

    public Wind getInitialSeatWind(){
        return initialSeatWind;
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
        return "player("+name+" "+ initialSeatWind +" "+seatWind+" "+score+" "+rank+")";
    }
}
