package com.onesignal;

class OSSMSSubscriptionChangedInternalObserver
{
    static void fireChangesToPublicObserver(final OSSMSSubscriptionState ossmsSubscriptionState) {
        if (OneSignal.getSMSSubscriptionStateChangesObserver().notifyChange(new OSSMSSubscriptionStateChanges(OneSignal.lastSMSSubscriptionState, (OSSMSSubscriptionState)ossmsSubscriptionState.clone()))) {
            (OneSignal.lastSMSSubscriptionState = (OSSMSSubscriptionState)ossmsSubscriptionState.clone()).persistAsFrom();
        }
    }
    
    void changed(final OSSMSSubscriptionState ossmsSubscriptionState) {
        fireChangesToPublicObserver(ossmsSubscriptionState);
    }
}
