package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.score.*;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.function.Predicate.not;

public class RoundPlayer extends TableStrategyDelegator implements WinningPlayerAccessor{
    private final HandScoreCalculator calculator = StandardHandScoreCalculator.getInstance();
    private final RoundAccessor round;
    private final TableMaster master;
    private final GamePlayerAccessor gamePlayer;
    private final Wind seatWind;
    private final Hand hand = new Hand();
    private final Set<Tile> river = new HashSet<>();
    private boolean orphanRiver = true;
    private boolean readyPrepared = false;
    private boolean firstAroundReady = false;
    private boolean ready = false;
    private boolean readyAround = false;
    private boolean riverLock = false;
    private boolean aroundLock = false;
    RoundPlayer(RoundAccessor round, GamePlayerAccessor gamePlayer){
        super(gamePlayer);
        this.round = round;
        this.master = round.getMaster();
        this.gamePlayer = gamePlayer;
        this.seatWind = gamePlayer.getSeatWindAt(round.getRoundCount());
    }

    void distributed(Tile tile){
        hand.distributed(tile);
        master.handUpdated(seatWind, hand.getAllTiles(), false);
    }

    void distributed(List<Tile> tiles){
        hand.distributed(tiles);
        master.handUpdated(seatWind, hand.getAllTiles(), false);
    }

    /**
     * 与えられた牌を手牌に追加します。
     * <p>結果は参加者に通知されます。
     * @param tile 牌
     */
    void draw(Tile tile){
        hand.draw(tile);
        master.handUpdated(seatWind, hand.getAllTiles(), true);
        if(aroundLock){
            aroundLock = false;
        }
    }

    /**
     * 指定された牌を手牌から削除します。
     * <p>結果は参加者に通知されます。
     * @throws IllegalArgumentException 牌が手牌中にない場合
     * @param tile 牌
     */
    void discard(Tile tile){
        hand.discard(tile);
        master.handUpdated(seatWind, hand.getAllTiles(), false);
        river.add(tile);
        if(orphanRiver && !tile.isOrphan()){
            orphanRiver = false;
        }
        if(!ready){
            riverLock = river.stream().anyMatch(hand::isCompletedBy);
        }
        if(riverLock){
            handLocked();
        }
    }

    /**
     * 立直を宣言します。
     * <p>結果は参加者に通知されます。
     * @throws IllegalStateException 点数が1000点未満の場合
     */
    void declareReady(){
        if(!ready && gamePlayer.getScore()<1000){
            throw new IllegalStateException("declare ready without enough score");
        }
        gamePlayer.applyScore(-1000);
        readyPrepared = true;
        master.declared(seatWind, Declaration.READY);
    }

    void aroundInterrupted(){
        readyAround = false;
    }

    void discardTileClaimed(){
        orphanRiver = false;
    }

    void discardTileSettled(Wind discarder, Tile discarded, Wind caller){
        if(discarder==seatWind){
            if(readyAround){
                readyAround = false;
            }
            if(readyPrepared){
                readyPrepared = false;
                ready = true;
                readyAround = true;
                if(round.isFirstAround()){
                    firstAroundReady = true;
                }
                master.readyBoneAdded(seatWind);
            }
        }else if(caller!=seatWind){
            if(hand.isCompletedBy(discarded)){
                aroundLock = true;
            }
            if(aroundLock){
                handLocked();
            }
        }
    }

    void roundExhausted(){
        if(isHandReady()){
            master.handRevealed(seatWind, hand.getAllTiles(), false);
        }
    }

    /**
     * 相手の打牌に対してカンを宣言し、大明槓を副露します。
     * @throws IllegalStateException 手牌の状態が不正の場合
     * @param claimedTile 副露牌
     * @param discarder 副露方向
     */
    void declareKan(Tile claimedTile, Wind discarder){
        int index = hand.makeCallQuad(claimedTile, seatWind.from(discarder));
        master.declared(seatWind, Declaration.KAN);
        master.tiltMeldAdded(seatWind, discarder.from(seatWind), hand.getOpenMelds().get(index).getTilesFormed());
        master.handUpdated(seatWind, hand.getAllTiles(), false);
        master.riverTileTaken(discarder);
    }

