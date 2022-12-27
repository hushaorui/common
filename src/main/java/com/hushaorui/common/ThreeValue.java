package com.hushaorui.common;

/**
 * 双值
 * @param <T1> 值1
 * @param <T2> 值2
 */
public class ThreeValue<T1, T2, T3> {
    private T1 value1;
    private T2 value2;
    private T3 value3;

    public ThreeValue(T1 value1, T2 value2, T3 value3) {
        this.value1 =value1;
        this.value2 = value2;
        this.value3 = value3;
    }
    public ThreeValue() {}

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

    public T3 getValue3() {
        return value3;
    }

    public void setValue3(T3 value3) {
        this.value3 = value3;
    }

    @Override
    public int hashCode() {
        if (value1 == null) {
            if (value2 == null) {
                if (value3 == null) {
                    return 0;
                } else {
                    return value3.hashCode();
                }
            } else {
                if (value3 == null) {
                    return value2.hashCode();
                } else {
                    return value2.hashCode() + value3.hashCode();
                }
            }
        } else {
            if (value2 == null) {
                if (value3 == null) {
                    return value1.hashCode();
                } else {
                    return value1.hashCode() + value3.hashCode();
                }
            } else {
                if (value3 == null) {
                    return value1.hashCode() + value2.hashCode();
                } else {
                    return value1.hashCode() + value2.hashCode() + value3.hashCode();
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof ThreeValue)) {
            return false;
        }
        ThreeValue threeValue = (ThreeValue) obj;
        if (! this.value1.equals(threeValue.getValue1())) {
            return false;
        }
        if (! this.value2.equals(threeValue.getValue2())) {
            return false;
        }
        return this.value3.equals(threeValue.getValue3());
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", value1, value2, value3);
    }
}
