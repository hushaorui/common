package com.hushaorui.common;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface WebConstants {

    String SYSTEM = "system";

    String GLOBAL = "global";
    String TEST = "test";

    String WARNING = "warning";
    String INFO = "info";
    String ERROR = "error";
    String SUCCESS = "success";

    String DEFAULT = "default";

    String NOT_FOUND = "not found";

    String DEFAULT_ENCODING = "UTF-8";

    /** 中文、大小写字母、数字 正则表达式 */
    String CHINESE_ENGLISH_NUMBER_REGEX = "[0-9a-zA-Z\u4e00-\u9fa5]+";

    // 以逗号切割，保留双引号内逗号且去除两端空格的正则表达式
    String COMMA_SPLIT_REGEX = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    // 以分号切割，保留双引号内分号且去除两端空格的正则表达式
    String SEMICOLON_SPLIT_REGEX = ";(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    // 以等号切割，保留双引号内等号且去除两端空格的正则表达式
    String EQUAL_SIGN_SPLIT_REGEX = "=(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    // 以冒号切割，保留双引号内冒号且去除两端空格的正则表达式
    String COLON_SPLIT_REGEX = ":(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    // 以空白字符切割，保留双引号内空白字符的正则表达式
    String BLANK_SPLIT_REGEX = "\\s+(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";

    /** Object类中的方法 */
    Set<Method> OBJECT_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Object.class.getMethods())));

    /** 进程权限拦截器会用到的额外参数 */
    String CMD_PROCESS_IN_CMD = "processInCmd";
    /** 进程权限拦截器会用到的额外参数 */
    String CMD_PROCESS_IN_DB = "processInDB";

    /** 默认yaml解析器 */
    Yaml YAML = new Yaml(new Representer() {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            if (propertyValue == null) {
                // if value of property is null, ignore it. 忽略null字段
                return null;
            } else {
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    });

}
