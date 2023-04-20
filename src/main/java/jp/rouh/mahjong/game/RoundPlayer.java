package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.score.*;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;
import jp.rouh.util.Lists;

import java.util.*;

import static java.util.function.Predicate.not;

/**
 * 局プレイヤー。
 * @author Rouh
 * @version 1.0
 */
class RoundPlayer extends ForwardingTableStrategy{
    private final HandScoreCalculator calculator = new StandardHandScoreCalculator();
    private final RoundAccessor round;
    private final TableMaster notifier;
    private final GamePlayerAccessor gamePlayer;
    private final Wind seatWind;
    private final Set<Tile> river = new HashSet<>();
    private final List<Tile> handTiles = new ArrayList<>();
    private final LinkedList<Meld> openMelds = new LinkedList<>();

    //stored when player draw a tile from the wall, disposed when player discard a tile
    private Tile drawnTile;

    //stored when player has distributed init tiles, updated when player discard a tile
    private Set<Tile> winningTiles;

    //stored when player declare pon or chi, disposed when player discard a tile
    private Set<Tile> discardLockedTiles;

    //stored when player completed ready
    private Set<Tile> readyQuadTiles;

    private boolean called = false;
    private boolean readyPrepared = false;
    private boolean firstAroundReady = false;
    private boolean ready = false;
    private boolean readyAround = false;
    private boolean riverLock = false;
    private boolean aroundLock = false;

    /**
     * 局プレイヤーを生成します。
     * @param round 局情報の参照先
     * @param gamePlayer 対局プレイヤー情報の参照先
     */
    RoundPlayer(RoundAccessor round, GamePlayerAccessor gamePlayer){
        super(gamePlayer);
        this.round = round;
        this.notifier = round.getMaster();
        this.gamePlayer = gamePlayer;
        this.seatWind = gamePlayer.getSeatWindAt(round.getRoundCount());
    }

    /**
     * このプレイヤーに最後の1枚の配牌を行います。
     * @param tile 配牌される牌
     */
    void distributed(Tile tile){
        if(handTiles.size()!=12){
            throw new IllegalStateException("invalid size of distributing hand tiles: "+handTiles);
        }
        handTiles.add(tile);
        handTiles.sort(Comparator.naturalOrder());
        winningTiles = HandTiles.winningTilesOf(handTiles);
        notifier.handUpdated(seatWind, handTiles, false);
    }

    /**
     * このプレイヤーに4枚の配牌を行います。
     * @param tiles 配牌される牌のリスト(長さ4)
     */
    void distributed(List<Tile> tiles){
        if(tiles.size()!=4){
            throw new IllegalArgumentException("invalid size of distributing tiles: "+tiles);
        }
        if(handTiles.size()>=12 || handTiles.size()%4!=0){
            throw new IllegalStateException("invalid size of distributing hand tiles: "+handTiles);
        }
        handTiles.addAll(tiles);
        handTiles.sort(Comparator.naturalOrder());
        notifier.handUpdated(seatWind, handTiles, false);
    }

    /**
     * 与えられた牌を手牌に追加します。
     *
     * <p>通常のツモ及び嶺上ツモの際に呼ばれます。
     * @param tile 自摸牌
     */
    void draw(Tile tile){
        requireCallPhase();
        drawnTile = tile;
        aroundLock = false;
        notifier.handUpdated(seatWind, Lists.added(handTiles, drawnTile), true);
    }

    /**
     * 指定された牌を打牌します。
     *
     * <p>立直宣言牌として打牌する場合は代わりに{@link #discardAndReady}を用います。
     * @param tile 打牌する牌
     */
    void discard(Tile tile){
        discard(tile, false);
    }

    /**
     * 指定された牌を立直宣言牌として打牌します。
     *
     * @param tile 打牌する牌
     */
    void discardAndReady(Tile tile){
        if(ready || readyPrepared || getScore()<1000){
            throw new IllegalStateException("ready not allowed");
        }
        readyPrepared = true;
        notifier.declared(seatWind, Declaration.READY);
        discard(tile, true);
    }

