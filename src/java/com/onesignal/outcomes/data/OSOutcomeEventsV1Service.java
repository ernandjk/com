package com.onesignal.outcomes.data;

import com.onesignal.OneSignalApiResponseHandler;
import org.json.JSONObject;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OneSignalAPIClient;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016¨\u0006\u000b" }, d2 = { "Lcom/onesignal/outcomes/data/OSOutcomeEventsV1Service;", "Lcom/onesignal/outcomes/data/OSOutcomeEventsClient;", "client", "Lcom/onesignal/OneSignalAPIClient;", "(Lcom/onesignal/OneSignalAPIClient;)V", "sendOutcomeEvent", "", "jsonObject", "Lorg/json/JSONObject;", "responseHandler", "Lcom/onesignal/OneSignalApiResponseHandler;", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeEventsV1Service extends OSOutcomeEventsClient
{
    public OSOutcomeEventsV1Service(final OneSignalAPIClient oneSignalAPIClient) {
        Intrinsics.checkNotNullParameter((Object)oneSignalAPIClient, "client");
        super(oneSignalAPIClient);
    }
    
    @Override
    public void sendOutcomeEvent(final JSONObject jsonObject, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        Intrinsics.checkNotNullParameter((Object)jsonObject, "jsonObject");
        Intrinsics.checkNotNullParameter((Object)oneSignalApiResponseHandler, "responseHandler");
        this.getClient().post("outcomes/measure", jsonObject, oneSignalApiResponseHandler);
    }
}
