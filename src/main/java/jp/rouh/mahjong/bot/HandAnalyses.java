package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.score.HandTiles;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;
import jp.rouh.util.FlexList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 打牌ボットのロジック用ユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
final class HandAnalyses{
    private HandAnalyses(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 打牌時に最も手牌の評価が高くなる牌を算出します。
     * @param allTiles 手牌(14枚)
     * @param counter 残り枚数カウンター
     * @return 捨て牌
     */
    public static Tile selectDiscardTileByHighestReadyScore(List<Tile> allTiles, TileCounter counter){
        return allTiles.stream()
                .distinct()
                .max(Comparator.comparing(d->calculateReadyScore(new FlexList<>(allTiles).removed(d), counter)))
                .orElseThrow();
    }

    /**
     * 立直可能な手牌に対して, 最も和了牌枚数の多い立直宣言牌を算出します。
     * @param handTilesByReadyTile 立直宣言牌と打牌後の手牌のマップ
     * @param counter 残り枚数カウンター
     * @return 立直宣言牌
     */
    public static Tile selectReadyTileByHighestWaitingTileCount(Map<Tile, List<Tile>> handTilesByReadyTile, TileCounter counter){
        return handTilesByReadyTile.entrySet().stream()
                .max(Comparator.comparing(entry->
                        HandTiles.winningTilesOf(entry.getValue(), List.of()).stream()
                                .mapToInt(counter::count)
                                .sum()))
                .orElseThrow()
                .getKey();
    }

    /**
     * ある手牌の立直にどれほど近いか評価します。
     * <p>向聴数と受け入れ枚数を基準とした独自の評価点(0..900)が返されます。
     * @param handTiles 手牌(13枚)
     * @param counter 残り枚数カウンター
     * @return 評価点
     */
    public static int calculateReadyScore(List<Tile> handTiles, TileCounter counter){
        int msr = minimumSwapToReadyOfHand(handTiles);
        int etc = effectiveTilesOfHand(handTiles).stream()
                .mapToInt(counter::count)
                .sum();
        return (9 - msr)*100 + etc;
    }

    /**
     * ある手牌に対する有効牌を列挙します。
     * <p>有効牌とは, 自摸するとその手牌の向聴数を下げる牌のことを指します。
     * @param handTiles 手牌(13枚)
     * @return 有効牌のセット
     */
    private static Set<Tile> effectiveTilesOfHand(List<Tile> handTiles){
        var patternsByMsr = toPattern(handTiles).stream()
                .collect(Collectors.groupingBy(HandAnalyses::minimumSwapToReadyOfPattern));
        int currentMsr = Collections.min(patternsByMsr.keySet());
        var patterns = patternsByMsr.get(currentMsr);
        var acceptableTiles = patterns.stream()
                .flatMap(p->p.stream()
                        .flatMap(c->acceptableTilesOfChunk(c).stream()))
                .collect(Collectors.toSet());
        var discardTiles = patterns.stream()
                .flatMap(p->p.stream()
                        .filter(c->c.size()!=3).flatMap(List::stream))
                .collect(Collectors.toSet());
        var effectiveTiles = new HashSet<Tile>();
        for(var acceptTile:acceptableTiles){
            for(var discardTile:discardTiles){
                var derivedHandTiles = new FlexList<>(handTiles)
                        .removed(discardTile).added(acceptTile);
                if(minimumSwapToReadyOfHand(derivedHandTiles) < currentMsr){
                    effectiveTiles.add(acceptTile);
                    break;
                }
            }
        }
        return effectiveTiles;
    }

    /**
     * 手牌の向聴数を計算します。
     * @param handTiles 手牌(13枚)
     * @return 向聴数
     */
    private static int minimumSwapToReadyOfHand(List<Tile> handTiles){
        return toPattern(handTiles).stream()
                .mapToInt(HandAnalyses::minimumSwapToReadyOfPattern)
                .min().orElseThrow();
    }

    /**
     * 手牌パターンの向聴数を計算します。
     * @param pattern 手牌パターン
     * @return 向聴数
     */
    private static int minimumSwapToReadyOfPattern(List<List<Tile>> pattern){
        int mc = 0;
        int tbc = 0;
        int sbc = 0;
        for(var chunk:pattern){
            if(chunk.size()==3) mc++;
            if(chunk.size()==2){
                if(chunk.get(0).equalsIgnoreRed(chunk.get(1))){
                    tbc++;
                }else{
                    sbc++;
                }
            }
        }
        return minimumSwapToReady(tbc, sbc, mc);
    }

    /**
     * 向聴数を計算します。
     * @param tbc 対子の数
     * @param sbc 塔子の数
     * @param mc 面子の数
     * @return 向聴数
     */
    private static int minimumSwapToReady(int tbc, int sbc, int mc){
        if((tbc*2 + sbc*2 + mc*3)>13)
            throw new IllegalArgumentException("invalid pattern: tripleBase="+tbc+" straightBase="+sbc+" meld="+mc);
        if(tbc==0 && sbc==0 && mc==4) return 0;
        if(tbc==1 && sbc==1 && mc==3) return 0;
        if((sbc + mc)>4) return minimumSwapToReady(tbc, sbc - 1, mc);
        if(tbc>1) return minimumSwapToReady(tbc - 1, sbc + 1, mc);
        if(sbc>1) return 1 + minimumSwapToReady(tbc, sbc - 1, mc + 1);
        return 1 + minimumSwapToReady(tbc + 1, sbc, mc);
    }

    /**
     * あるパターン要素(面子/対子/塔子/孤立牌)に対する待ち牌を列挙します。
     * <p>待ち牌とは, その要素を昇格(孤立牌⇒塔子/対子, 塔子⇒面子, 対子⇒面子)させる牌のセットを取得します。
     * @param chunk パターン要素
     * @return 待ち牌
     */
    private static Set<Tile> acceptableTilesOfChunk(List<Tile> chunk){
        if(chunk.size()>3 || chunk.isEmpty()) throw new IllegalArgumentException("invalid chunk size: "+chunk);
        if(chunk.size()==3) return Set.of();
        if(chunk.size()==2){
            var base = chunk.stream().map(Tiles::toNonPrisedRed).sorted().toList();
            if(Tiles.isStep(base.get(0), base.get(1))){
                return Set.of(base.get(0).next());
            }
            if(Tiles.isNext(base.get(0), base.get(1))){
                var set = new HashSet<Tile>(2);
                Tiles.previousOf(base.get(0)).ifPresent(set::add);
                Tiles.nextOf(base.get(1)).ifPresent(set::add);
                return set;
            }
            return Set.of(base.get(0));
        }
        var set = new HashSet<Tile>(5);
        var tile = Tiles.toNonPrisedRed(chunk.get(0));
        var prev1 = Tiles.previousOf(tile);
        var prev2 = prev1.flatMap(Tiles::previousOf);
        var next1 = Tiles.nextOf(tile);
        var next2 = next1.flatMap(Tiles::nextOf);
        prev2.ifPresent(set::add);
        prev1.ifPresent(set::add);
        next2.ifPresent(set::add);
        next1.ifPresent(set::add);
        set.add(tile);
        return set;
    }

    /**
     * 手牌のパターンのセットを取得します。
     * <p>手牌パターンとは, 手牌を面子/塔子/対子/孤立牌に分解したものを指します。
     * <p>一つの手牌には複数の手牌パターンが存在しますが, このうち面子数が最大のものを取得します。
     * <p>手牌はまず順子を構成しうる境界で分割されます。各セクションに対してパターン要素を算出したのち,
     * これらセクションごとのパターン要素の組み合わせが最終的な手牌パターンとして返されることになります。
     * @param handTiles 手牌(13枚)
     * @return 手牌パターンのセット
     */
    static Set<List<List<Tile>>> toPattern(List<Tile> handTiles){
        var sections = new FlexList<>(handTiles).sorted().separateByDiff((t1, t2)->!Tiles.isNeighbour(t1, t2));
        return sections.stream()
                .map(HandAnalyses::toMeldBasePattern)
                .reduce(HandAnalyses::combine)
                .orElseThrow();
    }

    private static Set<List<List<Tile>>> combine(Set<List<List<Tile>>> ps1, Set<List<List<Tile>>> ps2){
        var patterns = new HashSet<List<List<Tile>>>();
        for(var p1:ps1){
            for(var p2:ps2){
                var pattern = new ArrayList<List<Tile>>();
                pattern.addAll(p1);
                pattern.addAll(p2);
                patterns.add(pattern);
            }
        }
        return patterns;
    }

    /**
     * 手牌のセクションをパターン要素(面子/対子/塔子/孤立牌)に分解します。
     * @param section 手牌セクション
     * @return 手牌セクションパターン
     */
    private static Set<List<List<Tile>>> toMeldBasePattern(List<Tile> section){
        var patterns = new HashSet<List<List<Tile>>>();
        for(var meldPattern:toMeldPatterns(section)){
            for(var basePattern:toBasePatterns(meldPattern.get(meldPattern.size() - 1))){
                var pattern = new ArrayList<>(meldPattern.subList(0, meldPattern.size() - 1));
                pattern.addAll(basePattern.subList(0, basePattern.size() - 1));
                basePattern.get(basePattern.size() - 1).forEach(t->pattern.add(List.of(t)));
                patterns.add(pattern);
            }
        }
        return patterns;
    }

    /**
     * 手牌のセクションを2枚で構成されるパターン要素(対子/塔子)に分解します。
     * <p>リストの最終要素に余りの牌が格納されます。
     * @param section 手牌セクション
     * @return 手牌セクションの対子/塔子パターン
     */
    private static Set<List<List<Tile>>> toBasePatterns(List<Tile> section){
        var basePattern = toBasePatterns(section, List.of(), List.of());
        return basePattern.stream()
                .filter(p1->basePattern.stream()
                        .filter(p2->p1!=p2)
                        .noneMatch(p2->p2.subList(0, p2.size() - 1).containsAll(p1.subList(0, p1.size() - 1))))
                .collect(Collectors.toSet());
    }

    private static Set<List<List<Tile>>> toBasePatterns(List<Tile> remaining, List<Tile> skipped, List<List<Tile>> bases){
        if(remaining.size()<2){
            var others = new ArrayList<Tile>();
            others.addAll(skipped);
            others.addAll(remaining);
            var pattern = new ArrayList<>(bases);
            pattern.add(others);
            return Set.of(pattern);
        }
        var set = new HashSet<List<List<Tile>>>();
        for(var base:findLeadingBases(remaining)){
            var derivedRemaining = new ArrayList<>(remaining);
            var derivedBases = new ArrayList<>(bases);
            base.forEach(derivedRemaining::remove);
            derivedBases.add(base);
            set.addAll(toMeldPatterns(derivedRemaining, skipped, derivedBases));
        }
        var skipping = remaining.stream()
                .filter(t->t.equalsIgnoreRed(remaining.get(0)))
                .toList();
        var derivedRemaining = new ArrayList<>(remaining);
        var derivedSkipped = new ArrayList<>(skipped);
        skipping.forEach(derivedRemaining::remove);
        derivedSkipped.addAll(skipping);
        set.addAll(toMeldPatterns(derivedRemaining, derivedSkipped, bases));
        return set;
    }

    /**
     * 手牌のセクションを3枚で構成されるパターン要素(面子)に分解します。
     * <p>リストの最終要素に余りの牌が格納されます。
     * @param section 手牌セクション
     * @return 手牌セクションの面子パターン
     */
    private static Set<List<List<Tile>>> toMeldPatterns(List<Tile> section){
        var meldPatterns = toMeldPatterns(section, List.of(), List.of());
        var maxMeldCount = meldPatterns.stream().mapToInt(List::size).max().orElseThrow();
        return meldPatterns.stream()
                .filter(p->p.size()==maxMeldCount)
                .collect(Collectors.toSet());
    }

    private static Set<List<List<Tile>>> toMeldPatterns(List<Tile> remaining, List<Tile> skipped, List<List<Tile>> melds){
        if(remaining.size()<3){
            var others = new ArrayList<Tile>();
            others.addAll(skipped);
            others.addAll(remaining);
            var pattern = new ArrayList<>(melds);
            pattern.add(others);
            return Set.of(pattern);
        }
        var set = new HashSet<List<List<Tile>>>();
        for(var meld:findLeadingMelds(remaining)){
            var derivedRemaining = new ArrayList<>(remaining);
            var derivedMelds = new ArrayList<>(melds);
            meld.forEach(derivedRemaining::remove);
            derivedMelds.add(meld);
            set.addAll(toMeldPatterns(derivedRemaining, skipped, derivedMelds));
        }
        var skipping = remaining.stream()
                .filter(t->t.equalsIgnoreRed(remaining.get(0)))
                .toList();
        var derivedRemaining = new ArrayList<>(remaining);
        var derivedSkipped = new ArrayList<>(skipped);
        skipping.forEach(derivedRemaining::remove);
        derivedSkipped.addAll(skipping);
        set.addAll(toMeldPatterns(derivedRemaining, derivedSkipped, melds));
        return set;
    }

    /**
     * ソート済みの牌のリストから, 先頭の牌を含む塔子/対子を抽出します。
     * @param tiles ソート済みリスト
     * @return 塔子/対子のセット
     */
    private static Set<List<Tile>> findLeadingBases(List<Tile> tiles){
        if(tiles.size()<2) return Set.of();
        var bases = new HashSet<List<Tile>>(3);
        var first = tiles.get(0);
        var tail = tiles.subList(1, tiles.size());
        var pair = tail.stream().filter(t->t.equalsIgnoreRed(first)).findFirst();
        var next = tail.stream().filter(t->t.isNextOf(first)).findFirst();
        var step = tail.stream().filter(t->Tiles.isStep(t, first)).findFirst();
        pair.ifPresent(t->bases.add(List.of(first, t)));
        next.ifPresent(t->bases.add(List.of(first, t)));
        step.ifPresent(t->bases.add(List.of(first, t)));
        return bases;
    }

    /**
     * ソート済みの牌のリストから, 先頭の牌を含む面子を抽出します。
     * @param tiles ソート済みリスト
     * @return 面子のセット
     */
    private static Set<List<Tile>> findLeadingMelds(List<Tile> tiles){
        if(tiles.size()<3) return Set.of();
        var melds = new HashSet<List<Tile>>(2);
        if(tiles.get(0).equalsIgnoreRed(tiles.get(2))){
            melds.add(tiles.subList(0, 2));
        }
        var straight = new ArrayList<Tile>();
        straight.add(tiles.get(0));
        for(int i = 1; i<tiles.size(); i++){
            if(tiles.get(i).isNextOf(straight.get(straight.size() - 1))){
                straight.add(tiles.get(i));
                if(straight.size()==3){
                    melds.add(straight);
                    break;
                }
            }
        }
        return melds;
    }
}
