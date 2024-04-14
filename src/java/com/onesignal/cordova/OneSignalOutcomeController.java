package com.onesignal.cordova;

import com.onesignal.OneSignal$OutcomeCallback;
import com.onesignal.OneSignal;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import org.json.JSONObject;
import com.onesignal.OSOutcomeEvent;
import org.apache.cordova.CallbackContext;

public class OneSignalOutcomeController
{
    private static final String TAG = "OneSignalOutcome";
    
    public static boolean sendOutcome(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            final String string = jsonArray.getString(0);
            OneSignal.sendOutcome(string, (OneSignal$OutcomeCallback)new OneSignalOutcomeController$$ExternalSyntheticLambda0(callbackContext, string));
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean sendOutcomeWithValue(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            final String string = jsonArray.getString(0);
            final float floatValue = jsonArray.optDouble(1).floatValue();
            OneSignal.sendOutcomeWithValue(string, floatValue, (OneSignal$OutcomeCallback)new OneSignalOutcomeController$$ExternalSyntheticLambda2(callbackContext, string, floatValue));
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean sendUniqueOutcome(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            final String string = jsonArray.getString(0);
            OneSignal.sendUniqueOutcome(string, (OneSignal$OutcomeCallback)new OneSignalOutcomeController$$ExternalSyntheticLambda1(callbackContext, string));
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
