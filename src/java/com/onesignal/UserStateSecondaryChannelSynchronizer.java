package com.onesignal;

import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

abstract class UserStateSecondaryChannelSynchronizer extends UserStateSynchronizer
{
    UserStateSecondaryChannelSynchronizer(final OneSignalStateSynchronizer$UserStateSynchronizerType oneSignalStateSynchronizer$UserStateSynchronizerType) {
        super(oneSignalStateSynchronizer$UserStateSynchronizerType);
    }
    
    @Override
    protected void addOnSessionOrCreateExtras(final JSONObject jsonObject) {
        try {
            jsonObject.put("device_type", this.getDeviceType());
            jsonObject.putOpt("device_player_id", (Object)OneSignal.getUserId());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    protected void fireEventsForUpdateFailure(final JSONObject jsonObject) {
        if (jsonObject.has("identifier")) {
            this.fireUpdateFailure();
        }
    }
    
    abstract void fireUpdateFailure();
    
    abstract void fireUpdateSuccess(final JSONObject p0);
    
    protected abstract String getAuthHashKey();
    
    protected abstract String getChannelKey();
    
    protected abstract int getDeviceType();
    
    @Override
    String getExternalId(final boolean b) {
        return null;
    }
    
    @Override
    protected abstract String getId();
    
    @Override
    protected OneSignal$LOG_LEVEL getLogLevel() {
        return OneSignal$LOG_LEVEL.INFO;
    }
    
    @Override
    boolean getSubscribed() {
        return false;
    }
    
    @Override
    GetTagsResult getTags(final boolean b) {
        return null;
    }
    
    @Override
    public boolean getUserSubscribePreference() {
        return false;
    }
    
    @Override
    abstract void logoutChannel();
    
    @Override
    protected abstract UserState newUserState(final String p0, final boolean p1);
    
    @Override
    protected void onSuccessfulSync(final JSONObject jsonObject) {
        if (jsonObject.has("identifier")) {
            final JSONObject jsonObject2 = new JSONObject();
            try {
                jsonObject2.put(this.getChannelKey(), jsonObject.get("identifier"));
                if (jsonObject.has(this.getAuthHashKey())) {
                    jsonObject2.put(this.getAuthHashKey(), jsonObject.get(this.getAuthHashKey()));
                }
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
            this.fireUpdateSuccess(jsonObject2);
        }
    }
    
    void refresh() {
        this.scheduleSyncToServer();
    }
    
    @Override
    protected void scheduleSyncToServer() {
        if (this.getId() != null || this.getRegistrationId() != null) {
            if (OneSignal.getUserId() != null) {
                this.getNetworkHandlerThread(0).runNewJobDelayed();
            }
        }
    }
    
    void setChannelId(final String s, final String s2) {
        final UserState userStateForModification = this.getUserStateForModification();
        final ImmutableJSONObject syncValues = userStateForModification.getSyncValues();
        boolean b = false;
        Label_0069: {
            if (s.equals((Object)syncValues.optString("identifier"))) {
                final String optString = syncValues.optString(this.getAuthHashKey());
                String s3;
                if (s2 == null) {
                    s3 = "";
                }
                else {
                    s3 = s2;
                }
                if (optString.equals((Object)s3)) {
                    b = true;
                    break Label_0069;
                }
            }
            b = false;
        }
        if (b) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(this.getChannelKey(), (Object)s);
                jsonObject.put(this.getAuthHashKey(), (Object)s2);
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
            this.fireUpdateSuccess(jsonObject);
            return;
        }
        final String optString2 = syncValues.optString("identifier", (String)null);
        if (optString2 == null) {
            this.setNewSession();
        }
        try {
            final JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("identifier", (Object)s);
            if (s2 != null) {
                jsonObject2.put(this.getAuthHashKey(), (Object)s2);
            }
            if (s2 == null && optString2 != null && !optString2.equals((Object)s)) {
                this.saveChannelId("");
                this.resetCurrentState();
                this.setNewSession();
            }
            userStateForModification.generateJsonDiffFromIntoSyncValued(jsonObject2, null);
            this.scheduleSyncToServer();
        }
        catch (final JSONException ex2) {
            ex2.printStackTrace();
        }
    }
    
    @Override
    public void setPermission(final boolean b) {
    }
    
    @Override
    void setSubscription(final boolean b) {
    }
    
    @Override
    abstract void updateIdDependents(final String p0);
    
    @Override
    void updateState(final JSONObject jsonObject) {
    }
}
