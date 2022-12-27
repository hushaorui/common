package com.hushaorui.common;

import java.util.List;

/**
 * 可进行初始化的配置类
 */
public interface InitializedConfig {
    /**
     * 初始化
     */
    void init();

    /**
     * 获取所有配置集合
     */
    List<? extends LongIdInfoIF> getAllCfg();
}
