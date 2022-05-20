package jp.rouh.mahjong.app;

import jp.rouh.mahjong.app.view.TableScene;
import jp.rouh.mahjong.game.event.ForwardingTableStrategy;
import jp.rouh.mahjong.net.*;
import jp.rouh.util.net.BioMessageClient;
import jp.rouh.util.net.MessageConnection;
import jp.rouh.util.net.msg.MessageConverter;
import jp.rouh.util.net.msg.RemoteConnections;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * ルーム画面。
 * 麻雀ゲーム開始のためメンバーを集める部屋の画面。
 * @author Rouh
 * @version 1.0
 */
public class RoomScene extends Scene{
    private static final int HEADER_BASE_HEIGHT = 12;
    private static final int MAIN_BASE_HEIGHT = 96;
    private static final int FOOTER_BASE_HEIGHT = 12;
    private static final int BUTTON_WIDTH = 20;
    private final MessageConverter converter = RoomMessageConverters.getConverter();
    private final MemberPanel[] memberPanels = new MemberPanel[4];
    private final AtomicBoolean ready = new AtomicBoolean(false);
    private final TableDispatcher dispatcher;
    private final JLabel readyButton;
    private final JLabel startButton;
    private Connection connection;

    /**
     * 接続インターフェース。
     * ゲーム主催時のローカルサーバ, もしくは参加時のリモートサーバへの接続。
     */
    private interface Connection{

        /**
         * 接続先を{@link Room}として扱うプロキシを取得します。
         * @see RemoteConnections
         * @return 取得
         */
        Room getRoom();

        /**
         * 接続を切断します。
         */
        void close();
    }

    /**
     * 接続先からのリクエストを受け取り, 適宜{@link TableScene}に委譲します。
     * {@link ForwardingTableStrategy}
     */
    private class TableDispatcher extends ForwardingTableStrategy implements RoomObserver{
        private TableDispatcher(){
            super(getContext().sceneOf(TableScene.class).getTableView());
        }

        @Override
        public void roomUpdated(List<RoomMemberData> members){
            for(int i = 0; i<4; i++){
                if(i>=members.size()){
                    memberPanels[i].displayNone();
                }else{
                    memberPanels[i].displayMemberData(members.get(i));
                }
            }
            startButton.setEnabled(connection instanceof HostConnection && members.stream().allMatch(RoomMemberData::isReady));
        }

        @Override
        public void gameStarted(){
            getContext().moveTo(TableScene.class, scene->scene.setBackScene(RoomScene.class));
            //ゲーム開始後は準備完了状態をfalseへ
            toggleReady();
        }
    }

    /**
     * ゲーム主催時の接続
     */
    private class HostConnection implements Connection{
        private final RoomServer localServer;
        private final MessageConnection localConnection;
        private final Room localRoom;
        private HostConnection(int port) throws IOException{
            localServer = new RoomServer();
            localServer.start(port);
            localConnection = new BioMessageClient("localhost", port);
            localConnection.addListener(RemoteConnections.newDispatcher(dispatcher, localConnection, converter));
            localRoom = RemoteConnections.newProxy(Room.class, localConnection, converter);
        }

        @Override
        public Room getRoom(){
            return localRoom;
        }

        @Override
        public void close(){
            localConnection.close();
            localServer.close();
        }
    }

    /**
     * ゲーム参加時の接続
     */
    private class JoinConnection implements Connection{
        private final MessageConnection remoteConnection;
        private final Room remoteRoom;

        JoinConnection(String host, int port) throws IOException{
            remoteConnection = new BioMessageClient(host, port);
            remoteConnection.addListener(RemoteConnections.newDispatcher(dispatcher, remoteConnection, converter));
            remoteRoom = RemoteConnections.newProxy(Room.class, remoteConnection, converter);
        }

        @Override
        public Room getRoom(){
            return remoteRoom;
        }

        @Override
        public void close(){
            remoteConnection.close();
        }
    }

