package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Tile;

import java.util.NoSuchElementException;

import static jp.rouh.mahjong.game.event.TurnActionType.*;

/**
 * ターン内行動クラス。
 * <p>打牌や立直宣言など, 自身の摸打時に選択可能な行動を表します。
 * @param type 行動の種別
 * @param argument 行動の対象牌
 * @author Rouh
 * @version 1.0
 */
public record TurnAction(TurnActionType type, Tile argument) {

    @Override
    public Tile argument() {
        if (argument==null){
            throw new NoSuchElementException(type+" has no argument");
        }
        return argument;
    }

    /**
     * 行動の対象牌が存在するかどうか検査します。
     * @return true 行動の対象牌が存在する場合
     *         false 行動の対象牌が存在しない場合
     */
    public boolean hasArgument(){
        return argument!=null;
    }

    @Override
    public String toString(){
        return type+(argument==null?"":"_"+argument);
    }

    /**
     * ツモ宣言を表すオブジェクトを取得します。
     * <p>このオブジェクトに対して{@link #argument}メソッドを呼んだ場合,
     * {@link NoSuchElementException}例外が発生します。
     * @return ツモ宣言
     */
    public static TurnAction ofTsumo() {
        return new TurnAction(TSUMO, null);
    }

    /**
     * 九種九牌宣言を表すオブジェクトを取得します。
     * <p>このオブジェクトに対して{@link #argument}メソッドを呼んだ場合,
     * {@link NoSuchElementException}例外が発生します。
     * @return 九種九牌宣言
     */
    public static TurnAction ofNineTiles() {
        return new TurnAction(NINE_TILES, null);
    }

    /**
     * カン(暗槓/加槓)宣言を表すオブジェクトを取得します。
     * <p>暗槓の残りの構成牌や加槓先の面子は自明であるため必要としません。
     * @param t 任意の槓子構成牌1枚
     * @return カン宣言
     */
    public static TurnAction ofKan(Tile t) {
        return new TurnAction(TURN_KAN, t);
    }

    /**
     * 立直宣言打牌を表すオブジェクトを取得します。
     * @param t 打牌
     * @return 立直宣言打牌
     */
    public static TurnAction ofReadyAndDiscard(Tile t) {
        return new TurnAction(READY_DISCARD, t);
    }

    /**
     * 打牌を表すオブジェクトを取得します。
     * @param t 打牌
     * @return 打牌
     */
    public static TurnAction ofDiscard(Tile t) {
        return new TurnAction(DISCARD, t);
    }

    /**
     * ツモ切り打牌を表すオブジェクトを取得します。
     * @return ツモ切り打牌
     */
    public static TurnAction ofDiscardDrawn(Tile t){
        return new TurnAction(DISCARD_DRAWN, t);
    }
}
