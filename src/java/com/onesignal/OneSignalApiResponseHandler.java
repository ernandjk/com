package com.onesignal;

public interface OneSignalApiResponseHandler
{
    void onFailure(final int p0, final String p1, final Throwable p2);
    
    void onSuccess(final String p0);
}
