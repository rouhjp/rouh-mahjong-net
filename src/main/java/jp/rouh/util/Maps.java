package jp.rouh.util;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * マップ操作のユーティリティクラス。
 *
 * @author Rouh
 * @version 1.0
 */
@SuppressWarnings("unused")
public final class Maps{
    private Maps(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * キーを変換した新規マップを生成します。
     *
     * @param map 元のマップ
     * @param keyMapper キーの変換関数
     * @param <K2> 新しいマップで保持されるキーの型
     * @return 新規マップ
     */
    public static <K, V, K2> Map<K2, V> mapKey(Map<K, V> map, Function<? super K, ? extends K2> keyMapper){
        var newMap = new HashMap<K2, V>();
        for(var key:map.keySet()){
            newMap.put(keyMapper.apply(key), map.get(key));
        }
        return newMap;
    }

    /**
     * キーを変換した新規マップを生成します。
     *
     * @param map 元のマップ
     * @param keyMapper キーの変換関数
     * @param <K2> 新しいマップで保持されるキーの型
     * @return 新規マップ
     */
    public static <K, V, K2> Map<K2, V> mapKey(Map<K, V> map, BiFunction<? super K, ? super V, ? extends K2> keyMapper){
        var newMap = new HashMap<K2, V>();
        for(var key:map.keySet()){
            newMap.put(keyMapper.apply(key, map.get(key)), map.get(key));
        }
        return newMap;
    }

    /**
     * 値を変換した新規マップを生成します。
     *
     * @param map 元のマップ
     * @param valueMapper 値の変換関数
     * @param <V2> 新しいマップで保持される値の型
     * @return 新規マップ
     */
    public static <K, V, V2> Map<K, V2> mapValue(Map<K, V> map, Function<? super V, ? extends V2> valueMapper){
        var newMap = new HashMap<K, V2>();
        for(var key:map.keySet()){
            newMap.put(key, valueMapper.apply(map.get(key)));
        }
        return newMap;
    }

    /**
     * 値を変換した新規マップを生成します。
     *
     * @param map 元のマップ
     * @param valueMapper 値の変換関数
     * @param <V2> 新しいマップで保持される値の型
     * @return 新規マップ
     */
    public static <K, V, V2> Map<K, V2> mapValue(Map<K, V> map, BiFunction<? super K, ? super V, ? extends V2> valueMapper){
        var newMap = new HashMap<K, V2>();
        for(var key:map.keySet()){
            newMap.put(key, valueMapper.apply(key, map.get(key)));
        }
        return newMap;
    }

    /**
     * 指定した列挙型の全ての要素と初期値の可変マップを生成します。
     *
     * @param clazz 列挙型のクラスオブジェクト
     * @param defaultValue 初期値
     * @param <K> このマップで保持されるキーの型
     * @param <V> マップされる値の型
     * @return 列挙型の要素分の値が設定された可変のマップ
     */
    public static <K extends Enum<K>, V> EnumMap<K, V> ofEnum(Class<K> clazz, V defaultValue){
        var newMap = new EnumMap<K, V>(clazz);
        for(var constant:clazz.getEnumConstants()){
            newMap.put(constant, defaultValue);
        }
        return newMap;
    }

    /**
     * 指定した列挙型の全ての要素と初期値の可変マップを生成します。
     *
     * @param clazz 列挙型のクラスオブジェクト
     * @param initializer 初期値を生成する関数
     * @param <K> このマップで保持されるキーの型
     * @param <V> マップされる値の型
     * @return 列挙型の要素分の値が設定された可変のマップ
     */
    public static <K extends Enum<K>, V> EnumMap<K, V> ofEnum(Class<K> clazz, Function<K, ? extends V> initializer){
        var newMap = new EnumMap<K, V>(clazz);
        for(var constant:clazz.getEnumConstants()){
            newMap.put(constant, initializer.apply(constant));
        }
        return newMap;
    }
}
