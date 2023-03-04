package jp.rouh.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * リスト操作のユーティリティクラス。
 *
 * @author Rouh
 * @version 1.0
 */
@SuppressWarnings("unused")
public final class Lists {
    private Lists(){
        throw new AssertionError("instantiate utility class");
    }

    /**
     * リストと要素を結合し、新しいリストを取得します。
     *
     * @param list リスト
     * @param element 要素
     * @return 結合したリスト
     * @param <E> 要素の型
     * @throws NullPointerException 引数に{@code null}値が含まれる場合
     */
    public static <E> List<E> added(List<? extends E> list, E element){
        Objects.requireNonNull(list);
        var newList = new ArrayList<E>(list.size() + 1);
        newList.addAll(list);
        newList.add(element);
        return newList;
    }

    /**
     * リストとリストを結合し、新しいリストを取得します。
     *
     * @param a 1つ目のリスト
     * @param b 2つ目のリスト
     * @return 結合したリスト
     * @param <E> 要素の型
     * @throws NullPointerException 引数に{@code null}値が含まれる場合
     */
    public static <E> List<E> addedAll(List<? extends E> a, List<? extends E> b){
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        var list = new ArrayList<E>(a.size() + b.size());
        list.addAll(a);
        list.addAll(b);
        return list;
    }

    /**
     * リストから指定の要素を削除し、新しいリストを取得します。
     *
     * @param list リスト
     * @param element 要素
     * @return 削除したリスト
     * @param <E> 要素の型
     */
    public static <E> List<E> removed(List<? extends E> list, E element){
        Objects.requireNonNull(list);
        var newList = new ArrayList<E>(list);
        newList.remove(element);
        return newList;
    }

    /**
     * リストから指定のインデックスの要素を削除し、新しいリストを取得します。
     *
     * @param list リスト
     * @param index インデックス
     * @return 削除したリスト
     * @param <E> 要素の型
     */
    public static <E> List<E> removed(List<? extends E> list, int index){
        Objects.requireNonNull(list);
        var newList = new ArrayList<E>(list);
        newList.remove(index);
        return newList;
    }

    /**
     * リストから削除対象の要素を全て削除し、新しいリストを取得します。
     *
     * @param list リスト
     * @param elements 削除対象の要素のリスト
     * @return 削除したリスト
     * @param <E> 要素の型
     */
    public static <E> List<E> removedAll(List<? extends E> list, List<E> elements){
        Objects.requireNonNull(list);
        var newList = new ArrayList<E>(list);
        newList.removeAll(elements);
        return newList;
    }

    /**
     * リストから削除対象の要素を重複を考慮してひとつずつ削除し、新しいリストを取得します。
     *
     * @param list リスト
     * @param elements 要素のリスト
     * @return 削除したリスト
     * @param <E> 要素の型
     */
    public static <E> List<E> removedEach(List<? extends E> list, List<E> elements){
        Objects.requireNonNull(list);
        Objects.requireNonNull(elements);
        var newList = new ArrayList<E>(list);
        elements.forEach(newList::remove);
        return newList;
    }

    /**
     * リストから指定の叙述関数に適合する要素を全て削除し、新しいリストを取得します。
     *
     * @param list リスト
     * @param filter 叙述関数
     * @return 削除したリスト
     * @param <E> 要素の型
     */
    public static <E> List<E> removedIf(List<? extends E> list, Predicate<? super E> filter){
        Objects.requireNonNull(list);
        Objects.requireNonNull(filter);
        var newList = new ArrayList<E>(list);
        newList.removeIf(filter);
        return newList;
    }

    /**
     * リストの隣接する要素を順に叙述関数に渡し、適合した箇所を分割します。
     *
     * @param list リスト
     * @param condition 叙述関数
     * @return 分割したリスト
     * @param <E> 要素の型
     */
    public static <E> List<List<E>> split(List<? extends E> list, BiPredicate<? super E, ? super E> condition){
        Objects.requireNonNull(list);
        Objects.requireNonNull(condition);
        for (int i = 0; i<list.size() - 1; i++){
            var left = list.get(i);
            var right = list.get(i + 1);
            if (condition.test(left, right)){
                var sections = new ArrayList<List<E>>();
                sections.add(List.copyOf(list.subList(0, i + 1)));
                sections.addAll(split(list.subList(i + 1, list.size()), condition));
                return List.copyOf(sections);
            }
        }
        return List.of(List.copyOf(list));
    }

    /**
     * リストが対象の要素を全て含むか、重複を考慮してひとつずつ検査します。
     * @param list リスト
     * @param elements 要素のリスト
     * @return true 全て含む場合
     * @param <E> 要素の型
     */
    public static <E> boolean containsEach(List<? extends E> list, List<E> elements){
        var tmpList = new ArrayList<>(list);
        return elements.stream().allMatch(tmpList::remove);
    }

    /**
     * リストから対象の要素の数を取得します。
     * @param list リスト
     * @param target 要素
     * @return 要素の数
     * @param <E> 要素の型
     */
    public static <E> int count(List<? extends E> list, E target){
        Objects.requireNonNull(target);
        return countIf(list, target::equals);
    }

