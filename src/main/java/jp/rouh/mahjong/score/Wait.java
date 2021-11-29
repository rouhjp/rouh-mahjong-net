package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;

/**
 * 手牌の待ちを表すクラス。
 * @author Rouh
 * @version 1.0
 */
enum Wait{

    /**
     * 両面待ち
     */
    DOUBLE_SIDE_STRAIGHT(0),

    /**
     * 辺張待ち
     */
    SINGLE_SIDE_STRAIGHT(2),

    /**
     * 嵌張待ち
     */
    MIDDLE_STRAIGHT(2),

    /**
     * 双碰待ち
     */
    EITHER_HEAD(0),

    /**
     * 単騎待ち
     */
    SINGLE_HEAD(2);

    private final int point;

    Wait(int point){
        this.point = point;
    }

    /**
     * この待ちの符を返します。
     * <p>符は以下のとおりです。
     * <table>
     *   <tr>
     *     <th>両面</th>
     *     <td>0</td>
     *   </tr>
     *   <tr>
     *     <th>辺張</th>
     *     <td>2</td>
     *   </tr>
     *   <tr>
     *     <th>嵌張</th>
     *     <td>2</td>
     *   </tr>
     *   <tr>
     *     <th>双碰</th>
     *     <td>0</td>
     *   </tr>
     *   <tr>
     *     <th>単騎</th>
     *     <td>2</td>
     *   </tr>
     * </table>
     * @return 符
     */
    int getWaitPoint(){
        return point;
    }

    /**
     * 指定した和了牌が指定した面子中に含まれる場合,
     * <p>面子中の和了牌の位置から待ちを判定して返します。
     * @param meld        検査対象の面子
     * @param winningTile 和了牌
     * @return 対応する待ち
     * @throws IllegalArgumentException 和了牌を含まない場合
     */
    static Wait of(Meld meld, Tile winningTile){
        if(!meld.contains(winningTile)){
            throw new IllegalArgumentException("no winning tile found: " + winningTile + " " + meld);
        }
        if(meld.isSequence()){
            if(meld.getTilesSorted().get(1).equalsIgnoreRed(winningTile)){
                return MIDDLE_STRAIGHT;
            }
            if(meld.isTerminal() && !winningTile.isTerminal()){
                return DOUBLE_SIDE_STRAIGHT;
            }
            return DOUBLE_SIDE_STRAIGHT;
        }
        return EITHER_HEAD;
    }
}
