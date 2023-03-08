package jp.rouh.mahjong.score;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 整形済み手牌クラス。
 * <p>面子構成及び待ちを確定させた状態の手牌を表します。
 * <p>七対子形および国士無双形などの特殊形はこの型に適合しません。
 * @author Rouh
 * @version 1.0
 */
class FormattedHand{
    private final Head head;
    private final List<Meld> melds;
    private final Wait wait;

    /**
     * 整形済み手牌のコンストラクタ。
     * @param head  雀頭
     * @param melds 面子のリスト
     * @param wait  待ち
     * @throws IllegalArgumentException 面子の数が4でない場合
     */
    FormattedHand(Head head, List<Meld> melds, Wait wait){
        if(melds.size()!=4){
            throw new IllegalArgumentException("invalid melds: " + melds);
        }
        this.head = head;
        this.melds = melds;
        this.wait = wait;
    }

    /**
     * 雀頭を取得します。
     * @return 雀頭
     */
    Head getHead(){
        return head;
    }

    /**
     * 面子を取得します。
     * @return 面子
     */
    List<Meld> getMelds(){
        return melds;
    }

    /**
     * 雀頭と面子をまとめた手牌構成要素リストを取得します。
     * @return 手牌構成要素のリスト
     */
    List<HandComponent> getComponents(){
        var components = new ArrayList<HandComponent>();
        components.add(head);
        components.addAll(melds);
        return components;
    }

    /**
     * 待ちを取得します。
     * @return 待ち
     */
    Wait getWait(){
        return wait;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null || getClass()!=o.getClass()) return false;
        FormattedHand that = (FormattedHand)o;
        return Objects.equals(head, that.head) && Objects.equals(melds, that.melds) && wait==that.wait;
    }

    @Override
    public int hashCode(){
        return Objects.hash(head, melds, wait);
    }

    @Override
    public String toString(){
        var meldExpression = melds.stream()
                .map(meld->meld.isConcealed()?
                        meld.getTilesSorted().toString():
                        meld.getTilesSorted().toString().replace("[", "(").replace("]", ")"))
                .collect(Collectors.joining());
        return head+meldExpression+" "+wait;
    }
}
