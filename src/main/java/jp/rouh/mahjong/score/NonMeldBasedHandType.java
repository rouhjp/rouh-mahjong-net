package jp.rouh.mahjong.score;

import java.util.List;
import java.util.stream.Stream;

/**
 * 非面子ベース通常役クラス。
 *
 * <p>手牌の面子構成にかかわらず評価が可能な役。
 * <p>該当する役は以下の通りです。
 * <ol>
 *   <li>
 *     <h2>手牌構成牌に関わらず和了時の状況によって評価される役</h2>
 *     <ul>
 *       <li>立直</li>
 *       <li>両立直</li>
 *       <li>一発</li>
 *       <li>門前清自摸和</li>
 *       <li>海底摸月</li>
 *       <li>河底撈魚</li>
 *       <li>嶺上開花</li>
 *       <li>槍槓</li>
 *     </ul>
 *   </li>
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
 * @version 2.0
 */
enum NonMeldBasedHandType implements HandType{

    /**
     * 立直
     */
    READY("立直", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isReady() && !situation.isFirstAroundReady();
        }
    },

    /**
     * 両立直
     */
    DOUBLE_READY("両立直", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isFirstAroundReady();
        }
    },

    /**
     * 一発
     */
    ONE_SHOT("一発", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isReadyAroundWin();
        }
    },

    /**
     * 門前清自摸和
     */
    ALL_SELF_DRAW("門前清自摸和", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0 && situation.isTsumo();
        }
    },

    /**
     * 海底撈月
     */
    LAST_TILE_DRAW("海底摸月", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isLastTileWin() && situation.isTsumo();
        }
    },

    /**
     * 河底撈魚
     */
    LAST_TILE_GRAB("河底撈魚", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isLastTileWin() && !situation.isTsumo();
        }
    },

    /**
     * 嶺上開花
     */
    QUAD_DRAW("嶺上開花", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isQuadTurnWin();
        }
    },

    /**
     * 槍槓
     */
    QUAD_GRAB("槍槓", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isQuadTileWin();
        }
    },

    /**
     * 断么九
     */
    NO_ORPHANS("断么九", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getOrphanCount()==0;
        }
    },

    /**
     * 混一色
     */
    HALF_SINGLE_COLOR("混一色", 3){
        @Override
        public boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && feature.getHonorCount()>0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 混一色
     */
    CALLED_HALF_SINGLE_COLOR("混一色", 2){
        @Override
        public boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()>0
                    && feature.getHonorCount()>0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 清一色
     */
    FULL_SINGLE_COLOR("清一色", 6){
        @Override
        public boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && feature.getHonorCount()==0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 清一色
     */
    CALLED_FULL_SINGLE_COLOR("清一色", 5){
        @Override
        public boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()>0
                    && feature.getHonorCount()==0 && feature.getSuitTypeCount()==1;
        }
    },

    /**
     * 三槓子
     */
    THREE_QUADS("三槓子", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getQuadCount()==3;
        }
    },

    /**
     * 小三元
     */
    SMALL_THREE("小三元", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getDragonCount()==8;
        }
    },

    /**
     * 混老頭
     */
    HALF_TERMINALS("混老頭", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
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
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getDragonWhiteCount()==3;
        }
    },

    /**
     * 飜牌 發
     */
    DRAGON_GREEN("飜牌 發", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getDragonGreenCount()==3;
        }
    },

    /**
     * 飜牌 中
     */
    DRAGON_RED("飜牌 中", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getDragonRedCount()==3;
        }
    },

    /**
     * 自風牌
     */
    SEAT_WIND("自風牌", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getSeatWindCount()==3;
        }
    },

    /**
     * 場風牌
     */
    ROUND_WIND("場風牌", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getRoundWindCount()==3;
        }
    };

    private final String name;
    private final int doubles;
    NonMeldBasedHandType(String name, int doubles){
        this.name = name;
        this.doubles = doubles;
    }

    /**
     * 適合する環境ベース通常役をすべてリスト形式で取得する。
     * @param situation 和了状況
     * @return 通常役のリスト
     */
    static List<HandType> testAll(HandFeature feature, WinningSituation situation){
        return Stream.of(values()).filter(handType -> handType.test(feature, situation)).map(HandType.class::cast).toList();
    }

    /**
     * 与えられた和了状況でこの環境ベース通常役が成立するか検査します。
     * @param situation 和了状況
     * @return true 役が成立する場合
     *         false 役が成立しない場合
     */
    abstract boolean test(HandFeature feature, WinningSituation situation);

    @Override
    public String getName(){
        return name;
    }

    @Override
    public boolean isLimit(){
        return false;
    }

    @Override
    public int getDoubles(){
        return doubles;
    }

    @Override
    public int getLimitMultiplier(){
        return 0;
    }
}
