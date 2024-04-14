package com.onesignal.influence.data;

import com.onesignal.OneSignalRemoteParams$InfluenceParams;
import kotlin.collections.CollectionsKt;
import java.util.Collection;
import java.util.ArrayList;
import com.onesignal.OneSignal$AppEntryAction;
import java.util.Iterator;
import com.onesignal.influence.domain.OSInfluence;
import java.util.List;
import org.json.JSONObject;
import com.onesignal.influence.OSInfluenceConstants;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSTime;
import com.onesignal.OSLogger;
import com.onesignal.OSSharedPreferences;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u001c\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\nJ\u0010\u0010!\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\"\u001a\u00020#J\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\"\u001a\u00020#J\u0006\u0010%\u001a\u00020\u001eJ\u000e\u0010&\u001a\u00020\u001e2\u0006\u0010'\u001a\u00020(R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n8F¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0010\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\n8F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\rR\u0011\u0010\u0016\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0012R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00140\n8F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\rR\u001a\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\u000b0\u001bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006)" }, d2 = { "Lcom/onesignal/influence/data/OSTrackerFactory;", "", "preferences", "Lcom/onesignal/OSSharedPreferences;", "logger", "Lcom/onesignal/OSLogger;", "timeProvider", "Lcom/onesignal/OSTime;", "(Lcom/onesignal/OSSharedPreferences;Lcom/onesignal/OSLogger;Lcom/onesignal/OSTime;)V", "channels", "", "Lcom/onesignal/influence/data/OSChannelTracker;", "getChannels", "()Ljava/util/List;", "dataRepository", "Lcom/onesignal/influence/data/OSInfluenceDataRepository;", "iAMChannelTracker", "getIAMChannelTracker", "()Lcom/onesignal/influence/data/OSChannelTracker;", "influences", "Lcom/onesignal/influence/domain/OSInfluence;", "getInfluences", "notificationChannelTracker", "getNotificationChannelTracker", "sessionInfluences", "getSessionInfluences", "trackers", "Ljava/util/concurrent/ConcurrentHashMap;", "", "addSessionData", "", "jsonObject", "Lorg/json/JSONObject;", "getChannelByEntryAction", "entryAction", "Lcom/onesignal/OneSignal$AppEntryAction;", "getChannelsToResetByEntryAction", "initFromCache", "saveInfluenceParams", "influenceParams", "Lcom/onesignal/OneSignalRemoteParams$InfluenceParams;", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSTrackerFactory
{
    private final OSInfluenceDataRepository dataRepository;
    private final ConcurrentHashMap<String, OSChannelTracker> trackers;
    
    public OSTrackerFactory(final OSSharedPreferences osSharedPreferences, final OSLogger osLogger, final OSTime osTime) {
        Intrinsics.checkNotNullParameter((Object)osSharedPreferences, "preferences");
        Intrinsics.checkNotNullParameter((Object)osLogger, "logger");
        Intrinsics.checkNotNullParameter((Object)osTime, "timeProvider");
        final ConcurrentHashMap trackers = new ConcurrentHashMap();
        this.trackers = (ConcurrentHashMap<String, OSChannelTracker>)trackers;
        final OSInfluenceDataRepository dataRepository = new OSInfluenceDataRepository(osSharedPreferences);
        this.dataRepository = dataRepository;
        ((Map)trackers).put((Object)OSInfluenceConstants.INSTANCE.getIAM_TAG(), (Object)new OSInAppMessageTracker(dataRepository, osLogger, osTime));
        ((Map)trackers).put((Object)OSInfluenceConstants.INSTANCE.getNOTIFICATION_TAG(), (Object)new OSNotificationTracker(dataRepository, osLogger, osTime));
    }
    
    public final void addSessionData(final JSONObject jsonObject, final List<OSInfluence> list) {
        Intrinsics.checkNotNullParameter((Object)jsonObject, "jsonObject");
        Intrinsics.checkNotNullParameter((Object)list, "influences");
        for (final OSInfluence osInfluence : (Iterable)list) {
            if (OSTrackerFactory$WhenMappings.$EnumSwitchMapping$0[osInfluence.getInfluenceChannel().ordinal()] != 1) {
                continue;
            }
            this.getNotificationChannelTracker().addSessionData(jsonObject, osInfluence);
        }
    }
    
    public final OSChannelTracker getChannelByEntryAction(final OneSignal$AppEntryAction oneSignal$AppEntryAction) {
        Intrinsics.checkNotNullParameter((Object)oneSignal$AppEntryAction, "entryAction");
        OSChannelTracker notificationChannelTracker;
        if (oneSignal$AppEntryAction.isNotificationClick()) {
            notificationChannelTracker = this.getNotificationChannelTracker();
        }
        else {
            notificationChannelTracker = null;
        }
        return notificationChannelTracker;
    }
    
    public final List<OSChannelTracker> getChannels() {
        final List list = (List)new ArrayList();
        list.add((Object)this.getNotificationChannelTracker());
        list.add((Object)this.getIAMChannelTracker());
        return (List<OSChannelTracker>)list;
    }
    
    public final List<OSChannelTracker> getChannelsToResetByEntryAction(final OneSignal$AppEntryAction oneSignal$AppEntryAction) {
        Intrinsics.checkNotNullParameter((Object)oneSignal$AppEntryAction, "entryAction");
        final List list = (List)new ArrayList();
        if (oneSignal$AppEntryAction.isAppClose()) {
            return (List<OSChannelTracker>)list;
        }
        OSChannelTracker notificationChannelTracker;
        if (oneSignal$AppEntryAction.isAppOpen()) {
            notificationChannelTracker = this.getNotificationChannelTracker();
        }
        else {
            notificationChannelTracker = null;
        }
        if (notificationChannelTracker != null) {
            list.add((Object)notificationChannelTracker);
        }
        list.add((Object)this.getIAMChannelTracker());
        return (List<OSChannelTracker>)list;
    }
    
    public final OSChannelTracker getIAMChannelTracker() {
        final Object value = this.trackers.get((Object)OSInfluenceConstants.INSTANCE.getIAM_TAG());
        Intrinsics.checkNotNull(value);
        return (OSChannelTracker)value;
    }
    
    public final List<OSInfluence> getInfluences() {
        final Collection values = this.trackers.values();
        Intrinsics.checkNotNullExpressionValue((Object)values, "trackers.values");
        final Iterable iterable = (Iterable)values;
        final Collection collection = (Collection)new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
        final Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            collection.add((Object)((OSChannelTracker)iterator.next()).getCurrentSessionInfluence());
        }
        return (List<OSInfluence>)collection;
    }
    
    public final OSChannelTracker getNotificationChannelTracker() {
        final Object value = this.trackers.get((Object)OSInfluenceConstants.INSTANCE.getNOTIFICATION_TAG());
        Intrinsics.checkNotNull(value);
        return (OSChannelTracker)value;
    }
    
    public final List<OSInfluence> getSessionInfluences() {
        final Collection values = this.trackers.values();
        Intrinsics.checkNotNullExpressionValue((Object)values, "trackers.values");
        final Iterable iterable = (Iterable)values;
        final Collection collection = (Collection)new ArrayList();
        for (final Object next : iterable) {
            if (Intrinsics.areEqual((Object)((OSChannelTracker)next).getIdTag(), (Object)OSInfluenceConstants.INSTANCE.getIAM_TAG()) ^ true) {
                collection.add(next);
            }
        }
        final Iterable iterable2 = (Iterable)collection;
        final Collection collection2 = (Collection)new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10));
        final Iterator iterator2 = iterable2.iterator();
        while (iterator2.hasNext()) {
            collection2.add((Object)((OSChannelTracker)iterator2.next()).getCurrentSessionInfluence());
        }
        return (List<OSInfluence>)collection2;
    }
    
    public final void initFromCache() {
        final Collection values = this.trackers.values();
        Intrinsics.checkNotNullExpressionValue((Object)values, "trackers.values");
        final Iterator iterator = ((Iterable)values).iterator();
        while (iterator.hasNext()) {
            ((OSChannelTracker)iterator.next()).initInfluencedTypeFromCache();
        }
    }
    
    public final void saveInfluenceParams(final OneSignalRemoteParams$InfluenceParams oneSignalRemoteParams$InfluenceParams) {
        Intrinsics.checkNotNullParameter((Object)oneSignalRemoteParams$InfluenceParams, "influenceParams");
        this.dataRepository.saveInfluenceParams(oneSignalRemoteParams$InfluenceParams);
    }
}
