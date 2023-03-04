package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.util.Lists;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

/**
 * 面子ベース通常役クラス。
 * <p>通常役{@link BasicHandType}のうち,
 * 手牌の面子構成を確定させた後に初めて評価が可能な役を表します。
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
 * @version 1.0
 */
enum MeldBasedHandType implements BasicHandType{

    /**
     * 対々和
     */
    ALL_TRIPLES("対々和", 2){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return hand.getMelds().stream().noneMatch(Meld::isSequence);
        }
    },

    /**
     * 三暗刻
     */
    THREE_CONCEALED_TRIPLES("三暗刻", 2){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return hand.getMelds().stream()
                    .filter(Meld::isConcealed)
                    .filter(not(Meld::isSequence))
                    .count()==3;
        }
    },

    /**
     * 平和
     */
    NO_POINT("平和", 1){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return context.isSelfMade()
                    && hand.getHead().getHeadPoint(context.getSeatWind(), context.getRoundWind())
                    + hand.getMelds().stream().mapToInt(Meld::getMeldPoint).sum()
                    + hand.getWait().getWaitPoint()==0;
        }
    },

    /**
     * 混全帯么九(門前)
     */
    HALF_TERMINAL_SETS("混全帯么九", 2){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return context.isSelfMade()
                    && hand.getComponents().stream().allMatch(HandComponent::isOrphan)
                    && hand.getComponents().stream().anyMatch(HandComponent::isHonor)
                    && hand.getMelds().stream().anyMatch(Meld::isSequence);
        }
    },

    /**
     * 混全帯么九(食い下がり)
     */
    CALLED_HALF_TERMINAL_SETS("混全帯么九", 1){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return !context.isSelfMade()
                    && hand.getComponents().stream().allMatch(HandComponent::isOrphan)
                    && hand.getComponents().stream().anyMatch(HandComponent::isHonor)
                    && hand.getMelds().stream().anyMatch(Meld::isSequence);
        }
    },

    /**
     * 純全帯么九(門前)
     */
    FULL_TERMINAL_SETS("純全帯么九", 3){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return context.isSelfMade()
                    && hand.getComponents().stream().allMatch(HandComponent::isTerminal)
                    && hand.getMelds().stream().anyMatch(Meld::isSequence);
        }
    },

    /**
     * 純全帯么九(食い下がり)
     */
    CALLED_FULL_TERMINAL_SETS("純全帯么九", 2){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return !context.isSelfMade()
                    && hand.getComponents().stream().allMatch(HandComponent::isTerminal)
                    && hand.getMelds().stream().anyMatch(Meld::isSequence);
        }
    },

    /**
     * 一気通貫(門前)
     */
    FULL_STRAIGHTS("一気通貫", 2){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return context.isSelfMade()
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
        boolean test(FormattedHand hand, ScoringContext context){
            return !context.isSelfMade()
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
        boolean test(FormattedHand hand, ScoringContext context){
            return context.isSelfMade()
                    && Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds ->
                            melds.stream().allMatch(Meld::isSequence)
                                    && melds.stream().map(Meld::getTileType).distinct().count()==3
                                    && melds.stream().map(Meld::getFirst).mapToInt(Tile::suitNumber).distinct().count()==1);
        }
    },

    /**
     * 三色同順(食い下がり)
     */
    CALLED_THREE_COLOR_STRAIGHTS("三色同順", 1){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return !context.isSelfMade()
                    && Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds ->
                            melds.stream().allMatch(Meld::isSequence)
                                    && melds.stream().map(Meld::getTileType).distinct().count()==3
                                    && melds.stream().map(Meld::getFirst).mapToInt(Tile::suitNumber).distinct().count()==1);
        }
    },

    /**
     * 三色同刻
     */
    THREE_COLOR_TRIPLES("三色同刻", 2){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return Lists.combinationsOf(hand.getMelds(), 3)
                    .stream().anyMatch(melds ->
                            melds.stream().noneMatch(Meld::isSequence)
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
        boolean test(FormattedHand hand, ScoringContext context){
            return context.isSelfMade()
                    && Lists.combinationsOf(hand.getMelds(), 2).stream()
                    .filter(melds -> melds.stream().allMatch(Meld::isSequence)
                            && melds.get(0).getFirst().equalsIgnoreRed(melds.get(1).getFirst())).count()==1;
        }
    },

    /**
     * 二盃口
     */
    DOUBLE_DUAL_STRAIGHTS("二盃口", 3){
        @Override
        boolean test(FormattedHand hand, ScoringContext context){
            return context.isSelfMade()
                    && hand.getMelds().stream().allMatch(Meld::isSequence)
                    && hand.getMelds().stream().map(Meld::getFirst)
                    .collect(groupingBy(Tile::tileNumber))
                    .values().stream().allMatch(melds -> melds.size()==2);
        }
    };

    private static final List<MeldBasedHandType> CALL_SENSITIVE =
            List.of(HALF_TERMINAL_SETS, FULL_TERMINAL_SETS, FULL_STRAIGHTS, THREE_COLOR_STRAIGHTS);
    private final String name;
    private final int doubles;
    MeldBasedHandType(String name, int doubles){
        this.name = name;
        this.doubles = doubles;
    }

    /**
     * 適合する面子ベース通常役をすべてリスト形式で取得する。
     * @param hand    整形済み手牌
     * @param context 和了状況
     * @return 通常役のリスト
     */
    static List<BasicHandType> testAll(FormattedHand hand, ScoringContext context){
        return Stream.of(values())
                .filter(handType -> handType.test(hand, context))
                .collect(Collectors.<BasicHandType>toList());
    }

    /**
     * 与えられた整形済み手牌と和了状況でこの面子ベース通常役が成立するか検査します。
     * @param hand    整形済み手牌
     * @param context 和了状況
     * @return true 役が成立する場合
     *         false 役が成立しない場合
     */
    abstract boolean test(FormattedHand hand, ScoringContext context);

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String getUniqueName(){
        if(CALL_SENSITIVE.contains(this)){
            return "門前"+ name;
        }
        return name;
    }

    @Override
    public int getDoubles(){
        return doubles;
    }
}
