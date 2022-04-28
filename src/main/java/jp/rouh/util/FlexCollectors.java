package jp.rouh.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class FlexCollectors{

    /**
     * 指定の要素数の組み合わせのリストに集約するコレクターを取得します。
     * @param size 要素数
     * @param <E> 要素の型
     * @return 要素の組み合わせのリスト
     */
    public static <E> Collector<E, ?, List<List<E>>> toCombinations(int size){
        return Collector.<E, ArrayList<E>, List<List<E>>>of(
                ArrayList::new,
                List::add,
                (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                },
                list->combinationOf(list, size)
        );
    }

    /**
     * 全ての要素数の組み合わせのリストに集約するコレクターを取得します。
     * @param <E> 要素の型
     * @return 要素の組み合わせのリスト
     */
    public static <E> Collector<E, ?, List<List<E>>> toCombinations(){
        return Collector.<E, ArrayList<E>, List<List<E>>>of(
                ArrayList::new,
                List::add,
                (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                },
                FlexCollectors::combinationOf
        );
    }

    private static <E> List<List<E>> combinationOf(List<E> list){
        Objects.requireNonNull(list);
        if(list.isEmpty()) return Collections.emptyList();
        if(list.size()>15) throw new IllegalArgumentException("list is too large: size="+list.size());
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

    private static <E> List<List<E>> combinationOf(List<E> list, int combinationSize){
        Objects.requireNonNull(list);
        if(list.isEmpty()) return Collections.emptyList();
        if(combinationSize<=0 || combinationSize>list.size()) return Collections.emptyList();
        if(list.size()>15) throw new IllegalArgumentException("list is too large: size="+list.size());
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

    public static void main(String[] args){
        Stream.of("123", "456", "555", "789")
                .collect(toCombinations(3))
                .forEach(System.out::println);
    }
}
