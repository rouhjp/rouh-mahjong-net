package jp.rouh.mahjong.score;

/**
 * 和了時の付帯状況。
 *
 * @author Rouh
 * @version 2.0
 */
public enum WinningOption{

    /**
     * 立直済み。
     *
     * <p>立直していた場合に成立します。
     * ダブル立直の場合も成立します。
     */
    READY,

    /**
     * 第一巡立直済み。
     *
     * <p>プレイヤーの第一摸打で立直宣言を行っていた場合に成立します。
     * ただし, 第一摸打前に他家による副露や, カン宣言があった場合は成立しません。
     * <p>この項が成立する場合, ダブル立直役が成立します。
     * <p>この項が成立する場合, 必然的に{@code READY}も成立します。
     */
    FIRST_AROUND_READY,

    /**
     * 第一巡和了。
     *
     * <p>プレイヤーの第一摸打で和了した場合に成立します。
     * ただし, 第一摸打前に他家による副露や, カン宣言があった場合は成立しません。
     * <p>この項が成立する場合, ツモであれば天和もしくは地和役が成立します。
     */
    FIRST_AROUND_WIN,

    /**
     * 立直後第一巡和了。
     *
     * <p>プレイヤーが立直宣言打牌を行った後, 次に打牌を行うまでの間に和了した場合に成立します。
     * ただし, 途中に他家による副露や, カン宣言があった場合は成立しません。
     * <p>この項が成立する場合, 一発役が成立します。
     * <p>この項が成立する場合, 必然的に{@code READY}も成立します。
     */
    READY_AROUND_WIN,

    /**
     * 海底牌和了。
     *
     * <p>和了牌が壁牌の最後の牌(海底牌)である場合に成立します。
     * 嶺上牌は海底牌ではないため, 局の最終摸打でのツモ和了であっても,
     * カン宣言後のツモ和了であれば成立しません。
     * <p>この項が成立する場合, ツモであれば海底摸月役, ロンであれば河底撈魚役が成立します。
     */
    LAST_TILE_WIN,

    /**
     * 槍槓和了。
     *
     * <p>他家のカン宣言に対し, ロン和了をした場合に成立します。
     * <p>この項が成立する場合, 槍槓役が成立します。
     * <p>一般的に, 槍槓役と一発は同時に成立が可能です。
     */
    QUAD_TILE_WIN,

    /**
     * 嶺上牌ツモ和了。
     *
     * <p>カン宣言後の嶺上自摸牌で和了した場合に成立します。
     * <p>この項が成立する場合, 嶺上開花役が成立します。
     */
    QUAD_TURN_WIN
}