package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.game.event.PaymentData;
import jp.rouh.mahjong.game.event.HandScoreData;
import jp.rouh.mahjong.game.event.RiverScoreData;
import jp.rouh.mahjong.score.HandType;
import jp.rouh.mahjong.score.Meld;
import jp.rouh.mahjong.tile.Tile;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jp.rouh.mahjong.app.view.TileLabel.TILE_WIDTH;

/**
 * 結果表示パネル。
 * <p>局の終局時に和了得点や点数移動結果などを描画するパネルです。
 * @author Rouh
 * @version 1.0
 */
class RoundResultViewPanel extends TablePanel{

    /**
     * パネル幅の基本サイズ。
     * <p>実際の幅はアプリケーションの拡大率をかけ合わせた数になります。
     */
    static final int PANEL_WIDTH = 92;

    /**
     * パネル縦の基本サイズ。
     * <p>実際の幅はアプリケーションの拡大率をかけ合わせた数になります。
     */
    static final int PANEL_HEIGHT = 68;

    /**
     * コンストラクタ。
     */
    RoundResultViewPanel(){
        setLayout(null);
        setBaseSize(PANEL_WIDTH, PANEL_HEIGHT);
        setBorder(new LineBorder(Color.BLACK));
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    /**
     * 和了得点画面を描画します。
     * @param data 和了得点DTO
     */
    void displayScore(HandScoreData data){
        removeAll();
        var handTiles = data.getHandTiles();
        var winningTile = data.getWinningTile();
        var openMelds = data.getOpenMelds();
        var upperIndicators = data.getUpperIndicators();
        var lowerIndicators = data.getLowerIndicators();
        var handTypes = data.getHandTypes();
        boolean tsumo = data.isTsumo();
        displayHand(handTiles, winningTile, openMelds, tsumo);
        displayIndicators(upperIndicators, lowerIndicators);
        displayHandTypes(handTypes);
        displayExpression(data.getScoreExpression());
    }

    /**
     * 流し満願用の和了得点画面を描画します。
     * @param data 和了得点DTO
     */
    void displayScore(RiverScoreData data){
        removeAll();
        displayIndicators(List.of(), List.of());
        displayHandTypes(List.of(data.getHandType()));
        displayExpression(data.getScoreExpression());
    }

    private void displayExpression(String expression){
        var label = TableTextLabels.ofText(expression, 60, 6);
        label.setBorder(new LineBorder(Color.BLACK));
        label.setBaseLocation(18, 58);
        add(label);
    }

    private void displayHand(List<Tile> handTiles, Tile winningTile, List<Meld> openMelds, boolean tsumo){
        int margin = 2;
        int handWidth = (handTiles.size() + 1)*TILE_WIDTH + margin + openMelds.stream()
                .mapToInt(m->m.getTilesSorted().size()*TILE_WIDTH + margin).sum();
        int handTilesWidthOffset = (PANEL_WIDTH - handWidth)/2;
        //純手牌描画
        for(int i = 0; i<handTiles.size(); i++){
            var handTileLabel = TileLabel.ofFaceUp(Direction.TOP, handTiles.get(i));
            handTileLabel.setBaseLocation(handTilesWidthOffset + i*TILE_WIDTH, 4);
            add(handTileLabel);
        }
        //ツモ・ロン表示描画
        int winningTypeWidthOffset = handTilesWidthOffset + handTiles.size()*TILE_WIDTH;
        var winningTypeLabel = TableTextLabels.ofText(tsumo?"ツモ":"ロン", 8, 4);
        winningTypeLabel.setBaseLocation(winningTypeWidthOffset, 0);
        add(winningTypeLabel);
        //和了牌描画
        int winningTileWidthOffset = handTilesWidthOffset + handTiles.size()*TILE_WIDTH + margin;
        var winningTileLabel = TileLabel.ofFaceUp(Direction.TOP, winningTile);
        winningTileLabel.setBaseLocation(winningTileWidthOffset, 4);
        add(winningTileLabel);
        //面子描画(傾けなし)
        int openMeldsWidthOffset = winningTileWidthOffset + TILE_WIDTH + margin;
        for(int meldIndex = 0, totalTileIndex = 0; meldIndex<openMelds.size(); meldIndex++){
            var meld = openMelds.get(meldIndex);
            var meldLabels = new ArrayList<TileLabel>(4);
            if(meld.isConcealed()){
                meldLabels.add(TileLabel.ofFaceDown(Direction.TOP));
                meldLabels.add(TileLabel.ofFaceUp(Direction.TOP, meld.get(1)));
                meldLabels.add(TileLabel.ofFaceUp(Direction.TOP, meld.get(2)));
                meldLabels.add(TileLabel.ofFaceDown(Direction.TOP));
            }else{
                for(var tile:meld.getTilesSorted()){
                    meldLabels.add(TileLabel.ofFaceUp(Direction.TOP, tile));
                }
            }
            for(int tileIndex = 0; tileIndex<meldLabels.size(); tileIndex++, totalTileIndex++){
                var meldLabel = meldLabels.get(tileIndex);
                meldLabel.setBaseLocation(openMeldsWidthOffset + meldIndex*margin + totalTileIndex*TILE_WIDTH, 4);
                add(meldLabel);
            }
        }
    }

    private void displayIndicators(List<Tile> upperIndicators, List<Tile> lowerIndicators){
        int upperIndicatorsWidthOffset = PANEL_WIDTH/2 - 1 - 5*TILE_WIDTH;
        int lowerIndicatorsWidthOffset = PANEL_WIDTH/2 + 1;
        var upperIndicatorLabels = new ArrayList<TileLabel>(5);
        var lowerIndicatorLabels = new ArrayList<TileLabel>(5);
        for(var tile:upperIndicators){
            upperIndicatorLabels.add(TileLabel.ofFaceUp(Direction.TOP, tile));
        }
        for(var tile:lowerIndicators){
            lowerIndicatorLabels.add(TileLabel.ofFaceUp(Direction.TOP, tile));
        }
        while(upperIndicatorLabels.size()<5){
            upperIndicatorLabels.add(TileLabel.ofFaceDown(Direction.TOP));
        }
        while(lowerIndicatorLabels.size()<5){
            lowerIndicatorLabels.add(TileLabel.ofFaceDown(Direction.TOP));
        }
        for(int i = 0; i<upperIndicatorLabels.size(); i++){
            var indicatorLabel = upperIndicatorLabels.get(i);
            indicatorLabel.setBaseLocation(upperIndicatorsWidthOffset + i*TILE_WIDTH, 14);
            add(indicatorLabel);
        }
        for(int i = 0; i<lowerIndicatorLabels.size(); i++){
            var indicatorLabel = lowerIndicatorLabels.get(i);
            indicatorLabel.setBaseLocation(lowerIndicatorsWidthOffset + i*TILE_WIDTH, 14);
            add(indicatorLabel);
        }
    }

    private void displayHandTypes(List<HandType> handTypes){
        int handTypeNameWidth = 24;
        int handTypeDoublesWidth = 4;
        int handTypeWidth = 28;
        int handTypeHeight = 4;
        int centerHandTypesWidthOffset = PANEL_WIDTH/2 - handTypeWidth/2 - 1;
        int leftHandTypesWidthOffset = PANEL_WIDTH/2 - handTypeWidth - 1;
        int rightHandTypesWidthOffset = PANEL_WIDTH/2 + 1;
        for(int i = 0; i<handTypes.size(); i++){
            var handType = handTypes.get(i);
            int handTypesHeightOffset = 24 + (i%8)*handTypeHeight;
            int handTypesWidthOffset = i>=8? rightHandTypesWidthOffset:handTypes.size()>=8? leftHandTypesWidthOffset:centerHandTypesWidthOffset;
            var handTypeNameLabel = TableTextLabels.ofText(handType.getName(), handTypeNameWidth, handTypeHeight);
            var handTypeDoublesLabel = TableTextLabels.ofText(handType.getGrade().getCode(), handTypeDoublesWidth, handTypeHeight);
            boolean lastRow = i==7 || i==(handTypes.size() - 1);
            handTypeNameLabel.setBorder(BorderFactory.createMatteBorder(1, 1, lastRow?1:0, 1, Color.BLACK));
            handTypeDoublesLabel.setBorder(BorderFactory.createMatteBorder(1, 0, lastRow?1:0, 1, Color.BLACK));
            handTypeNameLabel.setBaseLocation(handTypesWidthOffset, handTypesHeightOffset);
            handTypeDoublesLabel.setBaseLocation(handTypesWidthOffset + handTypeNameWidth, handTypesHeightOffset);
            add(handTypeNameLabel);
            add(handTypeDoublesLabel);
        }
    }

    /**
     * 点数移動画面を描画します。
     * @param payments 精算情報DTOのマップ
     */
    void displayPayments(Map<Direction, PaymentData> payments){
        removeAll();
        payments.forEach(this::displayPlayerArea);
    }

    private void displayPlayerArea(Direction d, PaymentData data){
        int rankAfter = data.getRankAfter();
        int scoreBefore = data.getScoreBefore();
        int scoreAfter = data.getScoreAfter();
        int scoreApplied = data.getScoreApplied();
        var name = data.getName();
        var wind = data.getWind();
        var rankMsg = rankAfter + "位";

        var point = TableViewPoints.ofPlayerArea(d);
        var playerArea = new TablePanel();
        playerArea.setBaseSize(36, 16);
        var rankLabel = TableTextLabels.ofText(rankMsg, 12, 4);
        var nameLabel = TableTextLabels.ofText(name, 20, 4);
        var windLabel = TableTextLabels.ofText(wind.getText(), 12, 12);
        var upperLabelMsg = scoreBefore + (scoreApplied>=0?"+":"-") + Math.abs(scoreApplied);
        var upperScoreLabel = TableTextLabels.ofText(upperLabelMsg, 20, 4);
        var lowerScoreLabel = TableTextLabels.ofText(Integer.toString(scoreAfter), 20, 8);
        rankLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1,0, Color.BLACK));
        nameLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 1,1, Color.BLACK));
        windLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 1,1, Color.BLACK));
        upperScoreLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0,1, Color.BLACK));
        lowerScoreLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1,1, Color.BLACK));

        rankLabel.setBaseLocation(0, 0);
        nameLabel.setBaseLocation(12, 0);
        windLabel.setBaseLocation(0, 4);
        upperScoreLabel.setBaseLocation(12, 4);
        lowerScoreLabel.setBaseLocation(12, 8);

        playerArea.add(rankLabel);
        playerArea.add(nameLabel);
        playerArea.add(windLabel);
        playerArea.add(upperScoreLabel);
        playerArea.add(lowerScoreLabel);
        playerArea.setBaseLocationCentered(point);
        add(playerArea);
    }
}
