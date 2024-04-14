package com.onesignal;

class OSPermissionChangedInternalObserver
{
    static void fireChangesToPublicObserver(final OSPermissionState osPermissionState) {
        if (OneSignal.getPermissionStateChangesObserver().notifyChange(new OSPermissionStateChanges(OneSignal.lastPermissionState, (OSPermissionState)osPermissionState.clone()))) {
            (OneSignal.lastPermissionState = (OSPermissionState)osPermissionState.clone()).persistAsFrom();
        }
    }
    
    static void handleInternalChanges(final OSPermissionState osPermissionState) {
        if (!osPermissionState.areNotificationsEnabled()) {
            BadgeCountUpdater.updateCount(0, OneSignal.appContext);
        }
        OneSignalStateSynchronizer.setPermission(OneSignal.areNotificationsEnabledForSubscribedState());
    }
    
    void changed(final OSPermissionState osPermissionState) {
        handleInternalChanges(osPermissionState);
        fireChangesToPublicObserver(osPermissionState);
    }
}
