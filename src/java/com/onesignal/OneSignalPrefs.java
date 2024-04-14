package com.onesignal;

import android.content.SharedPreferences$Editor;
import java.util.Iterator;
import android.os.Handler;
import android.os.HandlerThread;
import android.content.SharedPreferences;
import java.util.Set;
import java.util.HashMap;

class OneSignalPrefs
{
    static final String PREFS_EXISTING_PURCHASES = "ExistingPurchases";
    public static final String PREFS_GT_APP_ID = "GT_APP_ID";
    public static final String PREFS_GT_DO_NOT_SHOW_MISSING_GPS = "GT_DO_NOT_SHOW_MISSING_GPS";
    public static final String PREFS_GT_FIREBASE_TRACKING_ENABLED = "GT_FIREBASE_TRACKING_ENABLED";
    public static final String PREFS_GT_PLAYER_ID = "GT_PLAYER_ID";
    public static final String PREFS_GT_REGISTRATION_ID = "GT_REGISTRATION_ID";
    public static final String PREFS_GT_SOUND_ENABLED = "GT_SOUND_ENABLED";
    public static final String PREFS_GT_UNSENT_ACTIVE_TIME = "GT_UNSENT_ACTIVE_TIME";
    public static final String PREFS_GT_VIBRATE_ENABLED = "GT_VIBRATE_ENABLED";
    public static final String PREFS_ONESIGNAL;
    public static final String PREFS_ONESIGNAL_ACCEPTED_NOTIFICATION_LAST = "ONESIGNAL_ACCEPTED_NOTIFICATION_LAST";
    public static final String PREFS_ONESIGNAL_EMAIL_ADDRESS_LAST = "PREFS_ONESIGNAL_EMAIL_ADDRESS_LAST";
    public static final String PREFS_ONESIGNAL_EMAIL_ID_LAST = "PREFS_ONESIGNAL_EMAIL_ID_LAST";
    public static final String PREFS_ONESIGNAL_PERMISSION_ACCEPTED_LAST = "ONESIGNAL_PERMISSION_ACCEPTED_LAST";
    public static final String PREFS_ONESIGNAL_PLAYER_ID_LAST = "ONESIGNAL_PLAYER_ID_LAST";
    public static final String PREFS_ONESIGNAL_PUSH_TOKEN_LAST = "ONESIGNAL_PUSH_TOKEN_LAST";
    public static final String PREFS_ONESIGNAL_SUBSCRIPTION = "ONESIGNAL_SUBSCRIPTION";
    public static final String PREFS_ONESIGNAL_SUBSCRIPTION_LAST = "ONESIGNAL_SUBSCRIPTION_LAST";
    public static final String PREFS_ONESIGNAL_SYNCED_SUBSCRIPTION = "ONESIGNAL_SYNCED_SUBSCRIPTION";
    public static final String PREFS_ONESIGNAL_USERSTATE_DEPENDVALYES_ = "ONESIGNAL_USERSTATE_DEPENDVALYES_";
    public static final String PREFS_ONESIGNAL_USERSTATE_SYNCVALYES_ = "ONESIGNAL_USERSTATE_SYNCVALYES_";
    public static final String PREFS_ONESIGNAL_USER_PROVIDED_CONSENT = "ONESIGNAL_USER_PROVIDED_CONSENT";
    public static final String PREFS_OS_ATTRIBUTED_INFLUENCES = "PREFS_OS_ATTRIBUTED_INFLUENCES";
    public static final String PREFS_OS_CACHED_IAMS = "PREFS_OS_CACHED_IAMS";
    public static final String PREFS_OS_CLEAR_GROUP_SUMMARY_CLICK = "OS_CLEAR_GROUP_SUMMARY_CLICK";
    public static final String PREFS_OS_CLICKED_CLICK_IDS_IAMS = "PREFS_OS_CLICKED_CLICK_IDS_IAMS";
    public static final String PREFS_OS_DISABLE_GMS_MISSING_PROMPT = "PREFS_OS_DISABLE_GMS_MISSING_PROMPT";
    public static final String PREFS_OS_DISMISSED_IAMS = "PREFS_OS_DISPLAYED_IAMS";
    public static final String PREFS_OS_EMAIL_ID = "OS_EMAIL_ID";
    public static final String PREFS_OS_ETAG_PREFIX = "PREFS_OS_ETAG_PREFIX_";
    public static final String PREFS_OS_FILTER_OTHER_GCM_RECEIVERS = "OS_FILTER_OTHER_GCM_RECEIVERS";
    public static final String PREFS_OS_HTTP_CACHE_PREFIX = "PREFS_OS_HTTP_CACHE_PREFIX_";
    public static final String PREFS_OS_IMPRESSIONED_IAMS = "PREFS_OS_IMPRESSIONED_IAMS";
    public static final String PREFS_OS_LAST_LOCATION_TIME = "OS_LAST_LOCATION_TIME";
    public static final String PREFS_OS_LAST_SESSION_TIME = "OS_LAST_SESSION_TIME";
    public static final String PREFS_OS_LAST_TIME_IAM_DISMISSED = "PREFS_OS_LAST_TIME_IAM_DISMISSED";
    public static final String PREFS_OS_LOCATION_SHARED = "PREFS_OS_LOCATION_SHARED";
    static final String PREFS_OS_OUTCOMES_V2 = "PREFS_OS_OUTCOMES_V2";
    public static final String PREFS_OS_PAGE_IMPRESSIONED_IAMS = "PREFS_OS_PAGE_IMPRESSIONED_IAMS";
    public static final String PREFS_OS_RECEIVE_RECEIPTS_ENABLED = "PREFS_OS_RECEIVE_RECEIPTS_ENABLED";
    public static final String PREFS_OS_REQUIRES_USER_PRIVACY_CONSENT = "PREFS_OS_REQUIRES_USER_PRIVACY_CONSENT";
    public static final String PREFS_OS_RESTORE_TTL_FILTER = "OS_RESTORE_TTL_FILTER";
    public static final String PREFS_OS_SMS_ID = "PREFS_OS_SMS_ID";
    public static final String PREFS_OS_SMS_ID_LAST = "PREFS_OS_SMS_ID_LAST";
    public static final String PREFS_OS_SMS_NUMBER_LAST = "PREFS_OS_SMS_NUMBER_LAST";
    public static final String PREFS_OS_UNSENT_ATTRIBUTED_ACTIVE_TIME = "OS_UNSENT_ATTRIBUTED_ACTIVE_TIME";
    public static final String PREFS_OS_UNSUBSCRIBE_WHEN_NOTIFICATIONS_DISABLED = "PREFS_OS_UNSUBSCRIBE_WHEN_NOTIFICATIONS_DISABLED";
    public static final String PREFS_PLAYER_PURCHASES = "GTPlayerPurchases";
    static final String PREFS_PURCHASE_TOKENS = "purchaseTokens";
    public static final String PREFS_TRIGGERS = "OneSignalTriggers";
    public static WritePrefHandlerThread prefsHandler;
    static HashMap<String, HashMap<String, Object>> prefsToApply;
    
