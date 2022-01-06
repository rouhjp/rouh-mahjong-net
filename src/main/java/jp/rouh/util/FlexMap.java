package jp.rouh.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 拡張的なシンタックスを提供するマップクラス。
 * <p>このクラスは{@link HashMap}クラス実装の拡張クラスです。
 * @param <K> このマップで保持されるキーの型
 * @param <V> マップされる値の型
 * @author Rouh
 * @version 1.0
 */
public class FlexMap<K, V> extends HashMap<K, V>{

    /**
     * デフォルトの初期容量を持つ空の{@link FlexMap}を生成します。
     */
    public FlexMap(){
        super();
    }

    /**
     * 指定された初期容量を持つ空の{@link FlexMap}を作成します。
     * @param initialCapacity 初期容量
     */
    public FlexMap(int initialCapacity){
        super(initialCapacity);
    }

    /**
     * 与えられた既存のマップを元に{@link FlexMap}のインスタンスを生成します。
     * @param map 既存マップ
     */
    public FlexMap(Map<? extends K, ? extends V> map){
        super(map);
    }

    /**
     * このマップを元にキーの型を変更した新規マップを生成します。
     * @param keyMapper キーの変換関数
     * @param <K2> 新しいマップで保持されるキーの型
     * @return 新規マップ
     */
    public <K2> FlexMap<K2, V> mapKey(Function<? super K, ? extends K2> keyMapper){
        var newMap = new FlexMap<K2, V>();
        for(var key:keySet()){
            newMap.put(keyMapper.apply(key), get(key));
        }
        return newMap;
    }

    /**
     * このマップを元にキーの型を変更した新規マップを生成します。
     * @param keyMapper キーの変換関数
     * @param <K2> 新しいマップで保持されるキーの型
     * @return 新規マップ
     */
    public <K2> FlexMap<K2, V> mapKey(BiFunction<? super K, ? super V, ? extends K2> keyMapper){
        var newMap = new FlexMap<K2, V>();
        for(var key:keySet()){
            newMap.put(keyMapper.apply(key, get(key)), get(key));
        }
        return newMap;
    }

    /**
     * このマップを元に値の型を変更した新規マップを生成します。
     * @param valueMapper 値の変換関数
     * @param <V2> 新しいマップの値の型
     * @return 新規マップ
     */
    public <V2> FlexMap<K, V2> mapValue(Function<? super V, ? extends V2> valueMapper){
        var newMap = new FlexMap<K, V2>();
        for(var key:keySet()){
            newMap.put(key, valueMapper.apply(get(key)));
        }
        return newMap;
    }

    /**
     * このマップを元に値の型を変更した新規マップを生成します。
     * @param valueMapper 値の変換関数
     * @param <V2> 新しいマップの値の型
     * @return 新規マップ
     */
    public <V2> FlexMap<K, V2> mapValue(BiFunction<? super K, ? super V, ? extends V2> valueMapper){
        var newMap = new FlexMap<K, V2>();
        for(var key:keySet()){
            newMap.put(key, valueMapper.apply(key, get(key)));
        }
        return newMap;
    }

    /**
     * 指定した列挙型の全ての要素と初期値の可変マップを生成します。
     * @param clazz 列挙型のクラスオブジェクト
     * @param defaultValue 初期値
     * @param <K> このマップで保持されるキーの型
     * @param <V> マップされる値の型
     * @return 列挙型の要素分の値が設定された可変のマップ
     */
    public static <K extends Enum<K>, V> FlexMap<K, V> of(Class<K> clazz, V defaultValue){
        var newMap = new FlexMap<K, V>();
        for(var constant:clazz.getEnumConstants()){
            newMap.put(constant, defaultValue);
        }
        return newMap;
    }

    /**
     * 指定した列挙型の全ての要素と初期値の可変マップを生成します。
     * @param clazz 列挙型のクラスオブジェクト
     * @param initializer 初期値を生成する関数
     * @param <K> このマップで保持されるキーの型
     * @param <V> マップされる値の型
     * @return 列挙型の要素分の値が設定された可変のマップ
     */
    public static <K extends Enum<K>, V> FlexMap<K, V> of(Class<K> clazz, Function<K, ? extends V> initializer){
        var newMap = new FlexMap<K, V>();
        for(var constant:clazz.getEnumConstants()){
            newMap.put(constant, initializer.apply(constant));
        }
        return newMap;
    }
}
