package jp.rouh.mahjong.game;

/**
 * 局パラメータ。
 * @author Rouh
 * @version 1.0
 */
class RoundParameter{
    private final RoundID roundId;
    private final int streakCount;
    private final int depositCount;
    private final boolean last;

    /**
     * 局パラメータのコンストラクタ。
     * @param roundId 局ID
     * @param streakCount 本場数
     * @param depositCount 供託数
     * @param last オーラスかどうか
     */
    RoundParameter(RoundID roundId, int streakCount, int depositCount, boolean last){
        this.roundId = roundId;
        this.streakCount = streakCount;
        this.depositCount = depositCount;
        this.last = last;
    }

    /**
     * 局IDを取得します。
     * @return 局ID
     */
    RoundID getRoundId(){
        return roundId;
    }

    /**
     * 本場数を取得します。
     * @return 本場数
     */
    int getStreakCount(){
        return streakCount;
    }

    /**
     * 供託数を取得します。
     * @return 供託数
     */
    int getDepositCount(){
        return depositCount;
    }

    /**
     * オーラスかどうかを取得します。
     * @return オーラスかどうか
     */
    boolean isLast(){
        return last;
    }
}
