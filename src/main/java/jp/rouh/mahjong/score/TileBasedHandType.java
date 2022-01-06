package jp.rouh.mahjong.score;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 牌ベース通常役クラス。
 * <p>通常役{@link BasicHandType}のうち,
 * 手牌の面子構成にかかわらず評価が可能な役を表します。
 * <p>該当する役は以下の通りです。
 * <ol>
 *   <li>
 *     <h2>すべての手牌構成牌についての条件により成立する役</h2>
 *     <ul>
 *       <li>断么九</li>
 *       <li>混一色</li>
 *       <li>清一色</li>
 *       <li>混老頭</li>
 *     </ul>
 *   </li>
 *   <li>
 *     <h2>成立する場合, 面子構成が一意に確定する役</h2>
 *     <ul>
 *       <li>三槓子</li>
 *       <li>小三元</li>
 *     </ul>
 *   </li>
 *   <li>
 *     <h2>刻子単体で成立するため, いずれの面子構成でも成立する役</h2>
 *     <ul>
 *       <li>飜牌 白</li>
 *       <li>飜牌 發</li>
 *       <li>飜牌 中</li>
 *       <li>自風牌</li>
 *       <li>場風牌</li>
 *     </ul>
 *   </li>
 * </ol>
 * @author Rouh
 * @version 1.0
 */
public enum TileBasedHandType implements BasicHandType{

    /**
     * 断么九
     */
    NO_ORPHANS("断么九", 1){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getOrphanCount()==0;
        }
    },

    /**
     * 混一色
     */
    HALF_SINGLE_COLOR("混一色", 3){
        @Override
        public boolean test(HandFeature feature, ScoringContext context){
            return context.isSelfMade()
                    && feature.getHonorCount()>0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 混一色
     */
    CALLED_HALF_SINGLE_COLOR("混一色", 2){
        @Override
        public boolean test(HandFeature feature, ScoringContext context){
            return !context.isSelfMade()
                    && feature.getHonorCount()>0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 清一色
     */
    FULL_SINGLE_COLOR("清一色", 6){
        @Override
        public boolean test(HandFeature feature, ScoringContext context){
            return context.isSelfMade()
                    && feature.getHonorCount()==0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 清一色
     */
    CALLED_FULL_SINGLE_COLOR("清一色", 5){
        @Override
        public boolean test(HandFeature feature, ScoringContext context){
            return !context.isSelfMade()
                    && feature.getHonorCount()==0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 三槓子
     */
    THREE_QUADS("三槓子", 2){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getQuadCount()==3;
        }
    },

    /**
     * 小三元
     */
    SMALL_THREE("小三元", 2){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getDragonCount()==8;
        }
    },

    /**
     * 混老頭
     */
    HALF_TERMINALS("混老頭", 2){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getHonorCount()>0
                    && feature.getOrphanCount()==14
                    && feature.getTileDistinctCount()<=7;
        }
    },

    /**
     * 飜牌 白
     */
    DRAGON_WHITE("飜牌 白", 1){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getDragonWhiteCount()==3;
        }
    },

    /**
     * 飜牌 發
     */
    DRAGON_GREEN("飜牌 發", 1){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getDragonGreenCount()==3;
        }
    },

    /**
     * 飜牌 中
     */
    DRAGON_RED("飜牌 中", 1){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getDragonRedCount()==3;
        }
    },

    /**
     * 自風牌
     */
    SEAT_WIND("自風牌", 1){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getSeatWindCount()==3;
        }
    },

    /**
     * 場風牌
     */
    ROUND_WIND("場風牌", 1){
        @Override
        boolean test(HandFeature feature, ScoringContext context){
            return feature.getRoundWindCount()==3;
        }
    };

    private final String name;
    private final int doubles;

    TileBasedHandType(String name, int doubles){
        this.name = name;
        this.doubles = doubles;
    }

    /**
     * 適合する牌ベース通常役をすべてリスト形式で取得する。
     * @param feature 特徴量
     * @param context 和了状況
     * @return 通常役のリスト
     */
    static List<BasicHandType> testAll(HandFeature feature, ScoringContext context){
        return Stream.of(values())
                .filter(handType -> handType.test(feature, context))
                .collect(Collectors.<BasicHandType>toList());
    }

    /**
     * 与えられた和了状況と手牌の特徴量でこの牌ベース通常役が成立するか検査します。
     * @param feature 手牌の特徴量
     * @param context 和了状況
     * @return true この役が与えられた和了状況で成立する場合
     *         false この役が与えられた和了状況で成立しない場合
     */
    abstract boolean test(HandFeature feature, ScoringContext context);

    @Override
    public String getName(){
        return name;
    }

    @Override
    public int getDoubles(){
        return doubles;
    }

    @Override
    public boolean isLimit(){
        return false;
    }
}
