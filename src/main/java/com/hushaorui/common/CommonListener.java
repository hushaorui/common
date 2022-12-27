package com.hushaorui.common;

/**
 * 泛型过滤器
 * @param <OBJ> 对象类型
 */
@FunctionalInterface
public interface CommonListener<OBJ, RESULT> {

    RESULT check(OBJ obj);
}
