package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.HandScoreData;
import jp.rouh.mahjong.game.event.RiverScoreData;
import jp.rouh.mahjong.score.*;

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
        var data = new HandScoreData();
        data.setHandTiles(context.getHandTiles());
        data.setWinningTile(context.getWinningTile());
        data.setOpenMelds(context.getOpenMelds().stream()
                .map(Meld::getTilesSorted).toList());
        data.setMeldTiltSides(context.getOpenMelds().stream()
                .map(Meld::getSourceSide).toList());
        data.setTsumo(context.isTsumo());
        data.setUpperIndicators(context.getUpperIndicators());
        data.setLowerIndicators(context.getLowerIndicators());
        data.setHandTypeNames(handScore.getHandTypes().stream()
                .map(HandType::getName).toList());
        data.setHandTypeGrades(handScore.getHandTypes().stream()
                .map(HandType::getGrade)
                .map(HandTypeGrade::getCode).toList());
        data.setScoreExpression(handScore.getScoreExpression());
        return data;
    }

    RiverScoreData getRiverScoreData(){
        var handType = handScore.getHandTypes().get(0);
        return new RiverScoreData(handType.getName(), handType.getGrade().getCode(), handScore.getScoreExpression());
    }

}
