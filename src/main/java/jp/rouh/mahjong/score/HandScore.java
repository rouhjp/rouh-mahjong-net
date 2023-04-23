package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

/**
 * 得点クラス。
 *
 * <p>ある手牌に対する得点, 点数区分, 役を提供します。得点には詰み符や供託を含ません。
 * <p>例えば, 子の30符4飜の手は,
 * 基本点{@link #getBaseScore}が 30*2^(2 + 4) となり1920点,
 * 得点{@link #getScore}が 1920*4 を100の位で切り上げた7700点,
 * ロン時の放銃者の基本支払額が得点と同じ7700点,
 * ツモ時の親の基本支払額が 1920*2 を100の位で切り上げた3900点
 * ツモ時の子の基本支払額が 1920*1 を100の位で切り上げた2000点
 * となります。
 * @author Rouh
 * @version 2.0
 */
public class HandScore implements Comparable<HandScore>{
    private final int point;
    private final int doubles;
    private final LimitType limit;
    private final List<PointType> pointTypes;
    private final List<HandType> handTypes;
    private final List<Side> completerSides;
    private final Side supplierSide;
    private final Wind winnerWind;

    private HandScore(List<HandType> handTypes, List<PointType> pointTypes, Wind winnerWind, Side supplierSide){
        this.point = pointOf(pointTypes);
        this.doubles = handTypes.stream().mapToInt(HandType::getDoubles).sum();
        this.limit = LimitType.of(point, doubles);
        this.pointTypes = List.copyOf(pointTypes);
        this.handTypes = List.copyOf(handTypes);
        this.completerSides = List.of();
        this.supplierSide = supplierSide;
        this.winnerWind = winnerWind;
    }

    private HandScore(List<HandType> handTypes, Wind winnerWind, Side supplierSide, List<Side> completerSides){
        this.point = 0;
        this.doubles = 0;
        this.pointTypes = List.of();
        this.handTypes = List.copyOf(handTypes);
        this.limit = LimitType.ofMultiplier(handTypes.stream().mapToInt(HandType::getLimitMultiplier).sum());
        this.completerSides = List.copyOf(completerSides);
        this.supplierSide = supplierSide;
        this.winnerWind = winnerWind;
    }

    private HandScore(HandType handType, LimitType limit, Wind winnerWind){
        this.point = 0;
        this.doubles = 0;
        this.limit = limit;
        this.pointTypes = List.of();
        this.handTypes = List.of(handType);
        this.completerSides = List.of();
        this.supplierSide = Side.SELF;
        this.winnerWind = winnerWind;
    }

