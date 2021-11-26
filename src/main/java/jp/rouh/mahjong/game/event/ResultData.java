package jp.rouh.mahjong.game.event;

/**
 * 和了結果情報DTO。
 * @param sd 得点情報
 * @param pd 精算情報
 * @author Rouh
 * @version 1.0
 */
public record ResultData(ScoringData sd, PaymentData pd) {
}
