package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.score.HandTiles;
import jp.rouh.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 打牌ボットの牌効率をテストするクラス。
 * @author Rouh
 * @version 1.0
 */
class BotPerformanceTester{
    private final DiscardingBot bot;

    /***
     * 新規テスターを生成します。
     * @param bot テスト対象の打牌ボット。
     */
    BotPerformanceTester(DiscardingBot bot){
        this.bot = bot;
    }

    /**
     * テスト対象ボットの性能を計測し, 標準出力に結果を出力します。
     * @param n 試行回数
     * @param t 一回あたりのターン数
     */
    void testPerformance(int n, int t){
        if(n<=0 || t<=0) throw new IllegalStateException("params must be more than 1: n="+n+" t="+t);
        long beforeMillis = System.currentTimeMillis();
        int callCount = 0;
        int successCount = 0;
        for(int i = 0; i<n; i++){
            var supplier = new TileSupplier();
            var tiles = new ArrayList<Tile>(14);
            tiles.addAll(supplier.take(13));
            tiles.sort(Comparator.naturalOrder());
            for(int j = 0; j<t; j++){
                var tileIn = supplier.take();
                tiles.add(tileIn);
                tiles.sort(Comparator.naturalOrder());
                var tileOut = bot.select(tiles, supplier);
                callCount++;
                if(!tiles.remove(tileOut)){
                    throw new IllegalStateException("bot selected invalid tile: "+tileOut+" from "+tiles);
                }
                if(HandTiles.isHandReady(tiles, List.of())){
                    successCount++;
                    break;
                }
            }
        }
        long afterMillis = System.currentTimeMillis();
        long millisPerCall = (afterMillis - beforeMillis)/callCount;
        int successRate = (int)(100*successCount/(double)n);
        System.out.println("performance of "+bot+"(n="+n+" t="+t+"): success="+successCount+"("+successRate+"%) cost="+millisPerCall);
    }

    /**
     * ボットのプレイを標準出力に出力します。
     * @param t ターン数
     */
    void testPlay(int t){
        testPlay(t,
                (tiles, tile)->System.out.println(viewOf(tiles)+" "+ viewOf(tile)),
                (tile, tiles)->System.out.println("discard "+ viewOf(tile)+"\n"));
    }

    private void testPlay(int t, BiConsumer<List<Tile>, Tile> beforeAction, BiConsumer<Tile, List<Tile>> afterAction){
        var supplier = new TileSupplier();
        var tiles = new ArrayList<Tile>(14);
        tiles.addAll(supplier.take(13));
        tiles.sort(Comparator.naturalOrder());
        for(int i = 0; i<t; i++){
            var tileIn = supplier.take();
            beforeAction.accept(tiles, tileIn);
            tiles.add(tileIn);
            tiles.sort(Comparator.naturalOrder());
            var tileOut = bot.select(tiles, supplier);
            if(!tiles.remove(tileOut)){
                throw new IllegalStateException("bot selected invalid tile: "+tileOut+" from "+tiles);
            }
            afterAction.accept(tileOut, tiles);
            System.out.println(HandAnalyses.calculateReadyScore(tiles, supplier));
        }
    }

    private static String viewOf(List<Tile> tiles){
        return tiles.stream().map(BotPerformanceTester::viewOf).collect(Collectors.joining());
    }

    private static String viewOf(Tile tile){
        if(tile.isCharacter()) return "["+tile.suitNumber()+(tile.isPrisedRed()?"!":"")+"]";
        if(tile.isCircle()) return "("+tile.suitNumber()+(tile.isPrisedRed()?"!":"")+")";
        if(tile.isBamboo()) return "{"+tile.suitNumber()+(tile.isPrisedRed()?"!":"")+"}";
        return "|" + switch(tile){
            case WE -> "東";
            case WS -> "南";
            case WW -> "西";
            case WN -> "北";
            case DW -> "白";
            case DG -> "發";
            case DR -> "中";
            default-> tile.name();
        } + "|";
    }
}
