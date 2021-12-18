package jp.rouh.mahjong.app.view;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * アプリケーションの拡大率を管理するクラス。
 * @author Rouh
 * @version 1.0
 */
class TableSizeManager{
    private static final TableSizeManager INSTANCE = new TableSizeManager(5);
    private final AtomicInteger weight;
    private TableSizeManager(int defaultWeight){
        this.weight = new AtomicInteger(defaultWeight);
    }

    /**
     * アプリケーションの拡大率を更新します。
     * @param weight アプリケーションの拡大率
     */
    void setWeight(int weight){
        this.weight.set(weight);
    }

    /**
     * アプリケーションの拡大率を取得します。
     * @return 拡大率
     */
    int getWeight(){
        return this.weight.get();
    }

    /**
     * このクラスのシングルトンインスタンスを取得します。
     * @return インスタンス
     */
    static TableSizeManager getInstance(){
        return INSTANCE;
    }
}
