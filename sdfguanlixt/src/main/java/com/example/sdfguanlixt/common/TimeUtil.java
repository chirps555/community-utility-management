package com.example.sdfguanlixt.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeUtil {

    private static final DateTimeFormatter DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TimeUtil() {
    }

    public static String nowDateTime() {
        return LocalDateTime.now().format(DATETIME);
    }

    public static String today() {
        return LocalDate.now().toString();
    }
}
