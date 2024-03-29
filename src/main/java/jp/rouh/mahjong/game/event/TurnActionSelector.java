package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Tile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static jp.rouh.mahjong.game.event.TurnActionType.*;

/**
 * ターン内行動のリストをラップし, プレゼンテーションに有用なメソッドを提供するクラス。
 * @author Rouh
 * @version 1.0
 */
public class TurnActionSelector{
    private final List<TurnAction> discardActions;
    private final List<TurnAction> readyActions;
    private final List<TurnAction> kanActions;
    private final TurnAction tsumoActionNullable;
    private final TurnAction nineTileActionNullable;
    private final TurnAction discardDrawnActionNullable;

    /**
     * コンストラクタ。
     * @param choices ターン内行動のリスト
     */
    public TurnActionSelector(List<TurnAction> choices){
        this.discardActions = choices.stream().filter(c->c.type()==DISCARD).toList();
        this.readyActions = choices.stream().filter(c->c.type()==READY_DISCARD).toList();
        this.kanActions = choices.stream().filter(c->c.type()==TURN_KAN).toList();
        this.tsumoActionNullable = choices.stream().filter(c->c.type()==TSUMO).findAny().orElse(null);
        this.nineTileActionNullable = choices.stream().filter(c->c.type()==NINE_TILES).findAny().orElse(null);
        this.discardDrawnActionNullable = choices.stream().filter(c->c.type()==DISCARD_DRAWN).findAny().orElse(null);
    }

    /**
     * 手牌中の立直宣言牌として選択可能な牌を全て取得します。
     * @return 立直宣言牌のセット
     */
    public Set<Tile> getReadySelectableTiles(){
        return readyActions.stream().map(TurnAction::argument).collect(Collectors.toSet());
    }

    /**
     * 現在が立直状態でツモ切り打牌のみ打牌が選択可能であるか検査します。
     * @return true 立直状態である場合
     *         false 立直状態でない場合
     */
    public boolean isReady(){
        return discardActions.isEmpty() && discardDrawnActionNullable!=null;
    }

    /**
     * 九種九牌を宣言可能か検査します。
     * <p>宣言可能である場合, {@link #getNineTilesAction()}で選択肢を取得可能です。
     * @return true 九種九牌が宣言可能である場合
     *         false 九種九牌が宣言可能でない場合
     */
    public boolean canDeclareNineTiles(){
        return nineTileActionNullable!=null;
    }

    /**
     * ツモを宣言可能か検査します。
     * <p>宣言可能である場合, {@link #getTsumoAction()}で選択肢を取得可能です。
     * @return true ツモが宣言可能である場合
     *         false ツモが宣言可能でない場合
     */
    public boolean canDeclareTsumo(){
        return tsumoActionNullable!=null;
    }

    /**
     * ツモ切り打牌が可能か検査します。
     * <p>副露後(カンを除く)の場合はツモ切り打牌を選択できません。
     * @return true ツモ切り打牌が可能である場合
     *         false ツモ切り打牌が可能でない場合
     */
    public boolean canDiscardDrawn(){
        return discardDrawnActionNullable!=null;
    }

    /**
     * 立直を宣言可能か検査します。
     * <p>宣言可能である場合, {@link #canSelectForReady}で打牌可能な牌を検査することができます。
     * @return true 立直が宣言可能である場合
     *         false 立直が宣言可能でない場合
     */
    public boolean canDeclareReady(){
        return !readyActions.isEmpty();
    }

    /**
     * カンを宣言可能か検査します。
     * <p>宣言可能である場合, {@link #canSelectForKan}で構成牌として利用可能な牌を検査することができます。
     * @return true カンが宣言可能である場合
     *         false カンが宣言可能でない場合
     */
    public boolean canDeclareKan(){
        return !kanActions.isEmpty();
    }

    /**
     * 手牌中のある牌が打牌が可能か検査します。
     * <p>食い替えとなる牌は打牌不能となります。
     * <p>立直状態の場合はいかなる牌もこの検査に適合しません。
     * 打牌する場合は代わりにツモ切り打牌を選択する必要があります。
     * @param selecting 手牌中の牌
     * @return true 打牌可能な場合
     *         false 打牌不可能な場合
     */
    public boolean canSelectForDiscard(Tile selecting){
        return discardActions.stream().anyMatch(a->a.argument().equals(selecting));
    }

