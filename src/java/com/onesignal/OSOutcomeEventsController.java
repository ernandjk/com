package com.onesignal;

import com.onesignal.influence.domain.OSInfluenceType;
import com.onesignal.outcomes.domain.OSOutcomeSource;
import com.onesignal.outcomes.domain.OSOutcomeSourceBody;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.onesignal.influence.domain.OSInfluence;
import java.util.List;
import com.onesignal.outcomes.domain.OSOutcomeEventParams;
import java.util.Set;
import com.onesignal.outcomes.data.OSOutcomeEventsFactory;

class OSOutcomeEventsController
{
    private static final String OS_DELETE_CACHED_UNIQUE_OUTCOMES_NOTIFICATIONS_THREAD = "OS_DELETE_CACHED_UNIQUE_OUTCOMES_NOTIFICATIONS_THREAD";
    private static final String OS_SAVE_OUTCOMES = "OS_SAVE_OUTCOMES";
    private static final String OS_SAVE_UNIQUE_OUTCOME_NOTIFICATIONS = "OS_SAVE_UNIQUE_OUTCOME_NOTIFICATIONS";
    private static final String OS_SEND_SAVED_OUTCOMES = "OS_SEND_SAVED_OUTCOMES";
    private final OSSessionManager osSessionManager;
    private final OSOutcomeEventsFactory outcomeEventsFactory;
    private Set<String> unattributedUniqueOutcomeEventsSentOnSession;
    
    public OSOutcomeEventsController(final OSSessionManager osSessionManager, final OSOutcomeEventsFactory outcomeEventsFactory) {
        this.osSessionManager = osSessionManager;
        this.outcomeEventsFactory = outcomeEventsFactory;
        this.initUniqueOutcomeEventsSentSets();
    }
    
    private List<OSInfluence> getUniqueIds(final String s, final List<OSInfluence> list) {
        List notCachedUniqueOutcome = this.outcomeEventsFactory.getRepository().getNotCachedUniqueOutcome(s, (List)list);
        if (notCachedUniqueOutcome.size() <= 0) {
            notCachedUniqueOutcome = null;
        }
        return (List<OSInfluence>)notCachedUniqueOutcome;
    }
    
    private void initUniqueOutcomeEventsSentSets() {
        this.unattributedUniqueOutcomeEventsSentOnSession = OSUtils.newConcurrentSet();
        final Set unattributedUniqueOutcomeEventsSent = this.outcomeEventsFactory.getRepository().getUnattributedUniqueOutcomeEventsSent();
        if (unattributedUniqueOutcomeEventsSent != null) {
            this.unattributedUniqueOutcomeEventsSentOnSession = (Set<String>)unattributedUniqueOutcomeEventsSent;
        }
    }
    
    private List<OSInfluence> removeDisabledInfluences(final List<OSInfluence> list) {
        final ArrayList list2 = new ArrayList((Collection)list);
        for (final OSInfluence osInfluence : list) {
            if (osInfluence.getInfluenceType().isDisabled()) {
                final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
                final StringBuilder sb = new StringBuilder("Outcomes disabled for channel: ");
                sb.append(osInfluence.getInfluenceChannel().toString());
                OneSignal.onesignalLog(debug, sb.toString());
                ((List)list2).remove((Object)osInfluence);
            }
        }
        return (List<OSInfluence>)list2;
    }
    
    private void saveAttributedUniqueOutcomeNotifications(final OSOutcomeEventParams osOutcomeEventParams) {
        new Thread((Runnable)new Runnable(this, osOutcomeEventParams) {
            final OSOutcomeEventsController this$0;
            final OSOutcomeEventParams val$eventParams;
            
            public void run() {
                Thread.currentThread().setPriority(10);
                this.this$0.outcomeEventsFactory.getRepository().saveUniqueOutcomeNotifications(this.val$eventParams);
            }
        }, "OS_SAVE_UNIQUE_OUTCOME_NOTIFICATIONS").start();
    }
    
    private void saveUnattributedUniqueOutcomeEvents() {
        this.outcomeEventsFactory.getRepository().saveUnattributedUniqueOutcomeEventsSent((Set)this.unattributedUniqueOutcomeEventsSentOnSession);
    }
    
    private void saveUniqueOutcome(final OSOutcomeEventParams osOutcomeEventParams) {
        if (osOutcomeEventParams.isUnattributed()) {
            this.saveUnattributedUniqueOutcomeEvents();
        }
        else {
            this.saveAttributedUniqueOutcomeNotifications(osOutcomeEventParams);
        }
    }
    
