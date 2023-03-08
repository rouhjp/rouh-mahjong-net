package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.score.PaymentTable;
import jp.rouh.mahjong.tile.*;
import jp.rouh.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jp.rouh.mahjong.game.RoundResultType.*;

/**
 * 局クラス。
 * @author Rouh
 * @version 1.0
 */
class Round implements TableMasterAdapter, RoundAccessor, WallObserver{
    private static final Logger LOG = LoggerFactory.getLogger(Round.class);
    private final Map<Wind, RoundPlayer> roundPlayers;
    private final WallGenerator wallGenerator;
    private final RoundID id;
    private final int streak;
    private final int deposit;
    private final boolean last;
    private Wall wall;
    private Wind turnWind = Wind.EAST;
    private boolean afterCall = false;
    private boolean afterQuad = false;
    private boolean firstAround = true;
    private final List<Tile> firstAroundDiscards = new ArrayList<>(4);
    private RoundResultType resultType;

    /**
     * 局のコンストラクタ。
     * @param params 局パラメータ
     * @param gamePlayers プレイヤー
     */
    Round(RoundParameter params, List<? extends GamePlayerAccessor> gamePlayers){
        this(params, gamePlayers, (d1, d2)->new ArrayWall(Tiles.newShuffledTileSet().toArray(new Tile[0]), d1 + d2));
    }

    /**
     * 局のコンストラクタ。
     * @param params 局パラメータ
     * @param gamePlayers プレイヤー
     * @param wallGenerator 牌山生成関数
     */
    Round(RoundParameter params, List<? extends GamePlayerAccessor> gamePlayers, WallGenerator wallGenerator){
        this.id = params.getRoundId();
        this.streak = params.getStreakCount();
        this.deposit = params.getDepositCount();
        this.last = params.isLast();
        this.wallGenerator = wallGenerator;
        this.roundPlayers = gamePlayers.stream()
                .map(gamePlayer->new RoundPlayer(this, gamePlayer))
                .collect(Collectors.toMap(RoundPlayer::getSeatWind, Function.identity()));
    }

    /**
     * 局を開始します。
     * @throws IllegalStateException プレイヤー数が不正の場合
     */
    public RoundResultType start(int dice1, int dice2){
        LOG.info("round started");
        roundStarted(id.wind(), id.count(), streak, deposit, last);
        seatUpdated();
        diceRolled(Wind.EAST, dice1, dice2);
        wall = wallGenerator.generate(dice1, dice2);
        wallGenerated();
        wall.addObserver(this);
        wall.revealIndicatorImmediately();
        for(int i = 0; i<3; i++){
            for(var wind:Wind.values()){
                roundPlayers.get(wind).distributed(wall.takeFourTiles());
            }
        }
        for(var wind:Wind.values()){
            roundPlayers.get(wind).distributed(wall.takeTile());
        }
        while(wall.hasDrawableTile() || afterCall){
            turnStarted(turnWind);
            var turnPlayer = roundPlayers.get(turnWind);
            if(afterQuad){
                turnPlayer.draw(wall.takeQuadTile());
                afterCall = false;
            }else if(!afterCall){
                turnPlayer.draw(wall.takeTile());
            }
            doTurn();
            if(resultType!=null){
                roundFinished();
                return resultType;
            }
        }
        doExhaustion();
        roundFinished();
        return resultType;
    }

    /**
     * 流局(荒牌平局)時の処理。
     */
    private void doExhaustion(){
        roundDrawn(DrawType.EXHAUSTED);
        var orphanRiverWinds = Stream.of(Wind.values())
                .filter(wind->getPlayerAt(wind).isOrphanRiver()).toList();
        if(!orphanRiverWinds.isEmpty()){
            var payments = new PaymentTable();
            var riverScores = new ArrayList<RiverScoreData>();
            boolean secondary = false;
            for(var orphanRiverWind:orphanRiverWinds){
                var result = getPlayerAt(orphanRiverWind).declareOrphanRiver(secondary);
                riverScores.add(result.getRiverScoreData());
                payments.apply(result.getHandScore(), result.getWinningContext());
                secondary = true;
            }
            riverScoreNotified(riverScores);
            paymentNotified(payments);
            resultType =  orphanRiverWinds.contains(Wind.EAST)? DEALER_VICTORY:NON_DEALER_VICTORY;
            return;
        }
        var handReadyWinds = Stream.of(Wind.values())
                .filter(wind->getPlayerAt(wind).isHandReady()).toList();
        for(var handReadyWind:handReadyWinds){
            getPlayerAt(handReadyWind).roundExhausted();
        }
        paymentNotified(PaymentTable.ofDrawn(handReadyWinds));
        resultType = handReadyWinds.contains(Wind.EAST)? DRAW_ADVANTAGE_DEALER:DRAW_ADVANTAGE_NON_DEALER;
    }

