package com.onesignal;

import org.json.JSONException;

class UserStatePush extends UserState
{
    UserStatePush(final String s, final boolean b) {
        super(s, b);
    }
    
    private int getNotificationTypes() {
        final int optInt = this.getDependValues().optInt("subscribableStatus", 1);
        if (optInt < -2) {
            return optInt;
        }
        if (!this.getDependValues().optBoolean("androidPermission", true)) {
            return 0;
        }
        if (!this.getDependValues().optBoolean("userSubscribePref", true)) {
            return -2;
        }
        return 1;
    }
    
    @Override
    protected void addDependFields() {
        try {
            this.putOnSyncValues("notification_types", this.getNotificationTypes());
        }
        catch (final JSONException ex) {}
    }
    
    @Override
    boolean isSubscribed() {
        return this.getNotificationTypes() > 0;
    }
    
    @Override
    UserState newInstance(final String s) {
        return new UserStatePush(s, false);
    }
}
