package jp.rouh.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ListsTest {

    @Test
    void testAdded(){
        var list = List.of('B', 'C', 'D');
        var element = 'A';
        var result = Lists.added(list, element);
        var expected = List.of('B', 'C', 'D', 'A');
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testAddedAll(){
        var list1 = List.of('B', 'C', 'D');
        var list2 = List.of('A', 'B', 'C');
        var result = Lists.addedAll(list1, list2);
        var expected = List.of('B', 'C', 'D', 'A', 'B', 'C');
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testRemoved(){
        var list = List.of('B', 'D', 'D');
        var element = 'D';
        var result = Lists.removed(list, (Character)element);
        var expected = List.of('B', 'D');
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testRemovedIndex(){
        var list = List.of('B', 'D', 'D');
        int index = 0;
        var result = Lists.removed(list, index);
        var expected = List.of('D', 'D');
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testRemovedAll(){
        var list = List.of('A', 'B', 'D', 'D');
        var elements = List.of('A', 'D');
        var result = Lists.removedAll(list, elements);
        var expected = List.of('B');
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testRemovedEach(){
        var list = List.of('A', 'B', 'D', 'D');
        var elements = List.of('A', 'D');
        var result = Lists.removedEach(list, elements);
        var expected = List.of('B', 'D');
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testRemovedIf(){
        var list = List.of('A', 'b', 'c', 'D');
        var result = Lists.removedIf(list, Character::isUpperCase);
        var expected = List.of('b', 'c');
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSplit(){
        var list = List.of('A', 'B', 'D', 'E');
        var result = Lists.split(list, (l, r)-> (r - l >1));
        var expected = List.of(List.of('A', 'B'), List.of('D', 'E'));
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testContainsEach(){
        var list1 = List.of('A', 'B', 'C', 'D');
        var list2 = List.of('A', 'B', 'C', 'C');
        var elements = List.of('B', 'C', 'C');
        Assertions.assertFalse(Lists.containsEach(list1, elements));
        Assertions.assertTrue(Lists.containsEach(list2, elements));
    }

    @Test
    void testCount(){
        var list = List.of('B', 'C', 'C', 'D', 'C');
        Assertions.assertEquals(3, Lists.count(list, 'C'));
    }

    @Test
    void testCountIf(){
        var list = List.of('A', 'b', 'C', 'd', 'E');
        Assertions.assertEquals(3, Lists.countIf(list, Character::isUpperCase));
    }

    @Test
    void testIndexOf(){
        var list1 = List.of('a', 'B', 'c', 'D', 'e');
        var list2 = List.of('a', 'b', 'c', 'd', 'e');
        Assertions.assertEquals(1, Lists.indexOf(list1, Character::isUpperCase));
        Assertions.assertEquals(-1, Lists.indexOf(list2, Character::isUpperCase));
    }

    @Test
    void testCombinationsOf(){
        var list = List.of('A', 'B', 'C');
        var result = Lists.combinationsOf(list);
        Assertions.assertEquals(7, result.size());
        Assertions.assertTrue(result.contains(List.of('A')));
        Assertions.assertTrue(result.contains(List.of('B')));
        Assertions.assertTrue(result.contains(List.of('C')));
        Assertions.assertTrue(result.contains(List.of('A', 'B')));
        Assertions.assertTrue(result.contains(List.of('A', 'C')));
        Assertions.assertTrue(result.contains(List.of('B', 'C')));
        Assertions.assertTrue(result.contains(List.of('A', 'B', 'C')));
    }

    @Test
    void testCombinationsOfSize(){
        var list = List.of('A', 'B', 'C');
        var result = Lists.combinationsOf(list, 2);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.contains(List.of('A', 'B')));
        Assertions.assertTrue(result.contains(List.of('A', 'C')));
        Assertions.assertTrue(result.contains(List.of('B', 'C')));
    }

    @Test
    void testToCombinations(){
        var list = List.of('A', 'B', 'C');
        var result = list.stream().collect(Lists.toCombinations());
        var expected = Lists.combinationsOf(list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testToCombinationsSize(){
        var list = List.of('A', 'B', 'C');
        var result = list.stream().collect(Lists.toCombinations(2));
        var expected = Lists.combinationsOf(list, 2);
        Assertions.assertEquals(expected, result);
    }

}
