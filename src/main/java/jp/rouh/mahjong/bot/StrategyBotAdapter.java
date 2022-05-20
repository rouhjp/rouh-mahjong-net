package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.game.event.*;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 捨て牌ボットをテーブル戦略とするアダプタクラス。
 * @author Rouh
 * @version 1.0
 */
class StrategyBotAdapter implements TileCounter, TableObserverAdapter, TableStrategy{
    private static final Logger LOG = LoggerFactory.getLogger(StrategyBotAdapter.class);
    private final DiscardingBot bot;
    private final List<Tile> exposedTiles = new ArrayList<>(136);
    private final List<Tile> allTiles = new ArrayList<>(14);
    private boolean distributed = false;

    StrategyBotAdapter(DiscardingBot bot){
        this.bot = bot;
    }

    @Override
    public int count(Tile tile){
        return (int)exposedTiles.stream().filter(t->t.equalsIgnoreRed(tile)).count();
    }

    @Override
    public void roundStarted(Wind wind, int count, int streak, int deposit, boolean last){
        synchronized(this){
            exposedTiles.clear();
            distributed = false;
        }
    }

    @Override
    public void wallTileRevealed(Side side, int column, Tile tile){
        synchronized(this){
            //ドラ表示牌の登録
            exposedTiles.add(tile);
        }
    }

    @Override
    public void riverTileAdded(Side side, Tile tile, boolean tilt){
        synchronized(this){
            if(side!=Side.SELF){
                //他家の捨て牌の登録
                exposedTiles.add(tile);
            }
        }
    }

    @Override
    public void tiltMeldAdded(Side side, Side tilt, List<Tile> tiles){
        synchronized(this){
            if(side!=Side.SELF){
                //大明槓/チー/ポンの手出し牌の登録
                if(tiles.size()==4){
                    exposedTiles.removeIf(t->t.equals(tiles.get(0)));
                    exposedTiles.addAll(tiles);
                }else if(tiles.size()==3){
                    switch(side){
                        case RIGHT ->{
                            exposedTiles.add(tiles.get(0));
                            exposedTiles.add(tiles.get(1));
                        }
                        case ACROSS ->{
                            exposedTiles.add(tiles.get(0));
                            exposedTiles.add(tiles.get(2));
                        }
                        case LEFT -> {
                            exposedTiles.add(tiles.get(1));
                            exposedTiles.add(tiles.get(2));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void meldTileAdded(Side side, int index, Tile tile){
        synchronized(this){
            //加槓牌の登録
            if(side!=Side.SELF){
                exposedTiles.add(tile);
            }
        }
    }

    @Override
    public void selfQuadAdded(Side side, List<Tile> tiles){
        synchronized(this){
            //暗槓の登録
            if(side!=Side.SELF){
                exposedTiles.addAll(tiles);
            }
        }
    }

    @Override
    public void handUpdated(List<Tile> allTiles, boolean wide){
        synchronized(this){
            if(wide){
                if(!distributed){
                    //配牌の登録
                    distributed = true;
                    exposedTiles.addAll(allTiles);
                }else{
                    //自摸牌の登録
                    exposedTiles.add(allTiles.get(allTiles.size() - 1));
                }
                this.allTiles.clear();
                this.allTiles.addAll(allTiles);
                this.allTiles.sort(Comparator.naturalOrder());
            }
        }
    }

    @Override
    public TurnAction selectTurnAction(List<TurnAction> choices){
        synchronized(this){
            try{
                var selector = new TurnActionSelector(choices);
                if(selector.canDeclareTsumo()){
                    return selector.getTsumoAction();
                }
                if(selector.canDeclareNineTiles()){
                    return selector.getNineTilesAction();
                }
                if(selector.canDeclareReady()){
                    var readyTiles = selector.getReadySelectableTiles();
                    var readyDiscardTile = bot.selectReady(allTiles, readyTiles, this);
                    return selector.getReadyActionOf(readyDiscardTile);
                }
                var discardTile = bot.select(allTiles, this);
                return selector.getDiscardActionOf(discardTile);
            }catch(Exception e){
                LOG.error(bot + " produced an error", e);
                LOG.error("allTiles=" + allTiles);
                LOG.error("choices=" + choices);
                LOG.error("choice automatically selected: " + choices.get(0));
                return choices.get(0);
            }
        }
    }

    @Override
    public CallAction selectCallAction(List<CallAction> choices){
        synchronized(this){
            var selector = new CallActionSelector(choices);
            if(selector.canDeclareRon()){
                return selector.getRonAction();
            }
            return selector.getPassAction();
        }
    }
}
