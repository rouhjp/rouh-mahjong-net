package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.score.PaymentTable;
import jp.rouh.mahjong.tile.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jp.rouh.mahjong.game.event.TurnActionType.*;
import static jp.rouh.mahjong.game.RoundResultType.*;

/**
 * 局クラス。
 * @author Rouh
 * @version 1.0
 */
public class Round implements TableMasterAdapter, RoundAccessor, WallObserver{
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
        this(params, gamePlayers, (d1, d2)->new ArrayWall(Tiles.shuffledTileSet().toArray(new Tile[0]), d1 + d2));
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
        distribute();
        while(wall.hasDrawableTile()){
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
        roundDrawn(DrawType.EXHAUSTED);
        var orphanRiverWinds = Stream.of(Wind.values())
                .filter(wind->getPlayerAt(wind).isOrphanRiver()).toList();
        if(!orphanRiverWinds.isEmpty()){
            var paymentTable = new PaymentTable();
            var riverScoreDataList = new ArrayList<RiverScoreData>();
            boolean secondary = false;
            for(var orphanRiverWind:orphanRiverWinds){
                var result = getPlayerAt(orphanRiverWind).declareOrphanRiver(secondary);
                secondary = true;
                riverScoreDataList.add(result.getRiverScoreData());
                paymentTable.apply(result.getHandScore(), result.getWinningContext());
            }
            riverScoreNotified(riverScoreDataList);
            applyPayment(paymentTable);
            roundFinished();
            return orphanRiverWinds.contains(Wind.EAST)? DEALER_VICTORY:NON_DEALER_VICTORY;
        }
        var handReadyWinds = Stream.of(Wind.values())
                .filter(wind->getPlayerAt(wind).isHandReady()).toList();
        for(var handReadyWind:handReadyWinds){
            getPlayerAt(handReadyWind).roundExhausted();
        }
        applyPayment(PaymentTable.ofDrawn(handReadyWinds));
        roundFinished();
        return handReadyWinds.contains(Wind.EAST)? DRAW_ADVANTAGE_DEALER:DRAW_ADVANTAGE_NON_DEALER;
    }

    private void distribute(){
        for(int i = 0; i<3; i++){
            for(var wind:Wind.values()){
                roundPlayers.get(wind).distributed(wall.takeFourTiles());
            }
        }
        for(var wind:Wind.values()){
            roundPlayers.get(wind).distributed(wall.takeTile());
        }
    }

    private void doTurn(){
        LOG.info(turnWind+" turn started");
        var turnPlayer = roundPlayers.get(turnWind);
        var turnAction = turnPlayer.selectTurnAction(turnPlayer.getTurnChoices(afterCall, afterQuad));
        LOG.info(turnWind+" "+turnAction);
        if(turnAction.type()==TSUMO){
            var result = turnPlayer.declareTsumo(afterQuad);
            var paymentTable = new PaymentTable();
            paymentTable.apply(result.getHandScore(), result.getWinningContext());
            handScoreNotified(List.of(result.getHandScoreData()));
            applyPayment(paymentTable);
            resultType = turnWind==Wind.EAST? DEALER_VICTORY:NON_DEALER_VICTORY;
            return;
        }
        if(turnAction.type()==NINE_TILES){
            turnPlayer.declareNineTiles();
            roundDrawn(DrawType.NINE_TILES_DECLARED);
            resultType = turnWind==Wind.EAST? DRAW_ADVANTAGE_DEALER:DRAW_ADVANTAGE_NON_DEALER;
            return;
        }
        if(turnAction.type()==TURN_KAN){
            var targetTile = turnAction.argument();
            var self = turnPlayer.declareKan(targetTile);
            var callActions = mediateCallActionForQuad(targetTile, self);
            if(!callActions.isEmpty()){
                mediateWinnings(callActions, targetTile, true);
                return;
            }
            if(self){
                wall.revealIndicatorImmediately();
            }
            afterCall = false;
            afterQuad = true;
            return;
        }
        if(turnAction.type()==READY_DISCARD){
            turnPlayer.declareReady();
            seatUpdated();
        }
        var discardTile = turnAction.argument();
        turnPlayer.discard(discardTile);
        riverTileAdded(turnWind, discardTile, turnAction.type()==READY_DISCARD);
        doDiscard(discardTile);
    }

