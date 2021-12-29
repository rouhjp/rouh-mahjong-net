package jp.rouh.mahjong.game.event;

import java.util.List;

public enum TableStrategyMock implements TableObserverAdapter, TableStrategy{

    DISCARD{
        @Override
        public TurnAction selectTurnAction(List<TurnAction> choices){
            return choices.get(0);
        }

        @Override
        public CallAction selectCallAction(List<CallAction> choices){
            return choices.get(0);
        }
    }
}
