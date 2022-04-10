package jp.rouh.util.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ブロッキングIOによるメッセージ通信サーバ
 * @author Rouh
 * @version 1.0
 */
public class BioMessageServer implements MessageServer{
    private static final Logger LOG = LoggerFactory.getLogger(BioMessageServer.class);
    private final ExecutorService clientAcceptingThread = Executors.newSingleThreadExecutor();
    private final Queue<MessageServerListener> listeners = new ConcurrentLinkedQueue<>();
    private final Queue<BioMessageChannel> users = new ConcurrentLinkedQueue<>();
    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();

    public BioMessageServer(){
        //pass
    }

    @Override
    public void start(int port) throws IOException{
        serverSocket.set(new ServerSocket(port));
        clientAcceptingThread.submit(()->{
            try{
                while(!serverSocket.get().isClosed()){
                    var socket = serverSocket.get().accept();
                    var userId = "U" + UUID.randomUUID();
                    LOG.info("user " + userId + " (" + socket.getInetAddress() + ") joined server");
                    var user = new BioMessageChannel(this, socket, userId);
                    users.add(user);
                }
            }catch(IOException e){
                LOG.info("IOException caught while accepting socket: " + e.getMessage());
            }
        });
        clientAcceptingThread.shutdown();
    }

    @Override
    public void addListener(MessageServerListener listener){
        listeners.add(listener);
    }

    @Override
    public void close(){
        try{
            LOG.info("closing users");
            users.forEach(MessageConnection::close);
            clientAcceptingThread.shutdownNow();
            LOG.info("closing server");
            serverSocket.get().close();
            LOG.info("server closed");
        }catch(IOException e){
            LOG.warn("IOException caught while terminating server: "+e.getMessage());
        }
    }

    private static class BioMessageChannel implements MessageConnection{
        private final ExecutorService messageListeningThread = Executors.newSingleThreadExecutor();
        private final Queue<MessageListener> listeners = new ConcurrentLinkedQueue<>();
        private final BioMessageServer server;
        private final BufferedReader messageReader;
        private final PrintWriter messageWriter;
        private final Socket socket;
        private final String userId;
        private volatile boolean closed = false;
        private BioMessageChannel(BioMessageServer server, Socket socket, String userId) throws IOException{
            this.userId = userId;
            this.server = server;
            this.socket = socket;
            this.messageReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.messageWriter = new PrintWriter(socket.getOutputStream());
            server.listeners.forEach(listener->listener.userConnected(userId, this));
            messageListeningThread.submit(()->{
               try{
                   while(!socket.isClosed()){
                       var message = messageReader.readLine();
                       received(message);
                   }
               }catch(IOException e){
                   LOG.warn("IOException caught while listening message from client " + e.getMessage());
               }finally{
                   close();
               }
            });
            messageListeningThread.shutdown();
        }

        @Override
        public void addListener(MessageListener listener){
            listeners.add(listener);
        }

        private void received(String message){
            LOG.info("received from " + userId + " message=" + message);
            listeners.forEach(listener->listener.received(message));
        }

        @Override
        public void send(String message){
            LOG.info("send to " + userId + " message=" + message);
            if(!socket.isClosed()){
                messageWriter.println(message);
                messageWriter.flush();
            }else{
                LOG.warn("failed to send message: already closed message="+message);
            }
        }

        @Override
        public void close(){
            synchronized(this){
                if(!closed){
                    closed = true;
                    server.users.remove(this);
                    server.listeners.forEach(listener->listener.userDisconnected(userId));
                    if(!messageListeningThread.isTerminated()){
                        messageListeningThread.shutdownNow();
                    }
                    if(!socket.isClosed()){
                        try{
                            socket.close();
                        }catch(IOException e){
                            LOG.warn("IOException caught while closing channel of " + userId + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
