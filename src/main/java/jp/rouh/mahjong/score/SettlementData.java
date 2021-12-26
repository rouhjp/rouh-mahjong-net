package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Wind;

public class SettlementData implements SettlementContext{
    private final Wind seatWind;
    private final Side winningSide;
    private final boolean quadDraw;
    private final int deposit;
    private final int streak;
    public SettlementData(Wind seatWind, Side winningSide, boolean afterQuad, int deposit, int streak){
        this.seatWind = seatWind;
        this.winningSide = winningSide;
        this.quadDraw = afterQuad;
        this.deposit = deposit;
        this.streak = streak;
    }

    @Override
    public Wind getSeatWind(){
        return seatWind;
    }

    @Override
    public Side getWinningSide(){
        return winningSide;
    }

    @Override
    public boolean isQuadTileDrawWin(){
        return quadDraw;
    }

    @Override
    public int getTotalDepositCount(){
        return deposit;
    }

    @Override
    public int getRoundStreakCount(){
        return streak;
    }
}
