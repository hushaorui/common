package com.hushaorui.common.util;

import com.hushaorui.common.RefBoolean;
import com.hushaorui.common.RefString;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 字符串相关的工具类
 */
public abstract class StringExtendUtils {

    /**
     * 获取一个随机的uuid
     * @return uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取一个随机的uuid
     * @return uuid
     */
    public static String getUUID(boolean upper) {
        if (upper) {
            return UUID.randomUUID().toString().toUpperCase();
        } else {
            return UUID.randomUUID().toString();
        }
    }

    /**
     * 获取随机的长度为32的十六进制数
     * @return 十六进制数
     */
    public static String getHexUUID() {
        return getHexUUID(false);
    }

    /**
     * 获取随机的长度为32的十六进制数
     * @return 十六进制数
     */
    public static String getHexUUID(boolean upper) {
        if (upper) {
            return getUUID().replaceAll("-", "").toUpperCase();
        } else {
            return getUUID().replaceAll("-", "");
        }
    }

    /**
     * 获取随机的长度为64的十六进制数
     * @return 十六进制数
     */
    public static String getHexUUID64() {
        return getHexUUID64(false);
    }

    /**
     * 获取随机的长度为64的十六进制数
     * @return 十六进制数
     */
    public static String getHexUUID64(boolean upper) {
        if (upper) {
            return (getUUID() + getUUID()).replaceAll("-", "").toUpperCase();
        } else {
            return (getUUID() + getUUID()).replaceAll("-", "");
        }
    }

    public static Map<String, String> parseStringMap(String text, String firstSplit, String secondSplit) {
        return parseStringMap(text, firstSplit, secondSplit, null);
    }
    /** 将字符串两次分割为map */
    public static Map<String, String> parseStringMap(String text, String firstSplit, String secondSplit, Map<String, String> map) {
        String[] tempArray = text.split(firstSplit);
        if (map == null) {
            map = new HashMap<>(tempArray.length, 1.5f);
        }
        for (String temp : tempArray) {
            if (! text.contains(secondSplit)) {
                map.put(text, temp);
            } else {
                String[] array = temp.split(secondSplit);
                map.put(array[0], array[1]);
            }
        }
        return map;
    }

    /** id集合 */
    private static final ConcurrentHashMap<String, AtomicLong> idMap = new ConcurrentHashMap<>();
    /** 获取线程安全的唯一的id */
    public static Long generatorId(String key) {
        return idMap.computeIfAbsent(key, k -> new AtomicLong(1)).getAndIncrement();
    }

    /** 去除字符串左侧空白字符 */
    public static String trimLeft(String source) {
        return source.replaceAll("\\s*(.*)", "$1");
    }

    /** 去除字符串右侧空白字符 */
    public static String trimRight(String source) {
        return source.replaceAll("(.*)\\s*", "$1");
    }

    /** 判断字符串包含子字符串的数量 */
    public static int subStringCount(String string, String subString) {
        return "".equals(subString) ? 0 : (string.length() - string.replace(subString, "").length()) / subString.length();
    }

    /** 将引号外的所有空白字符 多个变一个空格 */
    public static String formatBlank(String string) {
        return string.replaceAll("\\s+(?=([^\\\"^“^”]*[\\\"^“^”][^\\\"^“^”]*[\\\"^“^”])*[^\\\"^“^”]*$)", " ");
    }

    public static class ElementObj {
        @Getter
        private String name; // 标签名称    ""代表空白部分
        @Getter
        private boolean root; // 是否是根标签，有些方法只能根标签调用
        @Getter
        private int depth; // 深度， 根标签为0
        @Getter
        private String errorMsg;
        @Getter
        private Map<String, String> attrs = new LinkedHashMap<>(); // 属性和属性值
        @Getter
        private String content; // 内容
        @Getter
        private List<ElementObj> children; // 子标签
        private Map<String, ElementObj> idMapping; // id为key的集合
        @Override
        public String toString() {
            if (errorMsg != null) {
                return errorMsg;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < depth * 4; i++) {
                builder.append(" ");
            }
            builder.append("<").append(name);
            attrs.forEach((key, value) -> {
                builder.append(" ").append(key).append("=");
                if ("true".equals(value) || "false".equals(value)) {
                    builder.append(value);
                } else {
                    builder.append("\"").append(value).append("\"");
                }
            });
            builder.append(">");
            if (children != null && ! children.isEmpty()) {
                children.forEach(obj -> {
                    builder.append(System.lineSeparator()).append(obj.toString());
                });
                // 有子标签
                builder.append(System.lineSeparator()).append("/").append(name).append(">");
            } else {
                // 没有子标签
                builder.append(System.lineSeparator()).append("/>");
            }
            return builder.toString();
        }
    }

