package com.onesignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OneSignalRemoteParams
{
    public static final int DEFAULT_INDIRECT_ATTRIBUTION_WINDOW = 1440;
    public static final int DEFAULT_NOTIFICATION_LIMIT = 10;
    private static final String DIRECT_PARAM = "direct";
    private static final String DISABLE_GMS_MISSING_PROMPT = "disable_gms_missing_prompt";
    private static final String ENABLED_PARAM = "enabled";
    private static final String FCM_API_KEY = "api_key";
    private static final String FCM_APP_ID = "app_id";
    private static final String FCM_PARENT_PARAM = "fcm";
    private static final String FCM_PROJECT_ID = "project_id";
    private static final String IAM_ATTRIBUTION_PARAM = "in_app_message_attribution";
    private static final int INCREASE_BETWEEN_RETRIES = 10000;
    private static final String INDIRECT_PARAM = "indirect";
    private static final String LOCATION_SHARED = "location_shared";
    private static final int MAX_WAIT_BETWEEN_RETRIES = 90000;
    private static final int MIN_WAIT_BETWEEN_RETRIES = 30000;
    private static final String NOTIFICATION_ATTRIBUTION_PARAM = "notification_attribution";
    private static final String OUTCOMES_V2_SERVICE_PARAM = "v2_enabled";
    private static final String OUTCOME_PARAM = "outcomes";
    private static final String REQUIRES_USER_PRIVACY_CONSENT = "requires_user_privacy_consent";
    private static final String UNATTRIBUTED_PARAM = "unattributed";
    private static final String UNSUBSCRIBE_ON_NOTIFICATION_DISABLE = "unsubscribe_on_notifications_disabled";
    private static int androidParamsRetries;
    
    static void makeAndroidParamsRequest(String s, final String s2, final Callback callback) {
        final OneSignalRemoteParams$1 oneSignalRemoteParams$1 = new OneSignalRemoteParams$1(s, s2, callback);
        final StringBuilder sb = new StringBuilder("apps/");
        sb.append(s);
        sb.append("/android_params.js");
        final String s3 = s = sb.toString();
        if (s2 != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(s3);
            sb2.append("?player_id=");
            sb2.append(s2);
            s = sb2.toString();
        }
        OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "Starting request to get Android parameters.");
        OneSignalRestClient.get(s, (OneSignalRestClient.ResponseHandler)oneSignalRemoteParams$1, "CACHE_KEY_REMOTE_PARAMS");
    }
    
    private static void processJson(final String s, final Callback ex) {
        try {
            ((Callback)ex).complete((Params)new OneSignalRemoteParams$2(new JSONObject(s)));
            return;
        }
        catch (final JSONException ex) {}
        catch (final NullPointerException ex2) {}
        OneSignal.Log(OneSignal.LOG_LEVEL.FATAL, "Error parsing android_params!: ", (Throwable)ex);
        final OneSignal.LOG_LEVEL fatal = OneSignal.LOG_LEVEL.FATAL;
        final StringBuilder sb = new StringBuilder("Response that errored from android_params!: ");
        sb.append(s);
        OneSignal.Log(fatal, sb.toString());
    }
    
    private static void processOutcomeJson(final JSONObject jsonObject, final InfluenceParams influenceParams) {
        if (jsonObject.has("v2_enabled")) {
            influenceParams.outcomesV2ServiceEnabled = jsonObject.optBoolean("v2_enabled");
        }
        if (jsonObject.has("direct")) {
            influenceParams.directEnabled = jsonObject.optJSONObject("direct").optBoolean("enabled");
        }
        if (jsonObject.has("indirect")) {
            final JSONObject optJSONObject = jsonObject.optJSONObject("indirect");
            influenceParams.indirectEnabled = optJSONObject.optBoolean("enabled");
            if (optJSONObject.has("notification_attribution")) {
                final JSONObject optJSONObject2 = optJSONObject.optJSONObject("notification_attribution");
                influenceParams.indirectNotificationAttributionWindow = optJSONObject2.optInt("minutes_since_displayed", 1440);
                influenceParams.notificationLimit = optJSONObject2.optInt("limit", 10);
            }
            if (optJSONObject.has("in_app_message_attribution")) {
                final JSONObject optJSONObject3 = optJSONObject.optJSONObject("in_app_message_attribution");
                influenceParams.indirectIAMAttributionWindow = optJSONObject3.optInt("minutes_since_displayed", 1440);
                influenceParams.iamLimit = optJSONObject3.optInt("limit", 10);
            }
        }
        if (jsonObject.has("unattributed")) {
            influenceParams.unattributedEnabled = jsonObject.optJSONObject("unattributed").optBoolean("enabled");
        }
    }
    
    interface Callback
    {
        void complete(final Params p0);
    }
    
    static class FCMParams
    {
        String apiKey;
        String appId;
        String projectId;
    }
    
    public static class InfluenceParams
    {
        boolean directEnabled;
        int iamLimit;
        boolean indirectEnabled;
        int indirectIAMAttributionWindow;
        int indirectNotificationAttributionWindow;
        int notificationLimit;
        boolean outcomesV2ServiceEnabled;
        boolean unattributedEnabled;
        
        public InfluenceParams() {
            this.indirectNotificationAttributionWindow = 1440;
            this.notificationLimit = 10;
            this.indirectIAMAttributionWindow = 1440;
            this.iamLimit = 10;
            this.directEnabled = false;
            this.indirectEnabled = false;
            this.unattributedEnabled = false;
            this.outcomesV2ServiceEnabled = false;
        }
        
        public int getIamLimit() {
            return this.iamLimit;
        }
        
        public int getIndirectIAMAttributionWindow() {
            return this.indirectIAMAttributionWindow;
        }
        
        public int getIndirectNotificationAttributionWindow() {
            return this.indirectNotificationAttributionWindow;
        }
        
        public int getNotificationLimit() {
            return this.notificationLimit;
        }
        
        public boolean isDirectEnabled() {
            return this.directEnabled;
        }
        
        public boolean isIndirectEnabled() {
            return this.indirectEnabled;
        }
        
        public boolean isUnattributedEnabled() {
            return this.unattributedEnabled;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("InfluenceParams{indirectNotificationAttributionWindow=");
            sb.append(this.indirectNotificationAttributionWindow);
            sb.append(", notificationLimit=");
            sb.append(this.notificationLimit);
            sb.append(", indirectIAMAttributionWindow=");
            sb.append(this.indirectIAMAttributionWindow);
            sb.append(", iamLimit=");
            sb.append(this.iamLimit);
            sb.append(", directEnabled=");
            sb.append(this.directEnabled);
            sb.append(", indirectEnabled=");
            sb.append(this.indirectEnabled);
            sb.append(", unattributedEnabled=");
            sb.append(this.unattributedEnabled);
            sb.append('}');
            return sb.toString();
        }
    }
    
    static class Params
    {
        boolean clearGroupOnSummaryClick;
        Boolean disableGMSMissingPrompt;
        boolean enterprise;
        FCMParams fcmParams;
        boolean firebaseAnalytics;
        String googleProjectNumber;
        InfluenceParams influenceParams;
        Boolean locationShared;
        JSONArray notificationChannels;
        boolean receiveReceiptEnabled;
        Boolean requiresUserPrivacyConsent;
        boolean restoreTTLFilter;
        Boolean unsubscribeWhenNotificationsDisabled;
        boolean useEmailAuth;
        boolean useSMSAuth;
        boolean useUserIdAuth;
    }
}
