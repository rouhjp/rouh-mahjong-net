package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.app.ApplicationContext;
import jp.rouh.mahjong.app.Scene;
import jp.rouh.mahjong.game.event.TableStrategy;

public class TableScene extends Scene{
    private final ApplicationContext context;
    private final TableViewPanel view = new TableViewPanel();

    private final int EDGE_BASE_SIZE = 2;

    public TableScene(ApplicationContext context){
        super(context);
        this.context = context;
        setLayout(null);
        int weight = context.getSizeWeight();
        view.setLocation(EDGE_BASE_SIZE*weight, EDGE_BASE_SIZE*weight);
        add(view);
    }

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
