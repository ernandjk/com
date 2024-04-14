package com.onesignal;

import org.json.JSONObject;
import android.content.ContentValues;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.Collection;
import java.util.Set;

class OSInAppMessageRepository
{
    static final long IAM_CACHE_DATA_LIFETIME = 15552000L;
    static final String IAM_DATA_RESPONSE_RETRY_KEY = "retry";
    private final OneSignalDbHelper dbHelper;
    private int htmlNetworkRequestAttemptCount;
    private final OSLogger logger;
    private final OSSharedPreferences sharedPreferences;
    
    OSInAppMessageRepository(final OneSignalDbHelper dbHelper, final OSLogger logger, final OSSharedPreferences sharedPreferences) {
        this.htmlNetworkRequestAttemptCount = 0;
        this.dbHelper = dbHelper;
        this.logger = logger;
        this.sharedPreferences = sharedPreferences;
    }
    
    private void cleanInAppMessageClickedClickIds(final Set<String> set) {
        if (set != null && set.size() > 0) {
            final Set<String> stringSet = OneSignalPrefs.getStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_CLICKED_CLICK_IDS_IAMS", null);
            if (stringSet != null && stringSet.size() > 0) {
                stringSet.removeAll((Collection)set);
                OneSignalPrefs.saveStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_CLICKED_CLICK_IDS_IAMS", stringSet);
            }
        }
    }
    
    private void cleanInAppMessageIds(final Set<String> set) {
        if (set != null && set.size() > 0) {
            final Set<String> stringSet = OneSignalPrefs.getStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_DISPLAYED_IAMS", null);
            final Set<String> stringSet2 = OneSignalPrefs.getStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_IMPRESSIONED_IAMS", null);
            if (stringSet != null && stringSet.size() > 0) {
                stringSet.removeAll((Collection)set);
                OneSignalPrefs.saveStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_DISPLAYED_IAMS", stringSet);
            }
            if (stringSet2 != null && stringSet2.size() > 0) {
                stringSet2.removeAll((Collection)set);
                OneSignalPrefs.saveStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_IMPRESSIONED_IAMS", stringSet2);
            }
        }
    }
    
    private String htmlPathForMessage(final String s, final String s2, final String s3) {
        if (s2 == null) {
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("Unable to find a variant for in-app message ");
            sb.append(s);
            logger.error(sb.toString());
            return null;
        }
        final StringBuilder sb2 = new StringBuilder("in_app_messages/");
        sb2.append(s);
        sb2.append("/variants/");
        sb2.append(s2);
        sb2.append("/html?app_id=");
        sb2.append(s3);
        return sb2.toString();
    }
    
