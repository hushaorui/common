package com.hushaorui.common.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MD5Utils {
    /**
     * 对字符串进行MD5摘要加密，返回结果与MySQL的MD5函数一致
     *
     * @param input 输入
     * @return 返回值中的字母为小写
     */
    public static String md5(String input) {
        if (null == input) {
            input = "";
        }
        String result = "";
        try {
            // MessageDigest类用于为应用程序提供信息摘要算法的功能，如MD5或SHA算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 获取输入
            md.update(input.getBytes());
            // 获得产出（有符号的哈希值字节数组，包含16个元素）
            byte[] output = md.digest();

            // 32位的加密字符串
            StringBuilder builder = new StringBuilder(32);
            // 下面进行十六进制的转换
            for (int b : output) {
                // 转变成对应的ASSIC值
                int value = b;
                // 将负数转为正数（最终返回结果是无符号的）
                if (value < 0) {
                    value += 256;
                }
                // 小于16，转为十六进制后只有一个字节，左边追加0来补足2个字节
                if (value < 16) {
                    builder.append("0");
                }
                // 将16位byte[]转换为32位无符号String
                builder.append(Integer.toHexString(value));
            }
            result = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }


    //0的ASCII
    private static final int ASCII_0 = 48;
    //9的ASCII
    private static final int ASCII_9 = 57;
    //A的ASCII
    private static final int ASCII_A = 65;
    //F的ASCII
    private static final int ASCII_F = 70;
    //a的ASCII
    private static final int ASCII_a = 97;
    //f的ASCII
    private static final int ASCII_f = 102;


    //可表示16进制数字的字符
    private static final char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String HASH_MD5 = "MD5";

    /**
     * 获取字节数组MD5
     */
    public static String encoding(byte[] bs) {

        String encodingStr = null;
        try {
            MessageDigest mdTemp = MessageDigest.getInstance(HASH_MD5);
            mdTemp.update(bs);

            return toHexString(mdTemp.digest());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return encodingStr;
    }

    /**
     * 获取字符串MD5
     */
    public static String encoding(String text) {
        if (text == null) {
            return null;
        }
        return encoding(text.getBytes(StandardCharsets.UTF_8));
    }


    public static String encodeTwice(String text) {
        if (text == null) {
            return null;
        }
        String md5Once = encoding(text.getBytes(StandardCharsets.UTF_8));
        return encoding(md5Once.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取文件内容MD5
     */
    public static String encodingFile(String filePath) {
        try (InputStream fis = new FileInputStream(filePath)) {
            return encoding(fis);
        } catch (Exception ee) {
            return null;
        }
    }

    /**
     * 获取输入流MD5
     */
    public static String encoding(InputStream fis) throws Exception {
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(HASH_MD5);
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        return toHexString(md5.digest());
    }

    /**
     * 转换为用16进制字符表示的MD5
     */
    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte value : b) {
            sb.append(hexChar[(value & 0xf0) >>> 4]);
            sb.append(hexChar[value & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 是否是合法的MD5
     */
    public static boolean validate(String md5Str) {
        if (md5Str == null || md5Str.length() != 32) {
            return false;
        }
        byte[] by = md5Str.getBytes();
        for (byte b : by) {
            if ((int) b < ASCII_0
                    || ((int) b > ASCII_9 && (int) b < ASCII_A)
                    || ((int) b > ASCII_F && (int) b < ASCII_a)
                    || (int) b > ASCII_f) {
                return false;
            }
        }
        return true;
    }
}
