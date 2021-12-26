package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.Wind;

/**
 * 局ID子クラス。
 * @param wind 局の場風
 * @param count 局数(1..4)
 */
public record RoundID(Wind wind, int count){

    public RoundID{
        if(count<=0 || count>4){
            throw new IllegalArgumentException("invalid round count: " + count);
        }
    }

    /**
     * 次局の局IDを取得します。
     * @return 次局の局ID
     */
    public RoundID next(){
        if(count<4){
            return new RoundID(wind, count + 1);
        }
        return new RoundID(wind.next(), 1);
    }
}
