package jp.rouh.util.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ブロッキングIOによるメッセージ通信クライアント。
 * このクラスはメッセージ通信{@link MessageConnection}の実装です。
 * @author Rouh
 * @version 1.0
 */
public class BioMessageClient implements MessageConnection{
    private static final Logger LOG = LoggerFactory.getLogger(BioMessageClient.class);
    private final ExecutorService messageListeningThread = Executors.newSingleThreadExecutor();
    private final Queue<MessageListener> listeners = new ConcurrentLinkedQueue<>();
    private final BufferedReader messageReader;
    private final PrintWriter messageWriter;
    private final Socket socket;
    private volatile boolean closed = false;

    /**
     * 新規にサーバへの接続を確立します。
     * @param host ホスト名
     * @param port ポート番号
     * @throws IOException 接続に失敗した場合
     */
    public BioMessageClient(String host, int port) throws IOException{
        this.socket = new Socket(host, port);
        this.messageReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.messageWriter = new PrintWriter(socket.getOutputStream());
        messageListeningThread.submit(()->{
            try{
                while(!socket.isClosed()){
                    var message = messageReader.readLine();
                    received(message);
                }
            }catch(IOException e){
                LOG.warn("IOException caught while listening message from client " + e.getMessage());
            }finally{
                try{
                    messageReader.close();
                    messageWriter.close();
                    socket.close();
                }catch(IOException e){
                    LOG.warn("IOException caught while terminating server: "+e.getMessage());
                }
            }
        });
        messageListeningThread.shutdown();
    }

    private void received(String message){
        LOG.info("received from server message="+message);
        listeners.forEach(listener->listener.received(message));
    }

    @Override
    public void addListener(MessageListener listener){
        listeners.add(listener);
    }

    @Override
    public void send(String message){
        LOG.info("send to server message=" + message);
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
                try{
                    socket.close();
                    messageListeningThread.shutdownNow();
                }catch(IOException e){
                    LOG.warn("IOException caught while terminating server: " + e.getMessage());
                }
            }
        }
    }
}
