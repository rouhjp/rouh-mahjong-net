package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.tile.Tile;

import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

/**
 * 牌ラベルクラス。
 * @author Rouh
 * @version 1.0
 */
class TileLabel extends TableLabel{

    /**
     * 牌の厚み
     */
    static final int TILE_DEPTH = 2;

    /**
     * 牌の横幅
     */
    static final int TILE_WIDTH = 4;

    /**
     * 牌の縦幅
     */
    static final int TILE_HEIGHT = 6;

    /**
     * 牌ラベルのコンストラクタ。
     * <p>牌ラベルの画像の生成は実行コストが高い可能性があるため,
     * イベントとしてキューに追加し実行します。
     * @param imageSupplier 牌画像供給関数(ワーカースレッドで読み込まれる)
     * @param width 牌ラベルの幅
     * @param height 牌ラベルの高さ
     */
    private TileLabel(Supplier<BufferedImage> imageSupplier, int width, int height){
        setHorizontalAlignment(CENTER);
        setBorder(new LineBorder(Color.BLACK));
        setBaseSize(width, height);
        setImageIconLater(imageSupplier);
    }

    /**
     * 裏向きに寝かされた状態の牌のラベルを取得します。
     * @param d 牌の向き
     * @return 牌ラベル
     */
    static TileLabel ofFaceDown(Direction d){
        int width = d.isSideways()? TILE_HEIGHT:TILE_WIDTH;
        int height = (d.isSideways()? TILE_WIDTH:TILE_HEIGHT) + TILE_DEPTH;
        return new TileLabel(()-> TileImageManager.getInstance().getFaceDownImage(d), width, height);
    }

    /**
     * 表向きに寝かされた状態の牌のラベルを取得します。
     * @param d 牌の向き
     * @param t 牌の種類
     * @return 牌ラベル
     */
    static TileLabel ofFaceUp(Direction d, Tile t){
        int width = d.isSideways()? TILE_HEIGHT:TILE_WIDTH;
        int height = (d.isSideways()? TILE_WIDTH:TILE_HEIGHT) + TILE_DEPTH;
        return new TileLabel(()-> TileImageManager.getInstance().getFaceUpImage(d, t), width, height);
    }

    /**
     * 手前向きに立たされた牌のラベルを取得します。
     * @param t 牌の種類
     * @return 牌ラベル
     */
    static TileLabel ofFrontStand(Tile t){
        int height = TILE_HEIGHT + TILE_DEPTH;
        return new TileLabel(()-> TileImageManager.getInstance().getStandImage(t), TILE_WIDTH, height);
    }

    /**
     * 横もしくは奥向きに立たされた牌のラベルを取得します。
     * @param d 牌の向き
     * @return 牌ラベル
     * @throws IllegalArgumentException 引数に手前
     */
    static TileLabel ofSideStand(Direction d){
        int width = d.isSideways()? TILE_DEPTH:TILE_WIDTH;
        int height = d.isSideways()? (TILE_HEIGHT + TILE_WIDTH):(TILE_HEIGHT + TILE_DEPTH);
        return new TileLabel(()-> TileImageManager.getInstance().getStandImage(d), width, height);
    }
}
