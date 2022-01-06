package jp.rouh.mahjong.game;

import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Wind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 配山{@link Wall}の実装クラス。
 * @author Rouh
 * @version 1.0
 */
class ArrayWall implements Wall{
    private static final int[] QUAD_TILE_OFFSETS = {134, 135, 132, 133};
    private static final int[] UPPER_INDICATOR_OFFSETS = {130, 128, 126, 124, 122};
    private static final int[] LOWER_INDICATOR_OFFSETS = {131, 129, 127, 125, 123};
    private final List<WallObserver> observers = new ArrayList<>();
    private final Tile[] tiles;
    private final int firstIndex;
    private int drawCount = 0;
    private int quadCount = 0;
    private int revealCount = 0;

    /**
     * 牌山のコンストラクタ。
     * <p>引数で渡される牌のリストを上方向から時計回りの牌山の牌としてそのまま使用します。
     * <p>ランダムな牌山を作成する場合は事前にシャッフルした牌の配列を渡す必要があります。
     * @param tiles 牌の配列(長さ136)
     * @param diceSum 2つのサイコロの目の合計値(2..12)
     * @throws IllegalArgumentException 与えられた牌が136枚でない場合
     */
    ArrayWall(Tile[] tiles, int diceSum){
        if(tiles.length!=136)
            throw new IllegalArgumentException("invalid length of tiles: "+tiles.length);
        this.tiles = tiles.clone();
        this.firstIndex = ((3- diceSum%4)*34 + diceSum*2)%136;
    }

    @Override
    public void addObserver(WallObserver observer){
        this.observers.add(observer);
    }

    @Override
    public int getQuadCount(){
        return quadCount;
    }

    @Override
    public int getDrawableTileCount(){
        return 122 - drawCount - quadCount;
    }

    @Override
    public boolean hasDrawableTile(){
        return getDrawableTileCount()>=1;
    }

    @Override
    public Tile takeTile(){
        if(!hasDrawableTile())
            throw new IllegalStateException("no drawable tiles left");
        var takeOffset = drawCount++;
        var takenTile = tileAt(takeOffset);
        var wind = windOf(takeOffset);
        var column = columnOf(takeOffset);
        var floor = floorOf(takeOffset);
        observers.forEach(o->o.tileTaken(wind, column, floor));
        return takenTile;
    }

    @Override
    public Tile takeQuadTile(){
        if(quadCount==4)
            throw new IllegalStateException("can't draw 5th quad tile");
        var takeOffset = QUAD_TILE_OFFSETS[quadCount++];
        var takenTile = tileAt(takeOffset);
        var wind = windOf(takeOffset);
        var column = columnOf(takeOffset);
        var floor = floorOf(takeOffset);
        observers.forEach(o->o.tileTaken(wind, column, floor));
        return takenTile;
    }

    @Override
    public void revealIndicatorImmediately(){
        if(revealCount==5)
            throw new IllegalStateException("can't reveal 6th indicator");
        var revealOffset = UPPER_INDICATOR_OFFSETS[revealCount++];
        var revealedTile = tileAt(revealOffset);
        var wind = windOf(revealOffset);
        var column = columnOf(revealOffset);
        observers.forEach(o->o.tileRevealed(wind, column, revealedTile));
    }

    @Override
    public void revealIndicatorsIfPresent(){
        while(revealCount - 1<quadCount)
            revealIndicatorImmediately();
    }

    @Override
    public List<Tile> getUpperIndicators(){
        return Arrays.stream(UPPER_INDICATOR_OFFSETS)
                .limit(revealCount)
                .mapToObj(this::tileAt)
                .toList();
    }

    @Override
    public List<Tile> getLowerIndicators(){
        return Arrays.stream(LOWER_INDICATOR_OFFSETS)
                .limit(revealCount)
                .mapToObj(this::tileAt)
                .toList();
    }

    private Tile tileAt(int offset){
        return tiles[indexOf(offset)];
    }

    private int indexOf(int offset){
        return (firstIndex + offset)%136;
    }

    private Wind windOf(int offset){
        return Wind.values()[indexOf(offset)/34];
    }

    private int columnOf(int offset){
        return indexOf(offset)%34/2;
    }

    private int floorOf(int offset){
        return (indexOf(offset) + 1)%2;
    }
}
