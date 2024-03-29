package jp.rouh.mahjong.bot;

import jp.rouh.mahjong.game.event.CallAction;
import jp.rouh.mahjong.game.event.TableObserverAdapter;
import jp.rouh.mahjong.game.event.TableStrategy;
import jp.rouh.mahjong.game.event.TurnAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * テーブル戦略のモック。
 * @author Rouh
 * @version 1.0
 */
public enum TableStrategyMock implements TableObserverAdapter, TableStrategy{

    /**
     * ツモ切りをする戦略
     */
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
