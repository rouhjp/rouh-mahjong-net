package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.HandScoreData;
import jp.rouh.mahjong.game.event.RiverScoreData;
import jp.rouh.mahjong.score.HandScore;
import jp.rouh.mahjong.score.WinningContext;

class WinningResult{
    private final HandScore handScore;
    private final WinningContext context;

    WinningResult(HandScore handScore, WinningContext context){
        this.handScore = handScore;
        this.context = context;
    }

    HandScore getHandScore(){
        return handScore;
    }

    WinningContext getWinningContext(){
        return context;
    }

    HandScoreData getHandScoreData(){
        return new HandScoreData.Builder()
                .withHandTiles(context.getHandTiles())
                .withWinningTile(context.getWinningTile())
                .withOpenMelds(context.getOpenMelds())
                .withTsumo(context.isTsumo())
                .withUpperIndicators(context.getUpperIndicators())
                .withLowerIndicators(context.getLowerIndicators())
                .withHandTypes(handScore.getHandTypes())
                .withScoreExpression(handScore.getScoreExpression())
                .build();
    }

    RiverScoreData getRiverScoreData(){
        return new RiverScoreData(handScore.getHandTypes().get(0), handScore.getScoreExpression());
    }

}
