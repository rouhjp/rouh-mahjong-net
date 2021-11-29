package jp.rouh.mahjong.score;

/**
 * 点数が固定の特殊な役クラス。
 * <p>流し満貫が該当します。
 * @author Rouh
 * @version 1.0
 */
enum FixedScoreHandType implements HandType{

    /**
     * 流し満貫
     */
    RIVER_JACKPOT("流し満貫", Limit.JACKPOT);

    private final String text;
    private final Limit limit;

    FixedScoreHandType(String text, Limit limit){
        this.text = text;
        this.limit = limit;
    }

    @Override
    public String getText(){
        return text;
    }

    @Override
    public boolean isLimit(){
        return false;
    }

    @Override
    public HandTypeGrade getGrade(){
        return HandTypeGrade.UNDEFINED;
    }

    /**
     * この役により成立する点数区分を取得します。
     * @return 点数区分
     */
    Limit getFixedScore(){
        return limit;
    }
}