    private static int pointOf(List<PointType> pointTypes){
        int totalPoint = pointTypes.stream().mapToInt(PointType::getPoint).sum();
        if(totalPoint==25){
            //七対子25符の場合のみ切り上げ不要
            return totalPoint;
        }
        return (int)Math.ceil(totalPoint/10d)*10;
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
    List<HandScore> divide(){
        if(!isHandLimit()) return List.of(this);
        return IntStream.range(0, handTypes.size())
                .mapToObj(i->new HandScore(List.of(handTypes.get(i)), winnerWind, supplierSide, List.of(completerSides.get(i))))
                .toList();
    }

    /**
     * 役満包の方向を取得します。
     * <p>複合役満の場合は, {@code Side.SELF}を返します。
     * <p>複合役満の場合は, {@link #divide()}した後で呼び出す必要があります。
     * <p>役満包がない場合, {@code Side.SELF}を返します。
     * <p>役満でない場合, {@code Side.SELF}を返します。
     * @return SELF 役満でない場合
     *         SELF 複合役満の場合
     *         SELF 包が存在しない場合
     *         包の方向
     */
    Side getCompleterSide(){
        if(completerSides.size()==1){
            return completerSides.get(0);
        }
        return Side.SELF;
    }

    /**
     * 放銃者もしくは大明槓責任払いの方向を取得します。
     * <p>放銃者もしくは大明槓責任払いが存在しない場合は, {@code Side.SELF}を返します。
     * @return SELF 放銃者もしくは大明槓責任払いが存在しない場合
     *         放銃者もしくは大明槓責任払いの方向
     */
    Side getSupplierSide(){
        return supplierSide;
    }

    /**
     * 和了者の自風牌を取得します。
     * @return 和了者の自風牌
     */
    public Wind getWinnerWind(){
        return winnerWind;
    }

    /**
     * 和了者が親かどうか検査します。
     * @return true 和了者が親の場合
     *         false 和了者が子の場合
     */
    public boolean isDealer(){
        return winnerWind==Wind.EAST;
    }

    /**
     * 基本点を取得します。
     * <p>基本点は, 符*(2^(飜 + 2))で算出される値です。
     * 符が10の位に切り上げられているため, 基本点は10の位までの計算精度を持ちます。
     * <p>基本点を子なら4倍, 親なら6倍して100の位で切り上げた値が得点となります。
     * @return 基本点
     */
    public int getBaseScore(){
        if(!limit.isEmpty()){
            return limit.getBaseScore();
        }
        return Math.min(2000, point*(int)Math.pow(2, doubles + 2));
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
        return (int)Math.ceil(((isDealer()? 6:4)*getBaseScore())/100d)*100;
    }

    /**
     * 支払ベースの点数を取得します。
     * <p>ツモ和了などで支払の対象者が複数存在する場合は,
     * 点数を支払い対象者分に割ったのち, それらを足し合わせた点数となります。
     * <p>この時, 100点未満の端数が発生した場合切り上げとなるため,
     * 当初の点数と支払い点数の合計は異なる場合があります。
     * <p>例えば子が1300点を和了した場合, 400-700となり, 最終的な支払の合計は1500点となります。
     * <p>このメソッドでは, 最終的な支払の合計を取得します。
     * @return 支払ベース点数
     */
    public int getPaymentScore(){
        return divide().stream().mapToInt(handScore->{
            int score = handScore.getScore();
            int responsibleCount = (handScore.getCompleterSide()!=Side.SELF?1:0)
                    + (handScore.getSupplierSide()!=Side.SELF?1:0);
            if (responsibleCount==2){
                return ceil(score/2)*2;
            }
            if (responsibleCount==1){
                return score;
            }
            if (isDealer()){
                return ceil(score/3)*3;
            }
            return ceil(score/2) + ceil(score/4)*2;
        }).sum();
    }

    private static int ceil(int score){
        return (int)Math.ceil(score/100d)*100;
    }

    /**
     * 飜数を取得します。
     * @return 飜数
     */
    public int getDoubles(){
        return doubles;
    }

    /**
     * 符数を取得します。
     * @return 符
     */
    public int getPoint(){
        return point;
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
        return limit.isHandLimit();
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
        return (point>0?point+"符 "+doubles+"飜 ":"")
                + (limit.isEmpty()?"":limit.getName()+" ")
                + (getScore()+"点");
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
        int result;
        if((result = Integer.compare(getBaseScore(), o.getBaseScore()))!=0) return result;
        if((result = Integer.compare(doubles, o.doubles))!=0) return result;
        if((result = Integer.compare(point, o.point))!=0) return result;
        return Integer.compare(handTypes.size(), o.handTypes.size());
    }

    /**
     * 得点オブジェクトを生成します。
     * <p>与えられる役のリストは順序が保持されるため, 呼び出し側で考慮する必要があります。
     * また, 重複がないことやドラのみの役とならないよう呼び出し側が保証する必要があります。
     * @param pointTypes 符の詳細のリスト
     * @param handTypes 通常役のリスト(順序が保持されます)
     * @param winnerWind 和了者の自風牌
     * @param supplierSide 放銃者(もしくは大明槓責任払い)の方向
     * @return 得点
     * @throws IllegalArgumentException 役満の役を渡した場合
     */
    public static HandScore of(List<PointType> pointTypes, List<HandType> handTypes, Wind winnerWind, Side supplierSide){
        if(handTypes.stream().anyMatch(HandType::isLimit)){
            throw new IllegalArgumentException("limit hand type found: "+handTypes);
        }
        if(pointTypes.isEmpty()){
            throw new IllegalArgumentException("point types is empty");
        }
        return new HandScore(handTypes, pointTypes, winnerWind, supplierSide);
    }

    /**
     * 役満(数え役満を除く)の得点オブジェクトを生成します。
     * @param handTypes 役満役と役満包の方向(なければ{@code Side.SELF})のマップ
     * @param winnerWind 和了者の自風牌
     * @param supplierSide 放銃者(もしくは大明槓責任払い)の方向
     * @return 得点
     * @throws IllegalArgumentException 役満以外の役を渡した場合
     */
    public static HandScore ofHandLimit(List<HandType> handTypes, Wind winnerWind, Side supplierSide, List<Side> completerSides){
        if(handTypes.stream().anyMatch(not(HandType::isLimit))){
            throw new IllegalArgumentException("non limit hand found: "+handTypes);
        }
        return new HandScore(handTypes, winnerWind, supplierSide, completerSides);
    }

    /**
     * 流し満貫の得点オブジェクトを生成します。
     * @param winnerWind 和了者の自風牌
     * @return 得点
     */
    public static HandScore ofRiverLimit(Wind winnerWind){
        var handType = new HandType(){
            @Override
            public String getName(){
                return "流し満貫";
            }

            @Override
            public boolean isLimit(){
                return true;
            }

            @Override
            public int getDoubles(){
                return 0;
            }

            @Override
            public int getLimitMultiplier(){
                return 0;
            }
        };
        return new HandScore(handType, LimitType.LIMIT, winnerWind);
    }
}
