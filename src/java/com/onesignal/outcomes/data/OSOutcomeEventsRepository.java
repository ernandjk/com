package com.onesignal.outcomes.data;

import com.onesignal.OneSignalApiResponseHandler;
import java.util.Set;
import com.onesignal.outcomes.domain.OSOutcomeEventParams;
import com.onesignal.influence.domain.OSInfluence;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSLogger;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b \u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u0016J$\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u0015\u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013H\u0016J\u000e\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0013H\u0016J\u0010\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0010\u0018\u00010\u001aH\u0016J\u0010\u0010\u001b\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u0018H\u0016J(\u0010\u001d\u001a\u00020\u000e2\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020#H&J\u0010\u0010$\u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\u0018H\u0016J\u0016\u0010%\u001a\u00020\u000e2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00100\u001aH\u0016J\u0010\u0010'\u001a\u00020\u000e2\u0006\u0010(\u001a\u00020\u0018H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006)" }, d2 = { "Lcom/onesignal/outcomes/data/OSOutcomeEventsRepository;", "Lcom/onesignal/outcomes/domain/OSOutcomeEventsRepository;", "logger", "Lcom/onesignal/OSLogger;", "outcomeEventsCache", "Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;", "outcomeEventsService", "Lcom/onesignal/outcomes/data/OutcomeEventsService;", "(Lcom/onesignal/OSLogger;Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;Lcom/onesignal/outcomes/data/OutcomeEventsService;)V", "getLogger", "()Lcom/onesignal/OSLogger;", "getOutcomeEventsService", "()Lcom/onesignal/outcomes/data/OutcomeEventsService;", "cleanCachedUniqueOutcomeEventNotifications", "", "notificationTableName", "", "notificationIdColumnName", "getNotCachedUniqueOutcome", "", "Lcom/onesignal/influence/domain/OSInfluence;", "name", "influences", "getSavedOutcomeEvents", "Lcom/onesignal/outcomes/domain/OSOutcomeEventParams;", "getUnattributedUniqueOutcomeEventsSent", "", "removeEvent", "outcomeEvent", "requestMeasureOutcomeEvent", "appId", "deviceType", "", "event", "responseHandler", "Lcom/onesignal/OneSignalApiResponseHandler;", "saveOutcomeEvent", "saveUnattributedUniqueOutcomeEventsSent", "unattributedUniqueOutcomeEvents", "saveUniqueOutcomeNotifications", "eventParams", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public abstract class OSOutcomeEventsRepository implements com.onesignal.outcomes.domain.OSOutcomeEventsRepository
{
    private final OSLogger logger;
    private final OSOutcomeEventsCache outcomeEventsCache;
    private final OutcomeEventsService outcomeEventsService;
    
    public OSOutcomeEventsRepository(final OSLogger logger, final OSOutcomeEventsCache outcomeEventsCache, final OutcomeEventsService outcomeEventsService) {
        Intrinsics.checkNotNullParameter((Object)logger, "logger");
        Intrinsics.checkNotNullParameter((Object)outcomeEventsCache, "outcomeEventsCache");
        Intrinsics.checkNotNullParameter((Object)outcomeEventsService, "outcomeEventsService");
        this.logger = logger;
        this.outcomeEventsCache = outcomeEventsCache;
        this.outcomeEventsService = outcomeEventsService;
    }
    
    @Override
    public void cleanCachedUniqueOutcomeEventNotifications(final String s, final String s2) {
        Intrinsics.checkNotNullParameter((Object)s, "notificationTableName");
        Intrinsics.checkNotNullParameter((Object)s2, "notificationIdColumnName");
        this.outcomeEventsCache.cleanCachedUniqueOutcomeEventNotifications(s, s2);
    }
    
    protected final OSLogger getLogger() {
        return this.logger;
    }
    
    @Override
    public List<OSInfluence> getNotCachedUniqueOutcome(final String s, final List<OSInfluence> list) {
        Intrinsics.checkNotNullParameter((Object)s, "name");
        Intrinsics.checkNotNullParameter((Object)list, "influences");
        final List<OSInfluence> notCachedUniqueInfluencesForOutcome = this.outcomeEventsCache.getNotCachedUniqueInfluencesForOutcome(s, list);
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal getNotCachedUniqueOutcome influences: ");
        sb.append((Object)notCachedUniqueInfluencesForOutcome);
        logger.debug(sb.toString());
        return notCachedUniqueInfluencesForOutcome;
    }
    
    public final OutcomeEventsService getOutcomeEventsService() {
        return this.outcomeEventsService;
    }
    
    @Override
    public List<OSOutcomeEventParams> getSavedOutcomeEvents() {
        return this.outcomeEventsCache.getAllEventsToSend();
    }
    
    @Override
    public Set<String> getUnattributedUniqueOutcomeEventsSent() {
        final Set<String> unattributedUniqueOutcomeEventsSentByChannel = this.outcomeEventsCache.getUnattributedUniqueOutcomeEventsSentByChannel();
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal getUnattributedUniqueOutcomeEventsSentByChannel: ");
        sb.append((Object)unattributedUniqueOutcomeEventsSentByChannel);
        logger.debug(sb.toString());
        return unattributedUniqueOutcomeEventsSentByChannel;
    }
    
    @Override
    public void removeEvent(final OSOutcomeEventParams osOutcomeEventParams) {
        Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "outcomeEvent");
        this.outcomeEventsCache.deleteOldOutcomeEvent(osOutcomeEventParams);
    }
    
    @Override
    public abstract void requestMeasureOutcomeEvent(final String p0, final int p1, final OSOutcomeEventParams p2, final OneSignalApiResponseHandler p3);
    
    @Override
    public void saveOutcomeEvent(final OSOutcomeEventParams osOutcomeEventParams) {
        Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "event");
        this.outcomeEventsCache.saveOutcomeEvent(osOutcomeEventParams);
    }
    
    @Override
    public void saveUnattributedUniqueOutcomeEventsSent(final Set<String> set) {
        Intrinsics.checkNotNullParameter((Object)set, "unattributedUniqueOutcomeEvents");
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal save unattributedUniqueOutcomeEvents: ");
        sb.append((Object)set);
        logger.debug(sb.toString());
        this.outcomeEventsCache.saveUnattributedUniqueOutcomeEventsSentByChannel(set);
    }
    
    @Override
    public void saveUniqueOutcomeNotifications(final OSOutcomeEventParams osOutcomeEventParams) {
        Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "eventParams");
        this.outcomeEventsCache.saveUniqueOutcomeEventParams(osOutcomeEventParams);
    }
}