    public static ElementObj parseHtml(String htmlString) {
        ElementObj rootElement = new ElementObj();
        rootElement.root = true;
        int length = htmlString.length();

        StringBuilder builder = new StringBuilder();
        // 当前正在操作的标签
        ElementObj current = null;
        // 标记上一个字符是 <
        RefBoolean lastIsLower = new RefBoolean();
        // 上一个是空白字符
        RefBoolean lastIsBlank = new RefBoolean();
        // 上一个是 = 隔了空白字符也算
        RefBoolean lastIsEqual = new RefBoolean();
        // 是否在一个引号当中
        RefBoolean isInQuotation = new RefBoolean();
        // 当前的属性名称
        RefString attrName = new RefString();
        // <meta http-equiv = "Content-Type" readonly content="text/html; charset=gbk">
        for (int i = 0; i < length; i++) {
            String string = String.valueOf(htmlString.charAt(i));
            if (string.matches("\\s+")) {
                // 是空白字符
                if (current != null && current.name != null) {
                    // 已经确定根标签
                    if (lastIsBlank.value) {
                        // 上一个也是空白字符，则拼接
                        builder.append(string);
                    } else {
                        // 上一个不是空白字符
                        handleBuilder(current, attrName, lastIsEqual, builder);
                    }
                } // 还没有确定根标签，则空白字符舍弃掉
                // 改变标记
                lastIsBlank.value = true;
            } else {
                // 特殊字符 = < > / \ " !
                if ("=".equals(string)) {
                    if (! isInQuotation.value) {
                        // 不在句子当中，则标记
                        lastIsEqual.value = true;
                    } else if (current != null) {
                        // 在句子中
                        if (lastIsBlank.value) {
                            // 上一个字符是空白字符
                            handleBuilder(current, attrName, lastIsEqual, builder);
                        } else {
                            // 非空白，一起拼接
                            builder.append(string);
                        }
                    }
                } else if ("\"".equals(string)) {
                    isInQuotation.value = ! isInQuotation.value;
                } else if ("\\".equals(string)) {
                    // 转义字符
                    rootElement.errorMsg = "出现了转义字符，无法解析";
                    return rootElement;
                } else if ("<".equals(string)) {
                    if (! isInQuotation.value) {
                        // 标签的开始
                        if (current == null) {
                            current = rootElement;
                        } else {
                            current = new ElementObj();
                        }
                    } else if (current != null) {
                        // 在句子中
                        if (lastIsBlank.value) {
                            // 上一个字符是空白字符
                            handleBuilder(current, attrName, lastIsEqual, builder);
                        } else {
                            // 非空白，一起拼接
                            builder.append(string);
                        }
                    }
                } else if (">".equals(string)) {
                    if (! isInQuotation.value) {
                        // 非句子中，标签属性的结尾
                        // TODO
                    } else if (current != null) {
                        // 在句子中
                        if (lastIsBlank.value) {
                            // 上一个字符是空白字符
                            handleBuilder(current, attrName, lastIsEqual, builder);
                        } else {
                            // 非空白，一起拼接
                            builder.append(string);
                        }
                    }
                }
                lastIsBlank.value = false;
            }
        }
        return rootElement;
    }

    private static void handleBuilder(ElementObj current, RefString attrName, RefBoolean lastIsEqual, StringBuilder builder) {
        String complete = builder.toString().trim();
        builder.setLength(0);
        if (current.name == null) {
            // 名字还没确定，则本次为名字
            current.name = complete;
        } else if (attrName.value == null) {
            attrName.value = complete;
        } else if (lastIsEqual.value) {
            // 前面出现过 = ，并且没有处理，这里处理掉
            lastIsEqual.value = false;
        } else {
            current.attrs.put(attrName.value, complete);
            attrName.value = null;
        }
    }
}
