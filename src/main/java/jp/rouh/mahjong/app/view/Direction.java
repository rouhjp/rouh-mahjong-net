package jp.rouh.mahjong.app.view;

import jp.rouh.mahjong.tile.Side;

/**
 * 画面の上下左右の向きを表すクラス。
 * @author Rouh
 * @version 1.0
 */
enum Direction{

    /**
     * 上
     */
    TOP,

    /**
     * 右
     */
    RIGHT,

    /**
     * 下
     */
    BOTTOM,

    /**
     * 左
     */
    LEFT;

    /**
     * この向きから相対的に右にあたる向きを取得します。
     * @return 向き
     */
    public Direction turnRight(){
        return values()[(ordinal() + 1)%4];
    }

    /**
     * この向きから相対的に左にあたる向きを取得します。
     * @return 向き
     */
    public Direction turnLeft(){
        return values()[(ordinal() + 3)%4];
    }

    /**
     * この向きから相対的に反対にあたる向きを取得します。
     * @return 向き
     */
    public Direction reversed(){
        return values()[(ordinal() + 2)%4];
    }

    /**
     * この向きが左右方向の向きかどうか検査します。
     * @return true 左または右向きの場合
     *         false 上または下向きの場合
     */
    public boolean isSideways(){
        return this==RIGHT || this==LEFT;
    }

    /**
     * 上向きを基準に, 度数法の角度を取得します。
     * <table>
     *   <tr>
     *     <th>direction</th>
     *     <th>angle</th>
     *   </tr>
     *   <tr>
     *     <td>TOP</td>
     *     <td>0</td>
     *   </tr>
     *   <tr>
     *     <td>RIGHT</td>
     *     <td>90</td>
     *   </tr>
     *   <tr>
     *     <td>BOTTOM</td>
     *     <td>180</td>
     *   </tr>
     *   <tr>
     *     <td>LEFT</td>
     *     <td>270</td>
     *   </tr>
     * </table>
     * @return 角度
     */
    public int angle(){
        return ordinal()*90;
    }

    /**
     * 相対方位{@link Side}を上下左右の向き{@link Direction}に変換します。
     * <p>例えば自プレイヤーの方向を表す相対方向{@code Side.SELF}は,
     * 画面下部に表示するため, {@code Direction.BOTTOM}に変換されます。
     * <table>
     *   <tr>
     *     <th>Side</th>
     *     <th>Direction</th>
     *   </tr>
     *   <tr>
     *     <td>SELF</td>
     *     <td>BOTTOM</td>
     *   </tr>
     *   <tr>
     *     <td>RIGHT</td>
     *     <td>RIGHT</td>
     *   </tr>
     *   <tr>
     *     <td>ACROSS</td>
     *     <td>TOP</td>
     *   </tr>
     *   <tr>
     *     <td>LEFT</td>
     *     <td>LEFT</td>
     *   </tr>
     * </table>
     * @param side 相対方位
     * @return 対応する上下左右の向き
     */
    public static Direction of(Side side){
        return switch(side){
            case SELF -> BOTTOM;
            case RIGHT -> RIGHT;
            case ACROSS -> TOP;
            case LEFT -> LEFT;
        };
    }
}
