package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.TableStrategyDelegator;
import jp.rouh.mahjong.tile.Wind;

/**
 * 対局プレイヤー。
 * @author Rouh
 * @version 1.0
 */
class GamePlayer extends TableStrategyDelegator implements GamePlayerAccessor{
    private final GameAccessor game;
    private final String name;
    private final Wind initialSeatWind;
    private int score;

    /**
     * 対局プレイヤーのコンストラクタ。
     * @param game 対局への参照
     * @param player 元のプレイヤー情報
     * @param initialSeatWind 対局開始時の自風
     */
    GamePlayer(GameAccessor game, Player player, Wind initialSeatWind){
        super(player.getStrategy());
        this.game = game;
        this.name = player.getName();
        this.initialSeatWind = initialSeatWind;
        this.score = game.getDefaultScore();
    }

    @Override
    public String getName(){
        return name;
    }

    public Wind getInitialSeatWind(){
        return initialSeatWind;
    }

    @Override
    public Wind getSeatWindAt(int roundCount){
        return initialSeatWind.shift(4 - roundCount);
    }

    @Override
    public int getScore(){
        return score;
    }

    @Override
    public int getRank(){
        return game.getRankOf(this);
    }

    @Override
    public void applyScore(int score){
        this.score += score;
    }
}
