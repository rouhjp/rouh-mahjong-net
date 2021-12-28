package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;
import java.util.Map;

/**
 * 対局時の情報を各風のプレイヤーへ通知するインターフェース。
 * @author Rouh
 * @version 1.0
 */
interface TableMaster{

    /**
     * 指定された自風に位置するプレイヤーを通知先として取得します。
     * @param seatWind 自風
     * @return 通知先プレイヤー
     */
    TableObserver getPlayerAt(Wind seatWind);

    /**
     * 対局が開始したことを通知します。
     * @param players 参加プレイヤー名のリスト
     */
    void gameStarted(List<ProfileData> players);

    /**
     * プレイヤー情報を通知します。
     * @param players プレイヤーの自風
     */
    void seatUpdated(Map<Wind, PlayerData> players);

    /**
     * 局が開始したことを通知します。
     * @param wind    局風
     * @param count   局数
     * @param streak  本場数
     * @param deposit 供託本数
     * @param last    オーラスかどうか
     */
    void roundStarted(Wind wind, int count, int streak, int deposit, boolean last);

    /**
     * 局が流局したことを通知します。
     * @param drawType 流局の種類
     */
    void roundDrawn(DrawType drawType);

    /**
     * 局が和了されたことを通知します。
     * @param scores 結果情報
     */
    void roundSettled(List<HandScoreData> scores);

    /**
     * 局が流し満貫によって和了されたことを通知します。
     * @param scores 和了結果のリスト
     */
    void roundSettledByRiver(List<RiverScoreData> scores);

    /**
     * 局の精算を通知します。
     * @param payments 精算内容
     */
    void paymentSettled(Map<Wind, PaymentData> payments);

    /**
     * サイコロが振られたことを通知します。
     * @param wind サイコロを振ったプレイヤーの自風
     * @param dice1 1つ目のサイコロの目(1..6)
     * @param dice2 2つ目のサイコロの目(1..6)
     */
    void diceRolled(Wind wind, int dice1, int dice2);

    /**
     * プレイヤーによってポン/カン/チー/リーチ/ツモ/ロンが宣言されたことを通知します。
     * @param wind 宣言者の自風
     * @param declaration 宣言
     */
    void declared(Wind wind, Declaration declaration);

    /**
     * リーチ棒が追加されたことを通知します。
     * @param wind リーチ宣言者の自風
     */
    void readyBoneAdded(Wind wind);

    /**
     * 山牌が生成されたことを通知します。
     */
    void wallGenerated();

    /**
     * 山牌から牌がツモされたことを通知します。
     * @param wind   対象牌のある山の方位
     * @param column 卓中央から見て左から数えた対象牌の位置(0..16)
     * @param floor  対象牌の段目(上段であれば0, 下段であれば1)
     */
    void wallTileTaken(Wind wind, int column, int floor);

    /**
     * 山牌上の牌がめくられたことを通知します。
     * <p>めくられる牌は常に上段の牌となります。
     * @param wind   対象牌のある山の方位
     * @param column 卓中央から見て左から数えた対象牌の位置(0..16)
     * @param tile   対象牌
     */
    void wallTileRevealed(Wind wind, int column, Tile tile);

    /**
     * 手牌が更新されたことを通知します。
     * @param wind      手牌を更新したプレイヤーの方位
     * @param wideTiles 自摸牌を含む手牌(副露は含まない)
     * @param wide      自摸牌がある(摸打中)かどうか
     */
    void handUpdated(Wind wind, List<Tile> wideTiles, boolean wide);

    /**
     * 手牌が倒されたことを通知します。
     * @param wind      手牌を公開したプレイヤーの方位
     * @param wideTiles 自摸牌を含む手牌(副露は含まない)
     * @param wide      自摸牌がある(摸打中)かどうか
     */
    void handRevealed(Wind wind, List<Tile> wideTiles, boolean wide);

    /**
     * 牌が河に捨てられたことを通知します。
     * @param wind 打牌者の自風
     * @param tile 打牌
     * @param tilt 横向き(立直宣言牌)かどうか
     */
    void riverTileAdded(Wind wind, Tile tile, boolean tilt);

    /**
     * 捨て牌が副露によって河から取り除かれたことを通知します。
     * <p>取り除かれた牌は常に河の末尾の牌になります。
     * @param wind 打牌者の自風
     */
    void riverTileTaken(Wind wind);

    /**
     * 面子(順子/刻子/大明槓)が追加されたことを通知します。
     * @param wind  副露者の自風
     * @param tilt  副露牌が倒された方位
     * @param tiles 構成牌のリスト
     */
    void tiltMeldAdded(Wind wind, Side tilt, List<Tile> tiles);

    /**
     * 暗槓が追加されたことを通知します。
     * @param wind  槓宣言者の自風
     * @param tiles 構成牌のリスト
     */
    void selfQuadAdded(Wind wind, List<Tile> tiles);

    /**
     * 加槓によって刻子に牌が追加されたことを通知します。
     * @param wind  通知先から見た槓宣言者の相対方位
     * @param index 追加先の副露の位置(0..3, 副露者から見て最も右側が0)
     * @param tile  追加牌
     */
    void meldTileAdded(Wind wind, int index, Tile tile);

}
