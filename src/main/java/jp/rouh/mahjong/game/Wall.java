package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.Tile;

import java.util.List;

/**
 * 牌山を表すインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface Wall{

    /**
     * 牌山オブザーバを登録します。
     * @param observer 牌山オブザーバ
     */
    void addObserver(WallObserver observer);

    /**
     * 発生したカンの回数を取得します。
     * @return カン回数(0..4)
     */
    int getQuadCount();

    /**
     * 残りツモ可能枚数を取得します。
     * @return ツモ可能枚数(0..)
     */
    int getDrawableTileCount();

    /**
     * ツモ可能牌が存在するか検査します。
     * @return true 残りツモ可能枚数が1以上の場合
     *         false 残りつも可能枚数が0の場合
     */
    boolean hasDrawableTile();

    /**
     * 山から牌を一枚ツモします。
     * <p>この操作の結果として山のツモ可能牌は1枚減ります。
     * 山のツモ可能枚数が0の状態でこの操作を行った場合は,
     * {@link IllegalStateException}例外が発生します。
     * @throws IllegalStateException ツモ可能牌が0の場合
     * @return 自摸牌
     */
    Tile takeTile();

    /**
     * 山から牌を4枚ツモします。
     * <p>この操作は基本的に{@link #takeTile()}メソッドを
     * 4回呼び出す操作と等価です。
     * <p>この操作の結果として山のツモ可能牌は4枚減ります。
     * この操作は配牌時のツモとして利用されることを想定します。
     * 山のツモ可能枚数が4未満の状態でこの操作を行った場合は,
     * {@link IllegalStateException}例外が発生します。
     * @throws IllegalStateException ツモ可能牌が0の場合
     * @return 自摸牌のリスト
     */
    default List<Tile> takeFourTiles(){
        return List.of(takeTile(), takeTile(), takeTile(), takeTile());
    }

    /**
     * 嶺上牌を1枚ツモします。
     * <p>この操作の結果として嶺上牌は1枚減ります。
     * この操作が実行されるたびに{@link #getQuadCount()}で取得される値が1増加します。
     * <p>嶺上牌は計4枚です。この操作が5回呼ばれた場合,
     * {@link IllegalStateException}例外が発生します。
     * @throws IllegalStateException 5枚目の嶺上牌をツモしようとした場合
     * @return 嶺上牌
     */
    Tile takeQuadTile();

    /**
     * ドラの即めくりを実行します。
     * <p>この操作を実行した場合, {@link #getUpperIndicators()}で取得される
     * 表ドラの数が1つ増加します。
     * @throws IllegalStateException 6枚目のドラを追加しようとした場合
     */
    void revealIndicatorImmediately();

    /**
     * 待機中のドラめくりを実行します。
     * <p>明槓はカン宣言時に即時新ドラ追加は実行されず,
     * 当該ターン終了時(打牌成立時/連続カンを含む副露成立時)に実行されます。
     * <p>このため, この操作はターン終了時に実行されることを想定し,
     * 明槓が当該ターンに発生していた場合,
     * {@link #revealIndicatorImmediately()}メソッドを呼び出し,
     * 即めくりでない新ドラを追加します。
     * <p>当該ターンに明槓が発生していたかどうかは,
     * {@link #takeQuadTile()}メソッドが呼び出された回数と
     * {@link #revealIndicatorImmediately()}メソッドが呼び出された回数の
     * 差分で検査します。
     * @throws IllegalStateException 6枚目のドラを追加しようとした場合
     */
    void revealIndicatorsIfPresent();

    /**
     * 表ドラ表示牌をリスト形式で取得します。
     * @return 表ドラ表示牌のリスト
     */
    List<Tile> getUpperIndicators();

    /**
     * 裏ドラ表示牌をリスト形式で取得します。
     * <p>局終了時以外でも参照可能です。この操作で得られる牌の数は
     * {@link #getUpperIndicators()}で得られる牌の数と同数です。
     * @return 裏ドラ表示牌のリスト
     */
    List<Tile> getLowerIndicators();

}
