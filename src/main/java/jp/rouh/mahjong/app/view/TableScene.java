package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.app.ApplicationContext;
import jp.rouh.mahjong.app.RoomScene;
import jp.rouh.mahjong.app.Scene;
import jp.rouh.mahjong.game.event.TableStrategy;
import jp.rouh.mahjong.game.event.TableView;
import jp.rouh.mahjong.game.event.TableViewStrategy;

/**
 * 麻雀卓画面。
 * @author Rouh
 * @version 1.0
 */
public class TableScene extends Scene{
    private final ApplicationContext context;
    private final TableViewPanel viewPanel;
    private final TableViewStrategy strategy;
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
        viewPanel = new TableViewPanel(callback);
        strategy = new TableViewStrategy(viewPanel);
        viewPanel.setLocation(EDGE_BASE_SIZE*weight, EDGE_BASE_SIZE*weight);
        add(viewPanel);
    }

    /**
     * テーブル戦略{@link TableStrategy}オブジェクトとして描画パネルを取得します。
     * @return 描画パネル
     */
    public TableStrategy getTableView(){
        return strategy;
    }

    @Override
    public void weightUpdated(){
        int weight = context.getSizeWeight();
        TableSizeManager.getInstance().setWeight(weight);
        viewPanel.setLocation(EDGE_BASE_SIZE*weight, EDGE_BASE_SIZE*weight);
        viewPanel.resize();
    }

}
