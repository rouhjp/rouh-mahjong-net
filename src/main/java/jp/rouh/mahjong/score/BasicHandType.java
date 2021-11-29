package jp.rouh.mahjong.score;

/**
 * 通常役インターフェース。
 * <p>役{@link HandType}のうち, 役満役{@link LimitHandType}以外の役を表します。
 * @author Rouh
 * @version 1.0
 */
public interface BasicHandType extends HandType{

    /**
     * 役の飜数を取得します。
     * @return 飜
     */
    int getDoubles();

    @Override
    default boolean isLimit(){
        return false;
    }

    @Override
    default HandTypeGrade getGrade(){
        return HandTypeGrade.ofDoubles(getDoubles());
    }
}
