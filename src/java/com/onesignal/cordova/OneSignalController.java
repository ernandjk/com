package com.onesignal.cordova;

import com.onesignal.OneSignal$OSLanguageError;
import com.onesignal.OneSignal$OSSetLanguageCompletionHandler;
import com.onesignal.OneSignal$ExternalIdError;
import com.onesignal.OneSignal$OSExternalUserIdUpdateCompletionHandler;
import com.onesignal.OneSignal$PromptForPushNotificationPermissionResponseHandler;
import org.json.JSONException;
import com.onesignal.OneSignal$PostNotificationResponseHandler;
import org.json.JSONObject;
import com.onesignal.OneSignal$OSGetTagsHandler;
import com.onesignal.OSDeviceState;
import org.apache.cordova.CallbackContext;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONArray;
import com.onesignal.OneSignal;

public class OneSignalController
{
    public static boolean clearOneSignalNotifications() {
        try {
            OneSignal.clearOneSignalNotifications();
            return true;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteTags(final JSONArray jsonArray) {
        try {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < jsonArray.length(); ++i) {
                ((Collection)list).add((Object)jsonArray.get(i).toString());
            }
            OneSignal.deleteTags((Collection)list);
            return true;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return false;
        }
    }
    
    public static boolean disablePush(final JSONArray jsonArray) {
        try {
            OneSignal.disablePush(jsonArray.getBoolean(0));
            return true;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return false;
        }
    }
    
    public static boolean getDeviceState(final CallbackContext callbackContext) {
        final OSDeviceState deviceState = OneSignal.getDeviceState();
        if (deviceState != null) {
            CallbackHelper.callbackSuccess(callbackContext, deviceState.toJSONObject());
        }
        return true;
    }
    
    public static boolean getTags(final CallbackContext callbackContext) {
        OneSignal.getTags((OneSignal$OSGetTagsHandler)new OneSignalController$$ExternalSyntheticLambda0(callbackContext));
        return true;
    }
    
    public static boolean isLocationShared(final CallbackContext callbackContext) {
        CallbackHelper.callbackSuccessBoolean(callbackContext, false);
        return true;
    }
    
    public static boolean postNotification(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            OneSignal.postNotification(jsonArray.getJSONObject(0), (OneSignal$PostNotificationResponseHandler)new OneSignal$PostNotificationResponseHandler(callbackContext) {
                final CallbackContext val$jsPostNotificationCallBack;
                
                public void onFailure(final JSONObject jsonObject) {
                    CallbackHelper.callbackError(this.val$jsPostNotificationCallBack, jsonObject);
                }
                
                public void onSuccess(final JSONObject jsonObject) {
                    CallbackHelper.callbackSuccess(this.val$jsPostNotificationCallBack, jsonObject);
                }
            });
            return true;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return false;
        }
    }
    
