package com.onesignal;

import com.onesignal.influence.data.OSTrackerFactory;

class OSRemoteParamController
{
    private OneSignalRemoteParams.Params remoteParams;
    
    OSRemoteParamController() {
        this.remoteParams = null;
    }
    
    private void saveReceiveReceiptEnabled(final boolean b) {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_RECEIVE_RECEIPTS_ENABLED", b);
    }
    
    private void saveRestoreTTLFilter(final boolean b) {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "OS_RESTORE_TTL_FILTER", this.remoteParams.restoreTTLFilter);
    }
    
    void clearRemoteParams() {
        this.remoteParams = null;
    }
    
    boolean getClearGroupSummaryClick() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "OS_CLEAR_GROUP_SUMMARY_CLICK", true);
    }
    
    boolean getFirebaseAnalyticsEnabled() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "GT_FIREBASE_TRACKING_ENABLED", false);
    }
    
    OneSignalRemoteParams.Params getRemoteParams() {
        return this.remoteParams;
    }
    
    boolean getSavedUserConsentStatus() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_USER_PROVIDED_CONSENT", false);
    }
    
    boolean hasDisableGMSMissingPromptKey() {
        final OneSignalRemoteParams.Params remoteParams = this.remoteParams;
        return remoteParams != null && remoteParams.disableGMSMissingPrompt != null;
    }
    
    boolean hasLocationKey() {
        final OneSignalRemoteParams.Params remoteParams = this.remoteParams;
        return remoteParams != null && remoteParams.locationShared != null;
    }
    
    boolean hasPrivacyConsentKey() {
        final OneSignalRemoteParams.Params remoteParams = this.remoteParams;
        return remoteParams != null && remoteParams.requiresUserPrivacyConsent != null;
    }
    
    boolean hasUnsubscribeNotificationKey() {
        final OneSignalRemoteParams.Params remoteParams = this.remoteParams;
        return remoteParams != null && remoteParams.unsubscribeWhenNotificationsDisabled != null;
    }
    
    boolean isGMSMissingPromptDisable() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_DISABLE_GMS_MISSING_PROMPT", false);
    }
    
    boolean isLocationShared() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_LOCATION_SHARED", true);
    }
    
    boolean isPrivacyConsentRequired() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_REQUIRES_USER_PRIVACY_CONSENT", false);
    }
    
    boolean isReceiveReceiptEnabled() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_RECEIVE_RECEIPTS_ENABLED", false);
    }
    
    boolean isRemoteParamsCallDone() {
        return this.remoteParams != null;
    }
    
    boolean isRestoreTTLFilterActive() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "OS_RESTORE_TTL_FILTER", true);
    }
    
    void saveGMSMissingPromptDisable(final boolean b) {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_DISABLE_GMS_MISSING_PROMPT", b);
    }
    
    void saveLocationShared(final boolean b) {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_LOCATION_SHARED", b);
    }
    
    void savePrivacyConsentRequired(final boolean b) {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_REQUIRES_USER_PRIVACY_CONSENT", b);
    }
    
    void saveRemoteParams(final OneSignalRemoteParams.Params remoteParams, final OSTrackerFactory osTrackerFactory, final OSSharedPreferences osSharedPreferences, final OSLogger osLogger) {
        this.remoteParams = remoteParams;
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "GT_FIREBASE_TRACKING_ENABLED", remoteParams.firebaseAnalytics);
        this.saveRestoreTTLFilter(remoteParams.restoreTTLFilter);
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "OS_CLEAR_GROUP_SUMMARY_CLICK", remoteParams.clearGroupOnSummaryClick);
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, osSharedPreferences.getOutcomesV2KeyName(), remoteParams.influenceParams.outcomesV2ServiceEnabled);
        this.saveReceiveReceiptEnabled(remoteParams.receiveReceiptEnabled);
        final StringBuilder sb = new StringBuilder("OneSignal saveInfluenceParams: ");
        sb.append(remoteParams.influenceParams.toString());
        osLogger.debug(sb.toString());
        osTrackerFactory.saveInfluenceParams(remoteParams.influenceParams);
        if (remoteParams.disableGMSMissingPrompt != null) {
            this.saveGMSMissingPromptDisable(remoteParams.disableGMSMissingPrompt);
        }
        if (remoteParams.unsubscribeWhenNotificationsDisabled != null) {
            this.saveUnsubscribeWhenNotificationsAreDisabled(remoteParams.unsubscribeWhenNotificationsDisabled);
        }
        if (remoteParams.locationShared != null) {
            OneSignal.startLocationShared(remoteParams.locationShared);
        }
        if (remoteParams.requiresUserPrivacyConsent != null) {
            this.savePrivacyConsentRequired(remoteParams.requiresUserPrivacyConsent);
        }
    }
    
    void saveUnsubscribeWhenNotificationsAreDisabled(final boolean b) {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_UNSUBSCRIBE_WHEN_NOTIFICATIONS_DISABLED", b);
    }
    
    void saveUserConsentStatus(final boolean b) {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_USER_PROVIDED_CONSENT", b);
    }
    
    boolean unsubscribeWhenNotificationsAreDisabled() {
        return OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_UNSUBSCRIBE_WHEN_NOTIFICATIONS_DISABLED", true);
    }
}
