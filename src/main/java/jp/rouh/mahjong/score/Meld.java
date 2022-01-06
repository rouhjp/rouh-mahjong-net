package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;

import java.util.List;

/**
 * 面子インターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface Meld extends HandComponent{

    /**
     * 面子を構成する牌をグラフィック上の並びで取得します。
     * <p>このメソッドはビューの実装用です。
     * <p>例えば, 手牌中の[2, 4]に対して[3]をチーして成立した順子に対し
     * このメソッドを呼び出した場合, 返されるリストは[3, 2, 4]の順になります。
     * また, 加槓された牌は最後に追加されます。
     * @return 面子構成牌のリスト
     */
    @SuppressWarnings("unused")
    List<Tile> getTilesFormed();

    /**
     * {@inheritDoc}
     * @return 面子の構成牌のリスト
     */
    @Override
    List<Tile> getTilesSorted();

    /**
     * 面子から三枚の構成牌を取得します。
     * <p>槓子の場合, 構成牌の一枚を無視した三枚の構成牌を返します。
     * @return 長さ3の面子構成牌のリスト
     */
    List<Tile> getTilesTruncated();

    /**
     * 副露元の相対方位を返します。
     * <p>副露されたものでない場合, {@code Side.SELF}を返します。
     * 暗槓の場合は, {@code Side.SELF}を返します。
     * 加槓の場合は, 元の明刻子の副露元の相対方位を返します。
     * @return 副露元の相対方位
     */
    Side getSourceSide();

    /**
     * 副露元の相対方位を返します。
     * <p>副露されたものでない場合, {@code Side.SELF}を返します。
     * 暗槓もしくは加槓の場合は, {@code Side.SELF}を返します。
     * @return 副露元の相対位置
     */
    default Side getDirectSourceSide(){
        if(isAddQuad()){
            return Side.SELF;
        }
        return getSourceSide();
    }

    /**
     * この面子が順子であるか検査します。
     * @return true 順子の場合
     *         false 順子でない場合
     */
    boolean isSequence();

    /**
     * この面子が刻子であるか検査します。
     * @return true 刻子の場合
     *         false 刻子でない場合
     */
    boolean isTriple();

    /**
     * この面子が槓子であるか検査します。
     * @return true 槓子の場合
     *         false 槓子でない場合
     */
    boolean isQuad();

    /**
     * この面子が暗面子であるかどうか検査します。
     * <p>暗槓はこの検査に適合します。ロンによって成立した刻子は適合しません。
     * @return true 暗面子の場合
     *         false 明面子の場合
     */
    boolean isConcealed();

    /**
     * この面子が暗槓であるか検査します。
     * @return true 暗槓の場合
     *         false 暗槓でない場合
     */
    boolean isSelfQuad();

    /**
     * この面子が加槓であるか検査します。
     * @return true 加槓の場合
     *         false 加槓でない場合
     */
    boolean isAddQuad();

    /**
     * この面子が大明槓であるか検査します。
     * @return true  大明槓の場合
     *         false 大明槓でない場合
     */
    boolean isCallQuad();

    /**
     * この面子の符を計算し取得します。
     * <p>面子の符は以下のように計算されます。
     * 順子の場合は0
     * 刻子の場合は以下の表に従う
     * <table>
     *     <tr><th></th><th>中張</th><th>么九</th></tr>
     *     <tr><th>明刻</th><td>2</td><td>4</td></tr>
     *     <tr><th>暗刻</th><td>4</td><td>8</td></tr>
     *     <tr><th>明槓</th><td>8</td><td>16</td></tr>
     *     <tr><th>暗槓</th><td>16</td><td>32</td></tr>
     * </table>
     * @return 面子の符
     */
    default int getMeldPoint(){
        if(isSequence()) return 0;
        return 2*(isQuad()? 4:1)*(isConcealed()? 2:1)*(isTerminal()? 2:1);
    }

    /**
     * カンによって暗槓を生成します。
     * @param tiles 手牌中の構成牌のリスト(長さ4)
     * @return 暗槓
     * @throws IllegalArgumentException 構成牌が槓子を構成し得ない場合
     */
    static Meld ofSelfQuad(List<Tile> tiles){
        return new SelfQuad(tiles);
    }

    /**
     * カンによって加槓を生成します。
     * @param triple 元となる明刻
     * @param tile   追加牌
     * @return 加槓
     * @throws IllegalArgumentException 指定された面子が刻子でない場合
     *                                  追加牌と刻子が加槓を構成し得ない場合
     */
    static Meld ofAddQuad(Meld triple, Tile tile){
        return new AddQuad(triple, tile);
    }

    /**
     * カンによって大明槓を生成します。
     * @param baseTiles   手牌中の構成牌のリスト(長さ3)
     * @param claimedTile 副露牌
     * @param source      副露元
     * @return 明槓
     * @throws IllegalArgumentException 構成牌が槓子に適合しない場合
     *                                  副露元に自家{@code SELF}を指定した場合
     */
    static Meld ofCallQuad(List<Tile> baseTiles, Tile claimedTile, Side source){
        if(!Tiles.isQuad(baseTiles, claimedTile) || source==Side.SELF){
            throw new IllegalArgumentException("invalid quad: base=" + baseTiles + " claimed=" + claimedTile);
        }
        return new CallMeld(baseTiles, claimedTile, source);
    }

    /**
     * ポンによって明刻を生成します。
     * @param baseTiles   手牌中の構成牌のリスト(長さ2)
     * @param claimedTile 副露牌
     * @param source      副露元
     * @return 明刻
     * @throws IllegalArgumentException 構成牌が刻子に適合しない場合, 副露元に自家{@code SELF}を指定した場合
     */
    static Meld ofCallTriple(List<Tile> baseTiles, Tile claimedTile, Side source){
        if(!Tiles.isTriple(baseTiles, claimedTile) || source==Side.SELF){
            throw new IllegalArgumentException("invalid triple: base=" + baseTiles + " claimed=" + claimedTile);
        }
        return new CallMeld(baseTiles, claimedTile, source);
    }

    /**
     * チーによって順子を生成します。
     * @param baseTiles   手牌中の構成牌のリスト(長さ2)
     * @param claimedTile 副露牌
     * @return 明順
     * @throws IllegalArgumentException 構成牌が順子に適合しない場合
     */
    static Meld ofCallSequence(List<Tile> baseTiles, Tile claimedTile){
        if(!Tiles.isSequence(baseTiles, claimedTile)){
            throw new IllegalArgumentException("invalid sequence: base=" + baseTiles + " claimed=" + claimedTile);
        }
        return new CallMeld(baseTiles, claimedTile, Side.LEFT);
    }
}
