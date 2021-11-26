package jp.rouh.mahjong.game.event;

/**
 * プレイヤーの宣言を表すクラス
 * @author Rouh
 * @version 1.0
 */
public enum Declaration {

    /**
     * ポン
     */
    PON("ポン"),

    /**
     * カン
     */
    KAN("カン"),

    /**
     * チー
     */
    CHI("チー"),

    /**
     * ロン
     */
    RON("ロン"),

    /**
     * リーチ
     */
    READY("リーチ"),

    /**
     * ツモ
     */
    TSUMO("ツモ");

    private final String text;

    Declaration(String text) {
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
