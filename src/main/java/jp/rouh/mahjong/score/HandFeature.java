package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

/**
 * 手牌特徴量クラス。
 *
 * <p>手牌から役判定に有用な値を計算し保持します。
 * @author Rouh
 * @version 2.0
 */
class HandFeature{
    private final int dragonWhiteCount;
    private final int dragonGreenCount;
    private final int dragonRedCount;
    private final int dragonCount;
    private final int windCount;
    private final int roundWindCount;
    private final int seatWindCount;
    private final int winningTileCount;
    private final int terminalCount;
    private final int honorCount;
    private final int orphanCount;
    private final int greenTileCount;
    private final int largestDuplicationCount;
    private final int tileDistinctCount;
    private final int suitTypeCount;
    private final int prisedTileCount;
    private final int hiddenPrisedTileCount;
    private final int redPrisedTileCount;
    private final int quadCount;
    private final int callCount;

    /**
     * 手牌特徴量のコンストラクタ。
     * @param handTiles 手牌(和了牌を含まない)
     * @param openMelds 公開面子
     * @param winningTile 和了牌
     * @param situation 和了状況
     */
    HandFeature(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, WinningSituation situation){
        var hand14 = new ArrayList<Tile>(14);
        var hand18 = new ArrayList<Tile>(18);
        hand14.addAll(handTiles);
        hand18.addAll(handTiles);
        for(var meld:openMelds){
            hand14.addAll(meld.getTilesTruncated());
            hand18.addAll(meld.getTilesSorted());
        }
        hand14.add(winningTile);
        hand18.add(winningTile);
        var roundWindTile = situation.getRoundWind().toTile();
        var seatWindTile = situation.getSeatWind().toTile();
        int dragonWhiteCount = 0;
        int dragonGreenCount = 0;
        int dragonRedCount = 0;
        int dragonCount = 0;
        int windCount = 0;
        int roundWindCount = 0;
        int seatWindCount = 0;
        int winningTileCount = 0;
        int terminalCount = 0;
        int honorCount = 0;
        int orphanCount = 0;
        int greenTileCount = 0;
        for(var tile: hand14){
            if(tile==Tile.DW) dragonWhiteCount++;
            if(tile==Tile.DG) dragonGreenCount++;
            if(tile==Tile.DR) dragonRedCount++;
            if(tile.isDragon()) dragonCount++;
            if(tile.isWind()) windCount++;
            if(tile.equalsIgnoreRed(roundWindTile)) roundWindCount++;
            if(tile.equalsIgnoreRed(seatWindTile)) seatWindCount++;
            if(tile.equalsIgnoreRed(winningTile)) winningTileCount++;
            if(tile.isTerminal()) terminalCount++;
            if(tile.isHonor()) honorCount++;
            if(tile.isOrphan()) orphanCount++;
            if(tile.isGreen()) greenTileCount++;
        }
        this.dragonWhiteCount = dragonWhiteCount;
        this.dragonGreenCount = dragonGreenCount;
        this.dragonRedCount = dragonRedCount;
        this.dragonCount = dragonCount;
        this.windCount = windCount;
        this.roundWindCount = roundWindCount;
        this.seatWindCount = seatWindCount;
        this.winningTileCount = winningTileCount;
        this.terminalCount = terminalCount;
        this.honorCount = honorCount;
        this.orphanCount = orphanCount;
        this.greenTileCount = greenTileCount;
        this.largestDuplicationCount = hand14.stream().collect(groupingBy(Tile::tileNumber)).values().stream().mapToInt(List::size).max().orElseThrow();
        this.tileDistinctCount = (int)hand18.stream().mapToInt(Tile::tileNumber).distinct().count();
        this.suitTypeCount = hand18.stream().filter(not(Tile::isHonor)).collect(groupingBy(Tile::tileType)).size();
        this.prisedTileCount = situation.getUpperPrisedTiles().stream()
                        .mapToInt(prisedTile->(int)hand18.stream().filter(tile->tile.equalsIgnoreRed(prisedTile)).count()).sum();
        this.hiddenPrisedTileCount = situation.isReady()?situation.getLowerPrisedTiles().stream()
                .mapToInt(prisedTile->(int)hand18.stream().filter(tile->tile.equalsIgnoreRed(prisedTile)).count()).sum():0;
        this.redPrisedTileCount = (int)hand18.stream().filter(Tile::isPrisedRed).count();
        this.quadCount = hand18.size() - 14;
        this.callCount = (int)openMelds.stream().filter(not(Meld::isConcealed)).count();
    }

    /**
     * 手牌14枚中の白の枚数を取得します。
     * <p>白の槓子は白3枚として計上します。
     * @return 白の枚数
     */
    int getDragonWhiteCount(){
        return dragonWhiteCount;
    }

