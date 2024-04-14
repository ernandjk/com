package com.onesignal;

import org.json.JSONObject;

public class OSDeviceState
{
    private final boolean areNotificationsEnabled;
    private final String emailAddress;
    private final boolean emailSubscribed;
    private final String emailUserId;
    private final boolean pushDisabled;
    private final String pushToken;
    private final String smsNumber;
    private final boolean smsSubscribed;
    private final String smsUserId;
    private final boolean subscribed;
    private final String userId;
    
    OSDeviceState(final OSSubscriptionState osSubscriptionState, final OSPermissionState osPermissionState, final OSEmailSubscriptionState osEmailSubscriptionState, final OSSMSSubscriptionState ossmsSubscriptionState) {
        this.areNotificationsEnabled = osPermissionState.areNotificationsEnabled();
        this.pushDisabled = osSubscriptionState.isPushDisabled();
        this.subscribed = osSubscriptionState.isSubscribed();
        this.userId = osSubscriptionState.getUserId();
        this.pushToken = osSubscriptionState.getPushToken();
        this.emailUserId = osEmailSubscriptionState.getEmailUserId();
        this.emailAddress = osEmailSubscriptionState.getEmailAddress();
        this.emailSubscribed = osEmailSubscriptionState.isSubscribed();
        this.smsUserId = ossmsSubscriptionState.getSmsUserId();
        this.smsNumber = ossmsSubscriptionState.getSMSNumber();
        this.smsSubscribed = ossmsSubscriptionState.isSubscribed();
    }
    
    public boolean areNotificationsEnabled() {
        return this.areNotificationsEnabled;
    }
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public String getEmailUserId() {
        return this.emailUserId;
    }
    
    public String getPushToken() {
        return this.pushToken;
    }
    
    public String getSMSNumber() {
        return this.smsNumber;
    }
    
    public String getSMSUserId() {
        return this.smsUserId;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public boolean isEmailSubscribed() {
        return this.emailSubscribed;
    }
    
    public boolean isPushDisabled() {
        return this.pushDisabled;
    }
    
    public boolean isSMSSubscribed() {
        return this.smsSubscribed;
    }
    
    public boolean isSubscribed() {
        return this.subscribed;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("areNotificationsEnabled", this.areNotificationsEnabled);
            jsonObject.put("isPushDisabled", this.pushDisabled);
            jsonObject.put("isSubscribed", this.subscribed);
            jsonObject.put("userId", (Object)this.userId);
            jsonObject.put("pushToken", (Object)this.pushToken);
            jsonObject.put("isEmailSubscribed", this.emailSubscribed);
            jsonObject.put("emailUserId", (Object)this.emailUserId);
            jsonObject.put("emailAddress", (Object)this.emailAddress);
            jsonObject.put("isSMSSubscribed", this.smsSubscribed);
            jsonObject.put("smsUserId", (Object)this.smsUserId);
            jsonObject.put("smsNumber", (Object)this.smsNumber);
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
        return jsonObject;
    }
}