    private void printHttpErrorForInAppMessageRequest(final String s, final int n, final String s2) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("Encountered a ");
        sb.append(n);
        sb.append(" error while attempting in-app message ");
        sb.append(s);
        sb.append(" request: ");
        sb.append(s2);
        logger.error(sb.toString());
    }
    
    private void printHttpSuccessForInAppMessageRequest(final String s, final String s2) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("Successful post for in-app message ");
        sb.append(s);
        sb.append(" request: ");
        sb.append(s2);
        logger.debug(sb.toString());
    }
    
    private void saveClickedMessagesId(final Set<String> set) {
        this.sharedPreferences.saveStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_CLICKED_CLICK_IDS_IAMS", set);
    }
    
    private void saveImpressionedMessages(final Set<String> set) {
        this.sharedPreferences.saveStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_IMPRESSIONED_IAMS", set);
    }
    
    void cleanCachedInAppMessages() {
        synchronized (this) {
            final String[] array = { String.valueOf(System.currentTimeMillis() / 1000L - 15552000L) };
            final java.util.Set<String> concurrentSet = OSUtils.newConcurrentSet();
            final java.util.Set<String> concurrentSet2 = OSUtils.newConcurrentSet();
            Cursor cursor = null;
            try {
                Label_0367: {
                    try {
                        final Cursor query = this.dbHelper.query("in_app_message", new String[] { "message_id", "click_ids" }, "last_display < ?", array, (String)null, (String)null, (String)null);
                        if (query != null) {
                            cursor = query;
                            if (query.getCount() != 0) {
                                cursor = query;
                                if (query.moveToFirst()) {
                                    do {
                                        cursor = query;
                                        final String string = query.getString(query.getColumnIndex("message_id"));
                                        cursor = query;
                                        final String string2 = query.getString(query.getColumnIndex("click_ids"));
                                        cursor = query;
                                        concurrentSet.add((Object)string);
                                        cursor = query;
                                        cursor = query;
                                        final JSONArray jsonArray = new JSONArray(string2);
                                        cursor = query;
                                        concurrentSet2.addAll((Collection)OSUtils.newStringSetFromJSONArray(jsonArray));
                                        cursor = query;
                                    } while (query.moveToNext());
                                }
                                if (query != null && !query.isClosed()) {
                                    final Cursor cursor2 = query;
                                    cursor2.close();
                                }
                                break Label_0367;
                            }
                        }
                        cursor = query;
                        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "Attempted to clean 6 month old IAM data, but none exists!");
                        if (query != null && !query.isClosed()) {
                            query.close();
                        }
                    }
                    finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        this.dbHelper.delete("in_app_message", "last_display < ?", array);
                        this.cleanInAppMessageIds(concurrentSet);
                        this.cleanInAppMessageClickedClickIds(concurrentSet2);
                        return;
                        final Cursor cursor2;
                        iftrue(Label_0367:)(cursor2.isClosed());
                    }
                }
            }
            catch (final JSONException ex) {}
        }
    }
    
    List<OSInAppMessageInternal> getCachedInAppMessages() {
        synchronized (this) {
            final ArrayList list = new ArrayList();
            Cursor query = null;
            try {
                while (true) {
                    try {
                        final Cursor cursor = query = this.dbHelper.query("in_app_message", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
                        if (cursor.moveToFirst()) {
                            do {
                                query = cursor;
                                final String string = cursor.getString(cursor.getColumnIndex("message_id"));
                                query = cursor;
                                final String string2 = cursor.getString(cursor.getColumnIndex("click_ids"));
                                query = cursor;
                                final int int1 = cursor.getInt(cursor.getColumnIndex("display_quantity"));
                                query = cursor;
                                final long long1 = cursor.getLong(cursor.getColumnIndex("last_display"));
                                query = cursor;
                                final int int2 = cursor.getInt(cursor.getColumnIndex("displayed_in_session"));
                                boolean b = true;
                                if (int2 != 1) {
                                    b = false;
                                }
                                query = cursor;
                                query = cursor;
                                final JSONArray jsonArray = new JSONArray(string2);
                                query = cursor;
                                final Set<String> stringSetFromJSONArray = OSUtils.newStringSetFromJSONArray(jsonArray);
                                query = cursor;
                                query = cursor;
                                query = cursor;
                                final OSInAppMessageRedisplayStats osInAppMessageRedisplayStats = new OSInAppMessageRedisplayStats(int1, long1);
                                query = cursor;
                                final OSInAppMessageInternal osInAppMessageInternal = new OSInAppMessageInternal(string, (Set)stringSetFromJSONArray, b, osInAppMessageRedisplayStats);
                                query = cursor;
                                ((List)list).add((Object)osInAppMessageInternal);
                                query = cursor;
                            } while (cursor.moveToNext());
                        }
                        if (cursor != null && !cursor.isClosed()) {
                            final Cursor cursor2 = cursor;
                            cursor2.close();
                            return (List<OSInAppMessageInternal>)list;
                        }
                        return (List<OSInAppMessageInternal>)list;
                    }
                    finally {
                        if (query != null && !query.isClosed()) {
                            query.close();
                        }
                        Label_0413: {
                            return (List<OSInAppMessageInternal>)list;
                        }
                        continue;
                        final Cursor cursor2;
                        iftrue(Label_0413:)(cursor2.isClosed());
                    }
                    break;
                }
            }
            catch (final JSONException ex) {}
        }
    }
    
    Set<String> getClickedMessagesId() {
        return this.sharedPreferences.getStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_CLICKED_CLICK_IDS_IAMS", null);
    }
    
    Set<String> getDismissedMessagesId() {
        return this.sharedPreferences.getStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_DISPLAYED_IAMS", null);
    }
    
    void getIAMData(final String s, final String s2, final String s3, final OSInAppMessageRequestResponse osInAppMessageRequestResponse) {
        OneSignalRestClient.get(this.htmlPathForMessage(s2, s3, s), (OneSignalRestClient.ResponseHandler)new OSInAppMessageRepository$8(this, osInAppMessageRequestResponse), null);
    }
    
    void getIAMPreviewData(final String s, final String s2, final OSInAppMessageRequestResponse osInAppMessageRequestResponse) {
        final StringBuilder sb = new StringBuilder("in_app_messages/device_preview?preview_id=");
        sb.append(s2);
        sb.append("&app_id=");
        sb.append(s);
        OneSignalRestClient.get(sb.toString(), (OneSignalRestClient.ResponseHandler)new OSInAppMessageRepository$7(this, osInAppMessageRequestResponse), null);
    }
    
    Set<String> getImpressionesMessagesId() {
        return this.sharedPreferences.getStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_IMPRESSIONED_IAMS", null);
    }
    
    Date getLastTimeInAppDismissed() {
        final String string = this.sharedPreferences.getString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_LAST_TIME_IAM_DISMISSED", null);
        if (string == null) {
            return null;
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            return simpleDateFormat.parse(string);
        }
        catch (final ParseException ex) {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.ERROR, ex.getLocalizedMessage());
            return null;
        }
    }
    
    String getSavedIAMs() {
        return this.sharedPreferences.getString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_CACHED_IAMS", null);
    }
    
    Set<String> getViewPageImpressionedIds() {
        return this.sharedPreferences.getStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_PAGE_IMPRESSIONED_IAMS", null);
    }
    
    void saveDismissedMessagesId(final Set<String> set) {
        this.sharedPreferences.saveStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_DISPLAYED_IAMS", set);
    }
    
    void saveIAMs(final String s) {
        this.sharedPreferences.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_CACHED_IAMS", s);
    }
    
    void saveInAppMessage(final OSInAppMessageInternal osInAppMessageInternal) {
        synchronized (this) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put("message_id", osInAppMessageInternal.messageId);
            contentValues.put("display_quantity", Integer.valueOf(osInAppMessageInternal.getRedisplayStats().getDisplayQuantity()));
            contentValues.put("last_display", Long.valueOf(osInAppMessageInternal.getRedisplayStats().getLastDisplayTime()));
            contentValues.put("click_ids", osInAppMessageInternal.getClickedClickIds().toString());
            contentValues.put("displayed_in_session", Boolean.valueOf(osInAppMessageInternal.isDisplayedInSession()));
            if (this.dbHelper.update("in_app_message", contentValues, "message_id = ?", new String[] { osInAppMessageInternal.messageId }) == 0) {
                this.dbHelper.insert("in_app_message", (String)null, contentValues);
            }
        }
    }
    
    void saveLastTimeInAppDismissed(final Date date) {
        String string;
        if (date != null) {
            string = date.toString();
        }
        else {
            string = null;
        }
        this.sharedPreferences.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_LAST_TIME_IAM_DISMISSED", string);
    }
    
    void saveViewPageImpressionedIds(final Set<String> set) {
        this.sharedPreferences.saveStringSet(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_PAGE_IMPRESSIONED_IAMS", set);
    }
    
    void sendIAMClick(final String s, String string, final String s2, final int n, final String s3, final String s4, final boolean b, final Set<String> set, final OSInAppMessageRequestResponse osInAppMessageRequestResponse) {
        try {
            final JSONObject jsonObject = new JSONObject(this, s, n, string, s4, s2, b) {
                final OSInAppMessageRepository this$0;
                final String val$appId;
                final String val$clickId;
                final int val$deviceType;
                final boolean val$isFirstClick;
                final String val$userId;
                final String val$variantId;
                
                {
                    this.put("app_id", (Object)val$appId);
                    this.put("device_type", val$deviceType);
                    this.put("player_id", (Object)val$userId);
                    this.put("click_id", (Object)val$clickId);
                    this.put("variant_id", (Object)val$variantId);
                    if (val$isFirstClick) {
                        this.put("first_click", true);
                    }
                }
            };
            final StringBuilder sb = new StringBuilder("in_app_messages/");
            sb.append(s3);
            sb.append("/click");
            string = sb.toString();
            OneSignalRestClient.post(string, jsonObject, (OneSignalRestClient.ResponseHandler)new OSInAppMessageRepository$2(this, (Set)set, osInAppMessageRequestResponse));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            this.logger.error("Unable to execute in-app message action HTTP request due to invalid JSON");
        }
    }
    
    void sendIAMImpression(String string, final String s, final String s2, final int n, final String s3, final Set<String> set, final OSInAppMessageRequestResponse osInAppMessageRequestResponse) {
        try {
            final JSONObject jsonObject = new JSONObject(this, string, s, s2, n) {
                final OSInAppMessageRepository this$0;
                final String val$appId;
                final int val$deviceType;
                final String val$userId;
                final String val$variantId;
                
                {
                    this.put("app_id", (Object)val$appId);
                    this.put("player_id", (Object)val$userId);
                    this.put("variant_id", (Object)val$variantId);
                    this.put("device_type", val$deviceType);
                    this.put("first_impression", true);
                }
            };
            final StringBuilder sb = new StringBuilder("in_app_messages/");
            sb.append(s3);
            sb.append("/impression");
            string = sb.toString();
            OneSignalRestClient.post(string, jsonObject, (OneSignalRestClient.ResponseHandler)new OSInAppMessageRepository$6(this, (Set)set, osInAppMessageRequestResponse));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            this.logger.error("Unable to execute in-app message impression HTTP request due to invalid JSON");
        }
    }
    
    void sendIAMPageImpression(final String s, String string, final String s2, final int n, final String s3, final String s4, final Set<String> set, final OSInAppMessageRequestResponse osInAppMessageRequestResponse) {
        try {
            final JSONObject jsonObject = new JSONObject(this, s, string, s2, n, s4) {
                final OSInAppMessageRepository this$0;
                final String val$appId;
                final int val$deviceType;
                final String val$pageId;
                final String val$userId;
                final String val$variantId;
                
                {
                    this.put("app_id", (Object)val$appId);
                    this.put("player_id", (Object)val$userId);
                    this.put("variant_id", (Object)val$variantId);
                    this.put("device_type", val$deviceType);
                    this.put("page_id", (Object)val$pageId);
                }
            };
            final StringBuilder sb = new StringBuilder("in_app_messages/");
            sb.append(s3);
            sb.append("/pageImpression");
            string = sb.toString();
            OneSignalRestClient.post(string, jsonObject, (OneSignalRestClient.ResponseHandler)new OSInAppMessageRepository$4(this, (Set)set, osInAppMessageRequestResponse));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            this.logger.error("Unable to execute in-app message impression HTTP request due to invalid JSON");
        }
    }
    
    interface OSInAppMessageRequestResponse
    {
        void onFailure(final String p0);
        
        void onSuccess(final String p0);
    }
}
