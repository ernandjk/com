package com.onesignal;

import java.util.Collection;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import com.onesignal.language.LanguageContext;

class OSInAppMessageDummyController extends OSInAppMessageController
{
    OSInAppMessageDummyController(final OneSignalDbHelper oneSignalDbHelper, final OSTaskController osTaskController, final OSLogger osLogger, final OSSharedPreferences osSharedPreferences, final LanguageContext languageContext) {
        super(oneSignalDbHelper, osTaskController, osLogger, osSharedPreferences, languageContext);
    }
    
    @Override
    void addTriggers(final Map<String, Object> map) {
    }
    
    @Override
    void cleanCachedInAppMessages() {
    }
    
    @Override
    void displayPreviewMessage(final String s) {
    }
    
    @Override
    OSInAppMessageInternal getCurrentDisplayedInAppMessage() {
        return null;
    }
    
    @Override
    Object getTriggerValue(final String s) {
        return null;
    }
    
    public void initRedisplayData() {
    }
    
    @Override
    void initWithCachedInAppMessages() {
    }
    
    @Override
    boolean isInAppMessageShowing() {
        return false;
    }
    
    @Override
    public void messageTriggerConditionChanged() {
    }
    
    public void messageWasDismissed(final OSInAppMessageInternal osInAppMessageInternal) {
    }
    
    @Override
    void onMessageActionOccurredOnMessage(final OSInAppMessageInternal osInAppMessageInternal, final JSONObject jsonObject) {
    }
    
    @Override
    void onMessageActionOccurredOnPreview(final OSInAppMessageInternal osInAppMessageInternal, final JSONObject jsonObject) {
    }
    
    @Override
    void receivedInAppMessageJson(final JSONArray jsonArray) throws JSONException {
    }
    
    @Override
    void removeTriggersForKeys(final Collection<String> collection) {
    }
    
    @Override
    void setInAppMessagingEnabled(final boolean b) {
    }
}
