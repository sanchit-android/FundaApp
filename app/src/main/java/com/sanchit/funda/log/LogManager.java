package com.sanchit.funda.log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class LogManager {

    private static final List<String> _LOGS = new ArrayList<>();

    public static void log(String message) {
        _LOGS.add("INFO  " + message);
    }

    public static void log(Class clazz, Exception e) {
        _LOGS.add("ERROR " + clazz.getName() + ": " + e.getClass().getCanonicalName() + ": " + e.getMessage());
    }

    public static void clean() {
        _LOGS.clear();
    }

    public static List<String> getLogs() {
        return _LOGS;
    }


}
