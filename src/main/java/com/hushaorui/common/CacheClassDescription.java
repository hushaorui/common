package com.hushaorui.common;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 缓存类的描述
 */
@Getter
@Setter
public class CacheClassDescription {
    /** pojo类的class */
    private Class<?> cacheClass;
    /** <所有唯一的字段, get方法> ，如果里面的map中有多个字段，则联合唯一 */
    private Map<String, Map<Field, Method>> uniqueFields;
    /** 所有不能为null的get方法 */
    private Map<String, Method> notNullGetMethods;
    /** 所有需要根据该字段查找所有数据对应的方法 */
    private Map<String, Method> findAllGetMethods;
    /** 分组字段的名称 */
    private String groupFieldName;
    /** 分组的字段对应的 get方法 */
    private Method groupFieldGetMethod;
    /** 唯一键对应的mapper的select方法 */
    private Map<String, String> uniqueNameMap;
}
