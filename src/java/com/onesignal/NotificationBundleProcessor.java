package com.onesignal;

import android.database.Cursor;
import android.content.Context;
import org.json.JSONArray;
import android.content.ContentValues;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;

class NotificationBundleProcessor
{
    private static final String ANDROID_NOTIFICATION_ID = "android_notif_id";
    static final String DEFAULT_ACTION = "__DEFAULT__";
    static final String IAM_PREVIEW_KEY = "os_in_app_message_preview_id";
    public static final String PUSH_ADDITIONAL_DATA_KEY = "a";
    public static final String PUSH_MINIFIED_BUTTONS_LIST = "o";
    public static final String PUSH_MINIFIED_BUTTON_ICON = "p";
    public static final String PUSH_MINIFIED_BUTTON_ID = "i";
    public static final String PUSH_MINIFIED_BUTTON_TEXT = "n";
    
    static JSONObject bundleAsJSONObject(final Bundle bundle) {
        final JSONObject jsonObject = new JSONObject();
        for (final String s : bundle.keySet()) {
            try {
                jsonObject.put(s, bundle.get(s));
            }
            catch (final JSONException ex) {
                final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                final StringBuilder sb = new StringBuilder("bundleAsJSONObject error for key: ");
                sb.append(s);
                OneSignal.Log(error, sb.toString(), (Throwable)ex);
            }
        }
        return jsonObject;
    }
    
    static JSONObject getCustomJSONObject(final JSONObject jsonObject) throws JSONException {
        return new JSONObject(jsonObject.optString("custom"));
    }
    
    static boolean hasRemoteResource(final Bundle bundle) {
        return isBuildKeyRemote(bundle, "licon") || isBuildKeyRemote(bundle, "bicon") || bundle.getString("bg_img", (String)null) != null;
    }
    
    private static boolean isBuildKeyRemote(final Bundle bundle, final String s) {
        final String trim = bundle.getString(s, "").trim();
        return trim.startsWith("http://") || trim.startsWith("https://");
    }
    
    static void markNotificationAsDismissed(final OSNotificationGenerationJob osNotificationGenerationJob) {
        if (!osNotificationGenerationJob.isNotificationToDisplay()) {
            return;
        }
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("Marking restored or disabled notifications as dismissed: ");
        sb.append(osNotificationGenerationJob.toString());
        OneSignal.Log(debug, sb.toString());
        final StringBuilder sb2 = new StringBuilder("android_notification_id = ");
        sb2.append((Object)osNotificationGenerationJob.getAndroidId());
        final String string = sb2.toString();
        final OneSignalDbHelper instance = OneSignalDbHelper.getInstance(osNotificationGenerationJob.getContext());
        final ContentValues contentValues = new ContentValues();
        contentValues.put("dismissed", Integer.valueOf(1));
        instance.update("notification", contentValues, string, (String[])null);
        BadgeCountUpdater.update((OneSignalDb)instance, osNotificationGenerationJob.getContext());
    }
    
