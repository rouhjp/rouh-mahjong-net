package jp.rouh.mahjong.score;

import java.util.List;
import java.util.stream.Stream;

/**
 * 役満役クラス。
 * <p>役{@link HandType}インターフェースを実装します。
 * <p>該当する役は以下の通りです。
 * <ol><li><h2>環境役:手牌の構成牌に依存しない成立条件の役</h2>
 *         <ul><li>天和</li>
 *             <li>地和</li></ul></li>
 *     <li><h2>統一役:面子構成に依存しない成立条件の役</h2>
 *         <ul><li>緑一色</li>
 *             <li>字一色</li></ul></li>
 *     <li><h2>特殊役:特殊な形のため並べ替えパターンが発生し得ない役</h2>
 *         <ul><li>国士無双</li></ul></li>
 *     <li><h2>複数刻子役:成立条件の必然で複数並べ替えパターンが発生し得ない役</h2>
 *         <ul><li>四槓子</li>
 *             <li>大三元</li>
 *             <li>小四喜</li>
 *             <li>大四喜</li>
 *             <li>清老頭</li></ul></li>
 *     <li><h2>複数順子役:成立条件の必然で複数並べ替えパターンが発生し得ない役</h2>
 *         <ul><li>九蓮宝燈</li></ul></li>
 *     <li><h2>面子役:高点法上別の解釈パターンより優先される役</h2>
 *         <ul><li>四暗刻</li></ul></li></ol>
 * @author Rouh
 * @version 1.0
 */
public enum LimitHandType implements HandType{

    /**
     * 天和
     */
    BLESSING_OF_HEAVEN("天和", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isFirstAroundWin() && context.isDealer()
                    && context.isTsumo();
        }
    },

    /**
     * 地和
     */
    BLESSING_OF_EARTH("地和", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isFirstAroundWin() && !context.isDealer()
                    && context.isTsumo();
        }
    },

    /**
     * 国士無双
     */
    THIRTEEN_ORPHANS("国士無双", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isSelfMade() && feature.getTileDistinctCount()==13
                    && feature.getOrphanCount()==14 && feature.getWinningTileCount()==1;
        }
    },

    /**
     * 国士無双十三面待ち
     */
    THIRTEEN_ORPHANS13("国士無双十三面", 2){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isSelfMade() && feature.getTileDistinctCount()==13
                    && feature.getOrphanCount()==14 && feature.getWinningTileCount()==2;
        }
    },

    /**
     * 九蓮宝燈
     */
    NINE_GATES("九蓮宝燈", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isSelfMade() && feature.getQuadCount()==0
                    && feature.getSuitTypeCount()==1 && feature.getTileDistinctCount()==9
                    && feature.getHonorCount()==0
                    && feature.getTerminalCount()==(feature.getLargestDuplicationCount()==4? 7:6)
                    && feature.getWinningTileCount()%2==1;
        }
    },

    /**
     * 純正九蓮宝燈
     */
    NINE_GATES9("純正九蓮宝燈", 2){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isSelfMade() && feature.getQuadCount()==0
                    && feature.getSuitTypeCount()==1 && feature.getTileDistinctCount()==9
                    && feature.getHonorCount()==0
                    && feature.getTerminalCount()==(feature.getLargestDuplicationCount()==4? 7:6)
                    && feature.getWinningTileCount()%2==0;
        }
    },

    /**
     * 四槓子
     */
    FOUR_QUADS("四槓子", 2){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return feature.getQuadCount()==4;
        }
    },

    /**
     * 大三元
     */
    BIG_THREE("大三元", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return feature.getDragonCount()==9;
        }
    },

    /**
     * 小四喜
     */
    SMALL_WIND("小四喜", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return feature.getWindCount()==11;
        }
    },

    /**
     * 大四喜
     */
    BIG_WIND("大四喜", 2){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return feature.getWindCount()==12;

        }
    },

    /**
     * 字一色
     */
    ALL_HONORS("字一色", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return feature.getHonorCount()==14;

        }
    },

    /**
     * 清老頭
     */
    ALL_TERMINALS("清老頭", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return feature.getTerminalCount()==14;

        }
    },

    /**
     * 緑一色
     */
    ALL_GREENS("緑一色", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return feature.getGreenTileCount()==14;
        }
    },

    /**
     * 四暗刻
     */
    FOUR_CONCEALED_TRIPLES("四暗刻", 1){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isSelfMade()
                    && feature.getTileDistinctCount()==5
                    && feature.getLargestDuplicationCount()==3
                    && feature.getWinningTileCount()==3
                    && context.isTsumo();
        }
    },

    /**
     * 四暗刻単騎待ち
     */
    FOUR_CONCEALED_TRIPLES1("四暗刻単騎", 2){
        @Override
        boolean test(HandFeature feature, WinningContext context){
            return context.isSelfMade()
                    && feature.getTileDistinctCount()==5
                    && feature.getLargestDuplicationCount()==3
                    && feature.getWinningTileCount()==2;
        }
    };

    private final String text;
    private final int multiplier;

    LimitHandType(String text, int multiplier){
        this.text = text;
        this.multiplier = multiplier;
    }

    /**
     * 成立する役満役をリスト形式で取得します。
     * <p>成立する役満役がない場合は空のリストが返されます。
     * @param feature 手牌の特徴量
     * @param context 和了状況
     * @return 役満役のリスト
     */
    static List<LimitHandType> testAll(HandFeature feature, WinningContext context){
        return Stream.of(values()).filter(handType -> handType.test(feature, context)).toList();
    }

    /**
     * 与えられた和了状況と手牌の特徴量でこの役が成立するか検査します。
     * @param feature 手牌の特徴量
     * @param context 和了状況
     * @return true この役が与えられた和了状況で成立する場合
     *         false この役が与えられた和了状況で成立しない場合
     */
    abstract boolean test(HandFeature feature, WinningContext context);

    /**
     * 役満の倍数を取得します。
     * <p>例えばシングル役満であれば1を,
     * ダブル役満であれば2を返します。
     * @return 倍数
     */
    public int getMultiplier(){
        return multiplier;
    }

    @Override
    public String getText(){
        return text;
    }

    @Override
    public boolean isLimit(){
        return true;
    }

    @Override
    public HandTypeGrade getGrade(){
        return HandTypeGrade.ofLimit(multiplier);
    }
}
