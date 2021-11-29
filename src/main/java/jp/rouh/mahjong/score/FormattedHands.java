package jp.rouh.mahjong.score;

import jp.rouh.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 整形済み手牌に関するユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
final class FormattedHands{
    private FormattedHands(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 手牌を複数の整形済み手牌にフォーマットします。
     * <p>手牌が七対子もしくは完成形でない場合は空のセットが返されます。
     * @param handTiles   純手牌
     * @param openMelds   公開面子
     * @param winningTile 和了牌
     * @param context     和了状況
     * @return 整形済み手牌のセット
     */
    static Set<FormattedHand> format(List<Tile> handTiles, List<Meld> openMelds, Tile winningTile, WinningContext context){
        var formattedHands = new HashSet<FormattedHand>();
        for(var arranged: HandTiles.arrange(handTiles, winningTile)){
            var head = new Head(arranged.get(0));
            var tail = arranged.subList(1, arranged.size());
            for(int i = 0; i<tail.size(); i++){
                if(tail.get(i).contains(winningTile)){
                    int winningMeldIndex = i;
                    var handMelds = IntStream.range(0, tail.size())
                            .mapToObj(index -> new HandMeld(tail.get(index), winningMeldIndex!=index || context.isTsumo()))
                            .toList();
                    var wait = Wait.of(handMelds.get(winningMeldIndex), winningTile);
                    var melds = new ArrayList<Meld>();
                    melds.addAll(handMelds);
                    melds.addAll(openMelds);
                    formattedHands.add(new FormattedHand(head, melds, wait));
                }
            }
        }
        return formattedHands;
    }

    /**
     * 整形済み手牌の符を計算します。
     * @param hand    整形済み手牌
     * @param context 和了状況
     * @return 符
     */
    static int calculatePointOf(FormattedHand hand, WinningContext context){
        int waitPoint = hand.getWait().getWaitPoint();
        int headPoint = hand.getHead().getHeadPoint(context.getSeatWind(), context.getRoundWind());
        int meldPoint = hand.getMelds().stream().mapToInt(Meld::getMeldPoint).sum();
        int handPoint = waitPoint + headPoint + meldPoint;
        boolean noPoint = context.isSelfMade() && handPoint==0;
        int selfDrawPoint = context.isTsumo() && !noPoint? 2:0;
        int concealedAddition = context.isSelfMade() && !context.isTsumo()? 10:0;
        int noPointAddition = handPoint==0 && !context.isSelfMade()? 10:0;
        int totalPoint = 20 + handPoint + selfDrawPoint + concealedAddition + noPointAddition;
        return (int)Math.ceil(totalPoint/10d)*10;
    }
}