    private void doDiscard(Tile discarded){
        var discarderWind = turnWind;
        var turnPlayer = getPlayerAt(turnWind);
        var actions = mediateCallAction(discarded);
        for(var action:actions){
            LOG.info(action.from()+" "+action.get());
        }
        if(!actions.isEmpty()){
            switch(actions.get(0).get().type()){
                case RON -> {
                    mediateWinnings(actions, discarded, false);
                    return;
                }
                case KAN -> {
                    assert actions.size()==1: "multiple kan declared simultaneously";
                    var callWind = actions.get(0).from();
                    getPlayerAt(callWind).declareKan(discarded, turnWind);
                    turnPlayer.discardTileClaimed();
                    roundPlayers.values().forEach(RoundPlayer::aroundInterrupted);
                    firstAround = false;
                    afterCall = true;
                    afterQuad = true;
                    turnWind = callWind;
                }
                case PON -> {
                    assert actions.size()==1: "multiple pon declared simultaneously";
                    var callWind = actions.get(0).from();
                    var baseTiles = actions.get(0).get().arguments();
                    getPlayerAt(callWind).declarePon(discarded, baseTiles, turnWind);
                    turnPlayer.discardTileClaimed();
                    roundPlayers.values().forEach(RoundPlayer::aroundInterrupted);
                    firstAround = false;
                    afterCall = true;
                    afterQuad = false;
                    turnWind = callWind;
                }
                case CHI -> {
                    assert actions.size()==1: "multiple chi declared simultaneously";
                    var callWind = actions.get(0).from();
                    var baseTiles = actions.get(0).get().arguments();
                    getPlayerAt(callWind).declareChi(discarded, baseTiles);
                    turnPlayer.discardTileClaimed();
                    roundPlayers.values().forEach(RoundPlayer::aroundInterrupted);
                    firstAround = false;
                    afterCall = true;
                    afterQuad = false;
                    turnWind = callWind;
                }
            }
            for(var wind:Wind.values()){
                getPlayerAt(wind).discardTileSettled(discarderWind, discarded, turnWind);
            }
        }else{
            for(var wind:Wind.values()){
                getPlayerAt(wind).discardTileSettled(discarderWind, discarded, null);
            }
            afterCall = false;
            afterQuad = false;
            turnWind = turnWind.next();
        }
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

    private void applyPayment(PaymentTable table){
        LOG.info("payment: "+table);
        var dataMap = new HashMap<Wind, PaymentData>();
        for(var wind:Wind.values()){
            dataMap.put(wind, new PaymentData());
            dataMap.get(wind).setName(getPlayerAt(wind).getName());
            dataMap.get(wind).setWind(wind);
            dataMap.get(wind).setScoreBefore(getPlayerAt(wind).getScore());
            dataMap.get(wind).setRankBefore(getPlayerAt(wind).getRank());
        }
        for(var wind:Wind.values()){
            getPlayerAt(wind).applyScore(table.paymentOf(wind));
        }
        for(var wind:Wind.values()){
            dataMap.get(wind).setScoreAfter(getPlayerAt(wind).getScore());
            dataMap.get(wind).setRankAfter(getPlayerAt(wind).getRank());
        }
        paymentNotified(dataMap);
    }

    private void mediateWinnings(List<SignedCallAction> winningActions, Tile winningTile, boolean quadGrab){
        LOG.info("winnings: "+winningActions.stream().map(SignedCallAction::from).toList());
        LOG.info("deposit="+deposit);
        if(winningActions.size()==3){
            for(var winningAction:winningActions){
                declared(winningAction.from(), Declaration.RON);
            }
            roundDrawn(DrawType.THREE_PLAYERS_CLAIMED);
            resultType = DRAW_ADVANTAGE_NON_DEALER;
            return;
        }
        boolean dealerWon = winningActions.stream().anyMatch(sca->sca.from()==Wind.EAST);
        resultType = dealerWon? DEALER_VICTORY:NON_DEALER_VICTORY;
        var paymentTable = new PaymentTable();
        var handScoreDataList = new ArrayList<HandScoreData>();
        boolean secondary = false;
        for(var side:new Side[]{Side.RIGHT, Side.ACROSS, Side.LEFT}){
            var winnerWind = side.of(turnWind);
            if(winningActions.stream().anyMatch(action->action.from()==winnerWind)){
                var winner = getPlayerAt(winnerWind);
                var result = quadGrab?
                        winner.declareRonByQuad(winningTile, turnWind, secondary):
                        winner.declareRon(winningTile, turnWind, secondary);
                secondary = true;
                paymentTable.apply(result.getHandScore(), result.getWinningContext());
                handScoreDataList.add(result.getHandScoreData());
            }
        }
        handScoreNotified(handScoreDataList);
        applyPayment(paymentTable);
    }

    private List<SignedCallAction> mediateCallActionForQuad(Tile target, boolean self){
        var executorService = Executors.newFixedThreadPool(3);
        var futures = new ArrayList<Future<SignedCallAction>>(3);
        for(var eachWind:turnWind.others()){
            var future = executorService.submit(()->{
                var action = self?
                        getPlayerAt(eachWind).selectCallActionForSelfQuad(target):
                        getPlayerAt(eachWind).selectCallActionForAddQuad(target);
                return new SignedCallAction(eachWind, action);
            });
            futures.add(future);
        }
        try{
            var actions = new ArrayList<SignedCallAction>(3);
            for(var future: futures){
                var action = future.get();
                if(action.get().type()!=CallActionType.PASS){
                    actions.add(action);
                }
            }
            return actions;
        }catch(InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
    }

    private List<SignedCallAction> mediateCallAction(Tile discarded){
        var executorService = Executors.newFixedThreadPool(3);
        var completionService = new ExecutorCompletionService<SignedCallAction>(executorService);
        var choiceMap = new HashMap<Wind, List<CallAction>>();
        for(var eachWind:turnWind.others()){
            var choices = getPlayerAt(eachWind).getCallChoices(turnWind, discarded);
            choiceMap.put(eachWind, choices);
        }
        if(choiceMap.values().stream().allMatch(choices->choices.size()==1)){
            return List.of();
        }
        var futures = new ArrayList<Future<SignedCallAction>>(3);
        for(var eachWind:turnWind.others()){
            var future = completionService.submit(()->{
                var choices = choiceMap.get(eachWind);
                if(choices.size()==1){
                    return new SignedCallAction(eachWind, choices.get(0));
                }
                var action = getPlayerAt(eachWind).selectCallAction(choices);
                return new SignedCallAction(eachWind, action);
            });
            futures.add(future);
        }
        executorService.shutdown();
        try{
            var finalAnswer = (SignedCallAction)null;
            var answers = new ArrayList<SignedCallAction>();
            for(int i = 0; i<3; i++){
                var answer = completionService.take().get();
                answers.add(answer);
                choiceMap.remove(answer.from());
                if(answer.get().type()!=CallActionType.PASS){
                    if(choiceMap.values().stream().noneMatch(choices->
                            choices.stream().anyMatch(choice->choice.higherPriorityThan(answer.get())))){
                        finalAnswer = answer;
                        executorService.shutdownNow();
                        for(var future:futures){
                            if(!future.isDone()){
                                future.cancel(true);
                            }
                        }
                    }
                }
            }
            if(finalAnswer!=null) return List.of(finalAnswer);
            int highestPriority = answers.stream().mapToInt(sca->sca.get().priority()).max().orElseThrow();
            return answers.stream()
                    .filter(sca->sca.get().priority()==highestPriority)
                    .filter(sca->sca.get().type()!=CallActionType.PASS)
                    .toList();
        }catch(InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
    }

    private void seatUpdated(){
        var seatMap = Stream.of(Wind.values())
                .collect(Collectors.toMap(wind->wind, wind->roundPlayers.get(wind).getPlayerData()));
        seatUpdated(seatMap);
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
