package com.onesignal.cordova;

import org.json.JSONArray;
import com.onesignal.OneSignal;
import org.json.JSONException;
import org.json.JSONObject;
import com.onesignal.OneSignal$EmailUpdateError;
import com.onesignal.OneSignal$EmailUpdateHandler;
import org.apache.cordova.CallbackContext;

public class OneSignalEmailController
{
    public static boolean logoutEmail(final CallbackContext callbackContext) {
        OneSignal.logoutEmail((OneSignal$EmailUpdateHandler)new OneSignal$EmailUpdateHandler(callbackContext) {
            final CallbackContext val$jsSetEmailContext;
            
            public void onFailure(final OneSignal$EmailUpdateError oneSignal$EmailUpdateError) {
                try {
                    final StringBuilder sb = new StringBuilder("{'error' : '");
                    sb.append(oneSignal$EmailUpdateError.getMessage());
                    sb.append("'}");
                    CallbackHelper.callbackError(this.val$jsSetEmailContext, new JSONObject(sb.toString()));
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
            
            public void onSuccess() {
                CallbackHelper.callbackSuccess(this.val$jsSetEmailContext, null);
            }
        });
        return true;
    }
    
    public static boolean setEmail(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            OneSignal.setEmail(jsonArray.getString(0), jsonArray.getString(1), (OneSignal$EmailUpdateHandler)new OneSignal$EmailUpdateHandler(callbackContext) {
                final CallbackContext val$jsSetEmailContext;
                
                public void onFailure(final OneSignal$EmailUpdateError oneSignal$EmailUpdateError) {
                    try {
                        final StringBuilder sb = new StringBuilder("{'error' : '");
                        sb.append(oneSignal$EmailUpdateError.getMessage());
                        sb.append("'}");
                        CallbackHelper.callbackError(this.val$jsSetEmailContext, new JSONObject(sb.toString()));
                    }
                    catch (final JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                
                public void onSuccess() {
                    CallbackHelper.callbackSuccess(this.val$jsSetEmailContext, null);
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
    
    public static boolean setUnauthenticatedEmail(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            OneSignal.setEmail(jsonArray.getString(0), (String)null, (OneSignal$EmailUpdateHandler)new OneSignal$EmailUpdateHandler(callbackContext) {
                final CallbackContext val$jsSetEmailContext;
                
                public void onFailure(final OneSignal$EmailUpdateError oneSignal$EmailUpdateError) {
                    try {
                        final StringBuilder sb = new StringBuilder("{'error' : '");
                        sb.append(oneSignal$EmailUpdateError.getMessage());
                        sb.append("'}");
                        CallbackHelper.callbackError(this.val$jsSetEmailContext, new JSONObject(sb.toString()));
                    }
                    catch (final JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                
                public void onSuccess() {
                    CallbackHelper.callbackSuccess(this.val$jsSetEmailContext, null);
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
}
