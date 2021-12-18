package jp.rouh.mahjong.app.view;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 牌以外の卓上で用いる画像を管理するクラス。
 * <p>このクラスはスレッドセーフです。
 * @author Rouh
 * @version 1.0
 */
class TableImageManager{
    private static final TableImageManager INSTANCE = new TableImageManager();
    private final Map<Integer, BufferedImage> diceImages = new ConcurrentHashMap<>();
    private final Map<Direction, BufferedImage> readyBoneImages = new ConcurrentHashMap<>();
    private TableImageManager(){
        //pass
    }

    /**
     * 指定の目のサイコロの画像を取得します。
     * @param value 目の値(1..6)
     * @return サイコロ画像
     * @throws IllegalArgumentException 目の値が不正の場合
     */
    BufferedImage getDiceImage(int value){
        if(value<1 || value>6){
            throw new IllegalArgumentException("invalid dice value: "+value);
        }
        return diceImages.computeIfAbsent(value, x->ImageResources.ofDices(value));
    }

    /**
     * 指定の向きのリーチ棒の画像を取得します。
     * @param d 向き
     * @return リーチ棒画像
     */
    BufferedImage getReadyBoneImage(Direction d){
        return readyBoneImages.computeIfAbsent(d, x->ImageFunctions.rotate(ImageResources.ofReadyBone(), d));
    }

    /**
     * シングルトンインスタンスを取得します。
     * @return インスタンス
     */
    static TableImageManager getInstance(){
        return INSTANCE;
    }
}
