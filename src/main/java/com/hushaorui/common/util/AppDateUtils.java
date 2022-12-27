package com.hushaorui.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public abstract class AppDateUtils {
    public static final String YYYY_MM_DD_HH_MM_SS_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS_STRING = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYY_MM_DD_STRING = "yyyy-MM-dd";
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_STRING);
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS_STRING);
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern(YYYY_MM_DD_STRING);
    private static TimeZone timeZone = TimeZone.getDefault();
    private static ZoneOffset zoneOffset;
    static {
        String of;
        int rawOffset = timeZone.getRawOffset();
        if (rawOffset > 0) {
            of = "+" + (rawOffset / 3600000);
        } else {
            of = String.valueOf(rawOffset / 3600000);
        }
        zoneOffset = ZoneOffset.of(of);
    }

    public static LocalDateTime timestampToDateTime(long timestamp){
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, zoneOffset.normalized());
    }

    public static long dataTimeToTimestamp(LocalDateTime localDateTime){
        return localDateTime.toInstant(zoneOffset).toEpochMilli();
    }

    public static String toYYYY_MM_DD(long timestamp) {
        return YYYY_MM_DD.format(timestampToDateTime(timestamp));
    }
}
