package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.ScoringData;
import jp.rouh.mahjong.score.HandScore;
import jp.rouh.mahjong.score.Settlement;

public record WinningResult(HandScore handScore, Settlement settlement, ScoringData scoringData){
}
