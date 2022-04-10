package jp.rouh.mahjong.net;

public class RoomMemberData{
    private String name;
    private boolean ready;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean isReady(){
        return ready;
    }

    public void setReady(boolean ready){
        this.ready = ready;
    }
}
