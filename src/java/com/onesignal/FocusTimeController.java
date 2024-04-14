package com.onesignal;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.atomic.AtomicBoolean;
import com.onesignal.influence.domain.OSInfluence;
import java.util.List;

class FocusTimeController
{
    private OSLogger logger;
    private OSFocusTimeProcessorFactory processorFactory;
    private Long timeFocusedAtMs;
    
    FocusTimeController(final OSFocusTimeProcessorFactory processorFactory, final OSLogger logger) {
        this.processorFactory = processorFactory;
        this.logger = logger;
    }
    
    private Long getTimeFocusedElapsed() {
        if (this.timeFocusedAtMs == null) {
            return null;
        }
        final long n = (long)((OneSignal.getTime().getElapsedRealtime() - this.timeFocusedAtMs) / 1000.0 + 0.5);
        if (n >= 1L && n <= 86400L) {
            return n;
        }
        return null;
    }
    
    private boolean giveProcessorsValidFocusTime(final List<OSInfluence> list, final FocusEventType focusEventType) {
        final Long timeFocusedElapsed = this.getTimeFocusedElapsed();
        if (timeFocusedElapsed == null) {
            return false;
        }
        this.processorFactory.getTimeProcessorWithInfluences(list).addTime(timeFocusedElapsed, list, focusEventType);
        return true;
    }
    
    void appBackgrounded() {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("Application backgrounded focus time: ");
        sb.append((Object)this.timeFocusedAtMs);
        logger.debug(sb.toString());
        this.processorFactory.getTimeProcessorSaved().sendUnsentTimeNow();
        this.timeFocusedAtMs = null;
    }
    
    void appForegrounded() {
        this.timeFocusedAtMs = OneSignal.getTime().getElapsedRealtime();
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("Application foregrounded focus time: ");
        sb.append((Object)this.timeFocusedAtMs);
        logger.debug(sb.toString());
    }
    
    void appStopped() {
        final Long timeFocusedElapsed = this.getTimeFocusedElapsed();
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("Application stopped focus time: ");
        sb.append((Object)this.timeFocusedAtMs);
        sb.append(" timeElapsed: ");
        sb.append((Object)timeFocusedElapsed);
        logger.debug(sb.toString());
        if (timeFocusedElapsed == null) {
            return;
        }
        final List<OSInfluence> sessionInfluences = OneSignal.getSessionManager().getSessionInfluences();
        this.processorFactory.getTimeProcessorWithInfluences(sessionInfluences).saveUnsentActiveData(timeFocusedElapsed, sessionInfluences);
    }
    
    void doBlockingBackgroundSyncOfUnsentTime() {
        if (OneSignal.isInForeground()) {
            return;
        }
        this.processorFactory.getTimeProcessorSaved().syncUnsentTimeFromSyncJob();
    }
    
    void onSessionEnded(final List<OSInfluence> list) {
        final FocusEventType end_SESSION = FocusEventType.END_SESSION;
        if (!this.giveProcessorsValidFocusTime(list, end_SESSION)) {
            this.processorFactory.getTimeProcessorWithInfluences(list).sendUnsentTimeNow(end_SESSION);
        }
    }
    
    private enum FocusEventType
    {
        private static final FocusEventType[] $VALUES;
        
        BACKGROUND, 
        END_SESSION;
    }
    
    abstract static class FocusTimeProcessorBase
    {
        protected long MIN_ON_FOCUS_TIME_SEC;
        protected String PREF_KEY_FOR_UNSENT_TIME;
        private final AtomicBoolean runningOnFocusTime;
        private Long unsentActiveTime;
        
        FocusTimeProcessorBase() {
            this.unsentActiveTime = null;
            this.runningOnFocusTime = new AtomicBoolean();
        }
        
        private void addTime(final long n, final List<OSInfluence> list, final FocusEventType focusEventType) {
            this.saveUnsentActiveData(n, list);
            this.sendUnsentTimeNow(focusEventType);
        }
        
        private JSONObject generateOnFocusPayload(final long n) throws JSONException {
            final JSONObject put = new JSONObject().put("app_id", (Object)OneSignal.getSavedAppId()).put("type", 1).put("state", (Object)"ping").put("active_time", n).put("device_type", new OSUtils().getDeviceType());
            OneSignal.addNetType(put);
            return put;
        }
        
        private long getUnsentActiveTime() {
            if (this.unsentActiveTime == null) {
                this.unsentActiveTime = OneSignalPrefs.getLong(OneSignalPrefs.PREFS_ONESIGNAL, this.PREF_KEY_FOR_UNSENT_TIME, 0L);
            }
            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append(":getUnsentActiveTime: ");
            sb.append((Object)this.unsentActiveTime);
            OneSignal.Log(debug, sb.toString());
            return this.unsentActiveTime;
        }
        
