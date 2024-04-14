package com.onesignal;

import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

class OneSignalStateSynchronizer
{
    private static final Object LOCK;
    private static HashMap<UserStateSynchronizerType, UserStateSynchronizer> userStateSynchronizers;
    
    static {
        LOCK = new Object();
        OneSignalStateSynchronizer.userStateSynchronizers = (HashMap<UserStateSynchronizerType, UserStateSynchronizer>)new HashMap();
    }
    
    static void clearLocation() {
        getPushStateSynchronizer().clearLocation();
        getEmailStateSynchronizer().clearLocation();
        getSMSStateSynchronizer().clearLocation();
    }
    
    static UserStateEmailSynchronizer getEmailStateSynchronizer() {
        Label_0071: {
            if (OneSignalStateSynchronizer.userStateSynchronizers.containsKey((Object)UserStateSynchronizerType.EMAIL) && OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.EMAIL) != null) {
                break Label_0071;
            }
            final Object lock = OneSignalStateSynchronizer.LOCK;
            synchronized (lock) {
                if (OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.EMAIL) == null) {
                    OneSignalStateSynchronizer.userStateSynchronizers.put((Object)UserStateSynchronizerType.EMAIL, (Object)new UserStateEmailSynchronizer());
                }
                monitorexit(lock);
                return (UserStateEmailSynchronizer)OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.EMAIL);
            }
        }
    }
    
    static String getLanguage() {
        return getPushStateSynchronizer().getLanguage();
    }
    
    static UserStatePushSynchronizer getPushStateSynchronizer() {
        Label_0071: {
            if (OneSignalStateSynchronizer.userStateSynchronizers.containsKey((Object)UserStateSynchronizerType.PUSH) && OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.PUSH) != null) {
                break Label_0071;
            }
            final Object lock = OneSignalStateSynchronizer.LOCK;
            synchronized (lock) {
                if (OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.PUSH) == null) {
                    OneSignalStateSynchronizer.userStateSynchronizers.put((Object)UserStateSynchronizerType.PUSH, (Object)new UserStatePushSynchronizer());
                }
                monitorexit(lock);
                return (UserStatePushSynchronizer)OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.PUSH);
            }
        }
    }
    
    static String getRegistrationId() {
        return getPushStateSynchronizer().getRegistrationId();
    }
    
    static UserStateSMSSynchronizer getSMSStateSynchronizer() {
        Label_0071: {
            if (OneSignalStateSynchronizer.userStateSynchronizers.containsKey((Object)UserStateSynchronizerType.SMS) && OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.SMS) != null) {
                break Label_0071;
            }
            final Object lock = OneSignalStateSynchronizer.LOCK;
            synchronized (lock) {
                if (OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.SMS) == null) {
                    OneSignalStateSynchronizer.userStateSynchronizers.put((Object)UserStateSynchronizerType.SMS, (Object)new UserStateSMSSynchronizer());
                }
                monitorexit(lock);
                return (UserStateSMSSynchronizer)OneSignalStateSynchronizer.userStateSynchronizers.get((Object)UserStateSynchronizerType.SMS);
            }
        }
    }
    
    static boolean getSubscribed() {
        return getPushStateSynchronizer().getSubscribed();
    }
    
    static boolean getSyncAsNewSession() {
        return getPushStateSynchronizer().getSyncAsNewSession() || getEmailStateSynchronizer().getSyncAsNewSession() || getSMSStateSynchronizer().getSyncAsNewSession();
    }
    
    static UserStateSynchronizer$GetTagsResult getTags(final boolean b) {
        return getPushStateSynchronizer().getTags(b);
    }
    
    static List<UserStateSynchronizer> getUserStateSynchronizers() {
        final ArrayList list = new ArrayList();
        ((List)list).add((Object)getPushStateSynchronizer());
        if (OneSignal.hasEmailId()) {
            ((List)list).add((Object)getEmailStateSynchronizer());
        }
        if (OneSignal.hasSMSlId()) {
            ((List)list).add((Object)getSMSStateSynchronizer());
        }
        return (List<UserStateSynchronizer>)list;
    }
    
    static boolean getUserSubscribePreference() {
        return getPushStateSynchronizer().getUserSubscribePreference();
    }
    
    static void initUserState() {
        getPushStateSynchronizer().initUserState();
        getEmailStateSynchronizer().initUserState();
        getSMSStateSynchronizer().initUserState();
    }
    
    static void logoutEmail() {
        getPushStateSynchronizer().logoutEmail();
        getEmailStateSynchronizer().logoutChannel();
    }
    
    static void logoutSMS() {
        getSMSStateSynchronizer().logoutChannel();
        getPushStateSynchronizer().logoutSMS();
    }
    
    static boolean persist() {
        final boolean persist = getPushStateSynchronizer().persist();
        final boolean persist2 = getEmailStateSynchronizer().persist();
        final boolean persist3 = getSMSStateSynchronizer().persist();
        final boolean b = true;
        boolean b2 = persist2;
        if (persist2) {
            b2 = (getEmailStateSynchronizer().getRegistrationId() != null);
        }
        boolean b3 = persist3;
        if (persist3) {
            b3 = (getSMSStateSynchronizer().getRegistrationId() != null);
        }
        boolean b4 = b;
        if (!persist) {
            b4 = b;
            if (!b2) {
                b4 = (b3 && b);
            }
        }
        return b4;
    }
    
    static void readyToUpdate(final boolean b) {
        getPushStateSynchronizer().readyToUpdate(b);
        getEmailStateSynchronizer().readyToUpdate(b);
        getSMSStateSynchronizer().readyToUpdate(b);
    }
    
    static void refreshSecondaryChannelState() {
        getEmailStateSynchronizer().refresh();
        getSMSStateSynchronizer().refresh();
    }
    
    static void resetCurrentState() {
        getPushStateSynchronizer().resetCurrentState();
        getEmailStateSynchronizer().resetCurrentState();
        getSMSStateSynchronizer().resetCurrentState();
        getPushStateSynchronizer().saveChannelId((String)null);
        getEmailStateSynchronizer().saveChannelId((String)null);
        getSMSStateSynchronizer().saveChannelId((String)null);
        OneSignal.setLastSessionTime(-3660L);
    }
    
    static void sendPurchases(final JSONObject jsonObject, final OneSignalRestClient.ResponseHandler responseHandler) {
        final Iterator iterator = getUserStateSynchronizers().iterator();
        while (iterator.hasNext()) {
            ((UserStateSynchronizer)iterator.next()).sendPurchases(jsonObject, responseHandler);
        }
    }
    
    static void sendTags(JSONObject put, final OneSignal.ChangeTagsUpdateHandler changeTagsUpdateHandler) {
        try {
            put = new JSONObject().put("tags", (Object)put);
            getPushStateSynchronizer().sendTags(put, changeTagsUpdateHandler);
            getEmailStateSynchronizer().sendTags(put, changeTagsUpdateHandler);
            getSMSStateSynchronizer().sendTags(put, changeTagsUpdateHandler);
        }
        catch (final JSONException ex) {
            if (changeTagsUpdateHandler != null) {
                final StringBuilder sb = new StringBuilder("Encountered an error attempting to serialize your tags into JSON: ");
                sb.append(ex.getMessage());
                sb.append("\n");
                sb.append((Object)ex.getStackTrace());
                changeTagsUpdateHandler.onFailure(new OneSignal.SendTagsError(-1, sb.toString()));
            }
            ex.printStackTrace();
        }
    }
    
    static void setEmail(final String s, final String s2) {
        getPushStateSynchronizer().setEmail(s, s2);
        getEmailStateSynchronizer().setChannelId(s, s2);
    }
    
    static void setExternalUserId(final String s, final String s2, final OneSignal.OSExternalUserIdUpdateCompletionHandler osExternalUserIdUpdateCompletionHandler) throws JSONException {
        final OneSignalStateSynchronizer$1 oneSignalStateSynchronizer$1 = new OneSignalStateSynchronizer$1(new JSONObject(), osExternalUserIdUpdateCompletionHandler);
        final Iterator iterator = getUserStateSynchronizers().iterator();
        while (iterator.hasNext()) {
            ((UserStateSynchronizer)iterator.next()).setExternalUserId(s, s2, (OneSignal.OSInternalExternalUserIdUpdateCompletionHandler)oneSignalStateSynchronizer$1);
        }
    }
    
    static void setNewSession() {
        getPushStateSynchronizer().setNewSession();
        getEmailStateSynchronizer().setNewSession();
        getSMSStateSynchronizer().setNewSession();
    }
    
    static void setNewSessionForEmail() {
        getEmailStateSynchronizer().setNewSession();
    }
    
    static void setPermission(final boolean permission) {
        getPushStateSynchronizer().setPermission(permission);
    }
    
    static void setSMSNumber(final String s, final String s2) {
        getPushStateSynchronizer().setSMSNumber(s, s2);
        getSMSStateSynchronizer().setChannelId(s, s2);
    }
    
    static void setSubscription(final boolean subscription) {
        getPushStateSynchronizer().setSubscription(subscription);
    }
    
    static void syncUserState(final boolean b) {
        getPushStateSynchronizer().syncUserState(b);
        getEmailStateSynchronizer().syncUserState(b);
        getSMSStateSynchronizer().syncUserState(b);
    }
    
    static void updateDeviceInfo(final JSONObject jsonObject, final OSDeviceInfoCompletionHandler osDeviceInfoCompletionHandler) {
        getPushStateSynchronizer().updateDeviceInfo(jsonObject, osDeviceInfoCompletionHandler);
        getEmailStateSynchronizer().updateDeviceInfo(jsonObject, osDeviceInfoCompletionHandler);
        getSMSStateSynchronizer().updateDeviceInfo(jsonObject, osDeviceInfoCompletionHandler);
    }
    
    static void updateLocation(final LocationController.LocationPoint locationPoint) {
        getPushStateSynchronizer().updateLocation(locationPoint);
        getEmailStateSynchronizer().updateLocation(locationPoint);
        getSMSStateSynchronizer().updateLocation(locationPoint);
    }
    
    static void updatePushState(final JSONObject jsonObject) {
        getPushStateSynchronizer().updateState(jsonObject);
    }
    
    interface OSDeviceInfoCompletionHandler
    {
        void onFailure(final OSDeviceInfoError p0);
        
        void onSuccess(final String p0);
    }
    
    static class OSDeviceInfoError
    {
        public int errorCode;
        public String message;
        
        OSDeviceInfoError(final int errorCode, final String message) {
            this.errorCode = errorCode;
            this.message = message;
        }
        
        public int getCode() {
            return this.errorCode;
        }
        
        public String getMessage() {
            return this.message;
        }
    }
    
    enum UserStateSynchronizerType
    {
        private static final UserStateSynchronizerType[] $VALUES;
        
        EMAIL, 
        PUSH, 
        SMS;
        
        public boolean isEmail() {
            return this.equals((Object)UserStateSynchronizerType.EMAIL);
        }
        
        public boolean isPush() {
            return this.equals((Object)UserStateSynchronizerType.PUSH);
        }
        
        public boolean isSMS() {
            return this.equals((Object)UserStateSynchronizerType.SMS);
        }
    }
}
