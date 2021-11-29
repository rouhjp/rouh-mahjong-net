package jp.rouh.util;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * 拡張的なシンタックスを提供するリストクラス
 *
 * <p>このクラスの実装は{@link java.util.ArrayList}です。
 * @param <E> 要素の型
 * @author Rouh
 * @version 1.0
 */
public class FlexList<E> extends ArrayList<E>{

    /**
     * 空のリストを作成します。
     */
    public FlexList(){
        super();
    }

    /**
     * 指定された初期容量で空のリストを作成します。
     * @param initialCapacity 初期容量
     */
    public FlexList(int initialCapacity){
        super(initialCapacity);
    }

    /**
     * 指定されたコレクションの要素が含まれているリストを、要素がコレクションのイテレータによって返される順序で作成します。
     * @param collection コレクション
     */
    public FlexList(Collection<? extends E> collection){
        super(collection);
    }

    /**
     * このリストが指定した述語関数に適合する要素を含むかどうか検査します。
     * @param predicate 述語関数
     * @return true  適合する要素を含む場合
     *         false 適合する要素を含まない場合
     */
    public boolean contains(Predicate<? super E> predicate){
        return indexOf(predicate)!=-1;
    }

    /**
     * このリストが指定したコレクション中の重複を含む全ての要素を内包しているかどうか検査します。
     *
     * <p>{List#containsAll}と異なり, 与えられたコレクションに重複する要素がある場合
     * このリストも同数以上の重複した要素を含んでいなければ検査に適合しません。
     * @param collection 検査する要素のコレクション
     * @return true  内包している場合
     *         false 内包していない場合
     */
    public boolean containsWhole(Collection<? extends E> collection){
        var tmp = new FlexList<>(this);
        return collection.stream().allMatch(tmp::remove);
    }

    /**
     * 指定した述語関数に適合する最初の要素の位置を取得します。
     * @param predicate 述語関数
     * @return 適合する要素の位置
     *         -1 適合する要素が見つからなかった場合
     */
    public int indexOf(Predicate<? super E> predicate){
        Objects.requireNonNull(predicate);
        for(int i = 0; i<size(); i++){
            if(predicate.test(get(i))){
                return i;
            }
        }
        return -1;
    }

    /**
     * 指定した述語関数に適合する最後の要素の位置を取得します。
     * @param predicate 述語関数
     * @return 適合する要素の位置
     *         -1 適合する要素が見つからなかった場合
     */
    public int lastIndexOf(Predicate<? super E> predicate){
        Objects.requireNonNull(predicate);
        for(int i = size() - 1; i>=0; i--){
            if(predicate.test(get(i))){
                return i;
            }
        }
        return -1;
    }

    /**
     * 指定した述語関数に適合する要素の数を返します。
     * @param predicate 述語関数
     * @return 適合する要素数
     */
    public int countIf(Predicate<? super E> predicate){
        return (int)stream().filter(predicate).count();
    }

    /**
     * 重複を取り除いた後, 自身の参照を返します。
     * @return 重複を取り除いたリスト
     */
    public FlexList<E> distinct(){
        for(int i = 0; i<size(); i++){
            int lastIndex;
            if((lastIndex = lastIndexOf(get(i)))!=i){
                remove(lastIndex);
            }
        }
        return this;
    }

    /**
     * ソート処理{@link Collections#sort}を実施した後, 自身の参照を返します。
     * @param comparator 要素のコンパレータ
     * @return ソート済みリスト
     */
    public FlexList<E> sorted(Comparator<? super E> comparator){
        sort(comparator);
        return this;
    }

    /**
     * ソート処理{@link Collections#sort}を実施した後, 自身の参照を返します。
     * @throws ClassCastException 要素が{@link Comparable}インターフェースを実装していない場合
     * @return ソート済みリスト
     */
    public FlexList<E> sorted(){
        @SuppressWarnings("unchecked")
        var comparator = (Comparator<? super E>) Comparator.naturalOrder();
        return sorted(comparator);
    }

    /**
     * シャッフル処理{@link Collections#shuffle}を実施した後, 自身の参照を返します。
     * @return シャッフル済みリスト
     */
    public FlexList<E> shuffled(){
        Collections.shuffle(this);
        return this;
    }

    /**
     * シャッフル処理{@link Collections#shuffle}を実施した後, 自身の参照を返します。
     * @param rand シャッフルに用いる乱数オブジェクト
     * @return シャッフル済みリスト
     */
    public FlexList<E> shuffled(Random rand){
        Collections.shuffle(this, rand);
        return this;
    }

    /**
     * 追加処理{@link List#add}を実施した後, 自身の参照を返します。
     * @param element 追加する要素
     * @return 要素を追加したリスト
     */
    public FlexList<E> added(E element){
        add(element);
        return this;
    }

    /**
     * 追加処理{@link List#addAll}を実施した後, 自身の参照を返します。
     * @param collection 追加する要素のコレクション
     * @return 要素を追加したリスト
     */
    public FlexList<E> addedAll(Collection<? extends E> collection){
        addAll(collection);
        return this;
    }

    /**
     * 削除処理{@link List#remove}を実施した後, 自身の参照を返します。
     * @param element 削除する要素
     * @return 要素を削除したリスト
     */
    public FlexList<E> removed(E element){
        remove(element);
        return this;
    }

    /**
     * 削除処理{@link List#removeAll}を実施した後, 自身の参照を返します。
     * @param collection 削除する要素のコレクション
     * @return 要素を削除したリスト
     */
    public FlexList<E> removedAll(Collection<? extends E> collection){
        removeAll(collection);
        return this;
    }