    /**
     * ターン開始時の処理。
     */
    private void doTurn(){
        var turnPlayer = roundPlayers.get(turnWind);
        var turnChoices = turnPlayer.getTurnChoices(afterCall, afterQuad);
        LOG.debug("["+turnWind+"]"+turnPlayer.getName()+": selecting turn action from "+turnChoices);
        var turnAction = turnPlayer.selectTurnAction(turnPlayer.getTurnChoices(afterCall, afterQuad));
        LOG.debug("["+turnWind+"]"+turnPlayer.getName()+": selected "+turnAction);
        switch(turnAction.type()){
            case TSUMO -> {
                var result = turnPlayer.declareTsumo(afterQuad);
                var payments = new PaymentTable();
                payments.apply(result.getHandScore(), result.getWinningContext());
                handScoreNotified(List.of(result.getHandScoreData()));
                paymentNotified(payments);
                resultType = turnWind==Wind.EAST? DEALER_VICTORY:NON_DEALER_VICTORY;
            }
            case NINE_TILES -> {
                turnPlayer.declareNineTiles();
                roundDrawn(DrawType.NINE_TILES_DECLARED);
                resultType = turnWind==Wind.EAST? DRAW_ADVANTAGE_DEALER:DRAW_ADVANTAGE_NON_DEALER;
            }
            case TURN_KAN -> {
                var declaredTile = turnAction.argument();
                var selfQuad = turnPlayer.declareKan(declaredTile);
                var callActions = new CallActionMediator(turnWind.others())
                        .withPlayers(this::getPlayerAt)
                        .withChoices(wind->getPlayerAt(wind).getQuadCallChoices(declaredTile, selfQuad))
                        .mediate();
                if(!callActions.isEmpty()){
                    doClaim(callActions, declaredTile, true);
                    return;
                }
                if(selfQuad){
                    wall.revealIndicatorImmediately();
                }
                afterCall = false;
                afterQuad = true;
            }
            case READY_DISCARD -> {
                var discardedTile = turnAction.argument();
                turnPlayer.declareReady();
                turnPlayer.discard(discardedTile);
                seatUpdated();
                riverTileAdded(turnWind, discardedTile, true);
                doDiscard(discardedTile);
            }
            case DISCARD_ANY, DISCARD_DRAWN -> {
                var discardedTile = turnAction.argument();
                turnPlayer.discard(discardedTile);
                riverTileAdded(turnWind, discardedTile, false);
                doDiscard(discardedTile);
            }
        }
    }

    /**
     * 打牌時の処理。
     * @param discarded 打牌
     */
    private void doDiscard(Tile discarded){
        var discarderWind = turnWind;
        var discarder = getPlayerAt(turnWind);
        LOG.debug("["+turnWind+"]"+discarder.getName()+" discarded "+discarded);
        var actions = new CallActionMediator(discarderWind.others())
                .withPlayers(this::getPlayerAt)
                .withChoices(wind->getPlayerAt(wind).getCallChoices(discarderWind, discarded))
                .mediate();
        actions.forEach((wind, action)->LOG.debug("["+wind+"] select call action "+action));
        if(!actions.isEmpty()){
            if(actions.values().stream().allMatch(action->action.type()==CallActionType.RON)){
                doClaim(actions, discarded, false);
                return;
            }
            assert actions.size()==1;
            var action = List.copyOf(actions.values()).get(0);
            var callerWind = List.copyOf(actions.keySet()).get(0);
            var caller = getPlayerAt(callerWind);
            switch(action.type()){
                case KAN -> caller.declareKan(discarded, discarderWind);
                case PON -> caller.declarePon(discarded, action.arguments(), discarderWind);
                case CHI -> caller.declareChi(discarded, action.arguments());
                default -> throw new AssertionError();
            }
            discarder.discardTileClaimed();
            roundPlayers.values().forEach(RoundPlayer::aroundInterrupted);
            roundPlayers.values().forEach(player->player.discardTileSettled(discarderWind, discarded, callerWind));
            afterCall = true;
            afterQuad = action.type()==CallActionType.KAN;
            turnWind = callerWind;
            firstAround = false;
        }else{
            roundPlayers.values().forEach(player->player.discardTileSettled(discarderWind, discarded, null));
            afterCall = false;
            afterQuad = false;
            turnWind = turnWind.next();
            if(firstAround){
                firstAroundDiscards.add(discarded);
                if(firstAroundDiscards.size()==4){
                    firstAround = false;
                    if(firstAroundDiscards.get(0).isWind() && firstAroundDiscards.stream().distinct().count()==1){
                        roundDrawn(DrawType.FOUR_WINDS_DISCARDED);
                        resultType = DRAW_ADVANTAGE_NON_DEALER;
                        return;
                    }
                }
            }
        }
        if(getReadyCount()==4){
            roundDrawn(DrawType.FOUR_PLAYERS_READIED);
            resultType = DRAW_ADVANTAGE_NON_DEALER;
            return;
        }
        if(getTotalQuadCount()==4 && getQuadPlayerCount()>=2){
            roundDrawn(DrawType.FOUR_QUADS_BUILT);
            resultType = DRAW_ADVANTAGE_NON_DEALER;
            return;
        }
        wall.revealIndicatorsIfPresent();
    }

