package jp.rouh.mahjong.game.event;

/**
 * 摸打中の行動の種別を表すクラス。
 * <p>摸打中の行動を表す{@link TurnAction}クラスによって使用されるタグ・クラスです。
 *
 * @see TurnAction
 * @author Rouh
 * @version 1.0
 */
public enum TurnActionType {

    /**
     * ツモを宣言します。
     */
    TSUMO,

    /**
     * 九種九牌を宣言します。
     */
    NINE_TILES,

    /**
     * カン(加槓/暗槓)を宣言します。
     * <p>どの牌をカンするか指定する必要があります。
     */
    KAN,

    /**
     * 立直宣言打牌します。
     * <p>どの牌を打牌するか指定する必要があります。
     */
    READY_DISCARD,

    /**
     * 打牌します。
     * <p>どの牌を打牌するか指定する必要があります。
     */
    DISCARD
}
