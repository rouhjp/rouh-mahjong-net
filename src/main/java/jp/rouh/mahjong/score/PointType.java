package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Wind;

/**
 * 符の種類クラス。
 *
 * @author Rouh
 * @version 2.0
 */
public enum PointType{

    /**
     * 副底
     */
    BASE("副底", 20),

    /**
     * 七対子固定符
     */
    SEVEN_PAIR_BASE("七対子固定符", 25),

    /**
     * 雀頭
     */
    HEAD("雀頭", 0),

    /**
     * 雀頭(飜牌)
     */
    VALUABLE_HEAD("雀頭(飜牌)", 2),

    /**
     * 雀頭(連風牌)
     */
    DOUBLE_VALUABLE_HEAD("雀頭(連風牌)", 4),

    /**
     * 順子
     */
    STRAIGHT("順子", 0),

    /**
     * 明刻(中張牌)
     */
    TRIPLE("明刻(中張牌)", 2),

    /**
     * 明刻(么九牌)
     */
    ORPHAN_TRIPLE("明刻(么九牌)", 4),

    /**
     * 暗刻(中張牌)
     */
    CONCEALED_TRIPLE("暗刻(中張牌)", 4),

    /**
     * 暗刻(么九牌)
     */
    ORPHAN_CONCEALED_TRIPLE("暗刻(么九牌)", 8),

    /**
     * 明槓(中張牌)
     */
    QUAD("明槓(中張牌)", 8),

    /**
     * 明槓(么九牌)
     */
    ORPHAN_QUAD("明槓(么九牌)", 16),

    /**
     * 暗槓(中張牌)
     */
    CONCEALED_QUAD("暗槓(中張牌)", 16),

    /**
     * 暗槓(么九牌)
     */
    ORPHAN_CONCEALED_QUAD("暗槓(么九牌)", 32),

    /**
     * 待ち(両面)
     */
    DOUBLE_SIDE_STRAIGHT_WAIT("待ち(両面)", 0),

    /**
     * 待ち(双碰)
     */
    EITHER_HEAD_WAIT("待ち(双碰)", 0),

    /**
     * 待ち(単騎)
     */
    SINGLE_HEAD_WAIT("待ち(単騎)", 2),

    /**
     * 待ち(嵌張)
     */
    MIDDLE_STRAIGHT_WAIT("待ち(嵌張)", 2),

    /**
     * 待ち(辺張)
     */
    SINGLE_SIDE_STRAIGHT_WAIT("待ち(辺張)", 2),

    /**
     * 自摸符
     */
    TSUMO("自摸符", 2),

    /**
     * 門前加符
     */
    CONCEALED_RON("門前加符", 10),

    /**
     * 平和加符
     */
    CALLED_NO_POINT("平和加符", 10);

    private final String name;
    private final int point;
    PointType(String name, int point){
        this.name = name;
        this.point = point;
    }

    /**
     * 符の種類名を取得します。
     * @return 種類名
     */
    public String getName(){
        return name;
    }

    /**
     * 符を取得します。
     * @return 符
     */
    public int getPoint(){
        return point;
    }

    /**
     * 符が0かどうか検査します。
     * @return true 符が0の場合
     *         false 符が付く場合
     */
    public boolean isNoPoint(){
        return point==0;
    }

    /**
     * 雀頭に対応する符の種類を取得します。
     * @param head 雀頭
     * @return 符の種類
     */
    static PointType ofHead(Head head, Wind roundWind, Wind seatWind){
        if(head.isDragon()){
            return PointType.VALUABLE_HEAD;
        }
        if(head.isWind()){
            boolean seatWindHead = head.getFirst().equalsIgnoreRed(seatWind.toTile());
            boolean roundWindHead = head.getFirst().equalsIgnoreRed(roundWind.toTile());
            if(seatWindHead && roundWindHead){
                return PointType.DOUBLE_VALUABLE_HEAD;
            }
            if(seatWindHead || roundWindHead){
                return PointType.VALUABLE_HEAD;
            }
            return PointType.HEAD;
        }
        return PointType.HEAD;
    }

    /**
     * 待ちに対応する符の種類を取得します。
     * @param wait 待ち
     * @return 符の種類
     */
    static PointType ofWait(Wait wait){
        return switch(wait){
            case EITHER_HEAD -> EITHER_HEAD_WAIT;
            case SINGLE_HEAD -> SINGLE_HEAD_WAIT;
            case MIDDLE_STRAIGHT -> MIDDLE_STRAIGHT_WAIT;
            case DOUBLE_SIDE_STRAIGHT -> DOUBLE_SIDE_STRAIGHT_WAIT;
            case SINGLE_SIDE_STRAIGHT -> SINGLE_SIDE_STRAIGHT_WAIT;
        };
    }

    /**
     * 面子に対応する符の種類を取得します。
     * @param meld 面子
     * @return 符の種類
     */
    static PointType ofMeld(Meld meld){
        if(meld.isStraight()){
            return STRAIGHT;
        }
        if(meld.isQuad()){
            if(meld.isConcealed()){
                if(meld.isOrphan()){
                    return ORPHAN_CONCEALED_QUAD;
                }
                return CONCEALED_QUAD;
            }
            if(meld.isOrphan()){
                return ORPHAN_QUAD;
            }
            return QUAD;
        }
        if(meld.isConcealed()){
            if(meld.isOrphan()){
                return ORPHAN_CONCEALED_TRIPLE;
            }
            return CONCEALED_TRIPLE;
        }
        if(meld.isOrphan()){
            return ORPHAN_TRIPLE;
        }
        return TRIPLE;
    }
}
