package com.doctordark.util;

import com.google.common.collect.Iterables;
import java.util.Comparator;
import com.google.common.collect.Ordering;
import java.util.List;
import java.util.Map;
import com.google.common.base.Function;

public class MapSorting
{
    private static final Function EXTRACT_KEY;
    private static final Function EXTRACT_VALUE;
    
    public static <T, V extends Comparable<V>> List<Map.Entry<T, V>> sortedValues(final Map<T, V> map) {
        return MapSorting.<T, V>sortedValues(map, (Comparator<V>)Ordering.natural());
    }
    
    public static <T, V> List<Map.Entry<T, V>> sortedValues(final Map<T, V> map, final Comparator<V> valueComparator) {
        return (List<Map.Entry<T, V>>)Ordering.from((Comparator)valueComparator).onResultOf((Function)MapSorting.<Object, Object>extractValue()).sortedCopy((Iterable)map.entrySet());
    }
    
    public static <T, V> Iterable<T> keys(final List<Map.Entry<T, V>> entryList) {
        return (Iterable<T>)Iterables.transform((Iterable)entryList, (Function)MapSorting.<Object, Object>extractKey());
    }
    
    public static <T, V> Iterable<V> values(final List<Map.Entry<T, V>> entryList) {
        return (Iterable<V>)Iterables.transform((Iterable)entryList, (Function)MapSorting.<Object, Object>extractValue());
    }
    
    private static <T, V> Function<Map.Entry<T, V>, T> extractKey() {
        return (Function<Map.Entry<T, V>, T>)MapSorting.EXTRACT_KEY;
    }
    
    private static <T, V> Function<Map.Entry<T, V>, V> extractValue() {
        return (Function<Map.Entry<T, V>, V>)MapSorting.EXTRACT_VALUE;
    }
    
    static {
        EXTRACT_KEY = (Function)new Function<Map.Entry<Object, Object>, Object>() {
            public Object apply(final Map.Entry<Object, Object> input) {
                return (input == null) ? null : input.getKey();
            }
        };
        EXTRACT_VALUE = (Function)new Function<Map.Entry<Object, Object>, Object>() {
            public Object apply(final Map.Entry<Object, Object> input) {
                return (input == null) ? null : input.getValue();
            }
        };
    }
}
