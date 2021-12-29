package jp.rouh.mahjong.game;

import jp.rouh.mahjong.game.event.ProfileData;
import jp.rouh.mahjong.game.event.TableStrategy;

/**
 * プレイヤークラス。
 * @author Rouh
 * @version 1.0
 */
class Player{
    private final String name;
    private final TableStrategy strategy;

    /**
     * コンストラクタ。
     * @param name プレイヤーの名前
     * @param strategy プレイヤーの戦略オブジェクト
     */
    Player(String name, TableStrategy strategy){
        this.name = name;
        this.strategy = strategy;
    }

    /**
     * プレイヤーの名前を取得します。
     * @return 名前
     */
    String getName(){
        return name;
    }

    /**
     * プレイヤーの戦略オブジェクトを取得します。
     * @return 戦略オブジェクト
     */
    TableStrategy getStrategy(){
        return strategy;
    }

    /**
     * プレイヤーのプロファイル情報を取得します。
     * @return プロファイル情報
     */
    ProfileData getProfileData(){
        return new ProfileData(name);
    }
}