    private void sendAndCreateOutcomeEvent(final String s, final float n, final List<OSInfluence> list, final OneSignal.OutcomeCallback outcomeCallback) {
        final long n2 = OneSignal.getTime().getCurrentTimeMillis() / 1000L;
        final int deviceType = new OSUtils().getDeviceType();
        final String appId = OneSignal.appId;
        final Iterator iterator = list.iterator();
        boolean b = false;
        OSOutcomeSourceBody setSourceChannelIds = null;
        OSOutcomeSourceBody setSourceChannelIds2 = null;
        while (iterator.hasNext()) {
            final OSInfluence osInfluence = (OSInfluence)iterator.next();
            final int n3 = OSOutcomeEventsController$6.$SwitchMap$com$onesignal$influence$domain$OSInfluenceType[osInfluence.getInfluenceType().ordinal()];
            if (n3 != 1) {
                if (n3 != 2) {
                    if (n3 != 3) {
                        if (n3 != 4) {
                            continue;
                        }
                        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
                        final StringBuilder sb = new StringBuilder("Outcomes disabled for channel: ");
                        sb.append((Object)osInfluence.getInfluenceChannel());
                        OneSignal.Log(verbose, sb.toString());
                        if (outcomeCallback != null) {
                            outcomeCallback.onSuccess(null);
                        }
                        return;
                    }
                    else {
                        b = true;
                    }
                }
                else {
                    OSOutcomeSourceBody osOutcomeSourceBody;
                    if ((osOutcomeSourceBody = setSourceChannelIds2) == null) {
                        osOutcomeSourceBody = new OSOutcomeSourceBody();
                    }
                    setSourceChannelIds2 = this.setSourceChannelIds(osInfluence, osOutcomeSourceBody);
                }
            }
            else {
                OSOutcomeSourceBody osOutcomeSourceBody2;
                if ((osOutcomeSourceBody2 = setSourceChannelIds) == null) {
                    osOutcomeSourceBody2 = new OSOutcomeSourceBody();
                }
                setSourceChannelIds = this.setSourceChannelIds(osInfluence, osOutcomeSourceBody2);
            }
        }
        if (setSourceChannelIds == null && setSourceChannelIds2 == null && !b) {
            OneSignal.Log(OneSignal.LOG_LEVEL.VERBOSE, "Outcomes disabled for all channels");
            if (outcomeCallback != null) {
                outcomeCallback.onSuccess(null);
            }
            return;
        }
        final OSOutcomeEventParams osOutcomeEventParams = new OSOutcomeEventParams(s, new OSOutcomeSource(setSourceChannelIds, setSourceChannelIds2), n, 0L);
        this.outcomeEventsFactory.getRepository().requestMeasureOutcomeEvent(appId, deviceType, osOutcomeEventParams, (OneSignalApiResponseHandler)new OSOutcomeEventsController$4(this, osOutcomeEventParams, outcomeCallback, n2, s));
    }
    
    private void sendSavedOutcomeEvent(final OSOutcomeEventParams osOutcomeEventParams) {
        this.outcomeEventsFactory.getRepository().requestMeasureOutcomeEvent(OneSignal.appId, new OSUtils().getDeviceType(), osOutcomeEventParams, (OneSignalApiResponseHandler)new OSOutcomeEventsController$3(this, osOutcomeEventParams));
    }
    
