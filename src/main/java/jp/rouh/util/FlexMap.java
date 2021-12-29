package jp.rouh.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FlexMap<K, V> extends HashMap<K, V>{

    public FlexMap(){
        super();
    }

    public FlexMap(Map<K, V> map){
        super(map);
    }

    public <K2> FlexMap<K2, V> mapKey(Function<K, K2> keyMapper){
        var newMap = new FlexMap<K2, V>();
        for(var key:keySet()){
            newMap.put(keyMapper.apply(key), get(key));
        }
        return newMap;
    }

    public <K2> FlexMap<K2, V> mapKey(BiFunction<K, V, K2> keyMapper){
        var newMap = new FlexMap<K2, V>();
        for(var key:keySet()){
            newMap.put(keyMapper.apply(key, get(key)), get(key));
        }
        return newMap;
    }

    public <V2> FlexMap<K, V2> mapValue(Function<V, V2> valueMapper){
        var newMap = new FlexMap<K, V2>();
        for(var key:keySet()){
            newMap.put(key, valueMapper.apply(get(key)));
        }
        return newMap;
    }

    public <V2> FlexMap<K, V2> mapValue(BiFunction<K, V, V2> valueMapper){
        var newMap = new FlexMap<K, V2>();
        for(var key:keySet()){
            newMap.put(key, valueMapper.apply(key, get(key)));
        }
        return newMap;
    }
}
