package jp.rouh.mahjong.score;

public enum PointType{

    BASE("副底", 20),

    SEVEN_PAIR_BASE("七対子固定符", 25),

    HEAD("雀頭", 0),

    VALUABLE_HEAD("雀頭(飜牌)", 2),

    DOUBLE_VALUABLE_HEAD("雀頭(連風牌)", 4),

    STRAIGHT("順子", 0),

    TRIPLE("明刻(中張牌)", 2),

    ORPHAN_TRIPLE("明刻(么九牌)", 4),

    CONCEALED_TRIPLE("暗刻(中張牌)", 4),

    ORPHAN_CONCEALED_TRIPLE("暗刻(么九牌)", 8),

    QUAD("明槓(中張牌)", 8),

    ORPHAN_QUAD("明槓(么九牌)", 16),

    CONCEALED_QUAD("暗槓(中張牌)", 16),

    ORPHAN_CONCEALED_QUAD("暗槓(么九牌)", 32),

    DOUBLE_SIDE_STRAIGHT_WAIT("待ち(両面)", 0),

    EITHER_HEAD_WAIT("待ち(双碰)", 0),

    SINGLE_HEAD_WAIT("待ち(単騎)", 2),

    MIDDLE_STRAIGHT_WAIT("待ち(嵌張)", 2),

    SINGLE_SIDE_STRAIGHT_WAIT("待ち(辺張)", 2),

    TSUMO("自摸符", 2),

    SELF_MADE("門前加符", 10),

    CALLED_NO_POINT("平和加符", 10);

    private final String name;
    private final int point;
    PointType(String name, int point){
        this.name = name;
        this.point = point;
    }

    public String getName(){
        return name;
    }

    public int getPoint(){
        return point;
    }

    public boolean isEmpty(){
        return point==0;
    }

    static PointType ofWait(Wait wait){
        return switch(wait){
            case EITHER_HEAD -> EITHER_HEAD_WAIT;
            case SINGLE_HEAD -> SINGLE_HEAD_WAIT;
            case MIDDLE_STRAIGHT -> MIDDLE_STRAIGHT_WAIT;
            case DOUBLE_SIDE_STRAIGHT -> DOUBLE_SIDE_STRAIGHT_WAIT;
            case SINGLE_SIDE_STRAIGHT -> SINGLE_SIDE_STRAIGHT_WAIT;
        };
    }

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
