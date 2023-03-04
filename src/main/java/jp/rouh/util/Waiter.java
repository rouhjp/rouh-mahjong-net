package jp.rouh.util;

import java.util.concurrent.Semaphore;

/**
 * 別スレッドから値が設定されるまでスレッドをブロックして待機するクラス。
 *
 * @param <T> 値の型
 * @author Rouh
 * @version 1.0
 */
@SuppressWarnings("unused")
public class Waiter<T>{
    private final Semaphore semaphore = new Semaphore(0);
    private volatile boolean waiting;
    private volatile Thread waitingThread;
    private volatile T value;

    /**
     * 値の取得を待機しているスレッドが存在するか検査します。
     * @return true スレッドが存在する場合
     *         false スレッドが存在しない場合
     */
    public boolean isWaiting(){
        return waiting;
    }

    /**
     * 値が別スレッドから設定されるまで待機し, 値を取得します。
     * @return 設定された値
     * @throws IllegalStateException 既に待機中のスレッドが存在する場合
     * @throws InterruptedException 待機中にスレッドが中断された場合
     */
    public T waitForArrival() throws InterruptedException{
        if(isWaiting()) throw new IllegalStateException("another thread is already waiting");
        try{
            waiting = true;
            waitingThread = Thread.currentThread();
            semaphore.acquire();
            return value;
        }finally{
            waiting = false;
            waitingThread = null;
        }
    }

    /**
     * 値を設定し, 待機中のスレッドに値を共有します。
     * @param value 値
     * @throws IllegalStateException 値の設定を待機しているスレッドが存在しない場合
     */
    public void arrived(T value){
        if(!isWaiting()) throw new IllegalStateException("no thread is waiting for arrival");
        this.value = value;
        this.semaphore.release();
    }

    /**
     * 待機中のスレッドがいる場合, スレッドを中断します。
     */
    public void cancel(){
        if(isWaiting()){
            waitingThread.interrupt();
        }
    }
}
