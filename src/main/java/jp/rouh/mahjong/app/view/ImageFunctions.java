package jp.rouh.mahjong.app.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * 画像処理ユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
final class ImageFunctions{
    private ImageFunctions(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 画像の指定した位置に横線を描画します。
     * @param sourceImage 対象画像
     * @param position 上からの位置
     * @return 編集済み画像
     */
    static BufferedImage adaptLine(BufferedImage sourceImage, int position){
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        var linedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = linedImage.getGraphics();
        g.drawImage(sourceImage, 0, 0, null);
        g.setColor(Color.BLACK);
        g.fillRect(0, position, width, 1);
        g.drawImage(linedImage, 0, 0, null);
        g.dispose();
        return linedImage;
    }

    /**
     * 画像の一番上に横線を描画します。
     * @param sourceImage 対象画像
     * @return 編集済み画像
     */
    static BufferedImage adaptUpperLine(BufferedImage sourceImage){
        return adaptLine(sourceImage, 0);
    }

    /**
     * 画像の一番下に横線を描画します。
     * @param sourceImage 対象画像
     * @return 編集済み画像
     */
    static BufferedImage adaptLowerLine(BufferedImage sourceImage){
        return adaptLine(sourceImage, sourceImage.getHeight() - 1);
    }

    /**
     * ２つの画像を上下に結合します。
     * @param upperImage 上の画像
     * @param lowerImage 下の画像
     * @return 結合済み画像
     */
    static BufferedImage concat(BufferedImage upperImage, BufferedImage lowerImage){
        int width = upperImage.getWidth();
        int height = upperImage.getHeight() + lowerImage.getHeight();
        var concatImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = concatImage.getGraphics();
        g.drawImage(upperImage, 0, 0, null);
        g.drawImage(lowerImage, 0, upperImage.getHeight(), null);
        g.dispose();
        return concatImage;
    }

    /**
     * 画像を回転させます。
     * @param sourceImage 対象画像
     * @param direction 回転方向
     * @return 回転済み画像
     */
    static BufferedImage rotate(BufferedImage sourceImage, Direction direction){
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        int rotatedWidth = direction.isSideways()? height:width;
        int rotatedHeight = direction.isSideways()? width:height;
        var rotatedImage = new BufferedImage(rotatedWidth, rotatedHeight, BufferedImage.TYPE_INT_ARGB);
        var at = new AffineTransform();
        if(direction.isSideways()) at.translate(-(width - height)/2d, (width - height)/2d);
        at.rotate(Math.toRadians(direction.angle()), width/2d, height/2d);
        var op = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        op.filter(sourceImage, rotatedImage);
        return rotatedImage;
    }

    /**
     * 画像を180度回転して下向きにします。
     * @param sourceImage 対象画像
     * @return 回転済み画像
     */
    static BufferedImage upSideDown(BufferedImage sourceImage){
        return rotate(sourceImage, Direction.BOTTOM);
    }

    /**
     * 画像を指定のサイズにリサイズします。
     * <p>戻り値の型が{@link Image}であることに注意してください。
     * @param sourceImage 対象画像
     * @param width リサイズ後幅
     * @param height リサイズ後高さ
     * @return リサイズ済み画像
     */
    static Image resize(BufferedImage sourceImage, int width, int height){
        if(sourceImage.getWidth()==width && sourceImage.getHeight()==height){
            return sourceImage;
        }
        return sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}