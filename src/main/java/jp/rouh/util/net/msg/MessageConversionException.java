package jp.rouh.util.net.msg;

/**
 * メッセージ変換に失敗したことを示す例外。
 * @see MessageConverter
 * @author Rouh
 * @version 1.0
 */
public class MessageConversionException extends RuntimeException{

    /**
     * メッセージを指定したコンストラクタ。
     * @param message メッセージ
     */
    public MessageConversionException(String message){
        super(message);
    }

    /**
     * メッセージと原因例外を指定したコンストラクタ。
     * @param message メッセージ
     * @param cause 原因例外
     */
    public MessageConversionException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * 原因例外を指定したコンストラクタ。
     * @param cause 原因例外
     */
    public MessageConversionException(Throwable cause){
        super(cause);
    }
}
