package com.hushaorui.common;

import java.util.ListIterator;
import java.util.Stack;

/**
 * 安全的栈，不需要考虑空栈的情况
 * @param <T>
 */
public class SafeStack<T> extends Stack<T> {

    @Override
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        return super.pop();
    }

    public T pop(T defaultValue) {
        if (isEmpty()) {
            return defaultValue;
        }
        return super.pop();
    }

    /** 按条件弹出最后一个数据，不符合条件则不弹出 */
    public T popIf(CommonFilter<T> filter) {
        T peek = peek();
        if (peek == null) {
            return null;
        } else if (filter.check(peek)) {
            return pop();
        } else {
            return null;
        }
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return super.peek();
    }

    /** 从最后一个开始寻找，直到寻找到符合条件的那一个数据 */
    public T peekUntil(CommonFilter<T> filter) {
        ListIterator<T> listIterator = this.listIterator(size());
        while (listIterator.hasPrevious()) {
            T previous = listIterator.previous();
            if (filter.check(previous)) {
                return previous;
            }
        }
        return null;
    }

    /** 从最后一个开始弹出，直到符合条件的那一个数据 最后一个符合条件的也弹出，并返回 */
    public T popUntil(CommonFilter<T> filter) {
        while (! this.isEmpty()) {
            T pop = pop();
            if (filter.check(pop)) {
                return pop;
            }
        }
        return null;
    }

    /** 获取最后一个数据，如果栈为空，则返回默认值 */
    public T peek(T defaultValue) {
        if (isEmpty()) {
            return defaultValue;
        }
        return super.peek();
    }

    /**
     * 所有栈弹出一次
     * @param safeStacks 栈
     */
    public static void popStack(SafeStack<?>... safeStacks) {
        if (safeStacks == null) {
            return;
        }
        for (SafeStack<?> safeStack : safeStacks) {
            safeStack.pop();
        }
    }

    /**
     * 所有的栈弹出固定的次数
     * @param count 次数
     * @param safeStacks 栈
     */
    public static void popStack(int count, SafeStack<?>... safeStacks) {
        if (safeStacks == null) {
            return;
        }
        for (SafeStack<?> safeStack : safeStacks) {
            for (int i = 0; i < count; i++) {
                safeStack.pop();
            }
        }
    }
}