        private boolean hasMinSyncTime() {
            return this.getUnsentActiveTime() >= this.MIN_ON_FOCUS_TIME_SEC;
        }
        
        private void saveUnsentActiveData(final long n, final List<OSInfluence> list) {
            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append(":saveUnsentActiveData with lastFocusTimeInfluences: ");
            sb.append(list.toString());
            OneSignal.Log(debug, sb.toString());
            final long unsentActiveTime = this.getUnsentActiveTime();
            this.saveInfluences(list);
            this.saveUnsentActiveTime(unsentActiveTime + n);
        }
        
        private void saveUnsentActiveTime(final long n) {
            this.unsentActiveTime = n;
            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append(":saveUnsentActiveTime: ");
            sb.append((Object)this.unsentActiveTime);
            OneSignal.Log(debug, sb.toString());
            OneSignalPrefs.saveLong(OneSignalPrefs.PREFS_ONESIGNAL, this.PREF_KEY_FOR_UNSENT_TIME, n);
        }
        
        private void sendOnFocus(final long n) {
            try {
                final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.getClass().getSimpleName());
                sb.append(":sendOnFocus with totalTimeActive: ");
                sb.append(n);
                OneSignal.Log(debug, sb.toString());
                final JSONObject generateOnFocusPayload = this.generateOnFocusPayload(n);
                this.additionalFieldsToAddToOnFocusPayload(generateOnFocusPayload);
                this.sendOnFocusToPlayer(OneSignal.getUserId(), generateOnFocusPayload);
                if (OneSignal.hasEmailId()) {
                    this.sendOnFocusToPlayer(OneSignal.getEmailId(), this.generateOnFocusPayload(n));
                }
                if (OneSignal.hasSMSlId()) {
                    this.sendOnFocusToPlayer(OneSignal.getSMSId(), this.generateOnFocusPayload(n));
                }
                this.saveInfluences((List<OSInfluence>)new ArrayList());
            }
            catch (final JSONException ex) {
                OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Generating on_focus:JSON Failed.", (Throwable)ex);
            }
        }
        
        private void sendOnFocusToPlayer(final String s, final JSONObject jsonObject) {
            final FocusTimeController$FocusTimeProcessorBase$1 focusTimeController$FocusTimeProcessorBase$1 = new FocusTimeController$FocusTimeProcessorBase$1(this);
            final StringBuilder sb = new StringBuilder("players/");
            sb.append(s);
            sb.append("/on_focus");
            OneSignalRestClient.postSync(sb.toString(), jsonObject, (OneSignalRestClient.ResponseHandler)focusTimeController$FocusTimeProcessorBase$1);
        }
        
        private void sendUnsentTimeNow() {
            final List<OSInfluence> influences = this.getInfluences();
            final long unsentActiveTime = this.getUnsentActiveTime();
            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append(":sendUnsentTimeNow with time: ");
            sb.append(unsentActiveTime);
            sb.append(" and influences: ");
            sb.append(influences.toString());
            OneSignal.Log(debug, sb.toString());
            this.sendUnsentTimeNow(FocusEventType.BACKGROUND);
        }
        
        private void sendUnsentTimeNow(final FocusEventType focusEventType) {
            if (!OneSignal.hasUserId()) {
                final OneSignal.LOG_LEVEL warn = OneSignal.LOG_LEVEL.WARN;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.getClass().getSimpleName());
                sb.append(":sendUnsentTimeNow not possible due to user id null");
                OneSignal.Log(warn, sb.toString());
                return;
            }
            this.sendTime(focusEventType);
        }
        
        private void syncUnsentTimeFromSyncJob() {
            if (this.hasMinSyncTime()) {
                this.syncOnFocusTime();
            }
        }
        
        protected void additionalFieldsToAddToOnFocusPayload(final JSONObject jsonObject) {
        }
        
        protected abstract List<OSInfluence> getInfluences();
        
        protected abstract void saveInfluences(final List<OSInfluence> p0);
        
        protected abstract void sendTime(final FocusEventType p0);
        
        protected void syncOnFocusTime() {
            if (this.runningOnFocusTime.get()) {
                return;
            }
            final AtomicBoolean runningOnFocusTime = this.runningOnFocusTime;
            synchronized (runningOnFocusTime) {
                this.runningOnFocusTime.set(true);
                if (this.hasMinSyncTime()) {
                    this.sendOnFocus(this.getUnsentActiveTime());
                }
                this.runningOnFocusTime.set(false);
            }
        }
        
        protected void syncUnsentTimeOnBackgroundEvent() {
            if (!this.hasMinSyncTime()) {
                return;
            }
            OSSyncService.getInstance().scheduleSyncTask(OneSignal.appContext);
        }
    }
}
