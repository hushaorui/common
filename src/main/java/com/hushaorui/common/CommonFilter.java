package com.hushaorui.common;

/**
 * 泛型过滤器
 * @param <OBJ> 对象类型
 */
@FunctionalInterface
public interface CommonFilter<OBJ> {

    boolean check(OBJ obj);
}
