package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.util.Lists;

import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

/**
 * 面子ベース通常役クラス。
 *
 * <p>通常役のうち, 手牌の面子構成を確定させた後に初めて評価が可能な役。
 * <p>該当する役は以下の通りです。
 * <ul>
 *   <li>対々和</li>
 *   <li>三暗刻</li>
 *   <li>平和</li>
 *   <li>混全帯么九</li>
 *   <li>純全帯么九</li>
 *   <li>一気通貫</li>
 *   <li>三色同順</li>
 *   <li>三色同刻</li>
 *   <li>一盃口</li>
 *   <li>二盃口</li>
 * </ul>
 * @author Rouh
 * @version 2.0
 */
enum MeldBasedHandType implements HandType{

    /**
     * 対々和
     */
    ALL_TRIPLES("対々和", 2){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return hand.getMelds().stream().noneMatch(Meld::isStraight);
        }
    },

    /**
     * 三暗刻
     */
    THREE_CONCEALED_TRIPLES("三暗刻", 2){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return hand.getMelds().stream()
                    .filter(Meld::isConcealed)
                    .filter(not(Meld::isStraight))
                    .count()==3;
        }
    },

    /**
     * 平和
     */
    NO_POINT("平和", 1){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && hand.getHead().getHeadPoint(situation.getSeatWind(), situation.getRoundWind()).getPoint()
                    + hand.getMelds().stream().map(Meld::getMeldPointType).mapToInt(PointType::getPoint).sum()
                    + hand.getWait().getWaitPointType().getPoint()==0;
        }
    },

    /**
     * 混全帯么九(門前)
     */
    HALF_TERMINAL_SETS("混全帯么九", 2){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && hand.getComponents().stream().allMatch(HandComponent::isOrphan)
                    && hand.getComponents().stream().anyMatch(HandComponent::isHonor)
                    && hand.getMelds().stream().anyMatch(Meld::isStraight);
        }
    },

    /**
     * 混全帯么九(食い下がり)
     */
    CALLED_HALF_TERMINAL_SETS("混全帯么九", 1){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()>0
                    && hand.getComponents().stream().allMatch(HandComponent::isOrphan)
                    && hand.getComponents().stream().anyMatch(HandComponent::isHonor)
                    && hand.getMelds().stream().anyMatch(Meld::isStraight);
        }
    },

    /**
     * 純全帯么九(門前)
     */
    FULL_TERMINAL_SETS("純全帯么九", 3){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && hand.getComponents().stream().allMatch(HandComponent::isTerminal)
                    && hand.getMelds().stream().anyMatch(Meld::isStraight);
        }
    },

    /**
     * 純全帯么九(食い下がり)
     */
    CALLED_FULL_TERMINAL_SETS("純全帯么九", 2){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()>0
                    && hand.getComponents().stream().allMatch(HandComponent::isTerminal)
                    && hand.getMelds().stream().anyMatch(Meld::isStraight);
        }
    },

    /**
     * 一気通貫(門前)
     */
    FULL_STRAIGHTS("一気通貫", 2){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds -> melds.stream().map(Meld::getTileType).distinct().count()==1
                            && melds.stream().map(Meld::getTilesSorted).flatMap(List::stream)
                            .mapToInt(Tile::suitNumber).distinct().count()==9);
        }
    },

    /**
     * 一気通貫(食い下がり)
     */
    CALLED_FULL_STRAIGHTS("一気通貫", 1){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()>0
                    && Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds -> melds.stream().map(Meld::getTileType).distinct().count()==1
                            && melds.stream().map(Meld::getTilesSorted).flatMap(List::stream)
                            .mapToInt(Tile::suitNumber).distinct().count()==9);
        }
    },

    /**
     * 三色同順(門前)
     */
    THREE_COLOR_STRAIGHTS("三色同順", 2){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds ->
                            melds.stream().allMatch(Meld::isStraight)
                                    && melds.stream().map(Meld::getTileType).distinct().count()==3
                                    && melds.stream().map(Meld::getFirst).mapToInt(Tile::suitNumber).distinct().count()==1);
        }
    },

    /**
     * 三色同順(食い下がり)
     */
    CALLED_THREE_COLOR_STRAIGHTS("三色同順", 1){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()>0
                    && Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds ->
                            melds.stream().allMatch(Meld::isStraight)
                                    && melds.stream().map(Meld::getTileType).distinct().count()==3
                                    && melds.stream().map(Meld::getFirst).mapToInt(Tile::suitNumber).distinct().count()==1);
        }
    },

    /**
     * 三色同刻
     */
    THREE_COLOR_TRIPLES("三色同刻", 2){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds ->
                            melds.stream().noneMatch(Meld::isStraight)
                                    && melds.stream().noneMatch(Meld::isHonor)
                                    && melds.stream().map(Meld::getTileType).distinct().count()==3
                                    && melds.stream().map(Meld::getFirst)
                                    .mapToInt(Tile::suitNumber).distinct().count()==1);
        }
    },

    /**
     * 一盃口
     */
    DUAL_STRAIGHTS("一盃口", 1){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && Lists.combinationsOf(hand.getMelds(), 2).stream()
                    .filter(melds -> melds.stream().allMatch(Meld::isStraight)
                            && melds.get(0).getFirst().equalsIgnoreRed(melds.get(1).getFirst())).count()==1;
        }
    },

    /**
     * 二盃口
     */
    DOUBLE_DUAL_STRAIGHTS("二盃口", 3){
        @Override
        boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation){
            return feature.getCallCount()==0
                    && hand.getMelds().stream().allMatch(Meld::isStraight)
                    && hand.getMelds().stream().map(Meld::getFirst)
                    .collect(groupingBy(Tile::tileNumber))
                    .values().stream().allMatch(melds -> melds.size()==2);
        }
    };

    private final String name;
    private final int doubles;
    MeldBasedHandType(String name, int doubles){
        this.name = name;
        this.doubles = doubles;
    }

    /**
     * 適合する面子ベース通常役をすべてリスト形式で取得する。
     * @param hand    整形済み手牌
     * @param situation 和了状況
     * @return 通常役のリスト
     */
    static List<HandType> testAll(FormattedHand hand, HandFeature feature, WinningSituation situation){
        return Stream.of(values()).filter(handType -> handType.test(hand, feature, situation)).map(HandType.class::cast).toList();
    }

    /**
     * 与えられた整形済み手牌と和了状況でこの面子ベース通常役が成立するか検査します。
     * @param hand    整形済み手牌
     * @param situation 和了状況
     * @return true 役が成立する場合
     *         false 役が成立しない場合
     */
    abstract boolean test(FormattedHand hand, HandFeature feature, WinningSituation situation);

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