    private void sendUniqueOutcomeEvent(final String s, final List<OSInfluence> list, final OneSignal.OutcomeCallback outcomeCallback) {
        final List<OSInfluence> removeDisabledInfluences = this.removeDisabledInfluences(list);
        if (removeDisabledInfluences.isEmpty()) {
            OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "Unique Outcome disabled for current session");
            return;
        }
        final Iterator iterator = removeDisabledInfluences.iterator();
        while (true) {
            while (iterator.hasNext()) {
                if (((OSInfluence)iterator.next()).getInfluenceType().isAttributed()) {
                    final boolean b = true;
                    if (b) {
                        final List<OSInfluence> uniqueIds = this.getUniqueIds(s, removeDisabledInfluences);
                        if (uniqueIds == null) {
                            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
                            final StringBuilder sb = new StringBuilder("Measure endpoint will not send because unique outcome already sent for: \nSessionInfluences: ");
                            sb.append(removeDisabledInfluences.toString());
                            sb.append("\nOutcome name: ");
                            sb.append(s);
                            OneSignal.Log(debug, sb.toString());
                            if (outcomeCallback != null) {
                                outcomeCallback.onSuccess(null);
                            }
                            return;
                        }
                        this.sendAndCreateOutcomeEvent(s, 0.0f, uniqueIds, outcomeCallback);
                    }
                    else {
                        if (this.unattributedUniqueOutcomeEventsSentOnSession.contains((Object)s)) {
                            final OneSignal.LOG_LEVEL debug2 = OneSignal.LOG_LEVEL.DEBUG;
                            final StringBuilder sb2 = new StringBuilder("Measure endpoint will not send because unique outcome already sent for: \nSession: ");
                            sb2.append((Object)OSInfluenceType.UNATTRIBUTED);
                            sb2.append("\nOutcome name: ");
                            sb2.append(s);
                            OneSignal.Log(debug2, sb2.toString());
                            if (outcomeCallback != null) {
                                outcomeCallback.onSuccess(null);
                            }
                            return;
                        }
                        this.unattributedUniqueOutcomeEventsSentOnSession.add((Object)s);
                        this.sendAndCreateOutcomeEvent(s, 0.0f, removeDisabledInfluences, outcomeCallback);
                    }
                    return;
                }
            }
            final boolean b = false;
            continue;
        }
    }
    
    private OSOutcomeSourceBody setSourceChannelIds(final OSInfluence osInfluence, final OSOutcomeSourceBody osOutcomeSourceBody) {
        final int n = OSOutcomeEventsController$6.$SwitchMap$com$onesignal$influence$domain$OSInfluenceChannel[osInfluence.getInfluenceChannel().ordinal()];
        if (n != 1) {
            if (n == 2) {
                osOutcomeSourceBody.setNotificationIds(osInfluence.getIds());
            }
        }
        else {
            osOutcomeSourceBody.setInAppMessagesIds(osInfluence.getIds());
        }
        return osOutcomeSourceBody;
    }
    
    void cleanCachedUniqueOutcomes() {
        new Thread((Runnable)new Runnable(this) {
            final OSOutcomeEventsController this$0;
            
            public void run() {
                Thread.currentThread().setPriority(10);
                this.this$0.outcomeEventsFactory.getRepository().cleanCachedUniqueOutcomeEventNotifications("notification", "notification_id");
            }
        }, "OS_DELETE_CACHED_UNIQUE_OUTCOMES_NOTIFICATIONS_THREAD").start();
    }
    
    void cleanOutcomes() {
        OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "OneSignal cleanOutcomes for session");
        this.unattributedUniqueOutcomeEventsSentOnSession = OSUtils.newConcurrentSet();
        this.saveUnattributedUniqueOutcomeEvents();
    }
    
    void sendClickActionOutcomes(final List<OSInAppMessageOutcome> list) {
        for (final OSInAppMessageOutcome osInAppMessageOutcome : list) {
            final String name = osInAppMessageOutcome.getName();
            if (osInAppMessageOutcome.isUnique()) {
                this.sendUniqueOutcomeEvent(name, null);
            }
            else if (osInAppMessageOutcome.getWeight() > 0.0f) {
                this.sendOutcomeEventWithValue(name, osInAppMessageOutcome.getWeight(), null);
            }
            else {
                this.sendOutcomeEvent(name, null);
            }
        }
    }
    
    void sendOutcomeEvent(final String s, final OneSignal.OutcomeCallback outcomeCallback) {
        this.sendAndCreateOutcomeEvent(s, 0.0f, this.osSessionManager.getInfluences(), outcomeCallback);
    }
    
    void sendOutcomeEventWithValue(final String s, final float n, final OneSignal.OutcomeCallback outcomeCallback) {
        this.sendAndCreateOutcomeEvent(s, n, this.osSessionManager.getInfluences(), outcomeCallback);
    }
    
    void sendSavedOutcomes() {
        new Thread((Runnable)new Runnable(this) {
            final OSOutcomeEventsController this$0;
            
            public void run() {
                Thread.currentThread().setPriority(10);
                final Iterator iterator = this.this$0.outcomeEventsFactory.getRepository().getSavedOutcomeEvents().iterator();
                while (iterator.hasNext()) {
                    this.this$0.sendSavedOutcomeEvent((OSOutcomeEventParams)iterator.next());
                }
            }
        }, "OS_SEND_SAVED_OUTCOMES").start();
    }
    
    void sendUniqueOutcomeEvent(final String s, final OneSignal.OutcomeCallback outcomeCallback) {
        this.sendUniqueOutcomeEvent(s, this.osSessionManager.getInfluences(), outcomeCallback);
    }
}
