package jp.rouh.util.net.msg;

import jp.rouh.util.net.MessageConnection;
import jp.rouh.util.net.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class RemoteConnections{
    private static final Logger LOG = LoggerFactory.getLogger(RemoteConnections.class);
    private RemoteConnections(){
        throw new AssertionError("instantiate utility class");
    }

    public static <T> T newProxy(Class<T> remoteInterface, MessageConnection connection, MessageConverter converter){
        @SuppressWarnings("unchecked")
        var proxy = (T)Proxy.newProxyInstance(remoteInterface.getClassLoader(),
                new Class[]{remoteInterface}, new RemoteProxy(remoteInterface, connection, converter));
        return proxy;
    }

    public static MessageListener newDispatcher(Object dispatchTo, MessageConnection connection, MessageConverter converter){
        return message->{
            LOG.info("remote-receiver=> received: " + message);
            try{
                var object = converter.decode(message);
                if(object instanceof RemoteRequest request){
                    var method = request.getMethod();
                    var args = request.getParameters().toArray(new Object[0]);
                    LOG.info("remote-receiver=> dispatching request: " + method.getName());
                    var returnValue = method.invoke(dispatchTo, args);
                    LOG.info("remote-receiver=> dispatching request completed: " + method.getName());
                    if(method.getReturnType()!=void.class){
                        var response = new RemoteResponse();
                        response.setValue(returnValue);
                        LOG.info("remote-receiver=> sending back response: " + method.getName());
                        var returnMessage = converter.encode(response);
                        connection.send(returnMessage);
                        LOG.info("remote-receiver=> sending back response completed: " + method.getName());
                    }
                }
            }catch(Exception e){
                LOG.info("exception caught while resolving received message: " + e.getMessage() + " message=" + message);
            }
        };
    }

    @Deprecated
    public static void setReceiver(Object dispatchTo, MessageConnection connection, MessageConverter converter){
        connection.addListener(newDispatcher(dispatchTo, connection, converter));
    }

    private static class RemoteProxy implements InvocationHandler, MessageListener{
        private final BlockingDeque<Object> blockingDeque = new LinkedBlockingDeque<>();
        private final Class<?> remoteInterface;
        private final MessageConnection connection;
        private final MessageConverter converter;
        private RemoteProxy(Class<?> remoteInterface, MessageConnection connection, MessageConverter converter){
            this.remoteInterface = remoteInterface;
            this.converter = converter;
            this.connection = connection;
            this.connection.addListener(this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception{
            LOG.info("remote-proxy=> invoked "+method.getName());
            try{
                if(method.getDeclaringClass().isAssignableFrom(remoteInterface)){
                    var request = new RemoteRequest();
                    request.setMethod(method);
                    request.setParameters(args==null? List.of():List.of(args));
                    LOG.info("remote-proxy=> sending request | " + method.getName());
                    connection.send(converter.encode(request));
                    LOG.info("remote-proxy=> sending request completed | " + method.getName());
                    if(method.getReturnType()!=void.class){
                        LOG.info("remote-proxy=> waiting for return value response | " + method.getName());
                        var returnValue = blockingDeque.take();
                        LOG.info("remote-proxy=> waiting for return value response completed | " + method.getName());
                        return returnValue;
                    }
                    return null;
                }
                return method.invoke(proxy, args);
            }catch(Exception e){
                LOG.info("exception caught while invoking remote interface using proxy: "+method.getName()+" "+e);
                throw e;
            }
        }

        @Override
        public void received(String message){
            LOG.info("remote-response-waiter=> received something: "+message);
            var object = converter.decode(message);
            if(object instanceof RemoteResponse response){
                LOG.info("remote-response-waiter=> receiving response");
                blockingDeque.addLast(response.getValue());
                LOG.info("remote-response-waiter=> receiving response completed");
            }
        }
    }
}
