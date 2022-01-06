package jp.rouh.mahjong.game;

import jp.rouh.mahjong.score.HandTiles;
import jp.rouh.mahjong.score.Meld;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;

import java.util.*;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

class Hand{
    private final List<Tile> handTiles = new ArrayList<>(13);
    private final List<Meld> openMelds = new ArrayList<>(4);
    private Tile drawnTile;

    // cached when tile discarded, disposed when tile discarded
    private Set<Tile> completingTiles;

    // cached when tile claimed, disposed when tile discarded
    private Set<Tile> undiscardableTiles;

    private boolean isInTurn(){
        return (handTiles.size() + (drawnTile!=null?1:0) + openMelds.size()*3)==14;
    }

    private void requireOutOfTurn(){
        if(isInTurn()){
            throw new IllegalStateException("invalid status: not out of turn");
        }
    }

    private void requireInTurn(){
        if(!isInTurn()){
            throw new IllegalStateException("invalid status: not in turn");
        }
    }

    private void remove(Tile tile){
        remove(List.of(tile));
    }

    private void remove(List<Tile> tiles){
        if(drawnTile!=null){
            handTiles.add(drawnTile);
            drawnTile = null;
        }
        for(var tile:tiles){
            if(!handTiles.remove(tile)){
                throw new IllegalArgumentException("tile not found: "+tile);
            }
        }
        handTiles.sort(Comparator.naturalOrder());
    }

    void discard(Tile tile){
        requireInTurn();
        remove(tile);
        completingTiles = HandTiles.winningTilesOf(handTiles, openMelds);
        undiscardableTiles = null;
    }

    void draw(Tile tile){
        requireOutOfTurn();
        drawnTile = tile;
    }

    void distributed(Tile tile){
        handTiles.add(tile);
        if(handTiles.size()==13){
            handTiles.sort(Comparator.naturalOrder());
            completingTiles = HandTiles.winningTilesOf(handTiles, openMelds);
        }
    }

    void distributed(List<Tile> tiles){
        tiles.forEach(this::distributed);
    }

    int makeCallSequence(Tile claimedTile, List<Tile> baseTiles){
        requireOutOfTurn();
        remove(baseTiles);
        openMelds.add(Meld.ofCallSequence(baseTiles, claimedTile));
        undiscardableTiles = HandTiles.waitingTilesOf(baseTiles);
        return openMelds.size() - 1;
    }

    int makeCallTriple(Tile claimedTile, List<Tile> baseTiles, Side source){
        requireOutOfTurn();
        remove(baseTiles);
        openMelds.add(Meld.ofCallTriple(baseTiles, claimedTile, source));
        undiscardableTiles = HandTiles.waitingTilesOf(baseTiles);
        return openMelds.size() - 1;
    }

    int makeCallQuad(Tile claimedTile, Side source){
        requireOutOfTurn();
        var baseTiles = handTiles.stream()
                .filter(claimedTile::equalsIgnoreRed)
                .toList();
        remove(baseTiles);
        openMelds.add(Meld.ofCallQuad(baseTiles, claimedTile, source));
        return openMelds.size() - 1;
    }

    boolean hasAddQuadBaseOf(Tile targetTile){
        return openMelds.stream().anyMatch(meld->meld.getFirst().equalsIgnoreRed(targetTile));
    }

    int makeAddQuad(Tile targetTile){
        requireInTurn();
        var triple = openMelds.stream()
                .filter(meld->meld.getFirst().equalsIgnoreRed(targetTile))
                .findAny()
                .orElseThrow(()->new IllegalArgumentException("no add quad base found: "+targetTile));
        int index = openMelds.indexOf(triple);
        remove(targetTile);
        openMelds.remove(index);
        openMelds.add(index, Meld.ofAddQuad(triple, targetTile));
        return index;
    }

    int makeSelfQuad(Tile targetTile){
        requireInTurn();
        var quadTiles = getAllTiles().stream()
                .filter(targetTile::equalsIgnoreRed)
                .toList();
        remove(quadTiles);
        openMelds.add(Meld.ofSelfQuad(quadTiles));
        return openMelds.size() - 1;
    }

    boolean isNineTilesHand(){
        requireInTurn();
        return HandTiles.isNineTiles(handTiles, drawnTile);
    }

    boolean isThirteenOrphansHandReady(){
        requireOutOfTurn();
        return HandTiles.isThirteenOrphansHandReady(handTiles);
    }

    boolean isCompleted(){
        requireInTurn();
        return completingTiles.contains(drawnTile);
    }

    boolean isCompletedBy(Tile claimableTile){
        requireOutOfTurn();
        return completingTiles.contains(claimableTile);
    }

    boolean isHandReady(){
        requireOutOfTurn();
        return !completingTiles.isEmpty();
    }

    Set<Tile> getReadyTiles(){
        requireInTurn();
        return HandTiles.readyTilesOf(handTiles, drawnTile, openMelds);
    }

    Set<Tile> getDiscardableTiles(){
        requireInTurn();
        if(undiscardableTiles==null){
            return new HashSet<>(getAllTiles());
        }
        return getAllTiles().stream()
                .filter(not(undiscardableTiles::contains))
                .collect(toSet());
    }

    Set<Tile> getQuadTiles(){
        requireInTurn();
        var quadTiles = new HashSet<Tile>();
        quadTiles.addAll(HandTiles.addQuadTilesOf(handTiles, drawnTile, openMelds));
        quadTiles.addAll(HandTiles.selfQuadTilesOf(handTiles, drawnTile));
        return quadTiles;
    }

    Set<Tile> getReadyQuadTiles(){
        requireInTurn();
        return HandTiles.readyQuadTilesOf(handTiles);
    }

    Set<List<Tile>> getQuadBaseOf(Tile claimableTile){
        return HandTiles.quadBasesOf(handTiles, claimableTile);
    }

    Set<List<Tile>> getTripleBasesOf(Tile claimableTile){
        return HandTiles.tripleBasesOf(handTiles, claimableTile);
    }

    Set<List<Tile>> getSequenceBasesOf(Tile claimableTile){
        return HandTiles.sequenceBasesOf(handTiles, claimableTile);
    }

    Tile getDrawnTile(){
        return drawnTile;
    }

    List<Meld> getOpenMelds(){
        return openMelds;
    }

    Meld getLastOpenMeld(){
        return openMelds.get(openMelds.size() - 1);
    }

    List<Tile> getHandTiles(){
        return handTiles;
    }

    List<Tile> getAllTiles(){
        var allTiles = new ArrayList<>(handTiles);
        if(drawnTile!=null) allTiles.add(drawnTile);
        return allTiles;
    }
}