    static {
        PREFS_ONESIGNAL = "OneSignal";
        initializePool();
    }
    
    private static Object get(final String s, final String s2, final Class clazz, final Object o) {
        final HashMap hashMap = (HashMap)OneSignalPrefs.prefsToApply.get((Object)s);
        synchronized (hashMap) {
            if (clazz.equals(Object.class) && hashMap.containsKey((Object)s2)) {
                monitorexit(hashMap);
                return true;
            }
            final Object value = hashMap.get((Object)s2);
            if (value != null || hashMap.containsKey((Object)s2)) {
                return value;
            }
            monitorexit(hashMap);
            final SharedPreferences sharedPrefsByName = getSharedPrefsByName(s);
            if (sharedPrefsByName == null) {
                return o;
            }
            if (clazz.equals(String.class)) {
                return sharedPrefsByName.getString(s2, (String)o);
            }
            if (clazz.equals(Boolean.class)) {
                return sharedPrefsByName.getBoolean(s2, (boolean)o);
            }
            if (clazz.equals(Integer.class)) {
                return sharedPrefsByName.getInt(s2, (int)o);
            }
            if (clazz.equals(Long.class)) {
                return sharedPrefsByName.getLong(s2, (long)o);
            }
            if (clazz.equals(Set.class)) {
                return sharedPrefsByName.getStringSet(s2, (Set)o);
            }
            if (clazz.equals(Object.class)) {
                return sharedPrefsByName.contains(s2);
            }
            return null;
        }
    }
    
    static boolean getBool(final String s, final String s2, final boolean b) {
        return (boolean)get(s, s2, Boolean.class, b);
    }
    
    static int getInt(final String s, final String s2, final int n) {
        return (int)get(s, s2, Integer.class, n);
    }
    
    static long getLong(final String s, final String s2, final long n) {
        return (long)get(s, s2, Long.class, n);
    }
    
    static Object getObject(final String s, final String s2, final Object o) {
        return get(s, s2, Object.class, o);
    }
    
    static SharedPreferences getSharedPrefsByName(final String s) {
        synchronized (OneSignalPrefs.class) {
            if (OneSignal.appContext == null) {
                final StringBuilder sb = new StringBuilder("OneSignal.appContext null, could not read ");
                sb.append(s);
                sb.append(" from getSharedPreferences.");
                OneSignal.Log(OneSignal.LOG_LEVEL.WARN, sb.toString(), new Throwable());
                return null;
            }
            return OneSignal.appContext.getSharedPreferences(s, 0);
        }
    }
    
