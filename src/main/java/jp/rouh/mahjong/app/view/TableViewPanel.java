package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;
import jp.rouh.util.Waiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import static jp.rouh.mahjong.app.view.TileLabel.*;

/**
 * 麻雀卓を描画するパネル。
 * <p>このパネルはSwingコンポーネントで作成された
 * {@link TableObserver}インターフェース及び{@link TableStrategy}インターフェースの実装です。
 * <p>このオブジェクトのメソッドはすべて非イベントディスパッチスレッド(EDT)上から
 * 呼び出されることを想定します。
 * @author Rouh
 * @version 1.0
 */
public class TableViewPanel extends TablePanel implements TableObserver, TableStrategyAdapter{

    /**
     * 麻雀卓の幅の基本サイズ。
     * <p>実際の幅はアプリケーションの拡大率をかけ合わせた数になります。
     */
    static final int TABLE_WIDTH = 116;

    /**
     * 麻雀卓の幅の基本サイズ。
     * <p>実際の幅はアプリケーションの拡大率をかけ合わせた数になります。
     */
    static final int TABLE_HEIGHT = 116;

    /**
     * 面子間の隙間
     */
    private static final int MELD_MARGIN = TILE_WIDTH/2;

    /**
     * 上部レイヤーのZインデックス値
     */
    private static final int GLASS_LAYER = 300;

    private static final Logger LOG = LoggerFactory.getLogger(TableViewPanel.class);

    /**
     * 順序性を保ちながら描画を行うワーカスレッドのエクゼキュータ。
     * <p>呼び出し元のスレッド及びEDTをブロックせず与えられた順序で
     * 遅延描画などを行うことができます。コンポーネントの描画は
     * さらに{@link SwingUtilities#invokeLater}等を用いて
     * EDT上で処理させる必要があります。
     */
    private final ExecutorService worker = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

    //access from EDT, single thread access
    private int[] riverNextIndexes;
    private int[] riverReadyIndexes;
    private int[] meldNextIndexes;
    private int[] meldTotalOffsets;
    private int[][] additionalTileOffsets;
    private Direction[][] additionalTileTilts;
    private TableLabel[] lastRiverTileLabels;
    private TableLabel[][][] wallTileLabels;
    private TableLabel[][] handTileLabels;
    private TableLabel[] detectorLabels;
    private TableLabel[] glassLabels;
    private TableLabel[] optionButtons;
    private TableLabel[] playerNameLabels;
    private TableLabel[] playerWindLabels;
    private TableLabel[] playerScoreLabels;
    private TableLabel initialEastLabel;
    private RoundResultViewPanel roundResultWindow;
    private GameResultViewPanel gameResultWindow;
    private TableLabel drawMessageLabel;
    private TableLabel lockMessageLabel;

    //access from both EDT, non-EDT
    // volatile array needs to rewrite array itself when updated
    private volatile Tile[] handTiles;

    private final Waiter<ActionInput> actionInputWaiter = new Waiter<>();
    private final Waiter<Void> acknowledgeWaiter = new Waiter<>();

