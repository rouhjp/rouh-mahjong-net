package jp.rouh.mahjong.score;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 環境ベース通常役クラス。
 * <p>通常役{@link BasicHandType}のうち,
 * 手牌の形に一切影響を受けず, 和了時の状況によって評価される役を表します。
 * <p>該当する役は以下の通りです。
 * <ul>
 *   <li>立直</li>
 *   <li>両立直</li>
 *   <li>一発</li>
 *   <li>門前清自摸和</li>
 *   <li>海底摸月</li>
 *   <li>河底撈魚</li>
 *   <li>嶺上開花</li>
 *   <li>槍槓</li>
 * </ul>
 * @author Rouh
 * @version 1.0
 */
enum EnvironmentBasedHandType implements BasicHandType{

    /**
     * 立直
     */
    READY("立直", 1){
        @Override
        boolean test(WinningContext context){
            return context.isReady();
        }
    },

    /**
     * 両立直
     */
    DOUBLE_READY("両立直", 2){
        @Override
        boolean test(WinningContext context){
            return context.isFirstAroundReady();
        }
    },

    /**
     * 一発
     */
    ONE_SHOT("一発", 1){
        @Override
        boolean test(WinningContext context){
            return context.isReadyAroundWin();
        }
    },

    /**
     * 門前清自摸和
     */
    SELF_PICK("門前清自摸和", 1){
        @Override
        boolean test(WinningContext context){
            return context.isSelfMade() && context.isTsumo();
        }
    },

    /**
     * 海底撈月
     */
    LAST_TILE_DRAW("海底摸月", 1){
        @Override
        boolean test(WinningContext context){
            return context.isLastTileDrawWin();
        }
    },

    /**
     * 河底撈魚
     */
    LAST_TILE_GRAB("河底撈魚", 1){
        @Override
        boolean test(WinningContext context){
            return context.isLastTileGrabWin();
        }
    },

    /**
     * 嶺上開花
     */
    QUAD_DRAW("嶺上開花", 1){
        @Override
        boolean test(WinningContext context){
            return context.isQuadTileDrawWin();
        }
    },

    /**
     * 槍槓
     */
    QUAD_GRAB("槍槓", 1){
        @Override
        boolean test(WinningContext context){
            return context.isQuadTileGrabWin();
        }
    };

    private final String text;
    private final int doubles;

    EnvironmentBasedHandType(String text, int doubles){
        this.text = text;
        this.doubles = doubles;
    }

    /**
     * 適合する環境ベース通常役をすべてリスト形式で取得する。
     * @param context 和了状況
     * @return 通常役のリスト
     */
    static List<BasicHandType> testAll(WinningContext context){
        return Stream.of(values()).filter(handType -> handType.test(context))
                .collect(Collectors.<BasicHandType>toList());
    }

    /**
     * 与えられた和了状況でこの環境ベース通常役が成立するか検査します。
     * @param context 和了状況
     * @return true 役が成立する場合
     *         false 役が成立しない場合
     */
    abstract boolean test(WinningContext context);

    @Override
    public String getText(){
        return text;
    }

    @Override
    public int getDoubles(){
        return doubles;
    }

    @Override
    public boolean isLimit(){
        return false;
    }
}
