package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Tile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static jp.rouh.mahjong.game.event.CallActionType.*;

/**
 * ターン外行動のリストをラップし, プレゼンテーションに有用なメソッドを提供するクラス。
 * @author Rouh
 * @version 1.0
 */
class CallActionSelector{
    private final List<CallAction> chiActions;
    private final List<CallAction> ponActions;
    private final CallAction kanActionNullable;
    private final CallAction ronActionNullable;
    private final CallAction passAction;

    /**
     * コンストラクタ。
     * @param choices ターン外行動のリスト
     */
    CallActionSelector(List<CallAction> choices){
        this.passAction = choices.stream().filter(c->c.type()==PASS).findAny().orElseThrow();
        this.chiActions = choices.stream().filter(c->c.type()==CHI).toList();
        this.ponActions = choices.stream().filter(c->c.type()==PON).toList();
        this.kanActionNullable = choices.stream().filter(c->c.type()==KAN).findAny().orElse(null);
        this.ronActionNullable = choices.stream().filter(c->c.type()==RON).findAny().orElse(null);
    }

    /**
     * チーを宣言可能か検査します。
     * <p>一度の他家の打牌で複数のチー副露の可能性があるため,
     * この検査だけではチー構成牌を絞り込めない可能性があります。
     * <p>この牌を選択した場合にチー副露の選択が一意に定まるかどうかは
     * {@link #getSingledOutChiAction()}メソッドの戻り値を確認する必要があります。
     * @return true チーが宣言可能である場合
     *         false チーが宣言可能でない場合
     */
    boolean canDeclareChi(){
        return !chiActions.isEmpty();
    }

    /**
     * ポンを宣言可能か検査します。
     * <p>一度の他家の打牌で複数のポン副露の可能性があるため,
     * この検査だけではポン構成牌を絞り込めない可能性があります。
     * <p>この牌を選択した場合にポン副露の選択が一意に定まるかどうかは
     * {@link #getSingledOutPonAction()}メソッドの戻り値を確認する必要があります。
     * @return true ポンが宣言可能である場合
     *         false ポンが宣言可能でない場合
     */
    boolean canDeclarePon(){
        return !ponActions.isEmpty();
    }

    /**
     * カンを宣言可能か検査します。
     * <p>ある打牌についてカン可能な構成牌の組み合わせは常に1つであるため,
     * この検査に適合した場合は{@link #getKanAction()}メソッドでその選択肢を取得することができます。
     * @return true カンが宣言可能である場合
     *         false カンが宣言可能でない場合
     */
    boolean canDeclareKan(){
        return kanActionNullable!=null;
    }

    /**
     * ロンを宣言可能か検査します。
     * <p>ロンを宣言する場合の選択肢は{@link #getRonAction()}メソッドで取得することができます。
     * @return true ロンが宣言可能である場合
     *         false ロンが宣言可能でない場合
     */
    boolean canDeclareRon(){
        return ronActionNullable!=null;
    }

    /**
     * 手牌中のある牌がチー構成牌として選択可能かどうか検査します。
     * <p>一度の他家の打牌で複数のチー副露の可能性があるため,
     * この検査だけではチー構成牌を絞り込めない可能性があります。
     * <p>この牌を選択した場合にチー副露の選択が一意に定まるかどうかは
     * {@link #getSingledOutChiAction(Tile)}メソッドの戻り値を確認する必要があります。
     * @param selecting 手牌中の牌
     * @return true チー構成牌の場合
     *         false チー構成牌でない場合
     */
    boolean canSelectForChi(Tile selecting){
        return chiActions.stream().anyMatch(a->a.arguments().contains(selecting));
    }

    /**
     * 手牌中のある牌がポン構成牌として選択可能かどうか検査します。
     * <p>一度の他家の打牌で複数のポン副露の可能性があるため,
     * この検査だけではポン構成牌を絞り込めない可能性があります。
     * <p>この牌を選択した場合にポン副露の選択が一意に定まるかどうかは
     * {@link #getSingledOutPonAction(Tile)}メソッドの戻り値を確認する必要があります。
     * @param selecting 手牌中の牌
     * @return true ポン構成牌の場合
     *         false ポン構成牌でない場合
     */
    boolean canSelectForPon(Tile selecting){
        return ponActions.stream().anyMatch(a->a.arguments().contains(selecting));
    }