    private void discard(Tile tile, boolean readyDeclared){
        requireTurnPhase();
        boolean hasChanged = drawnTile==null || !drawnTile.equalsIgnoreRed(tile);
        if(drawnTile!=null){
            handTiles.add(drawnTile);
            handTiles.sort(Comparator.naturalOrder());
            drawnTile = null;
        }
        remove(tile);
        if(hasChanged){
            winningTiles = HandTiles.winningTilesOf(handTiles);
            riverLock = winningTiles.stream().anyMatch(river::contains);
        }
        river.add(tile);
        discardLockedTiles = null;
        notifier.handUpdated(seatWind, handTiles, false);
        if(riverLock){
            handLocked();
        }
        notifier.riverTileAdded(seatWind, tile, readyDeclared);
    }

    /**
     * チーによって順子を副露します。
     * @param calledTile 副露牌
     * @param baseTiles 残りの構成牌
     */
    void declareChi(Tile calledTile, List<Tile> baseTiles){
        requireCallPhase();
        remove(baseTiles);
        openMelds.add(Meld.ofCallStraight(baseTiles, calledTile));
        discardLockedTiles = HandTiles.waitingTargetsOf(baseTiles);
        notifier.declared(seatWind, Declaration.CHI);
        notifier.tiltMeldAdded(seatWind, Side.LEFT, openMelds.getLast().getTilesFormed());
        notifier.handUpdated(seatWind, handTiles, false);
        notifier.riverTileTaken(Side.LEFT.of(seatWind));
    }

    /**
     * ポンによって刻子を副露します。
     * @param calledTile 副露牌
     * @param baseTiles 残りの構成牌
     * @param discarderWind 副露元
     */
    void declarePon(Tile calledTile, List<Tile> baseTiles, Wind discarderWind){
        requireCallPhase();
        remove(baseTiles);
        openMelds.add(Meld.ofCallTriple(baseTiles, calledTile, discarderWind.from(seatWind)));
        discardLockedTiles = HandTiles.waitingTargetsOf(baseTiles);
        notifier.declared(seatWind, Declaration.PON);
        notifier.tiltMeldAdded(seatWind, discarderWind.from(seatWind), openMelds.getLast().getTilesFormed());
        notifier.handUpdated(seatWind, handTiles, false);
        notifier.riverTileTaken(discarderWind);
    }

    /**
     * 相手の打牌に対してカンを宣言し、大明槓を副露します。
     * @param calledTile 副露牌
     * @param discarderWind 副露元
     */
    void declareKan(Tile calledTile, Wind discarderWind){
        requireCallPhase();
        var baseTiles = handTiles.stream().filter(calledTile::equalsIgnoreRed).toList();
        remove(baseTiles);
        openMelds.add(Meld.ofCallQuad(baseTiles, calledTile, discarderWind.from(seatWind)));
        notifier.declared(seatWind, Declaration.KAN);
        notifier.tiltMeldAdded(seatWind, discarderWind.from(seatWind), openMelds.getLast().getTilesFormed());
        notifier.handUpdated(seatWind, handTiles, false);
        notifier.riverTileTaken(discarderWind);
    }

    /**
     * 摸打中にカン宣言をし, 加槓もしくは暗槓を構築します。
     * @param selectedTile カン宣言牌
     */
    void declareKan(Tile selectedTile){
        requireTurnPhase();
        requireDrawTurnPhase();
        handTiles.add(drawnTile);
        drawnTile = null;
        var quadTiles = handTiles.stream().filter(selectedTile::equalsIgnoreRed).toList();
        if(quadTiles.size()==4){
            //暗槓
            remove(quadTiles);
            openMelds.add(Meld.ofSelfQuad(quadTiles));
            notifier.declared(seatWind, Declaration.KAN);
            notifier.selfQuadAdded(seatWind, openMelds.getLast().getTilesFormed());
            notifier.handUpdated(seatWind, handTiles, false);
            return;
        }
        //加槓
        remove(selectedTile);
        var triple = openMelds.stream().filter(Meld::isTriple)
                .filter(meld->meld.getFirst().equalsIgnoreRed(selectedTile))
                .findFirst()
                .orElseThrow();
        int index = openMelds.indexOf(triple);
        openMelds.remove(index);
        openMelds.add(index, Meld.ofAddQuad(triple, selectedTile));
        notifier.declared(seatWind, Declaration.KAN);
        notifier.meldTileAdded(seatWind, index, selectedTile);
        notifier.handUpdated(seatWind, handTiles, false);
    }

