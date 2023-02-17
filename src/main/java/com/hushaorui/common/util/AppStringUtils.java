package com.hushaorui.common.util;

import com.hushaorui.common.CommonFilter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具类
 */
public abstract class AppStringUtils {

    /**
     * 将字符串转化为long类型的列表
     *
     * @param idString  字符串
     * @param separator 分隔符(非正则)
     * @return long类型的list
     */
    public static ArrayList<Long> idStringToList(String idString, String separator, CommonFilter<Long> filter) {
        String[] idArray = idString.split("\\s*" + separator + "\\s*");
        ArrayList<Long> idList = new ArrayList<>(idArray.length);
        if (filter == null) {
            for (String id : idArray) {
                try {
                    idList.add(Long.parseLong(id));
                } catch (NumberFormatException ignore) {
                }
            }
        } else {
            for (String id : idArray) {
                try {
                    long value = Long.parseLong(id);
                    if (filter.check(value)) {
                        idList.add(value);
                    }
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return idList;
    }

    public static ArrayList<Long> idStringToList(String idString, CommonFilter<Long> filter) {
        return idStringToList(idString, ",", filter);
    }

    public static ArrayList<Long> idStringToList(String idString) {
        return idStringToList(idString, ",", null);
    }

    /**
     * 将list转换为 item,item,item 类似的字符串
     *
     * @param list      列表
     * @param separator 分隔符
     * @return 字符串
     */
    public static String listToString(List<?> list, String separator) {
        StringBuilder builder = new StringBuilder();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            builder.append(list.get(i));
            if (i < size - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public static ArrayList<String> splitString(String string, String separator) {
        String[] array = string.split("\\s*" + separator + "\\s*");
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * 将html字符串中的一些特殊字符转义
     *
     * @param content html字符串
     * @return 转义后的字符串
     */
    public static String handleHtmlContent(String content) {
        return content.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    /**
     * 去除字符串两端的双引号或单引号(如果只有一端有则不去除)
     *
     * @param content 原字符串
     * @return 处理后的字符串
     */
    public static String trimQuotationMarks(String content) {
        if (content.length() >= 2) {
            if (content.startsWith("\"") && content.endsWith("\"")) {
                // 去除两端双引号
                content = content.substring(1, content.length() - 1);
            } else if (content.startsWith("'") && content.endsWith("'")) {
                // 去除两端单引号
                content = content.substring(1, content.length() - 1);
            }
        }
        return content;
    }

    /**
     * 获取字符串同步锁
     *
     * @param id       确保在同一个dataType下唯一
     * @param dataType 数据类型
     * @return 同步锁
     */
    public static String getLock(long id, Class<?> dataType, String... params) {
        if (params == null || params.length == 0) {
            return (id + dataType.getSimpleName()).intern();
        } else {
            return (String.format("%s%s_%s", id, dataType.getSimpleName(), Arrays.toString(params))).intern();
        }
    }

    public static String humpToUnderline(String content) {
        StringBuilder builder = new StringBuilder();
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String string = String.valueOf(chars[i]);
            String lower = string.toLowerCase();
            if (i != 0 && !string.equals(lower)) {
                // 不是第一个字母且是大写字母，拼接下划线
                builder.append("_");
            }
            builder.append(lower);
        }
        return builder.toString();
    }

    /**
     * 根据 "." 切割后获取除最后一项和最后一个 "." 外的字符串， 如 aa.cc.dd 返回 aa.cc
     *
     * @param string 原字符串
     * @return 字符串的前半段
     */
    public static String getStartStringWithDot(String string) {
        return string.replaceAll("(\\w+(\\.\\w+)*)\\.\\w+", "$1");
    }

    /**
     * 根据 "." 切割后获取除最后一项和最后一个 "." 外的字符串， 如 aa.cc.dd 返回 aa.cc
     *
     * @param string 原字符串
     * @return 字符串的前半段
     */
    public static String getStartStringWithChar(String string, String charString) {
        return string.replaceAll("(\\w+(\\.\\w+)*)" + charString + "\\w+", "$1");
    }

    /**
     * 加号和减号的集合
     */
    private static final Set<String> PLUS_AND_MINUS_SING_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("+", "-")));

    /**
     * 根据加号或减号切割(引号中，小括号中，中括号中的除外)，其中，引号可以让小括号和中括号失效，反斜线 "/" 可以让它们都失效
     */
    public static List<String> splitWithPlusOrMinus(String source) throws Exception {
        return split(source, PLUS_AND_MINUS_SING_SET);
    }


    private static Map<Set<String>, Map<String, Set<String>>> splitStrMapMapping = new ConcurrentHashMap<>();

    private static Map<String, Set<String>> transSplitSetToMap(Set<String> splitCollection) {
        return splitStrMapMapping.computeIfAbsent(splitCollection, k -> {
            Map<String, Set<String>> map = new HashMap<>();
            for (String splitStr : k) {
                // 将本体以及所有前缀作为key放入
                map.computeIfAbsent(splitStr, k2 -> new HashSet<>()).add(splitStr);
                for (int i = splitStr.length(); i > 0; i--) {
                    String subStr = splitStr.substring(0, i);
                    map.computeIfAbsent(subStr, k2 -> new HashSet<>()).add(splitStr);
                }
            }
            return map;
        });
    }

    /**
     * 根据特定字符串切割，其中，引号可以让小括号和中括号失效，反斜线 "/" 可以让它们都失效
     *
     * @param source      原字符串
     * @param splitStrSet 用于切割的字符(长度大于1)集合，
     * @return 切割后的字符串列表
     */
    public static List<String> splitWithLongStr(String source, Set<String> splitStrSet) throws Exception {
        return split(source, transSplitSetToMap(splitStrSet));
    }

    /**
     * 根据特定字符串切割，其中，引号可以让小括号和中括号失效，反斜线 "/" 可以让它们都失效
     *
     * @param source      原字符串
     * @param splitStrMap 用于切割的字符(长度大于1)集合, key为这些切割字符的前子串，比如 123的前子串有1, 12, 123
     * @return 切割后的字符串列表
     */
    public static List<String> split(String source, Map<String, Set<String>> splitStrMap) throws Exception {
        return split(source, splitStrMap, true, true);
    }

    /**
     * 根据特定字符串切割，其中，引号可以让小括号和中括号失效，反斜线 "/" 可以让它们都失效
     *
     * @param source        原字符串
     * @param splitStrMap   用于切割的字符(长度大于1)集合, key为这些切割字符的前子串，比如 123的前子串有1, 12, 123
     * @param smallBrackets 考虑小括号
     * @param brackets      考虑中括号
     * @return 切割后的字符串列表
     */
    public static List<String> split(String source, Map<String, Set<String>> splitStrMap, boolean smallBrackets, boolean brackets) throws Exception {
        ArrayList<String> list = new ArrayList<>();
        int length = source.length();
        // 左小括号(中文小括号也行)的数量
        int leftSmallBracketsCount = 0;
        // 左中括号的数量
        int leftBracketsCount = 0;
        // 左大括号的数量
        int leftBracesCount = 0;
        // 引号的数量
        int leftQuotationCount = 0;
        // 中文左引号的数量
        int leftCnQuotationCount = 0;
        // 上一个字符是反斜线
        boolean lastIsBackslash = false;
        StringBuilder builder = new StringBuilder();
        // 拼接切割字符
        StringBuilder splitBuilder = new StringBuilder();
        outer:
        for (int index = 0; index < length; index++) {
            // 单个字符
            String s = String.valueOf(source.charAt(index));
            // 不在 引号、小括号、中括号内
            boolean isNormal = (leftQuotationCount + leftCnQuotationCount == 0) && (!smallBrackets || leftSmallBracketsCount == 0) && (!brackets || leftBracketsCount == 0)
                    && leftBracesCount == 0;
            if (isNormal) {
                // 注意，这里已经将 s 拼接上去了
                String splitPrefix = splitBuilder.append(s).toString();
                // 以此为前缀的切割符集合
                Set<String> splitStrSet = splitStrMap.get(splitPrefix.intern());
                // 当前不是分隔符
                if (splitStrSet == null) {
                    if (splitPrefix.length() > 1) {
                        String oldPrefix = splitBuilder.substring(0, splitBuilder.length() - 1).intern();
                        splitStrSet = splitStrMap.get(oldPrefix);
                        if (splitStrSet.contains(oldPrefix)) {
                            // 可以进行切割，到此为止，还未处理 s
                            doSplit(list, builder, splitBuilder);
                        } else {
                            // 上一个能找出分隔符集合，但不符合条件，加上s，就不是分隔符了，属于普通字符，但是 splitPrefix的后缀中可能仍然含有分隔符
                            // 从最长的子串开始，这里遍历的子串包括了 s
                            for (int i = 1; i < splitPrefix.length(); i++) {
                                String newPrefix = splitPrefix.substring(i);
                                splitStrSet = splitStrMap.get(newPrefix);
                                if (splitStrSet != null) {
                                    // 子串是某些切割符的前缀
                                    splitBuilder.setLength(0);
                                    splitBuilder.append(newPrefix);
                                    // 剩余的部分属于普通字符串，拼接到builder
                                    builder.append(splitPrefix, 0, i);
                                    // 本轮已处理完毕
                                    continue outer;
                                }
                            }
                            builder.append(splitBuilder);
                            splitBuilder.setLength(0);
                            // 运行到此， s 在 for循环中已经处理过了，本轮处理完毕
                            continue;
                            // 碰到了以下的情况要注意
                            // abcdefg  和  cf  遇到了  xxabcfxxx   splitBuilder此时为 "abcf"
                        }
                    }
                    // 处理 s
                    // 看当前单字符是否是切割符的前缀
                    splitStrSet = splitStrMap.get(s);
                    if (splitStrSet == null) {
                        // 不是的话，置空
                        splitBuilder.setLength(0);
                    } else {
                        // 是的话，不再继续下面的逻辑
                        continue;
                    }
                } else if (splitStrSet.size() == 1) {
                    String splitStr = splitBuilder.toString().intern();
                    // 只有完全匹配时才进行切割
                    if (splitStrSet.iterator().next().equals(splitStr)) {
                        doSplit(list, builder, splitBuilder);
                    }
                    continue;
                } else {
                    // 有多个分隔符对应
                    continue;
                }
            }
            boolean old = !lastIsBackslash;
            // 处理转义
            lastIsBackslash = lastIsBackslash(s, lastIsBackslash, leftQuotationCount, leftCnQuotationCount);
            if (lastIsBackslash) {
                builder.append("\\");
                continue;
            }
            if (s.equals("\"")) {
                if (old) {
                    if (leftQuotationCount == 0) {
                        leftQuotationCount = 1;
                    } else {
                        leftQuotationCount = 0;
                    }
                }
            } else if (s.equals("“")) {
                leftCnQuotationCount++;
            } else if (s.equals("”")) {
                leftCnQuotationCount--;
            } else if (s.equals("[")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftBracketsCount++;
                }
            } else if (s.equals("]")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftBracketsCount--;
                }
            } else if (s.equals("{")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    leftBracesCount++;
                }
            } else if (s.equals("}")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    leftBracesCount--;
                }
            } else if (s.equals("(") || s.equals("（")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftSmallBracketsCount++;
                }
            } else if (s.equals(")") || s.equals("）")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftSmallBracketsCount--;
                }
            }
            builder.append(s);
        }
        if (splitBuilder.length() > 0) {
            String prefix = splitBuilder.toString().intern();
            Set<String> splitStrSet = splitStrMap.get(prefix);
            if (!splitStrSet.contains(prefix)) {
                // 不是完整的切割符
                builder.append(prefix);
            }
        }
        if (builder.length() > 0) {
            list.add(builder.toString());
        }
        return list;
    }

    private static void doSplit(List<String> list, StringBuilder builder, StringBuilder splitBuilder) {
        // 只有一个分隔符对应，则进行分割
        list.add(builder.toString());
        // 放入list后置空
        builder.setLength(0);
        // 切割符前缀置空
        splitBuilder.setLength(0);
    }

    private static boolean lastIsBackslash(String s, boolean lastIsBackslash, int leftQuotationCount, int leftCnQuotationCount) throws Exception {
        if (s.equals("\\")) {
            // 是反斜线，如果上一个是反斜线，则此反斜线失去转义的能力，如果不是，则转义下一个字符
            lastIsBackslash = !lastIsBackslash;
        } else if (lastIsBackslash) {
            // 上一个是反斜线，转义此此字符，为普通字符串
            if (leftQuotationCount + leftCnQuotationCount == 0) {
                // 不在引号内
                throw new Exception("Escape characters are not in quotation marks");
            }
            lastIsBackslash = false;
        }
        return lastIsBackslash;
    }

    /**
     * 根据特定字符串切割，其中，引号可以让小括号和中括号失效，反斜线 "/" 可以让它们都失效
     *
     * @param source      原字符串
     * @param splitStrSet 用于切割的字符集合
     * @return 切割后的字符串列表
     */
    public static List<String> split(String source, Collection<String> splitStrSet) throws Exception {
        return split(source, splitStrSet, true, true);
    }

    /**
     * 根据特定字符串切割，其中，引号可以让小括号和中括号失效，反斜线 "/" 可以让它们都失效
     *
     * @param source        原字符串
     * @param splitStrSet   用于切割的字符集合
     * @param smallBrackets 考虑小括号
     * @param brackets      考虑中括号
     * @return 切割后的字符串列表
     */
    public static List<String> split(String source, Collection<String> splitStrSet, boolean smallBrackets, boolean brackets) throws Exception {
        ArrayList<String> list = new ArrayList<>();
        int length = source.length();
        // 左小括号(中文小括号也行)的数量
        int leftSmallBracketsCount = 0;
        // 左中括号的数量
        int leftBracketsCount = 0;
        // 引号的数量
        int leftQuotationCount = 0;
        // 中文左引号的数量
        int leftCnQuotationCount = 0;
        // 左大括号的数量
        int leftBracesCount = 0;
        // 上一个字符是反斜线
        boolean lastIsBackslash = false;
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            // 单个字符
            String s = String.valueOf(source.charAt(index));
            if (splitStrSet.contains(s)) {
                if (!lastIsBackslash && (leftQuotationCount + leftCnQuotationCount == 0) && (!smallBrackets || leftSmallBracketsCount == 0)
                        && (!brackets || leftBracketsCount == 0) && leftBracesCount == 0) {
                    // 不在引号内，且不在括号内，且不在中括号内
                    // 是切割符
                    list.add(builder.toString());
                    builder.setLength(0);
                    continue;
                }
            }
            boolean old = !lastIsBackslash;
            lastIsBackslash = lastIsBackslash(s, lastIsBackslash, leftQuotationCount, leftCnQuotationCount);
            if (lastIsBackslash) {
                builder.append("\\");
                continue;
            }
            if (s.equals("\"")) {
                if (old) {
                    if (leftQuotationCount == 0) {
                        leftQuotationCount = 1;
                    } else {
                        leftQuotationCount = 0;
                    }
                }
            } else if (s.equals("“")) {
                leftCnQuotationCount++;
            } else if (s.equals("”")) {
                leftCnQuotationCount--;
            } else if (s.equals("[")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftBracketsCount++;
                }
            } else if (s.equals("]")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftBracketsCount--;
                }
            } else if (s.equals("{")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    leftBracesCount++;
                }
            } else if (s.equals("}")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    leftBracesCount--;
                }
            } else if (s.equals("(") || s.equals("（")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftSmallBracketsCount++;
                }
            } else if (s.equals(")") || s.equals("）")) {
                if (leftQuotationCount + leftCnQuotationCount == 0) {
                    // 不在引号内
                    leftSmallBracketsCount--;
                }
            }
            builder.append(s);
        }
        if (builder.length() > 0) {
            list.add(builder.toString());
        }
        return list;
    }

    public static String clearBackslashAndQuotationSigns(String source) {
        return source.replace("\\\"", "\"").replace("\\“", "“").replace("\\“", "“")
                .replace("\\”", "”");
    }

    /**
     * 获取一个集合的所有子集(无顺序，包括空集)
     */
    public static <T> List<List<T>> getSubSetList(List<T> list) {
        List<List<T>> result = new ArrayList<>();
        //先添加一个空集
        result.add(new ArrayList<>());
        for (T item : list) {
            //获取当前子集个数
            int size = result.size();
            //依次取出当前子集并为每一子集添加元素list.get(i)
            //最后再添加回result
            for (int j = 0; j < size; j++) {
                List<T> clone = new ArrayList<>(result.get(j));
                clone.add(item);
                result.add(clone);
            }
        }
        return result;
    }

    /**
     * 获取一个列表的全排列
     * @param list 原列表
     * @return 所有不同排列的列表
     */
    public static <T> List<List<T>> getAllArrays(List<T> list) {
        int[] sequences = new int[list.size()];
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = i;
        }
        List<List<T>> resultList = new ArrayList<>();
        dfs(list, sequences, 0, sequences.length - 1, resultList);
        return resultList;
    }

    private static <T> void dfs(List<T> list, int[] sequences, int start, int end, List<List<T>> resultList) {
        //递归结束条件
        if (start == end) {
            //System.out.println(Arrays.toString(sequences));//输出一个全排列序列
            ArrayList<T> arrayList = new ArrayList<>(sequences.length);
            for (int index : sequences) {
                arrayList.add(list.get(index));
            }
            resultList.add(arrayList);
            return;
        }
        for (int i = start; i <= end; i++) {
            swap(sequences, start, i);//把当前第1个数与后面所有数交换位置，注意所以i是从start开始
            dfs(list, sequences, start + 1, end, resultList);
            swap(sequences, start, i);//恢复，用于下一次交换
        }
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

}
