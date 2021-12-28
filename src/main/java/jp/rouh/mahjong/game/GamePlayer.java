package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.PlayerTempData;
import jp.rouh.mahjong.game.event.ProfileData;
import jp.rouh.mahjong.tile.Wind;

/**
 * 対局プレイヤー。
 * @author Rouh
 * @version 1.0
 */
public class GamePlayer extends TableStrategyDelegator implements GamePlayerAccessor{
    private final GameAccessor game;
    private final String name;
    private final Wind orderWind;
    private int score = 25000;

    /**
     * 対局プレイヤーのコンストラクタ。
     * @param game 対局への参照
     * @param player 元のプレイヤー情報
     * @param orderWind 席順
     */
    GamePlayer(GameAccessor game, TablePlayer player, Wind orderWind){
        super(player.getStrategy());
        this.game = game;
        this.name = player.getName();
        this.orderWind = orderWind;
    }

    @Override
    public String getName(){
        return name;
    }

    Wind getOrderWind(){
        return orderWind;
    }

    @Override
    public Wind getSeatWindAt(int roundCount){
        return game.getInitialSeatWindAt(orderWind).shift(4 - roundCount);
    }

    @Override
    public int getScore(){
        return score;
    }

    @Override
    public int getRank(){
        return game.getRankOf(orderWind);
    }

    @Override
    public void applyScore(int score){
        this.score += score;
    }

    PlayerTempData getPlayerTempData(Wind seatWind){
        return new PlayerTempData(name, seatWind, score);
    }

    ProfileData getProfileData(){
        return new ProfileData(name);
    }
}