    /**
     * 手牌中のある牌をチー構成牌として選択したとき,
     * 別のある牌が同じチー構成牌として選択可能か検査します。
     * <p>この牌を選択した場合定まる選択肢は
     * {@link #getSingledOutChiAction(Tile, Tile)}メソッドで取得可能です。
     * @param selecting 手牌中の牌
     * @param selected 既に選択されたチー構成牌
     * @return true チー構成牌の場合
     *         false チー構成牌でない場合
     */
    boolean canSelectForChiUnderSelection(Tile selecting, Tile selected){
        return chiActions.stream().anyMatch(a->
                a.arguments().equals(List.of(selected, selecting)) ||
                        a.arguments().equals(List.of(selected, selected)));
    }

    /**
     * 手牌中のある牌をポン構成牌として選択したとき,
     * 別のある牌が同じポン構成牌として選択可能か検査します。
     * <p>この牌を選択した場合定まる選択肢は
     * {@link #getSingledOutPonAction(Tile, Tile)}メソッドで取得可能です。
     * @param selecting 手牌中の牌
     * @param selected 既に選択されたポン構成牌
     * @return true ポン構成牌の場合
     *         false ポン構成牌でない場合
     */
    boolean canSelectForPonUnderSelection(Tile selecting, Tile selected){
        return ponActions.stream().anyMatch(a->
                a.arguments().equals(List.of(selected, selecting)) ||
                        a.arguments().equals(List.of(selecting, selected)));
    }

    /**
     * 構成牌を指定してチー宣言を行う選択肢を取得します。
     * <p>チー宣言が可能かどうか, 事前に{@link #canDeclareChi()}メソッドの
     * 検査に適合することを確認しておく必要があります。
     * <p>一度の他家の打牌で複数のチー副露の可能性があるため,
     * 構成牌を指定しないと選択肢が一意に定まらない場合があります。
     * その場合は{@code Optional.empty()}が返されます。
     * @return 選択肢 一意に選択肢が定まった場合
     *         空 構成牌を指定しなければ一意に選択肢が定まらない場合
     * @throws NoSuchElementException チー宣言不可能な場合
     */
    Optional<CallAction> getSingledOutChiAction(){
        if(chiActions.isEmpty()){
            throw new NoSuchElementException("invalid action");
        }
        return chiActions.size()==1? Optional.of(chiActions.get(0)):Optional.empty();
    }

    /**
     * 構成牌を指定してチー宣言を行う選択肢を取得します。
     * <p>構成牌が正しいかどうか, 事前に{@link #canSelectForChi}メソッドの
     * 検査に適合することを確認しておく必要があります。
     * <p>構成牌を1枚選択しただけでは選択肢が一意に定まらない場合があります。
     * 一意に定まらない場合は{@code Optional.empty()}が返されます。
     * @param selected 構成牌1
     * @return 選択肢 構成牌1によって一意に選択肢が定まった場合
     *         空 構成牌1によって一意に選択肢が定まらなかった場合
     * @throws NoSuchElementException 構成牌が不正の場合
     */
    Optional<CallAction> getSingledOutChiAction(Tile selected){
        var matchedChiActions = chiActions.stream()
                .filter(a->a.arguments().contains(selected)).toList();
        if(matchedChiActions.isEmpty()){
            throw new NoSuchElementException("invalid tile: "+selected);
        }
        return matchedChiActions.size()==1? Optional.of(matchedChiActions.get(0)):Optional.empty();
    }

    /**
     * 二つの構成牌を指定してチー宣言を行う選択肢を取得します。
     * <p>構成牌が正しいかどうか, 事前に{@link #canSelectForChiUnderSelection}メソッドの
     * 検査に適合することを確認しておく必要があります。
     * @param selected1 構成牌1
     * @param selected2 構成牌2
     * @return チー宣言の選択肢
     * @throws NoSuchElementException 構成牌が不正の場合
     */
    CallAction getSingledOutChiAction(Tile selected1, Tile selected2){
        return chiActions.stream()
                .filter(a->a.arguments().equals(List.of(selected1, selected2)) ||
                        a.arguments().equals(List.of(selected2, selected1)))
                .findAny()
                .orElseThrow(()->new NoSuchElementException("invalid tile: "+selected1+" "+selected2));
    }

