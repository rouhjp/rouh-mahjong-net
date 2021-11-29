package jp.rouh.mahjong.score;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 和了時の特殊条件を表すクラス。
 * @author Rouh
 * @version 1.0
 */
public enum WinningCharacteristics{

    /**
     * ツモ和了。
     * <p>役としての門前清自摸和ではなく, ツモ宣言による和了です。
     * 副露していた場合でも成立可能です。
     */
    TSUMO,

    /**
     * 門前和了。
     * <p>副露(暗槓を含まない)なしで和了したことを表します。
     * ロン和了であっても成立可能です。
     */
    CONCEALED,

    /**
     * 立直済み。
     * <p>立直していた場合に成立します。
     * ダブル立直の場合も成立します。
     */
    READY,

    /**
     * 第一巡立直済み。
     * <p>プレイヤーの第一摸打で立直宣言を行っていた場合に成立します。
     * ただし, 第一摸打前に他家による副露や, カン宣言があった場合は成立しません。
     * <p>この項が成立する場合, ダブル立直役が成立します。
     * <p>この項が成立する場合, 必然的に{@code READY}も成立します。
     */
    FIRST_AROUND_READY,

    /**
     * 第一巡和了。
     * <p>プレイヤーの第一摸打で和了した場合に成立します。
     * ただし, 第一摸打前に他家による副露や, カン宣言があった場合は成立しません。
     * <p>この項が成立する場合, 天和もしくは地和役が成立します。
     */
    FIRST_AROUND_WIN,

    /**
     * 立直後第一巡和了。
     * <p>プレイヤーが立直宣言打牌を行った後, 次に打牌を行うまでの間に和了した場合に成立します。
     * ただし, 途中に他家による副露や, カン宣言があった場合は成立しません。
     * <p>この項が成立する場合, 一発役が成立します。
     * <p>この項が成立する場合, 必然的に{@code READY}も成立します。
     */
    READY_AROUND_WIN,

    /**
     * 河底牌ロン和了。
     * <p>ロンした牌がその局で打牌される最後の牌(河低牌)である場合に成立します。
     * <p>この項が成立する場合, 河底撈魚役が成立します。
     */
    LAST_TILE_CALL_WIN,

    /**
     * 海底牌ツモ和了。
     * <p>ツモした牌が壁牌の最終ツモ牌(海底牌)である場合に成立します。
     * 嶺上牌は海底牌とは扱わないため, 局の最終摸打でのツモ和了であっても,
     * カン宣言後のツモ和了であれば成立しません。
     * <p>この項が成立する場合, 海底摸月役が成立します。
     * <p>この項が成立する場合, 必然的に{@code TSUMO}も成立します。
     * <p>この項が成立する場合, 必然的に{@code QUAD_TILE_CALL_WIN}は成立しません。
     */
    LAST_TILE_DRAW_WIN,
    /**
     * 槍槓和了。
     * <p>他家のカン宣言に対し, ロン和了をした場合に成立します。
     * <p>{@code READY_AROUND_WIN}と共存することが可能です。
     * <p>この項が成立する場合, 槍槓役が成立します。
     */
    QUAD_TILE_CALL_WIN,

    /**
     * 嶺上牌ツモ和了。
     * <p>カン宣言後の嶺上自摸牌で和了した場合に成立します。
     * <p>この項が成立する場合, 嶺上開花役が成立します。
     * <p>この項が成立する場合, 必然的に{@code TSUMO}も成立します。
     */
    QUAD_TILE_DRAW_WIN;

    /**
     * 与えられた特殊条件に矛盾がないか検査します。
     * @param cs 特殊条件のリスト
     * @throws IllegalArgumentException 矛盾がある場合
     */
    static void validate(Collection<WinningCharacteristics> cs){
        //もし key が含まれていれば必ず value も含まれなくてはならない
        var coexistingWcs = new HashMap<WinningCharacteristics, WinningCharacteristics>();
        coexistingWcs.put(READY, CONCEALED);
        coexistingWcs.put(FIRST_AROUND_READY, READY);
        coexistingWcs.put(FIRST_AROUND_WIN, CONCEALED);
        coexistingWcs.put(READY_AROUND_WIN, READY);
        coexistingWcs.put(LAST_TILE_DRAW_WIN, TSUMO);
        coexistingWcs.put(QUAD_TILE_DRAW_WIN, TSUMO);
        for(var coexistingWc: coexistingWcs.entrySet()){
            var ifThisIs = coexistingWc.getKey();
            var soThisIs = coexistingWc.getValue();
            if(cs.contains(ifThisIs) && !cs.contains(soThisIs)){
                throw new IllegalArgumentException("invalid characteristics: " + ifThisIs + " without " + soThisIs);
            }
        }
        //もし key が含まれていれば必ず value は含まれてはならない
        var conflictingWcs = new HashMap<WinningCharacteristics, List<WinningCharacteristics>>();
        conflictingWcs.put(LAST_TILE_CALL_WIN, List.of(TSUMO, FIRST_AROUND_WIN));
        conflictingWcs.put(LAST_TILE_DRAW_WIN, List.of(FIRST_AROUND_WIN));
        conflictingWcs.put(QUAD_TILE_CALL_WIN, List.of(LAST_TILE_DRAW_WIN, LAST_TILE_CALL_WIN, TSUMO));
        conflictingWcs.put(QUAD_TILE_DRAW_WIN, List.of(QUAD_TILE_CALL_WIN, LAST_TILE_DRAW_WIN, LAST_TILE_CALL_WIN, READY_AROUND_WIN, FIRST_AROUND_WIN));
        for(var conflictingWc: conflictingWcs.entrySet()){
            var ifThisIs = conflictingWc.getKey();
            var soTheyAreNot = conflictingWc.getValue();
            if(cs.contains(ifThisIs)){
                for(var soThisIsNot: soTheyAreNot){
                    if(cs.contains(soThisIsNot)){
                        throw new IllegalArgumentException("invalid characteristics: " + ifThisIs + " with " + soThisIsNot);
                    }
                }
            }
        }
    }
}
