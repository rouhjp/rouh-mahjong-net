package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.Wind;

/**
 * ゲームスパンを表すクラス。
 * @author Rouh
 * @version 1.0
 */
public class GameSpan{
    private RoundID lastRoundSign;
    private boolean extended = false;
    private GameSpan(RoundID lastRoundSign){
        this.lastRoundSign = lastRoundSign;
    }

    /**
     * このスパンの最初の局IDを取得します。
     * @return 局ID
     */
    public RoundID getFirstRoundSign(){
        return new RoundID(Wind.EAST, 1);
    }

    /**
     * 与えられた局がオーラスであるか検査します。
     * @param roundId 局ID
     * @return true オーラスの場合
     *         false オーラスでない場合
     */
    public boolean isLastRound(RoundID roundId){
        return lastRoundSign.equals(roundId);
    }

    /**
     * このスパンを延長します。
     */
    public void extend(){
        this.lastRoundSign = new RoundID(lastRoundSign.wind().next(), 4);
        this.extended = true;
    }

    /**
     * このスパンが延長されたものか検査します。
     * @return true 延長されている場合
     *         false 延長されていない場合
     */
    public boolean hasExtended(){
        return extended;
    }

    /**
     * 東風戦のスパンを取得します。
     * @return 東風戦のスパン
     */
    public static GameSpan ofEastGame(){
        return new GameSpan(new RoundID(Wind.EAST, 4));
    }

    /**
     * 半荘戦のスパンを取得します。
     * @return 半荘戦のスパン
     */
    public static GameSpan ofHalfGame(){
        return new GameSpan(new RoundID(Wind.SOUTH, 4));
    }

    /**
     * 一荘戦のスパンを取得します。
     * @return 一荘戦のスパン
     */
    public static GameSpan ofFullGame(){
        return new GameSpan(new RoundID(Wind.NORTH, 4));
    }

}