    /**
     * ポンによって刻子を副露します。
     * @param claimedTile 副露牌
     * @param baseTiles 副露先塔子
     * @param discarder 副露元
     */
    void declarePon(Tile claimedTile, List<Tile> baseTiles, Wind discarder){
        int index = hand.makeCallTriple(claimedTile, baseTiles, seatWind.from(discarder));
        master.declared(seatWind, Declaration.PON);
        master.tiltMeldAdded(seatWind, discarder.from(seatWind), hand.getOpenMelds().get(index).getTilesFormed());
        master.handUpdated(seatWind, hand.getAllTiles(), false);
        master.riverTileTaken(discarder);
    }

    /**
     * チーによって順子を副露します。
     * <p>結果は参加者に通知されます。
     * @param claimedTile 副露牌
     * @param baseTiles 副露先塔子
     */
    void declareChi(Tile claimedTile, List<Tile> baseTiles){
        int index = hand.makeCallSequence(claimedTile, baseTiles);
        master.declared(seatWind, Declaration.CHI);
        master.tiltMeldAdded(seatWind, Side.LEFT, hand.getOpenMelds().get(index).getTilesFormed());
        master.handUpdated(seatWind, hand.getAllTiles(), false);
        master.riverTileTaken(Side.LEFT.of(seatWind));
    }

    /**
     * 摸打中にカン宣言をし, 加槓もしくは暗槓を構築します。
     * @param targetTile 手牌中のカン宣言対象牌
     * @return true 暗槓の場合
     *         false 暗槓でない場合
     */
    boolean declareKan(Tile targetTile){
        if(hand.hasAddQuadBaseOf(targetTile)){
            int index = hand.makeAddQuad(targetTile);
            master.declared(seatWind, Declaration.KAN);
            master.meldTileAdded(seatWind, index, targetTile);
            master.handUpdated(seatWind, hand.getAllTiles(), false);
            return false;
        }else{
            int index = hand.makeSelfQuad(targetTile);
            master.declared(seatWind, Declaration.KAN);
            master.selfQuadAdded(seatWind, hand.getOpenMelds().get(index).getTilesFormed());
            master.handUpdated(seatWind, hand.getAllTiles(), false);
            return true;
        }
    }

    WinningResult declareOrphanRiver(boolean secondary){
        var score = HandScore.ofRiverJackpot(seatWind==Wind.EAST);
        var context = new WinningContext(round, this, seatWind, null, false, false, secondary);
        return new WinningResult(score, context);
    }

    WinningResult declareRon(Tile discarded, Wind discarder, boolean secondary){
        var context = new WinningContext(round, this, discarder, discarded, false, false, secondary);
        var score = calculator.calculate(hand.getHandTiles(), hand.getOpenMelds(), discarded, context);
        master.declared(seatWind, Declaration.RON);
        master.handRevealed(seatWind, hand.getAllTiles(), false);
        return new WinningResult(score, context);
    }

    WinningResult declareRonByQuad(Tile quadTile, Wind declarer, boolean secondary){
        var context = new WinningContext(round, this, declarer, quadTile, true, false, secondary);
        var score = calculator.calculate(hand.getHandTiles(), hand.getOpenMelds(), quadTile, context);
        master.declared(seatWind, Declaration.RON);
        master.handRevealed(seatWind, hand.getAllTiles(), false);
        return new WinningResult(score, context);
    }

    void declareNineTiles(){
        master.handRevealed(seatWind, hand.getAllTiles(), true);
    }

    WinningResult declareTsumo(boolean afterQuad){
        var context = new WinningContext(round, this, seatWind, hand.getDrawnTile(), false, afterQuad, false);
        var score = calculator.calculate(hand.getHandTiles(), hand.getOpenMelds(), hand.getDrawnTile(), context);
        master.declared(seatWind, Declaration.TSUMO);
        master.handRevealed(seatWind, hand.getAllTiles(), true);
        return new WinningResult(score, context);
    }

    boolean isHandReady(){
        return hand.isHandReady();
    }

    boolean isOrphanRiver(){
        return orphanRiver;
    }

    boolean hasQuad(){
        return hand.getOpenMelds().stream().anyMatch(Meld::isQuad);
    }

