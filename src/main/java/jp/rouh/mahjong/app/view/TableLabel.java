package jp.rouh.mahjong.app.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Supplier;

/**
 * 卓描画用に{@link JLabel}を機能拡張したラベルクラス。
 * @author Rouh
 * @version 1.0
 */
class TableLabel extends JLabel{
    private Runnable updateSize;
    private Runnable updateLocation;
    private Runnable updateIcon;
    private Runnable updateAction;
    private Dimension baseSize;
    private Point baseLocation;

    /**
     * このラベルを現在のアプリケーションの拡大率をもとにリサイズします。
     */
    void resize(){
        if(updateSize!=null) updateSize.run();
        if(updateLocation!=null) updateLocation.run();
        if(updateIcon!=null) updateIcon.run();
        if(updateAction!=null) updateAction.run();
    }

    /**
     * このラベルがリサイズされた時に実行する操作を設定します。
     * @param action 操作
     */
    void setResizeTriggerAction(Runnable action){
        updateAction = action;
    }

    /**
     * このラベルの基本サイズを設定します。
     * <p>基本サイズと{@link TableSizeManager}で管理する
     * アプリケーションの拡大率をもとにラベルのサイズが決定します。
     * @param width 幅
     * @param height 高さ
     */
    void setBaseSize(int width, int height){
        var weight = TableSizeManager.getInstance().getWeight();
        setSize(width*weight, height*weight);
        baseSize = new Dimension(width, height);
        updateSize = ()->{
            var updatedWeight = TableSizeManager.getInstance().getWeight();
            setSize(baseSize.width*updatedWeight, baseSize.height*updatedWeight);
        };
    }

    /**
     * このラベルの基本ロケーションを設定します。
     * <p>基本ロケーションと{@link TableSizeManager}で管理する
     * アプリケーションの拡大率をもとにラベルのロケーションが決定します。
     * @param x X座標
     * @param y Y座標
     */
    void setBaseLocation(int x, int y){
        var weight = TableSizeManager.getInstance().getWeight();
        setLocation(x*weight, y*weight);
        baseLocation = new Point(x, y);
        updateLocation = ()->{
            var updatedWeight = TableSizeManager.getInstance().getWeight();
            setLocation(baseLocation.x*updatedWeight, baseLocation.y*updatedWeight);
        };
    }

    /**
     * このラベルの基本ロケーションをこのラベルの中心で設定します。
     * <p>基本ロケーションと{@link TableSizeManager}で管理する
     * アプリケーションの拡大率をもとにラベルのロケーションが決定します。
     * <p>ラベルの位置は左上ではなく中心をもとに設定されます。
     * @param x X座標
     * @param y Y座標
     */
    void setBaseLocationCentered(int x, int y){
        var weight = TableSizeManager.getInstance().getWeight();
        setLocationCentered(x*weight, y*weight);
        baseLocation = new Point(x, y);
        updateLocation = ()->{
           var updatedWeight = TableSizeManager.getInstance().getWeight();
           setLocationCentered(baseLocation.x*updatedWeight, baseLocation.y*updatedWeight);
        };
    }

    /**
     * このラベルの基本ロケーションをこのラベルの中心で設定します。
     * <p>基本ロケーションと{@link TableSizeManager}で管理する
     * アプリケーションの拡大率をもとにラベルのロケーションが決定します。
     * <p>ラベルの位置は左上ではなく中心をもとに設定されます。
     * @param p 座標
     */
    void setBaseLocationCentered(Point p){
        setBaseLocationCentered(p.x, p.y);
    }

    /**
     * このラベルを{@link #setTranslationCentered}で移動した位置から元の位置にもどします。
     */
    void clearTranslationCentered(){
        setTranslationCentered(0, 0);
    }

    /**
     * このラベルをあらかじめ設定された基本ロケーションをもとに移動させます。
     * <p>ラベルの位置は左上ではなく中心をもとに設定されます。
     * @param dx X座標の差分
     * @param dy Y座標の差分
     */
    void setTranslationCentered(int dx, int dy){
        var weight = TableSizeManager.getInstance().getWeight();
        setLocationCentered((baseLocation.x + dx)*weight, (baseLocation.y + dy)*weight);
        updateLocation = ()->{
            var updatedWeight = TableSizeManager.getInstance().getWeight();
            setLocationCentered((baseLocation.x + dx)*updatedWeight, (baseLocation.y + dy)*updatedWeight);
        };
    }

    /**
     * 画像生成をワークスレッドで行い, 完了次第EDT上で画像をラベルにセットします。
     * <p>画像サイズはこのラベルのサイズにリサイズされるため,
     * 事前にラベルのサイズをセットしておく必要があります。
     * @param imageSupplier 画像生成処理
     */
    void setImageIconLater(Supplier<BufferedImage> imageSupplier){
        new SwingWorker<Void, Image>(){
            @Override
            protected Void doInBackground(){
                var image = ImageFunctions.resize(imageSupplier.get(), getWidth(), getHeight());
                publish(image);
                return null;
            }

            @Override
            protected void process(List<Image> chunks){
                for(var chunk:chunks){
                    setIcon(new ImageIcon(chunk));
                }
            }
        }.execute();
        updateIcon = ()->{
            setIcon(null);
            setImageIconLater(imageSupplier);
        };
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

    /**
     * このラベルに設定された基本座標を取得します。
     * @return 基本座標
     */
    Point getBaseLocation(){
        return new Point(baseLocation);
    }
}
