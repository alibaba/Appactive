package io.appactive.support.lang.number;

public interface NumComparator<T extends Number> {

    /**
     * 是否符合预期
     * 
     * @param value1
     * @param value2
     * @return
     */
    boolean isMatched(T value1, T value2);

    /**
     * 期望:[{0} {1} {2}]，实际:[{3} {4} {5}]
     * 
     * @param name1
     * @param value1
     * @param name2
     * @param value2
     * @return
     */
    Object[] params(String name1, T value1, String name2, T value2);
}