    List<TurnAction> getTurnChoices(boolean afterCall, boolean afterQuad){
        var choices = new ArrayList<TurnAction>();
        if(ready){
            choices.add(TurnAction.ofDiscardDrawn(hand.getDrawnTile()));
            if(hand.isCompleted()){
                choices.add(TurnAction.ofTsumo());
            }
            if(hand.canReadyQuad()){
                choices.add(TurnAction.ofKan(hand.getDrawnTile()));
            }
        }else{
            for(var discardTile: hand.getDiscardableTiles()){
                choices.add(TurnAction.ofDiscard(discardTile));
            }
            if(!afterCall){
                choices.add(TurnAction.ofDiscardDrawn(hand.getDrawnTile()));
                if(round.isFirstAround() && hand.isNineTilesHand()){
                    choices.add(TurnAction.ofNineTiles());
                }
                if(hand.isCompleted()){
                    var context = new WinningContext(round, this, seatWind, hand.getDrawnTile(), false, afterQuad, false);
                    var score = calculator.calculate(hand.getHandTiles(), hand.getOpenMelds(), hand.getDrawnTile(), context);
                    if(!score.isEmpty()){
                        choices.add(TurnAction.ofTsumo());
                    }
                }
                if(!round.isLastTurn() && round.getTotalQuadCount()<4){
                    for(var quadTile: hand.getQuadTiles()){
                        choices.add(TurnAction.ofKan(quadTile));
                    }
                }
                if(getCallCount()==0 && !round.isLastAround() && gamePlayer.getScore()>=1000){
                    for(var readyTile: hand.getReadyTiles()){
                        choices.add(TurnAction.ofReadyAndDiscard(readyTile));
                    }
                }
            }
        }
        return choices.stream().toList();
    }

    List<CallAction> getCallChoices(Wind discarder, Tile discarded){
        var choices = new ArrayList<CallAction>();
        choices.add(CallAction.ofPass());
        if(ready){
            if(hand.isCompletedBy(discarded) && !riverLock && !aroundLock){
                choices.add(CallAction.ofRon());
            }
        }else{
            if(hand.isCompletedBy(discarded) && !riverLock && !aroundLock){
                var context = new WinningContext(round, this, discarder, discarded, false, false, false);
                var score = calculator.calculate(hand.getHandTiles(), hand.getOpenMelds(), discarded, context);
                if(!score.isEmpty()){
                    choices.add(CallAction.ofRon());
                }
            }
            if(!hand.getQuadBaseOf(discarded).isEmpty()){
                choices.add(CallAction.ofKan());
            }
            if(!round.isLastTurn()){
                for(var tripleBase: hand.getTripleBasesOf(discarded)){
                    choices.add(CallAction.ofPon(tripleBase.get(0), tripleBase.get(1)));
                }
                if(Side.LEFT.of(seatWind)==discarder){
                    for(var sequenceBase: hand.getSequenceBasesOf(discarded)){
                        choices.add(CallAction.ofChi(sequenceBase.get(0), sequenceBase.get(1)));
                    }
                }
            }
        }
        return choices;
    }

    List<CallAction> getQuadCallChoices(Tile declared, boolean againstSelfQuad){
        if(hand.isCompletedBy(declared) && !riverLock && !aroundLock){
            if(!againstSelfQuad || hand.isThirteenOrphansHandReady()){
                return List.of(CallAction.ofPass(), CallAction.ofRon());
            }
        }
        return List.of(CallAction.ofPass());
    }

    public Wind getSeatWind(){
        return seatWind;
    }

    public int getScore(){
        return gamePlayer.getScore();
    }

    public int getRank(){
        return gamePlayer.getRank();
    }

    String getName(){
        return gamePlayer.getName();
    }

    PlayerData getPlayerData(){
        var initialSeatWind = gamePlayer.getSeatWindAt(1);
        return new PlayerData(getName(), initialSeatWind, seatWind, getScore(), getRank());
    }

    void applyScore(int score){
        gamePlayer.applyScore(score);
    }

    @Override
    public List<Tile> getHandTiles(){
        return hand.getHandTiles();
    }

    @Override
    public List<Meld> getOpenMelds(){
        return hand.getOpenMelds();
    }

    @Override
    public boolean isReady(){
        return ready;
    }

    @Override
    public int getCallCount(){
        return (int)hand.getOpenMelds().stream().filter(not(Meld::isConcealed)).count();
    }

    @Override
    public boolean isFirstAroundReady(){
        return firstAroundReady;
    }

    @Override
    public boolean isReadyAround(){
        return readyAround;
    }
}
