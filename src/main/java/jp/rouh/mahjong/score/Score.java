package jp.rouh.mahjong.score;

/**
 * 得点クラス。
 * @author Rouh
 * @version 1.0
 */
class Score{
    private final int point;
    private final int doubles;
    private final Limit limit;

    /**
     * 符と飜による得点のコンストラクタ。
     * @param point 符
     * @param doubles 飜
     * @throws IllegalArgumentException 符が20未満もしくは飜が0未満の場合
     */
    Score(int point, int doubles){
        if(point<20){
            throw new IllegalArgumentException("invalid point: less than 20 point="+point);
        }
        if(doubles<0){
            throw new IllegalArgumentException("invalid doubles: negative point="+point);
        }
        this.point = point;
        this.doubles = doubles;
        this.limit = Limit.of(point, doubles);
    }

    /**
     * 固定得点のコンストラクタ。
     * @param limit 点数区分
     */
    Score(Limit limit){
        this.point = 0;
        this.doubles = 0;
        this.limit = limit;
    }

    /**
     * 役満の得点のコンストラクタ。
     * @param multiplier 倍数
     */
    Score(int multiplier){
        this.point = 0;
        this.doubles = multiplier*13;
        this.limit = Limit.ofMultiplier(multiplier);
    }

    /**
     * 符及び飜が定義されたスコアかどうか検査します。
     * @return true 役満(数え役満を除く)でない場合
     *         false 役満(数え役満を除く)の場合, 固定得点(流し満願)の場合
     */
    public boolean hasPointAndDoubles(){
        return point!=0;
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
     * 点数区分を取得します。
     * @return 点数区分
     * @see Limit
     */
    public Limit getLimit(){
        return limit;
    }

    /**
     * 基本点を取得します。
     * <p>基本点は, 符*(2^(飜 + 2))で算出される値です。
     * 符が10の位に切り上げられているため, 基本点は10の位までの計算精度を持ちます。
     * <p>基本点を子なら4倍, 親なら6倍して100の位で切り上げた値が得点となります。
     * @return 基本点
     */
    int getBaseScore(){
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
     * @param dealer 親かどうか
     * @return 点数
     */
    int getScore(boolean dealer){
        return (int)Math.ceil(((dealer? 6:4)*getBaseScore())/100d)*100;
    }
}