    /**
     * 手牌中のある牌が立直宣言牌として選択可能か検査します。
     * @param selecting 手牌中の牌
     * @return true 立直宣言牌として選択可能な場合
     *         false 立直宣言牌として選択不可能な場合
     */
    public boolean canSelectForReady(Tile selecting){
        return readyActions.stream().anyMatch(a->a.argument().equals(selecting));
    }

    /**
     * 手牌中のある牌がカン構成牌として選択可能か検査します。
     * <p>カンには暗槓および加槓両方が含まれます。
     * @param selecting 手牌中の牌
     * @return true カン構成牌として選択可能な場合
     *         false カン構成牌として選択不可能な場合
     */
    public boolean canSelectForKan(Tile selecting){
        return kanActions.stream().anyMatch(a->a.argument().equals(selecting));
    }

    /**
     * この牌を指定して打牌を行う選択肢を取得します。
     * <p>事前に{@link #canSelectForDiscard}メソッドに適合するか確認する必要があります。
     * @param selected 手牌中の牌
     * @return 打牌の選択肢
     * @throws NoSuchElementException 選択肢した牌が不正の場合
     */
    public TurnAction getDiscardActionOf(Tile selected){
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
    public TurnAction getReadyActionOf(Tile selected){
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
    public TurnAction getKanActionOf(Tile selected){
        return kanActions.stream().filter(a->a.argument().equals(selected))
                .findAny().orElseThrow(()->new NoSuchElementException("invalid tile: "+selected));
    }

    /**
     * ツモ宣言を行う選択肢を取得します。
     * <p>あらかじめ{@link #canDeclareTsumo}メソッドに適合するか確認する必要があります。
     * @return ツモ宣言の選択肢
     * @throws NoSuchElementException ツモ宣言が不可能な場合
     */
    public TurnAction getTsumoAction(){
        if(tsumoActionNullable==null){
            throw new NoSuchElementException("invalid action");
        }
        return tsumoActionNullable;
    }

    /**
     * 九種九牌宣言を行う選択肢を取得します。
     * <p>あらかじめ{@link #canDeclareNineTiles}メソッドに適合するか確認する必要があります。
     * @return 九種九牌宣言の選択肢
     * @throws NoSuchElementException 九種九牌宣言が不可能な場合
     */
    public TurnAction getNineTilesAction(){
        if(nineTileActionNullable==null){
            throw new NoSuchElementException("invalid action");
        }
        return nineTileActionNullable;
    }

    /**
     * ツモ切り打牌を行う選択肢を取得します。
     * <p>あらかじめ{@link #canDiscardDrawn()}もしくは{@link #isReady()}
     * メソッドに適合するか確認する必要があります。
     * @return ツモ切り打牌の選択肢
     * @throws NoSuchElementException ツモ切り打牌が不可能な場合
     */
    public TurnAction getDiscardDrawnAction(){
        if(discardDrawnActionNullable==null){
            throw new NoSuchElementException("invalid action");
        }
        return discardDrawnActionNullable;
    }

    /**
     * 構成牌を指定してカン宣言を行う選択肢を取得します。
     * <p>カン宣言が可能かどうか, 事前に{@link #canDeclareKan()} ()}メソッドの
     * 検査に適合することを確認しておく必要があります。
     * <p>一度の他家の打牌で複数のカン宣言の可能性があるため,
     * 構成牌を指定しないと選択肢が一意に定まらない場合があります。
     * その場合は{@code Optional.empty()}が返されます。
     * @return 選択肢 一意に選択肢が定まった場合
     *         空 構成牌を指定しなければ一意に選択肢が定まらない場合
     * @throws NoSuchElementException カン宣言不可能な場合
     */
    public Optional<TurnAction> getSingleOutKanAction(){
        if(kanActions.isEmpty()){
            throw new NoSuchElementException("invalid action");
        }
        if(kanActions.size()==1){
            return Optional.of(kanActions.get(0));
        }
        if(kanActions.size()==2 && kanActions.get(0).argument().equalsIgnoreRed(kanActions.get(1).argument())){
            return Optional.of(kanActions.get(0));
        }
        return Optional.empty();
    }
}