    static String getString(final String s, final String s2, final String s3) {
        return (String)get(s, s2, String.class, s3);
    }
    
    public static Set<String> getStringSet(final String s, final String s2, final Set<String> set) {
        return (Set<String>)get(s, s2, Set.class, set);
    }
    
    public static void initializePool() {
        (OneSignalPrefs.prefsToApply = (HashMap<String, HashMap<String, Object>>)new HashMap()).put((Object)OneSignalPrefs.PREFS_ONESIGNAL, (Object)new HashMap());
        OneSignalPrefs.prefsToApply.put((Object)"GTPlayerPurchases", (Object)new HashMap());
        OneSignalPrefs.prefsToApply.put((Object)"OneSignalTriggers", (Object)new HashMap());
        OneSignalPrefs.prefsHandler = new WritePrefHandlerThread("OSH_WritePrefs");
    }
    
    private static void save(final String s, final String s2, final Object o) {
        final HashMap hashMap = (HashMap)OneSignalPrefs.prefsToApply.get((Object)s);
        synchronized (hashMap) {
            hashMap.put((Object)s2, o);
            startDelayedWrite();
        }
    }
    
    public static void saveBool(final String s, final String s2, final boolean b) {
        save(s, s2, b);
    }
    
    public static void saveInt(final String s, final String s2, final int n) {
        save(s, s2, n);
    }
    
    public static void saveLong(final String s, final String s2, final long n) {
        save(s, s2, n);
    }
    
    public static void saveObject(final String s, final String s2, final Object o) {
        save(s, s2, o);
    }
    
    public static void saveString(final String s, final String s2, final String s3) {
        save(s, s2, s3);
    }
    
    public static void saveStringSet(final String s, final String s2, final Set<String> set) {
        save(s, s2, set);
    }
    
    public static void startDelayedWrite() {
        OneSignalPrefs.prefsHandler.startDelayedWrite();
    }
    
    public static class WritePrefHandlerThread extends HandlerThread
    {
        private static final int WRITE_CALL_DELAY_TO_BUFFER_MS = 200;
        private long lastSyncTime;
        private Handler mHandler;
        private boolean threadStartCalled;
        
        WritePrefHandlerThread(final String s) {
            super(s);
            this.lastSyncTime = 0L;
        }
        
        private void flushBufferToDisk() {
            for (String s : OneSignalPrefs.prefsToApply.keySet()) {
                final SharedPreferences$Editor edit = OneSignalPrefs.getSharedPrefsByName(s).edit();
                final HashMap hashMap = (HashMap)OneSignalPrefs.prefsToApply.get((Object)s);
                synchronized (hashMap) {
                    for (final String s2 : hashMap.keySet()) {
                        final Object value = hashMap.get((Object)s2);
                        if (value instanceof String) {
                            edit.putString(s2, (String)value);
                        }
                        else if (value instanceof Boolean) {
                            edit.putBoolean(s2, (boolean)value);
                        }
                        else if (value instanceof Integer) {
                            edit.putInt(s2, (int)value);
                        }
                        else if (value instanceof Long) {
                            edit.putLong(s2, (long)value);
                        }
                        else if (value instanceof Set) {
                            edit.putStringSet(s2, (Set)value);
                        }
                        else {
                            if (value != null) {
                                continue;
                            }
                            edit.remove(s2);
                        }
                    }
                    hashMap.clear();
                    monitorexit(hashMap);
                    edit.apply();
                    continue;
                }
                break;
            }
            this.lastSyncTime = OneSignal.getTime().getCurrentTimeMillis();
        }
        
        private void scheduleFlushToDisk() {
            synchronized (this) {
                final Handler mHandler = this.mHandler;
                if (mHandler == null) {
                    return;
                }
                mHandler.removeCallbacksAndMessages((Object)null);
                if (this.lastSyncTime == 0L) {
                    this.lastSyncTime = OneSignal.getTime().getCurrentTimeMillis();
                }
                this.mHandler.postDelayed((Runnable)new Runnable(this) {
                    final WritePrefHandlerThread this$0;
                    
                    public void run() {
                        this.this$0.flushBufferToDisk();
                    }
                }, this.lastSyncTime - OneSignal.getTime().getCurrentTimeMillis() + 200L);
            }
        }
        
        private void startDelayedWrite() {
            synchronized (this) {
                if (OneSignal.appContext == null) {
                    return;
                }
                this.startThread();
                this.scheduleFlushToDisk();
            }
        }
        
        private void startThread() {
            if (this.threadStartCalled) {
                return;
            }
            this.start();
            this.threadStartCalled = true;
        }
        
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            this.mHandler = new Handler(this.getLooper());
            this.scheduleFlushToDisk();
        }
    }
}
