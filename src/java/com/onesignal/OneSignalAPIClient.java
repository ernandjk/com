package com.onesignal;

import org.json.JSONObject;

public interface OneSignalAPIClient
{
    void get(final String p0, final OneSignalApiResponseHandler p1, final String p2);
    
    void getSync(final String p0, final OneSignalApiResponseHandler p1, final String p2);
    
    void post(final String p0, final JSONObject p1, final OneSignalApiResponseHandler p2);
    
    void postSync(final String p0, final JSONObject p1, final OneSignalApiResponseHandler p2);
    
    void put(final String p0, final JSONObject p1, final OneSignalApiResponseHandler p2);
    
    void putSync(final String p0, final JSONObject p1, final OneSignalApiResponseHandler p2);
}
