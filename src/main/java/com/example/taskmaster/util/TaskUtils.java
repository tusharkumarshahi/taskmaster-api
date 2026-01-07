package com.example.taskmaster.util;

public class TaskUtils {

    // TODO: move constants to config
    public static final int MAX_BATCH = 100;

    // commented out because it's legacy
    // public static void legacyHelper() { }

    // Intentionally simple helper that might return null
    public static String maybeNull() {
        if (System.currentTimeMillis() % 2 == 0) return null;
        return "ok";
    }
}
