package jp.rouh.mahjong.app;

import javax.swing.*;
import java.awt.*;

/**
 * シーン(画面)の抽象クラス。
 * @author Rouh
 * @version 1.0
 */
public abstract class Scene extends JPanel{

    /**
     * シーンの幅の基本サイズ
     */
    static final int BASE_HEIGHT = 120;

    /**
     * シーンの高さの基本サイズ
     */
    static final int BASE_WIDTH = 120;


    private final ApplicationContext context;

    /**
     * シーンのコンストラクタ
     * @param context アプリケーションコンテキストの参照
     */
    public Scene(ApplicationContext context){
        this.context = context;
    }

    /**
     * シーンのサイズが変更された時に呼ばれる処理。
     * <p>必要に応じて各シーンでオーバーライドすることを想定します。
     */
    public void weightUpdated(){
        //pass
    }

    /**
     * アプリケーションコンテキストの参照を取得します。
     * @return アプリケーションコンテキストの参照
     */
    public ApplicationContext getContext(){
        return context;
    }

    @Override
    public Dimension getPreferredSize(){
        int weight = context.getSizeWeight();
        return new Dimension(BASE_WIDTH*weight, BASE_HEIGHT*weight);
    }
}
