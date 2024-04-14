package com.onesignal;

import org.json.JSONObject;
import java.util.Iterator;
import com.onesignal.influence.domain.OSInfluence;
import java.util.List;
import com.onesignal.influence.data.OSChannelTracker;
import org.json.JSONArray;
import com.onesignal.influence.domain.OSInfluenceType;
import java.util.ArrayList;
import com.onesignal.influence.data.OSTrackerFactory;

public class OSSessionManager
{
    private static final String OS_END_CURRENT_SESSION = "OS_END_CURRENT_SESSION";
    private OSLogger logger;
    private SessionListener sessionListener;
    protected OSTrackerFactory trackerFactory;
    
    public OSSessionManager(final SessionListener sessionListener, final OSTrackerFactory trackerFactory, final OSLogger logger) {
        this.sessionListener = sessionListener;
        this.trackerFactory = trackerFactory;
        this.logger = logger;
    }
    
    private void attemptSessionUpgrade(final OneSignal.AppEntryAction appEntryAction, final String s) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager attemptSessionUpgrade with entryAction: ");
        sb.append((Object)appEntryAction);
        logger.debug(sb.toString());
        final OSChannelTracker channelByEntryAction = this.trackerFactory.getChannelByEntryAction(appEntryAction);
        final List channelsToResetByEntryAction = this.trackerFactory.getChannelsToResetByEntryAction(appEntryAction);
        final ArrayList list = new ArrayList();
        boolean setSession;
        OSInfluence osInfluence;
        if (channelByEntryAction != null) {
            final OSInfluence currentSessionInfluence = channelByEntryAction.getCurrentSessionInfluence();
            final OSInfluenceType direct = OSInfluenceType.DIRECT;
            String directId;
            if ((directId = s) == null) {
                directId = channelByEntryAction.getDirectId();
            }
            setSession = this.setSession(channelByEntryAction, direct, directId, null);
            osInfluence = currentSessionInfluence;
        }
        else {
            setSession = false;
            osInfluence = null;
        }
        if (setSession) {
            final OSLogger logger2 = this.logger;
            final StringBuilder sb2 = new StringBuilder("OneSignal SessionManager attemptSessionUpgrade channel updated, search for ending direct influences on channels: ");
            sb2.append((Object)channelsToResetByEntryAction);
            logger2.debug(sb2.toString());
            ((List)list).add((Object)osInfluence);
            for (final OSChannelTracker osChannelTracker : channelsToResetByEntryAction) {
                if (osChannelTracker.getInfluenceType().isDirect()) {
                    ((List)list).add((Object)osChannelTracker.getCurrentSessionInfluence());
                    osChannelTracker.resetAndInitInfluence();
                }
            }
        }
        this.logger.debug("OneSignal SessionManager attemptSessionUpgrade try UNATTRIBUTED to INDIRECT upgrade");
        for (final OSChannelTracker osChannelTracker2 : channelsToResetByEntryAction) {
            if (osChannelTracker2.getInfluenceType().isUnattributed()) {
                final JSONArray lastReceivedIds = osChannelTracker2.getLastReceivedIds();
                if (lastReceivedIds.length() <= 0 || appEntryAction.isAppClose()) {
                    continue;
                }
                final OSInfluence currentSessionInfluence2 = osChannelTracker2.getCurrentSessionInfluence();
                if (!this.setSession(osChannelTracker2, OSInfluenceType.INDIRECT, null, lastReceivedIds)) {
                    continue;
                }
                ((List)list).add((Object)currentSessionInfluence2);
            }
        }
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb3 = new StringBuilder("Trackers after update attempt: ");
        sb3.append(this.trackerFactory.getChannels().toString());
        OneSignal.Log(debug, sb3.toString());
        this.sendSessionEndingWithInfluences((List<OSInfluence>)list);
    }
    
    private void sendSessionEndingWithInfluences(final List<OSInfluence> list) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager sendSessionEndingWithInfluences with influences: ");
        sb.append((Object)list);
        logger.debug(sb.toString());
        if (list.size() > 0) {
            new Thread((Runnable)new Runnable(this, list) {
                final OSSessionManager this$0;
                final List val$endingInfluences;
                
                public void run() {
                    Thread.currentThread().setPriority(10);
                    this.this$0.sessionListener.onSessionEnding((List<OSInfluence>)this.val$endingInfluences);
                }
            }, "OS_END_CURRENT_SESSION").start();
        }
    }
    
    private boolean setSession(final OSChannelTracker osChannelTracker, final OSInfluenceType influenceType, final String directId, final JSONArray indirectIds) {
        if (!this.willChangeSession(osChannelTracker, influenceType, directId, indirectIds)) {
            return false;
        }
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OSChannelTracker changed: ");
        sb.append(osChannelTracker.getIdTag());
        sb.append("\nfrom:\ninfluenceType: ");
        sb.append((Object)osChannelTracker.getInfluenceType());
        sb.append(", directNotificationId: ");
        sb.append(osChannelTracker.getDirectId());
        sb.append(", indirectNotificationIds: ");
        sb.append((Object)osChannelTracker.getIndirectIds());
        sb.append("\nto:\ninfluenceType: ");
        sb.append((Object)influenceType);
        sb.append(", directNotificationId: ");
        sb.append(directId);
        sb.append(", indirectNotificationIds: ");
        sb.append((Object)indirectIds);
        OneSignal.Log(debug, sb.toString());
        osChannelTracker.setInfluenceType(influenceType);
        osChannelTracker.setDirectId(directId);
        osChannelTracker.setIndirectIds(indirectIds);
        osChannelTracker.cacheState();
        final OneSignal.LOG_LEVEL debug2 = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb2 = new StringBuilder("Trackers changed to: ");
        sb2.append(this.trackerFactory.getChannels().toString());
        OneSignal.Log(debug2, sb2.toString());
        return true;
    }
    
    private boolean willChangeSession(final OSChannelTracker osChannelTracker, OSInfluenceType influenceType, final String s, final JSONArray jsonArray) {
        final boolean equals = influenceType.equals((Object)osChannelTracker.getInfluenceType());
        boolean b = true;
        if (!equals) {
            return true;
        }
        influenceType = osChannelTracker.getInfluenceType();
        if (influenceType.isDirect() && osChannelTracker.getDirectId() != null && !osChannelTracker.getDirectId().equals((Object)s)) {
            return true;
        }
        if (!influenceType.isIndirect() || osChannelTracker.getIndirectIds() == null || osChannelTracker.getIndirectIds().length() <= 0 || JSONUtils.compareJSONArrays(osChannelTracker.getIndirectIds(), jsonArray)) {
            b = false;
        }
        return b;
    }
    
    void addSessionIds(final JSONObject jsonObject, final List<OSInfluence> list) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager addSessionData with influences: ");
        sb.append(list.toString());
        logger.debug(sb.toString());
        this.trackerFactory.addSessionData(jsonObject, (List)list);
        final OSLogger logger2 = this.logger;
        final StringBuilder sb2 = new StringBuilder("OneSignal SessionManager addSessionIds on jsonObject: ");
        sb2.append((Object)jsonObject);
        logger2.debug(sb2.toString());
    }
    
    void attemptSessionUpgrade(final OneSignal.AppEntryAction appEntryAction) {
        this.attemptSessionUpgrade(appEntryAction, null);
    }
    
    List<OSInfluence> getInfluences() {
        return (List<OSInfluence>)this.trackerFactory.getInfluences();
    }
    
    List<OSInfluence> getSessionInfluences() {
        return (List<OSInfluence>)this.trackerFactory.getSessionInfluences();
    }
    
    void initSessionFromCache() {
        this.trackerFactory.initFromCache();
    }
    
    void onDirectInfluenceFromIAMClick(final String s) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager onDirectInfluenceFromIAMClick messageId: ");
        sb.append(s);
        logger.debug(sb.toString());
        this.setSession(this.trackerFactory.getIAMChannelTracker(), OSInfluenceType.DIRECT, s, null);
    }
    
    void onDirectInfluenceFromIAMClickFinished() {
        this.logger.debug("OneSignal SessionManager onDirectInfluenceFromIAMClickFinished");
        this.trackerFactory.getIAMChannelTracker().resetAndInitInfluence();
    }
    
    void onDirectInfluenceFromNotificationOpen(final OneSignal.AppEntryAction appEntryAction, final String s) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager onDirectInfluenceFromNotificationOpen notificationId: ");
        sb.append(s);
        logger.debug(sb.toString());
        if (s != null) {
            if (!s.isEmpty()) {
                this.attemptSessionUpgrade(appEntryAction, s);
            }
        }
    }
    
    void onInAppMessageReceived(final String s) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager onInAppMessageReceived messageId: ");
        sb.append(s);
        logger.debug(sb.toString());
        final OSChannelTracker iamChannelTracker = this.trackerFactory.getIAMChannelTracker();
        iamChannelTracker.saveLastId(s);
        iamChannelTracker.resetAndInitInfluence();
    }
    
    void onNotificationReceived(final String s) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager onNotificationReceived notificationId: ");
        sb.append(s);
        logger.debug(sb.toString());
        if (s != null) {
            if (!s.isEmpty()) {
                this.trackerFactory.getNotificationChannelTracker().saveLastId(s);
            }
        }
    }
    
    void restartSessionIfNeeded(final OneSignal.AppEntryAction appEntryAction) {
        final List channelsToResetByEntryAction = this.trackerFactory.getChannelsToResetByEntryAction(appEntryAction);
        final ArrayList list = new ArrayList();
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal SessionManager restartSessionIfNeeded with entryAction: ");
        sb.append((Object)appEntryAction);
        sb.append("\n channelTrackers: ");
        sb.append(channelsToResetByEntryAction.toString());
        logger.debug(sb.toString());
        for (final OSChannelTracker osChannelTracker : channelsToResetByEntryAction) {
            final JSONArray lastReceivedIds = osChannelTracker.getLastReceivedIds();
            final OSLogger logger2 = this.logger;
            final StringBuilder sb2 = new StringBuilder("OneSignal SessionManager restartSessionIfNeeded lastIds: ");
            sb2.append((Object)lastReceivedIds);
            logger2.debug(sb2.toString());
            final OSInfluence currentSessionInfluence = osChannelTracker.getCurrentSessionInfluence();
            boolean b;
            if (lastReceivedIds.length() > 0) {
                b = this.setSession(osChannelTracker, OSInfluenceType.INDIRECT, null, lastReceivedIds);
            }
            else {
                b = this.setSession(osChannelTracker, OSInfluenceType.UNATTRIBUTED, null, null);
            }
            if (b) {
                ((List)list).add((Object)currentSessionInfluence);
            }
        }
        this.sendSessionEndingWithInfluences((List<OSInfluence>)list);
    }
    
    public interface SessionListener
    {
        void onSessionEnding(final List<OSInfluence> p0);
    }
}
