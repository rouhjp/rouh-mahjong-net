package jp.rouh.mahjong.game.event;

/**
 * プレイヤーのプロファイル情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class ProfileData{
    private final String name;

    public ProfileData(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