    /**
     * 指定したコレクションの要素を一つずつ削除します。
     *
     * <p>{@link List#removeAll}とは異なり, このコレクションの要素に重複がある場合
     * 削除対象として引数に指定したコレクションに含まれる同じ要素の数だけ削除が実施されます。
     * @param collection 削除する要素のコレクション
     */
    public void removeEach(Collection<? extends E> collection){
        collection.forEach(this::remove);
    }

    /**
     * 削除処理{@link FlexList#removeEach}を実施した後, 自身の参照を返します。
     * @param collection 削除する溶損のコレクション
     * @return 要素を削除したリスト
     */
    public FlexList<E> removedEach(Collection<? extends E> collection){
        collection.forEach(this::remove);
        return this;
    }

    /**
     * このリストを不変リストに変換します。
     * @return 不変リスト
     */
    public List<E> toUnmodifiable(){
        return List.copyOf(this);
    }

    /**
     * このリストを分割します。
     *
     * <p>リストの先頭から順に隣接する2つの要素を与えられた二項述語関数で検査し
     * 検査に適合した箇所を境として分割します。
     * このメソッドを実施する前に, 任意の方法で要素をソートすることが想定されます。
     * @param separator 境となる2要素が与えられた場合のみtrueを返す二項述語関数
     * @return 分割した要素郡のリスト
     */
    public List<List<E>> separateByDiff(BiPredicate<? super E, ? super E> separator){
        if(size()==0) return List.of(List.of());
        if(size()==1) return List.of(List.of(get(0)));
        for(int i = 1; i<size(); i++){
            var left = get(i - 1);
            var right = get(i);
            if(separator.test(left, right)){
                var separated = new FlexList<List<E>>();
                separated.add(List.copyOf(subList(0, i)));
                separated.addAll(FlexList.copyOf(subList(i, size())).separateByDiff(separator));
                return separated;
            }
        }
        return List.of(List.copyOf(this));
    }


    /**
     * リストから, 全ての要素の組み合わせのリストを取得します。
     *
     * <p>例えば, このリストが["A", "B", "C"]の時, 全ての要素の組み合わせは
     * ["A"], ["B"], ["C"], ["A", "B"], ["A", "C"], ["B", "C"], ["A", "B", "C"] となります。
     * @return 全ての要素の組み合わせ
     */
    public List<List<E>> combination(){
        return IntStream.range(1, size()).mapToObj(this::combinationSizeOf)
                .flatMap(List::stream).collect(toList());
    }

    /**
     * リストから, 指定したサイズの全ての要素の組み合わせのリストを取得します。
     *
     * <p>例えば, このリストが["A", "B", "C"]の時, 長さ2の要素の組み合わせは
     * ["A", "B"], ["A", "C"], ["B", "C"] となります。
     * 必然的に, 全ての要素の組み合わせのリストの長さは,
     * リストサイズnと組み合わせのサイズmに対し, nCmとなり,
     * 残りの要素のリストの長さは n - nCm となります。
     * @param size 組み合わせのサイズ
     * @throws IndexOutOfBoundsException 与えられた組み合わせのサイズがリストの長さより大きい場合
     *                                   与えられた組み合わせのサイズが0または負の場合
     * @return 要素の組合わせ
     */
    public List<List<E>> combinationSizeOf(int size){
        if(size <= 0) throw new IndexOutOfBoundsException("combination size must be positive");
        if(size > size()) throw new IndexOutOfBoundsException("combination size out of list size");
        return new CombinationSupport(size, size()).getAllCombinationIndexes().stream()
                .map(indexes->indexes.stream().map(this::get).collect(toList())).collect(toList());
    }

    private static class CombinationSupport{
        private final int combinationSize;
        private final int collectionSize;
        private final List<List<Integer>> allCombinationIndexes;
        private CombinationSupport(int combinationSize, int collectionSize){
            this.combinationSize = combinationSize;
            this.collectionSize = collectionSize;
            this.allCombinationIndexes = new ArrayList<>();
            searchCombinationIndexes(new LinkedList<>());
        }
        private void searchCombinationIndexes(Deque<Integer> indexes){
            int initialIndex = indexes.isEmpty()?0:indexes.getLast() + 1;
            for(int i = initialIndex; i<collectionSize; i++){
                indexes.add(i);
                if(indexes.size()==combinationSize){
                    allCombinationIndexes.add(List.copyOf(indexes));
                }else{
                    searchCombinationIndexes(indexes);
                }
                indexes.removeLast();
            }
        }
        private List<List<Integer>> getAllCombinationIndexes(){
            return allCombinationIndexes;
        }
    }

    /**
     * 任意の数の要素を含むリストを返します。
     * @param elements 要素
     * @param <E> 要素の型
     * @return リスト
     */
    @SafeVarargs
    public static <E> FlexList<E> of(E...elements){
        var list = new FlexList<E>();
        if(elements.length>=1){
            list.addAll(Arrays.asList(elements));
        }
        return list;
    }

    /**
     * 指定されたコレクションが{@link FlexList}であればその参照を, そうでなければコンストラクタ
     * {@link FlexList#FlexList(Collection collection)}にて新たにリストを生成します。
     * @param collection コレクション
     * @return リスト
     */
    public static <E> FlexList<E> copyOf(Collection<E> collection){
        if(collection instanceof FlexList){
            return (FlexList<E>)collection;
        }
        return new FlexList<>(collection);
    }
}
