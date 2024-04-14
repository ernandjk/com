package com.onesignal;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

class UserStateEmailSynchronizer extends UserStateSecondaryChannelSynchronizer
{
    UserStateEmailSynchronizer() {
        super(OneSignalStateSynchronizer$UserStateSynchronizerType.EMAIL);
    }
    
    @Override
    void fireUpdateFailure() {
        OneSignal.fireEmailUpdateFailure();
    }
    
    @Override
    void fireUpdateSuccess(final JSONObject jsonObject) {
        OneSignal.fireEmailUpdateSuccess();
    }
    
    @Override
    protected String getAuthHashKey() {
        return "email_auth_hash";
    }
    
    @Override
    protected String getChannelKey() {
        return "email";
    }
    
    @Override
    protected int getDeviceType() {
        return 11;
    }
    
    @Override
    protected String getId() {
        return OneSignal.getEmailId();
    }
    
    @Override
    void logoutChannel() {
        OneSignal.saveEmailId("");
        this.resetCurrentState();
        this.getToSyncUserState().removeFromSyncValues("identifier");
        final ArrayList list = new ArrayList();
        ((List)list).add((Object)"email_auth_hash");
        ((List)list).add((Object)"device_player_id");
        ((List)list).add((Object)"external_user_id");
        this.getToSyncUserState().removeFromSyncValues((List<String>)list);
        this.getToSyncUserState().persistState();
        OneSignal.getEmailSubscriptionState().clearEmailAndId();
    }
    
    @Override
    protected UserState newUserState(final String s, final boolean b) {
        return new UserStateEmail(s, b);
    }
    
    @Override
    void saveChannelId(final String s) {
        OneSignal.saveEmailId(s);
    }
    
    @Override
    void updateIdDependents(final String s) {
        OneSignal.updateEmailIdDependents(s);
    }
}
