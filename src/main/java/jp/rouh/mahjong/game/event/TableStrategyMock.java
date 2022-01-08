package jp.rouh.mahjong.game.event;

import java.util.List;
import java.util.concurrent.TimeUnit;

public enum TableStrategyMock implements TableObserverAdapter, TableStrategy{

    DISCARD{
        @Override
        public TurnAction selectTurnAction(List<TurnAction> choices){
            try{
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return choices.get(0);
        }

        @Override
        public CallAction selectCallAction(List<CallAction> choices){
            return choices.get(0);
        }
    }
}
