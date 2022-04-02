package jp.rouh.mahjong.app;

import jp.rouh.mahjong.app.view.TableScene;
import jp.rouh.mahjong.game.GameTable;
import jp.rouh.mahjong.game.event.TableStrategyMock;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;

public class MenuScene extends Scene{

    private final JTextField joinPortField = new JTextField();
    private final JTextField joinHostField = new JTextField();
    private final JTextField hostPortField = new JTextField();

    public MenuScene(ApplicationContext context){
        super(context);
        var layout = new SpringLayout();
        setLayout(layout);

        joinHostField.setColumns(10);
        joinPortField.setColumns(4);
        hostPortField.setColumns(4);

        var joinRow = new JPanel();
        joinRow.setBorder(BorderFactory.createTitledBorder("Join Game"));
        joinRow.add(new JLabel("host:"));
        joinRow.add(joinHostField);
        joinRow.add(new JLabel("port:"));
        joinRow.add(joinPortField);
        var joinButton = new JButton("start");
        joinRow.add(joinButton);

        var hostRow = new JPanel();
        hostRow.setBorder(BorderFactory.createTitledBorder("Host Game"));
        hostRow.add(new JLabel("port:"));
        hostRow.add(hostPortField);
        var hostButton = new JButton("start");
        hostRow.add(hostButton);

        var testRow = new JPanel();
        testRow.setBorder(BorderFactory.createTitledBorder("Test Game"));
        var testButton = new JButton("start");
        testRow.add(testButton);

        layout.putConstraint(SpringLayout.NORTH, joinRow, 10, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.NORTH, hostRow, 10, SpringLayout.SOUTH, joinRow);
        layout.putConstraint(SpringLayout.NORTH, testRow, 10, SpringLayout.SOUTH, hostRow);

        testButton.addActionListener(event->context.moveTo(TableScene.class, tableScene->{
            var executor = Executors.newSingleThreadExecutor();
            executor.submit(()->{
                try{
                    var table = new GameTable();
                    table.addPlayer("けもみみ", tableScene.getTableView());
                    table.addPlayer("guest1", TableStrategyMock.DISCARD);
                    table.addPlayer("guest2", TableStrategyMock.DISCARD);
                    table.addPlayer("guest3", TableStrategyMock.DISCARD);
                    table.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            });
            executor.shutdown();
        }));

        add(joinRow);
        add(hostRow);
        add(testRow);

    }

    private static class LButton extends JLabel{
        private static final Color NON_ACTIVE_BG = Color.WHITE;
        private static final Color NON_ACTIVE_FG = Color.BLACK;
        private static final Color ACTIVE_BG = Color.DARK_GRAY;
        private static final Color ACTIVE_FG = Color.WHITE;

        LButton(String text, Runnable action){
            setText(text);
            setVerticalAlignment(CENTER);
            setHorizontalAlignment(CENTER);
            setBorder(new LineBorder(Color.BLACK));
            setOpaque(true);
            setBackground(NON_ACTIVE_BG);
            setForeground(NON_ACTIVE_FG);
            addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e){
                    action.run();
                }

                @Override
                public void mouseEntered(MouseEvent e){
                    setBackground(ACTIVE_BG);
                    setForeground(ACTIVE_FG);
                }

                @Override
                public void mouseExited(MouseEvent e){
                    setBackground(NON_ACTIVE_BG);
                    setForeground(NON_ACTIVE_FG);
                }
            });
        }
    }

}
