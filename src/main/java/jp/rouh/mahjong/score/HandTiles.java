package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;
import jp.rouh.util.FlexList;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.*;

/**
 * 手牌に関する操作を扱うユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
public final class HandTiles{
    private HandTiles(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 純手牌の長さが正しいかどうか検査し, 不正であれば例外をスローします。
     *
     * <p>自摸牌を独立した1枚と考えた場合, 門前の手牌(純手牌)は常に13枚であることが成立します。
     * 副露した場合は, 基本的に純手牌の枚数は13 - 3*副露した数となります。
     * 例外的にポンもしくはチー副露直後の打牌前のみ手牌の枚数は1枚多い状態になります。
     * <p>このバリデーション処理では, 手牌が基本的な状態であることを検査し,
     * 後続の処理に例外的な値が渡らないことを保証します。
     * @param handTiles 純手牌
     * @throws IllegalArgumentException 純手牌の長さが不正の場合
     */
    private static void validateHandTiles(List<Tile> handTiles){
        if(handTiles.isEmpty() || handTiles.size()%3!=1 || handTiles.size()>13){
            throw new IllegalArgumentException("illegal size of hand tiles");
        }
    }

    /**
     * 与えられた牌のセットに, 対応する赤ドラ/非赤ドラ牌を追加します。
     * @param set 牌のセット
     * @return 赤ドラ拡張済みセット
     */
    private static Set<Tile> expand(Set<Tile> set){
        return set.stream().map(Tiles::sameValuesOf).flatMap(List::stream).collect(toSet());
    }

    /**
     * 純手牌と追加牌が和了形かどうか検査します。
     *
     * <p>この処理の結果は{@code winningTilesOf(handTiles).contains(winningTile)}と等価です。
     * @param handTiles   純手牌
     * @param winningTile 追加牌
     * @return true  和了形の場合
     *         false 和了形でない場合
     */
    public static boolean isCompleted(List<Tile> handTiles, Tile winningTile){
        validateHandTiles(handTiles);
        if(isThirteenOrphans(handTiles, winningTile)) return true;
        if(isSevenPairs(handTiles, winningTile)) return true;
        if(!HandSections.matchCompleted(handTiles, winningTile)) return false;
        return isMeldHandCompleted(handTiles, winningTile);
    }

    /**
     * 純手牌と追加牌が国士無双形かどうか検査します。
     * @param handTiles   純手牌
     * @param winningTile 追加牌
     * @return true  国士無双形である場合
     *         false 国士無双形でない場合
     *         手牌の長さが不正の場合
     */
    public static boolean isThirteenOrphans(List<Tile> handTiles, Tile winningTile){
        var completedHandTiles = FlexList.copyOf(handTiles).added(winningTile);
        return handTiles.size()==13 && completedHandTiles.stream().allMatch(Tile::isOrphan)
                && completedHandTiles.stream().distinct().count()==13;
    }

    /**
     * 純手牌と追加牌が七対子形かどうか検査します。
     *
     * <p>同種牌4枚を含む手牌は検査に適合しません。
     * @param handTiles   純手牌
     * @param winningTile 追加牌
     * @return true  七対子形である場合
     *         false 七対子形でない場合
     *         手牌の長さが不正の場合
     */
    public static boolean isSevenPairs(List<Tile> handTiles, Tile winningTile){
        return handTiles.size()==13 && FlexList.copyOf(handTiles).added(winningTile)
                .stream().collect(groupingBy(Tile::tileNumber)).values().stream()
                .allMatch(group -> group.size()==2);
    }

    /**
     * 純手牌と自摸牌から立直宣言可能牌のセットを取得します。
     * @param handTiles 純手牌
     * @param drawnTile 自摸牌
     * @param openMelds 副露(暗槓を含む)
     * @return 立直宣言可能牌
     * @see #isHandReady(List, List)
     */
    public static Set<Tile> readyTilesOf(List<Tile> handTiles, Tile drawnTile, List<Meld> openMelds){
        validateHandTiles(handTiles);
        var completedHandTiles = new FlexList<>(handTiles).added(drawnTile).sorted();
        var readyTiles = new HashSet<Tile>();
        for(var readyTile: Set.copyOf(completedHandTiles)){
            var readyHandTiles = new FlexList<>(completedHandTiles).removed(readyTile);
            if(isHandReady(readyHandTiles, openMelds)){
                readyTiles.add(readyTile);
            }
        }
        return readyTiles;
    }

    /**
     * 純手牌が聴牌かどうか検査します。
     *
     * <p>この処理では面子手のみならず国士無双及び七対子の可能性も検査します。
     * <p>この処理の結果は{@code !winningTilesOf(handTiles).isEmpty()}と等価です。
     * <p>聴牌とは, 手牌が完成するための和了牌が存在する状態を指します。
     * 手牌で既に4枚使用されている牌は和了牌にはなりません。
     * @param handTiles 純手牌(長さ3n+1(n=0..4))
     * @param openMelds 副露(暗槓を含む)
     * @return true  聴牌である場合
     *         false 聴牌でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     * @see #winningTilesOf(List, List)
     */
    public static boolean isHandReady(List<Tile> handTiles, List<Meld> openMelds){
        validateHandTiles(handTiles);
        if(!thirteenOrphansWinningTilesOf(handTiles).isEmpty()) return true;
        if(!sevenPairsWinningTilesOf(handTiles).isEmpty()) return true;
        if(!HandSections.matchReady(handTiles)) return false;
        var otherTiles = openMelds.stream()
                .map(Meld::getTilesSorted)
                .flatMap(List::stream)
                .collect(toList());
        for(var winningTile: HandSections.winningTileCandidatesOf(handTiles, otherTiles)){
            if(isMeldHandCompleted(handTiles, winningTile)){
                return true;
            }
        }
        return false;
    }

    /**
     * 手牌が国士無双形の聴牌かどうか判定します。
     * @param handTiles 手牌(長さ3n+1(n=0..4))
     * @return true  国士無双形聴牌である場合
     *         false 国士無双形聴牌でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isThirteenOrphansHandReady(List<Tile> handTiles){
        validateHandTiles(handTiles);
        return !thirteenOrphansWinningTilesOf(handTiles).isEmpty();
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
     * @param handTiles 純手牌(長さ3n+1(n=0..4))
     * @param openMelds 副露(暗槓を含む)
     * @return 和了牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> winningTilesOf(List<Tile> handTiles, List<Meld> openMelds){
        validateHandTiles(handTiles);
        var winningTiles = new HashSet<Tile>();
        winningTiles.addAll(sevenPairsWinningTilesOf(handTiles));
        winningTiles.addAll(thirteenOrphansWinningTilesOf(handTiles));
        if(!HandSections.matchReady(handTiles)) return winningTiles;
        var otherTiles = openMelds.stream()
                .map(Meld::getTilesSorted)
                .flatMap(List::stream)
                .collect(toList());
        for(var winningTile: HandSections.winningTileCandidatesOf(handTiles, otherTiles)){
            if(isMeldHandCompleted(handTiles, winningTile)){
                winningTiles.add(winningTile);
            }
        }
        return expand(winningTiles);
    }

    /**
     * 手牌が国士無双形となるための和了牌を取得します。
     *
     * <p>手牌が国士無双形聴牌でない場合は空のセットが返されます。
     * @param handTiles 手牌
     * @return 和了牌のセット(長さ0..1, 13)
     */
    private static Set<Tile> thirteenOrphansWinningTilesOf(List<Tile> handTiles){
        if(handTiles.size()!=13) return emptySet();
        if(!handTiles.stream().allMatch(Tile::isOrphan)) return emptySet();
        var required = FlexList.copyOf(Tiles.orphans()).removedEach(handTiles);
        if(required.isEmpty()) return Set.copyOf(Tiles.orphans());
        if(required.size()==1) return Set.of(required.get(0));
        return emptySet();
    }

    /**
     * 手牌が七対子形となるための和了牌を取得します。
     *
     * <p>手牌が七対子形聴牌でない場合は空のセットが返されます。
     * @param handTiles 手牌
     * @return 和了牌のセット(長さ0..1)
     */
    private static Set<Tile> sevenPairsWinningTilesOf(List<Tile> handTiles){
        if(handTiles.size()!=13) return emptySet();
        var nonPairTiles = handTiles.stream().collect(groupingBy(Tile::tileNumber)).values()
                .stream().filter(group -> group.size()!=2).flatMap(List::stream).collect(toList());
        if(nonPairTiles.size()==1) return Set.copyOf(nonPairTiles);
        return emptySet();
    }

    /**
     * 手牌と追加牌が面子手和了形かどうか検査します。
     *
     * <p>この処理では国士無双形および七対子形の和了形かどうかは検査されません。
     * <p>この処理の結果は{@code !arrange(handTiles).isEmpty()}と等価です。
     * @param handTiles   手牌
     * @param winningTile 追加牌
     * @return true  面子手和了形である場合
     *         false 面子手和了形でない場合
     */
    private static boolean isMeldHandCompleted(List<Tile> handTiles, Tile winningTile){
        var completedHandTiles = new FlexList<>(handTiles).added(winningTile);
        for(var headTiles: extractPairs(completedHandTiles)){
            var tail = new FlexList<>(completedHandTiles).removedEach(headTiles);
            if(!arrange(tail).isEmpty()) return true;
        }
        return false;
    }

    /**
     * 手牌と追加牌を並べ替え, 雀頭と面子構成牌のリストに変換します。
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
     * @param handTiles   手牌(長さ3n+1(n=0..4))
     * @param winningTile 和了牌
     * @return 面子のリストのセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<List<Tile>>> arrange(List<Tile> handTiles, Tile winningTile){
        validateHandTiles(handTiles);
        var hands = new HashSet<List<List<Tile>>>();
        var completedHandTiles = new FlexList<>(handTiles).added(winningTile);
        for(var headTiles: extractPairs(completedHandTiles)){
            var tail = new FlexList<>(completedHandTiles).removedEach(headTiles);
            for(var melds: arrange(tail)){
                var hand = new ArrayList<List<Tile>>();
                hand.add(headTiles);
                hand.addAll(melds);
                hands.add(hand);
            }
        }
        return hands;
    }

    /**
     * 牌のリストを並べ替え, 面子構成牌のリストに変換します。
     *
     * <p>与えられた牌のリストから刻子または順子構成牌を抽出し, 面子構成牌のリストを取得します。
     * この並べ替えパターンは複数存在する可能性があるため, 結果はセットとして返されます。
     * <p>牌のリストの長さは0以上の3の倍数である必要があります。
     * 牌のリストの長さが0の場合は, 長さ0の面子構成牌のリストの単一のセットが返されます。
     * 並べ替えの結果, 刻子構成牌にも順子構成牌にも解釈ができない牌が余った場合,
     * 並べ替え不可として, 空のセットが返されます。
     * <pre>
     *     [1 1 1 2 2 2 3 3 3]         ... {@code List<Tile>}
     *     +-> [[1 2 3][1 2 3][1 2 3]]
     *     +-> [[1 1 1][2 2 2][3 3 3]] ... {@code Set<List<List<Tile>>>}
     * </pre>
     * <pre>
     *     []       ... {@code List<Tile>}
     *     +-> [[]] ... {@code Set<List<List<Tile>>>}
     * </pre>
     * <pre>
     *     [1 1 1 2 2 2 3 3 4] ... {@code List<Tile>}
     *                         ... {@code Set<List<List<Tile>>>}
     * </pre>
     * @param tiles 牌のリスト
     * @return 面子のリストのセット
     */
    private static Set<List<List<Tile>>> arrange(List<Tile> tiles){
        var patterns = new HashSet<List<List<Tile>>>();
        var tail = new FlexList<>(tiles).sorted();
        var melds = new FlexList<List<Tile>>(tail.size()/3);
        while(tail.size()>0){
            assert tail.size()%3==0;
            var meld = new ArrayList<Tile>(3);
            if(tail.get(0).equalsIgnoreRed(tail.get(2))){
                // 手牌から刻子構成牌を引き抜きます
                meld.add(tail.remove(2));
                meld.add(tail.remove(1));
                meld.add(tail.remove(0));
            }else{
                try{
                    // 手牌から順子構成牌を引き抜きます
                    meld.add(tail.remove(0));
                    meld.add(tail.remove(tail.indexOf(tile -> tile.isNextOf(meld.get(0)))));
                    meld.add(tail.remove(tail.indexOf(tile -> tile.isNextOf(meld.get(1)))));
                }catch(IndexOutOfBoundsException ignored){
                    // 順子構成牌が手牌に存在しない場合
                    // リストの削除操作でIndexOutOfBoundsException例外がスローされます。
                    // この場合, 指定した牌のリストでは面子への並び替えは不可能として
                    // 空のセットを返却します。
                    return emptySet();
                }
            }
            melds.add(meld);
        }
        melds.sort(meldsPatternComparator());
        patterns.add(melds);
        if(melds.size()>=3){
            // 三連刻形であれば三盃口形へ変換したパターンを追加します
            // [1 1 1][2 2 2][3 3 3] => [1 2 3][1 2 3][1 2 3]
            combination:
            for(var triples: melds.combinationSizeOf(3)){
                if(triples.stream().anyMatch(not(Tiles::isTriple))) continue;
                var theOthers = new FlexList<>(melds).removedEach(triples);
                var straights = new ArrayList<List<Tile>>();
                for(int i = 0; i<3; i++){
                    var straight = new ArrayList<Tile>(3);
                    straight.add(triples.get(0).get(i));
                    straight.add(triples.get(1).get(i));
                    straight.add(triples.get(2).get(i));
                    if(!Tiles.isSequence(straight)) continue combination;
                    straights.add(straight);
                }
                straights.addAll(theOthers);
                straights.sort(meldsPatternComparator());
                patterns.add(straights);
            }
        }
        return patterns;
    }

    private static Comparator<List<Tile>> meldsPatternComparator(){
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
     * 牌のリストから対子を抽出します。
     *
     * <p>与えられた牌のリストに2枚以上重複のある牌がある場合,
     * うち2枚をリストとして結果のセットに格納します。
     * <p>赤ドラ牌は通常の牌と同様に扱われ, 同種の牌の組み合わせに
     * 赤ドラ牌を含む2枚とそうでない2枚の二種類がある場合は,
     * どちらか一つが結果のセットに格納されます。
     * <p>牌のリスト中に1つも2枚以上重複のある牌がない場合は空のセットが返されます。
     * <pre>
     *     [1 2 3 4 4 4 5 5 5 5R] ... {@code List<Tile>}
     *     +-> [4 4]
     *     +-> [5 5]              ... {@code Set<List<Tile>>}
     * </pre>
     * <pre>
     *     [1 2 3 4 5 6 7 8 9] ... {@code List<Tile>}
     *                         ... {@code Set<List<Tile>>}
     * </pre>
     * @param tiles 牌のリスト
     * @return 対子のセット
     */
    private static Set<List<Tile>> extractPairs(List<Tile> tiles){
        return tiles.stream()
                .collect(groupingBy(Tile::tileNumber))
                .values().stream()
                .filter(duplicated -> duplicated.size()>=2)
                .map(duplicated -> duplicated.subList(0, 2))
                .collect(toSet());
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
     * @param handTiles 手牌(長さ3n+1(n=0..4))
     * @return カン可能牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> readyQuadTilesOf(List<Tile> handTiles){
        validateHandTiles(handTiles);
        var tripleTiles = handTiles.stream()
                .collect(groupingBy(Tile::tileNumber)).values().stream()
                .filter(duplicated -> duplicated.size()>=3)
                .map(triple -> triple.get(0))
                .collect(toSet());
        if(tripleTiles.isEmpty()) return emptySet();
        var patterns = HandSections.winningTileCandidatesOf(handTiles).stream()
                .map(winningTile -> arrange(handTiles, winningTile)).flatMap(Set::stream).collect(toSet());
        var readyQuadTiles = tripleTiles.stream().filter(tripleTile ->
                        patterns.stream().allMatch(melds ->
                                melds.stream()
                                        .filter(Tiles::isTriple)
                                        .map(triple -> triple.get(0))
                                        .anyMatch(tripleTile::equalsIgnoreRed)))
                .collect(toSet());
        return expand(readyQuadTiles);
    }

    /**
     * 手牌と自摸牌から暗槓可能な牌のセットを取得します。
     * @param handTiles 手牌(長さ3n+1(n=0..4))
     * @param drawnTile 自摸牌
     * @return 暗槓可能牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> selfQuadTilesOf(List<Tile> handTiles, Tile drawnTile){
        validateHandTiles(handTiles);
        var addedHandTiles = new FlexList<>(handTiles).added(drawnTile);
        return addedHandTiles.stream().distinct()
                .filter(tile -> addedHandTiles.countIf(tile::equalsIgnoreRed)==4)
                .collect(toSet());
    }

    /**
     * 手牌と自摸牌から加槓可能な牌のセットを取得します。
     * @param handTiles 手牌(長さ3n+1(n=0..4))
     * @param drawnTile 自摸牌
     * @param openMelds 副露面子
     * @return 加槓可能牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<Tile> addQuadTilesOf(List<Tile> handTiles, Tile drawnTile, List<Meld> openMelds){
        validateHandTiles(handTiles);
        var completedHandTiles = new FlexList<>(handTiles).added(drawnTile);
        var tripleTiles = openMelds.stream()
                .filter(Meld::isTriple)
                .map(Meld::getFirst)
                .collect(toList());
        return completedHandTiles.stream()
                .filter(tile -> tripleTiles.stream().anyMatch(tile::equalsIgnoreRed))
                .collect(toSet());
    }

    /**
     * 手牌から打牌に対して可能なカンの構成牌のセットを返します。
     *
     * <p>可能なカン構成牌が存在しない場合は空のセットが返されます。
     * カン構成牌のセットは1つの打牌に対して1つのみしか存在しないため,
     * カン構成牌が存在する場合, 結果で返されるセットのサイズは1となります。
     * @param handTiles     手牌(長さ3n+1(n=0..4))
     * @param discardedTile 打牌
     * @return カン構成牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<Tile>> quadBasesOf(List<Tile> handTiles, Tile discardedTile){
        validateHandTiles(handTiles);
        var target = handTiles.stream().filter(discardedTile::equalsIgnoreRed).collect(toList());
        if(target.size()!=3) return emptySet();
        return Set.of(target);
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
     * @param handTiles     手牌(長さ3n+1(n=0..4))
     * @param discardedTile 打牌
     * @return ポン構成牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<Tile>> tripleBasesOf(List<Tile> handTiles, Tile discardedTile){
        validateHandTiles(handTiles);
        var target = handTiles.stream().filter(discardedTile::equalsIgnoreRed)
                .sorted().collect(toCollection(FlexList::new));
        if(target.size()<2) return emptySet();
        if(target.size()==2) return Set.of(target);
        return Set.copyOf(target.combinationSizeOf(2));
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
     * @param handTiles     手牌(長さ3n+1(n=0..4))
     * @param discardedTile 打牌
     * @return チー構成牌のセット
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static Set<List<Tile>> sequenceBasesOf(List<Tile> handTiles, Tile discardedTile){
        validateHandTiles(handTiles);
        var tilesOperable = FlexList.copyOf(handTiles);
        return sequenceBasesOf(discardedTile).stream()
                .filter(tilesOperable::containsWhole)
                .filter(base -> {
                    //全手牌喰い替え牌の事前防止
                    var calledHandTiles = new FlexList<>(handTiles).removedEach(base);
                    return !waitingTilesOf(base).containsAll(calledHandTiles);
                })
                .collect(toSet());
    }

    /**
     * 打牌に対するチー可能な搭子のセットを返します。
     *
     * <p>結果で返されるセットには, 赤ドラ/非赤ドラ牌の両方を含みます。
     * <pre>
     *     [3]        ... {@code Tile}
     *     +-> [1 2]
     *     +-> [2 4]
     *     +-> [4 5]
     *     +-> [4 5R] ... {@code Set<List<Tile>>}
     * </pre>
     * <pre>
     *     [W] ... {@code Tile}
     *         ... {@code Set<List<Tile>>}
     * </pre>
     * @param tile 打牌
     * @return 塔子のセット
     */
    private static Set<List<Tile>> sequenceBasesOf(Tile tile){
        if(tile.isHonor()) return emptySet();
        var bases = new HashSet<List<Tile>>();
        if(tile.hasNext()){
            var second = tile.next();
            if(second.hasNext()){
                var third = second.next();
                bases.add(List.of(second, third));
                bases.add(List.of(Tiles.toPrisedRedIfExists(second), Tiles.toPrisedRedIfExists(third)));
            }
        }
        if(tile.hasPrevious()){
            var second = tile.previous();
            if(second.hasPrevious()){
                var first = second.previous();
                bases.add(List.of(first, second));
                bases.add(List.of(Tiles.toPrisedRedIfExists(first), Tiles.toPrisedRedIfExists(second)));
            }
        }
        if(tile.hasPrevious() && tile.hasNext()){
            var first = tile.previous();
            var third = tile.next();
            bases.add(List.of(first, third));
            bases.add(List.of(Tiles.toPrisedRedIfExists(first), Tiles.toPrisedRedIfExists(third)));
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
     * @param base 搭子
     * @return 待ち牌のセット
     * @throws IllegalArgumentException 搭子構成牌が不正の場合
     */
    public static Set<Tile> waitingTilesOf(List<Tile> base){
        if(base.size()!=2){
            throw new IllegalArgumentException("invalid base size: " + base);
        }
        var sorted = new FlexList<>(base).sorted();
        if(sorted.get(0).equalsIgnoreRed(sorted.get(1))){
            return expand(Set.of(sorted.get(0)));
        }
        if(sorted.get(1).isNextOf(sorted.get(0))){
            var waitingTiles = new HashSet<Tile>();
            if(sorted.get(0).hasPrevious()) waitingTiles.add(sorted.get(0).previous());
            if(sorted.get(1).hasNext()) waitingTiles.add(sorted.get(1).next());
            return expand(waitingTiles);
        }
        if(sorted.get(0).hasNext()){
            var middleTile = sorted.get(0).next();
            if(sorted.get(1).isNextOf(middleTile)){
                return expand(Set.of(middleTile));
            }
        }
        return Set.of();
    }

    /**
     * 手牌と自摸牌が九種九牌形であるか検査します。
     * @param handTiles 手牌(長さ3n+1(n=0..4))
     * @param drawnTile 自摸牌
     * @return true  九種九牌形である場合
     *         false 九種九牌形でない場合
     * @throws IllegalArgumentException 手牌の長さが不正の場合
     */
    public static boolean isNineTiles(List<Tile> handTiles, Tile drawnTile){
        validateHandTiles(handTiles);
        return new FlexList<>(handTiles).added(drawnTile)
                .stream().filter(Tile::isOrphan).distinct().count()>=9;
    }
}
