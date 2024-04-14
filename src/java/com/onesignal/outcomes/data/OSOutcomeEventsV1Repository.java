package com.onesignal.outcomes.data;

import com.onesignal.influence.domain.OSInfluenceType;
import com.onesignal.outcomes.domain.OSOutcomeEventParams;
import org.json.JSONObject;
import org.json.JSONException;
import com.onesignal.OneSignalApiResponseHandler;
import com.onesignal.OSOutcomeEvent;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSLogger;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ(\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J(\u0010\u0013\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J(\u0010\u0014\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J(\u0010\u0017\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002¨\u0006\u0018" }, d2 = { "Lcom/onesignal/outcomes/data/OSOutcomeEventsV1Repository;", "Lcom/onesignal/outcomes/data/OSOutcomeEventsRepository;", "logger", "Lcom/onesignal/OSLogger;", "outcomeEventsCache", "Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;", "outcomeEventsService", "Lcom/onesignal/outcomes/data/OutcomeEventsService;", "(Lcom/onesignal/OSLogger;Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;Lcom/onesignal/outcomes/data/OutcomeEventsService;)V", "requestMeasureDirectOutcomeEvent", "", "appId", "", "deviceType", "", "event", "Lcom/onesignal/OSOutcomeEvent;", "responseHandler", "Lcom/onesignal/OneSignalApiResponseHandler;", "requestMeasureIndirectOutcomeEvent", "requestMeasureOutcomeEvent", "eventParams", "Lcom/onesignal/outcomes/domain/OSOutcomeEventParams;", "requestMeasureUnattributedOutcomeEvent", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeEventsV1Repository extends OSOutcomeEventsRepository
{
    public OSOutcomeEventsV1Repository(final OSLogger osLogger, final OSOutcomeEventsCache osOutcomeEventsCache, final OutcomeEventsService outcomeEventsService) {
        Intrinsics.checkNotNullParameter((Object)osLogger, "logger");
        Intrinsics.checkNotNullParameter((Object)osOutcomeEventsCache, "outcomeEventsCache");
        Intrinsics.checkNotNullParameter((Object)outcomeEventsService, "outcomeEventsService");
        super(osLogger, osOutcomeEventsCache, outcomeEventsService);
    }
    
    private final void requestMeasureDirectOutcomeEvent(final String s, final int n, final OSOutcomeEvent osOutcomeEvent, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        try {
            final JSONObject put = osOutcomeEvent.toJSONObjectForMeasure().put("app_id", (Object)s).put("device_type", n).put("direct", true);
            final OutcomeEventsService outcomeEventsService = this.getOutcomeEventsService();
            Intrinsics.checkNotNullExpressionValue((Object)put, "jsonObject");
            outcomeEventsService.sendOutcomeEvent(put, oneSignalApiResponseHandler);
        }
        catch (final JSONException ex) {
            this.getLogger().error("Generating direct outcome:JSON Failed.", (Throwable)ex);
        }
    }
    
    private final void requestMeasureIndirectOutcomeEvent(final String s, final int n, final OSOutcomeEvent osOutcomeEvent, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        try {
            final JSONObject put = osOutcomeEvent.toJSONObjectForMeasure().put("app_id", (Object)s).put("device_type", n).put("direct", false);
            final OutcomeEventsService outcomeEventsService = this.getOutcomeEventsService();
            Intrinsics.checkNotNullExpressionValue((Object)put, "jsonObject");
            outcomeEventsService.sendOutcomeEvent(put, oneSignalApiResponseHandler);
        }
        catch (final JSONException ex) {
            this.getLogger().error("Generating indirect outcome:JSON Failed.", (Throwable)ex);
        }
    }
    
    private final void requestMeasureUnattributedOutcomeEvent(final String s, final int n, final OSOutcomeEvent osOutcomeEvent, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        try {
            final JSONObject put = osOutcomeEvent.toJSONObjectForMeasure().put("app_id", (Object)s).put("device_type", n);
            final OutcomeEventsService outcomeEventsService = this.getOutcomeEventsService();
            Intrinsics.checkNotNullExpressionValue((Object)put, "jsonObject");
            outcomeEventsService.sendOutcomeEvent(put, oneSignalApiResponseHandler);
        }
        catch (final JSONException ex) {
            this.getLogger().error("Generating unattributed outcome:JSON Failed.", (Throwable)ex);
        }
    }
    
    @Override
    public void requestMeasureOutcomeEvent(final String s, final int n, final OSOutcomeEventParams osOutcomeEventParams, final OneSignalApiResponseHandler oneSignalApiResponseHandler) {
        Intrinsics.checkNotNullParameter((Object)s, "appId");
        Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "eventParams");
        Intrinsics.checkNotNullParameter((Object)oneSignalApiResponseHandler, "responseHandler");
        final OSOutcomeEvent fromOutcomeEventParamsV2toOutcomeEventV1 = OSOutcomeEvent.fromOutcomeEventParamsV2toOutcomeEventV1(osOutcomeEventParams);
        Intrinsics.checkNotNullExpressionValue((Object)fromOutcomeEventParamsV2toOutcomeEventV1, "event");
        final OSInfluenceType session = fromOutcomeEventParamsV2toOutcomeEventV1.getSession();
        if (session != null) {
            final int n2 = OSOutcomeEventsV1Repository$WhenMappings.$EnumSwitchMapping$0[session.ordinal()];
            if (n2 != 1) {
                if (n2 != 2) {
                    if (n2 == 3) {
                        this.requestMeasureUnattributedOutcomeEvent(s, n, fromOutcomeEventParamsV2toOutcomeEventV1, oneSignalApiResponseHandler);
                    }
                }
                else {
                    this.requestMeasureIndirectOutcomeEvent(s, n, fromOutcomeEventParamsV2toOutcomeEventV1, oneSignalApiResponseHandler);
                }
            }
            else {
                this.requestMeasureDirectOutcomeEvent(s, n, fromOutcomeEventParamsV2toOutcomeEventV1, oneSignalApiResponseHandler);
            }
        }
    }
}
