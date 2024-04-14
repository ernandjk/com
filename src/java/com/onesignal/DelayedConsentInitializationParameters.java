package com.onesignal;

import android.content.Context;

class DelayedConsentInitializationParameters
{
    private final String appId;
    private final Context context;
    
    DelayedConsentInitializationParameters(final Context context, final String appId) {
        this.context = context;
        this.appId = appId;
    }
    
    String getAppId() {
        return this.appId;
    }
    
    Context getContext() {
        return this.context;
    }
}
