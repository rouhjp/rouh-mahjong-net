package jp.rouh.mahjong.net;

import jp.rouh.mahjong.game.GameTable;
import jp.rouh.mahjong.game.event.TableStrategyMock;
import jp.rouh.util.net.BioMessageServer;
import jp.rouh.util.net.MessageConnection;
import jp.rouh.util.net.MessageServerListener;
import jp.rouh.util.net.msg.MessageConverter;
import jp.rouh.util.net.msg.RemoteConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * 麻雀ルームサーバ。
 * @author Rouh
 * @version 1.0
 */
public class RoomServer extends BioMessageServer implements MessageServerListener{
    private static final Logger LOG = LoggerFactory.getLogger(RoomServer.class);
    private final MessageConverter converter = RoomMessageConverters.getConverter();
    private final Map<String, RoomMember> members = new ConcurrentHashMap<>();

    /**
     * 麻雀ルームサーバを生成します。
     */
    public RoomServer(){
        addListener(this);
    }

    /**
     * 麻雀ルームサーバに接続したメンバ。
     */
    private class RoomMember implements Room{
        private final RoomObserver observer;
        private String name;
        private boolean ready;
        private RoomMember(MessageConnection connection){
            connection.addListener(RemoteConnections.newDispatcher(this, connection, converter));
            observer = RemoteConnections.newProxy(RoomObserver.class, connection, converter);
        }

        private RoomObserver getObserver(){
            return observer;
        }

        @Override
        public void notifyName(String name){
            LOG.info(name + " new name notified: "+name);
            this.name = name;
            updated();
        }

        @Override
        public void notifyReady(boolean ready){
            LOG.info(name + " ready notified: "+ready);
            this.ready = ready;
            updated();
        }

        @Override
        public void start(){
            RoomServer.this.start();
        }

        public RoomMemberData getData(){
            var data = new RoomMemberData();
            data.setName(name);
            data.setReady(ready);
            return data;
        }
    }

    /**
     * メンバからゲーム開始要求を受け取った際の処理。
     * <p>接続した全メンバにゲーム開始を通知し, 新規スレッドでゲームを開始します。
     * <p>接続メンバが4人に満たない場合はNPCを数合わせとして追加してゲームを開始します。
     */
    private void start(){
        members.values().forEach(member->member.observer.gameStarted());
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{
            var table = new GameTable();
            var players = members.values().stream().toList();
            for(var player:players){
                table.addPlayer(player.name, player.getObserver());
            }
            for(int i = 1;i<=(4 - players.size());i++){
                table.addPlayer("bot"+i, TableStrategyMock.DISCARD);
            }
            table.start();
        });
        executor.shutdown();
    }

    /**
     * メンバが接続, 名前通知, 準備完了状態変更した際に実行される情報更新処理。
     * <p>接続した全メンバに変更を通知します。
     */
    private void updated(){
        var memberList = members.values().stream().map(RoomMember::getData).toList();
        members.values().forEach(member->member.getObserver().roomUpdated(memberList));
    }

    @Override
    public void userConnected(String userId, MessageConnection connection){
        if(members.size()<4){
            members.put(userId, new RoomMember(connection));
        }else{
            connection.close();
        }
    }

    @Override
    public void userDisconnected(String userId){
        members.remove(userId);
        updated();
    }
}
