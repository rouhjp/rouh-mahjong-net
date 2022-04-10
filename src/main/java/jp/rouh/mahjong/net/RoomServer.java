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

public class RoomServer extends BioMessageServer implements MessageServerListener{
    private static final Logger LOG = LoggerFactory.getLogger(RoomServer.class);
    private final MessageConverter converter = RoomMessageConverters.getConverter();
    private final Map<String, RoomMember> members = new ConcurrentHashMap<>();

    public RoomServer(){
        addListener(this);
    }

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
