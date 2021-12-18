package jp.rouh.mahjong.app.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 麻雀卓上に表示するテキストラベルの生成に関するユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
final class TableTextLabels{
    private TableTextLabels(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 指定した向きに回転したテキストラベルを取得します。
     * @param d ラベルの向き
     * @param text ラベルのテキスト
     * @param width 回転前のラベルの幅
     * @param height 回転前のラベルの高さ
     * @return ラベル
     */
    static TableLabel ofRotatedImageText(Direction d, String text, int width, int height){
        return ofRotatedImageText(d, width, height, (size, g)->{
            g.setColor(Color.BLACK);
            var metrics = g.getFontMetrics();
            int x = (size.width - metrics.stringWidth(text))/2;
            int y = (size.height - metrics.getHeight())/2 + metrics.getAscent();
            g.drawString(text, x, y);
        });
    }

    /**
     * 指定した向きに回転したテキストラベルを取得します。
     * @param d ラベルの向き
     * @param text ラベルのテキスト
     * @param width 回転前のラベルの幅
     * @param height 回転前のラベルの高さ
     * @param background ラベルの背景色
     * @return ラベル
     */
    static TableLabel ofRotatedImageText(Direction d, String text, int width, int height, Color background){
        return ofRotatedImageText(d, width, height, (size, g)->{
            g.setColor(background);
            g.fillRect(0, 0, size.width, size.height);
            g.setColor(Color.BLACK);
            var metrics = g.getFontMetrics();
            int x = (size.width - metrics.stringWidth(text))/2;
            int y = (size.height - metrics.getHeight())/2 + metrics.getAscent();
            g.drawString(text, x, y);
        });
    }

    /**
     * 指定した向きに回転した左揃えのテキストラベルを取得します。
     * @param d ラベルの向き
     * @param text ラベルのテキスト
     * @param width 回転前のラベルの幅
     * @param height 回転前のラベルの高さ
     * @return ラベル
     */
    static TableLabel ofRotatedLeftAlignImageText(Direction d, String text, int width, int height){
        return ofRotatedImageText(d, width, height, (size, g)->{
            g.setColor(Color.BLACK);
            var metrics = g.getFontMetrics();
            int y = (size.height - metrics.getHeight())/2 + metrics.getAscent();
            g.drawString(text, 0, y);
        });
    }

    private static TableLabel ofRotatedImageText(Direction d, int width, int height, BiConsumer<Dimension, Graphics> customizer){
        var label = new TableLabel();
        int rotatedWidth = d.isSideways()?height:width;
        int rotatedHeight = d.isSideways()?width:height;
        var imageSupplier = (Supplier<BufferedImage>)()->{
            int weight = 5;
            int baseImageWidth = width*weight;
            int baseImageHeight = height*weight;
            var textImage = new BufferedImage(baseImageWidth, baseImageHeight, BufferedImage.TYPE_INT_ARGB);
            var g = textImage.getGraphics();
            customizer.accept(new Dimension(baseImageWidth, baseImageHeight), g);
            g.drawImage(textImage, 0, 0, null);
            g.dispose();
            return ImageFunctions.rotate(textImage, d.reversed());
        };
        label.setBaseSize(rotatedWidth, rotatedHeight);
        label.setImageIconLater(imageSupplier);
        return label;
    }

    /**
     * テキストのラベルを取得します。
     * @param text ラベルのテキスト
     * @param width ラベルの幅
     * @param height ラベルの高さ
     * @return ラベル
     */
    static TableLabel ofText(String text, int width, int height){
        var label = new TableLabel();
        label.setText(text);
        label.setBaseSize(width, height);
        label.setFont(new Font("Dialog", Font.BOLD, label.getHeight()*2/3));
        label.setResizeTriggerAction(()->
                label.setFont(new Font("Dialog", Font.BOLD, label.getHeight()*2/3)));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
}
