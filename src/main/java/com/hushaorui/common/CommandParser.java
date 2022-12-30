package com.hushaorui.common;

import com.hushaorui.common.util.AppStringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 命令解析器
 */
public class CommandParser {
    // 所有参数集合
    private Map<String, String> optionMap;
    // 命令名称
    @Getter
    private String commandName;
    // 命令原字符串
    @Getter
    private String source;
    // 语言
    @Getter
    @Setter
    private String lang;
    // 命令中是否存在有效的 "-"，有与没有是两种风格
    private boolean containsMinusSign;
    private CommandParser() {}

    public static CommandParser parse(String source) {
        // 使用空白字符切割
        String[] stringArray = source.split(WebConstants.BLANK_SPLIT_REGEX);
        CommandParser commandParser = new CommandParser();
        commandParser.source = source;
        if (stringArray.length == 0) {
            commandParser.optionMap = Collections.emptyMap();
            commandParser.commandName = "";
            return commandParser;
        } else if (stringArray.length == 1) {
            commandParser.optionMap = Collections.emptyMap();
            commandParser.commandName = stringArray[0];
            return commandParser;
        }
        commandParser.commandName = stringArray[0];
        // 这里需要一个有序的Map
        Map<String, String> hashMap = new LinkedHashMap<>();
        String currentOption = null;
        // 第一个位置是命令的名称，从第二个位置开始解析
        for (int i = 1; i < stringArray.length; i++) {
            String commandString = stringArray[i];
            if (commandString.startsWith("-")) {
                currentOption = commandString.substring(1);
                // 先将自身放入map，防止后面没有参数而遗失数据
                putToMap(hashMap, currentOption, currentOption);
                commandParser.containsMinusSign = true;
                continue;
            }
            commandString = AppStringUtils.trimQuotationMarks(commandString);
            if (currentOption == null) {
                // 没有参数项，只能将自己作为key
                hashMap.put(commandString, commandString);
            } else {
                putToMap(hashMap, currentOption, commandString);
                currentOption = null;
            }
        }
        commandParser.optionMap = hashMap;
        return commandParser;
    }

    private static void putToMap(Map<String, String> hashMap, String key, String value) {
        hashMap.put(key, value);
        if (key.length() > 1) {
            for (int i = 0; i < key.length(); i++) {
                hashMap.put(String.valueOf(key.charAt(i)), value);
            }
        }
    }

    /**
     * Description: 判断命令中是否存在有效的 "-"，有与没有是两种风格
     * @author 胡绍瑞[hushaorei@163.com]
     */
    public boolean isContainsMinusSign() {
        return containsMinusSign;
    }

    /**
     * 获取所有值，一般用于没有 "-" 的命令，如 login 用户名 密码
     * 返回的数组长度为 params 的长度
     */
    public String[] getAllStringValue(String... params) {
        if (params == null || params.length == 0 || ! containsMinusSign) {
            String[] array = new String[Math.max(params == null ? 0 : params.length, optionMap.size())];
            int i = 0;
            for (String key : optionMap.keySet()) {
                array[i++] = key;
            }
            return array;
        }
        String[] array = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            array[i] = getOption(params[i]);
        }
        return array;
    }

    /**
     * 获取原始map中的字符串数据
     */
    public String getOption(String key) {
        return optionMap.get(key);
    }

    public String getOption(String key, String defaultValue) {
        if (optionMap.containsKey(key)) {
            return optionMap.get(key);
        }
        return defaultValue;
    }

    public byte getByteValue(String key, byte defaultValue) {
        String string = optionMap.get(key);
        if (string == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(string);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }
    public short getShortValue(String key, short defaultValue) {
        String string = optionMap.get(key);
        if (string == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(string);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }
    public int getIntValue(String key, int defaultValue) {
        String string = optionMap.get(key);
        if (string == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(string);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }
    public long getLongValue(String key, long defaultValue) {
        String string = optionMap.get(key);
        if (string == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(string);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }
    public float getFloatValue(String key, float defaultValue) {
        String string = optionMap.get(key);
        if (string == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(string);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }
    public double getDoubleValue(String key, double defaultValue) {
        String string = optionMap.get(key);
        if (string == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(string);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }
    public boolean getBoolean(String key) {
        String string = optionMap.get(key);
        if (string == null) {
            return false;
        }
        if (string.equals(key)) {
            return true;
        }
        try {
            return Boolean.parseBoolean(string);
        } catch (Exception ignore) {
            return false;
        }
    }
    public List<String> getStringList(String key, String split) {
        String string = optionMap.get(key);
        if (string == null) {
            return Collections.emptyList();
        }
        String[] array = string.split(split);
        return new ArrayList<>(Arrays.asList(array));
    }
    public List<Integer> getIntegerList(String key, String split) {
        String string = optionMap.get(key);
        if (string == null) {
            return Collections.emptyList();
        }
        String[] array = string.split(split);
        List<Integer> list = new ArrayList<>(array.length);
        for (String numberString : array) {
            list.add(Integer.parseInt(numberString));
        }
        return list;
    }
    public Map<Integer, Integer> getIntIntMap(String key, String split1, String split2) {
        String string;
        if (optionMap.containsKey(key)) {
            string = optionMap.get(key);
        } else {
            return Collections.emptyMap();
        }
        String[] array = string.split(split1);
        Map<Integer, Integer> hashMap = new HashMap<>(array.length * 4 / 3 + 1);
        for (String tempEntry : array) {
            String[] temp = tempEntry.split(split2);
            int mapKey = Integer.parseInt(temp[0]);
            int mapValue;
            if (temp.length >= 2) {
                mapValue = Integer.parseInt(temp[1]);
            } else {
                // 默认为1
                mapValue = 1;
            }
            hashMap.put(mapKey, mapValue);
        }
        return hashMap;
    }

    public Map<String, Integer> getStringIntMap(String key, String split1, String split2) {
        String string = optionMap.get(key);
        if (string == null) {
            return Collections.emptyMap();
        }
        String[] array = string.split(split1);
        Map<String, Integer> hashMap = new HashMap<>(array.length * 4 / 3 + 1);
        for (String tempEntry : array) {
            String[] temp = tempEntry.split(split2);
            int mapValue;
            if (temp.length >= 2) {
                mapValue = Integer.parseInt(temp[1]);
            } else {
                // 默认为1
                mapValue = 1;
            }
            hashMap.put(temp[0], mapValue);
        }
        return hashMap;
    }

    public File getFile(String key, boolean createIfNotExist) {
        String string = optionMap.get(key);
        if (string == null) {
            return null;
        }
        File file = new File(string);
        if (! file.exists()) {
            try {
                if (createIfNotExist) {
                    file.createNewFile();
                    return file;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return file;
        }
    }

}
