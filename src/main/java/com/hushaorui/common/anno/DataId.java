package com.hushaorui.common.anno;

import java.lang.annotation.*;

/**
 * id字段标记
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DataId {
}
