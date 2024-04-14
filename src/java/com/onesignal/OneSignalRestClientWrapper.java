package com.onesignal;

import org.json.JSONObject;

class OneSignalRestClientWrapper implements OneSignalAPIClient
{
    public void get(final String s, final OneSignalApiResponseHandler oneSignalApiResponseHandler, final String s2) {
        OneSignalRestClient.get(s, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, oneSignalApiResponseHandler) {
            final OneSignalRestClientWrapper this$0;
            final OneSignalApiResponseHandler val$responseHandler;
            
            public void onFailure(final int n, final String s, final Throwable t) {
                this.val$responseHandler.onFailure(n, s, t);
            }
            
            public void onSuccess(final String s) {
                this.val$responseHandler.onSuccess(s);
            }
        }, s2);
    }
    
    public void getSync(final String s, final OneSignalApiResponseHandler oneSignalApiResponseHandler, final String s2) {
        OneSignalRestClient.getSync(s, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, oneSignalApiResponseHandler) {
            final OneSignalRestClientWrapper this$0;
            final OneSignalApiResponseHandler val$responseHandler;
            
            public void onFailure(final int n, final String s, final Throwable t) {
                this.val$responseHandler.onFailure(n, s, t);
            }
            
            public void onSuccess(final String s) {
                this.val$responseHandler.onSuccess(s);
            }
        }, s2);
    }
    
    public void post(final String s, final JSONObject jsonObject, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        OneSignalRestClient.post(s, jsonObject, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, oneSignalApiResponseHandler) {
            final OneSignalRestClientWrapper this$0;
            final OneSignalApiResponseHandler val$responseHandler;
            
            public void onFailure(final int n, final String s, final Throwable t) {
                this.val$responseHandler.onFailure(n, s, t);
            }
            
            public void onSuccess(final String s) {
                this.val$responseHandler.onSuccess(s);
            }
        });
    }
    
    public void postSync(final String s, final JSONObject jsonObject, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        OneSignalRestClient.postSync(s, jsonObject, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, oneSignalApiResponseHandler) {
            final OneSignalRestClientWrapper this$0;
            final OneSignalApiResponseHandler val$responseHandler;
            
            public void onFailure(final int n, final String s, final Throwable t) {
                this.val$responseHandler.onFailure(n, s, t);
            }
            
            public void onSuccess(final String s) {
                this.val$responseHandler.onSuccess(s);
            }
        });
    }
    
    public void put(final String s, final JSONObject jsonObject, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        OneSignalRestClient.put(s, jsonObject, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, oneSignalApiResponseHandler) {
            final OneSignalRestClientWrapper this$0;
            final OneSignalApiResponseHandler val$responseHandler;
            
            public void onFailure(final int n, final String s, final Throwable t) {
                this.val$responseHandler.onFailure(n, s, t);
            }
            
            public void onSuccess(final String s) {
                this.val$responseHandler.onSuccess(s);
            }
        });
    }
    
    public void putSync(final String s, final JSONObject jsonObject, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        OneSignalRestClient.putSync(s, jsonObject, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, oneSignalApiResponseHandler) {
            final OneSignalRestClientWrapper this$0;
            final OneSignalApiResponseHandler val$responseHandler;
            
            public void onFailure(final int n, final String s, final Throwable t) {
                this.val$responseHandler.onFailure(n, s, t);
            }
            
            public void onSuccess(final String s) {
                this.val$responseHandler.onSuccess(s);
            }
        });
    }
}
