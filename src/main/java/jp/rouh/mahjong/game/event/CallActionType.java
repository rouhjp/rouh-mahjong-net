package jp.rouh.mahjong.game.event;

/**
 * 他家の打牌に対する行動の種別を表すクラス。
 * <p>他家の打牌に対する行動を表す{@link CallAction}クラスによって使用されるタグ・クラスです。
 *
 * @see CallAction
 * @author Rouh
 * @version 1.0
 */
public enum CallActionType {

    /**
     * パスします。
     */
    PASS,

    /**
     * ロンを宣言します。
     */
    RON,

    /**
     * チーを宣言します。
     * <p>どの構成牌でチーするか指定する必要があります。
     */
    CHI,

    /**
     * ポンを宣言します。
     * <p>どの構成牌でポンするか指定する必要があります。
     */
    PON,

    /**
     * カン(大明槓)を宣言します。
     */
    KAN
}
