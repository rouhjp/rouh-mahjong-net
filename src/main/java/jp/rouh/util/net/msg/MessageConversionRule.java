package jp.rouh.util.net.msg;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * ある型のオブジェクトを文字列に変換するルール。
 * @see MessageConverter
 * @param <E> 対象の型
 * @author Rouh
 * @version 1.0
 */
public class MessageConversionRule<E>{
    private final Function<E, String> encoder;
    private final Predicate<String> selector;
    private final Function<String, E> decoder;

    /**
     * 文字列変換ルールを生成します。
     * <p>注入される関数について, エンコーダ関数でオブジェクトを文字列に変換した場合,
     * デコーダ関数で元々と同値のオブジェクトを復元できるよう実装される必要があります。
     * <p>セレクタ関数はこのルールのエンコーダでエンコードされた文字列かどうかの判定に用います。
     * @param encoder エンコーダ。オブジェクトを文字列に変換する関数。
     * @param selector セレクタ。ある文字列がこのルールでデコード可能か判断する関数。
     * @param decoder デコーダ。文字列からオブジェクトを復元する関数。
     */
    public MessageConversionRule(Function<E, String> encoder, Predicate<String> selector, Function<String, E> decoder){
        this.encoder = Objects.requireNonNull(encoder);
        this.selector = Objects.requireNonNull(selector);
        this.decoder = Objects.requireNonNull(decoder);
    }

    /**
     * 文字列変換ルールを作成するためのビルダークラス。
     * @param <E> 対象の型
     */
    public static class Builder<E>{
        private Function<E, String> encoder;
        private Predicate<String> selector;
        private Function<String, E> decoder;

        /**
         * エンコーダをビルダに設定します。
         * @param encoder エンコーダ
         * @return このビルダの参照
         */
        public Builder<E> setEncoder(Function<E, String> encoder){
            this.encoder = encoder;
            return this;
        }

        /**
         * セレクタをビルダに設定します。
         * @param selector セレクタ
         * @return このビルダの参照
         */
        public Builder<E> setSelector(Predicate<String> selector){
            this.selector = selector;
            return this;
        }

        /**
         * デコーダをビルダに設定します。
         * @param decoder デコーダ
         * @return このビルダの参照
         */
        public Builder<E> setDecoder(Function<String, E> decoder){
            this.decoder = decoder;
            return this;
        }

        /**
         * ビルダから文字列変換ルールを生成します。
         * @return 変換ルール
         */
        public MessageConversionRule<E> build(){
            return new MessageConversionRule<>(encoder, selector, decoder);
        }
    }

    /**
     * エンコーダを取得します。
     * @return エンコーダ。
     */
    Function<E, String> getEncoder(){
        return encoder;
    }

    /**
     * セレクタを取得します。
     * @return セレクタ。
     */
    Predicate<String> getSelector(){
        return selector;
    }

    /**
     * デコーダを取得します。
     * @return デコーダ。
     */
    Function<String, E> getDecoder(){
        return decoder;
    }

}
