package com.hushaorui.common;

import java.util.HashSet;
import java.util.Set;

/**
 * 双值
 * @param <T1> 值1
 * @param <T2> 值2
 */
public class TwinsValue<T1, T2> {
    private T1 value1;
    private T2 value2;

    public TwinsValue(T1 value1, T2 value2) {
        this.value1 =value1;
        this.value2 = value2;
    }
    public TwinsValue() {}

    public T1 getValue1() {
        return value1;
    }

    public void setValue1(T1 value1) {
        this.value1 = value1;
    }

    public T2 getValue2() {
        return value2;
    }

    public void setValue2(T2 value2) {
        this.value2 = value2;
    }

    @Override
    public int hashCode() {
        if (value1 == null) {
            if (value2 == null) {
                return 0;
            } else {
                return value2.hashCode();
            }
        } else {
            if (value2 == null) {
                return value1.hashCode();
            } else {
                return value1.hashCode() * value2.hashCode() + value2.hashCode();
            }
        }
    }

    /*public static void main(String[] args) {
        TwinsValue<Integer, Integer> v1 = new TwinsValue<>(0, 100);
        TwinsValue<Integer, Integer> v2 = new TwinsValue<>(100, 0);
        TwinsValue<Integer, Integer> v3 = new TwinsValue<>(100, 0);
        System.out.println(v1.hashCode() == v2.hashCode());
        System.out.println(v1.equals(v2));
        Set<TwinsValue<Integer, Integer>> hashSet = new HashSet<>();
        hashSet.add(v1);
        hashSet.add(v2);
        hashSet.add(v3);
        System.out.println(hashSet.size());
    }*/

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof TwinsValue)) {
            return false;
        }
        TwinsValue TwinsValue = (TwinsValue) obj;
        if (! this.value1.equals(TwinsValue.getValue1())) {
            return false;
        }
        return this.value2.equals(TwinsValue.getValue2());
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", value1, value2);
    }
}
