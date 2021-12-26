package jp.rouh.util;

import java.util.concurrent.*;

/**
 * 与えられたタスクを与えられた順にシングルスレッドで実行するエクゼキュータサービス。
 * @author Rouh
 * @version 1.0
 */
public class SingleThreadTaskExecutor extends ThreadPoolExecutor implements ExecutorService{

    /**
     * コンストラクタ。
     */
    public SingleThreadTaskExecutor(){
        super(1, 1, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(){
            @Override
            public Runnable take() throws InterruptedException{
                return takeFirst();
            }
        });
    }
}
