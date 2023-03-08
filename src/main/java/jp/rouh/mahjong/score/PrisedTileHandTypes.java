package jp.rouh.mahjong.score;

import java.util.ArrayList;
import java.util.List;

/**
 * ドラ役関連ユーティリティクラス。
 * @author Rouh
 * @version 1.0
 */
final class PrisedTileHandTypes{
    private PrisedTileHandTypes(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * 特徴量から成立するドラ役をリスト形式で取得します。
     * @param feature 特徴量
     * @return ドラ役のリスト
     */
    static List<BasicHandType> testAll(HandFeature feature){
        var handTypes = new ArrayList<BasicHandType>();
        int count = feature.getOpenPrisedTileCount();
        if(count>0){
            handTypes.add(of(count));
        }
        int redCount = feature.getRedPrisedTileCount();
        if(redCount>0){
            handTypes.add(ofRed(redCount));
        }
        int hiddenCount = feature.getHiddenPrisedTileCount();
        if(hiddenCount>0){
            handTypes.add(ofHidden(hiddenCount));
        }
        return handTypes;
    }

    /**
     * 指定したドラ数のドラ役を取得します。
     * @param count ドラ数
     * @return ドラ役
     * @throws IllegalArgumentException ドラ数が0以下の場合
     */
    static BasicHandType of(int count){
        if(count<=0) throw new IllegalArgumentException("invalid count: " + count);
        return new BasicHandType(){
            @Override
            public int getDoubles(){
                return count;
            }

            @Override
            public String getName(){
                return count==1?"ドラ":"ドラ"+count;
            }

            @Override
            public String toString(){
                return "PRISED_TILE("+count+")";
            }
        };
    }

    /**
     * 指定したドラ数の赤ドラ役を取得します。
     * @param count 赤ドラ数
     * @return 赤ドラ役
     * @throws IllegalArgumentException ドラ数が0以下の場合
     */
    static BasicHandType ofRed(int count){
        if(count<=0) throw new IllegalArgumentException("invalid count: " + count);
        return new BasicHandType(){
            @Override
            public int getDoubles(){
                return count;
            }

            @Override
            public String getName(){
                return count==1?"赤ドラ":"赤ドラ"+count;
            }

            @Override
            public String toString(){
                return "RED_PRISED_TILE("+count+")";
            }
        };
    }

    /**
     * 指定したドラ数の裏ドラ役を取得します。
     * @param count 裏ドラ数
     * @return 裏ドラ役
     * @throws IllegalArgumentException ドラ数が0以下の場合
     */
    static BasicHandType ofHidden(int count){
        if(count<=0) throw new IllegalArgumentException("invalid count: " + count);
        return new BasicHandType(){
            @Override
            public int getDoubles(){
                return count;
            }

            @Override
            public String getName(){
                return count==1?"裏ドラ":"裏ドラ"+count;
            }

            @Override
            public String toString(){
                return "HIDDEN_PRISED_TILE("+count+")";
            }
        };
    }
}
