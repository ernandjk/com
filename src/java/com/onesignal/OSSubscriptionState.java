package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OSSubscriptionState implements Cloneable
{
    private static final String CHANGED_KEY = "changed";
    private boolean accepted;
    private OSObservable<Object, OSSubscriptionState> observable;
    private boolean pushDisabled;
    private String pushToken;
    private String userId;
    
    OSSubscriptionState(final boolean b, final boolean accepted) {
        this.observable = new OSObservable<Object, OSSubscriptionState>("changed", false);
        if (b) {
            this.pushDisabled = OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_SUBSCRIPTION_LAST", true);
            this.userId = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_PLAYER_ID_LAST", null);
            this.pushToken = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_PUSH_TOKEN_LAST", null);
            this.accepted = OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_PERMISSION_ACCEPTED_LAST", false);
        }
        else {
            this.pushDisabled = (OneSignalStateSynchronizer.getUserSubscribePreference() ^ true);
            this.userId = OneSignal.getUserId();
            this.pushToken = OneSignalStateSynchronizer.getRegistrationId();
            this.accepted = accepted;
        }
    }
    
    private void setAccepted(final boolean accepted) {
        final boolean subscribed = this.isSubscribed();
        this.accepted = accepted;
        if (subscribed != this.isSubscribed()) {
            this.observable.notifyChange(this);
        }
    }
    
    void changed(final OSPermissionState osPermissionState) {
        this.setAccepted(osPermissionState.areNotificationsEnabled());
    }
    
    @Override
    protected Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    boolean compare(final OSSubscriptionState osSubscriptionState) {
        if (this.pushDisabled == osSubscriptionState.pushDisabled) {
            String userId = this.userId;
            final String s = "";
            if (userId == null) {
                userId = "";
            }
            String userId2 = osSubscriptionState.userId;
            if (userId2 == null) {
                userId2 = "";
            }
            if (userId.equals((Object)userId2)) {
                String pushToken = this.pushToken;
                if (pushToken == null) {
                    pushToken = "";
                }
                final String pushToken2 = osSubscriptionState.pushToken;
                String s2 = s;
                if (pushToken2 != null) {
                    s2 = pushToken2;
                }
                if (pushToken.equals((Object)s2)) {
                    if (this.accepted == osSubscriptionState.accepted) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public OSObservable<Object, OSSubscriptionState> getObservable() {
        return this.observable;
    }
    
    public String getPushToken() {
        return this.pushToken;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public boolean isPushDisabled() {
        return this.pushDisabled;
    }
    
    public boolean isSubscribed() {
        return this.userId != null && this.pushToken != null && !this.pushDisabled && this.accepted;
    }
    
    void persistAsFrom() {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_SUBSCRIPTION_LAST", this.pushDisabled);
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_PLAYER_ID_LAST", this.userId);
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_PUSH_TOKEN_LAST", this.pushToken);
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_PERMISSION_ACCEPTED_LAST", this.accepted);
    }
    
    void setPushDisabled(final boolean pushDisabled) {
        final boolean b = this.pushDisabled != pushDisabled;
        this.pushDisabled = pushDisabled;
        if (b) {
            this.observable.notifyChange(this);
        }
    }
    
    void setPushToken(final String pushToken) {
        if (pushToken == null) {
            return;
        }
        final boolean equals = pushToken.equals((Object)this.pushToken);
        this.pushToken = pushToken;
        if (equals ^ true) {
            this.observable.notifyChange(this);
        }
    }
    
    void setUserId(final String userId) {
        boolean b = true;
        Label_0032: {
            if (userId == null) {
                if (this.userId != null) {
                    break Label_0032;
                }
            }
            else if (!userId.equals((Object)this.userId)) {
                break Label_0032;
            }
            b = false;
        }
        this.userId = userId;
        if (b) {
            this.observable.notifyChange(this);
        }
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final String userId = this.userId;
            if (userId != null) {
                jsonObject.put("userId", (Object)userId);
            }
            else {
                jsonObject.put("userId", JSONObject.NULL);
            }
            final String pushToken = this.pushToken;
            if (pushToken != null) {
                jsonObject.put("pushToken", (Object)pushToken);
            }
            else {
                jsonObject.put("pushToken", JSONObject.NULL);
            }
            jsonObject.put("isPushDisabled", this.isPushDisabled());
            jsonObject.put("isSubscribed", this.isSubscribed());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }
}
