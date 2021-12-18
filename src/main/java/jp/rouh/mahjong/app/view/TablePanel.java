package jp.rouh.mahjong.app.view;

import javax.swing.*;
import java.awt.*;

/**
 * 卓描画用に{@link JLayeredPane}を機能拡張したラベルクラス。
 * @author Rouh
 * @version 1.0
 */
class TablePanel extends JLayeredPane{
    private Dimension baseSize;
    private Point baseLocation;

    /**
     * このパネルを現在のアプリケーションの拡大率をもとにリサイズします。
     * <p>このパネルが{@link TablePanel}もしくは{@link TableLabel}の
     * コンポーネントを持つ場合, そのコンポーネントも同様にリサイズします。
     */
    void resize(){
        var weight = TableSizeManager.getInstance().getWeight();
        if(baseSize!=null){
            setSize(baseSize.width*weight, baseSize.height*weight);
        }
        if(baseLocation!=null){
            setLocationCentered(baseLocation.x*weight, baseLocation.y*weight);
        }
        for(var component:getComponents()){
            if(component instanceof TableLabel tableLabel){
                tableLabel.resize();
            }else if(component instanceof TablePanel tablePanel){
                tablePanel.resize();
            }
        }
    }

    /**
     * このパネルの基本サイズを設定します。
     * <p>基本サイズと{@link TableSizeManager}で管理する
     * アプリケーションの拡大率をもとにラベルのサイズが決定します。
     * @param width 幅
     * @param height 高さ
     */
    void setBaseSize(int width, int height){
        var weight = TableSizeManager.getInstance().getWeight();
        baseSize = new Dimension(width, height);
        setSize(width*weight, height*weight);
    }

    /**
     * このパネルの基本ロケーションを設定します。
     * <p>基本ロケーションと{@link TableSizeManager}で管理する
     * アプリケーションの拡大率をもとにパネルのロケーションが決定します。
     * @param x X座標
     * @param y Y座標
     */
    void setBaseLocationCentered(int x, int y){
        var weight = TableSizeManager.getInstance().getWeight();
        baseLocation = new Point(x, y);
        setLocationCentered(x*weight, y*weight);
    }

    /**
     * このパネルの基本ロケーションを設定します。
     * <p>基本ロケーションと{@link TableSizeManager}で管理する
     * アプリケーションの拡大率をもとにパネルのロケーションが決定します。
     * @param p 座標
     */
    void setBaseLocationCentered(Point p){
        setBaseLocationCentered(p.x, p.y);
    }


    /**
     * 指定した座標に中心が来るよう移動します。
     * <p>{@link Component#setLocation}メソッドでは
     * コンポーネントの左上を指定した座標に合わせる形で移動を行いますが,
     * このメソッドでは, 画像の中心が座標に合わせられます。
     * @param x 移動先のx座標
     * @param y 移動先のy座標
     */
    void setLocationCentered(int x, int y){
        setLocation(x - getWidth()/2, y - getHeight()/2);
    }
}
