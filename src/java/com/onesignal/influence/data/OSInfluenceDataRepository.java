package com.onesignal.influence.data;

import com.onesignal.OneSignalRemoteParams$InfluenceParams;
import org.json.JSONException;
import org.json.JSONArray;
import com.onesignal.influence.domain.OSInfluenceType;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSSharedPreferences;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\nJ\u000e\u0010'\u001a\u00020%2\u0006\u0010&\u001a\u00020\nJ\u0010\u0010(\u001a\u00020%2\b\u0010)\u001a\u0004\u0018\u00010\u0006J\u000e\u0010*\u001a\u00020%2\u0006\u0010+\u001a\u00020\u0019J\u000e\u0010,\u001a\u00020%2\u0006\u0010-\u001a\u00020.J\u000e\u0010/\u001a\u00020%2\u0006\u00100\u001a\u00020\u0019R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u00068F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e8F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u000e8F¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0013\u001a\u00020\u00148F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0015R\u0011\u0010\u0016\u001a\u00020\u00148F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0015R\u0011\u0010\u0017\u001a\u00020\u00148F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0015R\u0011\u0010\u0018\u001a\u00020\u00198F¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u001c\u001a\u00020\u00198F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001bR\u0011\u0010\u001e\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b\u001f\u0010\fR\u0011\u0010 \u001a\u00020\u000e8F¢\u0006\u0006\u001a\u0004\b!\u0010\u0010R\u0011\u0010\"\u001a\u00020\u000e8F¢\u0006\u0006\u001a\u0004\b#\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00061" }, d2 = { "Lcom/onesignal/influence/data/OSInfluenceDataRepository;", "", "preferences", "Lcom/onesignal/OSSharedPreferences;", "(Lcom/onesignal/OSSharedPreferences;)V", "cachedNotificationOpenId", "", "getCachedNotificationOpenId", "()Ljava/lang/String;", "iamCachedInfluenceType", "Lcom/onesignal/influence/domain/OSInfluenceType;", "getIamCachedInfluenceType", "()Lcom/onesignal/influence/domain/OSInfluenceType;", "iamIndirectAttributionWindow", "", "getIamIndirectAttributionWindow", "()I", "iamLimit", "getIamLimit", "isDirectInfluenceEnabled", "", "()Z", "isIndirectInfluenceEnabled", "isUnattributedInfluenceEnabled", "lastIAMsReceivedData", "Lorg/json/JSONArray;", "getLastIAMsReceivedData", "()Lorg/json/JSONArray;", "lastNotificationsReceivedData", "getLastNotificationsReceivedData", "notificationCachedInfluenceType", "getNotificationCachedInfluenceType", "notificationIndirectAttributionWindow", "getNotificationIndirectAttributionWindow", "notificationLimit", "getNotificationLimit", "cacheIAMInfluenceType", "", "influenceType", "cacheNotificationInfluenceType", "cacheNotificationOpenId", "id", "saveIAMs", "iams", "saveInfluenceParams", "influenceParams", "Lcom/onesignal/OneSignalRemoteParams$InfluenceParams;", "saveNotifications", "notifications", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSInfluenceDataRepository
{
    private final OSSharedPreferences preferences;
    
    public OSInfluenceDataRepository(final OSSharedPreferences preferences) {
        Intrinsics.checkNotNullParameter((Object)preferences, "preferences");
        this.preferences = preferences;
    }
    
    public final void cacheIAMInfluenceType(final OSInfluenceType osInfluenceType) {
        Intrinsics.checkNotNullParameter((Object)osInfluenceType, "influenceType");
        final OSSharedPreferences preferences = this.preferences;
        preferences.saveString(preferences.getPreferencesName(), "PREFS_OS_OUTCOMES_CURRENT_IAM_INFLUENCE", osInfluenceType.toString());
    }
    
    public final void cacheNotificationInfluenceType(final OSInfluenceType osInfluenceType) {
        Intrinsics.checkNotNullParameter((Object)osInfluenceType, "influenceType");
        final OSSharedPreferences preferences = this.preferences;
        preferences.saveString(preferences.getPreferencesName(), "PREFS_OS_OUTCOMES_CURRENT_SESSION", osInfluenceType.toString());
    }
    
    public final void cacheNotificationOpenId(final String s) {
        final OSSharedPreferences preferences = this.preferences;
        preferences.saveString(preferences.getPreferencesName(), "PREFS_OS_LAST_ATTRIBUTED_NOTIFICATION_OPEN", s);
    }
    
    public final String getCachedNotificationOpenId() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getString(preferences.getPreferencesName(), "PREFS_OS_LAST_ATTRIBUTED_NOTIFICATION_OPEN", (String)null);
    }
    
    public final OSInfluenceType getIamCachedInfluenceType() {
        final String string = OSInfluenceType.UNATTRIBUTED.toString();
        final OSSharedPreferences preferences = this.preferences;
        return OSInfluenceType.Companion.fromString(preferences.getString(preferences.getPreferencesName(), "PREFS_OS_OUTCOMES_CURRENT_IAM_INFLUENCE", string));
    }
    
    public final int getIamIndirectAttributionWindow() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getInt(preferences.getPreferencesName(), "PREFS_OS_IAM_INDIRECT_ATTRIBUTION_WINDOW", 1440);
    }
    
    public final int getIamLimit() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getInt(preferences.getPreferencesName(), "PREFS_OS_IAM_LIMIT", 10);
    }
    
    public final JSONArray getLastIAMsReceivedData() throws JSONException {
        final OSSharedPreferences preferences = this.preferences;
        final String string = preferences.getString(preferences.getPreferencesName(), "PREFS_OS_LAST_IAMS_RECEIVED", "[]");
        JSONArray jsonArray;
        if (string != null) {
            jsonArray = new JSONArray(string);
        }
        else {
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }
    
    public final JSONArray getLastNotificationsReceivedData() throws JSONException {
        final OSSharedPreferences preferences = this.preferences;
        final String string = preferences.getString(preferences.getPreferencesName(), "PREFS_OS_LAST_NOTIFICATIONS_RECEIVED", "[]");
        JSONArray jsonArray;
        if (string != null) {
            jsonArray = new JSONArray(string);
        }
        else {
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }
    
    public final OSInfluenceType getNotificationCachedInfluenceType() {
        final OSSharedPreferences preferences = this.preferences;
        return OSInfluenceType.Companion.fromString(preferences.getString(preferences.getPreferencesName(), "PREFS_OS_OUTCOMES_CURRENT_SESSION", OSInfluenceType.UNATTRIBUTED.toString()));
    }
    
    public final int getNotificationIndirectAttributionWindow() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getInt(preferences.getPreferencesName(), "PREFS_OS_INDIRECT_ATTRIBUTION_WINDOW", 1440);
    }
    
    public final int getNotificationLimit() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getInt(preferences.getPreferencesName(), "PREFS_OS_NOTIFICATION_LIMIT", 10);
    }
    
    public final boolean isDirectInfluenceEnabled() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getBool(preferences.getPreferencesName(), "PREFS_OS_DIRECT_ENABLED", false);
    }
    
    public final boolean isIndirectInfluenceEnabled() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getBool(preferences.getPreferencesName(), "PREFS_OS_INDIRECT_ENABLED", false);
    }
    
    public final boolean isUnattributedInfluenceEnabled() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getBool(preferences.getPreferencesName(), "PREFS_OS_UNATTRIBUTED_ENABLED", false);
    }
    
    public final void saveIAMs(final JSONArray jsonArray) {
        Intrinsics.checkNotNullParameter((Object)jsonArray, "iams");
        final OSSharedPreferences preferences = this.preferences;
        preferences.saveString(preferences.getPreferencesName(), "PREFS_OS_LAST_IAMS_RECEIVED", jsonArray.toString());
    }
    
    public final void saveInfluenceParams(final OneSignalRemoteParams$InfluenceParams oneSignalRemoteParams$InfluenceParams) {
        Intrinsics.checkNotNullParameter((Object)oneSignalRemoteParams$InfluenceParams, "influenceParams");
        final OSSharedPreferences preferences = this.preferences;
        preferences.saveBool(preferences.getPreferencesName(), "PREFS_OS_DIRECT_ENABLED", oneSignalRemoteParams$InfluenceParams.isDirectEnabled());
        final OSSharedPreferences preferences2 = this.preferences;
        preferences2.saveBool(preferences2.getPreferencesName(), "PREFS_OS_INDIRECT_ENABLED", oneSignalRemoteParams$InfluenceParams.isIndirectEnabled());
        final OSSharedPreferences preferences3 = this.preferences;
        preferences3.saveBool(preferences3.getPreferencesName(), "PREFS_OS_UNATTRIBUTED_ENABLED", oneSignalRemoteParams$InfluenceParams.isUnattributedEnabled());
        final OSSharedPreferences preferences4 = this.preferences;
        preferences4.saveInt(preferences4.getPreferencesName(), "PREFS_OS_NOTIFICATION_LIMIT", oneSignalRemoteParams$InfluenceParams.getNotificationLimit());
        final OSSharedPreferences preferences5 = this.preferences;
        preferences5.saveInt(preferences5.getPreferencesName(), "PREFS_OS_INDIRECT_ATTRIBUTION_WINDOW", oneSignalRemoteParams$InfluenceParams.getIndirectNotificationAttributionWindow());
        final OSSharedPreferences preferences6 = this.preferences;
        preferences6.saveInt(preferences6.getPreferencesName(), "PREFS_OS_IAM_LIMIT", oneSignalRemoteParams$InfluenceParams.getIamLimit());
        final OSSharedPreferences preferences7 = this.preferences;
        preferences7.saveInt(preferences7.getPreferencesName(), "PREFS_OS_IAM_INDIRECT_ATTRIBUTION_WINDOW", oneSignalRemoteParams$InfluenceParams.getIndirectIAMAttributionWindow());
    }
    
    public final void saveNotifications(final JSONArray jsonArray) {
        Intrinsics.checkNotNullParameter((Object)jsonArray, "notifications");
        final OSSharedPreferences preferences = this.preferences;
        preferences.saveString(preferences.getPreferencesName(), "PREFS_OS_LAST_NOTIFICATIONS_RECEIVED", jsonArray.toString());
    }
}
