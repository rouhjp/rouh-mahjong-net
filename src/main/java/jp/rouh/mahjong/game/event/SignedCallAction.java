package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Wind;

/**
 * 署名付きターン外行動。
 * <p>ターン外行動{@link CallAction}と行動者の自風を保持します。
 * @author Rouh
 * @version 1.0
 */
public class SignedCallAction{
    private final Wind wind;
    private final CallAction callAction;

    public SignedCallAction(Wind wind, CallAction callAction){
        this.wind = wind;
        this.callAction = callAction;
    }

    @Override
    public String toString(){
        return wind+"-"+callAction.toString();
    }

    /**
     * このターン外行動の行動者の自風を取得します。
     * @return 自風
     */
    public Wind from(){
        return wind;
    }

    /**
     * ターン外行動を取得します。
     * @return ターン外行動
     */
    public CallAction get(){
        return callAction;
    }
}
