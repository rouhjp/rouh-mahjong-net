package jp.rouh.mahjong.app;

import javax.swing.*;
import java.awt.*;

/**
 * 自動サイズ調整パネル。
 * 基本サイズとして幅と高さを設定することで, アプリケーションコンテキストの
 * {@link ApplicationContext#getSizeWeight()}を元に自動でパネルの preferredSize が調整されます。
 * @author Rouh
 * @version 1.0
 */
class AutoResizablePanel extends JPanel{
    private final int baseWidth;
    private final int baseHeight;
    private final ApplicationContext context;

    /**
     * 自動リサイズパネルを生成します。
     * @param baseWidth 基本サイズの幅
     * @param baseHeight 基本サイズの高さ
     * @param context アプリケーションコンテキストの参照
     */
    AutoResizablePanel(int baseWidth, int baseHeight, ApplicationContext context){
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        this.context = context;
    }

    @Override
    public Dimension getPreferredSize(){
        int weight = context.getSizeWeight();
        return new Dimension(baseWidth*weight, baseHeight*weight);
    }
}