    /**
     * 手牌14枚中の發の枚数を取得します。
     * <p>發の槓子は發3枚として計上します。
     * @return 發の枚数
     */
    int getDragonGreenCount(){
        return dragonGreenCount;
    }

    /**
     * 手牌14枚中の中の枚数を取得します。
     * <p>中の槓子は中3枚として計上します。
     * @return 中の枚数
     */
    int getDragonRedCount(){
        return dragonRedCount;
    }

    /**
     * 手牌14枚中の三元牌の枚数を取得します。
     * <p>三元牌の槓子は三元牌3枚として計上します。
     * @return 三元牌の枚数
     */
    int getDragonCount(){
        return dragonCount;
    }

    /**
     * 手牌14枚中の風牌の枚数を取得します。
     * <p>風牌の槓子は風牌3枚として計上します。
     * @return 風牌の枚数
     */
    int getWindCount(){
        return windCount;
    }

    /**
     * 手牌14枚中の場風牌の枚数を取得します。
     * <p>場風牌の槓子は場風牌3枚として計上します。
     * @return 場風牌の枚数
     */
    int getRoundWindCount(){
        return roundWindCount;
    }

    /**
     * 手牌14枚中の自風牌の枚数を取得します。
     * <p>自風牌の槓子は自風牌3枚として計上します。
     * @return 自風牌の枚数
     */
    int getSeatWindCount(){
        return seatWindCount;
    }

    /**
     * 手牌14枚中の和了牌と同種の牌の枚数を取得します。
     * <p>和了牌1枚は必ず計上されるため, 結果枚数は1以上の値になります。
     * <p>赤ドラ牌と非赤ドラ牌の区別なく検査されます。
     * @return 和了牌と同種の牌の枚数
     */
    int getWinningTileCount(){
        return winningTileCount;
    }

    /**
     * 手牌14枚中の老頭牌の枚数を取得します。
     * <p>老頭牌の槓子は老頭牌3枚として計上します。
     * @return 老頭牌の枚数
     */
    int getTerminalCount(){
        return terminalCount;
    }

    /**
     * 手牌14枚中の字牌の枚数を取得します。
     * <p>字牌の槓子は字牌3枚として計上します。
     * @return 字牌の枚数
     */
    int getHonorCount(){
        return honorCount;
    }

    /**
     * 手牌14枚中の么九牌の枚数を取得します。
     * <p>么九牌の槓子は么九牌3枚として計上します。
     * @return 么九牌の枚数
     */
    int getOrphanCount(){
        return orphanCount;
    }

    /**
     * 手牌14枚中の緑一色構成牌の枚数を取得します。
     * <p>緑一色構成牌の槓子は緑一色構成牌3枚として計上します。
     * @return 緑一色構成牌の枚数
     */
    int getGreenTileCount(){
        return greenTileCount;
    }

    /**
     * 手牌14枚中の重複のある牌のうち, 最も多い重複の枚数を取得します。
     * <p>槓子は3枚の重複として計上されます。
     * 門前手牌中に4枚の重複のある牌がある場合(未槓子)は4枚と計上されます。
     * <p>赤ドラ牌と非赤ドラ牌の区別なく検査されます。
     * @return 最も多い重複の枚数
     */
    int getLargestDuplicationCount(){
        return largestDuplicationCount;
    }

    /**
     * 手牌14枚中に使われた牌の種類の数を取得します。
     * <p>赤ドラ牌と非赤ドラ牌の区別なく検査されます。
     * @return 牌の種類の数
     */
    int getTileDistinctCount(){
        return tileDistinctCount;
    }

    /**
     * 手牌14枚中に使われた数牌の種類(萬子/筒子/索子)の数を取得します。
     * <p>萬子/筒子/索子全てを含む場合は3,
     * 萬子/筒子/索子のうち2種類を含む場合は2,
     * 萬子/筒子/索子のうち1種類を含む場合は1,
     * 字牌のみの場合は0が返されます。
     * @return 数牌の種類の数(0..3)
     */
    int getSuitTypeCount(){
        return suitTypeCount;
    }

    /**
     * 手牌中の表ドラの数を取得します。
     * @return 表ドラの数
     */
    int getPrisedTileCount(){
        return prisedTileCount;
    }

    /**
     * 手牌中の裏ドラの数を取得します。
     * <p>立直していない場合0が返されます。
     * @return 裏ドラの数
     */
    int getHiddenPrisedTileCount(){
        return hiddenPrisedTileCount;
    }

    /**
     * 手牌中の赤ドラの数を取得します。
     * @return 赤ドラの数
     */
    int getRedPrisedTileCount(){
        return redPrisedTileCount;
    }

    /**
     * 手牌の槓子の数を取得します。
     * @return 槓子の数
     */
    int getQuadCount(){
        return quadCount;
    }

    /**
     * 副露(暗槓を含まない)の数を取得します。
     * @return 副露の数
     */
    int getCallCount(){
        return callCount;
    }
}