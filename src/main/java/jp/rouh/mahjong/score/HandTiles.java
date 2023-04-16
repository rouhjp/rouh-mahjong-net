package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;
import jp.rouh.util.Lists;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

/**
 * 手牌に関する操作を扱うユーティリティクラス。
 *
 * @author Rouh
 * @version 1.0
 */
public final class HandTiles{
    private HandTiles(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 手牌の長さが正しいかどうか検査し, 不正であれば例外をスローします。
     *
     * <p>自摸牌を除く門前の手牌は常に13枚であることが成立します。
     * 副露した場合は, 基本的に手牌の枚数は13 - 3*副露した数となります。
     * 例外的にポンもしくはチー副露直後の打牌前のみ手牌の枚数は1枚多い状態になります。
     * <p>このバリデーション処理では, 手牌が基本的な状態であることを検査し,
     * 後続の処理に例外的な値が渡らないことを保証します。
     * @param handTiles 手牌
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    private static void requireValidSize(List<Tile> handTiles){
        if(handTiles.isEmpty() || handTiles.size()%3!=1 || handTiles.size()>13){
            throw new IllegalArgumentException("illegal size of hand tiles");
        }
    }

    /**
     * 手牌が九種九牌形であるかどうか検査します。
     *
     * <p>手牌が副露によって13枚未満の場合は{@code false}が返されます。
     * @param handTiles 手牌(自摸牌を含まない、長さが13以下の3N+1のリスト)
     * @param drawnTile 自摸牌
     * @return true  九種九牌形である場合
     *         false 九種九牌形でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isNineTiles(List<Tile> handTiles, Tile drawnTile){
        requireValidSize(handTiles);
        var allTiles = Lists.added(handTiles, drawnTile);
        return allTiles.stream().filter(Tile::isOrphan).distinct().count()>=9;
    }

    /**
     * 手牌が和了形であるかどうか検査します。
     *
     * <p>面子手(四面子一雀頭)のほかに、七対子形、国士無双形の場合も検査します。
     * @param handTiles 手牌(自摸牌を含まない、長さが13以下の3N+1のリスト)
     * @param drawnTile 自摸牌
     * @return true  和了形である場合
     *         false 和了形でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isCompleted(List<Tile> handTiles, Tile drawnTile){
        requireValidSize(handTiles);
        if (isCompletedSevenPairs(handTiles, drawnTile)) return true;
        if (isCompletedThirteenOrphans(handTiles, drawnTile)) return true;
        if (!HandTileMetrics.matchCompleted(handTiles, drawnTile)) return false;
        return isCompletedMeldHand(handTiles, drawnTile);
    }

    /**
     * 手牌が国士無双形であるかどうか検査します。
     *
     * <p>手牌が副露によって13枚未満の場合は{@code false}が返されます。
     * @param handTiles 手牌(自摸牌を含まない、長さが13以下の3N+1のリスト)
     * @param drawnTile 自摸牌
     * @return true  国士無双形である場合
     *         false 国士無双形でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isCompletedThirteenOrphans(List<Tile> handTiles, Tile drawnTile){
        requireValidSize(handTiles);
        var tiles = Lists.added(handTiles, drawnTile).stream().distinct().sorted().toList();
        return handTiles.size()==13 && Tiles.orphans().equals(tiles);
    }

    /**
     * 手牌が七対子形であるかどうか検査します。
     *
     * <p>手牌が副露によって13枚未満の場合は{@code false}が返されます。
     * <p>同種牌4枚を含む手牌は検査に適合しません。
     * @param handTiles 手牌(自摸牌を含まない、長さが13以下の3N+1のリスト)
     * @param drawnTile 自摸牌
     * @return true  七対子形である場合
     *         false 七対子形でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isCompletedSevenPairs(List<Tile> handTiles, Tile drawnTile){
        requireValidSize(handTiles);
        return handTiles.size()==13 && Lists.added(handTiles, drawnTile).stream()
                .collect(groupingBy(Tile::tileNumber))
                .values().stream()
                .allMatch(group->group.size()==2);
    }

    /**
     * 手牌と自摸牌が面子手和了形かどうか検査します。
     *
     * <p>この処理では国士無双形および七対子形の和了形かどうかは検査されません。
     * <p>この処理の結果は{@code !arrange(handTiles).isEmpty()}と等価です。
     * @param handTiles 手牌(自摸牌を含まない、長さが13以下の3N+1のリスト)
     * @param drawnTile 自摸牌
     * @return true  面子手和了形である場合
     *         false 面子手和了形でない場合
     */
    private static boolean isCompletedMeldHand(List<Tile> handTiles, Tile drawnTile){
        var allTiles = Lists.added(handTiles, drawnTile);
        return extractHeads(allTiles).stream()
                .anyMatch(headTiles-> arrange(Lists.removedEach(allTiles, headTiles)).isPresent());
    }

    /**
     * 手牌が聴牌かどうか検査します。
     *
     * <p>面子手(四面子一雀頭)のほかに、七対子形、国士無双形の聴牌の場合も検査します。
     * <p>聴牌とは、手牌が完成するための和了牌が存在する状態を指します。
     * 手牌中(副露は含めない)で4枚全て使われている牌は和了牌と認めません。
     * @param handTiles 手牌(自摸牌を含まない、長さが13以下の3N+1のリスト)
     * @return true  聴牌である場合
     *         false 聴牌でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isHandReady(List<Tile> handTiles){
        if (!winningTilesOfThirteenOrphans(handTiles).isEmpty()) return true;
        if (!winningTilesOfSevenPairs(handTiles).isEmpty()) return true;
        if (!HandTileMetrics.matchReady(handTiles)) return false;
        return HandTileMetrics.winningCandidatesOf(handTiles).stream()
                .anyMatch(winningTile -> isCompletedMeldHand(handTiles, winningTile));
    }

    /**
     * 手牌が国士無双形の聴牌かどうか判定します。
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @return true  国士無双形聴牌である場合
     *         false 国士無双形聴牌でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isHandReadyThirteenOrphans(List<Tile> handTiles){
        requireValidSize(handTiles);
        return !winningTilesOfThirteenOrphans(handTiles).isEmpty();
    }

    /**
     * 手牌に対する和了牌のセットを取得します。
     *
     * <p>手牌が聴牌でない場合は空のセットを返します。
     * <p>結果で返されるセットには, 赤ドラ/非赤ドラ牌の両方を含みます。
     * <p>手牌中で既に4枚使用されている牌は和了牌として列挙されません。
     * <pre>
     *     [2 3 7 7 7 8 8 8 9 9] [[5 5 5 5]] ... {@code List<Tile>, List<Meld>}
     *     +-> 1
     *     +-> 5                             ... {@code Set<Tile>}
     * </pre>
     * <pre>
     *     [2 3 7 7 7 8 8 8 9 9] [[4 4 4 4]] ... {@code List<Tile>, List<Meld>}
     *     +-> 1                             ... {@code Set<Tile>}
     * </pre>
     * <pre>
     *     [1 2 7 7 7 8 8 8 9 9] [[3 3 3 3]] ... {@code List<Tile>, List<Meld>}
     *     +->                               ... {@code Set<Tile>}
     * </pre>
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @return 和了牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> winningTilesOf(List<Tile> handTiles){
        var winningTiles = new HashSet<Tile>();
        winningTiles.addAll(winningTilesOfSevenPairs(handTiles));
        winningTiles.addAll(winningTilesOfThirteenOrphans(handTiles));
        if (!HandTileMetrics.matchReady(handTiles)) return winningTiles;
        for (var winningTile:HandTileMetrics.winningCandidatesOf(handTiles)){
            if (isCompletedMeldHand(handTiles, winningTile)){
                winningTiles.add(winningTile);
            }
        }
        return winningTiles;
    }

    /**
     * 手牌が国士無双形となるための和了牌を取得します。
     *
     * <p>手牌が国士無双形聴牌でない場合は空のセットが返されます。
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @return 和了牌のセット(長さ0..1, 13)
     */
    private static Set<Tile> winningTilesOfThirteenOrphans(List<Tile> handTiles){
        if (handTiles.size()!=13) return emptySet();
        return Tiles.orphans().stream()
                .filter(tile->isCompletedThirteenOrphans(handTiles, tile))
                .collect(toSet());
    }

    /**
     * 手牌が七対子形となるための和了牌を取得します。
     *
     * <p>手牌が七対子形聴牌でない場合は空のセットが返されます。
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @return 和了牌のセット(長さ0..1)
     */
    private static Set<Tile> winningTilesOfSevenPairs(List<Tile> handTiles){
        if (handTiles.size()!=13) return emptySet();
        var nonPairTiles = handTiles.stream()
                .filter(tile->handTiles.stream().filter(tile::equalsIgnoreRed).count()==1)
                .toList();
        if (nonPairTiles.size()!=1) return emptySet();
        if (!isCompletedSevenPairs(handTiles, nonPairTiles.get(0))) return emptySet();
        return Set.of(nonPairTiles.get(0));
    }

    /**
     * 手牌と自摸牌から立直宣言可能牌のセットを取得します。
     *
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @param drawnTile 自摸牌
     * @return 立直宣言可能牌
     */
    public static Set<Tile> readyTilesOf(List<Tile> handTiles, Tile drawnTile){
        requireValidSize(handTiles);
        var allTiles = Lists.added(handTiles, drawnTile);
        return allTiles.stream()
                .distinct()
                .filter(tile->isHandReady(Lists.removed(allTiles, tile)))
                .collect(toSet());
    }

    /**
     * 手牌と和了牌を並べ替え, 雀頭と面子構成牌のリストに変換します。
     *
     * <p>与えられた牌のリストから1つの雀頭と0～4つの刻子または順子構成牌を抽出し,
     * 先頭の要素に雀頭構成牌を, それ以降の要素に面子構成牌を持つリストを取得します。
     * この並べ替えパターンは複数存在する可能性があるため, 結果はこれらリストのセットとして返されます。
     * <p>手牌の長さは1以上の3の倍数+1である必要があります。
     * 並べ替えの結果, 刻子構成牌にも順子構成牌にも解釈ができない牌が余った場合,
     * 並べ替え不可として, 空のセットが返されます。
     * <pre>
     *     [2 2 3 3 4 4 5 5 6 6 7 7 8] [8]         ... {@code List<Tile>, Tile}
     *     +-> [[2 2][3 4 5][3 4 5][6 7 8][6 7 8]]
     *     +-> [[5 5][2 3 4][2 3 4][6 7 8][6 7 8]]
     *     +-> [[8 8][2 3 4][2 3 4][5 6 7][5 6 7]] ... {@code Set<List<List<Tile>>>}
     * </pre>
     * <pre>
     *     [2] [2]     ... {@code List<Tile>, Tile}
     *     +-> [[2 2]] ... {@code Set<List<List<Tile>>>}
     * </pre>
     * <pre>
     *     [2 2 2 3 3 3 4 4 4 5 5 5 6] [9] ... {@code List<Tile>, Tile}
     *                                     ... {@code Set<List<List<Tile>>>}
     * </pre>
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @param winningTile 和了牌
     * @return 面子のリストのセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<List<Tile>>> arrangeAll(List<Tile> handTiles, Tile winningTile){
        requireValidSize(handTiles);
        var allTiles = Lists.added(handTiles, winningTile);
        var hands = new HashSet<List<List<Tile>>>();
        for (var headTiles:extractHeads(allTiles)){
            var body = Lists.removedEach(allTiles, headTiles);
            for (var melds: arrangeAll(body)){
                var arrangedHand = new ArrayList<List<Tile>>(melds.size() + 1);
                arrangedHand.add(headTiles);
                arrangedHand.addAll(melds);
                hands.add(arrangedHand);
            }
        }
        return hands;
    }

    private static Set<List<Tile>> extractHeads(List<Tile> tiles){
        return tiles.stream()
                .collect(groupingBy(Tile::tileNumber))
                .values().stream()
                .filter(group->group.size()>=2)
                .map(group->group.subList(0, 2))
                .collect(toSet());
    }

    private static Set<List<List<Tile>>> arrangeAll(List<Tile> bodyTiles){
        var meldHands = arrange(bodyTiles);
        return meldHands.map(HandTiles::rearrangeAll).orElse(Collections.emptySet());
    }

    private static Optional<List<List<Tile>>> arrange(List<Tile> bodyTiles){
        var checkingTiles = new ArrayList<>(bodyTiles);
        checkingTiles.sort(Comparator.naturalOrder());
        var meldTiles = new ArrayList<List<Tile>>(bodyTiles.size()/3);
        while(checkingTiles.size()>0){
            assert checkingTiles.size()%3==0;
            var meld = new ArrayList<Tile>(3);
            if (checkingTiles.get(0).equalsIgnoreRed(checkingTiles.get(2))){
                // 手牌から刻子構成牌を引き抜きます
                meld.add(checkingTiles.remove(2));
                meld.add(checkingTiles.remove(1));
                meld.add(checkingTiles.remove(0));
            }else{
                try {
                    // 手牌から順子構成牌を引き抜きます
                    meld.add(checkingTiles.remove(0));
                    meld.add(checkingTiles.remove(Lists.indexOf(checkingTiles, tile -> tile.isNextOf(meld.get(0)))));
                    meld.add(checkingTiles.remove(Lists.indexOf(checkingTiles, tile -> tile.isNextOf(meld.get(1)))));
                }catch (IndexOutOfBoundsException e){
                    // 順子構成牌が手牌に存在しない場合
                    // リストの削除操作でIndexOutOfBoundsException例外がスローされます。
                    // この場合, 指定した牌のリストでは面子への並び替えは不可能として
                    // 空のOptionalを返却します。
                    return Optional.empty();
                }
            }
            meldTiles.add(meld);
        }
        meldTiles.sort(meldComparator());
        return Optional.of(meldTiles);
    }

    private static Set<List<List<Tile>>> rearrangeAll(List<List<Tile>> melds){
        if (melds.size()>=3){
            var meldHands = new HashSet<List<List<Tile>>>();
            meldHands.add(melds);
            combinations: for (var threeMelds:Lists.combinationsOf(melds, 3)){
                if (threeMelds.stream().allMatch(Tiles::isTriple)){
                    var otherMelds = Lists.removedEach(melds, threeMelds);
                    var arrangedMeldHand = new ArrayList<List<Tile>>();
                    for (int i = 0; i<3; i++){
                        var straight = new ArrayList<Tile>(3);
                        straight.add(threeMelds.get(0).get(i));
                        straight.add(threeMelds.get(1).get(i));
                        straight.add(threeMelds.get(2).get(i));
                        if (!Tiles.isStraight(straight)){
                            continue combinations;
                        }
                        arrangedMeldHand.add(straight);
                    }
                    arrangedMeldHand.addAll(otherMelds);
                    arrangedMeldHand.sort(meldComparator());
                    meldHands.add(arrangedMeldHand);
                }
            }
            return meldHands;
        }
        return Set.of(melds);
    }

    private static Comparator<List<Tile>> meldComparator(){
        return (o1, o2) -> {
            if(o1.size()!=o2.size()){
                return o1.size() - o2.size();
            }
            for(int i = 0; i<o1.size(); i++){
                var t1 = o1.get(i);
                var t2 = o2.get(i);
                int comparison = t1.compareTo(t2);
                if(comparison!=0) return comparison;
            }
            return 0;
        };
    }

    /**
     * 手牌に対する立直後カン可能牌のセットを取得します。
     *
     * <p>立直後はたとえ4枚重複のある牌が揃ったとしても,
     * カンによって面子構成に変化が起こる場合はカン不可となります。
     * つまり成立し得る和了形の全てにおいて刻子を形成する牌のみがカン可能となります。
     * <p>例えば次の手[1 1 1 3 4 4 4 8 8 8 9 9 9]は,
     * [1][4][8][9]はいずれも手中に3枚の重複があり, 残り1枚をツモした場合は
     * 槓構成牌を揃えることになりますが, このうち[1]および[4]は和了牌によっては雀頭
     * と解釈可能なパターンが存在するため, カン不可となります。
     * <p>結果で返されるセットには, 赤ドラ/非赤ドラ牌の両方を含みます。
     * <p>この処理は立直宣言時に1度だけ呼び出され, 以降ツモの度にこの結果のセットに対して
     * {@code readyQuadTiles.contains(discardedTile)}を実行することでカン可能かを検査可能です。
     * <p>立直後カン可能牌が存在しない場合は空のセットを返します。
     * <p>結果で返されるセットには, 赤ドラ/非赤ドラ牌の両方を含みます。
     * <pre>
     *     [1 1 1 3 4 4 4 8 8 8 9 9 9] ... {@code List<Tile>}
     *     => [2]: [[1 1][1 2 3][4 4 4][8 8 8][9 9 9]]
     *             [[4 4][1 1 1][2 3 4][8 8 8][9 9 9]]
     *        [3]: [[3 3][1 1 1][4 4 4][8 8 8][9 9 9]]
     *        [5]: [[4 4][1 1 1][3 4 5][8 8 8][9 9 9]]
     *        => [1]: [1 1][1 2 3][1 1 1] => false
     *           [4]: [4 4][2 3 4][4 4 4] => false
     *           [8]: [8 8 8] => true
     *           [9]: [9 9 9] => true
     *     +-> 8
     *     +-> 9 ... {@code Set<Tile>}
     * </pre>
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @return カン可能牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> readyKanTargetsOf(List<Tile> handTiles){
        requireValidSize(handTiles);
        var tripleTiles = handTiles.stream()
                .collect(groupingBy(Tile::tileNumber))
                .values().stream()
                .filter(group->group.size()==3)
                .map(group->group.get(0))
                .collect(toSet());
        if (tripleTiles.isEmpty()) return Collections.emptySet();
        var arrangedHands = HandTileMetrics.winningCandidatesOf(handTiles).stream()
                .flatMap(winningTile->arrangeAll(handTiles, winningTile).stream())
                .toList();
        var triplesAppearedAtLeastOnePattern = arrangedHands.stream()
                .flatMap(List::stream)
                .filter(Tiles::isTriple)
                .collect(toSet());
        var triplesAppearedEveryPattern = triplesAppearedAtLeastOnePattern.stream()
                .filter(triple->arrangedHands.stream().allMatch(hand->hand.contains(triple)))
                .collect(toSet());
        return triplesAppearedEveryPattern.stream()
                .flatMap(List::stream)
                .distinct()
                .flatMap(tile->Tiles.colorTilesOf(tile).stream())
                .collect(toSet());
    }

    /**
     * 手牌と自摸牌から暗槓可能な牌のセットを取得します。
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @param drawnTile 自摸牌
     * @return 暗槓可能牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> selfKanTargetsOf(List<Tile> handTiles, Tile drawnTile){
        requireValidSize(handTiles);
        var allTiles = Lists.added(handTiles, drawnTile);
        return allTiles.stream()
                .collect(groupingBy(Tile::tileNumber))
                .values().stream()
                .filter(group->group.size()==4)
                .flatMap(List::stream)
                .collect(toSet());
    }

    /**
     * 手牌と自摸牌から加槓可能な牌のセットを取得します。
     *
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @param drawnTile 自摸牌
     * @param melds 副露面子
     * @return 加槓可能牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> addKanTargetsOf(List<Tile> handTiles, Tile drawnTile, List<List<Tile>> melds){
        requireValidSize(handTiles);
        var allTiles = Lists.added(handTiles, drawnTile);
        var tripleTiles = melds.stream()
                .filter(Tiles::isTriple)
                .map(triple->triple.get(0))
                .toList();
        return allTiles.stream()
                .filter(tile->tripleTiles.stream().anyMatch(tile::equalsIgnoreRed))
                .collect(toSet());
    }

    /**
     * 手牌から打牌に対して可能なカンの構成牌のセットを返します。
     *
     * <p>可能なカン構成牌が存在しない場合は空のセットが返されます。
     * カン構成牌のセットは1つの打牌に対して1つのみしか存在しないため,
     * カン構成牌が存在する場合, 結果で返されるセットのサイズは1となります。
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @param discardedTile 打牌
     * @return カン構成牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<Tile>> kanBasesOf(List<Tile> handTiles, Tile discardedTile){
        requireValidSize(handTiles);
        var targetTiles = handTiles.stream()
                .filter(discardedTile::equalsIgnoreRed)
                .sorted()
                .toList();
        if(targetTiles.size()!=3) return emptySet();
        return Set.of(targetTiles);
    }

    /**
     * 手牌から打牌に対して可能なポンの構成牌のセットを返します。
     *
     * <p>可能なポン構成牌が存在しない場合は空のセットが返されます。
     * ポン構成牌のセットは1つの打牌に対して基本的には1つのみしか存在しませんが,
     * 例外として赤ドラを含む3枚を持つ以下のような場合, 結果セットは
     * 赤ドラを含むセット, 赤ドラを含まないセットの2パターンが発生します。
     * <pre>
     *     [5 5R 6 7] [5] ... {@code List<Tile>, Tile}
     *     +-> [5 5R]
     *     +-> [5 5]      ... {@code Set<List<Tile>>}
     * </pre>
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @param discardedTile 打牌
     * @return ポン構成牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<Tile>> ponBasesOf(List<Tile> handTiles, Tile discardedTile){
        requireValidSize(handTiles);
        var targetTiles = handTiles.stream()
                .filter(discardedTile::equalsIgnoreRed)
                .sorted()
                .toList();
        if(targetTiles.size()<2) return emptySet();
        if(targetTiles.size()==2) return Set.of(targetTiles);
        return Set.copyOf(Lists.combinationsOf(targetTiles, 2));
    }

    /**
     * 手牌から打牌に対して可能なチーの構成牌のセットを返します。
     *
     * <p>結果で返されるセットには, 赤ドラ/非赤ドラ牌の両方を含みます。
     * <p>チーをした後の手牌が全て喰い替え牌となり、チョンボとなる場合は、
     * チー不可能として、この結果のセットから除かれます。
     * 例えば, 手牌[4 5 6 6]の時, 打牌[3]に対する[4 5]チーは,
     * その後の手牌が[6 6]となり喰い替え牌しか残らないため不可となります。
     * <p>手牌にチー可能な搭子が存在しない場合は空のセットを返します。
     * <pre>
     *     [2 2 3 4 5 5R 6] [4] ... {@code List<Tile>, Tile}
     *     +-> [2 3]
     *     +-> [3 5]
     *     +-> [3 5R]
     *     +-> [5 6]
     *     +-> [5R 6]           ... {@code Set<List<Tile>>}
     * </pre>
     * <pre>
     *     [4 5 6 7] [3] ... {@code List<Tile>, Tile}
     *     +-> [4 5]     ... {@code Set<List<Tile>>}
     * </pre>
     * <pre>
     *     [4 5 6 6] [3] ... {@code List<Tile>, Tile}
     *                   ... {@code Set<List<Tile>>}
     * </pre>
     * @param handTiles 手牌(自摸牌を含まない、長さ3n+1(n=0..4))
     * @param discardedTile 打牌
     * @return チー構成牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<Tile>> chiBasesOf(List<Tile> handTiles, Tile discardedTile){
        requireValidSize(handTiles);
        return chiBasesOf(discardedTile).stream()
                .flatMap(base->colorBasesOf(base).stream())
                .filter(base->Lists.containsEach(handTiles, base))
                .filter(base->!waitingTargetsOf(base) //全手牌喰い替え牌の事前防止
                        .containsAll(Lists.removedEach(handTiles, base)))
                .collect(toSet());
    }


    private static Set<List<Tile>> colorBasesOf(List<Tile> base){
        var expandedBases = new HashSet<List<Tile>>();
        for (var first:Tiles.colorTilesOf(base.get(0))){
            for (var second:Tiles.colorTilesOf(base.get(1))){
                expandedBases.add(List.of(first, second));
            }
        }
        return expandedBases;
    }

    private static Set<List<Tile>> chiBasesOf(Tile tile){
        if (tile.isHonor()) return Collections.emptySet();
        var bases = new HashSet<List<Tile>>();
        if (tile.hasNext()){
            var second = tile.next();
            if (second.hasNext()){
                var third = second.next();
                bases.add(List.of(second, third));
            }
        }
        if (tile.hasPrevious()){
            var second = tile.previous();
            if (second.hasPrevious()){
                var first = second.previous();
                bases.add(List.of(first, second));
            }
        }
        if (tile.hasPrevious() && tile.hasNext()){
            var first = tile.previous();
            var third = tile.next();
            bases.add(List.of(first, third));
        }
        return bases;
    }

    /**
     * 搭子に対する待ち牌のセットを取得します。
     *
     * <p>結果で返されるセットには, 赤ドラ/非赤ドラ牌の両方を含みます。
     * <pre>
     *     [3 4]    ... {@code List<Tile>}
     *     +-> 2
     *     +-> 5
     *     +-> 5R ... {@code Set<Tile>}
     * </pre>
     * <pre>
     *     [1 1]   ... {@code List<Tile>}
     *     +-> 1 ... {@code Set<Tile>}
     * </pre>
     * <pre>
     *     [1 2]   ... {@code List<Tile>}
     *     +-> 3 ... {@code Set<Tile>}
     * </pre>
     * <pre>
     *     [1 3]   ... {@code List<Tile>}
     *     +-> 2 ... {@code Set<Tile>}
     * </pre>
     * @param base 搭子(長さ2)
     * @return 待ち牌のセット
     * @throws IllegalArgumentException 搭子構成牌が不正の場合
     */
    public static Set<Tile> waitingTargetsOf(List<Tile> base){
        if (base.size()!=2){
            throw new IllegalArgumentException("invalid size of base: "+base);
        }
        var sorted = base.stream().sorted().toList();
        var lower = sorted.get(0);
        var upper = sorted.get(1);
        //対子
        if (upper.equalsIgnoreRed(lower)){
            return Set.copyOf(Tiles.colorTilesOf(upper));
        }
        //両面塔子 辺張塔子
        if (upper.isNextOf(lower)){
            var waitingTiles = new HashSet<Tile>(2);
            if (lower.hasPrevious()) waitingTiles.addAll(Tiles.colorTilesOf(lower.previous()));
            if (upper.hasNext()) waitingTiles.addAll(Tiles.colorTilesOf(upper.next()));
            return waitingTiles;
        }
        //嵌張塔子
        if (upper.hasPrevious() && lower.hasNext()){
            var middleTile = upper.previous();
            if (middleTile.equalsIgnoreRed(lower.next())){
                return Set.copyOf(Tiles.colorTilesOf(middleTile));
            }
        }
        return Set.of();
    }

}
