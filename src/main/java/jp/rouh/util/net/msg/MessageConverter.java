package jp.rouh.util.net.msg;

import java.util.*;
import java.util.function.Function;

/**
 * オブジェクトと文字列を相互変換するコンバータ。
 * <p>あるオブジェクトを{@link #encode}メソッドで文字列に変換した場合,
 * その後当該文字列を{@link #decode}メソッドに渡すことで元のオブジェクトを復元することができます。
 * <p>具体的な変換規則の実装は, オブジェクトの型ごとに{@link MessageConversionRule}クラスで規定し,
 * 複数のルールを{@link #addRuleFirst}もしくは{@link #addRuleLast}メソッドで登録することで
 * オブジェクトと文字列の総合変換機能を提供します。
 * <p>コンバータによるオブジェクトと文字列の相互変換はシリアライズの代替手段として利用されることを想定します。
 * @see MessageConversionRule
 * @author Rouh
 * @version 1.0
 */
public class MessageConverter{
    private final Map<Class<?>, MessageConversionRule<?>> rules = new HashMap<>();
    private final Deque<Class<?>> supportedTypes = new ArrayDeque<>();

    /**
     * 空のコンバータを作成します。
     */
    public MessageConverter(){
        //pass
    }

    /**
     * このコンバータに新たな変換規則を追加します。
     * <p>このメソッドで追加したルールは, 呼び出し時点での先頭に設定され,
     * エンコード/デコードが実行された場合, 他すべてのルールの評価前に評価されます。
     * @param clazz 対象の型
     * @param rule 変換ルール
     * @param <E> 対象の型
     */
    public <E> void addRuleFirst(Class<E> clazz, MessageConversionRule<E> rule){
        rules.put(clazz, rule);
        supportedTypes.addFirst(clazz);
    }

    /**
     * このコンバータに新たな変換規則を追加します。
     * <p>このメソッドで追加したルールは, 呼び出し時点での末尾に設定され,
     * エンコード/デコードが実行された場合, 他すべてのルールの評価後に評価されます。
     * @param clazz 対象の型
     * @param rule 変換ルール
     * @param <E> 対象の型
     */
    public <E> void addRuleLast(Class<E> clazz, MessageConversionRule<E> rule){
        rules.put(clazz, rule);
        supportedTypes.addLast(clazz);
    }

    /**
     * このコンバータでオブジェクトを文字列に変換します。
     * @param obj オブジェクト
     * @return 文字列
     * @throws MessageConversionException 変換に失敗した場合
     */
    public String encode(Object obj) throws MessageConversionException{
        try{
            for(var supportedType: supportedTypes){
                if(supportedType.isAssignableFrom(obj.getClass())){
                    @SuppressWarnings("unchecked")
                    var encoder = (Function<Object, String>)rules.get(supportedType).getEncoder();
                    return encoder.apply(obj);
                }
            }
            throw new MessageConversionException("encoding unsupported type object: " + obj);
        }catch(MessageConversionException e){
            throw e;
        }catch(Exception e){
            throw new MessageConversionException("exception caught while encoding object: " + obj.getClass().getName() + " " + obj, e);
        }
    }

    /**
     * このコンバータで文字列をオブジェクトに復元します。
     * @param str 文字列
     * @return オブジェクト
     * @throws MessageConversionException 復元に失敗した場合
     */
    public Object decode(String str) throws MessageConversionException{
        try{
            for(var supportedType: supportedTypes){
                if(rules.get(supportedType).getSelector().test(str)){
                    @SuppressWarnings("unchecked")
                    var decoder = (Function<String, Object>)rules.get(supportedType).getDecoder();
                    return decoder.apply(str);
                }
            }
            throw new MessageConversionException("decoding unsupported type string: " + str);
        }catch(MessageConversionException e){
            throw e;
        }catch(Exception e){
            throw new MessageConversionException("exception caught while decoding string: " + str, e);
        }
    }
}
