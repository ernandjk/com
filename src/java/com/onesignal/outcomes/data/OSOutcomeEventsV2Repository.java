package com.onesignal.outcomes.data;

import org.json.JSONObject;
import org.json.JSONException;
import com.onesignal.OneSignalApiResponseHandler;
import com.onesignal.outcomes.domain.OSOutcomeEventParams;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSLogger;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ(\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016¨\u0006\u0013" }, d2 = { "Lcom/onesignal/outcomes/data/OSOutcomeEventsV2Repository;", "Lcom/onesignal/outcomes/data/OSOutcomeEventsRepository;", "logger", "Lcom/onesignal/OSLogger;", "outcomeEventsCache", "Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;", "outcomeEventsService", "Lcom/onesignal/outcomes/data/OutcomeEventsService;", "(Lcom/onesignal/OSLogger;Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;Lcom/onesignal/outcomes/data/OutcomeEventsService;)V", "requestMeasureOutcomeEvent", "", "appId", "", "deviceType", "", "event", "Lcom/onesignal/outcomes/domain/OSOutcomeEventParams;", "responseHandler", "Lcom/onesignal/OneSignalApiResponseHandler;", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeEventsV2Repository extends OSOutcomeEventsRepository
{
    public OSOutcomeEventsV2Repository(final OSLogger osLogger, final OSOutcomeEventsCache osOutcomeEventsCache, final OutcomeEventsService outcomeEventsService) {
        Intrinsics.checkNotNullParameter((Object)osLogger, "logger");
        Intrinsics.checkNotNullParameter((Object)osOutcomeEventsCache, "outcomeEventsCache");
        Intrinsics.checkNotNullParameter((Object)outcomeEventsService, "outcomeEventsService");
        super(osLogger, osOutcomeEventsCache, outcomeEventsService);
    }
    
    @Override
    public void requestMeasureOutcomeEvent(final String s, final int n, final OSOutcomeEventParams osOutcomeEventParams, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        Intrinsics.checkNotNullParameter((Object)s, "appId");
        Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "event");
        Intrinsics.checkNotNullParameter((Object)oneSignalApiResponseHandler, "responseHandler");
        try {
            final JSONObject put = osOutcomeEventParams.toJSONObject().put("app_id", (Object)s).put("device_type", n);
            final OutcomeEventsService outcomeEventsService = this.getOutcomeEventsService();
            Intrinsics.checkNotNullExpressionValue((Object)put, "jsonObject");
            outcomeEventsService.sendOutcomeEvent(put, oneSignalApiResponseHandler);
        }
        catch (final JSONException ex) {
            this.getLogger().error("Generating indirect outcome:JSON Failed.", (Throwable)ex);
        }
    }
}
