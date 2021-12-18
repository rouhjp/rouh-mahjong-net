package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Tile;

import java.util.List;
import java.util.NoSuchElementException;

import static jp.rouh.mahjong.game.event.TurnActionType.*;

/**
 * ターン内行動のリストをラップし, プレゼンテーションに有用なメソッドを提供するクラス。
 * @author Rouh
 * @version 1.0
 */
class TurnActionSelector{
    private final List<TurnAction> discardActions;
    private final List<TurnAction> readyActions;
    private final List<TurnAction> kanActions;
    private final TurnAction tsumoActionNullable;
    private final TurnAction nineTileActionNullable;

    /**
     * コンストラクタ。
     * @param choices ターン内行動のリスト
     */
    TurnActionSelector(List<TurnAction> choices){
        this.discardActions = choices.stream().filter(c->c.type()==DISCARD).toList();
        this.readyActions = choices.stream().filter(c->c.type()==READY_DISCARD).toList();
        this.kanActions = choices.stream().filter(c->c.type()==TURN_KAN).toList();
        this.tsumoActionNullable = choices.stream().filter(c->c.type()==TSUMO).findAny().orElse(null);
        this.nineTileActionNullable = choices.stream().filter(c->c.type()==NINE_TILES).findAny().orElse(null);
    }

    /**
     * 九種九牌を宣言可能か検査します。
     * <p>宣言可能である場合, {@link #getNineTilesAction()}で選択肢を取得可能です。
     * @return true 九種九牌が宣言可能である場合
     *         false 九種九牌が宣言可能でない場合
     */
    boolean canDeclareNineTiles(){
        return nineTileActionNullable!=null;
    }

    /**
     * ツモを宣言可能か検査します。
     * <p>宣言可能である場合, {@link #getTsumoAction()}で選択肢を取得可能です。
     * @return true ツモが宣言可能である場合
     *         false ツモが宣言可能でない場合
     */
    boolean canDeclareTsumo(){
        return tsumoActionNullable!=null;
    }

    /**
     * 立直を宣言可能か検査します。
     * <p>宣言可能である場合, {@link #canSelectForReady}で打牌可能な牌を検査することができます。
     * @return true 立直が宣言可能である場合
     *         false 立直が宣言可能でない場合
     */
    boolean canDeclareReady(){
        return !readyActions.isEmpty();
    }

    /**
     * カンを宣言可能か検査します。
     * <p>宣言可能である場合, {@link #canSelectForKan}で構成牌として利用可能な牌を検査することができます。
     * @return true カンが宣言可能である場合
     *         false カンが宣言可能でない場合
     */
    boolean canDeclareKan(){
        return !kanActions.isEmpty();
    }

    /**
     * 手牌中のある牌が打牌が可能か検査します。
     * <p>食い替えとなる牌は打牌不能となります。
     * @param selecting 手牌中の牌
     * @return true 打牌可能な場合
     *         false 打牌不可能な場合
     */
    boolean canSelectForDiscard(Tile selecting){
        return discardActions.stream().anyMatch(a->a.argument().equals(selecting));
    }

    /**
     * 手牌中のある牌が立直宣言牌として選択可能か検査します。
     * @param selecting 手牌中の牌
     * @return true 立直宣言牌として選択可能な場合
     *         false 立直宣言牌として選択不可能な場合
     */
    boolean canSelectForReady(Tile selecting){
        return readyActions.stream().anyMatch(a->a.argument().equals(selecting));
    }

    /**
     * 手牌中のある牌がカン構成牌として選択可能か検査します。
     * <p>カンには暗槓および加槓両方が含まれます。
     * @param selecting 手牌中の牌
     * @return true カン構成牌として選択可能な場合
     *         false カン構成牌として選択不可能な場合
     */
    boolean canSelectForKan(Tile selecting){
        return kanActions.stream().anyMatch(a->a.argument().equals(selecting));
    }

    /**
     * この牌を指定して打牌を行う選択肢を取得します。
     * <p>事前に{@link #canSelectForDiscard}メソッドに適合するか確認する必要があります。
     * @param selected 手牌中の牌
     * @return 打牌の選択肢
     * @throws NoSuchElementException 選択肢した牌が不正の場合
     */
    TurnAction getDiscardActionOf(Tile selected){
        return discardActions.stream().filter(a->a.argument().equals(selected))
                .findAny().orElseThrow(()->new NoSuchElementException("invalid tile: "+selected));
    }

    /**
     * この牌を指定して立直を行う選択肢を取得します。
     * <p>事前に{@link #canSelectForReady}メソッドに適合するか確認する必要があります。
     * @param selected 手牌中の牌
     * @return 立直宣言の選択肢
     * @throws NoSuchElementException 選択肢した牌が不正の場合
     */
    TurnAction getReadyActionOf(Tile selected){
        return readyActions.stream().filter(a->a.argument().equals(selected))
                .findAny().orElseThrow(()->new NoSuchElementException("invalid tile: "+selected));
    }

    /**
     * この牌を指定してカン宣言を行う選択肢を取得します。
     * <p>事前に{@link #canSelectForKan}メソッドに適合するか確認する必要があります。
     * @param selected 手牌中の牌
     * @return カン宣言の選択肢
     * @throws NoSuchElementException 選択肢した牌が不正の場合
     */
    TurnAction getKanActionOf(Tile selected){
        return kanActions.stream().filter(a->a.argument().equals(selected))
                .findAny().orElseThrow(()->new NoSuchElementException("invalid tile: "+selected));
    }

    /**
     * ツモ宣言を行う選択肢を取得します。
     * <p>あらかじめ{@link #canDeclareTsumo}メソッドに適合するか確認する必要があります。
     * @return ツモ宣言の選択肢
     * @throws NoSuchElementException ツモ宣言が不可能な場合
     */
    TurnAction getTsumoAction(){
        if(tsumoActionNullable==null){
            throw new NoSuchElementException();
        }
        return tsumoActionNullable;
    }

    /**
     * 九種九牌宣言を行う選択肢を取得します。
     * <p>あらかじめ{@link #canDeclareNineTiles}メソッドに適合するか確認する必要があります。
     * @return 九種九牌宣言の選択肢
     * @throws NoSuchElementException 九種九牌宣言が不可能な場合
     */
    TurnAction getNineTilesAction(){
        if(nineTileActionNullable==null){
            throw new NoSuchElementException();
        }
        return nineTileActionNullable;
    }
}
