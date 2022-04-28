package jp.rouh.mahjong.game.event;

/**
 * 画面上の選択肢クラス。
 * <p>自身の摸打時や相手の打牌時に選択可能な選択肢を表します。
 * <p>{@link TurnAction}や{@link CallAction}クラスは
 * 「この牌を選択し立直する」「この牌を選択しチーを宣言する」などの
 * 入力を求められた際の最終的なプレイヤー行動の一単位であるのに対し,
 * このクラスで扱う選択肢は, 立直ボタンの押下など, プレゼンテーションの一単位です。
 * @author Rouh
 * @version 1.0
 */
public enum ActionInput{

    /**
     * 0番目の牌を選択
     */
    SELECT_0(0),

    /**
     * 1番目の牌を選択
     */
    SELECT_1(1),

    /**
     * 2番目の牌を選択
     */
    SELECT_2(2),

    /**
     * 3番目の牌を選択
     */
    SELECT_3(3),

    /**
     * 4番目の牌を選択
     */
    SELECT_4(4),

    /**
     * 5番目の牌を選択
     */
    SELECT_5(5),

    /**
     * 6番目の牌を選択
     */
    SELECT_6(6),

    /**
     * 7番目の牌を選択
     */
    SELECT_7(7),

    /**
     * 8番目の牌を選択
     */
    SELECT_8(8),

    /**
     * 9番目の牌を選択
     */
    SELECT_9(9),

    /**
     * 10番目の牌を選択
     */
    SELECT_10(10),

    /**
     * 11番目の牌を選択
     */
    SELECT_11(11),

    /**
     * 12番目の牌を選択
     */
    SELECT_12(12),

    /**
     * 13番目の牌を選択
     */
    SELECT_13(13),

    /**
     * チーを選択
     */
    SELECT_CHI("チー"),

    /**
     * ポンを選択
     */
    SELECT_PON("ポン"),

    /**
     * カンを選択
     */
    SELECT_KAN("カン"),

    /**
     * ロンを選択
     */
    SELECT_RON("ロン"),

    /**
     * ツモを選択
     */
    SELECT_TSUMO("ツモ"),

    /**
     * 立直を選択
     */
    SELECT_READY("立直"),

    /**
     * 九種九牌を選択
     */
    SELECT_NINE_TILES("九種九牌"),

    /**
     * キャンセルを選択
     * <p>立直などを選択した後に、通常の打牌に戻る際に使用する。
     */
    SELECT_CANCEL("キャンセル"),

    /**
     * パスを選択
     */
    SELECT_PASS("パス");

    private final int index;
    private final String optionText;

    ActionInput(int index){
        this.index = index;
        this.optionText = "";
    }

    ActionInput(String optionText){
        this.index = -1;
        this.optionText = optionText;
    }

    /**
     * この選択が牌以外の選択であるか検査します。
     * @return true 牌以外の選択の場合
     *         false 牌の選択の場合
     */
    public boolean isOption(){
        return index==-1;
    }

    /**
     * この選択が牌の選択である場合, 何番目の牌を選んだかを取得します。
     * <p>牌以外の選択であれば-1を返します。
     * @return インデックス
     */
    public int getIndex(){
        return index;
    }

    /**
     * この選択が牌以外の選択である場合, そのオプションのテキストを取得します。
     * <p>牌の選択である場合は空の文字列を返します。
     * @return テキスト
     */
    public String getOptionText(){
        return optionText;
    }

    /**
     * 牌選択のインスタンスを取得します。
     * @param index 左から何番目の牌か
     * @return 牌選択
     */
    public static ActionInput ofIndex(int index){
        if(index>=14){
            throw new IllegalArgumentException("invalid index: " + index);
        }
        return values()[index];
    }
}
