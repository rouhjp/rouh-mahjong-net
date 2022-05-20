package jp.rouh.mahjong.bot;

/**
 * 打牌ボットのパフォーマンスを確認する手動テストクラス。
 * @author Rouh
 * @version 1.0
 */
public class BotManualTest{
    public static void main(String[] args){
        new BotPerformanceTester(StandardDiscardingBot.READY_SCORE_MAXIMIZER).testPlay(25);
        new BotPerformanceTester(StandardDiscardingBot.MIDDLE_SUIT_COLLECTOR).testPerformance(100, 30);
        new BotPerformanceTester(StandardDiscardingBot.READY_SCORE_MAXIMIZER).testPerformance(100, 30);
    }
}
