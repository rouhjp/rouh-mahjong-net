package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.game.event.GameScoreData;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 対局結果表示パネル。
 * <p>対局の終局時に順位を描画するパネルです。
 * @author Rouh
 * @version 1.0
 */
public class GameResultViewPanel extends TablePanel{

    /**
     * パネル幅の基本サイズ。
     * <p>実際の幅はアプリケーションの拡大率をかけ合わせた数になります。
     */
    static final int PANEL_WIDTH = 65;

    /**
     * パネル縦の基本サイズ。
     * <p>実際の幅はアプリケーションの拡大率をかけ合わせた数になります。
     */
    static final int PANEL_HEIGHT = 90;

    /**
     * コンストラクタ。
     */
    GameResultViewPanel(){
        setLayout(null);
        setBaseSize(PANEL_WIDTH, PANEL_HEIGHT);
        setBorder(new LineBorder(Color.BLACK));
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    void displayPlayerRank(List<GameScoreData> dataList){
        removeAll();
        for(int i = 0; i<dataList.size(); i++){
            var rank = (i + 1)+"位";
            var name = dataList.get(i).getName();
            var score = Integer.toString(dataList.get(i).getScore());
            double point = dataList.get(i).getResultPoint();
            var rankLabel = TableTextLabels.ofText(rank, 15, 10);
            var nameLabel = TableTextLabels.ofText(name, 40, 10);
            var scoreLabel = TableTextLabels.ofText(score, 30, 10);
            var pointLabel = TableTextLabels.ofText(stringOf(point), 25, 10);
            rankLabel.setBorder(BorderFactory.createMatteBorder(i==0?1:0, 1, 0, 0, Color.BLACK));
            nameLabel.setBorder(BorderFactory.createMatteBorder(i==0?1:0, 0, 0, 1, Color.BLACK));
            scoreLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
            pointLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            scoreLabel.setForeground(new Color(100, 150, 250));
            pointLabel.setForeground(point>=0?new Color(10, 150, 100):Color.RED);
            rankLabel.setBaseLocation(5, 5 + 20*i);
            nameLabel.setBaseLocation(20, 5 + 20*i);
            scoreLabel.setBaseLocation(5, 15 + 20*i);
            pointLabel.setBaseLocation(35, 15 + 20*i);
            add(rankLabel);
            add(nameLabel);
            add(scoreLabel);
            add(pointLabel);
        }
    }

    private static String stringOf(double point){
        return (point>0?"+":"")+BigDecimal.valueOf(point).toPlainString();
    }
}
