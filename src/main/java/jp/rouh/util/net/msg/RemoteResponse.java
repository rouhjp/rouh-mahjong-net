package jp.rouh.util.net.msg;

/**
 * 接続先オブジェクトでのリモートメソッド呼び出しの戻り値を格納するDTO。
 * @author Rouh
 * @version 1.0
 */
public class RemoteResponse{
    private String requestId;
    private Object value;

    /**
     * リクエストIDを取得します。
     * @return リクエストID
     */
    public String getRequestId(){
        return requestId;
    }

    /**
     * リクエストIDを設定します。
     * @param requestId リクエストID
     */
    public void setRequestId(String requestId){
        this.requestId = requestId;
    }

    /**
     * 戻り値を取得します。
     * @return 戻り値
     */
    public Object getValue(){
        return value;
    }

    /**
     * 戻り値を設定します。
     * @param value 戻り値
     */
    public void setValue(Object value){
        this.value = value;
    }
}
