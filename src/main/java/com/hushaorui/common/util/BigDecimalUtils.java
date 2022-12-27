package com.hushaorui.common.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalUtils {

    /** 加法 */
    public static BigDecimal add(String s1, String s2) {
        BigDecimal b1 = new BigDecimal(s1);
        return b1.add(new BigDecimal(s2));
    }

    /** 减法 */
    public static BigDecimal subtract(String s1, String s2) {
        BigDecimal b1 = new BigDecimal(s1);
        return b1.subtract(new BigDecimal(s2));
    }

    /** 乘法 */
    public static BigDecimal multiply(String s1, String s2) {
        BigDecimal b1 = new BigDecimal(s1);
        return b1.multiply(new BigDecimal(s2));
    }

    /** 除法 */
    public static BigDecimal divide(String s1, String s2) {
        BigDecimal b1 = new BigDecimal(s1);
        return b1.divide(new BigDecimal(s2));
    }

    /** 取余 数组中 0：商，1：余数 */
    public static BigDecimal[] divideAndRemainder(String s1, String s2) {
        BigDecimal b1 = new BigDecimal(s1);
        return b1.divideAndRemainder(new BigDecimal(s2));
    }

    // 2的60次方
    private static final BigDecimal NUM_2_60 = new BigDecimal(2).pow(60);
    // 0
    private static final BigDecimal NUM_0 = new BigDecimal(0);
    // 16
    private static final BigDecimal NUM_16 = new BigDecimal(16);

    public static BigDecimal parseHexString(String hexString) {
        int length = hexString.length();
        if (length < 16) {
            // long最大值: 7fffffffffffffff, 长度：16
            return new BigDecimal(Long.parseLong(hexString, 16));
        } else {
            // 初始化为0
            BigDecimal bigDecimal = NUM_0;
            // 截取字符串的结束索引(不包含该索引位置的字符)
            int endIndex = length;
            // 截取字符串的开始索引
            int startIndex = length - 15;
            //
            int pow = 0;
            while (endIndex > 0) {
                BigDecimal temp = new BigDecimal(Long.parseLong(hexString.substring(startIndex, endIndex), 16));
                if (pow > 0) {
                    temp = temp.multiply(NUM_2_60.pow(pow));
                }
                bigDecimal = bigDecimal.add(temp);
                if (startIndex == 0) {
                    break;
                }
                endIndex -= 15;
                startIndex -= 15;
                if (startIndex < 0) {
                    startIndex = 0;
                }
                pow ++;
            }
            return bigDecimal;
        }
    }

    /** 转16进制字符串 */
    public static String toHexString(BigDecimal bigDecimal) {
        StringBuilder builder = new StringBuilder();
        do {
            // 除以 16
            BigDecimal[] bigDecimals = bigDecimal.divideAndRemainder(NUM_16);
            // 16的整数倍
            bigDecimal = bigDecimals[0];
            // 余数
            int remainder;
            if (bigDecimals.length > 1) {
                remainder = bigDecimals[1].intValue();
            } else {
                remainder = 0;
            }
            if (remainder > 9) {
                builder.insert(0, (char) (remainder - 10 + 'a'));
            } else {
                builder.insert(0, remainder);
            }
        } while (bigDecimal.compareTo(NUM_0) > 0);
        return builder.toString();
    }

    /**
     * 开方
     * @param value 被开方数
     * @param scale 保留小数位
     * @return 结果
     */
    public static BigDecimal sqrt(BigDecimal value, int scale){
        BigDecimal num2 = BigDecimal.valueOf(2);
        int precision = 100;
        MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
        BigDecimal deviation = value;
        int cnt = 0;
        while (cnt < precision) {
            deviation = (deviation.add(value.divide(deviation, mc))).divide(num2, mc);
            cnt++;
        }
        deviation = deviation.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return deviation;
    }
}
