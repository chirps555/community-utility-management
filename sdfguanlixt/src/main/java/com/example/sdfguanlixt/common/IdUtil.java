package com.example.sdfguanlixt.common;

import java.util.concurrent.ThreadLocalRandom;

public final class IdUtil {

    private IdUtil() {
    }

    public static String genId(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_"
                + Long.toString(ThreadLocalRandom.current().nextLong(1L << 30), 36);
    }
}
