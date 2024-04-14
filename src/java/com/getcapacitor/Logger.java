package com.getcapacitor;

import android.text.TextUtils;
import android.util.Log;

public class Logger
{
    public static final String LOG_TAG_CORE = "Capacitor";
    public static CapConfig config;
    private static Logger instance;
    
    public static void debug(final String s) {
        debug("Capacitor", s);
    }
    
    public static void debug(final String s, final String s2) {
        if (!shouldLog()) {
            return;
        }
        Log.d(s, s2);
    }
    
    public static void error(final String s) {
        error("Capacitor", s, null);
    }
    
    public static void error(final String s, final String s2, final Throwable t) {
        if (!shouldLog()) {
            return;
        }
        Log.e(s, s2, t);
    }
    
    public static void error(final String s, final Throwable t) {
        error("Capacitor", s, t);
    }
    
    private static Logger getInstance() {
        if (Logger.instance == null) {
            Logger.instance = new Logger();
        }
        return Logger.instance;
    }
    
    public static void info(final String s) {
        info("Capacitor", s);
    }
    
    public static void info(final String s, final String s2) {
        if (!shouldLog()) {
            return;
        }
        Log.i(s, s2);
    }
    
    public static void init(final CapConfig capConfig) {
        getInstance().loadConfig(capConfig);
    }
    
    private void loadConfig(final CapConfig config) {
        Logger.config = config;
    }
    
    public static boolean shouldLog() {
        final CapConfig config = Logger.config;
        return config == null || config.isLoggingEnabled();
    }
    
    public static String tags(final String... array) {
        if (array != null && array.length > 0) {
            final StringBuilder sb = new StringBuilder("Capacitor/");
            sb.append(TextUtils.join((CharSequence)"/", (Object[])array));
            return sb.toString();
        }
        return "Capacitor";
    }
    
    public static void verbose(final String s) {
        verbose("Capacitor", s);
    }
    
    public static void verbose(final String s, final String s2) {
        if (!shouldLog()) {
            return;
        }
        Log.v(s, s2);
    }
    
    public static void warn(final String s) {
        warn("Capacitor", s);
    }
    
    public static void warn(final String s, final String s2) {
        if (!shouldLog()) {
            return;
        }
        Log.w(s, s2);
    }
}
