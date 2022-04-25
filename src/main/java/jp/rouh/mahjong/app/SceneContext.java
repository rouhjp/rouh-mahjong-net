package jp.rouh.mahjong.app;

import java.util.function.Consumer;

/**
 * 画面遷移コンテキスト。
 * <p>各シーンが他シーンに遷移する際にアクセスするインターフェース。
 * @author Rouh
 * @version 1.0
 */
public interface SceneContext{

    /**
     * 指定したシーンに画面遷移します。
     * @param clazz 遷移先シーンのクラスオブジェクト
     * @param <T> 遷移先シーン
     */
    <T extends Scene> void moveTo(Class<T> clazz);

    /**
     * 指定したシーンを取得します。
     * @param clazz 遷移先シーンのクラスオブジェクト
     * @param <T> 遷移先シーン
     * @return 遷移先シーン
     */
    <T extends Scene> T sceneOf(Class<T> clazz);

    /**
     * 指定したシーンに画面遷移します。
     * @param clazz 遷移先シーンのクラスオブジェクト
     * @param initializer 遷移前に遷移先シーンに適用する初期化処理
     * @param <T> 遷移先シーン
     */
    default <T extends Scene> void moveTo(Class<T> clazz, Consumer<T> initializer){
        var scene = sceneOf(clazz);
        initializer.accept(scene);
        moveTo(clazz);
    }
}
