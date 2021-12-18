package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.tile.Tile;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 牌画像を管理するクラス。
 * <p>牌の画像を初回呼び出し時に生成し, その後はキャッシュから参照を渡します。
 * <p>このクラスはスレッドセーフです。
 * @author Rouh
 * @version 1.0
 */
class TileImageManager{
    private static final TileImageManager INSTANCE = new TileImageManager();
    private final Map<Direction, Map<Tile, BufferedImage>> faceUpImages;
    private final Map<Direction, BufferedImage> faceDownImages;
    private final Map<Tile, BufferedImage> playerHandImages;
    private final Map<Direction, BufferedImage> opponentHandImages;

    private TileImageManager(){
        faceUpImages = new ConcurrentHashMap<>();
        faceUpImages.put(Direction.TOP, new ConcurrentHashMap<>());
        faceUpImages.put(Direction.RIGHT, new ConcurrentHashMap<>());
        faceUpImages.put(Direction.BOTTOM, new ConcurrentHashMap<>());
        faceUpImages.put(Direction.LEFT, new ConcurrentHashMap<>());
        faceDownImages = new ConcurrentHashMap<>();
        playerHandImages = new ConcurrentHashMap<>();
        opponentHandImages = new ConcurrentHashMap<>();
    }

    /**
     * 表向きに倒された牌の画像を取得します。
     * <p>牌の画像は, 例えば河に捨てられた牌やドラ表示牌などに使用されます。
     * @param d 牌の向き
     * @param t 牌
     * @return 牌の画像
     */
    BufferedImage getFaceUpImage(Direction d, Tile t){
        return faceUpImages.get(d).computeIfAbsent(t, key->TileImageFactory.createFaceUpTileImage(d, t.name()));
    }

    /**
     * 裏向きに倒された牌の画像を取得します。
     * <p>牌の画像は, 例えば山牌や暗槓の牌に使用されます。
     * @param d 牌の向き
     * @return 牌の画像
     */
    BufferedImage getFaceDownImage(Direction d){
        return faceDownImages.computeIfAbsent(d, TileImageFactory::createFaceDownTileImage);
    }

    /**
     * 手前に向いた立ててある牌の画像を取得します。
     * <p>牌の画像は, プレイヤーの手牌に使用されます。
     * @param t 牌
     * @return 牌の画像
     */
    BufferedImage getStandImage(Tile t){
        return playerHandImages.computeIfAbsent(t, key->TileImageFactory.createFrontStandTileImage(t.name()));
    }

    /**
     * 指定した方向に向いた立ててある牌の画像を取得します。
     * <p>牌の画像は, 他プレイヤーの手牌に使用されます。
     * プレイヤー自身の手牌は牌の表面が見えるため, 牌の種類を
     * 引数に持つ{@link #getStandImage}メソッドを変わりに使用します。
     * @param d 倒した時の牌の向き
     * @throws IllegalArgumentException 向きが{@code Direction.TOP}の場合
     * @return 牌の画像
     */
    BufferedImage getStandImage(Direction d){
        return opponentHandImages.computeIfAbsent(d, TileImageFactory::createSideStandTileImage);
    }

    /**
     * シングルトンインスタンスを取得します。
     * @return インスタンス
     */
    static TileImageManager getInstance(){
        return INSTANCE;
    }
}
