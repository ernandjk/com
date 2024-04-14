package com.onesignal.cordova;

import com.onesignal.OSNotificationOpenedResult;
import org.json.JSONObject;
import com.onesignal.OSInAppMessageAction;
import com.onesignal.OSInAppMessage;
import com.onesignal.OSInAppMessageLifecycleHandler;
import com.onesignal.OneSignal$OSInAppMessageClickHandler;
import com.onesignal.OneSignal$OSNotificationWillShowInForegroundHandler;
import com.onesignal.OneSignal$OSNotificationOpenedHandler;
import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.onesignal.OneSignal$LOG_LEVEL;
import org.json.JSONArray;
import com.onesignal.OSNotificationReceivedEvent;
import java.util.HashMap;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

public class OneSignalPush extends CordovaPlugin
{
    private static final String ADD_EMAIL_SUBSCRIPTION_OBSERVER = "addEmailSubscriptionObserver";
    private static final String ADD_PERMISSION_OBSERVER = "addPermissionObserver";
    private static final String ADD_SMS_SUBSCRIPTION_OBSERVER = "addSMSSubscriptionObserver";
    private static final String ADD_SUBSCRIPTION_OBSERVER = "addSubscriptionObserver";
    private static final String ADD_TRIGGERS = "addTriggers";
    private static final String CLEAR_ONESIGNAL_NOTIFICATIONS = "clearOneSignalNotifications";
    private static final String COMPLETE_NOTIFICATION = "completeNotification";
    private static final String DELETE_TAGS = "deleteTags";
    private static final String DISABLE_PUSH = "disablePush";
    private static final String GET_DEVICE_STATE = "getDeviceState";
    private static final String GET_TAGS = "getTags";
    private static final String GET_TRIGGER_VALUE_FOR_KEY = "getTriggerValueForKey";
    private static final String INIT = "init";
    private static final String IN_APP_MESSAGING_PAUSED = "isInAppMessagingPaused";
    private static final String IS_LOCATION_SHARED = "isLocationShared";
    private static final String LOGOUT_EMAIL = "logoutEmail";
    private static final String LOGOUT_SMS_NUMBER = "logoutSMSNumber";
    private static final String PAUSE_IN_APP_MESSAGES = "pauseInAppMessages";
    private static final String POST_NOTIFICATION = "postNotification";
    private static final String PROMPT_FOR_PUSH_NOTIFICATIONS_WITH_USER_RESPONSE = "promptForPushNotificationsWithUserResponse";
    private static final String PROMPT_LOCATION = "promptLocation";
    private static final String PROVIDE_USER_CONSENT = "provideUserConsent";
    private static final String REGISTER_FOR_PROVISIONAL_AUTHORIZATION = "registerForProvisionalAuthorization";
    private static final String REMOVE_EXTERNAL_USER_ID = "removeExternalUserId";
    private static final String REMOVE_GROUPED_NOTIFICATIONS = "removeGroupedNotifications";
    private static final String REMOVE_NOTIFICATION = "removeNotification";
    private static final String REMOVE_TRIGGERS_FOR_KEYS = "removeTriggersForKeys";
    private static final String REQUIRES_CONSENT = "requiresUserPrivacyConsent";
    private static final String SEND_OUTCOME = "sendOutcome";
    private static final String SEND_OUTCOME_WITH_VALUE = "sendOutcomeWithValue";
    private static final String SEND_TAGS = "sendTags";
    private static final String SEND_UNIQUE_OUTCOME = "sendUniqueOutcome";
    private static final String SET_EMAIL = "setEmail";
    private static final String SET_EXTERNAL_USER_ID = "setExternalUserId";
    private static final String SET_IN_APP_MESSAGE_CLICK_HANDLER = "setInAppMessageClickHandler";
    private static final String SET_IN_APP_MESSAGE_LIFECYCLE_HANDLER = "setInAppMessageLifecycleHandler";
    private static final String SET_LANGUAGE = "setLanguage";
    private static final String SET_LAUNCH_URLS_IN_APP = "setLaunchURLsInApp";
    private static final String SET_LOCATION_SHARED = "setLocationShared";
    private static final String SET_LOG_LEVEL = "setLogLevel";
    private static final String SET_NOTIFICATION_OPENED_HANDLER = "setNotificationOpenedHandler";
    private static final String SET_NOTIFICATION_WILL_SHOW_IN_FOREGROUND_HANDLER = "setNotificationWillShowInForegroundHandler";
    private static final String SET_ON_DID_DISMISS_IN_APP_MESSAGE_HANDLER = "setOnDidDismissInAppMessageHandler";
    private static final String SET_ON_DID_DISPLAY_IN_APP_MESSAGE_HANDLER = "setOnDidDisplayInAppMessageHandler";
    private static final String SET_ON_WILL_DISMISS_IN_APP_MESSAGE_HANDLER = "setOnWillDismissInAppMessageHandler";
    private static final String SET_ON_WILL_DISPLAY_IN_APP_MESSAGE_HANDLER = "setOnWillDisplayInAppMessageHandler";
    private static final String SET_REQUIRES_CONSENT = "setRequiresUserPrivacyConsent";
    private static final String SET_SMS_NUMBER = "setSMSNumber";
    private static final String SET_UNAUTHENTICATED_EMAIL = "setUnauthenticatedEmail";
    private static final String SET_UNAUTHENTICATED_SMS_NUMBER = "setUnauthenticatedSMSNumber";
    private static final String TAG = "OneSignalPush";
    private static final String UNSUBSCRIBE_WHEN_NOTIFICATIONS_DISABLED = "unsubscribeWhenNotificationsAreDisabled";
    private static final String USER_PROVIDED_CONSENT = "userProvidedPrivacyConsent";
    private static CallbackContext jsInAppMessageDidDismissCallBack;
    private static CallbackContext jsInAppMessageDidDisplayCallBack;
    private static CallbackContext jsInAppMessageWillDismissCallback;
    private static CallbackContext jsInAppMessageWillDisplayCallback;
    private static final HashMap<String, OSNotificationReceivedEvent> notificationReceivedEventCache;
    
