package jp.rouh.mahjong.game;

/**
 * 局の結果種別。
 * @author Rouh
 * @version 1.0
 */
public enum RoundResultType{

    /**
     * 親優位流局
     * <ul>
     *   <li>親が九種九牌した場合</li>
     *   <li>荒牌平局して親が聴牌の場合</li>
     * </ul>
     */
    DRAW_ADVANTAGE_DEALER,

    /**
     * 子優位流局
     * <ul>
     *   <li>子が九種九牌した場合</li>
     *   <li>九種九牌以外の途中流局が発生した場合</li>
     *   <li>荒牌平局して親がノーテンの場合</li>
     * </ul>
     */
    DRAW_ADVANTAGE_NON_DEALER,

    /**
     * 親和了
     * <ul>
     *   <li>親が役を和了した場合</li>
     *   <li>親が流し満貫をした場合</li>
     * </ul>
     */
    DEALER_VICTORY,

    /**
     * 子和了
     * <ul>
     *   <li>子が役を和了した場合</li>
     *   <li>子が流し満貫をした場合</li>
     * </ul>
     */
    NON_DEALER_VICTORY;

    /**
     * 親に優位がある場合かどうか検査します。
     * @return true 親優位の場合
     *         false 子優位の場合
     */
    boolean isDealerAdvantage(){
        return this==DEALER_VICTORY || this==DRAW_ADVANTAGE_DEALER;
    }

    /**
     * 子の和了かどうか検査します。
     * @return true 子の和了の場合
     *         false 子の和了でない場合
     */
    boolean isNonDealerVictory(){
        return this==NON_DEALER_VICTORY;
    }

    /**
     * 流局かどうか検査します。
     * @return true 流局の場合
     *         false 和了が発生した場合
     */
    boolean isDrawn(){
        return this==DRAW_ADVANTAGE_DEALER || this==DRAW_ADVANTAGE_NON_DEALER;
    }

}
