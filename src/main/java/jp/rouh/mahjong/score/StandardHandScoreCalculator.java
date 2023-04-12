package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 標準点数計算クラス。
 * @author Rouh
 * @version 1.0
 */
public final class StandardHandScoreCalculator implements HandScoreCalculator{
    private static final Logger LOG = LoggerFactory.getLogger(StandardHandScoreCalculator.class);

    public StandardHandScoreCalculator(){
    }

    @Override
    public HandScore calculate(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, ScoringContext context){
        LOG.debug("--start calculating score--");
        LOG.debug(handTiles + " " + openMelds + " " + winningTile);
        var fullTiles = new ArrayList<>(handTiles);
        var fourteenTiles = new ArrayList<>(handTiles);
        fullTiles.add(winningTile);
        fourteenTiles.add(winningTile);
        openMelds.stream().map(Meld::getTilesSorted).forEach(fullTiles::addAll);
        openMelds.stream().map(Meld::getTilesTruncated).forEach(fourteenTiles::addAll);
        var feature = new HandFeature(fullTiles, fourteenTiles, winningTile, context);
        var limitHandTypes = LimitHandType.testAll(feature, context);
        if(!limitHandTypes.isEmpty()){
            return HandScore.ofLimit(limitHandTypes, context.isDealer());
        }
        var environmentBasedHandTypes = EnvironmentBasedHandType.testAll(context);
        var tileBasedHandTypes = TileBasedHandType.testAll(feature, context);
        var prisedTileHandTypes = PrisedTileHandTypes.testAll(feature);
        var handScores = new ArrayList<HandScore>();
        if(IrregularFormatHandType.SEVEN_PAIR.test(handTiles, winningTile, context)){
            var pointTypes = List.of(PointType.SEVEN_PAIR_BASE);
            var handTypes = new ArrayList<BasicHandType>();
            handTypes.addAll(environmentBasedHandTypes);
            handTypes.addAll(tileBasedHandTypes);
            handTypes.add(IrregularFormatHandType.SEVEN_PAIR);
            handTypes.addAll(prisedTileHandTypes);
            handScores.add(HandScore.of(pointTypes, handTypes, context.isDealer()));
            LOG.debug("case--> Irregular");
            for(var handType: handTypes){
                LOG.debug("        " + handType.getUniqueName());
            }
        }
        var formattedHands = format(handTiles, openMelds, winningTile, context);
        for(var formattedHand: formattedHands){
            var pointTypes = pointTypesOf(formattedHand, context);
            var meldBasedHandTypes = MeldBasedHandType.testAll(formattedHand, context);
            var handTypes = new ArrayList<BasicHandType>();
            handTypes.addAll(environmentBasedHandTypes);
            handTypes.addAll(tileBasedHandTypes);
            handTypes.addAll(meldBasedHandTypes);
            if(!handTypes.isEmpty()){
                handTypes.addAll(prisedTileHandTypes);
            }
            handScores.add(HandScore.of(pointTypes, handTypes, context.isDealer()));
            LOG.debug("case--> " + formattedHand);
            for(var handType: handTypes){
                LOG.debug("        " + handType.getUniqueName());
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
            LOG.debug(handType.getUniqueName() + " " + handType.getGrade().getCode());
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
     * @param context     和了状況
     * @return 整形済み手牌のセット
     */
    private static Set<FormattedHand> format(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, ScoringContext context){
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
                        if(!context.isTsumo() && k==i){
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

    private static List<PointType> pointTypesOf(FormattedHand hand, ScoringContext context){
        var pointTypes = new ArrayList<PointType>();
        pointTypes.add(hand.getWait().getWaitPointType());
        pointTypes.add(hand.getHead().getHeadPoint(context.getSeatWind(), context.getRoundWind()));
        pointTypes.addAll(hand.getMelds().stream().map(Meld::getMeldPointType).toList());
        boolean concealedNoPoint = pointTypes.isEmpty() && context.isSelfMade();
        boolean calledNoPoint = pointTypes.isEmpty() && !context.isSelfMade();
        if(context.isTsumo() && !concealedNoPoint) pointTypes.add(PointType.TSUMO);
        if(context.isSelfMade() && !context.isTsumo()) pointTypes.add(PointType.SELF_MADE);
        if(calledNoPoint) pointTypes.add(PointType.CALLED_NO_POINT);
        pointTypes.add(PointType.BASE);
        return pointTypes.stream().sorted().toList();
    }

    @Override
    public HandType forName(String uniqueName){
        for(var handType:LimitHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:EnvironmentBasedHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:TileBasedHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:MeldBasedHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:IrregularFormatHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        for(var handType:FixedScoreHandType.values()){
            if(handType.getUniqueName().equals(uniqueName)){
                return handType;
            }
        }
        if(uniqueName.equals("ドラ")){
            return PrisedTileHandTypes.of(1);
        }
        if(uniqueName.matches("ドラ[0-9]+")){
            int count = Integer.parseInt(uniqueName.substring(2));
            return PrisedTileHandTypes.of(count);
        }
        if(uniqueName.equals("裏ドラ")){
            return PrisedTileHandTypes.ofHidden(1);
        }
        if(uniqueName.matches("裏ドラ[0-9]+")){
            int count = Integer.parseInt(uniqueName.substring(3));
            return PrisedTileHandTypes.ofHidden(count);
        }
        if(uniqueName.equals("赤ドラ")){
            return PrisedTileHandTypes.ofRed(1);
        }
        if(uniqueName.matches("赤ドラ[0-9]+")){
            int count = Integer.parseInt(uniqueName.substring(3));
            return PrisedTileHandTypes.ofRed(count);
        }
        throw new NoSuchElementException("hand type not found: "+uniqueName);
    }
}