    /**
     * ロン発生時の処理。
     * @param ronActions ロン宣言のマップ
     * @param winningTile 和了牌
     * @param againstQuad 槍槓かどうか
     */
    private void doClaim(Map<Wind, CallAction> ronActions, Tile winningTile, boolean againstQuad){
        if(ronActions.size()==3){
            for(var wind:ronActions.keySet()){
                declared(wind, Declaration.RON);
            }
            roundDrawn(DrawType.THREE_PLAYERS_CLAIMED);
            resultType = DRAW_ADVANTAGE_NON_DEALER;
            return;
        }
        resultType = ronActions.containsKey(Wind.EAST)? DEALER_VICTORY:NON_DEALER_VICTORY;
        var payments = new PaymentTable();
        var handScores = new ArrayList<HandScoreData>();
        boolean secondary = false;
        for(var side:new Side[]{Side.RIGHT, Side.ACROSS, Side.LEFT}){
            if(ronActions.containsKey(side.of(turnWind))){
                var winner = getPlayerAt(side.of(turnWind));
                var result = againstQuad?
                        winner.declareRonByQuad(winningTile, turnWind, secondary):
                        winner.declareRon(winningTile, turnWind, secondary);
                secondary = true;
                payments.apply(result.getHandScore(), result.getWinningContext());
                handScores.add(result.getHandScoreData());
            }
        }
        handScoreNotified(handScores);
        paymentNotified(payments);
    }

    private void paymentNotified(PaymentTable table){
        var beforeRankMap = Maps.ofEnum(Wind.class, wind->getPlayerAt(wind).getRank());
        var beforeScoreMap = Maps.ofEnum(Wind.class, wind->getPlayerAt(wind).getScore());
        roundPlayers.forEach((wind, player)->player.applyScore(table.paymentOf(wind)));
        var dataMap = new HashMap<Wind, PaymentData>();
        for(var wind:Wind.values()){
            var data = new PaymentData();
            data.setName(getPlayerAt(wind).getName());
            data.setWind(getPlayerAt(wind).getSeatWind());
            data.setRankBefore(beforeRankMap.get(wind));
            data.setScoreBefore(beforeScoreMap.get(wind));
            data.setRankAfter(getPlayerAt(wind).getRank());
            data.setScoreAfter(getPlayerAt(wind).getScore());
            dataMap.put(wind, data);
        }
        paymentNotified(dataMap);
    }

    private void seatUpdated(){
        var dataMap = new HashMap<Wind, PlayerData>();
        for(var wind:Wind.values()){
            dataMap.put(wind, getPlayerAt(wind).getPlayerData());
        }
        seatUpdated(dataMap);
    }

    @Override
    public Wind getRoundWind(){
        return id.wind();
    }

    @Override
    public int getRoundCount(){
        return id.count();
    }

    @Override
    public int getRoundStreakCount(){
        return streak;
    }

    @Override
    public int getRoundDepositCount(){
        return deposit;
    }

    @Override
    public int getReadyCount(){
        return (int)roundPlayers.values().stream()
                .filter(RoundPlayer::isReady)
                .count();
    }

    @Override
    public int getTotalQuadCount(){
        return wall.getQuadCount();
    }

    private int getQuadPlayerCount(){
        return (int)roundPlayers.values().stream()
                .filter(RoundPlayer::hasQuad)
                .count();
    }

    @Override
    public boolean isFirstAround(){
        return firstAround;
    }

    @Override
    public boolean isLastAround(){
        return wall.getDrawableTileCount()<4;
    }

    @Override
    public boolean isLastTurn(){
        return wall.getDrawableTileCount()==0;
    }

    @Override
    public List<Tile> getUpperIndicators(){
        return wall.getUpperIndicators();
    }

    @Override
    public List<Tile> getLowerIndicators(){
        return wall.getLowerIndicators();
    }

    @Override
    public TableMaster getMaster(){
        return this;
    }

    @Override
    public RoundPlayer getPlayerAt(Wind seatWind){
        return roundPlayers.get(seatWind);
    }

    @Override
    public void tileTaken(Wind wind, int column, int floor){
        wallTileTaken(wind, column, floor);
    }

    @Override
    public void tileRevealed(Wind wind, int column, Tile tile){
        wallTileRevealed(wind, column, tile);
    }
}