    /**
     * ルーム画面を生成します。
     * @param context アプリケーションコンテキストの参照
     */
    RoomScene(ApplicationContext context){
        super(context);
        this.dispatcher = new TableDispatcher();
        var sceneLayout = new SpringLayout();
        setLayout(sceneLayout);

        var headerPanel = new AutoResizablePanel(Scene.BASE_WIDTH, HEADER_BASE_HEIGHT, context);
        var mainPanel = new AutoResizablePanel(Scene.BASE_WIDTH, MAIN_BASE_HEIGHT, context);
        var footerPanel = new AutoResizablePanel(Scene.BASE_WIDTH, FOOTER_BASE_HEIGHT, context);
        sceneLayout.putConstraint(SpringLayout.NORTH, headerPanel, 0, SpringLayout.NORTH, this);
        sceneLayout.putConstraint(SpringLayout.NORTH, mainPanel, 0, SpringLayout.SOUTH, headerPanel);
        sceneLayout.putConstraint(SpringLayout.NORTH, footerPanel, 0, SpringLayout.SOUTH, mainPanel);
        add(headerPanel);
        add(mainPanel);
        add(footerPanel);

        var headerLayout = new SpringLayout();
        headerPanel.setLayout(headerLayout);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        var exitButton = new LButton(BUTTON_WIDTH, HEADER_BASE_HEIGHT, this::exit);
        exitButton.setText("EXIT");
        var statusLabel = new JLabel("connected");
        headerLayout.putConstraint(SpringLayout.WEST, statusLabel, 0, SpringLayout.WEST, headerPanel);
        headerLayout.putConstraint(SpringLayout.EAST, exitButton, 0, SpringLayout.EAST, headerPanel);
        headerPanel.add(statusLabel);
        headerPanel.add(exitButton);

        var footerLayout = new SpringLayout();
        footerPanel.setLayout(footerLayout);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        var readyButton = new LButton(BUTTON_WIDTH, FOOTER_BASE_HEIGHT, this::toggleReady);
        var startButton = new LButton(BUTTON_WIDTH, FOOTER_BASE_HEIGHT, this::start);
        this.readyButton = readyButton;
        this.startButton = startButton;
        readyButton.setText("READY");
        startButton.setText("START");
        footerLayout.putConstraint(SpringLayout.WEST, readyButton, 0, SpringLayout.WEST, footerPanel);
        footerLayout.putConstraint(SpringLayout.WEST, startButton, 0, SpringLayout.EAST, readyButton);
        footerPanel.add(readyButton);
        footerPanel.add(startButton);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        for(int i = 0; i<4; i++){
            var memberPanel = new MemberPanel(i);
            memberPanels[i] = memberPanel;
            mainPanel.add(memberPanel);
        }
    }

    /**
     * ゲームを主催し, ルーム画面を初期化します。
     * @param port ポート番号
     * @param name 接続名
     * @param callback 完了時のコールバック処理
     * @param exceptionHandler 例外発生時のコールバック処理
     */
    void initAsHost(int port, String name, Runnable callback, Consumer<IOException> exceptionHandler){
        //接続はブロッキング処理のためワークスレッド上で実行します
        new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground(){
                try{
                    connection = new HostConnection(port);
                    connection.getRoom().notifyName(name);
                    SwingUtilities.invokeLater(callback);
                }catch(IOException e){
                    exceptionHandler.accept(e);
                }
                return null;
            }
        }.execute();
    }

    /**
     * ゲームに参加し, ルーム画面を初期化します。
     * @param host ホスト名
     * @param port ポート番号
     * @param name 接続名
     * @param callback 完了時のコールバック処理
     * @param exceptionHandler 例外発生時のコールバック処理
     */
    void initAsGuest(String host, int port, String name, Runnable callback, Consumer<IOException> exceptionHandler){
        //接続はブロッキング処理のためワークスレッド上で実行します
        new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground(){
                try{
                    connection = new JoinConnection(host, port);
                    connection.getRoom().notifyName(name);
                    SwingUtilities.invokeLater(callback);
                }catch(IOException e){
                    exceptionHandler.accept(e);
                }
                return null;
            }
        }.execute();
    }

    /**
     * スタートボタン押下時の処理
     */
    private void start(){
        var room = connection.getRoom();
        room.start();
    }

    /**
     * 準備完了ボタン押下時の処理
     */
    public void toggleReady(){
        connection.getRoom().notifyReady(!ready.get());
        ready.set(!ready.get());
        readyButton.setText(ready.get()?"CANCEL":"READY");
    }

    /**
     * 退室ボタン押下時の処理
     */
    public void exit(){
        connection.close();
        connection = null;
        getContext().moveTo(MenuScene.class);
    }

    private class MemberPanel extends AutoResizablePanel{
        private final JLabel nameLabel = new JLabel();
        private final JLabel readyLabel = new JLabel("ready");
        private MemberPanel(int index){
            super(Scene.BASE_WIDTH, MAIN_BASE_HEIGHT/4, getContext());
            var indexLabel = new JLabel(new String[]{"①", "②", "③", "④"}[index%4]);
            readyLabel.setForeground(Color.RED);
            setLayout(new FlowLayout());
            add(indexLabel);
            add(nameLabel);
            add(readyLabel);
            setVisible(false);
        }

        private void displayMemberData(RoomMemberData data){
            nameLabel.setText(data.getName());
            readyLabel.setVisible(data.isReady());
            setVisible(true);
        }

        private void displayNone(){
            setVisible(false);
        }
    }

    private class LButton extends JLabel{
        private static final Color NON_ACTIVE_BG = Color.WHITE;
        private static final Color NON_ACTIVE_FG = Color.BLACK;
        private static final Color ACTIVE_BG = Color.DARK_GRAY;
        private static final Color ACTIVE_FG = Color.WHITE;
        private final int baseWidth;
        private final int baseHeight;
        LButton(int baseWidth, int baseHeight, Runnable action){
            this.baseWidth = baseWidth;
            this.baseHeight = baseHeight;
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

        @Override
        public Dimension getPreferredSize(){
            int weight = getContext().getSizeWeight();
            return new Dimension(baseWidth*weight, baseHeight*weight);
        }
    }
}
