package jp.rouh.util.net.msg;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 接続先オブジェクトでのリモートメソッド呼び出しに必要な情報を格納するDTO。
 * @author Rouh
 * @version 1.0
 */
public class RemoteRequest{
    private Method method;
    private List<Object> parameters;

    /**
     * メソッドを取得します。
     * @return メソッド
     */
    public Method getMethod(){
        return method;
    }

    /**
     * メソッドを設定します。
     * @param method メソッド
     */
    public void setMethod(Method method){
        this.method = method;
    }

    /**
     * パラメータを取得します。
     * @return パラメータ
     */
    public List<Object> getParameters(){
        return parameters;
    }

    /**
     * パラメータを設定します。
     * @param parameters パラメータ
     */
    public void setParameters(List<Object> parameters){
        this.parameters = parameters;
    }
}
