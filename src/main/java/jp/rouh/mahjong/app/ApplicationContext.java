package jp.rouh.mahjong.app;

/**
 * アプリケーションコンテキスト。
 * <p>アプリケーション内の各コンポーネントから,
 * 他画面への遷移やアプリケーションスコープのリソースなど,
 * アプリケーション全体に関わる操作を行う際に参照するインターフェース。
 * @see SceneContext 画面遷移コンテキスト
 * @author Rouh
 * @version 1.0
 */
public interface ApplicationContext extends SceneContext{

    /**
     * アプリケーションのフレームに対応する現在のサイズの重みを取得します。
     * <p>各コンポーネントの大きさは基本サイズに重みをかけ合わせた値であることを想定します。
     * @return 重み
     */
    int getSizeWeight();

}
