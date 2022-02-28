package jp.rouh.mahjong.game.event;

/**
 * プレイヤーのプロファイル情報DTO。
 * @author Rouh
 * @version 1.0
 */
public class ProfileData{
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
