package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * 得点を表すクラス。
 * <p>ある手牌に対する得点, 点数区分, 役を提供します。
 * 得点には詰み符や供託を含ません。
 * <p>例えば, 子の30符4飜の手は,
 * 基本点{@link #getBaseScore}が 30*2^(2 + 4) となり1920点,
 * 得点{@link #getScore}が 1920*4 を100の位で切り上げた7700点,
 * ロン時の放銃者の基本支払額が得点と同じ7700点,
 * ツモ時の親の基本支払額が 1920*2 を100の位で切り上げた3900点
 * ツモ時の子の基本支払額が 1920*1 を100の位で切り上げた2000点
 * となります。
 * @author Rouh
 * @version 1.0
 */
public class HandScore implements Comparable<HandScore>{
    private final int point;
    private final int doubles;
    private final List<HandType> handTypes;
    private final Limit limit;
    private final boolean handLimit;
    private final boolean dealer;

    private HandScore(FixedScoreHandType handType, boolean dealer){
        this.point = 0;
        this.doubles = 0;
        this.handTypes = List.of(handType);
        this.limit = handType.getFixedScore();
        this.handLimit = limit.isHandLimit();
        this.dealer = dealer;
    }

    private HandScore(List<LimitHandType> handTypes, boolean dealer){
        this.point = 0;
        int multiplier = handTypes.stream().mapToInt(LimitHandType::getMultiplier).sum();
        this.doubles = 13*multiplier;
        this.handTypes = List.copyOf(handTypes);
        this.limit = Limit.ofMultiplier(multiplier);
        this.handLimit = true;
        this.dealer = dealer;
    }

    private HandScore(int point, List<BasicHandType> handTypes, boolean dealer){
        this.point = point;
        this.doubles = handTypes.stream().mapToInt(BasicHandType::getDoubles).sum();
        this.handTypes = List.copyOf(handTypes);
        this.limit = Limit.of(point, doubles);
        this.handLimit = false;
        this.dealer = dealer;
    }

    /**
     * この得点が複数の役満で構成される場合, それぞれの役満の得点に分解します。
     * <p>この得点が役満(数え役満を除く)でない場合,
     * 分解不可能として, このオブジェクトの単体リストを返します。
     * <p>分解された得点オブジェクトの点数の合計値は
     * もとの得点オブジェクトの点数と一致します。
     * <p>この操作は, 包の点数計算のために用います。
     * 例えば, 包の発生した大四喜と, 同時に成立した包の対象外である字一色の手が
     * ツモ和了となった場合, 大四喜の点数のみ包による責任払いが発生します。
     * @return 分割した得点オブジェクトのリスト
     */
    List<HandScore> disorganize(){
        if(!handLimit) return List.of(this);
        return handTypes.stream()
                .map(handType->HandScore.ofLimit(List.of((LimitHandType)handType), dealer))
                .toList();
    }

    /**
     * 符を取得します。
     * <p>符は10の位で切り上げられた値です。
     * <p>役満(数え役満を含まない)の場合は0が返されます。
     * @return 符
     */
    public int getPoint(){
        return point;
    }

    /**
     * 飜を取得します。
     * <p>役満(数え役満を含まない)の場合は倍数に13をかけた値が返されます。
     * @return 飜
     */
    public int getDoubles(){
        return doubles;
    }

    /**
     * 基本点を取得します。
     * <p>基本点は, 符*(2^(飜 + 2))で算出される値です。
     * 符が10の位に切り上げられているため, 基本点は10の位までの計算精度を持ちます。
     * <p>基本点を子なら4倍, 親なら6倍して100の位で切り上げた値が得点となります。
     * @return 基本点
     */
    public int getBaseScore(){
        int doubles = getDoubles();
        if(handLimit){
            return 8000*(doubles/13);
        }else{
            if(doubles>=13) return 8000;
            if(doubles>=11) return 6000;
            if(doubles>=8) return 4000;
            if(doubles>=6) return 3000;
            if(doubles>=5) return 2000;
            return Math.min(2000, point*(int)Math.pow(2, doubles + 2));
        }
    }

    /**
     * 点数を取得します。
     * <p>この値は, 基本点から子なら4倍, 親なら6倍して100の位で切り上げた値を指します。
     * <p>ただし, 実際の支払いにおいては, 子なら基本点, 親なら基本点の二倍の数値を
     * それぞれ100の位で切り上げた額を負担するため, 必ずしも支払い額の合計が得点と合致するとは
     * 限りません。積み棒が無い場合, 一人の放銃者が全額を負担する際の額は得点と一致します。
     * @return 点数
     */
    public int getScore(){
        return (int)Math.ceil(((dealer? 6:4)*getBaseScore())/100d)*100;
    }

    /**
     * 点数区分を取得します。
     * @return 点数区分
     * @see Limit
     */
    public Limit getLimit(){
        return limit;
    }

    /**
     * 役をリスト形式で取得します。
     * @return 役のリスト
     */
    public List<HandType> getHandTypes(){
        return handTypes;
    }

    /**
     * 役満(数え役満を除く)かどうか検査します。
     * @return true 役満(数え役満を除く)の場合
     *         false 役満(数え役満を除く)でない場合
     */
    public boolean isHandLimit(){
        return handLimit;
    }

    /**
     * 役ナシかどうか検査します。
     * @return true 役ナシの場合
     *         false 役ナシでない場合
     */
    public boolean isEmpty(){
        return handTypes.isEmpty();
    }

    /**
     * 得点を表す文字列を取得します。
     * @return 得点を表す文字列
     */
    public String getScoreExpression(){
        return (handLimit?"":point+"符 "+doubles+"翻 ")
                + (limit.isEmpty()?"":limit.getText()+" ")+getScore()+"点";
    }

    @Override
    public int compareTo(HandScore o){
        return getBaseScore() - o.getBaseScore();
    }

    /**
     * 空の得点オブジェクトを生成します。
     * @return 空の得点
     */
    public static HandScore ofEmpty(){
        return new HandScore(0, List.of(), false);
    }

    /**
     * 得点オブジェクトを生成します。
     * <p>与えられる役のリストは順序が保持されるため, 呼び出し側で考慮する必要があります。
     * また, 重複がないことやドラのみの役とならないよう呼び出し側が保証する必要があります。
     * @param point     符
     * @param handTypes 通常役のリスト(順序が保持されます)
     * @param dealer    親かどうか
     * @return 得点
     */
    public static HandScore of(int point, List<BasicHandType> handTypes, boolean dealer){
        return new HandScore(point, handTypes, dealer);
    }

    /**
     * 役満(数え役満を除く)の得点オブジェクトを生成します。
     * @param limitHandTypes 役満役のリスト
     * @param dealer         親かどうか
     * @return 得点
     */
    public static HandScore ofLimit(List<LimitHandType> limitHandTypes, boolean dealer){
        return new HandScore(limitHandTypes, dealer);
    }

    /**
     * 流し満貫の得点オブジェクトを生成します。
     * @param dealer 親かどうか
     * @return 得点
     */
    public static HandScore ofRiverJackpot(boolean dealer){
        return new HandScore(FixedScoreHandType.RIVER_JACKPOT, dealer);
    }
}