    /**
     * 構成牌を指定してポン宣言を行う選択肢を取得します。
     * <p>ポン宣言が可能かどうか, 事前に{@link #canDeclarePon()}メソッドの
     * 検査に適合することを確認しておく必要があります。
     * <p>一度の他家の打牌で複数のポン副露の可能性があるため,
     * 構成牌を指定しないと選択肢が一意に定まらない場合があります。
     * その場合は{@code Optional.empty()}が返されます。
     * @return 選択肢 一意に選択肢が定まった場合
     *         空 構成牌を指定しなければ一意に選択肢が定まらない場合
     * @throws NoSuchElementException ポン宣言不可能な場合
     */
    Optional<CallAction> getSingledOutPonAction(){
        if(ponActions.isEmpty()){
            throw new NoSuchElementException("invalid action");
        }
        return ponActions.size()==1? Optional.of(ponActions.get(0)):Optional.empty();
    }

    /**
     * 構成牌を指定してポン宣言を行う選択肢を取得します。
     * <p>構成牌が正しいかどうか, 事前に{@link #canSelectForPon}メソッドの
     * 検査に適合することを確認しておく必要があります。
     * <p>構成牌を1枚選択しただけでは選択肢が一意に定まらない場合があります。
     * 一意に定まらない場合は{@code Optional.empty()}が返されます。
     * @param selected 構成牌1
     * @return 選択肢 構成牌1によって一意に選択肢が定まった場合
     *         空 構成牌1によって一意に選択肢が定まらなかった場合
     * @throws NoSuchElementException 構成牌が不正の場合
     */
    Optional<CallAction> getSingledOutPonAction(Tile selected){
        var matchedPonActions = ponActions.stream()
                .filter(a->a.arguments().contains(selected)).toList();
        if(matchedPonActions.isEmpty()){
            throw new NoSuchElementException("invalid tile: "+selected);
        }
        return matchedPonActions.size()==1? Optional.of(matchedPonActions.get(0)):Optional.empty();
    }

    /**
     * 二つの構成牌を指定してポン宣言を行う選択肢を取得します。
     * <p>構成牌が正しいかどうか, 事前に{@link #canSelectForPonUnderSelection}メソッドの
     * 検査に適合することを確認しておく必要があります。
     * @param selected1 構成牌1
     * @param selected2 構成牌2
     * @return ポン宣言の選択肢
     * @throws NoSuchElementException 構成牌が不正の場合
     */
    CallAction getSingledOutPonAction(Tile selected1, Tile selected2){
        return ponActions.stream()
                .filter(a->a.arguments().equals(List.of(selected1, selected2)) ||
                        a.arguments().equals(List.of(selected2, selected1)))
                .findAny()
                .orElseThrow(()->new NoSuchElementException("invalid tile: "+selected1+" "+selected2));
    }

    /**
     * カン宣言を行う選択肢を取得します。
     * <p>カン宣言が可能かどうか, 事前に{@link #canDeclareKan()}メソッドで検査する必要があります。
     * @return カン宣言の選択肢
     * @throws NoSuchElementException カン宣言不可能な場合
     */
    CallAction getKanAction(){
        if(kanActionNullable==null){
            throw new NoSuchElementException();
        }
        return kanActionNullable;
    }

    /**
     * ロン宣言を行う選択肢を取得します。
     * <p>ロン宣言が可能かどうか, 事前に{@link #canDeclareRon()}メソッドで検査する必要があります。
     * @return ロン宣言の選択肢
     * @throws NoSuchElementException ロン宣言不可能な場合
     */
    CallAction getRonAction(){
        if(ronActionNullable==null){
            throw new NoSuchElementException();
        }
        return ronActionNullable;
    }

    /**
     * パスする選択肢を取得します。
     * @return パスの選択肢
     */
    CallAction getPassAction(){
        return passAction;
    }
}