    /**
     * 九種九牌を宣言します。
     */
    void declareNineTiles(){
        requireTurnPhase();
        requireDrawTurnPhase();
        notifier.handRevealed(seatWind, Lists.added(handTiles, drawnTile), true);
    }

    /**
     * このターンで選択可能なターン行動をリスト形式で取得します。
     *
     * @param afterCall このターンが副露後かどうか
     * @param afterQuad このターンがカン後かどうか
     * @return ターン行動のリスト
     */
    List<TurnAction> getTurnActions(boolean afterCall, boolean afterQuad){
        requireTurnPhase();
        if(afterCall){
            //副露後
            return handTiles.stream().filter(not(discardLockedTiles::contains)).map(TurnAction::ofDiscard).toList();
        }
        requireDrawTurnPhase();
        var actions = new ArrayList<TurnAction>();
        if(ready){
            //自摸後(立直下)
            actions.add(TurnAction.ofDiscardDrawn(drawnTile));
            if(winningTiles.contains(drawnTile)){
                actions.add(TurnAction.ofTsumo());
            }
            if(!round.isLastTurn() && round.getTotalQuadCount()<4 && readyQuadTiles.contains(drawnTile)){
                actions.add(TurnAction.ofKan(drawnTile));
            }
            return actions;
        }
        //自摸後
        actions.addAll(handTiles.stream().map(TurnAction::ofDiscard).toList());
        actions.add(TurnAction.ofDiscard(drawnTile));
        actions.add(TurnAction.ofDiscardDrawn(drawnTile));
        if(round.isFirstAround() && HandTiles.isNineTiles(handTiles, drawnTile)){
            actions.add(TurnAction.ofNineTiles());
        }
        if(winningTiles.contains(drawnTile) && hasScore(afterQuad)){
            actions.add(TurnAction.ofTsumo());
        }
        if(!round.isLastTurn() && round.getTotalQuadCount()<4){
            var meldTiles = openMelds.stream().map(Meld::getTilesSorted).toList();
            actions.addAll(HandTiles.addKanTargetsOf(handTiles, drawnTile, meldTiles).stream().map(TurnAction::ofKan).toList());
            actions.addAll(HandTiles.selfKanTargetsOf(handTiles, drawnTile).stream().map(TurnAction::ofKan).toList());
        }
        boolean concealed = openMelds.stream().allMatch(Meld::isConcealed);
        if(concealed && !round.isLastTurn() && getScore()>=1000){
            actions.addAll(HandTiles.readyTilesOf(handTiles, drawnTile).stream().map(TurnAction::ofReadyAndDiscard).toList());
        }
        return actions;
    }

    /**
     * 相手の打牌に対して選択可能なターン外行動をリスト形式で取得します。
     *
     * @param discardedTile 打牌
     * @param discarderWind 打牌者の自風
     * @return ターン外行動のリスト
     */
    List<CallAction> getCallActionsForDiscard(Wind discarderWind, Tile discardedTile){
        requireCallPhase();
        var actions = new ArrayList<CallAction>();
        actions.add(CallAction.ofPass());
        if(ready){
            if(winningTiles.contains(discardedTile) && !riverLock && !aroundLock){
                actions.add(CallAction.ofRon());
            }
            return actions;
        }
        if(winningTiles.contains(discardedTile) && !riverLock && !aroundLock && hasScore(discardedTile, discarderWind.from(seatWind))){
            actions.add(CallAction.ofRon());
        }
        if(!HandTiles.kanBasesOf(handTiles, discardedTile).isEmpty()){
            actions.add(CallAction.ofKan());
        }
        if(!round.isLastTurn()){
            actions.addAll(HandTiles.ponBasesOf(handTiles, discardedTile).stream().map(CallAction::ofPon).toList());
            if(discarderWind==Side.LEFT.of(seatWind)){
                actions.addAll(HandTiles.chiBasesOf(handTiles, discardedTile).stream().map(CallAction::ofChi).toList());
            }
        }
        return actions;
    }

