package com.hushaorui.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 热更新类信息
 */
@Getter
@Setter
@ToString
public class AppHotFixClass {
    // 类名
    private String className;
    // class类文件的md5值
    private String md5Value;
    // 版本，从1开始
    private Integer version;
    // 该类字节码文件的绝对路径
    private String absolutePath;

}
