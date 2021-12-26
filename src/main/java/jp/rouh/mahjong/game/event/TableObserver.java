package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;
import java.util.Map;

/**
 * 対局時の情報をプレイヤーへ通知するインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface TableObserver {

    /**
     * 対局が開始したことを通知します。
     * @param players 参加プレイヤー名のリスト
     */
    void gameStarted(List<ProfileData> players);

    /**
     * 指定の方向に位置するプレイヤー情報を通知します。
     * @param players 通知先からみたプレイヤーの相対方位とそのプレイヤーの情報のマップ
     */
    void temporarySeatUpdated(Map<Side, PlayerTempData> players);

    /**
     * 指定の方向に位置するプレイヤー情報を通知します。
     * @param players 通知先からみたプレイヤーの相対方位とそのプレイヤーの情報のマップ
     */
    void seatUpdated(Map<Side, PlayerData> players);

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
     * @param scores 和了結果のリスト
     */
    void roundSettled(List<ScoringData> scores);

    /**
     * 局の精算を通知します。
     * @param payments 通知先からみたプレイヤーの相対方位とそのプレイヤーの精算情報のマップ
     */
    void paymentSettled(Map<Side, PaymentData> payments);

    /**
     * サイコロが振られたことを通知します。
     * @param side  通知先から見たサイコロを振ったプレイヤーの相対位置
     * @param dice1 1つ目のサイコロの目(1..6)
     * @param dice2 2つ目のサイコロの目(1..6)
     */
    void diceRolled(Side side, int dice1, int dice2);

    /**
     * プレイヤーによってポン/カン/チー/リーチ/ツモ/ロンが宣言されたことを通知します。
     * @param side        通知先から見た宣言者の相対方位
     * @param declaration 宣言
     */
    void declared(Side side, Declaration declaration);

    /**
     * リーチ棒が追加されたことを通知します。
     * @param side 通知先から見たリーチ宣言者の相対方位
     */
    void readyBoneAdded(Side side);

    /**
     * 山牌が生成されたことを通知します。
     */
    void wallGenerated();

    /**
     * 山牌から牌がツモされたことを通知します。
     * @param side   通知先から見た対象牌の相対方位
     * @param column 卓中央から見て左から数えた対象牌の位置(0..16)
     * @param floor  対象牌の段目(上段であれば0, 下段であれば1)
     */
    void wallTileTaken(Side side, int column, int floor);

    /**
     * 山牌上の牌がめくられたことを通知します。
     * <p>めくられる牌は常に上段の牌となります。
     * @param side   通知先から見た対象牌の相対方位
     * @param column 卓中央から見て左から数えた対象牌の位置(0..16)
     * @param tile   対象牌
     */
    void wallTileRevealed(Side side, int column, Tile tile);

    /**
     * プレイヤーのターンが開始したことを通知します。
     * @param side 通知先から見たプレイヤーの方向
     */
    void turnStarted(Side side);

    /**
     * 他家の手牌が更新されたことを通知します。
     * @param side 通知先から見た他家の相対方位
     * @param size 手牌の枚数
     * @param wide 自摸牌がある(摸打中)かどうか
     */
    void handUpdated(Side side, int size, boolean wide);

    /**
     * 自家の手牌が更新されたことを通知します。
     * @param allTiles 自摸牌を含む手牌(副露は含まない)
     * @param wide      自摸牌がある(摸打中)かどうか
     */
    void handUpdated(List<Tile> allTiles, boolean wide);

    /**
     * 手牌が倒されたことを通知します。
     * @param side      通知先から見た手牌を倒したプレイヤーの相対方位
     * @param allTiles 自摸牌を含む手牌(副露は含まない)
     * @param wide      自摸牌がある(摸打中)かどうか
     */
    void handRevealed(Side side, List<Tile> allTiles, boolean wide);

    /**
     * 牌が河に捨てられたことを通知します。
     * @param side 通知先から見た打牌者の相対方位
     * @param tile 打牌
     * @param tilt 横向き(立直宣言牌)かどうか
     */
    void riverTileAdded(Side side, Tile tile, boolean tilt);

    /**
     * 捨て牌が副露によって河から取り除かれたことを通知します。
     * <p>取り除かれた牌は常に河の末尾の牌になります。
     * @param side 通知先から見た打牌者の相対方位
     */
    void riverTileTaken(Side side);

    /**
     * 面子(順子/刻子/大明槓)が追加されたことを通知します。
     * @param side  通知先から見た副露者の相対方位
     * @param tilt  副露牌が倒された方位
     * @param tiles 構成牌のリスト
     */
    void tiltMeldAdded(Side side, Side tilt, List<Tile> tiles);

    /**
     * 暗槓が追加されたことを通知します。
     * @param side  通知先から見た槓宣言者の相対方位
     * @param tiles 構成牌のリスト
     */
    void selfQuadAdded(Side side, List<Tile> tiles);

    /**
     * 加槓によって刻子に牌が追加されたことを通知します。
     * @param side  通知先から見た槓宣言者の相対方位
     * @param index 追加先の副露の位置(0..3, 副露者から見て最も右側が0)
     * @param tile  追加牌
     */
    void meldTileAdded(Side side, int index, Tile tile);

}
