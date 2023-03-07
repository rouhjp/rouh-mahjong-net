package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 標準点数計算クラス。
 * @author Rouh
 * @version 1.0
 */
public final class StandardHandScoreCalculator implements HandScoreCalculator{
    private static final StandardHandScoreCalculator INSTANCE = new StandardHandScoreCalculator();
    private static final Logger LOG = LoggerFactory.getLogger(StandardHandScoreCalculator.class);

    private StandardHandScoreCalculator(){
    }

    @Override
    public HandScore calculate(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, ScoringContext context){
        LOG.debug("--start calculating score--");
        LOG.debug(handTiles+" "+openMelds+" "+winningTile);
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
            int sevenPairPoint = 25;
            var handTypes = new ArrayList<BasicHandType>();
            handTypes.addAll(environmentBasedHandTypes);
            handTypes.addAll(tileBasedHandTypes);
            handTypes.add(IrregularFormatHandType.SEVEN_PAIR);
            handTypes.addAll(prisedTileHandTypes);
            handScores.add(HandScore.of(sevenPairPoint, handTypes, context.isDealer()));
            LOG.debug("case--> Irregular");
            for(var handType:handTypes){
                LOG.debug("        "+handType.getUniqueName());
            }
        }
        var formattedHands = FormattedHands.format(handTiles, openMelds, winningTile, context);
        for(var formattedHand: formattedHands){
            int point = FormattedHands.calculatePointOf(formattedHand, context);
            var meldBasedHandTypes = MeldBasedHandType.testAll(formattedHand, context);
            var handTypes = new ArrayList<BasicHandType>();
            handTypes.addAll(environmentBasedHandTypes);
            handTypes.addAll(tileBasedHandTypes);
            handTypes.addAll(meldBasedHandTypes);
            if(!handTypes.isEmpty()){
                handTypes.addAll(prisedTileHandTypes);
            }
            handScores.add(HandScore.of(point, handTypes, context.isDealer()));
            LOG.debug("case--> "+formattedHand);
            for(var handType:handTypes){
                LOG.debug("        "+handType.getUniqueName());
            }
        }
        var score = handScores.stream().max(Comparator.naturalOrder()).orElse(HandScore.ofEmpty());
        LOG.debug("result: "+score.getScoreExpression());
        for(var handType:score.getHandTypes()){
            LOG.debug(handType.getUniqueName()+" "+handType.getGrade().getCode());
        }
        LOG.debug("--end calculating score--");
        return score;
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

    /**
     * このクラスのシングルトン・インスタンスを取得します。
     * @return インスタンス
     */
    public static HandScoreCalculator getInstance(){
        return INSTANCE;
    }
}
