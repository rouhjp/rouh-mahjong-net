package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.TableStrategy;

public class Player implements TablePlayer{
    private final String name;
    private final TableStrategy strategy;
    public Player(String name, TableStrategy strategy){
        this.name = name;
        this.strategy = strategy;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public TableStrategy getStrategy(){
        return strategy;
    }
}
