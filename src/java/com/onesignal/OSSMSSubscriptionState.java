package com.onesignal;

import org.json.JSONObject;

public class OSSMSSubscriptionState implements Cloneable
{
    private static final String CHANGED_KEY = "changed";
    private static final String SMS_NUMBER = "smsNumber";
    private static final String SMS_USER_ID = "smsUserId";
    private static final String SUBSCRIBED = "isSubscribed";
    private OSObservable<Object, OSSMSSubscriptionState> observable;
    private String smsNumber;
    private String smsUserId;
    
    OSSMSSubscriptionState(final boolean b) {
        this.observable = new OSObservable<Object, OSSMSSubscriptionState>("changed", false);
        if (b) {
            this.smsUserId = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_SMS_ID_LAST", null);
            this.smsNumber = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_SMS_NUMBER_LAST", null);
        }
        else {
            this.smsUserId = OneSignal.getSMSId();
            this.smsNumber = OneSignalStateSynchronizer.getSMSStateSynchronizer().getRegistrationId();
        }
    }
    
    void clearSMSAndId() {
        final boolean b = this.smsUserId != null || this.smsNumber != null;
        this.smsUserId = null;
        this.smsNumber = null;
        if (b) {
            this.observable.notifyChange(this);
        }
    }
    
    @Override
    protected Object clone() {
        try {
            return super.clone();
        }
        finally {
            return null;
        }
    }
    
    boolean compare(final OSSMSSubscriptionState ossmsSubscriptionState) {
        String smsUserId = this.smsUserId;
        final String s = "";
        if (smsUserId == null) {
            smsUserId = "";
        }
        String smsUserId2 = ossmsSubscriptionState.smsUserId;
        if (smsUserId2 == null) {
            smsUserId2 = "";
        }
        if (smsUserId.equals((Object)smsUserId2)) {
            String smsNumber = this.smsNumber;
            if (smsNumber == null) {
                smsNumber = "";
            }
            final String smsNumber2 = ossmsSubscriptionState.smsNumber;
            String s2 = s;
            if (smsNumber2 != null) {
                s2 = smsNumber2;
            }
            if (smsNumber.equals((Object)s2)) {
                return false;
            }
        }
        return true;
    }
    
    public OSObservable<Object, OSSMSSubscriptionState> getObservable() {
        return this.observable;
    }
    
    public String getSMSNumber() {
        return this.smsNumber;
    }
    
    public String getSmsUserId() {
        return this.smsUserId;
    }
    
    public boolean isSubscribed() {
        return this.smsUserId != null && this.smsNumber != null;
    }
    
    void persistAsFrom() {
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_SMS_ID_LAST", this.smsUserId);
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_SMS_NUMBER_LAST", this.smsNumber);
    }
    
    void setSMSNumber(final String smsNumber) {
        final boolean equals = smsNumber.equals((Object)this.smsNumber);
        this.smsNumber = smsNumber;
        if (equals ^ true) {
            this.observable.notifyChange(this);
        }
    }
    
    void setSMSUserId(final String smsUserId) {
        boolean b = true;
        Label_0032: {
            if (smsUserId == null) {
                if (this.smsUserId != null) {
                    break Label_0032;
                }
            }
            else if (!smsUserId.equals((Object)this.smsUserId)) {
                break Label_0032;
            }
            b = false;
        }
        this.smsUserId = smsUserId;
        if (b) {
            this.observable.notifyChange(this);
        }
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final String smsUserId = this.smsUserId;
            if (smsUserId != null) {
                jsonObject.put("smsUserId", (Object)smsUserId);
            }
            else {
                jsonObject.put("smsUserId", JSONObject.NULL);
            }
            final String smsNumber = this.smsNumber;
            if (smsNumber != null) {
                jsonObject.put("smsNumber", (Object)smsNumber);
            }
            else {
                jsonObject.put("smsNumber", JSONObject.NULL);
            }
            jsonObject.put("isSubscribed", this.isSubscribed());
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }
}
