package jp.rouh.util;

import java.util.EnumMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 列挙型のキー{@code K}とキー{@code K}のすべての要素に対応する値{@code V}を持つマップ
 * @param <K> 列挙型のキー
 * @param <V> 値
 */
public class ItemMap<K extends Enum<K>, V> extends EnumMap<K, V>{
    private final Class<K> clazz;

    /**
     * 列挙型用のマップのコンストラクタ。
     * @param clazz キーのクラス
     * @param defaultValue デフォルト値
     */
    public ItemMap(Class<K> clazz, V defaultValue){
        super(clazz);
        this.clazz = clazz;
        for(K item:clazz.getEnumConstants()){
            super.put(item, defaultValue);
        }
    }

    /**
     * 列挙型用のマップのコンストラクタ。
     * @param clazz キーのクラス
     * @param defaultValueSupplier デフォルト値生成関数
     */
    public ItemMap(Class<K> clazz, Function<K, V> defaultValueSupplier){
        super(clazz);
        this.clazz = clazz;
        for(K item:clazz.getEnumConstants()){
            super.put(item, defaultValueSupplier.apply(item));
        }
    }

    public <V2> ItemMap<K, V2> mapValue(Function<V, V2> valueMapper){
        return new ItemMap<>(clazz, key->valueMapper.apply(get(key)));
    }

    public <V2> ItemMap<K, V2> mapValue(BiFunction<K, V, V2> valueMapper){
        return new ItemMap<>(clazz, key->valueMapper.apply(key, get(key)));
    }
}