    /**
     * 相手のカンに対して選択可能なターン外行動をリスト形式で取得します。
     *
     * @param quadTile カン宣言牌
     * @param selfQuad カンが暗槓かどうか
     * @return ターン外行動のリスト
     */
    List<CallAction> getCallActionsForQuad(Tile quadTile, boolean selfQuad){
        requireCallPhase();
        if(winningTiles.contains(quadTile) && !riverLock && !aroundLock){
            if(!selfQuad || HandTiles.isCompletedThirteenOrphans(handTiles, quadTile)){
                return List.of(CallAction.ofPass(), CallAction.ofRon());
            }
        }
        return List.of(CallAction.ofPass());
    }

    /**
     * 流し満貫の結果を取得します。
     * @return 流し満願の結果
     */
    WinningResult declareOrphanRiver(){
        var score = HandScore.ofRiverLimit(seatWind);
        return new WinningResult(score, null, null, null, null);
    }

    /**
     * ロンを宣言し, 和了結果を取得します。
     * @param claimedTile 和了牌
     * @param supplierWind 放銃者
     * @param quadGrab 槍槓かどうか
     * @return 和了結果
     */
    WinningResult declareRon(Tile claimedTile, Wind supplierWind, boolean quadGrab){
        requireCallPhase();
        var situation = getWinningSituation(false, quadGrab, supplierWind.from(seatWind));
        var score = calculator.calculate(handTiles, openMelds, claimedTile, situation);
        notifier.declared(seatWind, Declaration.RON);
        notifier.handRevealed(seatWind, handTiles, false);
        return new WinningResult(score, handTiles, openMelds, claimedTile, situation);
    }

    /**
     * ツモを宣言し, 和了結果を取得します。
     * @param afterQuad ターンがカン後であるかどうか
     * @return 和了結果
     */
    WinningResult declareTsumo(boolean afterQuad){
        requireTurnPhase();
        requireDrawTurnPhase();
        var situation = getWinningSituation(afterQuad, false, Side.SELF);
        var score = calculator.calculate(handTiles, openMelds, drawnTile, situation);
        notifier.declared(seatWind, Declaration.TSUMO);
        notifier.handRevealed(seatWind, Lists.added(handTiles, drawnTile), true);
        return new WinningResult(score, handTiles, openMelds, drawnTile, situation);
    }

    private boolean hasScore(Tile claimedTile, Side from){
        return !calculator.calculate(handTiles, openMelds, claimedTile, getWinningSituation(false, false, from)).isEmpty();
    }

    private boolean hasScore(boolean afterQuad){
        return afterQuad || !calculator.calculate(handTiles, openMelds, drawnTile, getWinningSituation(false, false, Side.SELF)).isEmpty();
    }

    private WinningSituation getWinningSituation(boolean afterQuad, boolean quadGrab, Side from){
        var options = getWinningOptions(afterQuad, quadGrab);
        return new WinningSituation(round.getRoundWind(), seatWind, from,
                round.getUpperIndicators(), ready?round.getLowerIndicators():List.of(), options);
    }

    private List<WinningOption> getWinningOptions(boolean afterQuad, boolean quadGrab){
        var options = new ArrayList<WinningOption>();
        if(round.isFirstAround()) options.add(WinningOption.FIRST_AROUND_WIN);
        if(round.isLastTurn()) options.add(WinningOption.LAST_TILE_WIN);
        if(ready) options.add(WinningOption.READY);
        if(readyAround) options.add(WinningOption.READY_AROUND_WIN);
        if(firstAroundReady) options.add(WinningOption.FIRST_AROUND_READY);
        if(afterQuad) options.add(WinningOption.QUAD_TURN_WIN);
        if(quadGrab) options.add(WinningOption.QUAD_TILE_WIN);
        return options;
    }

    private void remove(Tile tile){
        if(!handTiles.remove(tile)){
            throw new IllegalArgumentException("tile not found: removing "+tile+" from "+handTiles);
        }
    }

    private void remove(List<Tile> tiles){
        for(var tile:tiles){
            remove(tile);
        }
    }

    private void requireCallPhase(){
        if(drawnTile!=null || (handTiles.size() + openMelds.size()*3)!=13){
            throw new IllegalStateException("invalid hand status: must be out of turn");
        }
    }

    private void requireTurnPhase(){
        if((handTiles.size() + openMelds.size()*3 + (drawnTile==null?0:1))!=14){
            throw new IllegalStateException("invalid hand status: must be on turn");
        }
    }

    private void requireDrawTurnPhase(){
        if(drawnTile==null){
            throw new IllegalStateException("invalid hand status: has no drawn tile");
        }
    }