    /**
     * コンストラクタ。
     */
    TableViewPanel(){
        setLayout(null);
        setBaseSize(TABLE_WIDTH, TABLE_HEIGHT);
        setBorder(new LineBorder(Color.BLACK));
        initialize();
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if(acknowledgeWaiter.isWaiting()){
                    acknowledgeWaiter.arrived(null);
                    LOG.info("user clicked!");
                }
            }

        });
    }

    private void initialize(){
        riverNextIndexes = new int[4];
        riverReadyIndexes = new int[]{-1, -1, -1, -1};
        meldNextIndexes = new int[4];
        meldTotalOffsets = new int[4];
        additionalTileOffsets = new int[4][4];
        additionalTileTilts = new Direction[4][4];
        lastRiverTileLabels = new TableLabel[4];
        wallTileLabels = new TableLabel[4][17][2];
        handTileLabels = new TableLabel[4][14];
        detectorLabels = new TableLabel[14];
        glassLabels = new TableLabel[14];
        optionButtons = new TableLabel[5];
        playerNameLabels = new TableLabel[4];
        playerWindLabels = new TableLabel[4];
        playerScoreLabels = new TableLabel[4];
        initialEastLabel = null;
        drawMessageLabel = null;
        lockMessageLabel = null;
    }

    private void clearTable(){
        initialize();
        removeAll();
        handTiles = null;
    }

    private void putTile(TileLabel label, Point p){
        putTile(label, p, 0);
    }

    private void putTile(TileLabel label, Point p, int floor){
        label.setBaseLocationCentered(p);
        setLayer(label, floor*TABLE_HEIGHT + p.y);
        add(label);
    }

    private void appendRiverTile(Direction d, Tile t, boolean rotated){
        if(rotated) riverReadyIndexes[d.ordinal()] = riverNextIndexes[d.ordinal()];
        int riverIndex = riverNextIndexes[d.ordinal()];
        int readyIndex = riverReadyIndexes[d.ordinal()];
        var point = TableViewPoints.ofRiverTile(d, riverIndex, readyIndex);
        var headingTo = rotated?d.reversed().turnLeft():d.reversed();
        var label = TileLabel.ofFaceUp(headingTo, t);
        putTile(label, point);
        riverNextIndexes[d.ordinal()]++;
        lastRiverTileLabels[d.ordinal()] = label;
    }

    private void removeLastRiverTile(Direction d){
        if(riverNextIndexes[d.ordinal()]==0){
            throw new IllegalStateException("tile to remove not found");
        }
        if(lastRiverTileLabels[d.ordinal()]==null){
            throw new IllegalStateException("can't remove last river tile twice");
        }
        remove(lastRiverTileLabels[d.ordinal()]);
        lastRiverTileLabels[d.ordinal()] = null;
        riverNextIndexes[d.ordinal()]--;
    }

    private void putWallTile(Direction d, int column, int floor){
        var point = TableViewPoints.ofWallTile(d, column, floor);
        var label = TileLabel.ofFaceDown(d);
        wallTileLabels[d.ordinal()][column][floor] = label;
        putTile(label, point, floor);
    }

    private void putWallTileFaceUp(Direction d, int column, int floor, Tile t){
        var point = TableViewPoints.ofWallTile(d, column, floor);
        var label = TileLabel.ofFaceUp(d, t);
        wallTileLabels[d.ordinal()][column][floor] = label;
        putTile(label, point, floor);
    }

    private void removeWallTile(Direction d, int column, int floor){
        remove(wallTileLabels[d.ordinal()][column][floor]);
        wallTileLabels[d.ordinal()][column][floor] = null;
    }

    private void putHandTileFrontStand(int index, Tile t, boolean isolated){
        var point = TableViewPoints.ofHandTile(Direction.BOTTOM, index, isolated);
        var label = TileLabel.ofFrontStand(t);
        handTileLabels[Direction.BOTTOM.ordinal()][index] = label;
        putTile(label, point);
        //当たり判定用ラベル
        var detector = new TableLabel();
        detector.setBaseSize(TILE_WIDTH, TILE_HEIGHT + TILE_DEPTH);
        detector.setBaseLocationCentered(point);
        detector.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if(acknowledgeWaiter.isWaiting()){
                    TableViewPanel.this.dispatchEvent(e);
                }else if(glassLabels[index]==null){
                    if(actionInputWaiter.isWaiting()){
                        actionInputWaiter.arrived(ActionInput.ofIndex(index));
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e){
                if(!acknowledgeWaiter.isWaiting() && glassLabels[index]==null){
                    label.setTranslationCentered(0, -2);
                }
            }

            @Override
            public void mouseExited(MouseEvent e){
                if(!acknowledgeWaiter.isWaiting() && glassLabels[index]==null){
                    label.clearTranslationCentered();
                }
            }
        });
        detectorLabels[index] = detector;
        setLayer(detector, 0);
        add(detector);
    }

    private void putHandTileSideStand(Direction d, int index, boolean isolated){
        if(d==Direction.BOTTOM){
            throw new IllegalArgumentException("invalid direction: BOTTOM");
        }
        var point = TableViewPoints.ofHandTile(d, index, isolated);
        var label = TileLabel.ofSideStand(d.isSideways()?d:d.reversed());
        handTileLabels[d.ordinal()][index] = label;
        putTile(label, point);
    }

    private void putHandTileFaceUp(Direction d, int index, Tile t, boolean isolated){
        var point = TableViewPoints.ofHandTile(d, index, isolated);
        var label = TileLabel.ofFaceUp(d.reversed(), t);
        handTileLabels[d.ordinal()][index] = label;
        putTile(label, point);
    }

    private void putHandTileGlass(int index){
        var label = handTileLabels[Direction.BOTTOM.ordinal()][index];
        label.clearTranslationCentered();
        var point = label.getBaseLocation();
        var glass = new GlassLabel();
        glass.setBaseLocationCentered(point);
        glassLabels[index] = glass;
        setLayer(glass, GLASS_LAYER);
        add(glass);
    }

    private void clearHandTiles(Direction d){
        for(int i = 0; i<14; i++){
            if(handTileLabels[d.ordinal()][i]!=null){
                remove(handTileLabels[d.ordinal()][i]);
                handTileLabels[d.ordinal()][i] = null;
                if(d==Direction.BOTTOM){
                    if(glassLabels[i]!=null){
                        remove(glassLabels[i]);
                        glassLabels[i] = null;
                    }
                    if(detectorLabels[i]!=null){
                        remove(detectorLabels[i]);
                        detectorLabels[i] = null;
                    }
                }
            }
        }
    }

    private void putMeldTile(Direction d, Tile t, int offset){
        var point = TableViewPoints.ofMeldTile(d, offset, false, false);
        var label = TileLabel.ofFaceUp(d.reversed(), t);
        putTile(label, point);
    }

    private void putMeldTileRotated(Direction d, Tile t, Direction tilt, int offset){
        var point = TableViewPoints.ofMeldTile(d, offset, true, false);
        var label = TileLabel.ofFaceUp(tilt.reversed(), t);
        putTile(label, point);
    }

    private void putMeldTileAdded(Direction d, Tile t, Direction tilt, int offset){
        var point = TableViewPoints.ofMeldTile(d, offset, true, true);
        var label = TileLabel.ofFaceUp(tilt.reversed(), t);
        putTile(label, point);
    }

    private void putMeldTileFaceDown(Direction d, int offset){
        var point = TableViewPoints.ofMeldTile(d, offset, false, false);
        var label = TileLabel.ofFaceDown(d.reversed());
        putTile(label, point);
    }

    private void appendTiltMeld(Direction d, List<Tile> tiles, int tiltIndex, Direction tilt){
        if(tiles.size()!=3 && tiles.size()!=4){
            throw new IllegalArgumentException("invalid size of meld tiles: "+tiles);
        }
        for(int index = tiles.size() - 1; index>=0; index--){
            if(index==tiltIndex){
                putMeldTileRotated(d, tiles.get(index), tilt, meldTotalOffsets[d.ordinal()]);
                additionalTileOffsets[d.ordinal()][meldNextIndexes[d.ordinal()]] = meldTotalOffsets[d.ordinal()];
                additionalTileTilts[d.ordinal()][meldNextIndexes[d.ordinal()]] = tilt;
                meldTotalOffsets[d.ordinal()] += TILE_HEIGHT;
            }else{
                putMeldTile(d, tiles.get(index), meldTotalOffsets[d.ordinal()]);
                meldTotalOffsets[d.ordinal()] += TILE_WIDTH;
            }
        }
        meldNextIndexes[d.ordinal()]++;
        meldTotalOffsets[d.ordinal()] += MELD_MARGIN;
    }

    private void appendSelfQuad(Direction d, List<Tile> tiles){
        if(tiles.size()!=4){
            throw new IllegalArgumentException("invalid size of quad tiles: "+tiles);
        }
        for(int index = 3; index >=0; index--){
            if(index==0 || index==3){
                putMeldTileFaceDown(d, meldTotalOffsets[d.ordinal()]);
            }else{
                putMeldTile(d, tiles.get(index), meldTotalOffsets[d.ordinal()]);
            }
            meldTotalOffsets[d.ordinal()] += TILE_WIDTH;
        }
        meldNextIndexes[d.ordinal()]++;
        meldTotalOffsets[d.ordinal()] += MELD_MARGIN;
    }

    private void addTileToMeld(Direction d, int meldIndex, Tile t){
        int offset = additionalTileOffsets[d.ordinal()][meldIndex];
        var tilt = additionalTileTilts[d.ordinal()][meldIndex];
        putMeldTileAdded(d, t, tilt, offset);
    }

    private void putRoundSign(Wind wind, int count){
        var text = wind.getText() + List.of("一", "二", "三", "四").get(count - 1) + "局";
        var label = TableTextLabels.ofText(text, 10, 4);
        int x = TableViewPoints.TABLE_CENTER.x;
        int y = TableViewPoints.TABLE_CENTER.y - 3;
        label.setBaseLocationCentered(x, y);
        add(label);
    }

    private void putStreakSign(int streak){
        var text = streak + "本場";
        var label = TableTextLabels.ofText(text, 10, 3);
        label.setForeground(Color.GRAY);
        int x = TableViewPoints.TABLE_CENTER.x;
        int y = TableViewPoints.TABLE_CENTER.y;
        label.setBaseLocationCentered(x, y);
        add(label);
    }

    private void putDepositSign(int deposit){
        var text = deposit + "供託";
        var label = TableTextLabels.ofText(text, 10, 3);
        label.setForeground(Color.GRAY);
        int x = TableViewPoints.TABLE_CENTER.x;
        int y = TableViewPoints.TABLE_CENTER.y + 2;
        label.setBaseLocationCentered(x, y);
        add(label);
    }

    private void putLastSign(){
        var text = "オーラス";
        var label = TableTextLabels.ofText(text, 10, 3);
        int x = TableViewPoints.TABLE_CENTER.x;
        int y = TableViewPoints.TABLE_CENTER.y + 4;
        label.setBaseLocationCentered(x, y);
        add(label);
    }

    private void putInitialEast(Direction d){
        var text = Wind.EAST.getText();
        var point = TableViewPoints.ofInitialEast(d);
        var label = TableTextLabels.ofRotatedImageText(d, text, 8, 4, Color.LIGHT_GRAY);
        label.setBaseLocationCentered(point);
        label.setBorder(new LineBorder(Color.BLACK));
        initialEastLabel = label;
        add(label);
    }

    private void putPlayerName(Direction d, String name){
        var point = TableViewPoints.ofPlayerName(d);
        var label = TableTextLabels.ofRotatedLeftAlignImageText(d, name, 18, 4);
        label.setBaseLocationCentered(point);
        playerNameLabels[d.ordinal()] = label;
        add(label);
    }

    private void putPlayerWind(Direction d, Wind wind){
        var text = wind.getText();
        var point = TableViewPoints.ofPlayerWind(d);
        var label = TableTextLabels.ofRotatedImageText(d, text, 4, 4);
        label.setBaseLocationCentered(point);
        playerWindLabels[d.ordinal()] = label;
        add(label);
    }

    private void putPlayerScore(Direction d, int score){
        var text = Integer.toString(score);
        var point = TableViewPoints.ofPlayerScore(d);
        var label = TableTextLabels.ofRotatedImageText(d, text, 12, 4);
        label.setBaseLocationCentered(point);
        playerScoreLabels[d.ordinal()] = label;
        add(label);
    }

    private void showPlayerMessage(Direction d, Declaration dc, int duration){
        var text = dc.getText();
        var point = TableViewPoints.ofPlayerMessage(d);
        var label = TableTextLabels.ofText(text, 16, 5);
        label.setOpaque(true);
        label.setBackground(Color.ORANGE);
        label.setBorder(new LineBorder(Color.BLACK));
        label.setBaseLocationCentered(point);
        setLayer(label, GLASS_LAYER);
        add(label);
        var fadeOutTimer = new Timer(duration, e->{
            remove(label);
            repaint();
        });
        fadeOutTimer.setRepeats(false);
        fadeOutTimer.start();
    }

    private void putOptionButton(ActionInput choice, int index){
        var text = choice.getOptionText();
        var point = TableViewPoints.ofOptionButton(index);
        var label = TableTextLabels.ofText(text, 16, 4);
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setBackground(Color.BLACK);
        label.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                label.setForeground(Color.BLACK);
                label.setBackground(Color.ORANGE);
            }

            @Override
            public void mouseExited(MouseEvent e){
                label.setForeground(Color.WHITE);
                label.setBackground(Color.BLACK);
            }

            @Override
            public void mousePressed(MouseEvent e){
                if(actionInputWaiter.isWaiting()){
                    actionInputWaiter.arrived(choice);
                }
            }
        });
        label.setBorder(new LineBorder(Color.BLACK));
        label.setBaseLocationCentered(point);
        optionButtons[index] = label;
        setLayer(label, GLASS_LAYER);
        add(label);
    }

    private void putReadyBone(Direction d){
        var point = TableViewPoints.ofReadyBone(d);
        int width = d.isSideways()?2:12;
        int height = d.isSideways()?12:2;
        var label = new TableLabel();
        label.setBaseSize(width, height);
        label.setBaseLocationCentered(point);
        label.setImageIconLater(()-> TableImageManager.getInstance().getReadyBoneImage(d));
        label.setBorder(new LineBorder(Color.BLACK));
        add(label);
    }

    private void showDice(Direction d, int index, int value, int duration){
        var point = TableViewPoints.ofDice(d, index);
        var label = new TableLabel();
        label.setBaseSize(4, 4);
        label.setBaseLocationCentered(point);
        label.setBorder(new LineBorder(Color.BLACK));
        label.setImageIconLater(()->TableImageManager.getInstance().getDiceImage(value));
        add(label);
        var fadeOutTimer = new Timer(duration, e->{
            remove(label);
            repaint();
        });
        fadeOutTimer.setRepeats(false);
        fadeOutTimer.start();
    }

    private void putDrawnMessage(DrawType drawType){
        var text = drawType.getText();
        var point = TableViewPoints.TABLE_CENTER;
        var label = TableTextLabels.ofText(text, 24, 8);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBaseLocationCentered(point);
        label.setBorder(new LineBorder(Color.BLACK));
        drawMessageLabel = label;
        setLayer(label, GLASS_LAYER);
        add(label);
    }

    private void putRoundResultWindow(){
        if(roundResultWindow==null){
            roundResultWindow = new RoundResultViewPanel();
            roundResultWindow.setBaseLocationCentered(TableViewPoints.TABLE_CENTER);
            setLayer(roundResultWindow, GLASS_LAYER);
            add(roundResultWindow);
        }else{
            roundResultWindow.removeAll();
            roundResultWindow.repaint();
        }
    }

    private void putPaymentResultWindow(Map<Direction, PaymentData> payments){
        putRoundResultWindow();
        roundResultWindow.displayPayments(payments);
    }

    private void putScoringResultWindow(HandScoreData data){
        putRoundResultWindow();
        roundResultWindow.displayScore(data);
    }

    private void putScoringResultWindow(RiverScoreData data){
        putRoundResultWindow();
        roundResultWindow.displayScore(data);
    }

    private void putGameResultWindow(List<GameScoreData> scores){
        if(gameResultWindow==null){
            gameResultWindow = new GameResultViewPanel();
            gameResultWindow.setBaseLocationCentered(TableViewPoints.TABLE_CENTER);
            setLayer(gameResultWindow, GLASS_LAYER);
            add(gameResultWindow);
            gameResultWindow.displayPlayerRank(scores);
        }else{
            gameResultWindow.removeAll();
            gameResultWindow.repaint();
        }
    }

    private void requireCallOnNonEDT(){
        if(SwingUtilities.isEventDispatchThread()){
            throw new IllegalThreadStateException("can't block EDT");
        }
    }

    @Override
    public List<Tile> getShownHandTiles(){
        return List.of(handTiles);
    }

    @Override
    public ActionInput waitForInput(List<ActionInput> choices){
        requireCallOnNonEDT();
        var future = worker.submit(()->{
            SwingUtilities.invokeLater(()->{
                for(int i = 0; i<choices.size(); i++){
                    if(choices.get(i).isOption()){
                        putOptionButton(choices.get(i), i);
                    }
                }
                for(int i = 0; i<14; i++){
                    if(handTileLabels[Direction.BOTTOM.ordinal()][i]!=null){
                        if(!choices.contains(ActionInput.ofIndex(i))){
                            putHandTileGlass(i);
                        }
                    }
                }
            });
            try{
                var input = actionInputWaiter.waitForArrival();
//                actionSemaphore.acquire();
                SwingUtilities.invokeAndWait(()->{
                    for(int i = 0; i<14; i++){
                        if(glassLabels[i]!=null){
                            remove(glassLabels[i]);
                            glassLabels[i] = null;
                        }
                    }
                    for(int i = 0; i<5; i++){
                        if(optionButtons[i]!=null){
                            remove(optionButtons[i]);
                            optionButtons[i] = null;
                        }
                    }
                    repaint();
                });
                return input;
            }catch(InterruptedException | InvocationTargetException e){
                return null;
            }
        });
        try{
            return future.get();
        }catch(InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void waitForAcknowledge(){
        requireCallOnNonEDT();
        try{
            LOG.info("--acquired--" + Thread.currentThread().getName());
            acknowledgeWaiter.waitForArrival();
            LOG.info("--released--" + Thread.currentThread().getName());
        }catch(InterruptedException e){
            LOG.info("ack interrupted "+e.getMessage());
        }
    }

    @Override
    public void gameStarted(List<ProfileData> players){
        LOG.info("gameStarted "+players);
    }

    @Override
    public void gameFinished(List<GameScoreData> scores){
        LOG.info("gameSettled "+scores);
        worker.submit(()->SwingUtilities.invokeLater(()->putGameResultWindow(scores)));
    }

    @Override
    public void temporarySeatUpdated(Map<Side, PlayerTempData> players){
        LOG.info("temporarySeatUpdated "+players);
        worker.submit(()->SwingUtilities.invokeLater(()->
                players.forEach((side, data)->{
                    var dir = Direction.of(side);
                    if(playerNameLabels[dir.ordinal()]!=null){
                        remove(playerNameLabels[dir.ordinal()]);
                    }
                    if(playerWindLabels[dir.ordinal()]!=null){
                        remove(playerWindLabels[dir.ordinal()]);
                    }
                    if(playerScoreLabels[dir.ordinal()]!=null){
                        remove(playerScoreLabels[dir.ordinal()]);
                    }
                    putPlayerName(dir, data.getName());
                    putPlayerWind(dir, data.getSeatWind());
                })
        ));
    }

    @Override
    public void seatUpdated(Map<Side, PlayerData> players){
        LOG.info("seatUpdated " + players);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->players.forEach((side, data)->{
                    var dir = Direction.of(side);
                    if(playerNameLabels[dir.ordinal()]!=null){
                        remove(playerNameLabels[dir.ordinal()]);
                    }
                    if(playerWindLabels[dir.ordinal()]!=null){
                        remove(playerWindLabels[dir.ordinal()]);
                    }
                    if(playerScoreLabels[dir.ordinal()]!=null){
                        remove(playerScoreLabels[dir.ordinal()]);
                    }
                    putPlayerName(dir, data.getName());
                    putPlayerWind(dir, data.getSeatWind());
                    putPlayerScore(dir, data.getScore());
                    if(data.getInitialSeatWind()==Wind.EAST){
                        if(initialEastLabel==null){
                            putInitialEast(dir);
                        }
                    }
                }));
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){
        LOG.info("roundStarted "+wind+" count="+count+" streak="+streak+" deposit="+deposit+" last="+last);
        worker.submit(()->SwingUtilities.invokeLater(()->{
            putRoundSign(wind, count);
            putStreakSign(streak);
            putDepositSign(deposit);
            if(last){
                putLastSign();
            }
        }));
    }

    @Override
    public void roundDrawn(DrawType drawType){
        LOG.info("roundDrawn "+drawType);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->putDrawnMessage(drawType));
                waitForAcknowledge();
                SwingUtilities.invokeAndWait(()->{
                    remove(drawMessageLabel);
                    drawMessageLabel = null;
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handScoreNotified(List<HandScoreData> scores){
        LOG.info("roundSettled "+scores);
        worker.submit(()->{
            try{
                for(var score: scores){
                    SwingUtilities.invokeAndWait(()->putScoringResultWindow(score));
                    waitForAcknowledge();
                }
                SwingUtilities.invokeAndWait(()->{
                    remove(roundResultWindow);
                    roundResultWindow = null;
                    repaint();
                });
            }catch(InterruptedException | InvocationTargetException e){
                LOG.debug("interrupted "+e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void riverScoreNotified(List<RiverScoreData> scores){
        LOG.info("roundSettledByRiver "+scores);
        worker.submit(()->{
           try{
               for(var score:scores){
                   SwingUtilities.invokeAndWait(()->putScoringResultWindow(score));
                   waitForAcknowledge();
               }
               SwingUtilities.invokeAndWait(()->{
                   remove(roundResultWindow);
                   roundResultWindow = null;
                   repaint();
               });
           }catch(InterruptedException | InvocationTargetException e){
               LOG.debug("interrupted " + e.getMessage());
               throw new RuntimeException(e);
           }
        });
    }

    @Override
    public void paymentNotified(Map<Side, PaymentData> payments){
        LOG.info("paymentSettled "+payments);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    var paymentsByDirection = new HashMap<Direction, PaymentData>();
                    payments.forEach((side, payment)->paymentsByDirection.put(Direction.of(side), payment));
                    putPaymentResultWindow(paymentsByDirection);
                });
                waitForAcknowledge();
                SwingUtilities.invokeAndWait(()->{
                    remove(roundResultWindow);
                    roundResultWindow = null;
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void roundFinished(){
        LOG.info("roundFinished");
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    clearTable();
                    repaint();
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void diceRolled(Side side, int dice1, int dice2){
        LOG.info("diceRolled "+side+" dice1="+dice1+" dice2="+dice2);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    showDice(Direction.of(side), 0, dice1, 1500);
                    showDice(Direction.of(side), 1, dice2, 1500);
                });
                TimeUnit.MILLISECONDS.sleep(500);
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void declared(Side side, Declaration declaration){
        LOG.info("declared "+side+" "+declaration);
        worker.submit(()->SwingUtilities.invokeLater(()->showPlayerMessage(Direction.of(side), declaration, 2000)));
    }

    @Override
    public void readyBoneAdded(Side side){
        LOG.info("readyBoneAdded "+side);
        worker.submit(()->SwingUtilities.invokeLater(()->putReadyBone(Direction.of(side))));
    }

    @Override
    public void wallGenerated(){
        LOG.info("wallGenerated");
        worker.submit(()->{
            var directions = Direction.values();
            try{
                for(int c = 0; c<17; c++){
                    for(int f = 0; f<2; f++){
                        for(var d: directions){
                            int C = c;
                            int F = f;
                            TimeUnit.MILLISECONDS.sleep(5);
                            SwingUtilities.invokeAndWait(()->putWallTile(d, C, F));
                        }
                    }
                }
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void wallTileTaken(Side side, int column, int floor){
        LOG.info("wallTileTaken " + side + " column=" + column + " floor=" + floor);
        worker.submit(()->{
            try{
                TimeUnit.MILLISECONDS.sleep(40);
                SwingUtilities.invokeAndWait(()->{
                    removeWallTile(Direction.of(side), column, floor);
                    repaint();
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void wallTileRevealed(Side side, int column, Tile tile){
        LOG.info("wallTileRevealed "+side+" column="+column+" "+tile);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    removeWallTile(Direction.of(side), column, 1);
                    putWallTileFaceUp(Direction.of(side), column, 1, tile);
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void turnStarted(Side side){
        LOG.info("turnStarted "+side);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    for(var playerWindLabel:playerWindLabels){
                        playerWindLabel.setBorder(null);
                    }
                    var direction = Direction.of(side);
                    var border = BorderFactory.createMatteBorder(
                            direction==Direction.TOP?1:0,
                            direction==Direction.LEFT?1:0,
                            direction==Direction.BOTTOM?1:0,
                            direction==Direction.RIGHT?1:0,
                            Color.RED
                    );
                    playerWindLabels[direction.ordinal()].setBorder(border);
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });

        if(side==Side.SELF){
            worker.submit(()->{
                try{
                    SwingUtilities.invokeAndWait(()->{
                        if(lockMessageLabel!=null){
                            remove(lockMessageLabel);
                            lockMessageLabel = null;
                            repaint();
                        }
                    });
                }catch(InterruptedException | InvocationTargetException e){
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public void handUpdated(Side side, int size, boolean wide){
        LOG.info("handUpdated "+side+" size="+size+" wide="+wide);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    clearHandTiles(Direction.of(side));
                    for(int i = 0; i<size; i++){
                        putHandTileSideStand(Direction.of(side), i, wide && i==(size - 1));
                    }
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handUpdated(List<Tile> allTiles, boolean wide){
        LOG.info("handUpdated "+allTiles+" wide="+wide);
        //volatile array update
        handTiles = allTiles.toArray(Tile[]::new);
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    clearHandTiles(Direction.BOTTOM);
                    for(int i = 0; i<allTiles.size(); i++){
                        putHandTileFrontStand(i, allTiles.get(i), wide && i==(allTiles.size() - 1));
                    }
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handRevealed(Side side, List<Tile> allTiles, boolean wide){
        LOG.info("handRevealed "+side+" "+allTiles+" wide="+wide);
        worker.submit(()->
            SwingUtilities.invokeLater(()->{
                clearHandTiles(Direction.of(side));
                for(int i = 0; i<allTiles.size(); i++){
                    putHandTileFaceUp(Direction.of(side), i, allTiles.get(i), wide && i==(allTiles.size() - 1));
                }
            })
        );
    }

    @Override
    public void handLocked(){
        LOG.info("handLocked");
        worker.submit(()->{
            try{
                SwingUtilities.invokeAndWait(()->{
                    if(lockMessageLabel==null){
                        var text = "フリテン";
                        int handSize = (int)Arrays.stream(handTileLabels[Direction.BOTTOM.ordinal()]).filter(Objects::nonNull).count();
                        var point = TableViewPoints.ofLockMessage(handSize);
                        var label = TableTextLabels.ofText(text, 10, 3);
                        label.setBaseLocationCentered(point);
                        lockMessageLabel = label;
                        setLayer(label, GLASS_LAYER);
                        add(label);
                    }
                });
            }catch(InterruptedException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void riverTileAdded(Side side, Tile tile, boolean tilt){
        LOG.info("riverTileAdded "+side+" "+tile+" tilt="+tilt);
        worker.submit(()->{
           try{
               SwingUtilities.invokeAndWait(()->appendRiverTile(Direction.of(side), tile, tilt));
           }catch(InterruptedException | InvocationTargetException e){
               throw new RuntimeException(e);
           }
        });
    }

    @Override
    public void riverTileTaken(Side side){
        LOG.info("riverTileTaken "+side);
        worker.submit(()->SwingUtilities.invokeLater(()->removeLastRiverTile(Direction.of(side))));
    }

    @Override
    public void tiltMeldAdded(Side side, Side tilt, List<Tile> tiles){
        LOG.info("tiltMeldAdded "+side+" "+tilt+" "+tiles);
        worker.submit(()->SwingUtilities.invokeLater(()->{
            var dir = Direction.of(side);
            switch(tilt){
                case LEFT -> appendTiltMeld(dir, tiles, 0, dir.turnLeft());
                case ACROSS -> appendTiltMeld(dir, tiles, 1, dir.turnRight());
                case RIGHT -> appendTiltMeld(dir, tiles, tiles.size() - 1, dir.turnLeft());
            }
        }));
    }

    @Override
    public void selfQuadAdded(Side side, List<Tile> tiles){
        LOG.info("selfQuadAdded "+side+" "+tiles);
        worker.submit(()->SwingUtilities.invokeLater(()->appendSelfQuad(Direction.of(side), tiles)));
    }

    @Override
    public void meldTileAdded(Side side, int index, Tile tile){
        LOG.info("meldTileAdded "+side+" index="+index+" "+tile);
        worker.submit(()->SwingUtilities.invokeLater(()->addTileToMeld(Direction.of(side), index, tile)));
    }

    private static class GlassLabel extends TableLabel{
        private GlassLabel(){
            setBaseSize(TILE_WIDTH, TILE_HEIGHT + TILE_DEPTH);
        }

        @Override
        protected void paintComponent(Graphics g){
            var g2d = (Graphics2D)g.create();
            var rect = new Rectangle(0, 0, getWidth(), getHeight());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.fill(rect);
            g2d.dispose();
        }
    }
}
