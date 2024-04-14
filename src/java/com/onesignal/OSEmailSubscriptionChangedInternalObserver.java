package com.onesignal;

class OSEmailSubscriptionChangedInternalObserver
{
    static void fireChangesToPublicObserver(final OSEmailSubscriptionState osEmailSubscriptionState) {
        if (OneSignal.getEmailSubscriptionStateChangesObserver().notifyChange(new OSEmailSubscriptionStateChanges(OneSignal.lastEmailSubscriptionState, (OSEmailSubscriptionState)osEmailSubscriptionState.clone()))) {
            (OneSignal.lastEmailSubscriptionState = (OSEmailSubscriptionState)osEmailSubscriptionState.clone()).persistAsFrom();
        }
    }
    
    void changed(final OSEmailSubscriptionState osEmailSubscriptionState) {
        fireChangesToPublicObserver(osEmailSubscriptionState);
    }
}
