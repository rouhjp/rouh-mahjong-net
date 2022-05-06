package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.game.GameTable;
import jp.rouh.mahjong.game.event.TableStrategyMock;
import jp.rouh.mahjong.game.event.TableViewStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Executors;

public class TableViewGameFrame{
    private final TableViewPanel table = new TableViewPanel(()->{});

    TableViewGameFrame(){
        var frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(600, 600));
        frame.setLayout(null);
        table.setLocation(10, 10);
        frame.add(table);
        frame.pack();
        frame.setVisible(true);
        frame.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                int frameWidth = frame.getContentPane().getWidth();
                int frameHeight = frame.getContentPane().getHeight();
                int frameMinSize = Math.min(frameWidth, frameHeight);
                int weight = Math.max(3, frameMinSize/120);
                int tableSize = 116*weight;
                int tableLocationX = (frameWidth - tableSize)/2;
                int tableLocationY = (frameHeight - tableSize)/2;
                table.setLocation(tableLocationX, tableLocationY);
                if(weight!=TableSizeManager.getInstance().getWeight()){
                    TableSizeManager.getInstance().setWeight(weight);
                    table.setSize(tableSize, tableSize);
                    table.resize();
                }
            }
        });
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{
            try{
                var table = new GameTable();
                table.addPlayer("けもみみ", new TableViewStrategy(this.table));
                table.addPlayer("guest1", TableStrategyMock.DISCARD);
                table.addPlayer("guest2", TableStrategyMock.DISCARD);
                table.addPlayer("guest3", TableStrategyMock.DISCARD);
                table.start();
            }catch(Exception e){
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(TableViewGameFrame::new);
    }
}