    /**
     * 暗槓もしくは加槓が発生したことを通知します。
     *
     * <p>立直後一巡であった場合, 一発の権利を消失します。
     */
    void quadDeclared(){
        readyAround = false;
    }

    /**
     * 副露が発生したことを通知します。
     *
     * <p>立直後一巡であった場合, 一発の権利を消失します。
     * <p>副露が自身に対する物であった場合, 流し満貫の権利を消失します。
     * @param discarderWind 打牌者の自風
     */
    void tileCalled(Wind discarderWind){
        readyAround = false;
        if(discarderWind==seatWind){
            called = true;
        }
    }

    /**
     * 打牌が完了したことを通知します。
     *
     * <p>副露が発生した場合も通知されます。
     * <p>立直宣言後であった場合, 立直状態になります。
     * <p>他家の打牌が和了牌である場合, 同巡フリテン状態になります。
     * @param discarderWind 打牌者の風牌
     * @param discardedTile 打牌された牌
     */
    void turnSettled(Wind discarderWind, Tile discardedTile){
        if(discarderWind==seatWind){
            readyAround = false;
            if(readyPrepared){
                gamePlayer.applyScore(-1000);
                readyPrepared = false;
                ready = true;
                readyAround = true;
                if(round.isFirstAround()){
                    firstAroundReady = true;
                }
                readyQuadTiles = HandTiles.readyKanTargetsOf(handTiles);
                notifier.readyBoneAdded(seatWind);
            }
        }else{
            if(winningTiles.contains(discardedTile)){
                aroundLock = true;
            }
            if(aroundLock){
                handLocked();
            }
        }
    }

    /**
     * 局が荒廃により流局したことを通知します。
     *
     * <p>聴牌であれば手を開きます。
     */
    void roundExhausted(){
        if(isHandReady()){
            notifier.handRevealed(seatWind, handTiles, false);
        }
    }

    /**
     * 最後に追加した面子が暗槓であるか確認します。
     * @return true 暗槓の場合
     *         false 暗槓でない場合
     */
    boolean isLastMeldSelfQuad(){
        return openMelds.size()>0 && openMelds.getLast().isSelfQuad();
    }

    /**
     * 立直状態かどうか検査します。
     *
     * <p>立直宣言後で打牌が完了していない場合は立直状態とはなりません。
     * @return true 立直状態の場合
     *         false 立直状態でない場合
     */
    boolean isReady(){
        return ready;
    }

    /**
     * 聴牌かどうか検査します。
     * @return true 聴牌の場合
     *         false 聴牌でない場合
     */
    boolean isHandReady(){
        return !winningTiles.isEmpty();
    }

    /**
     * 流し満貫状態か検査します。
     * @return true 流し満貫状態の場合
     *         false 流し満貫状態でない場合
     */
    boolean isOrphanRiver(){
        return !called && river.stream().allMatch(Tile::isOrphan);
    }

    /**
     * 槓子を保持しているか検査します。
     * @return true 槓子を保持している場合
     *         false 槓子を保持していない場合
     */
    boolean hasQuad(){
        return openMelds.stream().anyMatch(Meld::isQuad);
    }

    /**
     * プレイヤーの自風を取得します。
     * @return 自風
     */
    public Wind getSeatWind(){
        return seatWind;
    }

    /**
     * プレイヤーの点数を取得します。
     * @return 点数
     */
    public int getScore(){
        return gamePlayer.getScore();
    }

    /**
     * プレイヤーの順位を取得します。
     * @return 順位
     */
    public int getRank(){
        return gamePlayer.getRank();
    }

    /**
     * プレイヤーの名前を取得します。
     * @return 名前
     */
    String getName(){
        return gamePlayer.getName();
    }

    /**
     * プレイヤー情報を取得します。
     * @return プレイヤー情報
     */
    PlayerData getPlayerData(){
        var data = new PlayerData();
        data.setName(getName());
        data.setInitialSeatWind(gamePlayer.getSeatWindAt(1));
        data.setSeatWind(seatWind);
        data.setScore(getScore());
        data.setRank(getRank());
        return data;
    }

    /**
     * このプレイヤーに与えられた点数を適用します。
     * @param score 点数(負の数も可)
     */
    void applyScore(int score){
        gamePlayer.applyScore(score);
    }

}