    static {
        notificationReceivedEventCache = new HashMap();
    }
    
    private boolean completeNotification(final JSONArray jsonArray) {
        try {
            final String string = jsonArray.getString(0);
            final boolean boolean1 = jsonArray.getBoolean(1);
            final OSNotificationReceivedEvent osNotificationReceivedEvent = (OSNotificationReceivedEvent)OneSignalPush.notificationReceivedEventCache.get((Object)string);
            if (osNotificationReceivedEvent == null) {
                final OneSignal$LOG_LEVEL error = OneSignal$LOG_LEVEL.ERROR;
                final StringBuilder sb = new StringBuilder("Could not find notification completion block with id: ");
                sb.append(string);
                OneSignal.onesignalLog(error, sb.toString());
                return false;
            }
            if (boolean1) {
                osNotificationReceivedEvent.complete(osNotificationReceivedEvent.getNotification());
            }
            else {
                osNotificationReceivedEvent.complete((OSNotification)null);
            }
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean execute(final String s, final JSONArray jsonArray, final CallbackContext onDidDisplayInAppMessageHandler) {
        s.hashCode();
        final int hashCode = s.hashCode();
        boolean b = false;
        int n = -1;
        switch (hashCode) {
            case 2100724389: {
                if (!s.equals((Object)"getDeviceState")) {
                    break;
                }
                n = 51;
                break;
            }
            case 2043454507: {
                if (!s.equals((Object)"addSMSSubscriptionObserver")) {
                    break;
                }
                n = 50;
                break;
            }
            case 1952451695: {
                if (!s.equals((Object)"setInAppMessageClickHandler")) {
                    break;
                }
                n = 49;
                break;
            }
            case 1937105094: {
                if (!s.equals((Object)"setNotificationWillShowInForegroundHandler")) {
                    break;
                }
                n = 48;
                break;
            }
            case 1876895111: {
                if (!s.equals((Object)"clearOneSignalNotifications")) {
                    break;
                }
                n = 47;
                break;
            }
            case 1764581476: {
                if (!s.equals((Object)"deleteTags")) {
                    break;
                }
                n = 46;
                break;
            }
            case 1759695848: {
                if (!s.equals((Object)"setOnWillDismissInAppMessageHandler")) {
                    break;
                }
                n = 45;
                break;
            }
            case 1712493232: {
                if (!s.equals((Object)"setOnWillDisplayInAppMessageHandler")) {
                    break;
                }
                n = 44;
                break;
            }
            case 1705585714: {
                if (!s.equals((Object)"logoutEmail")) {
                    break;
                }
                n = 43;
                break;
            }
            case 1698054452: {
                if (!s.equals((Object)"setNotificationOpenedHandler")) {
                    break;
                }
                n = 42;
                break;
            }
            case 1646460011: {
                if (!s.equals((Object)"requiresUserPrivacyConsent")) {
                    break;
                }
                n = 41;
                break;
            }
            case 1564116395: {
                if (!s.equals((Object)"postNotification")) {
                    break;
                }
                n = 40;
                break;
            }
            case 1479947161: {
                if (!s.equals((Object)"sendUniqueOutcome")) {
                    break;
                }
                n = 39;
                break;
            }
            case 1391332442: {
                if (!s.equals((Object)"setEmail")) {
                    break;
                }
                n = 38;
                break;
            }
            case 1353224738: {
                if (!s.equals((Object)"disablePush")) {
                    break;
                }
                n = 37;
                break;
            }
            case 1247441857: {
                if (!s.equals((Object)"sendTags")) {
                    break;
                }
                n = 36;
                break;
            }
            case 1192606009: {
                if (!s.equals((Object)"promptLocation")) {
                    break;
                }
                n = 35;
                break;
            }
            case 924693697: {
                if (!s.equals((Object)"sendOutcomeWithValue")) {
                    break;
                }
                n = 34;
                break;
            }
            case 840798414: {
                if (!s.equals((Object)"removeGroupedNotifications")) {
                    break;
                }
                n = 33;
                break;
            }
            case 704423678: {
                if (!s.equals((Object)"removeTriggersForKeys")) {
                    break;
                }
                n = 32;
                break;
            }
            case 528813436: {
                if (!s.equals((Object)"addTriggers")) {
                    break;
                }
                n = 31;
                break;
            }
            case 474664645: {
                if (!s.equals((Object)"getTriggerValueForKey")) {
                    break;
                }
                n = 30;
                break;
            }
            case 375730650: {
                if (!s.equals((Object)"setLanguage")) {
                    break;
                }
                n = 29;
                break;
            }
            case 368306395: {
                if (!s.equals((Object)"promptForPushNotificationsWithUserResponse")) {
                    break;
                }
                n = 28;
                break;
            }
            case 250005397: {
                if (!s.equals((Object)"removeExternalUserId")) {
                    break;
                }
                n = 27;
                break;
            }
            case 98121183: {
                if (!s.equals((Object)"registerForProvisionalAuthorization")) {
                    break;
                }
                n = 26;
                break;
            }
            case 3237136: {
                if (!s.equals((Object)"init")) {
                    break;
                }
                n = 25;
                break;
            }
            case -25642547: {
                if (!s.equals((Object)"setInAppMessageLifecycleHandler")) {
                    break;
                }
                n = 24;
                break;
            }
            case -44052000: {
                if (!s.equals((Object)"userProvidedPrivacyConsent")) {
                    break;
                }
                n = 23;
                break;
            }
            case -55416018: {
                if (!s.equals((Object)"addEmailSubscriptionObserver")) {
                    break;
                }
                n = 22;
                break;
            }
            case -75129713: {
                if (!s.equals((Object)"getTags")) {
                    break;
                }
                n = 21;
                break;
            }
            case -91683917: {
                if (!s.equals((Object)"unsubscribeWhenNotificationsAreDisabled")) {
                    break;
                }
                n = 20;
                break;
            }
            case -109707068: {
                if (!s.equals((Object)"completeNotification")) {
                    break;
                }
                n = 19;
                break;
            }
            case -309915358: {
                if (!s.equals((Object)"setLogLevel")) {
                    break;
                }
                n = 18;
                break;
            }
            case -401573422: {
                if (!s.equals((Object)"pauseInAppMessages")) {
                    break;
                }
                n = 17;
                break;
            }
            case -409171008: {
                if (!s.equals((Object)"setSMSNumber")) {
                    break;
                }
                n = 16;
                break;
            }
            case -443285608: {
                if (!s.equals((Object)"logoutSMSNumber")) {
                    break;
                }
                n = 15;
                break;
            }
            case -513748344: {
                if (!s.equals((Object)"setUnauthenticatedEmail")) {
                    break;
                }
                n = 14;
                break;
            }
            case -537152704: {
                if (!s.equals((Object)"isInAppMessagingPaused")) {
                    break;
                }
                n = 13;
                break;
            }
            case -708539666: {
                if (!s.equals((Object)"setUnauthenticatedSMSNumber")) {
                    break;
                }
                n = 12;
                break;
            }
            case -832427597: {
                if (!s.equals((Object)"setOnDidDismissInAppMessageHandler")) {
                    break;
                }
                n = 11;
                break;
            }
            case -879630213: {
                if (!s.equals((Object)"setOnDidDisplayInAppMessageHandler")) {
                    break;
                }
                n = 10;
                break;
            }
            case -1066651761: {
                if (!s.equals((Object)"removeNotification")) {
                    break;
                }
                n = 9;
                break;
            }
            case -1191667453: {
                if (!s.equals((Object)"setLaunchURLsInApp")) {
                    break;
                }
                n = 8;
                break;
            }
            case -1324368275: {
                if (!s.equals((Object)"setRequiresUserPrivacyConsent")) {
                    break;
                }
                n = 7;
                break;
            }
            case -1369979222: {
                if (!s.equals((Object)"sendOutcome")) {
                    break;
                }
                n = 6;
                break;
            }
            case -1378044588: {
                if (!s.equals((Object)"addSubscriptionObserver")) {
                    break;
                }
                n = 5;
                break;
            }
            case -1447292548: {
                if (!s.equals((Object)"setLocationShared")) {
                    break;
                }
                n = 4;
                break;
            }
            case -1638764813: {
                if (!s.equals((Object)"setExternalUserId")) {
                    break;
                }
                n = 3;
                break;
            }
            case -1713806268: {
                if (!s.equals((Object)"isLocationShared")) {
                    break;
                }
                n = 2;
                break;
            }
            case -1761153978: {
                if (!s.equals((Object)"addPermissionObserver")) {
                    break;
                }
                n = 1;
                break;
            }
            case -2026324882: {
                if (!s.equals((Object)"provideUserConsent")) {
                    break;
                }
                n = 0;
                break;
            }
        }
        switch (n) {
            default: {
                final StringBuilder sb = new StringBuilder("Invalid action : ");
                sb.append(s);
                Log.e("OneSignalPush", sb.toString());
                final StringBuilder sb2 = new StringBuilder("Invalid action : ");
                sb2.append(s);
                CallbackHelper.callbackError(onDidDisplayInAppMessageHandler, sb2.toString());
                break;
            }
            case 51: {
                b = OneSignalController.getDeviceState(onDidDisplayInAppMessageHandler);
                break;
            }
            case 50: {
                b = OneSignalObserverController.addSMSSubscriptionObserver(onDidDisplayInAppMessageHandler);
                break;
            }
            case 49: {
                b = this.setInAppMessageClickHandler(onDidDisplayInAppMessageHandler);
                break;
            }
            case 48: {
                b = this.setNotificationWillShowInForegroundHandler(onDidDisplayInAppMessageHandler);
                break;
            }
            case 47: {
                b = OneSignalController.clearOneSignalNotifications();
                break;
            }
            case 46: {
                b = OneSignalController.deleteTags(jsonArray);
                break;
            }
            case 45: {
                b = this.setOnWillDismissInAppMessageHandler(onDidDisplayInAppMessageHandler);
                break;
            }
            case 44: {
                b = this.setOnWillDisplayInAppMessageHandler(onDidDisplayInAppMessageHandler);
                break;
            }
            case 43: {
                b = OneSignalEmailController.logoutEmail(onDidDisplayInAppMessageHandler);
                break;
            }
            case 42: {
                b = this.setNotificationOpenedHandler(onDidDisplayInAppMessageHandler);
                break;
            }
            case 41: {
                b = OneSignalController.requiresUserPrivacyConsent(onDidDisplayInAppMessageHandler);
                break;
            }
            case 40: {
                b = OneSignalController.postNotification(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 39: {
                b = OneSignalOutcomeController.sendUniqueOutcome(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 38: {
                b = OneSignalEmailController.setEmail(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 37: {
                b = OneSignalController.disablePush(jsonArray);
                break;
            }
            case 36: {
                b = OneSignalController.sendTags(jsonArray);
                break;
            }
            case 35: {
                OneSignalController.promptLocation();
                break;
            }
            case 34: {
                b = OneSignalOutcomeController.sendOutcomeWithValue(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 33: {
                b = OneSignalController.removeGroupedNotifications(jsonArray);
                break;
            }
            case 32: {
                b = OneSignalInAppMessagingController.removeTriggersForKeys(jsonArray);
                break;
            }
            case 31: {
                b = OneSignalInAppMessagingController.addTriggers(jsonArray);
                break;
            }
            case 30: {
                b = OneSignalInAppMessagingController.getTriggerValueForKey(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 29: {
                b = OneSignalController.setLanguage(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 28: {
                b = OneSignalController.promptForPushNotificationsWithUserResponse(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 27: {
                b = OneSignalController.removeExternalUserId(onDidDisplayInAppMessageHandler);
                break;
            }
            case 26: {
                b = OneSignalController.registerForProvisionalAuthorization();
                break;
            }
            case 25: {
                b = this.init(jsonArray);
                break;
            }
            case 24: {
                b = this.setInAppMessageLifecycleHandler();
                break;
            }
            case 23: {
                b = OneSignalController.userProvidedConsent(onDidDisplayInAppMessageHandler);
                break;
            }
            case 22: {
                b = OneSignalObserverController.addEmailSubscriptionObserver(onDidDisplayInAppMessageHandler);
                break;
            }
            case 21: {
                b = OneSignalController.getTags(onDidDisplayInAppMessageHandler);
                break;
            }
            case 20: {
                b = OneSignalController.unsubscribeWhenNotificationsAreDisabled(jsonArray);
                break;
            }
            case 19: {
                b = this.completeNotification(jsonArray);
                break;
            }
            case 18: {
                OneSignalController.setLogLevel(jsonArray);
                break;
            }
            case 17: {
                b = OneSignalInAppMessagingController.pauseInAppMessages(jsonArray);
                break;
            }
            case 16: {
                b = OneSignalSMSController.setSMSNumber(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 15: {
                b = OneSignalSMSController.logoutSMSNumber(onDidDisplayInAppMessageHandler);
                break;
            }
            case 14: {
                b = OneSignalEmailController.setUnauthenticatedEmail(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 13: {
                b = OneSignalInAppMessagingController.isInAppMessagingPaused(onDidDisplayInAppMessageHandler);
                break;
            }
            case 12: {
                b = OneSignalSMSController.setUnauthenticatedEmail(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 11: {
                b = this.setOnDidDismissInAppMessageHandler(onDidDisplayInAppMessageHandler);
                break;
            }
            case 10: {
                b = this.setOnDidDisplayInAppMessageHandler(onDidDisplayInAppMessageHandler);
                break;
            }
            case 9: {
                b = OneSignalController.removeNotification(jsonArray);
                break;
            }
            case 8: {
                b = OneSignalController.setLaunchURLsInApp();
                break;
            }
            case 7: {
                b = OneSignalController.setRequiresConsent(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 6: {
                b = OneSignalOutcomeController.sendOutcome(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 5: {
                b = OneSignalObserverController.addSubscriptionObserver(onDidDisplayInAppMessageHandler);
                break;
            }
            case 4: {
                OneSignalController.setLocationShared(jsonArray);
                break;
            }
            case 3: {
                b = OneSignalController.setExternalUserId(onDidDisplayInAppMessageHandler, jsonArray);
                break;
            }
            case 2: {
                b = OneSignalController.isLocationShared(onDidDisplayInAppMessageHandler);
                break;
            }
            case 1: {
                b = OneSignalObserverController.addPermissionObserver(onDidDisplayInAppMessageHandler);
                break;
            }
            case 0: {
                b = OneSignalController.provideUserConsent(jsonArray);
                break;
            }
        }
        return b;
    }
    
    public boolean init(final JSONArray jsonArray) {
        try {
            final String string = jsonArray.getString(0);
            OneSignal.sdkType = "cordova";
            OneSignal.setAppId(string);
            OneSignal.initWithContext((Context)this.cordova.getActivity());
            return true;
        }
        catch (final JSONException ex) {
            final StringBuilder sb = new StringBuilder("execute: Got JSON Exception ");
            sb.append(ex.getMessage());
            Log.e("OneSignalPush", sb.toString());
            return false;
        }
    }
    
    public void onDestroy() {
        OneSignal.setNotificationOpenedHandler((OneSignal$OSNotificationOpenedHandler)null);
        OneSignal.setNotificationWillShowInForegroundHandler((OneSignal$OSNotificationWillShowInForegroundHandler)null);
    }
    
    public boolean setInAppMessageClickHandler(final CallbackContext callbackContext) {
        OneSignal.setInAppMessageClickHandler((OneSignal$OSInAppMessageClickHandler)new CordovaInAppMessageClickHandler(callbackContext));
        return true;
    }
    
    public boolean setInAppMessageLifecycleHandler() {
        OneSignal.setInAppMessageLifecycleHandler((OSInAppMessageLifecycleHandler)new OSInAppMessageLifecycleHandler(this) {
            final OneSignalPush this$0;
            
            public void onDidDismissInAppMessage(final OSInAppMessage osInAppMessage) {
                if (OneSignalPush.jsInAppMessageDidDismissCallBack != null) {
                    CallbackHelper.callbackSuccess(OneSignalPush.jsInAppMessageDidDismissCallBack, osInAppMessage.toJSONObject());
                }
            }
            
            public void onDidDisplayInAppMessage(final OSInAppMessage osInAppMessage) {
                if (OneSignalPush.jsInAppMessageDidDisplayCallBack != null) {
                    CallbackHelper.callbackSuccess(OneSignalPush.jsInAppMessageDidDisplayCallBack, osInAppMessage.toJSONObject());
                }
            }
            
            public void onWillDismissInAppMessage(final OSInAppMessage osInAppMessage) {
                if (OneSignalPush.jsInAppMessageWillDismissCallback != null) {
                    CallbackHelper.callbackSuccess(OneSignalPush.jsInAppMessageWillDismissCallback, osInAppMessage.toJSONObject());
                }
            }
            
            public void onWillDisplayInAppMessage(final OSInAppMessage osInAppMessage) {
                if (OneSignalPush.jsInAppMessageWillDisplayCallback != null) {
                    CallbackHelper.callbackSuccess(OneSignalPush.jsInAppMessageWillDisplayCallback, osInAppMessage.toJSONObject());
                }
            }
        });
        return true;
    }
    
    public boolean setNotificationOpenedHandler(final CallbackContext callbackContext) {
        OneSignal.setNotificationOpenedHandler((OneSignal$OSNotificationOpenedHandler)new CordovaNotificationOpenHandler(callbackContext));
        return true;
    }
    
    public boolean setNotificationWillShowInForegroundHandler(final CallbackContext callbackContext) {
        OneSignal.setNotificationWillShowInForegroundHandler((OneSignal$OSNotificationWillShowInForegroundHandler)new CordovaNotificationInForegroundHandler(callbackContext));
        return true;
    }
    
    public boolean setOnDidDismissInAppMessageHandler(final CallbackContext jsInAppMessageDidDismissCallBack) {
        OneSignalPush.jsInAppMessageDidDismissCallBack = jsInAppMessageDidDismissCallBack;
        return true;
    }
    
    public boolean setOnDidDisplayInAppMessageHandler(final CallbackContext jsInAppMessageDidDisplayCallBack) {
        OneSignalPush.jsInAppMessageDidDisplayCallBack = jsInAppMessageDidDisplayCallBack;
        return true;
    }
    
    public boolean setOnWillDismissInAppMessageHandler(final CallbackContext jsInAppMessageWillDismissCallback) {
        OneSignalPush.jsInAppMessageWillDismissCallback = jsInAppMessageWillDismissCallback;
        return true;
    }
    
    public boolean setOnWillDisplayInAppMessageHandler(final CallbackContext jsInAppMessageWillDisplayCallback) {
        OneSignalPush.jsInAppMessageWillDisplayCallback = jsInAppMessageWillDisplayCallback;
        return true;
    }
    
    private static class CordovaInAppMessageClickHandler implements OneSignal$OSInAppMessageClickHandler
    {
        private CallbackContext jsInAppMessageClickedCallback;
        
        public CordovaInAppMessageClickHandler(final CallbackContext jsInAppMessageClickedCallback) {
            this.jsInAppMessageClickedCallback = jsInAppMessageClickedCallback;
        }
        
        public void inAppMessageClicked(final OSInAppMessageAction osInAppMessageAction) {
            try {
                final JSONObject jsonObject = osInAppMessageAction.toJSONObject();
                final JSONObject jsonObject2 = new JSONObject();
                if (jsonObject.has("first_click")) {
                    jsonObject2.put("firstClick", jsonObject.getBoolean("first_click"));
                }
                if (jsonObject.has("closes_message")) {
                    jsonObject2.put("closesMessage", jsonObject.getBoolean("closes_message"));
                }
                jsonObject2.put("clickName", (Object)jsonObject.optString("click_name", (String)null));
                jsonObject2.put("clickUrl", (Object)jsonObject.optString("click_url", (String)null));
                jsonObject2.put("outcomes", (Object)jsonObject.optJSONArray("outcomes"));
                jsonObject2.put("tags", (Object)jsonObject.optJSONObject("tags"));
                CallbackHelper.callbackSuccess(this.jsInAppMessageClickedCallback, jsonObject2);
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static class CordovaNotificationInForegroundHandler implements OneSignal$OSNotificationWillShowInForegroundHandler
    {
        private CallbackContext jsNotificationInForegroundCallBack;
        
        public CordovaNotificationInForegroundHandler(final CallbackContext jsNotificationInForegroundCallBack) {
            this.jsNotificationInForegroundCallBack = jsNotificationInForegroundCallBack;
        }
        
        public void notificationWillShowInForeground(final OSNotificationReceivedEvent osNotificationReceivedEvent) {
            try {
                final OSNotification notification = osNotificationReceivedEvent.getNotification();
                OneSignalPush.notificationReceivedEventCache.put((Object)notification.getNotificationId(), (Object)osNotificationReceivedEvent);
                CallbackHelper.callbackSuccess(this.jsNotificationInForegroundCallBack, notification.toJSONObject());
            }
            finally {
                final Throwable t;
                t.printStackTrace();
            }
        }
    }
    
    private static class CordovaNotificationOpenHandler implements OneSignal$OSNotificationOpenedHandler
    {
        private CallbackContext jsNotificationOpenedCallBack;
        
        public CordovaNotificationOpenHandler(final CallbackContext jsNotificationOpenedCallBack) {
            this.jsNotificationOpenedCallBack = jsNotificationOpenedCallBack;
        }
        
        public void notificationOpened(final OSNotificationOpenedResult osNotificationOpenedResult) {
            try {
                final CallbackContext jsNotificationOpenedCallBack = this.jsNotificationOpenedCallBack;
                if (jsNotificationOpenedCallBack != null) {
                    CallbackHelper.callbackSuccess(jsNotificationOpenedCallBack, osNotificationOpenedResult.toJSONObject());
                }
            }
            finally {
                final Throwable t;
                t.printStackTrace();
            }
        }
    }
}
