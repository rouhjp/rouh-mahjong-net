package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.List;

/**
 * 得点計算等に必要な和了時の情報をまとめたクラス。
 * <p>このクラスは{@link ScoringContext}及び{@link PaymentContext}の実装です。
 * @author Rouh
 * @version 1.0
 */
public class WinningContext implements ScoringContext, PaymentContext{
    private final Wind roundWind;
    private final Wind seatWind;
    private final Side winningSide;
    private final boolean dealer;
    private final boolean tsumo;
    private final boolean selfMade;
    private final boolean ready;
    private final boolean firstAroundReady;
    private final boolean firstAroundWin;
    private final boolean readyAroundWin;
    private final boolean lastTileGrabWin;
    private final boolean lastTileDrawWin;
    private final boolean quadTileGrabWin;
    private final boolean quadTileDrawWin;
    private final Tile winningTile;
    private final List<Tile> handTiles;
    private final List<Meld> openMelds;
    private final List<Tile> upperIndicators;
    private final List<Tile> lowerIndicators;
    private final List<Tile> upperPrisedTiles;
    private final List<Tile> lowerPrisedTiles;
    private final int depositCount;
    private final int streakCount;

    /**
     * コンストラクタ。
     * @param round 局の情報へのアクセッサ
     * @param player 和了プレイヤー情報へのアクセッサ
     * @param turnWind 放銃者の自風(ツモであれば和了者の自風)
     * @param quadGrab 槍槓かどうか
     * @param afterQuad 嶺上ツモ後かどうか
     * @param secondary ダブロン時の副次的なロンであるかどうか
     */
    public WinningContext(WinningRoundAccessor round,
                          WinningPlayerAccessor player,
                          Wind turnWind,
                          Tile winningTile,
                          boolean quadGrab,
                          boolean afterQuad,
                          boolean secondary){
        this.roundWind = round.getRoundWind();
        this.seatWind = player.getSeatWind();
        this.winningSide = turnWind.from(seatWind);
        this.dealer = seatWind==Wind.EAST;
        this.tsumo = winningSide==Side.SELF;
        this.selfMade = player.getCallCount()==0;
        this.ready = player.isReady();
        this.firstAroundReady = player.isFirstAroundReady();
        this.firstAroundWin = round.isFirstAround();
        this.readyAroundWin = player.isReadyAround();
        this.lastTileGrabWin = round.isLastTurn() && !tsumo;
        this.lastTileDrawWin = round.isLastTurn() && tsumo;
        this.quadTileGrabWin = quadGrab;
        this.quadTileDrawWin = afterQuad;
        this.winningTile = winningTile;
        this.handTiles = List.copyOf(player.getHandTiles());
        this.upperIndicators = List.copyOf(round.getUpperIndicators());
        this.lowerIndicators  =List.copyOf(round.getLowerIndicators());
        this.upperPrisedTiles = List.copyOf(round.getUpperPrisedTiles());
        this.lowerPrisedTiles = List.copyOf(round.getLowerPrisedTiles());
        this.depositCount = secondary?0:round.getTotalDepositCount();
        this.streakCount = round.getRoundStreakCount();
        this.openMelds = List.copyOf(player.getOpenMelds());
    }

    @Override
    public Wind getRoundWind(){
        return roundWind;
    }

    @Override
    public Wind getSeatWind(){
        return seatWind;
    }

    @Override
    public boolean isDealer(){
        return dealer;
    }

    @Override
    public boolean isTsumo(){
        return tsumo;
    }

    @Override
    public boolean isSelfMade(){
        return selfMade;
    }

    @Override
    public boolean isReady(){
        return ready;
    }

    @Override
    public boolean isFirstAroundReady(){
        return firstAroundReady;
    }

    @Override
    public boolean isFirstAroundWin(){
        return firstAroundWin;
    }

    @Override
    public boolean isReadyAroundWin(){
        return readyAroundWin;
    }

    @Override
    public boolean isLastTileGrabWin(){
        return lastTileGrabWin;
    }

    @Override
    public boolean isLastTileDrawWin(){
        return lastTileDrawWin;
    }

    @Override
    public boolean isQuadTileGrabWin(){
        return quadTileGrabWin;
    }

    @Override
    public boolean isQuadTileDrawWin(){
        return quadTileDrawWin;
    }

    public Tile getWinningTile(){
        return winningTile;
    }

    public List<Tile> getHandTiles(){
        return handTiles;
    }

    public List<Tile> getUpperIndicators(){
        return upperIndicators;
    }

    public List<Tile> getLowerIndicators(){
        return lowerIndicators;
    }

    @Override
    public List<Tile> getUpperPrisedTiles(){
        return upperPrisedTiles;
    }

    @Override
    public List<Tile> getLowerPrisedTiles(){
        return lowerPrisedTiles;
    }

    @Override
    public Side getWinningSide(){
        return winningSide;
    }

    @Override
    public int getTotalDepositCount(){
        return depositCount;
    }

    @Override
    public int getRoundStreakCount(){
        return streakCount;
    }

    @Override
    public List<Meld> getOpenMelds(){
        return openMelds;
    }
}
