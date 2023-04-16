package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;

import java.util.List;
import java.util.stream.Stream;

/**
 * 役満役クラス。
 *
 * <p>役{@link HandType}のうち、役満となる役。
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
 * @version 2.0
 */
public enum LimitHandType implements HandType{

    /**
     * 天和
     */
    BLESSING_OF_HEAVEN("天和", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isFirstAroundWin() && situation.isDealer()
                    && situation.isTsumo();
        }
    },

    /**
     * 地和
     */
    BLESSING_OF_EARTH("地和", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return situation.isFirstAroundWin() && !situation.isDealer()
                    && situation.isTsumo();
        }
    },

    /**
     * 国士無双
     */
    THIRTEEN_ORPHANS("国士無双", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0 && feature.getTileDistinctCount()==13
                    && feature.getOrphanCount()==14 && feature.getWinningTileCount()==1;
        }
    },

    /**
     * 国士無双十三面待ち
     */
    THIRTEEN_ORPHANS_13_WAIT("国士無双十三面", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0 && feature.getTileDistinctCount()==13
                    && feature.getOrphanCount()==14 && feature.getWinningTileCount()==2;
        }
    },

    /**
     * 九蓮宝燈
     */
    NINE_GATES("九蓮宝燈", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0 && feature.getQuadCount()==0
                    && feature.getSuitTypeCount()==1 && feature.getTileDistinctCount()==9
                    && feature.getHonorCount()==0
                    && feature.getTerminalCount()==(feature.getLargestDuplicationCount()==4? 7:6)
                    && feature.getWinningTileCount()%2==1;
        }
    },

    /**
     * 純正九蓮宝燈
     */
    NINE_GATES_9_WAIT("純正九蓮宝燈", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0 && feature.getQuadCount()==0
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
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getQuadCount()==4;
        }

        @Override
        Side getCompleterSide(List<Meld> openMelds){
            int count = 0;
            for(var meld:openMelds){
                if(meld.isQuad()){
                    if(++count==4){
                        if(meld.isCallQuad()){
                            return meld.getSourceSide();
                        }
                    }
                }
            }
            return Side.SELF;
        }
    },

    /**
     * 大三元
     */
    BIG_THREE("大三元", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getDragonCount()==9;
        }

        @Override
        Side getCompleterSide(List<Meld> openMelds){
            int count = 0;
            for(var meld:openMelds){
                if(meld.isDragon()){
                    if(++count==3){
                        return meld.getSourceSide();
                    }
                }
            }
            return Side.SELF;
        }
    },

    /**
     * 小四喜
     */
    SMALL_WIND("小四喜", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getWindCount()==11;
        }
    },

    /**
     * 大四喜
     */
    BIG_WIND("大四喜", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getWindCount()==12;
        }

        @Override
        Side getCompleterSide(List<Meld> openMelds){
            int count = 0;
            for(var meld:openMelds){
                if(meld.isWind()){
                    if(++count==4){
                        return meld.getSourceSide();
                    }
                }
            }
            return Side.SELF;
        }
    },

    /**
     * 字一色
     */
    ALL_HONORS("字一色", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getHonorCount()==14;

        }
    },

    /**
     * 清老頭
     */
    ALL_TERMINALS("清老頭", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getTerminalCount()==14;

        }
    },

    /**
     * 緑一色
     */
    ALL_GREENS("緑一色", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getGreenTileCount()==14;
        }
    },

    /**
     * 四暗刻
     */
    FOUR_CONCEALED_TRIPLES("四暗刻", 1){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && feature.getTileDistinctCount()==5
                    && feature.getLargestDuplicationCount()==3
                    && feature.getWinningTileCount()==3
                    && situation.isTsumo();
        }
    },

    /**
     * 四暗刻単騎待ち
     */
    FOUR_CONCEALED_TRIPLES_1_WAIT("四暗刻単騎", 2){
        @Override
        boolean test(HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && feature.getTileDistinctCount()==5
                    && feature.getLargestDuplicationCount()==3
                    && feature.getWinningTileCount()==2;
        }
    };

    private final String name;
    private final int multiplier;
    LimitHandType(String name, int multiplier){
        this.name = name;
        this.multiplier = multiplier;
    }

    /**
     * 成立する役満役をリスト形式で取得します。
     * <p>成立する役満役がない場合は空のリストが返されます。
     * @param feature 手牌の特徴量
     * @param situation 和了状況
     * @return 役満役のリスト
     */
    static List<HandType> testAll(HandFeature feature, WinningSituation situation){
        return Stream.of(values()).filter(handType -> handType.test(feature, situation)).map(HandType.class::cast).toList();
    }

    /**
     * 与えられた和了状況と手牌の特徴量でこの役が成立するか検査します。
     * @param feature 手牌の特徴量
     * @param situation 和了状況
     * @return true この役が与えられた和了状況で成立する場合
     *         false この役が与えられた和了状況で成立しない場合
     */
    abstract boolean test(HandFeature feature, WinningSituation situation);


    Side getCompleterSide(List<Meld> openMelds){
        return Side.SELF;
    }

    @Override
    public String getName(){
        return name;
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
        return multiplier;
    }
}
