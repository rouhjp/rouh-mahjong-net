package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 標準点数計算クラス。
 *
 * @author Rouh
 * @version 1.0
 */
public final class StandardHandScoreCalculator implements HandScoreCalculator{
    private static final Logger LOG = LoggerFactory.getLogger(StandardHandScoreCalculator.class);

    public StandardHandScoreCalculator(){
    }

    @Override
    public HandScore calculate(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, WinningSituation situation){
        if(handTiles.size() + openMelds.size()*3 >13){
            throw new IllegalArgumentException("invalid size of hand: "+handTiles+" meld: "+openMelds);
        }
        LOG.debug("--start calculating score--");
        LOG.debug(handTiles + " " + openMelds + " " + winningTile);
        var feature = new HandFeature(handTiles, openMelds, winningTile, situation);
        var limitHandTypes = LimitHandType.testAll(feature, situation);
        if(!limitHandTypes.isEmpty()){
            var completerSides = limitHandTypes.stream().map(handType->((LimitHandType)handType).getCompleterSide(openMelds)).toList();
            return HandScore.ofHandLimit(limitHandTypes, situation.getSeatWind(), situation.getSupplierSide(), completerSides);
        }
        var nonMeldBaseHandTypes = NonMeldBasedHandType.testAll(feature, situation);
        var prisedTileHandTypes = prisedTileHandTypes(feature);
        var handScores = new ArrayList<HandScore>();
        if(HandTiles.isCompletedSevenPairs(handTiles, winningTile)){
            var pointTypes = List.of(PointType.SEVEN_PAIR_BASE);
            var handTypes = new ArrayList<>(nonMeldBaseHandTypes);
            handTypes.add(handTypeOf("七対子", 2));
            handTypes.addAll(prisedTileHandTypes);
            handScores.add(HandScore.of(pointTypes, handTypes, situation.getSeatWind(), situation.getSupplierSide()));
            LOG.debug("case--> Irregular");
            for(var handType: handTypes){
                LOG.debug("        " + handType.getName());
            }
        }
        var formattedHands = format(handTiles, openMelds, winningTile, situation);
        for(var formattedHand: formattedHands){
            var pointTypes = pointTypesOf(formattedHand, feature, situation);
            var meldBasedHandTypes = MeldBasedHandType.testAll(formattedHand, feature, situation);
            var handTypes = new ArrayList<HandType>();
            handTypes.addAll(nonMeldBaseHandTypes);
            handTypes.addAll(meldBasedHandTypes);
            if(!handTypes.isEmpty()){
                handTypes.addAll(prisedTileHandTypes);
            }
            handScores.add(HandScore.of(pointTypes, handTypes, situation.getSeatWind(), situation.getSupplierSide()));
            LOG.debug("case--> " + formattedHand);
            for(var handType: handTypes){
                LOG.debug("        " + handType.getName());
            }
        }
        if(handScores.isEmpty()){
            LOG.debug("        hand is not completed");
            LOG.debug("--end calculating score--");
            throw new IllegalArgumentException("hand is not completed: "+handTiles);
        }
        var score = handScores.stream().max(Comparator.naturalOrder()).get();
        LOG.debug("result: " + score.getScoreExpression());
        for(var handType: score.getHandTypes()){
            LOG.debug(handType.getName());
        }
        LOG.debug("--end calculating score--");
        return score;
    }

    /**
     * 手牌を複数の整形済み手牌にフォーマットします。
     * <p>手牌が七対子もしくは完成形でない場合は空のセットが返されます。
     * @param handTiles   純手牌
     * @param openMelds   公開面子
     * @param winningTile 和了牌
     * @param situation   和了状況
     * @return 整形済み手牌のセット
     */
    private static Set<FormattedHand> format(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, WinningSituation situation){
        var formattedHands = new HashSet<FormattedHand>();
        for(var arranged: HandTiles.arrangeAll(handTiles, winningTile)){
            var head = new Head(arranged.get(0));
            var tail = arranged.subList(1, arranged.size());
            if(head.containsIgnoreRed(winningTile)){
                var wait = Wait.SINGLE_HEAD;
                var melds = new ArrayList<Meld>();
                for(var meldTiles:tail){
                    melds.add(Meld.ofHand(meldTiles));
                }
                melds.addAll(openMelds);
                formattedHands.add(new FormattedHand(head, melds, wait));
            }
            for(int i = 0; i<tail.size(); i++){
                var targetTile = tail.get(i).stream().filter(t->t.equalsIgnoreRed(winningTile)).findFirst();
                if(targetTile.isPresent()){
                    var melds = new ArrayList<Meld>(4);
                    for(int k = 0; k<tail.size(); k++){
                        if(!situation.isTsumo() && k==i){
                            var base = new ArrayList<>(tail.get(k));
                            base.remove(targetTile.get());
                            melds.add(Meld.ofHand(base, targetTile.get()));
                        }else{
                            melds.add(Meld.ofHand(tail.get(k)));
                        }
                    }
                    melds.addAll(openMelds);
                    var wait = Wait.of(melds.get(i), winningTile);
                    formattedHands.add(new FormattedHand(head, melds, wait));
                }
            }
        }
        return formattedHands;
    }

    private static List<PointType> pointTypesOf(FormattedHand hand, HandFeature feature, WinningSituation situation){
        var pointTypes = new ArrayList<PointType>();
        pointTypes.add(hand.getWait().getWaitPointType());
        pointTypes.add(hand.getHead().getHeadPoint(situation.getSeatWind(), situation.getRoundWind()));
        pointTypes.addAll(hand.getMelds().stream().map(Meld::getMeldPointType).toList());
        boolean concealedNoPoint = pointTypes.isEmpty() && feature.getCallCount()==0;
        boolean calledNoPoint = pointTypes.isEmpty() && feature.getCallCount()>0;
        if(situation.isTsumo() && !concealedNoPoint) pointTypes.add(PointType.TSUMO);
        if(feature.getCallCount()==0 && !situation.isTsumo()) pointTypes.add(PointType.SELF_MADE);
        if(calledNoPoint) pointTypes.add(PointType.CALLED_NO_POINT);
        pointTypes.add(PointType.BASE);
        return pointTypes.stream().sorted().toList();
    }

    private static HandType handTypeOf(String name, int doubles){
        return new HandType(){
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
        };
    }

    private static List<HandType> prisedTileHandTypes(HandFeature feature){
        var handTypes = new ArrayList<HandType>(3);
        int count = feature.getPrisedTileCount();
        if(count>0) handTypes.add(handTypeOf("ドラ"+count, count));
        int redCount = feature.getRedPrisedTileCount();
        if(redCount>0) handTypes.add(handTypeOf("赤ドラ"+redCount, redCount));
        int hiddenCount = feature.getHiddenPrisedTileCount();
        if(hiddenCount>0) handTypes.add(handTypeOf("裏ドラ"+hiddenCount, hiddenCount));
        return handTypes;
    }
}
