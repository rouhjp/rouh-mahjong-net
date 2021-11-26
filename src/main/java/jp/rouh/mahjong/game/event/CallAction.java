package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Tile;
import static jp.rouh.mahjong.game.event.CallActionType.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 他家の打牌に対する行動を表すクラス。
 *
 * @param type 行動の種別
 * @param arguments 行動に必要な牌
 * @author Rouh
 * @version 1.0
 */
public record CallAction(CallActionType type, List<Tile> arguments) {

    @Override
    public List<Tile> arguments() {
        if (arguments==null){
            throw new NoSuchElementException(type+" has no arguments");
        }
        return arguments;
    }

    /**
     * 打牌に対して如何なる宣言も行わない(パスする)ことを表すオブジェクトを取得します。
     * @return パス
     */
    public static CallAction ofPass(){
        return new CallAction(PASS, null);
    }

    /**
     * ロン宣言を表すオブジェクトを取得します。
     * @return ロン宣言
     */
    public static CallAction ofRon(){
        return new CallAction(RON, null);
    }

    /**
     * チー宣言を表すオブジェクトを取得します。
     * <p>あるチーに対する手牌中の残りの構成牌は一意に定まらないため
     * 引数にて明示的に指定する必要があります。
     * <p>指定された構成牌はソートされ保持されます。
     * @param t1 構成牌
     * @param t2 構成牌
     * @return ポン宣言
     */
    public static CallAction ofChi(Tile t1, Tile t2) {
        if (t1.compareTo(t2) > 0) {
            return new CallAction(CHI, List.of(t2, t1));
        } else {
            return new CallAction(CHI, List.of(t1, t2));
        }
    }

    /**
     * ポン宣言を表すオブジェクトを取得します。
     * <p>あるポンに対する手牌中の残りの構成牌は, 赤ドラを所持している場合
     * 一意に定まらないため, 引数にて明示的に指定する必要があります。
     * <p>指定された構成牌はソートされ保持されます。
     * @param t1 構成牌
     * @param t2 構成牌
     * @return ポン宣言
     */
    public static CallAction ofPon(Tile t1, Tile t2){
        if (t1.compareTo(t2) > 0){
            return new CallAction(RON, List.of(t2, t1));
        }else {
            return new CallAction(PON, List.of(t1, t2));
        }
    }

    /**
     * カン(大明槓)宣言を表すオブジェクトを取得します。
     * <p>大明槓に要する構成牌は自明であるため引数を必要としません。
     * <p>このオブジェクトに対して{@link #arguments}メソッドを呼んだ場合,
     * {@link NoSuchElementException}例外が発生します。
     * @return カン宣言
     */
    public static CallAction ofKan(){
        return new CallAction(KAN, null);
    }
}
