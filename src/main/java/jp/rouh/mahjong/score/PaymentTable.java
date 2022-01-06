package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Wind;
import jp.rouh.util.FlexMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * プレイヤー間の支払い額を保持する精算テーブルクラス。
 * <h2>和了時の決済</h2>
 * <h3>和了手の精算</h3>
 * <p>和了時の決済では一般に, ロンの場合, 放銃者がその得点の全額を,
 * 親のツモの場合は, その他全ての子プレイヤーが各その得点の1/3を,
 * 子のツモの場合は, 親プレイヤーがその得点の1/2, その他の子プレイヤーが1/4を支払います。
 * <p>例外的なケースとして, 大明槓包及び役満包(大三元/大四喜/四槓子の完成責任払い)があります。
 * <p>大明槓包は, ツモ和了の場合でも, そのツモが大明槓による嶺上牌でのツモの時,
 * 大明槓元の牌を打牌したプレイヤーが放銃者と同等の扱いとなり, 全額を支払う責任が発生します。
 * 以下, 役満包の説明中に記述する放銃者には大明槓包のケースを含みます。
 * <p>役満包(大三元/大四喜/四槓子 責任払い)は, 面子系の役満の最後の面子を副露させた(役を完成させた)
 * プレイヤーが放銃者と同等の責任を追う規定です。例えば, 大三元の包では, 既に白および發の面子を
 * 副露しているプレイヤーに中の副露を許したプレイヤーは包が発生し, そのプレイヤーが大三元の手を
 * 和了した際に責任払いが生じます。
 * <p>役満包が発生した場合, ツモ和了の場合は包責任者が全額を,
 * ロン和了で, かつ包責任者と放銃者が一致する場合はそのプレイヤーが全額を,
 * ロン和了で, かつ包責任者と放銃者が一致しない場合は, 両プレイヤーが1/2を支払います。
 * <p>また, 役満包は完成させた役に対する支払責任のみとなるため, 複数の役満手の場合は
 * それらを一つ一つに分けて支払い額を決定する必要があります。
 * 例えば, 大三元および字一色の手が和了され, 南家が大三元の包責任者, 西家が放銃者である場合,
 * まず大三元について包責任者の南家と放銃者の西家がそれぞれ大三元の点数の1/2を支払います。
 * 次に字一色について, 包と無関係のため南家は支払いの責任はなく,
 * 西家は放銃者として字一色の点数の全額を支払います。
 * <p>さらに, 役満包は複数発生するケースが考えられます。
 * 大三元と四槓子の包が同時に発生したケース, および大四喜と四槓子の包が同時に発生したケースです。
 * 前者の具体的な例としては, 3度目の副露で大三元が確定し, 4度目の副露(大明槓)で四槓子が確定したケースです。
 * この場合も前述と同様, 各役満に対して包者および放銃者を考慮して支払い額を決定します
 * <p>支払額は, 100の位で切り上げになります。
 * このため, 和了手の得点と支払額の合計は必ずしも一致しないことになります。
 * 例えば, 子の7700点のツモについて, 親が1/2の3850点, 子が1/4の1925点を負担しますが,
 * 100の位で切り上げが発生するため, 親は3900点, 子2人は2000点を支払い, その合計額は7900点となります。
 * <h3>供託及び詰み符の精算</h3>
 * <p>和了者は和了手の決済で得た収入のほかに, 供託及びリーチ棒を取得します。
 * <p>詰み符は和了者が追加で得る本場数*300点の追加点です。
 * 和了手に対する支払い者が同様に詰み符の支払い責任を負います。
 * 和了手に対する支払い額は包の適用範囲や親の倍払いなどによって負担額は一律ではありませんが,
 * 詰み符の支払いは一律に同額を負担します。その際に100の位未満の端数が発生した場合は,
 * 和了手に対する支払い同様に切り上げた額を支払います。
 * 例えば, 1本場300点を包責任者と放銃者の二人が支払う場合,
 * 150点を切り上げた200点をそれぞれが支払います。
 * <p>ダブロンが発生した場合, 供託を二重に支給してしまうと
 * 開局時と終局時の得点の合計値が不一致となってしまうため注意が必要です。
 * 二度目の支払額を算出する際は{@link PaymentContext#getTotalDepositCount()}の値を0にする必要があります。
 * <h2>流局時の決済</h2>
 * <p>流局時はノーテン罰符の支払いが発生します。ノーテン罰符による点数移動は以下の通りです。
 * <ul>
 *   <li>全員がノーテンの場合, 点数移動は発生しません。</li>
 *   <li>聴牌のプレイヤーが一名の場合, ノーテンのプレイヤーが各1000点を支払い, 聴牌のプレイヤーが3000点を受け取ります。</li>
 *   <li>聴牌のプレイヤーが二名の場合, ノーテンのプレイヤーが各1500点を支払い, 聴牌のプレイヤーが各1500点を受け取ります。</li>
 *   <li>聴牌のプレイヤーが三名の場合, ノーテンのプレイヤーが3000点を支払い, 聴牌のプレイヤーが各1000点を受け取ります。</li>
 *   <li>全員が聴牌の場合, 点数移動は発生しません。</li>
 * </ul>
 * @author Rouh
 * @version 1.0
 */
public class PaymentTable{

    /**
     * 積み符1本あたり加点
     */
    private static final int STREAK_SCORE = 300;

    /**
     * 供託1本あたり加点
     */
    private static final int DEPOSIT_SCORE = 1000;

    private final Map<Wind, Integer> paymentMap;

    /**
     * 空の精算テーブルを生成します。
     */
    public PaymentTable(){
        this.paymentMap = FlexMap.of(Wind.class, 0);
    }

    /**
     * 指定された自風のスコア収支を取得します。
     * <p>点数を取得した場合は正の値, 点数を失った場合は負の値が返されます。
     * @param wind 自風
     * @return 収支額
     */
    public int paymentOf(Wind wind){
        return paymentMap.get(wind);
    }

    /**
     * 精算テーブルに和了結果を計上します。
     * <p>既に精算テーブルに値が設定されている場合は合算されます。
     * @param handScore 和了点数結果
     * @param context 精算コンテクスト
     */
    public void apply(HandScore handScore, PaymentContext context){
        var seatWind = context.getSeatWind();
        var responsiblePlayerWinds = new HashSet<Wind>();
        var winningTileSupplier = (Wind)null;
        if(!context.isTsumo()){
            //放銃責任
            winningTileSupplier = context.getWinningSide().of(seatWind);
            responsiblePlayerWinds.add(winningTileSupplier);
        }else if(context.isQuadTileDrawWin() && context.getQuadSide()!=Side.SELF){
            //大明槓責任
            winningTileSupplier = context.getQuadSide().of(seatWind);
            responsiblePlayerWinds.add(winningTileSupplier);
        }
        for(var subHandScore: handScore.disorganize()){
            //役満包責任
            var completingTileSupplier = (Wind)null;
            if(subHandScore.isHandLimit()){
                var handType = (LimitHandType)subHandScore.getHandTypes().get(0);
                var openMelds = context.getOpenMelds();
                switch(handType){
                    case BIG_THREE -> {
                        //大三元の構成面子がすべて公開面子にある場合
                        if(openMelds.stream().filter(Meld::isDragon).count()>=3){
                            var lastDragonMeld = IntStream.iterate(openMelds.size(), i->i - 1)
                                    .mapToObj(openMelds::get).filter(Meld::isDragon).findFirst().orElseThrow();
                            //大三元の最後の公開面子が他家からの副露である場合
                            if(lastDragonMeld.isCallQuad()){
                                completingTileSupplier = lastDragonMeld.getSourceSide().of(seatWind);
                                responsiblePlayerWinds.add(completingTileSupplier);
                            }
                        }
                    }
                    case BIG_WIND -> {
                        //大四喜の構成面子がすべて公開面子にある場合
                        if(openMelds.stream().filter(Meld::isWind).count()>=4){
                            //大四喜の最後の公開面子が他家からの副露である場合
                            if(context.getQuadSide()!=Side.SELF){
                                completingTileSupplier = context.getQuadSide().of(seatWind);
                                responsiblePlayerWinds.add(completingTileSupplier);
                            }
                        }
                    }
                    case FOUR_QUADS -> {
                        //四槓子の最後の公開面子が他家からの副露である場合
                        if(context.getQuadSide()!=Side.LEFT){
                            completingTileSupplier = context.getQuadSide().of(seatWind);
                            responsiblePlayerWinds.add(completingTileSupplier);
                        }
                    }
                }
            }
            int score = subHandScore.getScore();
            if(completingTileSupplier!=null){
                if(winningTileSupplier!=null){
                    //ロン 包あり or ツモ 大明槓あり 包あり
                    //包:放銃者:他=50:50:0
                    //包・放銃者:他:他=100:0:0
                    if(winningTileSupplier==completingTileSupplier){
                        paymentMap.merge(winningTileSupplier, -score, Integer::sum);
                        paymentMap.merge(seatWind, score, Integer::sum);
                    }else{
                        paymentMap.merge(winningTileSupplier, -1*ceil(score/2), Integer::sum);
                        paymentMap.merge(completingTileSupplier, -1*ceil(score/2), Integer::sum);
                        paymentMap.merge(seatWind, ceil(score/2)*2, Integer::sum);
                    }
                }else{
                    //ツモ 包あり
                    //包:他:他=100:0:0
                    paymentMap.merge(completingTileSupplier, -score, Integer::sum);
                    paymentMap.merge(seatWind, score, Integer::sum);
                }
            }else{
                if(winningTileSupplier!=null){
                    //ロン or 大明槓あり
                    //放銃者:他:他=100:0:0
                    paymentMap.merge(winningTileSupplier, -score, Integer::sum);
                    paymentMap.merge(seatWind, score, Integer::sum);
                }else{
                    //ツモ
                    //子:子:子=33:33:33
                    //親:子:子=50:25:25
                    if(context.isDealer()){
                        for(var side: Side.SELF.others()){
                            paymentMap.merge(side.of(seatWind), -1*ceil(score/3), Integer::sum);
                            paymentMap.merge(seatWind, ceil(score/3), Integer::sum);
                        }
                    }else{
                        for(var side: Side.SELF.others()){
                            if(side.of(seatWind)==Wind.EAST){
                                paymentMap.merge(side.of(seatWind), -1*ceil(score/2), Integer::sum);
                                paymentMap.merge(seatWind, ceil(score/2), Integer::sum);

                            }else{
                                paymentMap.merge(side.of(seatWind), -1*ceil(score/4), Integer::sum);
                                paymentMap.merge(seatWind, ceil(score/4), Integer::sum);
                            }
                        }
                    }
                }
            }
        }
        //供託・詰み符
        int depositScore = context.getTotalDepositCount()*DEPOSIT_SCORE;
        int streakScore = context.getRoundStreakCount()*STREAK_SCORE;
        paymentMap.merge(seatWind, depositScore, Integer::sum);
        if(responsiblePlayerWinds.isEmpty()){
            for(var side:Side.SELF.others()){
                paymentMap.merge(side.of(seatWind), -1*ceil(streakScore/3), Integer::sum);
                paymentMap.merge(seatWind, ceil(streakScore/3), Integer::sum);
            }
        }else{
            for(var responsiblePlayerWind:responsiblePlayerWinds){
                paymentMap.merge(responsiblePlayerWind, -1*ceil(streakScore/responsiblePlayerWinds.size()), Integer::sum);
                paymentMap.merge(seatWind, ceil(streakScore/responsiblePlayerWinds.size()), Integer::sum);
            }
        }
    }

    @Override
    public String toString(){
        return "PaymentTable "+paymentMap;
    }

    private static int ceil(int score){
        return (int)Math.ceil(score/100d)*100;
    }

    /**
     * 流局(荒廃平局)時の精算テーブルを取得します。
     * @param handReadyWinds 聴牌のプレイヤーの自風のリスト
     * @return 精算テーブル
     */
    public static PaymentTable ofDrawn(Collection<Wind> handReadyWinds){
        if(handReadyWinds.size()==0 || handReadyWinds.size()==4){
            //0, 0, 0, 0
            return new PaymentTable();
        }
        //+3000, -1000, -1000, -1000
        //+1500, +1500, -1500, -1500
        //+1000, +1000, +1000, -3000
        var table = new PaymentTable();
        int winnerScore = 3000/handReadyWinds.size();
        int loserScore = -1*(3000/(4 - handReadyWinds.size()));
        for(var wind: Wind.values()){
            table.paymentMap.put(wind, handReadyWinds.contains(wind)? winnerScore:loserScore);
        }
        return table;
    }
}