    /**
     * リストから叙述関数に適合する要素の数を取得します。
     * @param list リスト
     * @param predicate 叙述関数
     * @return 要素の数
     * @param <E> 要素の型
     */
    public static <E> int countIf(List<? extends E> list, Predicate<? super E> predicate){
        Objects.requireNonNull(list);
        Objects.requireNonNull(predicate);
        return (int)list.stream().filter(predicate).count();
    }

    /**
     * リスト中に含まれる叙述関数に適合する最初の要素のインデックスを取得します。
     * @param list リスト
     * @param predicate 叙述関数
     * @return インデックス
     *         -1 要素が見つからない場合
     * @param <E> リストの要素の型
     * @throws NullPointerException 引数に{@code null}値が含まれる場合
     */
    public static <E> int indexOf(List<? extends E> list, Predicate<? super E> predicate){
        Objects.requireNonNull(list);
        Objects.requireNonNull(predicate);
        for(int i = 0; i<list.size(); i++){
            if(predicate.test(list.get(i))){
                return i;
            }
        }
        return -1;
    }

    /**
     * リストから全通りの組み合わせのリストを作成します。
     * <p>例えば、[A, B, C]のリストの組み合わせは
     * [[A], [B], [A, B], [C], [A, C], [B, C], [A, B, C]] となります。
     * @param list リスト
     * @return 組み合わせのリスト
     * @param <E> 要素の型
     * @throws NullPointerException 引数に{@code null}値が含まれる場合
     * @throws IllegalArgumentException リストの要素数が16以上の場合
     */
    public static <E> List<List<E>> combinationsOf(List<? extends E> list){
        Objects.requireNonNull(list);
        if(list.isEmpty()) return Collections.emptyList();
        if(list.size()>=16) throw new IllegalArgumentException("list is too large: size="+list.size());
        var combinations = new ArrayList<List<E>>();
        for(int n = 1; n<Math.pow(2, list.size()); n++){
            var combination = new ArrayList<E>();
            for(int i = 0, b = n; i<list.size(); i++){
                if(b%2==1) combination.add(list.get(i));
                b = b >> 1;
            }
            combinations.add(combination);
        }
        return combinations;
    }

    /**
     * リストから指定の長さの組み合わせのリストを作成します。
     * <p>例えば、長さ2の[A, B, C]のリストの組み合わせは
     * [[A, B], [A, C], [B, C]] となります。
     * @param list リスト
     * @return 組み合わせのリスト
     * @param <E> 要素の型
     * @throws NullPointerException 引数に{@code null}値が含まれる場合
     * @throws IllegalArgumentException リストの要素数が16以上の場合、組み合わせの長さが負の値の場合
     */
    public static <E> List<List<E>> combinationsOf(List<? extends E> list, int combinationSize){
        Objects.requireNonNull(list);
        if (combinationSize<0) throw new IllegalArgumentException("invalid combination size: size="+combinationSize);
        if(list.isEmpty()) return Collections.emptyList();
        if(combinationSize==0 || combinationSize>list.size()) return Collections.emptyList();
        if(list.size()>=16) throw new IllegalArgumentException("list is too large: size="+list.size());
        var combinations = new ArrayList<List<E>>();
        int[] indexes = new int[combinationSize];
        n: for(int n = 1; n<Math.pow(2, list.size()); n++){
            int c = 0;
            for(int i = 0, b = n; i<list.size(); i++){
                if(b%2==1){
                    if(c>=combinationSize) continue n;
                    indexes[c++] = i;
                }
                b = b >> 1;
            }
            if(c==combinationSize){
                var combination = new ArrayList<E>(combinationSize);
                for(int i = 0; i<combinationSize; i++){
                    combination.add(list.get(indexes[i]));
                }
                combinations.add(combination);
            }
        }
        return combinations;
    }

    private static <E> Collector<E, ?, List<List<E>>> foldCollector(Function<List<E>, List<List<E>>> function){
        return Collector.of(ArrayList::new, List::add, Lists::addedAll, function);
    }

    /**
     * 全ての要素数の組み合わせのリストに集約するコレクターを取得します。
     * @param <E> 要素の型
     * @return 要素の組み合わせのリスト
     * @throws IllegalArgumentException リストの要素数が16以上の場合
     */
    public static <E> Collector<E, ?, List<List<E>>> toCombinations(){
        return foldCollector(Lists::combinationsOf);
    }

    /**
     * 指定の要素数の組み合わせのリストに集約するコレクターを取得します。
     * @param size 要素数
     * @param <E> 要素の型
     * @return 要素の組み合わせのリスト
     * @throws IllegalArgumentException リストの要素数が16以上の場合、組み合わせの長さが負の値の場合
     */
    public static <E> Collector<E, ?, List<List<E>>> toCombinations(int size){
        return foldCollector(list->Lists.combinationsOf(list, size));
    }

}
