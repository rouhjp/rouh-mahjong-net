package jp.rouh.mahjong.app;

import jp.rouh.mahjong.app.view.TableScene;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * アプリケーションクラス。
 * <p>このアプリケーションの実行クラスです。
 * <p>GUIフレームを生成し, 各画面を表示します。
 * <p>アプリケーションコンテキストとして画面遷移やコンポーネントのリサイズを処理します。
 * @author Rouh
 * @version 1.0
 */
public class Application implements ApplicationContext{
    private static final String APPLICATION_TITLE = "Rouh Mahjong";
    private static final int DEFAULT_WEIGHT = 5;
    private final AtomicInteger weightManager = new AtomicInteger(DEFAULT_WEIGHT);
    private final Map<Class<? extends Scene>, Scene> scenes = new HashMap<>();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(cardLayout);
    private Application(){
        var frame = new JFrame(APPLICATION_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var defaultSize = new Dimension(Scene.BASE_WIDTH*DEFAULT_WEIGHT, Scene.BASE_HEIGHT*DEFAULT_WEIGHT);
        frame.getContentPane().setPreferredSize(defaultSize);
        frame.pack();
        frame.setLayout(null);
        rootPanel.setSize(defaultSize);
        rootPanel.setLocation(0, 0);
        rootPanel.setBorder(new LineBorder(Color.BLACK));
        createScenes();
        moveTo(MenuScene.class);
        frame.add(rootPanel);
        frame.setVisible(true);
        frame.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                int frameHeight = frame.getContentPane().getHeight();
                int frameWidth = frame.getContentPane().getWidth();
                int weight = Math.min(frameWidth/Scene.BASE_WIDTH, frameHeight/Scene.BASE_HEIGHT);
                int sceneHeight = Scene.BASE_HEIGHT*weight;
                int sceneWidth = Scene.BASE_WIDTH*weight;
                int sceneLocationX = (frameWidth - sceneWidth)/2;
                int sceneLocationY = (frameHeight - sceneHeight)/2;
                rootPanel.setLocation(sceneLocationX, sceneLocationY);
                rootPanel.setSize(sceneWidth, sceneHeight);
                if(weightManager.get()!=weight){
                    weightManager.set(weight);
                    scenes.values().forEach(Scene::weightUpdated);
                }
            }
        });
    }

    @Override
    public int getSizeWeight(){
        return weightManager.get();
    }

    private void createScenes(){
        scenes.put(MenuScene.class, new MenuScene(this));
        scenes.put(TableScene.class, new TableScene(this));
        scenes.forEach((clazz, scene)->rootPanel.add(scene, clazz.toString()));
    }

    @Override
    public <T extends Scene> void moveTo(Class<T> clazz){
        assert scenes.containsKey(clazz): "scene not found: "+clazz;
        cardLayout.show(rootPanel, clazz.toString());
    }

    @Override
    public <T extends Scene> void moveTo(Class<T> clazz, Consumer<T> initializer){
        assert scenes.containsKey(clazz): "scene not found: "+clazz;
        @SuppressWarnings("unchecked")
        var scene = (T)scenes.get(clazz);
        initializer.accept(scene);
        cardLayout.show(rootPanel, clazz.toString());
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(Application::new);
    }
}
