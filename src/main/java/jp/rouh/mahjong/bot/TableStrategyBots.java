package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.game.event.*;

/**
 * NPCテーブル戦略。
 * @author Rouh
 * @version 1.0
 */
public final class TableStrategyBots{
    private TableStrategyBots(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 最速で立直を目指す戦略のインスタンスを取得します。
     * @return テーブル戦略
     */
    public static TableStrategy newReadyBot(){
        return new StrategyBotAdapter(StandardDiscardingBot.READY_SCORE_MAXIMIZER);
    }

}
