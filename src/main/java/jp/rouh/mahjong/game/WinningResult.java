package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.HandScoreData;
import jp.rouh.mahjong.game.event.RiverScoreData;
import jp.rouh.mahjong.score.HandScore;
import jp.rouh.mahjong.score.Settlement;

public class WinningResult{
    private final HandScore handScore;
    private final Settlement settlement;
    private final HandScoreData handData;
    private final RiverScoreData riverData;

    WinningResult(HandScore handScore, Settlement settlement, HandScoreData data){
        this.handScore = handScore;
        this.settlement = settlement;
        this.handData = data;
        this.riverData = null;
    }

    WinningResult(HandScore handScore, Settlement settlement, RiverScoreData data){
        this.handScore = handScore;
        this.settlement = settlement;
        this.handData = null;
        this.riverData = data;
    }

    public HandScore getHandScore(){
        return handScore;
    }

    public Settlement getSettlement(){
        return settlement;
    }

    public HandScoreData getHandData(){
        return handData;
    }

    public RiverScoreData getRiverData(){
        return riverData;
    }
}