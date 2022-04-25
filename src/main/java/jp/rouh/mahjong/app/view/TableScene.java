package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.app.ApplicationContext;
import jp.rouh.mahjong.app.RoomScene;
import jp.rouh.mahjong.app.Scene;
import jp.rouh.mahjong.game.event.TableStrategy;

/**
 * 麻雀卓画面。
 * @author Rouh
 * @version 1.0
 */
public class TableScene extends Scene{
    private final ApplicationContext context;
    private final TableViewPanel view;

    private final int EDGE_BASE_SIZE = 2;

    /**
     * 麻雀卓画面を生成します。
     * @param context アプリケーションコンテキストの参照
     */
    public TableScene(ApplicationContext context){
        super(context);
        this.context = context;
        setLayout(null);
        int weight = context.getSizeWeight();
        var callback = (Runnable)()->context.moveTo(RoomScene.class);
        view = new TableViewPanel(callback);
        view.setLocation(EDGE_BASE_SIZE*weight, EDGE_BASE_SIZE*weight);
        add(view);
    }

    /**
     * テーブル戦略{@link TableStrategy}オブジェクトとして描画パネルを取得します。
     * @return 描画パネル
     */
    public TableStrategy getTableView(){
        return view;
    }

    @Override
    public void weightUpdated(){
        int weight = context.getSizeWeight();
        TableSizeManager.getInstance().setWeight(weight);
        view.setLocation(EDGE_BASE_SIZE*weight, EDGE_BASE_SIZE*weight);
        view.resize();
    }

}
