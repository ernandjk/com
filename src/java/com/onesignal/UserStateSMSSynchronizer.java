package com.onesignal;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

class UserStateSMSSynchronizer extends UserStateSecondaryChannelSynchronizer
{
    UserStateSMSSynchronizer() {
        super(OneSignalStateSynchronizer$UserStateSynchronizerType.SMS);
    }
    
    @Override
    void fireUpdateFailure() {
        OneSignal.fireSMSUpdateFailure();
    }
    
    @Override
    void fireUpdateSuccess(final JSONObject jsonObject) {
        OneSignal.fireSMSUpdateSuccess(jsonObject);
    }
    
    @Override
    protected String getAuthHashKey() {
        return "sms_auth_hash";
    }
    
    @Override
    protected String getChannelKey() {
        return "sms_number";
    }
    
    @Override
    protected int getDeviceType() {
        return 14;
    }
    
    @Override
    protected String getId() {
        return OneSignal.getSMSId();
    }
    
    @Override
    void logoutChannel() {
        this.saveChannelId("");
        this.resetCurrentState();
        this.getToSyncUserState().removeFromSyncValues("identifier");
        final ArrayList list = new ArrayList();
        ((List)list).add((Object)"sms_auth_hash");
        ((List)list).add((Object)"device_player_id");
        ((List)list).add((Object)"external_user_id");
        this.getToSyncUserState().removeFromSyncValues((List<String>)list);
        this.getToSyncUserState().persistState();
        OneSignal.getSMSSubscriptionState().clearSMSAndId();
    }
    
    @Override
    protected UserState newUserState(final String s, final boolean b) {
        return new UserStateSMS(s, b);
    }
    
    @Override
    void saveChannelId(final String s) {
        OneSignal.saveSMSId(s);
    }
    
    @Override
    void updateIdDependents(final String s) {
        OneSignal.updateSMSIdDependents(s);
    }
}
