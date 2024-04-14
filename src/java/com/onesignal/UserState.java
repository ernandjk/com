package com.onesignal;

import java.util.List;
import java.util.Iterator;
import java.util.Map$Entry;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.json.JSONObject;
import java.util.Set;

abstract class UserState
{
    public static final int DEVICE_TYPE_ANDROID = 1;
    public static final int DEVICE_TYPE_EMAIL = 11;
    public static final int DEVICE_TYPE_FIREOS = 2;
    public static final int DEVICE_TYPE_HUAWEI = 13;
    public static final int DEVICE_TYPE_SMS = 14;
    private static final String[] LOCATION_FIELDS;
    private static final Set<String> LOCATION_FIELDS_SET;
    private static final Object LOCK;
    public static final int PUSH_STATUS_FIREBASE_FCM_ERROR_IOEXCEPTION_AUTHENTICATION_FAILED = -29;
    static final int PUSH_STATUS_FIREBASE_FCM_ERROR_IOEXCEPTION_OTHER = -11;
    static final int PUSH_STATUS_FIREBASE_FCM_ERROR_IOEXCEPTION_SERVICE_NOT_AVAILABLE = -9;
    static final int PUSH_STATUS_FIREBASE_FCM_ERROR_MISC_EXCEPTION = -12;
    static final int PUSH_STATUS_FIREBASE_FCM_INIT_ERROR = -8;
    public static final int PUSH_STATUS_HMS_API_EXCEPTION_OTHER = -27;
    public static final int PUSH_STATUS_HMS_ARGUMENTS_INVALID = -26;
    public static final int PUSH_STATUS_HMS_TOKEN_TIMEOUT = -25;
    static final int PUSH_STATUS_INVALID_FCM_SENDER_ID = -6;
    static final int PUSH_STATUS_MISSING_ANDROID_SUPPORT_LIBRARY = -3;
    static final int PUSH_STATUS_MISSING_FIREBASE_FCM_LIBRARY = -4;
    public static final int PUSH_STATUS_MISSING_HMS_PUSHKIT_LIBRARY = -28;
    static final int PUSH_STATUS_NO_PERMISSION = 0;
    static final int PUSH_STATUS_OUTDATED_ANDROID_SUPPORT_LIBRARY = -5;
    static final int PUSH_STATUS_OUTDATED_GOOGLE_PLAY_SERVICES_APP = -7;
    public static final int PUSH_STATUS_SUBSCRIBED = 1;
    static final int PUSH_STATUS_UNSUBSCRIBE = -2;
    public static final String TAGS = "tags";
    private JSONObject dependValues;
    private String persistKey;
    private JSONObject syncValues;
    
    static {
        LOCK = new Object();
        LOCATION_FIELDS_SET = (Set)new HashSet((Collection)Arrays.asList((Object[])(LOCATION_FIELDS = new String[] { "lat", "long", "loc_acc", "loc_type", "loc_bg", "loc_time_stamp" })));
    }
    
    UserState(final String persistKey, final boolean b) {
        this.persistKey = persistKey;
        if (b) {
            this.loadState();
        }
        else {
            this.dependValues = new JSONObject();
            this.syncValues = new JSONObject();
        }
    }
    
