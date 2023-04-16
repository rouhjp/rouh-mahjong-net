package jp.rouh.mahjong.app;

import jp.rouh.mahjong.app.view.TableScene;
import jp.rouh.mahjong.game.GameTable;
import jp.rouh.mahjong.bot.TableStrategyMock;
import jp.rouh.mahjong.game.PreparedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;

/**
 * メニュー画面。
 * @author Rouh
 * @version 1.0
 */
public class MenuScene extends Scene{
    private final JTextField nameField = new JTextField();
    private final JTextField joinPortField = new JTextField();
    private final JTextField joinHostField = new JTextField();
    private final JTextField hostPortField = new JTextField();
    private static final Logger LOG = LoggerFactory.getLogger(MenuScene.class);

    /**
     * メニュー画面を生成します。
     * @param context アプリケーションコンテキストの参照
     */
    public MenuScene(ApplicationContext context){
        super(context);
        var layout = new SpringLayout();
        setLayout(layout);

        nameField.setColumns(8);
        joinHostField.setColumns(10);
        joinPortField.setColumns(4);
        hostPortField.setColumns(4);

        var nameRow = new JPanel();
        nameRow.add(new JLabel("name:"));
        nameRow.add(nameField);

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

        var errorMessageLabel = new JLabel();
        errorMessageLabel.setForeground(Color.RED);

        layout.putConstraint(SpringLayout.NORTH, nameRow, 10, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.NORTH, joinRow, 10, SpringLayout.SOUTH, nameRow);
        layout.putConstraint(SpringLayout.NORTH, hostRow, 10, SpringLayout.SOUTH, joinRow);
        layout.putConstraint(SpringLayout.NORTH, testRow, 10, SpringLayout.SOUTH, hostRow);
        layout.putConstraint(SpringLayout.NORTH, errorMessageLabel, 10, SpringLayout.SOUTH, testRow);

        add(nameRow);
        add(joinRow);
        add(hostRow);
        add(testRow);
        add(errorMessageLabel);

        joinButton.addActionListener(event->{
            try{
                var host = joinHostField.getText();
                int port = Integer.parseInt(joinPortField.getText());
                var name = nameField.getText().isBlank()? "guest":nameField.getText();
                var roomScene = context.sceneOf(RoomScene.class);
                roomScene.initAsGuest(host, port, name,
                        ()->context.moveTo(RoomScene.class),
                        e->errorMessageLabel.setText("接続に失敗しました: " + e.getMessage()));
            }catch(NumberFormatException e){
                errorMessageLabel.setText("ポート番号が不正です: " + joinPortField.getText());
            }
        });

        hostButton.addActionListener(event->{
            try{
                int port = Integer.parseInt(hostPortField.getText());
                var name = nameField.getText().isBlank()? "guest":nameField.getText();
                var roomScene = context.sceneOf(RoomScene.class);
                roomScene.initAsHost(port, name,
                        ()->context.moveTo(RoomScene.class),
                        e->errorMessageLabel.setText("接続に失敗しました: "+e.getMessage()));
            }catch(NumberFormatException e){
                errorMessageLabel.setText("ポート番号が不正です: "+hostPortField.getText());
            }
        });

        testButton.addActionListener(event->context.moveTo(TableScene.class, tableScene->{
            tableScene.setBackScene(MenuScene.class);
            var executor = Executors.newSingleThreadExecutor();
            executor.submit(()->{
                try{
                    var table = new GameTable();
                    var name = nameField.getText().isBlank()? "you":nameField.getText();
                    table.addPlayer(name, tableScene.getTableView());
                    table.addPlayer("guest1", TableStrategyMock.DISCARD);
                    table.addPlayer("guest2", TableStrategyMock.DISCARD);
                    table.addPlayer("guest3", TableStrategyMock.DISCARD);
                    table.start();
                }catch(Exception e){
                    LOG.error("エラー発生", e);
                }
            });
            executor.shutdown();
        }));

    }
}
