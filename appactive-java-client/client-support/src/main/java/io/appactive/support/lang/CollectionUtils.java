package io.appactive.support.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * copy from org.springframework.util.CollectionUtils
 */
public class CollectionUtils {

    /**
     * 将 list 转换为 map
     *
     * @param list          待转化的 list
     * @return
     */
    public static <K, O> Map<K, O> toMap(List<O> list, Function<O, K> keyConverter) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }

        Map<K, O> map = new HashMap<K, O>();
        for (O o : list) {
            map.put(keyConverter.apply(o), o);
        }
        return map;
    }


    /**
     * Return {@code true} if the supplied Collection is {@code null}
     * or empty. Otherwise, return {@code false}.
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null}
     * or empty. Otherwise, return {@code false}.
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }


    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * 将多个 EnumSet 结合在一起
     *
     * @param enumSets
     * @return
     */
    public static <O extends Enum<O>> EnumSet<O> union(Class<O> clazz, EnumSet<O>... enumSets) {
        if (null == enumSets) {
            return null;
        }
        EnumSet<O> result = EnumSet.noneOf(clazz);
        for (EnumSet<O> enumSet : enumSets) {
            result.addAll(enumSet);
        }
        return result;
    }

    public static boolean isEqualCollection(final Collection<?> a, final Collection<?> b) {
        if (a == null || b == null) {
            return a == b;
        }
        if(a.size() != b.size()) {
            return false;
        }
        final CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);
        if(helper.cardinalityA.size() != helper.cardinalityB.size()) {
            return false;
        }
        for( final Object obj : helper.cardinalityA.keySet()) {
            if(helper.freqA(obj) != helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }

    public static <O> Map<O, Integer> getCardinalityMap(final Iterable<? extends O> coll) {
        final Map<O, Integer> count = new HashMap<O, Integer>();
        for (final O obj : coll) {
            final Integer c = count.get(obj);
            if (c == null) {
                count.put(obj, Integer.valueOf(1));
            } else {
                count.put(obj, Integer.valueOf(c.intValue() + 1));
            }
        }
        return count;
    }

    private static class CardinalityHelper<O> {

        /** Contains the cardinality for each object in collection A. */
        final Map<O, Integer> cardinalityA;

        /** Contains the cardinality for each object in collection B. */
        final Map<O, Integer> cardinalityB;

        /**
         * Create a new CardinalityHelper for two collections.
         * @param a  the first collection
         * @param b  the second collection
         */
        public CardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
            cardinalityA = CollectionUtils.getCardinalityMap(a);
            cardinalityB = CollectionUtils.getCardinalityMap(b);
        }

        /**
         * Returns the maximum frequency of an object.
         * @param obj  the object
         * @return the maximum frequency of the object
         */
        public final int max(final Object obj) {
            return Math.max(freqA(obj), freqB(obj));
        }

        /**
         * Returns the minimum frequency of an object.
         * @param obj  the object
         * @return the minimum frequency of the object
         */
        public final int min(final Object obj) {
            return Math.min(freqA(obj), freqB(obj));
        }

        /**
         * Returns the frequency of this object in collection A.
         * @param obj  the object
         * @return the frequency of the object in collection A
         */
        public int freqA(final Object obj) {
            return getFreq(obj, cardinalityA);
        }

        /**
         * Returns the frequency of this object in collection B.
         * @param obj  the object
         * @return the frequency of the object in collection B
         */
        public int freqB(final Object obj) {
            return getFreq(obj, cardinalityB);
        }

        private final int getFreq(final Object obj, final Map<?, Integer> freqMap) {
            final Integer count = freqMap.get(obj);
            if (count != null) {
                return count.intValue();
            }
            return 0;
        }
    }



}
