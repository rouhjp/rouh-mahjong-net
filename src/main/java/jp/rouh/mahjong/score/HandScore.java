package jp.rouh.mahjong.score;

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
    private final Score score;
    private final List<HandType> handTypes;
    private final List<PointType> pointTypes;
    private final boolean handLimit;
    private final boolean dealer;

    private HandScore(FixedScoreHandType handType, boolean dealer){
        this.score = new Score(handType.getFixedLimit());
        this.handTypes = List.of(handType);
        this.pointTypes = List.of();
        this.handLimit = score.getLimit().isHandLimit();
        this.dealer = dealer;
    }

    private HandScore(List<LimitHandType> handTypes, boolean dealer){
        int multiplier = handTypes.stream().mapToInt(LimitHandType::getMultiplier).sum();
        this.score = new Score(multiplier);
        this.handTypes = List.copyOf(handTypes);
        this.pointTypes = List.of();
        this.handLimit = true;
        this.dealer = dealer;
    }

    private HandScore(List<PointType> pointTypes, List<BasicHandType> handTypes, boolean dealer){
        int doubles = handTypes.stream().mapToInt(BasicHandType::getDoubles).sum();
        int point = pointOf(pointTypes);
        this.score = new Score(point, doubles);
        this.handTypes = List.copyOf(handTypes);
        this.pointTypes = List.copyOf(pointTypes);
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
     * 基本点を取得します。
     * <p>基本点は, 符*(2^(飜 + 2))で算出される値です。
     * 符が10の位に切り上げられているため, 基本点は10の位までの計算精度を持ちます。
     * <p>基本点を子なら4倍, 親なら6倍して100の位で切り上げた値が得点となります。
     * @return 基本点
     */
    public int getBaseScore(){
        return score.getBaseScore();
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
        return score.getScore(dealer);
    }

    public int getDoubles(){
        return score.getDoubles();
    }

    public int getPoint(){
        return score.getPoint();
    }

    /**
     * 役をリスト形式で取得します。
     * @return 役のリスト
     */
    public List<HandType> getHandTypes(){
        return handTypes;
    }

    /**
     * 符の詳細をリスト形式で取得します。
     * @return 符の詳細のリスト
     */
    public List<PointType> getPointTypes(){
        return pointTypes;
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
        return (score.hasPointAndDoubles()?score.getPoint()+"符 "+score.getDoubles()+"翻 ":"")
                + (score.getLimit().isEmpty()?"":score.getLimit().getText()+" ")+getScore()+"点";
    }

    /**
     * {@inheritDoc}
     * <p>点数の高さを元に比較します。
     * <p>点数が同一の場合, 飜数, 符数, 役の数の順に比較を行います。
     * @param o 比較対象
     * @return 比較結果
     */
    @Override
    public int compareTo(HandScore o){
        int comparingScore = Integer.compare(getBaseScore(), o.getBaseScore());
        if(comparingScore!=0) return comparingScore;
        int comparingDoubles = Integer.compare(score.getDoubles(), o.score.getDoubles());
        if(comparingDoubles!=0) return comparingDoubles;
        int comparingPoint = Integer.compare(score.getPoint(), o.score.getPoint());
        if(comparingPoint!=0) return comparingPoint;
        return Integer.compare(handTypes.size(), o.handTypes.size());
    }

    private static int pointOf(List<PointType> pointTypes){
        var totalPoint = pointTypes.stream().mapToInt(PointType::getPoint).sum();
        //七対子25符固定の場合は切り上げ不要
        if(totalPoint==25) return totalPoint;
        return (int)Math.ceil(totalPoint/10d)*10;
    }

    /**
     * 得点オブジェクトを生成します。
     * <p>与えられる役のリストは順序が保持されるため, 呼び出し側で考慮する必要があります。
     * また, 重複がないことやドラのみの役とならないよう呼び出し側が保証する必要があります。
     * @param pointTypes 符の詳細のリスト
     * @param handTypes 通常役のリスト(順序が保持されます)
     * @param dealer    親かどうか
     * @return 得点
     */
    public static HandScore of(List<PointType> pointTypes, List<BasicHandType> handTypes, boolean dealer){
        return new HandScore(pointTypes, handTypes, dealer);
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
