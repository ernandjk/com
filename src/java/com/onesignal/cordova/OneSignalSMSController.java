package com.onesignal.cordova;

import org.json.JSONArray;
import com.onesignal.OneSignal;
import org.json.JSONException;
import org.json.JSONObject;
import com.onesignal.OneSignal$OSSMSUpdateError;
import com.onesignal.OneSignal$OSSMSUpdateHandler;
import org.apache.cordova.CallbackContext;

public class OneSignalSMSController
{
    public static boolean logoutSMSNumber(final CallbackContext callbackContext) {
        OneSignal.logoutSMSNumber((OneSignal$OSSMSUpdateHandler)new OneSignal$OSSMSUpdateHandler(callbackContext) {
            final CallbackContext val$jsSetSMSNumberContext;
            
            public void onFailure(final OneSignal$OSSMSUpdateError oneSignal$OSSMSUpdateError) {
                try {
                    final StringBuilder sb = new StringBuilder("{'error' : '");
                    sb.append(oneSignal$OSSMSUpdateError.getMessage());
                    sb.append("'}");
                    CallbackHelper.callbackError(this.val$jsSetSMSNumberContext, new JSONObject(sb.toString()));
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
            
            public void onSuccess(final JSONObject jsonObject) {
                CallbackHelper.callbackSuccess(this.val$jsSetSMSNumberContext, jsonObject);
            }
        });
        return true;
    }
    
    public static boolean setSMSNumber(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            OneSignal.setSMSNumber(jsonArray.getString(0), jsonArray.getString(1), (OneSignal$OSSMSUpdateHandler)new OneSignal$OSSMSUpdateHandler(callbackContext) {
                final CallbackContext val$jsSetSMSNumberContext;
                
                public void onFailure(final OneSignal$OSSMSUpdateError oneSignal$OSSMSUpdateError) {
                    try {
                        final StringBuilder sb = new StringBuilder("{'error' : '");
                        sb.append(oneSignal$OSSMSUpdateError.getMessage());
                        sb.append("'}");
                        CallbackHelper.callbackError(this.val$jsSetSMSNumberContext, new JSONObject(sb.toString()));
                    }
                    catch (final JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                
                public void onSuccess(final JSONObject jsonObject) {
                    CallbackHelper.callbackSuccess(this.val$jsSetSMSNumberContext, jsonObject);
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
            OneSignal.setSMSNumber(jsonArray.getString(0), (String)null, (OneSignal$OSSMSUpdateHandler)new OneSignal$OSSMSUpdateHandler(callbackContext) {
                final CallbackContext val$jsSetSMSNumberContext;
                
                public void onFailure(final OneSignal$OSSMSUpdateError oneSignal$OSSMSUpdateError) {
                    try {
                        final StringBuilder sb = new StringBuilder("{'error' : '");
                        sb.append(oneSignal$OSSMSUpdateError.getMessage());
                        sb.append("'}");
                        CallbackHelper.callbackError(this.val$jsSetSMSNumberContext, new JSONObject(sb.toString()));
                    }
                    catch (final JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                
                public void onSuccess(final JSONObject jsonObject) {
                    CallbackHelper.callbackSuccess(this.val$jsSetSMSNumberContext, jsonObject);
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
