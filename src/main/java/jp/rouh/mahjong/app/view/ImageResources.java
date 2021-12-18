package jp.rouh.mahjong.app.view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 画像リソース読み込みユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
final class ImageResources{
    private ImageResources(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * リーチ棒の画像を取得します。
     * @return リーチ棒画像
     */
    static BufferedImage ofReadyBone(){
        return read("/img/1000b.png");
    }

    /**
     * サイコロの画像を取得します。
     * @param value サイコロの目の値
     * @return サイコロ画像
     */
    static BufferedImage ofDices(int value){
        return read("/img/dices/dice"+value+".png");
    }

    /**
     * 牌の画像を取得します。
     * @param code 牌コード
     * @return 牌画像
     */
    static BufferedImage ofTile(String code){
        return read("/img/tiles/"+code+".jpg");
    }

    /**
     * 指定のパスにある画像を{@link InputStream}を用いて取得します。
     * @param filepath パス
     * @return 画像
     * @throws IllegalArgumentException パスの画像が見つからない場合
     */
    private static BufferedImage read(String filepath){
        try(InputStream resourceStream = ImageResources.class.getResourceAsStream(filepath)){
            return ImageIO.read(Objects.requireNonNull(resourceStream));
        }catch(NullPointerException e){
            throw new IllegalArgumentException("resource not found: "+filepath);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
