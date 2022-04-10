package jp.rouh.util.net.msg;

import java.lang.reflect.Method;
import java.util.List;

/**
 * リモートメソッド呼び出しのためのDTO。
 * @author Rouh
 * @version 1.0
 */
public class RemoteRequest{
    private Method method;
    private List<Object> parameters;

    public Method getMethod(){
        return method;
    }

    public void setMethod(Method method){
        this.method = method;
    }

    public List<Object> getParameters(){
        return parameters;
    }

    public void setParameters(List<Object> parameters){
        this.parameters = parameters;
    }
}
