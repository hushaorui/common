package com.hushaorui.common;

public class IntegerFlagContainer {

    public IntegerFlagContainer(int maxIndex) {
        if (maxIndex > 31) {
            throw new RuntimeException("code error, integer size must less than 32");
        }
        this.maxIndex = maxIndex;
    }
    private int flag;
    private int maxIndex;
    /** 改变 */
    public void update(int index) {
        if (index < 0 || index > maxIndex) {
            throw new RuntimeException("code error, index must between 0 and " + maxIndex);
        }
        flag |= 1 << index;
    }
    public boolean update() {
        return flag > 0;
    }

    /** 还原 */
    public void revert(int index) {
        if (index < 0 || index > maxIndex) {
            throw new RuntimeException("code error, index must between 0 and " + maxIndex);
        }
        if (isUpdate(index)) {
            flag -= (1 << index);
        }
    }
    /** 是否改变 */
    public boolean isUpdate(int index) {
        if (index < 0 || index > maxIndex) {
            throw new RuntimeException("code error, index must between 0 and " + maxIndex);
        }
        return (flag >> index & 1) == 1;
    }
    public String toString() {
        return Integer.toBinaryString(flag);
    }
    public String toBooleanString() {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i <= maxIndex; i++) {
            builder.append(i).append("=").append(isUpdate(i));
            if (i < maxIndex) {
                builder.append(" ,");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /*public static void main(String[] args) {
        IntegerFlagContainer flagContainer = new IntegerFlagContainer(31);
        flagContainer.update(10);
        System.out.println(flagContainer.toString());
        System.out.println(flagContainer.toBooleanString());
        flagContainer.update(11);
        System.out.println(flagContainer.toString());
        System.out.println(flagContainer.toBooleanString());
        flagContainer.revert(10);
        System.out.println(flagContainer.toString());
        System.out.println(flagContainer.toBooleanString());
        flagContainer.update(3);
        System.out.println(flagContainer.toString());
        System.out.println(flagContainer.toBooleanString());
    }*/
}