    private static JSONObject generateJsonDiff(JSONObject generateJsonDiff, final JSONObject jsonObject, final JSONObject jsonObject2, final Set<String> set) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            generateJsonDiff = JSONUtils.generateJsonDiff(generateJsonDiff, jsonObject, jsonObject2, (Set)set);
            return generateJsonDiff;
        }
    }
    
    private Set<String> getGroupChangeFields(final UserState userState) {
        try {
            if (this.dependValues.optLong("loc_time_stamp") != userState.dependValues.getLong("loc_time_stamp")) {
                final HashMap hashMap = new HashMap();
                hashMap.put((Object)"loc_bg", userState.dependValues.opt("loc_bg"));
                hashMap.put((Object)"loc_time_stamp", userState.dependValues.opt("loc_time_stamp"));
                this.putValues(userState.syncValues, (HashMap<String, Object>)hashMap);
                return UserState.LOCATION_FIELDS_SET;
            }
            return null;
        }
        finally {
            return null;
        }
    }
    
    private void loadState() {
        final String prefs_ONESIGNAL = OneSignalPrefs.PREFS_ONESIGNAL;
        final StringBuilder sb = new StringBuilder("ONESIGNAL_USERSTATE_DEPENDVALYES_");
        sb.append(this.persistKey);
        final String string = OneSignalPrefs.getString(prefs_ONESIGNAL, sb.toString(), (String)null);
        Label_0156: {
            if (string != null) {
                break Label_0156;
            }
            this.setDependValues(new JSONObject());
        Label_0257_Outer:
            while (true) {
                try {
                    final boolean equals = this.persistKey.equals((Object)"CURRENT_STATE");
                    final int n = 1;
                    int n2;
                    if (equals) {
                        n2 = OneSignalPrefs.getInt(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_SUBSCRIPTION", 1);
                    }
                    else {
                        n2 = OneSignalPrefs.getInt(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_SYNCED_SUBSCRIPTION", 1);
                    }
                    boolean b;
                    if (n2 == -2) {
                        b = false;
                        n2 = n;
                    }
                    else {
                        b = true;
                    }
                    final HashMap hashMap = new HashMap();
                    hashMap.put((Object)"subscribableStatus", (Object)n2);
                    hashMap.put((Object)"userSubscribePref", (Object)b);
                    this.putValues(this.dependValues, (HashMap<String, Object>)hashMap);
                    final String prefs_ONESIGNAL2 = OneSignalPrefs.PREFS_ONESIGNAL;
                    final StringBuilder sb2 = new StringBuilder("ONESIGNAL_USERSTATE_SYNCVALYES_");
                    sb2.append(this.persistKey);
                    final String string2 = OneSignalPrefs.getString(prefs_ONESIGNAL2, sb2.toString(), (String)null);
                    JSONObject syncValues = new JSONObject();
                    while (true) {
                        if (string2 == null) {
                            Label_0282: {
                                try {
                                    syncValues.put("identifier", (Object)OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "GT_REGISTRATION_ID", (String)null));
                                    break Label_0282;
                                    syncValues = new JSONObject(string2);
                                }
                                catch (final JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            this.setSyncValues(syncValues);
                            return;
                        }
                        continue;
                    }
                    try {
                        this.setDependValues(new JSONObject(string));
                    }
                    catch (final JSONException ex2) {
                        ex2.printStackTrace();
                    }
                    continue Label_0257_Outer;
                }
                catch (final JSONException ex3) {
                    continue;
                }
                break;
            }
        }
    }
    
    private void putValues(final JSONObject jsonObject, final HashMap<String, Object> hashMap) throws JSONException {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            for (final Map$Entry map$Entry : hashMap.entrySet()) {
                jsonObject.put((String)map$Entry.getKey(), map$Entry.getValue());
            }
        }
    }
    
    protected abstract void addDependFields();
    
    void clearLocation() {
        try {
            final HashMap hashMap = new HashMap();
            hashMap.put((Object)"lat", (Object)null);
            hashMap.put((Object)"long", (Object)null);
            hashMap.put((Object)"loc_acc", (Object)null);
            hashMap.put((Object)"loc_type", (Object)null);
            hashMap.put((Object)"loc_bg", (Object)null);
            hashMap.put((Object)"loc_time_stamp", (Object)null);
            this.putValues(this.syncValues, (HashMap<String, Object>)hashMap);
            final HashMap hashMap2 = new HashMap();
            hashMap2.put((Object)"loc_bg", (Object)null);
            hashMap2.put((Object)"loc_time_stamp", (Object)null);
            this.putValues(this.dependValues, (HashMap<String, Object>)hashMap2);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    UserState deepClone(final String s) {
        final UserState instance = this.newInstance(s);
        try {
            instance.dependValues = this.getDependValuesCopy();
            instance.syncValues = this.getSyncValuesCopy();
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return instance;
    }
    
    JSONObject generateJsonDiff(final UserState userState, final boolean b) {
        this.addDependFields();
        userState.addDependFields();
        final JSONObject generateJsonDiff = generateJsonDiff(this.syncValues, userState.syncValues, null, this.getGroupChangeFields(userState));
        if (!b && generateJsonDiff.toString().equals((Object)"{}")) {
            return null;
        }
        try {
            if (!generateJsonDiff.has("app_id")) {
                generateJsonDiff.put("app_id", (Object)this.syncValues.optString("app_id"));
            }
            if (this.syncValues.has("email_auth_hash")) {
                generateJsonDiff.put("email_auth_hash", (Object)this.syncValues.optString("email_auth_hash"));
            }
            if (this.syncValues.has("sms_auth_hash")) {
                generateJsonDiff.put("sms_auth_hash", (Object)this.syncValues.optString("sms_auth_hash"));
            }
            if (this.syncValues.has("external_user_id_auth_hash") && !generateJsonDiff.has("external_user_id_auth_hash")) {
                generateJsonDiff.put("external_user_id_auth_hash", (Object)this.syncValues.optString("external_user_id_auth_hash"));
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return generateJsonDiff;
    }
    
    JSONObject generateJsonDiffFromDependValues(final UserState userState, final Set<String> set) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            return JSONUtils.generateJsonDiff(this.dependValues, userState.dependValues, (JSONObject)null, (Set)set);
        }
    }
    
    JSONObject generateJsonDiffFromIntoDependValues(JSONObject generateJsonDiff, final Set<String> set) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            final JSONObject dependValues = this.dependValues;
            generateJsonDiff = JSONUtils.generateJsonDiff(dependValues, generateJsonDiff, dependValues, (Set)set);
            return generateJsonDiff;
        }
    }
    
    JSONObject generateJsonDiffFromIntoSyncValued(JSONObject generateJsonDiff, final Set<String> set) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            final JSONObject syncValues = this.syncValues;
            generateJsonDiff = JSONUtils.generateJsonDiff(syncValues, generateJsonDiff, syncValues, (Set)set);
            return generateJsonDiff;
        }
    }
    
    JSONObject generateJsonDiffFromSyncValued(final UserState userState, final Set<String> set) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            return JSONUtils.generateJsonDiff(this.syncValues, userState.syncValues, (JSONObject)null, (Set)set);
        }
    }
    
    public ImmutableJSONObject getDependValues() {
        try {
            return new ImmutableJSONObject(this.getDependValuesCopy());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return new ImmutableJSONObject();
        }
    }
    
    JSONObject getDependValuesCopy() throws JSONException {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            return new JSONObject(this.dependValues.toString());
        }
    }
    
    public ImmutableJSONObject getSyncValues() {
        try {
            return new ImmutableJSONObject(this.getSyncValuesCopy());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return new ImmutableJSONObject();
        }
    }
    
    public JSONObject getSyncValuesCopy() throws JSONException {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            return new JSONObject(this.syncValues.toString());
        }
    }
    
    abstract boolean isSubscribed();
    
    void mergeTags(JSONObject optJSONObject, final JSONObject jsonObject) {
        if (!optJSONObject.has("tags")) {
            return;
        }
        try {
            final JSONObject syncValuesCopy = this.getSyncValuesCopy();
            JSONObject jsonObject2;
            if (syncValuesCopy.has("tags")) {
                try {
                    jsonObject2 = new JSONObject(syncValuesCopy.optString("tags"));
                }
                catch (final JSONException ex) {
                    jsonObject2 = new JSONObject();
                }
            }
            else {
                jsonObject2 = new JSONObject();
            }
            optJSONObject = optJSONObject.optJSONObject("tags");
            final Iterator keys = optJSONObject.keys();
            while (keys.hasNext()) {
                final String s = (String)keys.next();
                if ("".equals((Object)optJSONObject.optString(s))) {
                    jsonObject2.remove(s);
                }
                else {
                    if (jsonObject != null && jsonObject.has(s)) {
                        continue;
                    }
                    jsonObject2.put(s, (Object)optJSONObject.optString(s));
                }
            }
            final Object lock = UserState.LOCK;
            synchronized (lock) {
                if (jsonObject2.toString().equals((Object)"{}")) {
                    this.syncValues.remove("tags");
                }
                else {
                    this.syncValues.put("tags", (Object)jsonObject2);
                }
            }
        }
        catch (final JSONException ex2) {
            ex2.printStackTrace();
        }
    }
    
    abstract UserState newInstance(final String p0);
    
    void persistState() {
        final Object lock;
        monitorenter(lock = UserState.LOCK);
        try {
            Label_0090: {
                try {
                    if (this.syncValues.has("external_user_id_auth_hash") && ((this.syncValues.has("external_user_id") && this.syncValues.get("external_user_id").toString() == "") || !this.syncValues.has("external_user_id"))) {
                        this.syncValues.remove("external_user_id_auth_hash");
                    }
                    break Label_0090;
                }
                finally {
                    monitorexit(lock);
                    final String prefs_ONESIGNAL = OneSignalPrefs.PREFS_ONESIGNAL;
                    final StringBuilder sb = new StringBuilder("ONESIGNAL_USERSTATE_SYNCVALYES_");
                    sb.append(this.persistKey);
                    OneSignalPrefs.saveString(prefs_ONESIGNAL, sb.toString(), this.syncValues.toString());
                    final String prefs_ONESIGNAL2 = OneSignalPrefs.PREFS_ONESIGNAL;
                    final StringBuilder sb2 = new StringBuilder("ONESIGNAL_USERSTATE_DEPENDVALYES_");
                    sb2.append(this.persistKey);
                    OneSignalPrefs.saveString(prefs_ONESIGNAL2, sb2.toString(), this.dependValues.toString());
                    monitorexit(lock);
                }
            }
        }
        catch (final JSONException ex) {}
    }
    
    void persistStateAfterSync(final JSONObject jsonObject, final JSONObject jsonObject2) {
        if (jsonObject != null) {
            final JSONObject dependValues = this.dependValues;
            generateJsonDiff(dependValues, jsonObject, dependValues, null);
        }
        if (jsonObject2 != null) {
            final JSONObject syncValues = this.syncValues;
            generateJsonDiff(syncValues, jsonObject2, syncValues, null);
            this.mergeTags(jsonObject2, null);
        }
        if (jsonObject != null || jsonObject2 != null) {
            this.persistState();
        }
    }
    
    void putOnDependValues(final String s, final Object o) throws JSONException {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            this.dependValues.put(s, o);
        }
    }
    
    void putOnSyncValues(final String s, final Object o) throws JSONException {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            this.syncValues.put(s, o);
        }
    }
    
    void removeFromDependValues(final String s) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            this.dependValues.remove(s);
        }
    }
    
    void removeFromDependValues(final List<String> list) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.dependValues.remove((String)iterator.next());
            }
        }
    }
    
    void removeFromSyncValues(final String s) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            this.syncValues.remove(s);
        }
    }
    
    void removeFromSyncValues(final List<String> list) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.syncValues.remove((String)iterator.next());
            }
        }
    }
    
    public void setDependValues(final JSONObject dependValues) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            this.dependValues = dependValues;
        }
    }
    
    void setLocation(final LocationController$LocationPoint locationController$LocationPoint) {
        try {
            final HashMap hashMap = new HashMap();
            hashMap.put((Object)"lat", (Object)locationController$LocationPoint.lat);
            hashMap.put((Object)"long", (Object)locationController$LocationPoint.log);
            hashMap.put((Object)"loc_acc", (Object)locationController$LocationPoint.accuracy);
            hashMap.put((Object)"loc_type", (Object)locationController$LocationPoint.type);
            this.putValues(this.syncValues, (HashMap<String, Object>)hashMap);
            final HashMap hashMap2 = new HashMap();
            hashMap2.put((Object)"loc_bg", (Object)locationController$LocationPoint.bg);
            hashMap2.put((Object)"loc_time_stamp", (Object)locationController$LocationPoint.timeStamp);
            this.putValues(this.dependValues, (HashMap<String, Object>)hashMap2);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setSyncValues(final JSONObject syncValues) {
        final Object lock = UserState.LOCK;
        synchronized (lock) {
            this.syncValues = syncValues;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserState{persistKey='");
        sb.append(this.persistKey);
        sb.append("', dependValues=");
        sb.append((Object)this.dependValues);
        sb.append(", syncValues=");
        sb.append((Object)this.syncValues);
        sb.append('}');
        return sb.toString();
    }
}
