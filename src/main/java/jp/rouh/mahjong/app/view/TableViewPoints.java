package jp.rouh.mahjong.app.view;

import java.awt.*;

/**
 * 卓上の牌の座標を取得するユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
final class TableViewPoints{

    /**
     * 麻雀卓の中心座標
     */
    static final Point TABLE_CENTER = new Point(TableViewPanel.TABLE_WIDTH/2, TableViewPanel.TABLE_HEIGHT/2);

    /**
     * 結果表示画面の中心座標
     */
    static final Point RESULT_CENTER = new Point(RoundResultViewPanel.PANEL_WIDTH/2, RoundResultViewPanel.PANEL_HEIGHT/2);

    private TableViewPoints(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * サイコロの座標を取得します。
     * @param dir プレイヤーの方向
     * @param index 何個目のサイコロか
     * @return サイコロの座標
     */
    static Point ofDice(Direction dir, int index){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(34)
                .goRight(30)
                .goLeft(index*5)
                .get();
    }

    /**
     * 手牌の座標を取得します。
     * @param dir プレイヤーの方向
     * @param index 手配の左からの位置(0..13)
     * @param isolated 左の手牌と離れている(自摸牌)かどうか
     * @return 手牌の座標
     */
    static Point ofHandTile(Direction dir, int index, boolean isolated){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(52)
                .goRight(44)
                .goLeft(index*4)
                .goLeft(isolated? 2:0)
                .get();
    }

    /**
     * 振り聴表示の座標を取得します。
     * @param handSize 手牌のサイズ
     * @return 振り聴表示の座標
     */
    static Point ofLockMessage(int handSize){
        return new PointWalker(TABLE_CENTER, Direction.BOTTOM)
                .goStraight(54)
                .goRight(42)
                .goLeft(handSize*4)
                .get();
    }

    /**
     * 捨て牌の座標を取得します。
     * @param dir プレイヤーの方向
     * @param index 捨て牌の位置(0..23)
     * @param readyIndex 立直宣言牌の序数(未立直の場合は-1)
     * @return 捨て牌の座標
     */
    static Point ofRiverTile(Direction dir, int index, int readyIndex){
        int row = index/6;
        int col = index%6;
        if(readyIndex==-1){
            return ofRiverTile(dir, row, col, false, false);
        }
        int readyRow = readyIndex/6;
        boolean readyRotation = index==readyIndex;
        boolean readyShift = row==readyRow && readyIndex<index;
        return ofRiverTile(dir, row, col, readyRotation, readyShift);
    }

    /**
     * 捨て牌の座標を取得します。
     * @param dir プレイヤーの方向
     * @param row 捨て牌の上からの位置(0..3)
     * @param col 捨て牌の左からの位置(0..5)
     * @param ready この牌が立直宣言牌かどうか
     * @param readyShift 同じ行の左側に立直宣言牌があるかどうか
     * @return 捨て牌の座標
     */
    private static Point ofRiverTile(Direction dir, int row, int col, boolean ready, boolean readyShift){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(15)
                .goStraight(row*6)
                //.goStraight(ready? 1:0)
                .goRight(10)
                .goLeft(col*4)
                .goLeft(ready? 1:0)
                .goLeft(readyShift? 2:0).get();
    }

    /**
     * 面子構成牌の座標を取得します。
     * @param dir プレイヤーの方向
     * @param offset この牌の座標までのオフセット
     * @param rotated この牌が横に倒されているかどうか
     * @param added この牌が加槓牌かどうか
     * @return 面子構成牌の座標
     */
    static Point ofMeldTile(Direction dir, int offset, boolean rotated, boolean added){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(52)
                .goStraight(rotated? 1:0)
                .goStraight(added? -4:0)
                .goLeft(46)
                .goRight(offset)
                .goRight(rotated? 1:0)
                .get();
    }

    /**
     * 山牌の座標を取得します。
     * @param dir 山の方向
     * @param col 山牌の左からの位置(0..16)
     * @param floor 山牌の階層(0..下段 1..上段)
     * @return 山牌の座標
     */
    static Point ofWallTile(Direction dir, int col, int floor){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(41)
                .goRight(32)
                .goLeft(col*4)
                .shift(Direction.TOP, floor*2)
                .get();
    }

    /**
     * リーチ棒の座標を取得します。
     * @param dir プレイヤーの方向
     * @return リーチ棒の座標
     */
    static Point ofReadyBone(Direction dir){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(10)
                .get();
    }

    /**
     * プレイヤーの名前表示の座標を取得します。
     * @param dir プレイヤーの方向
     * @return プレイヤーの名前表示の座標
     */
    static Point ofPlayerName(Direction dir){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(56)
                .goRight(48)
                .get();
    }

    /**
     * プレイヤーのスコア表示の座標を取得します。
     * @param dir プレイヤーの方向
     * @return プレイヤーのスコア表示の座標
     */
    static Point ofPlayerScore(Direction dir){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(7)
                .get();
    }

    /**
     * プレイヤーの自風表示の座標を取得します。
     * @param dir プレイヤーの方向
     * @return プレイヤーの自風表示の座標
     */
    static Point ofPlayerWind(Direction dir){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(9)
                .goRight(8)
                .get();
    }

    /**
     * プレイヤーの宣言表示の座標を取得します。
     * @param dir プレイヤーの方向
     * @return プレイヤーの宣言表示の座標
     */
    static Point ofPlayerMessage(Direction dir){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(42)
                .get();
    }

    /**
     * 起家表示の座標を取得します。
     * @param dir プレイヤーの方向
     * @return 起家表示の座標
     */
    static Point ofInitialEast(Direction dir){
        return new PointWalker(TABLE_CENTER, dir)
                .goStraight(52)
                .goRight(52)
                .get();
    }

    /**
     * プレイヤーの選択肢表示の座標を取得します。
     * @param index 選択肢表示の位置(0..)
     * @return 選択肢表示の座標
     */
    static Point ofOptionButton(int index){
        return new PointWalker(TABLE_CENTER, Direction.BOTTOM)
                .goStraight(44)
                .goRight(38)
                .goLeft(index*16)
                .get();
    }

    /**
     * 点数移動画面の各プレイヤー表示の座標を取得します。
     * @param dir プレイヤーの方向
     * @return プレイヤー表示の座標
     */
    static Point ofPlayerArea(Direction dir){
        return new PointWalker(RESULT_CENTER, dir)
                .goStraight(20)
                .get();
    }

    /**
     * 座標を中心からの方向と距離で指定するためのクラス。
     * @author Rouh
     * @version 1.0
     */
    private static class PointWalker{
        private final Direction direction;
        private int currentX;
        private int currentY;

        /**
         * 指定された初期地点と初期方向をもとにインスタンスを生成します。
         * @param initialPoint 初期位置
         * @param initialDirection 初期方向
         */
        private PointWalker(Point initialPoint, Direction initialDirection){
            this.currentX = initialPoint.x;
            this.currentY = initialPoint.y;
            this.direction = initialDirection;
        }

        private void walk(Direction direction, int distance){
            switch(direction){
                case TOP -> currentY -= distance;
                case BOTTOM -> currentY += distance;
                case RIGHT -> currentX += distance;
                case LEFT -> currentX -= distance;
            }
        }

        /**
         * 現在の向き先に指定した距離移動します。
         * @param distance 距離
         * @return 自参照
         */
        private PointWalker goStraight(int distance){
            walk(direction, distance);
            return this;
        }

        /**
         * 現在の向き先から見て右側に指定した距離移動します。
         * <p>この操作では向き先は変更されません。
         * @param distance 距離
         * @return 自参照
         */
        private PointWalker goRight(int distance){
            walk(direction.turnRight(), distance);
            return this;
        }

        /**
         * 現在の向き先から見て左側に指定した距離移動します。
         * <p>この操作では向き先は変更されません。
         * @param distance 距離
         * @return 自参照
         */
        private PointWalker goLeft(int distance){
            walk(direction.turnLeft(), distance);
            return this;
        }

        /**
         * 現在の向き先に関係なく指定した方向に指定した距離移動します。
         * @param direction 方向
         * @param distance 距離
         * @return 自参照
         */
        private PointWalker shift(Direction direction, int distance){
            walk(direction, distance);
            return this;
        }

        /**
         * 現在の座標を{@code Point}クラスで取得します。
         * @return 座標
         */
        private Point get(){
            return new Point(currentX, currentY);
        }
    }
}
