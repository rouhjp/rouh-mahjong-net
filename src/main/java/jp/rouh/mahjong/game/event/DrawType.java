package jp.rouh.mahjong.game.event;

/**
 * 流局の種類を表すクラス。
 * @author Rouh
 * @version 1.0
 */
public enum DrawType {

    /**
     * 荒牌平局
     */
    EXHAUSTED("流局"),

    /**
     * 九種九牌
     */
    NINE_TILES_DECLARED("九種九牌"),

    /**
     * 四槓散了
     */
    FOUR_QUADS_BUILT("四槓散了"),

    /**
     * 四家立直
     */
    FOUR_PLAYERS_READIED("四家立直"),

    /**
     * 四風連打
     */
    FOUR_WINDS_DISCARDED("四風連打"),

    /**
     * 三家和
     */
    THREE_PLAYERS_CLAIMED("三家和");

    private final String text;

    DrawType(String text) {
        this.text = text;
    }

    /**
     * 表示する文字列を取得します。
     * @return テキスト
     */
    public String getText() {
        return text;
    }
}