    private static void maximizeButtonsFromBundle(final Bundle bundle) {
        if (!bundle.containsKey("o")) {
            return;
        }
        try {
            final JSONObject jsonObject = new JSONObject(bundle.getString("custom"));
            JSONObject jsonObject2;
            if (jsonObject.has("a")) {
                jsonObject2 = jsonObject.getJSONObject("a");
            }
            else {
                jsonObject2 = new JSONObject();
            }
            final JSONArray jsonArray = new JSONArray(bundle.getString("o"));
            bundle.remove("o");
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                final String string = jsonObject3.getString("n");
                jsonObject3.remove("n");
                String string2;
                if (jsonObject3.has("i")) {
                    string2 = jsonObject3.getString("i");
                    jsonObject3.remove("i");
                }
                else {
                    string2 = string;
                }
                jsonObject3.put("id", (Object)string2);
                jsonObject3.put("text", (Object)string);
                if (jsonObject3.has("p")) {
                    jsonObject3.put("icon", (Object)jsonObject3.getString("p"));
                    jsonObject3.remove("p");
                }
            }
            jsonObject2.put("actionButtons", (Object)jsonArray);
            jsonObject2.put("actionId", (Object)"__DEFAULT__");
            if (!jsonObject.has("a")) {
                jsonObject.put("a", (Object)jsonObject2);
            }
            bundle.putString("custom", jsonObject.toString());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    static JSONArray newJsonArray(final JSONObject jsonObject) {
        return new JSONArray().put((Object)jsonObject);
    }
    
    static void processBundleFromReceiver(final Context context, final Bundle bundle, final ProcessBundleReceiverCallback processBundleReceiverCallback) {
        final ProcessedBundleResult processedBundleResult = new ProcessedBundleResult();
        if (!OSNotificationFormatHelper.isOneSignalBundle(bundle)) {
            processBundleReceiverCallback.onBundleProcessed(processedBundleResult);
            return;
        }
        processedBundleResult.setOneSignalPayload(true);
        maximizeButtonsFromBundle(bundle);
        if (OSInAppMessagePreviewHandler.notificationReceived(context, bundle)) {
            processedBundleResult.setInAppPreviewShown(true);
            processBundleReceiverCallback.onBundleProcessed(processedBundleResult);
            return;
        }
        startNotificationProcessing(context, bundle, processedBundleResult, (NotificationProcessingCallback)new NotificationBundleProcessor$2(processedBundleResult, processBundleReceiverCallback));
    }
    
    private static void processCollapseKey(final OSNotificationGenerationJob osNotificationGenerationJob) {
        if (osNotificationGenerationJob.isRestoring()) {
            return;
        }
        if (osNotificationGenerationJob.getJsonPayload().has("collapse_key")) {
            if (!"do_not_collapse".equals((Object)osNotificationGenerationJob.getJsonPayload().optString("collapse_key"))) {
                final Cursor query = OneSignalDbHelper.getInstance(osNotificationGenerationJob.getContext()).query("notification", new String[] { "android_notification_id" }, "collapse_id = ? AND dismissed = 0 AND opened = 0 ", new String[] { osNotificationGenerationJob.getJsonPayload().optString("collapse_key") }, (String)null, (String)null, (String)null);
                if (query.moveToFirst()) {
                    osNotificationGenerationJob.getNotification().setAndroidNotificationId(query.getInt(query.getColumnIndex("android_notification_id")));
                }
                query.close();
            }
        }
    }
    
    static void processFromFCMIntentService(final Context context, final BundleCompat bundleCompat) {
        OneSignal.initWithContext(context);
        try {
            final String string = bundleCompat.getString("json_payload");
            if (string == null) {
                final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                final StringBuilder sb = new StringBuilder("json_payload key is nonexistent from mBundle passed to ProcessFromFCMIntentService: ");
                sb.append((Object)bundleCompat);
                OneSignal.Log(error, sb.toString());
                return;
            }
            final JSONObject jsonObject = new JSONObject(string);
            final boolean boolean1 = bundleCompat.getBoolean("is_restoring", false);
            final long longValue = bundleCompat.getLong("timestamp");
            int intValue;
            if (bundleCompat.containsKey("android_notif_id")) {
                intValue = bundleCompat.getInt("android_notif_id");
            }
            else {
                intValue = 0;
            }
            OneSignal.notValidOrDuplicated(context, jsonObject, (OSNotificationDataController.InvalidOrDuplicateNotificationCallback)new NotificationBundleProcessor$1(boolean1, jsonObject, context, intValue, string, longValue));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    static int processJobForDisplay(final OSNotificationController osNotificationController, final boolean b) {
        return processJobForDisplay(osNotificationController, false, b);
    }
    
    private static int processJobForDisplay(final OSNotificationController osNotificationController, final boolean b, final boolean b2) {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("Starting processJobForDisplay opened: ");
        sb.append(b);
        sb.append(" fromBackgroundLogic: ");
        sb.append(b2);
        OneSignal.Log(debug, sb.toString());
        final OSNotificationGenerationJob notificationJob = osNotificationController.getNotificationJob();
        processCollapseKey(notificationJob);
        final int intValue = notificationJob.getAndroidId();
        final boolean shouldDisplayNotification = shouldDisplayNotification(notificationJob);
        boolean displayNotification = false;
        if (shouldDisplayNotification) {
            notificationJob.setIsNotificationToDisplay(true);
            if (b2 && OneSignal.shouldFireForegroundHandlers(notificationJob)) {
                osNotificationController.setFromBackgroundLogic(false);
                OneSignal.fireForegroundHandlers(osNotificationController);
                return intValue;
            }
            displayNotification = GenerateNotification.displayNotification(notificationJob);
        }
        if (!notificationJob.isRestoring()) {
            processNotification(notificationJob, b, displayNotification);
            OSNotificationWorkManager.removeNotificationIdProcessed(OSNotificationFormatHelper.getOSNotificationIdFromJson(osNotificationController.getNotificationJob().getJsonPayload()));
            OneSignal.handleNotificationReceived(notificationJob);
        }
        return intValue;
    }
    
    static int processJobForDisplay(final OSNotificationGenerationJob osNotificationGenerationJob, final boolean b) {
        return processJobForDisplay(new OSNotificationController(osNotificationGenerationJob, osNotificationGenerationJob.isRestoring(), true), false, b);
    }
    
    static void processNotification(final OSNotificationGenerationJob osNotificationGenerationJob, final boolean b, final boolean b2) {
        saveNotification(osNotificationGenerationJob, b);
        if (!b2) {
            markNotificationAsDismissed(osNotificationGenerationJob);
            return;
        }
        final String apiNotificationId = osNotificationGenerationJob.getApiNotificationId();
        OSReceiveReceiptController.getInstance().beginEnqueueingWork(osNotificationGenerationJob.getContext(), apiNotificationId);
        OneSignal.getSessionManager().onNotificationReceived(apiNotificationId);
    }
    
    private static void saveNotification(final OSNotificationGenerationJob osNotificationGenerationJob, final boolean b) {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("Saving Notification job: ");
        sb.append(osNotificationGenerationJob.toString());
        OneSignal.Log(debug, sb.toString());
        final Context context = osNotificationGenerationJob.getContext();
        final JSONObject jsonPayload = osNotificationGenerationJob.getJsonPayload();
        try {
            final JSONObject customJSONObject = getCustomJSONObject(osNotificationGenerationJob.getJsonPayload());
            final OneSignalDbHelper instance = OneSignalDbHelper.getInstance(osNotificationGenerationJob.getContext());
            final boolean notificationToDisplay = osNotificationGenerationJob.isNotificationToDisplay();
            int n = 1;
            if (notificationToDisplay) {
                final StringBuilder sb2 = new StringBuilder("android_notification_id = ");
                sb2.append((Object)osNotificationGenerationJob.getAndroidId());
                final String string = sb2.toString();
                final ContentValues contentValues = new ContentValues();
                contentValues.put("dismissed", Integer.valueOf(1));
                instance.update("notification", contentValues, string, (String[])null);
                BadgeCountUpdater.update((OneSignalDb)instance, context);
            }
            final ContentValues contentValues2 = new ContentValues();
            contentValues2.put("notification_id", customJSONObject.optString("i"));
            if (jsonPayload.has("grp")) {
                contentValues2.put("group_id", jsonPayload.optString("grp"));
            }
            if (jsonPayload.has("collapse_key") && !"do_not_collapse".equals((Object)jsonPayload.optString("collapse_key"))) {
                contentValues2.put("collapse_id", jsonPayload.optString("collapse_key"));
            }
            if (!b) {
                n = 0;
            }
            contentValues2.put("opened", Integer.valueOf(n));
            if (!b) {
                contentValues2.put("android_notification_id", osNotificationGenerationJob.getAndroidId());
            }
            if (osNotificationGenerationJob.getTitle() != null) {
                contentValues2.put("title", osNotificationGenerationJob.getTitle().toString());
            }
            if (osNotificationGenerationJob.getBody() != null) {
                contentValues2.put("message", osNotificationGenerationJob.getBody().toString());
            }
            contentValues2.put("expire_time", Long.valueOf(jsonPayload.optLong("google.sent_time", OneSignal.getTime().getCurrentTimeMillis()) / 1000L + jsonPayload.optInt("google.ttl", 259200)));
            contentValues2.put("full_data", jsonPayload.toString());
            instance.insertOrThrow("notification", (String)null, contentValues2);
            final OneSignal.LOG_LEVEL debug2 = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb3 = new StringBuilder("Notification saved values: ");
            sb3.append(contentValues2.toString());
            OneSignal.Log(debug2, sb3.toString());
            if (!b) {
                BadgeCountUpdater.update((OneSignalDb)instance, context);
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    private static boolean shouldDisplayNotification(final OSNotificationGenerationJob osNotificationGenerationJob) {
        return osNotificationGenerationJob.hasExtender() || OSUtils.isStringNotEmpty(osNotificationGenerationJob.getJsonPayload().optString("alert"));
    }
    
    private static void startNotificationProcessing(final Context context, final Bundle bundle, final ProcessedBundleResult processedBundleResult, final NotificationProcessingCallback notificationProcessingCallback) {
        final JSONObject bundleAsJSONObject = bundleAsJSONObject(bundle);
        OneSignal.notValidOrDuplicated(context, bundleAsJSONObject, (OSNotificationDataController.InvalidOrDuplicateNotificationCallback)new NotificationBundleProcessor$3(bundle.getBoolean("is_restoring", false), context, bundle, notificationProcessingCallback, bundleAsJSONObject, OneSignal.getTime().getCurrentTimeMillis() / 1000L, Integer.parseInt(bundle.getString("pri", "0")) > 9, processedBundleResult));
    }
    
    interface NotificationProcessingCallback
    {
        void onResult(final boolean p0);
    }
    
    interface ProcessBundleReceiverCallback
    {
        void onBundleProcessed(final ProcessedBundleResult p0);
    }
    
    static class ProcessedBundleResult
    {
        private boolean inAppPreviewShown;
        private boolean isDup;
        private boolean isOneSignalPayload;
        private boolean isWorkManagerProcessing;
        
        boolean isDup() {
            return this.isDup;
        }
        
        public boolean isWorkManagerProcessing() {
            return this.isWorkManagerProcessing;
        }
        
        boolean processed() {
            return !this.isOneSignalPayload || this.isDup || this.inAppPreviewShown || this.isWorkManagerProcessing;
        }
        
        void setDup(final boolean isDup) {
            this.isDup = isDup;
        }
        
        public void setInAppPreviewShown(final boolean inAppPreviewShown) {
            this.inAppPreviewShown = inAppPreviewShown;
        }
        
        void setOneSignalPayload(final boolean isOneSignalPayload) {
            this.isOneSignalPayload = isOneSignalPayload;
        }
        
        public void setWorkManagerProcessing(final boolean isWorkManagerProcessing) {
            this.isWorkManagerProcessing = isWorkManagerProcessing;
        }
    }
}
