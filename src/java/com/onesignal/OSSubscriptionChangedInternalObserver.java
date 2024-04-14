package com.onesignal;

class OSSubscriptionChangedInternalObserver
{
    static void fireChangesToPublicObserver(final OSSubscriptionState osSubscriptionState) {
        if (OneSignal.getSubscriptionStateChangesObserver().notifyChange(new OSSubscriptionStateChanges(OneSignal.lastSubscriptionState, (OSSubscriptionState)osSubscriptionState.clone()))) {
            (OneSignal.lastSubscriptionState = (OSSubscriptionState)osSubscriptionState.clone()).persistAsFrom();
        }
    }
    
    public void changed(final OSSubscriptionState osSubscriptionState) {
        fireChangesToPublicObserver(osSubscriptionState);
    }
}