    public static boolean promptForPushNotificationsWithUserResponse(final CallbackContext callbackContext, final JSONArray jsonArray) {
        boolean boolean1 = false;
        try {
            boolean1 = jsonArray.getBoolean(0);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        OneSignal.promptForPushNotifications(boolean1, (OneSignal$PromptForPushNotificationPermissionResponseHandler)new OneSignal$PromptForPushNotificationPermissionResponseHandler(callbackContext) {
            final CallbackContext val$callbackContext;
            
            public void response(final boolean b) {
                CallbackHelper.callbackSuccessBoolean(this.val$callbackContext, b);
            }
        });
        return true;
    }
    
    public static void promptLocation() {
        OneSignal.promptLocation();
    }
    
    public static boolean provideUserConsent(final JSONArray jsonArray) {
        try {
            OneSignal.provideUserConsent(jsonArray.getBoolean(0));
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean registerForProvisionalAuthorization() {
        return true;
    }
    
    public static boolean removeExternalUserId(final CallbackContext callbackContext) {
        OneSignal.removeExternalUserId((OneSignal$OSExternalUserIdUpdateCompletionHandler)new OneSignal$OSExternalUserIdUpdateCompletionHandler(callbackContext) {
            final CallbackContext val$callback;
            
            public void onFailure(final OneSignal$ExternalIdError oneSignal$ExternalIdError) {
                CallbackHelper.callbackError(this.val$callback, oneSignal$ExternalIdError.getMessage());
            }
            
            public void onSuccess(final JSONObject jsonObject) {
                CallbackHelper.callbackSuccess(this.val$callback, jsonObject);
            }
        });
        return true;
    }
    
    public static boolean removeGroupedNotifications(final JSONArray jsonArray) {
        try {
            OneSignal.removeGroupedNotifications(jsonArray.getString(0));
            return true;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeNotification(final JSONArray jsonArray) {
        try {
            OneSignal.removeNotification(jsonArray.getInt(0));
            return true;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return false;
        }
    }
    
    public static boolean requiresUserPrivacyConsent(final CallbackContext callbackContext) {
        CallbackHelper.callbackSuccessBoolean(callbackContext, OneSignal.requiresUserPrivacyConsent());
        return true;
    }
    
    public static boolean sendTags(final JSONArray jsonArray) {
        try {
            OneSignal.sendTags(jsonArray.getJSONObject(0));
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
        return true;
    }
    
    public static boolean setExternalUserId(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            String string;
            if (jsonArray.length() > 1) {
                string = jsonArray.getString(1);
            }
            else {
                string = null;
            }
            OneSignal.setExternalUserId(jsonArray.getString(0), string, (OneSignal$OSExternalUserIdUpdateCompletionHandler)new OneSignal$OSExternalUserIdUpdateCompletionHandler(callbackContext) {
                final CallbackContext val$callback;
                
                public void onFailure(final OneSignal$ExternalIdError oneSignal$ExternalIdError) {
                    CallbackHelper.callbackError(this.val$callback, oneSignal$ExternalIdError.getMessage());
                }
                
                public void onSuccess(final JSONObject jsonObject) {
                    CallbackHelper.callbackSuccess(this.val$callback, jsonObject);
                }
            });
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean setLanguage(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            OneSignal.setLanguage(jsonArray.getString(0), (OneSignal$OSSetLanguageCompletionHandler)new OneSignal$OSSetLanguageCompletionHandler(callbackContext) {
                final CallbackContext val$jsSetLanguageCallback;
                
                public void onFailure(final OneSignal$OSLanguageError oneSignal$OSLanguageError) {
                    try {
                        final StringBuilder sb = new StringBuilder("{'error' : '");
                        sb.append(oneSignal$OSLanguageError.getMessage());
                        sb.append("'}");
                        CallbackHelper.callbackError(this.val$jsSetLanguageCallback, new JSONObject(sb.toString()));
                    }
                    catch (final JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                
                public void onSuccess(final String s) {
                    try {
                        JSONObject jsonObject = new JSONObject("{'success' : 'true'}");
                        if (s != null) {
                            jsonObject = new JSONObject(s);
                        }
                        CallbackHelper.callbackSuccess(this.val$jsSetLanguageCallback, jsonObject);
                    }
                    catch (final JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return true;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return false;
        }
    }
    
    public static boolean setLaunchURLsInApp() {
        return true;
    }
    
    public static void setLocationShared(final JSONArray jsonArray) {
        try {
            OneSignal.setLocationShared(jsonArray.getBoolean(0));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setLogLevel(final JSONArray jsonArray) {
        try {
            OneSignal.setLogLevel(jsonArray.getInt(0), jsonArray.getInt(1));
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    public static boolean setRequiresConsent(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            OneSignal.setRequiresUserPrivacyConsent(jsonArray.getBoolean(0));
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean unsubscribeWhenNotificationsAreDisabled(final JSONArray jsonArray) {
        try {
            OneSignal.unsubscribeWhenNotificationsAreDisabled(jsonArray.getBoolean(0));
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean userProvidedConsent(final CallbackContext callbackContext) {
        CallbackHelper.callbackSuccessBoolean(callbackContext, OneSignal.userProvidedPrivacyConsent());
        return true;
    }
}
