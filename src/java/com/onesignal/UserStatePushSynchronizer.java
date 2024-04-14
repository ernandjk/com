package com.onesignal;

import org.json.JSONException;
import java.util.Set;
import org.json.JSONObject;

class UserStatePushSynchronizer extends UserStateSynchronizer
{
    private static boolean serverSuccess;
    
    UserStatePushSynchronizer() {
        super(OneSignalStateSynchronizer$UserStateSynchronizerType.PUSH);
    }
    
    @Override
    protected void addOnSessionOrCreateExtras(final JSONObject jsonObject) {
    }
    
    @Override
    protected void fireEventsForUpdateFailure(final JSONObject jsonObject) {
        if (jsonObject.has("email")) {
            OneSignal.fireEmailUpdateFailure();
        }
        if (jsonObject.has("sms_number")) {
            OneSignal.fireSMSUpdateFailure();
        }
    }
    
    @Override
    String getExternalId(final boolean b) {
        final Object lock = this.LOCK;
        synchronized (lock) {
            return this.getToSyncUserState().getSyncValues().optString("external_user_id", (String)null);
        }
    }
    
    @Override
    protected String getId() {
        return OneSignal.getUserId();
    }
    
    public String getLanguage() {
        return this.getToSyncUserState().getDependValues().optString("language", (String)null);
    }
    
    @Override
    protected OneSignal$LOG_LEVEL getLogLevel() {
        return OneSignal$LOG_LEVEL.ERROR;
    }
    
    @Override
    boolean getSubscribed() {
        return this.getToSyncUserState().isSubscribed();
    }
    
    @Override
    GetTagsResult getTags(final boolean b) {
        if (b) {
            final String userId = OneSignal.getUserId();
            final String savedAppId = OneSignal.getSavedAppId();
            final StringBuilder sb = new StringBuilder("players/");
            sb.append(userId);
            sb.append("?app_id=");
            sb.append(savedAppId);
            OneSignalRestClient.getSync(sb.toString(), (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this) {
                final UserStatePushSynchronizer this$0;
                
                void onSuccess(final String s) {
                    UserStatePushSynchronizer.serverSuccess = true;
                    Label_0021: {
                        if (s != null) {
                            final String s2 = s;
                            if (!s.isEmpty()) {
                                break Label_0021;
                            }
                        }
                        final String s2 = "{}";
                        try {
                            final JSONObject jsonObject = new JSONObject(s2);
                            if (jsonObject.has("tags")) {
                                final Object lock = this.this$0.LOCK;
                                synchronized (lock) {
                                    final UserStatePushSynchronizer this$0 = this.this$0;
                                    final JSONObject generateJsonDiff = this$0.generateJsonDiff(this$0.getCurrentUserState().getSyncValues().optJSONObject("tags"), this.this$0.getToSyncUserState().getSyncValues().optJSONObject("tags"), null, null);
                                    this.this$0.getCurrentUserState().putOnSyncValues("tags", jsonObject.optJSONObject("tags"));
                                    this.this$0.getCurrentUserState().persistState();
                                    this.this$0.getToSyncUserState().mergeTags(jsonObject, generateJsonDiff);
                                    this.this$0.getToSyncUserState().persistState();
                                }
                            }
                        }
                        catch (final JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }, "CACHE_KEY_GET_TAGS");
        }
        final Object lock = this.LOCK;
        synchronized (lock) {
            return new GetTagsResult(UserStatePushSynchronizer.serverSuccess, JSONUtils.getJSONObjectWithoutBlankValues(this.getToSyncUserState().getSyncValues(), "tags"));
        }
    }
    
    @Override
    public boolean getUserSubscribePreference() {
        return this.getToSyncUserState().getDependValues().optBoolean("userSubscribePref", true);
    }
    
    @Override
    void logoutChannel() {
    }
    
    void logoutEmail() {
        try {
            this.getUserStateForModification().putOnDependValues("logoutEmail", true);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    void logoutSMS() {
        final UserState toSyncUserState = this.getToSyncUserState();
        toSyncUserState.removeFromDependValues("sms_auth_hash");
        toSyncUserState.removeFromSyncValues("sms_number");
        toSyncUserState.persistState();
        final UserState currentUserState = this.getCurrentUserState();
        currentUserState.removeFromDependValues("sms_auth_hash");
        final String optString = currentUserState.getSyncValues().optString("sms_number");
        currentUserState.removeFromSyncValues("sms_number");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sms_number", (Object)optString);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        final OneSignal$LOG_LEVEL info = OneSignal$LOG_LEVEL.INFO;
        final StringBuilder sb = new StringBuilder("Device successfully logged out of SMS number: ");
        sb.append((Object)jsonObject);
        OneSignal.Log(info, sb.toString());
        OneSignal.handleSuccessfulSMSlLogout(jsonObject);
    }
    
    @Override
    protected UserState newUserState(final String s, final boolean b) {
        return new UserStatePush(s, b);
    }
    
    @Override
    protected void onSuccessfulSync(final JSONObject jsonObject) {
    }
    
    @Override
    void saveChannelId(final String s) {
        OneSignal.saveUserId(s);
    }
    
    @Override
    protected void scheduleSyncToServer() {
        this.getNetworkHandlerThread(0).runNewJobDelayed();
    }
    
    void setEmail(final String s, final String s2) {
        try {
            final UserState userStateForModification = this.getUserStateForModification();
            userStateForModification.putOnDependValues("email_auth_hash", s2);
            userStateForModification.generateJsonDiffFromIntoSyncValued(new JSONObject().put("email", (Object)s), null);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void setPermission(final boolean b) {
        try {
            this.getUserStateForModification().putOnDependValues("androidPermission", b);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    void setSMSNumber(final String s, final String s2) {
        try {
            final UserState userStateForModification = this.getUserStateForModification();
            userStateForModification.putOnDependValues("sms_auth_hash", s2);
            userStateForModification.generateJsonDiffFromIntoSyncValued(new JSONObject().put("sms_number", (Object)s), null);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    void setSubscription(final boolean b) {
        try {
            this.getUserStateForModification().putOnDependValues("userSubscribePref", b);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    void updateIdDependents(final String s) {
        OneSignal.updateUserIdDependents(s);
    }
    
    @Override
    void updateState(final JSONObject jsonObject) {
        try {
            final JSONObject jsonObject2 = new JSONObject();
            jsonObject2.putOpt("identifier", (Object)jsonObject.optString("identifier", (String)null));
            if (jsonObject.has("device_type")) {
                jsonObject2.put("device_type", jsonObject.optInt("device_type"));
            }
            jsonObject2.putOpt("parent_player_id", (Object)jsonObject.optString("parent_player_id", (String)null));
            this.getUserStateForModification().generateJsonDiffFromIntoSyncValued(jsonObject2, null);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        try {
            final JSONObject jsonObject3 = new JSONObject();
            if (jsonObject.has("subscribableStatus")) {
                jsonObject3.put("subscribableStatus", jsonObject.optInt("subscribableStatus"));
            }
            if (jsonObject.has("androidPermission")) {
                jsonObject3.put("androidPermission", jsonObject.optBoolean("androidPermission"));
            }
            this.getUserStateForModification().generateJsonDiffFromIntoDependValues(jsonObject3, null);
        }
        catch (final JSONException ex2) {
            ex2.printStackTrace();
        }
    }
}
