package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

public interface WinningRoundAccessor{

    /**
     * 場風を取得します。
     * @return 場風
     */
    Wind getRoundWind();

    /**
     * 局が一巡目か検査します。
     * <p>副露または槓子が発生した場合は検査に適合しません。
     * @return true  一巡目の場合
     *         false 二巡目以降の場合
     */
    boolean isFirstAround();

    /**
     * 局の最終摸打か検査します。
     * <p>最終摸打とは残りツモ可能枚数が0の時を指します。
     * <p>最終摸打ではカンの宣言はできません。
     * 最終摸打から1つ前の摸打でカンが発生した場合, その摸打は最終摸打となります。
     * <p>最終摸打で, かつ嶺上ツモでないツモ牌(海底牌)で和了した場合, 海底摸月が付与されます。
     * <p>最終摸打の捨て牌は副露できません。
     * <p>最終摸打の捨て牌でロンが発生した場合は河底撈魚が付与されます。
     * @return true  最終摸打の場合
     *         false 最終摸打でない場合
     */
    boolean isLastTurn();

    /**
     * ドラ表示牌をリスト形式で取得します。
     * @return ドラ表示牌のリスト
     */
    List<Tile> getUpperIndicators();

    /**
     * 裏ドラ表示牌をリスト形式で取得します。
     * <p>裏ドラ表示牌が公開されていない場合は空のリストを取得します。
     * @return 裏ドラ表示牌のリスト
     */
    List<Tile> getLowerIndicators();

    /**
     * ドラをリスト形式で取得します。
     * <p>ドラ表示牌ではなくドラ自体であることに注意が必要です。
     * @return ドラのリスト
     */
    default List<Tile> getUpperPrisedTiles(){
        return getUpperIndicators().stream()
                .map(Tile::indicates)
                .toList();
    }

    /**
     * 裏ドラをリスト形式で取得します。
     * <p>裏ドラ表示牌ではなくドラ自体であることに注意が必要です。
     * @return 裏ドラのリスト
     */
    default List<Tile> getLowerPrisedTiles(){
        return getLowerIndicators().stream()
                .map(Tile::indicates)
                .toList();
    }

    /**
     * 場の供託およびリーチ棒の合計値を取得します。
     * @return 場の供託およびリーチ棒の合計(0..)
     */
    int getTotalDepositCount();

    /**
     * 本場数を取得します。
     * @return 本場数(0..)
     */
    int getRoundStreakCount();

}
