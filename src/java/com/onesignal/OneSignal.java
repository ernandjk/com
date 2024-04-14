package com.onesignal;

import android.app.Application;
import com.onesignal.language.LanguageProvider;
import com.onesignal.language.LanguageProviderAppDefined;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.Build;
import android.content.Intent;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import android.os.Build$VERSION;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONException;
import android.app.AlertDialog$Builder;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import android.util.Log;
import org.json.JSONArray;
import java.util.Collection;
import com.onesignal.influence.data.OSTrackerFactory;
import java.util.HashSet;
import java.util.ArrayList;
import com.onesignal.outcomes.data.OSOutcomeEventsFactory;
import com.onesignal.language.LanguageContext;
import java.util.List;
import android.content.Context;
import android.app.Activity;
import java.lang.ref.WeakReference;

public class OneSignal
{
    static final long MIN_ON_SESSION_TIME_MILLIS = 30000L;
    private static final String VERSION = "040805";
    private static boolean androidParamsRequestStarted;
    private static OneSignalAPIClient apiClient;
    static WeakReference<Activity> appActivity;
    static Context appContext;
    private static AppEntryAction appEntryState;
    static String appId;
    private static OSEmailSubscriptionState currentEmailSubscriptionState;
    private static OSPermissionState currentPermissionState;
    private static OSSMSSubscriptionState currentSMSSubscriptionState;
    private static OSSubscriptionState currentSubscriptionState;
    private static DelayedConsentInitializationParameters delayedInitParams;
    private static String emailId;
    private static EmailUpdateHandler emailLogoutHandler;
    private static OSObservable<OSEmailSubscriptionObserver, OSEmailSubscriptionStateChanges> emailSubscriptionStateChangesObserver;
    private static EmailUpdateHandler emailUpdateHandler;
    private static List<EntryStateListener> entryStateListeners;
    private static FocusTimeController focusTimeController;
    private static boolean getTagsCall;
    static String googleProjectNumber;
    private static IAPUpdateJob iapUpdateJob;
    static OSInAppMessageClickHandler inAppMessageClickHandler;
    private static OSInAppMessageControllerFactory inAppMessageControllerFactory;
    private static boolean inForeground;
    private static boolean initDone;
    static LanguageContext languageContext;
    static OSEmailSubscriptionState lastEmailSubscriptionState;
    private static LocationController.LocationPoint lastLocationPoint;
    static OSPermissionState lastPermissionState;
    private static String lastRegistrationId;
    static OSSMSSubscriptionState lastSMSSubscriptionState;
    static OSSubscriptionState lastSubscriptionState;
    private static boolean locationFired;
    private static LOG_LEVEL logCatLevel;
    private static OSLogger logger;
    private static PushRegistrator mPushRegistrator;
    private static OSNotificationDataController notificationDataController;
    static OSNotificationOpenedHandler notificationOpenedHandler;
    static OSNotificationWillShowInForegroundHandler notificationWillShowInForegroundHandler;
    private static OSUtils osUtils;
    private static OSOutcomeEventsController outcomeEventsController;
    private static final Object outcomeEventsControllerSyncLock;
    private static OSOutcomeEventsFactory outcomeEventsFactory;
    private static final ArrayList<OSGetTagsHandler> pendingGetTagsHandlers;
    private static OSObservable<OSPermissionObserver, OSPermissionStateChanges> permissionStateChangesObserver;
    private static HashSet<String> postedOpenedNotifIds;
    private static OSSharedPreferences preferences;
    private static boolean registerForPushFired;
    static OSRemoteNotificationReceivedHandler remoteNotificationReceivedHandler;
    private static OSRemoteParamController remoteParamController;
    public static String sdkType;
    private static OSSessionManager.SessionListener sessionListener;
    private static OSSessionManager sessionManager;
    private static String smsId;
    private static OSSMSUpdateHandler smsLogoutHandler;
    private static OSObservable<OSSMSSubscriptionObserver, OSSMSSubscriptionStateChanges> smsSubscriptionStateChangesObserver;
    private static OSSMSUpdateHandler smsUpdateHandler;
    private static int subscribableStatus;
    private static OSObservable<OSSubscriptionObserver, OSSubscriptionStateChanges> subscriptionStateChangesObserver;
    private static OSTaskController taskController;
    private static OSTaskRemoteController taskRemoteController;
    private static OSTime time;
    private static TrackAmazonPurchase trackAmazonPurchase;
    private static TrackFirebaseAnalytics trackFirebaseAnalytics;
    private static TrackGooglePurchase trackGooglePurchase;
    private static OSTrackerFactory trackerFactory;
    private static Collection<JSONArray> unprocessedOpenedNotifs;
    private static String userId;
    private static LOG_LEVEL visualLogLevel;
    private static boolean waitingToPostStateSync;
    
    static {
        OneSignal.entryStateListeners = (List<EntryStateListener>)new ArrayList();
        OneSignal.visualLogLevel = LOG_LEVEL.NONE;
        OneSignal.logCatLevel = LOG_LEVEL.WARN;
        OneSignal.userId = null;
        OneSignal.emailId = null;
        OneSignal.smsId = null;
        OneSignal.subscribableStatus = Integer.MAX_VALUE;
        OneSignal.languageContext = null;
        OneSignal.appEntryState = AppEntryAction.APP_CLOSE;
        OneSignal.logger = (OSLogger)new OSLogWrapper();
        OneSignal.sessionListener = (OSSessionManager.SessionListener)new OneSignal$1();
        OneSignal.inAppMessageControllerFactory = new OSInAppMessageControllerFactory();
        OneSignal.time = (OSTime)new OSTimeImpl();
        OneSignal.remoteParamController = new OSRemoteParamController();
        OneSignal.taskController = new OSTaskController(OneSignal.logger);
        OneSignal.taskRemoteController = new OSTaskRemoteController(OneSignal.remoteParamController, OneSignal.logger);
        OneSignal.apiClient = (OneSignalAPIClient)new OneSignalRestClientWrapper();
        OneSignal.sessionManager = new OSSessionManager(OneSignal.sessionListener, OneSignal.trackerFactory = new OSTrackerFactory(OneSignal.preferences = (OSSharedPreferences)new OSSharedPreferencesWrapper(), OneSignal.logger, OneSignal.time), OneSignal.logger);
        outcomeEventsControllerSyncLock = new Object() {};
        OneSignal.sdkType = "native";
        OneSignal.osUtils = new OSUtils();
        OneSignal.unprocessedOpenedNotifs = (Collection<JSONArray>)new ArrayList();
        OneSignal.postedOpenedNotifIds = (HashSet<String>)new HashSet();
        pendingGetTagsHandlers = new ArrayList();
    }
    
    static void Log(final LOG_LEVEL log_LEVEL, final String s) {
        Log(log_LEVEL, s, null);
    }
    
    static void Log(final LOG_LEVEL log_LEVEL, String s, final Throwable t) {
        if (log_LEVEL.compareTo((Enum)OneSignal.logCatLevel) < 1) {
            if (log_LEVEL == LOG_LEVEL.VERBOSE) {
                Log.v("OneSignal", s, t);
            }
            else if (log_LEVEL == LOG_LEVEL.DEBUG) {
                Log.d("OneSignal", s, t);
            }
            else if (log_LEVEL == LOG_LEVEL.INFO) {
                Log.i("OneSignal", s, t);
            }
            else if (log_LEVEL == LOG_LEVEL.WARN) {
                Log.w("OneSignal", s, t);
            }
            else if (log_LEVEL == LOG_LEVEL.ERROR || log_LEVEL == LOG_LEVEL.FATAL) {
                Log.e("OneSignal", s, t);
            }
        }
        if (log_LEVEL.compareTo((Enum)OneSignal.visualLogLevel) < 1 && getCurrentActivity() != null) {
            try {
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append("\n");
                final String s2 = s = sb.toString();
                if (t != null) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(s2);
                    sb2.append(t.getMessage());
                    s = sb2.toString();
                    final StringWriter stringWriter = new StringWriter();
                    t.printStackTrace(new PrintWriter((Writer)stringWriter));
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append(s);
                    sb3.append(stringWriter.toString());
                    s = sb3.toString();
                }
                OSUtils.runOnMainUIThread((Runnable)new Runnable(log_LEVEL, s) {
                    final String val$finalFullMessage;
                    final LOG_LEVEL val$level;
                    
                    public void run() {
                        if (OneSignal.getCurrentActivity() != null) {
                            new AlertDialog$Builder((Context)OneSignal.getCurrentActivity()).setTitle((CharSequence)this.val$level.toString()).setMessage((CharSequence)this.val$finalFullMessage).show();
                        }
                    }
                });
            }
            finally {
                final Throwable t2;
                Log.e("OneSignal", "Error showing logging message.", t2);
            }
        }
    }
    
    public static void addEmailSubscriptionObserver(final OSEmailSubscriptionObserver osEmailSubscriptionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not add email subscription observer");
            return;
        }
        getEmailSubscriptionStateChangesObserver().addObserver(osEmailSubscriptionObserver);
        if (getCurrentEmailSubscriptionState(OneSignal.appContext).compare(getLastEmailSubscriptionState(OneSignal.appContext))) {
            OSEmailSubscriptionChangedInternalObserver.fireChangesToPublicObserver(getCurrentEmailSubscriptionState(OneSignal.appContext));
        }
    }
    
    static void addEntryStateListener(final EntryStateListener entryStateListener, final AppEntryAction appEntryAction) {
        if (!appEntryAction.equals((Object)AppEntryAction.NOTIFICATION_CLICK)) {
            OneSignal.entryStateListeners.add((Object)entryStateListener);
        }
    }
    
    static void addNetType(final JSONObject jsonObject) {
        try {
            jsonObject.put("net_type", (Object)OneSignal.osUtils.getNetType());
        }
        finally {}
    }
    
    public static void addPermissionObserver(final OSPermissionObserver osPermissionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not add permission observer");
            return;
        }
        getPermissionStateChangesObserver().addObserver(osPermissionObserver);
        if (getCurrentPermissionState(OneSignal.appContext).compare(getLastPermissionState(OneSignal.appContext))) {
            OSPermissionChangedInternalObserver.fireChangesToPublicObserver(getCurrentPermissionState(OneSignal.appContext));
        }
    }
    
    public static void addSMSSubscriptionObserver(final OSSMSSubscriptionObserver ossmsSubscriptionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not add sms subscription observer");
            return;
        }
        getSMSSubscriptionStateChangesObserver().addObserver(ossmsSubscriptionObserver);
        if (getCurrentSMSSubscriptionState(OneSignal.appContext).compare(getLastSMSSubscriptionState(OneSignal.appContext))) {
            OSSMSSubscriptionChangedInternalObserver.fireChangesToPublicObserver(getCurrentSMSSubscriptionState(OneSignal.appContext));
        }
    }
    
    public static void addSubscriptionObserver(final OSSubscriptionObserver osSubscriptionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not add subscription observer");
            return;
        }
        getSubscriptionStateChangesObserver().addObserver(osSubscriptionObserver);
        if (getCurrentSubscriptionState(OneSignal.appContext).compare(getLastSubscriptionState(OneSignal.appContext))) {
            OSSubscriptionChangedInternalObserver.fireChangesToPublicObserver(getCurrentSubscriptionState(OneSignal.appContext));
        }
    }
    
    public static void addTrigger(final String s, final Object o) {
        final HashMap hashMap = new HashMap();
        hashMap.put((Object)s, o);
        getInAppMessageController().addTriggers((Map)hashMap);
    }
    
    public static void addTriggers(final Map<String, Object> map) {
        getInAppMessageController().addTriggers((Map)map);
    }
    
    static void applicationOpenedByNotification(final String s) {
        OneSignal.sessionManager.onDirectInfluenceFromNotificationOpen(OneSignal.appEntryState = AppEntryAction.NOTIFICATION_CLICK, s);
    }
    
    static boolean areNotificationsEnabledForSubscribedState() {
        return !OneSignal.remoteParamController.unsubscribeWhenNotificationsAreDisabled() || OSUtils.areNotificationsEnabled(OneSignal.appContext);
    }
    
    static boolean atLogLevel(final LOG_LEVEL log_LEVEL) {
        final int compareTo = log_LEVEL.compareTo((Enum)OneSignal.visualLogLevel);
        boolean b = true;
        if (compareTo >= 1) {
            b = (log_LEVEL.compareTo((Enum)OneSignal.logCatLevel) < 1 && b);
        }
        return b;
    }
    
    static void backgroundSyncLogic() {
        if (OneSignal.inForeground) {
            return;
        }
        final TrackAmazonPurchase trackAmazonPurchase = OneSignal.trackAmazonPurchase;
        if (trackAmazonPurchase != null) {
            trackAmazonPurchase.checkListener();
        }
        getFocusTimeController().appBackgrounded();
        scheduleSyncService();
    }
    
    static void callEntryStateListeners(final AppEntryAction appEntryAction) {
        final Iterator iterator = ((List)new ArrayList((Collection)OneSignal.entryStateListeners)).iterator();
        while (iterator.hasNext()) {
            ((EntryStateListener)iterator.next()).onEntryStateChange(appEntryAction);
        }
    }
    
    public static void clearOneSignalNotifications() {
        if (!OneSignal.taskRemoteController.shouldQueueTaskForInit("clearOneSignalNotifications()")) {
            final OSNotificationDataController notificationDataController = OneSignal.notificationDataController;
            if (notificationDataController != null) {
                notificationDataController.clearOneSignalNotifications(new WeakReference((Object)OneSignal.appContext));
                return;
            }
        }
        OneSignal.logger.error("Waiting for remote params. Moving clearOneSignalNotifications() operation to a pending queue.");
        OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable() {
            public void run() {
                OneSignal.logger.debug("Running clearOneSignalNotifications() operation from pending queue.");
                OneSignal.clearOneSignalNotifications();
            }
        });
    }
    
    public static void deleteTag(final String s) {
        deleteTag(s, null);
    }
    
    public static void deleteTag(final String s, final ChangeTagsUpdateHandler changeTagsUpdateHandler) {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("deleteTag()")) {
            return;
        }
        final ArrayList list = new ArrayList(1);
        ((Collection)list).add((Object)s);
        deleteTags((Collection<String>)list, changeTagsUpdateHandler);
    }
    
    public static void deleteTags(final String s) {
        deleteTags(s, null);
    }
    
    public static void deleteTags(final String s, final ChangeTagsUpdateHandler changeTagsUpdateHandler) {
        try {
            deleteTags(new JSONArray(s), changeTagsUpdateHandler);
        }
        finally {
            final Throwable t;
            Log(LOG_LEVEL.ERROR, "Failed to generate JSON for deleteTags.", t);
        }
    }
    
    public static void deleteTags(final Collection<String> collection) {
        deleteTags(collection, null);
    }
    
    public static void deleteTags(final Collection<String> collection, final ChangeTagsUpdateHandler changeTagsUpdateHandler) {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("deleteTags()")) {
            return;
        }
        try {
            final JSONObject jsonObject = new JSONObject();
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                jsonObject.put((String)iterator.next(), (Object)"");
            }
            sendTags(jsonObject, changeTagsUpdateHandler);
        }
        finally {
            final Throwable t;
            Log(LOG_LEVEL.ERROR, "Failed to generate JSON for deleteTags.", t);
        }
    }
    
    public static void deleteTags(final JSONArray jsonArray, final ChangeTagsUpdateHandler changeTagsUpdateHandler) {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("deleteTags()")) {
            return;
        }
        try {
            final JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < jsonArray.length(); ++i) {
                jsonObject.put(jsonArray.getString(i), (Object)"");
            }
            sendTags(jsonObject, changeTagsUpdateHandler);
        }
        finally {
            final Throwable t;
            Log(LOG_LEVEL.ERROR, "Failed to generate JSON for deleteTags.", t);
        }
    }
    
    public static void disableGMSMissingPrompt(final boolean b) {
        if (getRemoteParamController().hasDisableGMSMissingPromptKey()) {
            return;
        }
        getRemoteParamController().saveGMSMissingPromptDisable(b);
    }
    
    public static void disablePush(final boolean pushDisabled) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("setSubscription()")) {
            OneSignal.logger.error("Waiting for remote params. Moving setSubscription() operation to a pending queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(pushDisabled) {
                final boolean val$disable;
                
                public void run() {
                    OneSignal.logger.debug("Running setSubscription() operation from pending queue.");
                    OneSignal.disablePush(this.val$disable);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("setSubscription()")) {
            return;
        }
        getCurrentSubscriptionState(OneSignal.appContext).setPushDisabled(pushDisabled);
        OneSignalStateSynchronizer.setSubscription(pushDisabled ^ true);
    }
    
    private static void doSessionInit() {
        if (shouldStartNewSession()) {
            final OSLogger logger = OneSignal.logger;
            final StringBuilder sb = new StringBuilder("Starting new session with appEntryState: ");
            sb.append((Object)getAppEntryState());
            logger.debug(sb.toString());
            OneSignalStateSynchronizer.setNewSession();
            getOutcomeEventsController().cleanOutcomes();
            OneSignal.sessionManager.restartSessionIfNeeded(getAppEntryState());
            getInAppMessageController().resetSessionLaunchTime();
            setLastSessionTime(OneSignal.time.getCurrentTimeMillis());
        }
        else if (isInForeground()) {
            final OSLogger logger2 = OneSignal.logger;
            final StringBuilder sb2 = new StringBuilder("Continue on same session with appEntryState: ");
            sb2.append((Object)getAppEntryState());
            logger2.debug(sb2.toString());
            OneSignal.sessionManager.attemptSessionUpgrade(getAppEntryState());
        }
        getInAppMessageController().initWithCachedInAppMessages();
        if (!OneSignal.inForeground && hasUserId()) {
            OneSignal.logger.debug("doSessionInit on background with already registered user");
        }
        startRegistrationOrOnSession();
    }
    
    private static void fireCallbackForOpenedNotifications() {
        final Iterator iterator = OneSignal.unprocessedOpenedNotifs.iterator();
        while (iterator.hasNext()) {
            runNotificationOpenedCallback((JSONArray)iterator.next());
        }
        OneSignal.unprocessedOpenedNotifs.clear();
    }
    
    static void fireEmailUpdateFailure() {
        final EmailUpdateHandler emailUpdateHandler = OneSignal.emailUpdateHandler;
        if (emailUpdateHandler != null) {
            emailUpdateHandler.onFailure(new EmailUpdateError(EmailErrorType.NETWORK, "Failed due to network failure. Will retry on next sync."));
            OneSignal.emailUpdateHandler = null;
        }
    }
    
    static void fireEmailUpdateSuccess() {
        final EmailUpdateHandler emailUpdateHandler = OneSignal.emailUpdateHandler;
        if (emailUpdateHandler != null) {
            emailUpdateHandler.onSuccess();
            OneSignal.emailUpdateHandler = null;
        }
    }
    
    static void fireForegroundHandlers(final OSNotificationController osNotificationController) {
        onesignalLog(LOG_LEVEL.INFO, "Fire notificationWillShowInForegroundHandler");
        final OSNotificationReceivedEvent notificationReceivedEvent = osNotificationController.getNotificationReceivedEvent();
        try {
            OneSignal.notificationWillShowInForegroundHandler.notificationWillShowInForeground(notificationReceivedEvent);
        }
        finally {
            onesignalLog(LOG_LEVEL.ERROR, "Exception thrown while notification was being processed for display by notificationWillShowInForegroundHandler, showing notification in foreground!");
            notificationReceivedEvent.complete(notificationReceivedEvent.getNotification());
        }
    }
    
    private static void fireNotificationOpenedHandler(final OSNotificationOpenedResult osNotificationOpenedResult) {
        OSUtils.runOnMainUIThread((Runnable)new Runnable(osNotificationOpenedResult) {
            final OSNotificationOpenedResult val$openedResult;
            
            public void run() {
                OneSignal.notificationOpenedHandler.notificationOpened(this.val$openedResult);
            }
        });
    }
    
    static void fireSMSUpdateFailure() {
        final OSSMSUpdateHandler smsUpdateHandler = OneSignal.smsUpdateHandler;
        if (smsUpdateHandler != null) {
            smsUpdateHandler.onFailure(new OSSMSUpdateError(SMSErrorType.NETWORK, "Failed due to network failure. Will retry on next sync."));
            OneSignal.smsUpdateHandler = null;
        }
    }
    
    static void fireSMSUpdateSuccess(final JSONObject jsonObject) {
        final OSSMSUpdateHandler smsUpdateHandler = OneSignal.smsUpdateHandler;
        if (smsUpdateHandler != null) {
            smsUpdateHandler.onSuccess(jsonObject);
            OneSignal.smsUpdateHandler = null;
        }
    }
    
    private static OSNotificationOpenedResult generateNotificationOpenedResult(final JSONArray jsonArray) {
        final int length = jsonArray.length();
        final int optInt = jsonArray.optJSONObject(0).optInt("androidNotificationId");
        final ArrayList list = new ArrayList();
        int n = 1;
        String s = null;
        JSONObject jsonObject = null;
        for (int i = 0; i < length; ++i) {
            String s2 = s;
            try {
                final JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                String optString = s;
                if (s == null) {
                    optString = s;
                    s2 = s;
                    jsonObject = jsonObject2;
                    if (jsonObject2.has("actionId")) {
                        s2 = s;
                        jsonObject = jsonObject2;
                        optString = jsonObject2.optString("actionId", (String)null);
                    }
                }
                if (n != 0) {
                    n = 0;
                    jsonObject = jsonObject2;
                }
                else {
                    s2 = optString;
                    jsonObject = jsonObject2;
                    s2 = optString;
                    jsonObject = jsonObject2;
                    final OSNotification osNotification = new OSNotification(jsonObject2);
                    s2 = optString;
                    jsonObject = jsonObject2;
                    ((List)list).add((Object)osNotification);
                    jsonObject = jsonObject2;
                }
            }
            finally {
                final LOG_LEVEL error = LOG_LEVEL.ERROR;
                final StringBuilder sb = new StringBuilder("Error parsing JSON item ");
                sb.append(i);
                sb.append("/");
                sb.append(length);
                sb.append(" for callback.");
                final Throwable t;
                Log(error, sb.toString(), t);
                s = s2;
            }
        }
        OSNotificationAction.ActionType actionType;
        if (s != null) {
            actionType = OSNotificationAction.ActionType.ActionTaken;
        }
        else {
            actionType = OSNotificationAction.ActionType.Opened;
        }
        return new OSNotificationOpenedResult(new OSNotification((List<OSNotification>)list, jsonObject, optInt), new OSNotificationAction(actionType, s));
    }
    
    static AppEntryAction getAppEntryState() {
        return OneSignal.appEntryState;
    }
    
    static boolean getClearGroupSummaryClick() {
        return OneSignal.remoteParamController.getClearGroupSummaryClick();
    }
    
    static Activity getCurrentActivity() {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
        Activity curActivity;
        if (activityLifecycleHandler != null) {
            curActivity = activityLifecycleHandler.getCurActivity();
        }
        else {
            curActivity = null;
        }
        return curActivity;
    }
    
    private static OSEmailSubscriptionState getCurrentEmailSubscriptionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.currentEmailSubscriptionState == null) {
            (OneSignal.currentEmailSubscriptionState = new OSEmailSubscriptionState(false)).getObservable().addObserverStrong(new OSEmailSubscriptionChangedInternalObserver());
        }
        return OneSignal.currentEmailSubscriptionState;
    }
    
    private static OSPermissionState getCurrentPermissionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.currentPermissionState == null) {
            (OneSignal.currentPermissionState = new OSPermissionState(false)).getObservable().addObserverStrong(new OSPermissionChangedInternalObserver());
        }
        return OneSignal.currentPermissionState;
    }
    
    private static OSSMSSubscriptionState getCurrentSMSSubscriptionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.currentSMSSubscriptionState == null) {
            (OneSignal.currentSMSSubscriptionState = new OSSMSSubscriptionState(false)).getObservable().addObserverStrong(new OSSMSSubscriptionChangedInternalObserver());
        }
        return OneSignal.currentSMSSubscriptionState;
    }
    
    private static OSSubscriptionState getCurrentSubscriptionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.currentSubscriptionState == null) {
            OneSignal.currentSubscriptionState = new OSSubscriptionState(false, getCurrentPermissionState(context).areNotificationsEnabled());
            getCurrentPermissionState(context).getObservable().addObserver(OneSignal.currentSubscriptionState);
            OneSignal.currentSubscriptionState.getObservable().addObserverStrong(new OSSubscriptionChangedInternalObserver());
        }
        return OneSignal.currentSubscriptionState;
    }
    
    static OneSignalDbHelper getDBHelperInstance() {
        return OneSignalDbHelper.getInstance(OneSignal.appContext);
    }
    
    static OneSignalDbHelper getDBHelperInstance(final Context context) {
        return OneSignalDbHelper.getInstance(context);
    }
    
    static DelayedConsentInitializationParameters getDelayedInitParams() {
        return OneSignal.delayedInitParams;
    }
    
    public static OSDeviceState getDeviceState() {
        final Context appContext = OneSignal.appContext;
        if (appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not get OSDeviceState");
            return null;
        }
        return new OSDeviceState(getCurrentSubscriptionState(appContext), getCurrentPermissionState(OneSignal.appContext), getCurrentEmailSubscriptionState(OneSignal.appContext), getCurrentSMSSubscriptionState(OneSignal.appContext));
    }
    
    static boolean getDisableGMSMissingPrompt() {
        return OneSignal.remoteParamController.isGMSMissingPromptDisable();
    }
    
    static String getEmailId() {
        if (OneSignal.emailId == null && OneSignal.appContext != null) {
            OneSignal.emailId = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "OS_EMAIL_ID", null);
        }
        if (TextUtils.isEmpty((CharSequence)OneSignal.emailId)) {
            return null;
        }
        return OneSignal.emailId;
    }
    
    static OSEmailSubscriptionState getEmailSubscriptionState() {
        return getCurrentEmailSubscriptionState(OneSignal.appContext);
    }
    
    static OSObservable<OSEmailSubscriptionObserver, OSEmailSubscriptionStateChanges> getEmailSubscriptionStateChangesObserver() {
        if (OneSignal.emailSubscriptionStateChangesObserver == null) {
            OneSignal.emailSubscriptionStateChangesObserver = new OSObservable<OSEmailSubscriptionObserver, OSEmailSubscriptionStateChanges>("onOSEmailSubscriptionChanged", true);
        }
        return OneSignal.emailSubscriptionStateChangesObserver;
    }
    
    static boolean getFirebaseAnalyticsEnabled() {
        return OneSignal.remoteParamController.getFirebaseAnalyticsEnabled();
    }
    
    static FocusTimeController getFocusTimeController() {
        if (OneSignal.focusTimeController == null) {
            OneSignal.focusTimeController = new FocusTimeController(new OSFocusTimeProcessorFactory(), OneSignal.logger);
        }
        return OneSignal.focusTimeController;
    }
    
    static OSInAppMessageController getInAppMessageController() {
        return OneSignal.inAppMessageControllerFactory.getController(getDBHelperInstance(), OneSignal.taskController, getLogger(), getSharedPreferences(), OneSignal.languageContext);
    }
    
    private static OSEmailSubscriptionState getLastEmailSubscriptionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.lastEmailSubscriptionState == null) {
            OneSignal.lastEmailSubscriptionState = new OSEmailSubscriptionState(true);
        }
        return OneSignal.lastEmailSubscriptionState;
    }
    
    private static OSPermissionState getLastPermissionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.lastPermissionState == null) {
            OneSignal.lastPermissionState = new OSPermissionState(true);
        }
        return OneSignal.lastPermissionState;
    }
    
    private static OSSMSSubscriptionState getLastSMSSubscriptionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.lastSMSSubscriptionState == null) {
            OneSignal.lastSMSSubscriptionState = new OSSMSSubscriptionState(true);
        }
        return OneSignal.lastSMSSubscriptionState;
    }
    
    private static long getLastSessionTime() {
        return OneSignalPrefs.getLong(OneSignalPrefs.PREFS_ONESIGNAL, "OS_LAST_SESSION_TIME", -31000L);
    }
    
    private static OSSubscriptionState getLastSubscriptionState(final Context context) {
        if (context == null) {
            return null;
        }
        if (OneSignal.lastSubscriptionState == null) {
            OneSignal.lastSubscriptionState = new OSSubscriptionState(true, false);
        }
        return OneSignal.lastSubscriptionState;
    }
    
    private static LOG_LEVEL getLogLevel(final int n) {
        switch (n) {
            default: {
                if (n < 0) {
                    return LOG_LEVEL.NONE;
                }
                return LOG_LEVEL.VERBOSE;
            }
            case 6: {
                return LOG_LEVEL.VERBOSE;
            }
            case 5: {
                return LOG_LEVEL.DEBUG;
            }
            case 4: {
                return LOG_LEVEL.INFO;
            }
            case 3: {
                return LOG_LEVEL.WARN;
            }
            case 2: {
                return LOG_LEVEL.ERROR;
            }
            case 1: {
                return LOG_LEVEL.FATAL;
            }
            case 0: {
                return LOG_LEVEL.NONE;
            }
        }
    }
    
    static OSLogger getLogger() {
        return OneSignal.logger;
    }
    
    static String getNotificationIdFromFCMJson(final JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        try {
            final JSONObject jsonObject2 = new JSONObject(jsonObject.getString("custom"));
            if (jsonObject2.has("i")) {
                return jsonObject2.optString("i", (String)null);
            }
            OneSignal.logger.debug("Not a OneSignal formatted FCM message. No 'i' field in custom.");
        }
        catch (final JSONException ex) {
            OneSignal.logger.debug("Not a OneSignal formatted FCM message. No 'custom' field in the JSONObject.");
        }
        return null;
    }
    
    static OSOutcomeEventsController getOutcomeEventsController() {
        if (OneSignal.outcomeEventsController == null) {
            final Object outcomeEventsControllerSyncLock = OneSignal.outcomeEventsControllerSyncLock;
            synchronized (outcomeEventsControllerSyncLock) {
                if (OneSignal.outcomeEventsController == null) {
                    if (OneSignal.outcomeEventsFactory == null) {
                        OneSignal.outcomeEventsFactory = new OSOutcomeEventsFactory(OneSignal.logger, OneSignal.apiClient, (OneSignalDb)getDBHelperInstance(), OneSignal.preferences);
                    }
                    OneSignal.outcomeEventsController = new OSOutcomeEventsController(OneSignal.sessionManager, OneSignal.outcomeEventsFactory);
                }
            }
        }
        return OneSignal.outcomeEventsController;
    }
    
    static OSObservable<OSPermissionObserver, OSPermissionStateChanges> getPermissionStateChangesObserver() {
        if (OneSignal.permissionStateChangesObserver == null) {
            OneSignal.permissionStateChangesObserver = new OSObservable<OSPermissionObserver, OSPermissionStateChanges>("onOSPermissionChanged", true);
        }
        return OneSignal.permissionStateChangesObserver;
    }
    
    private static PushRegistrator getPushRegistrator() {
        final PushRegistrator mPushRegistrator = OneSignal.mPushRegistrator;
        if (mPushRegistrator != null) {
            return mPushRegistrator;
        }
        if (OSUtils.isFireOSDeviceType()) {
            OneSignal.mPushRegistrator = (PushRegistrator)new PushRegistratorADM();
        }
        else if (OSUtils.isAndroidDeviceType()) {
            if (OSUtils.hasFCMLibrary()) {
                OneSignal.mPushRegistrator = (PushRegistrator)getPushRegistratorFCM();
            }
        }
        else {
            OneSignal.mPushRegistrator = (PushRegistrator)new PushRegistratorHMS();
        }
        return OneSignal.mPushRegistrator;
    }
    
    private static PushRegistratorFCM getPushRegistratorFCM() {
        final OneSignalRemoteParams.FCMParams fcmParams = OneSignal.remoteParamController.getRemoteParams().fcmParams;
        Object o;
        if (fcmParams != null) {
            o = new PushRegistratorFCM.Params(fcmParams.projectId, fcmParams.appId, fcmParams.apiKey);
        }
        else {
            o = null;
        }
        return new PushRegistratorFCM(OneSignal.appContext, (PushRegistratorFCM.Params)o);
    }
    
    static OSRemoteParamController getRemoteParamController() {
        return OneSignal.remoteParamController;
    }
    
    static OneSignalRemoteParams.Params getRemoteParams() {
        return OneSignal.remoteParamController.getRemoteParams();
    }
    
    static String getSMSId() {
        if (OneSignal.smsId == null && OneSignal.appContext != null) {
            OneSignal.smsId = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_OS_SMS_ID", null);
        }
        if (TextUtils.isEmpty((CharSequence)OneSignal.smsId)) {
            return null;
        }
        return OneSignal.smsId;
    }
    
    static OSSMSSubscriptionState getSMSSubscriptionState() {
        return getCurrentSMSSubscriptionState(OneSignal.appContext);
    }
    
    static OSObservable<OSSMSSubscriptionObserver, OSSMSSubscriptionStateChanges> getSMSSubscriptionStateChangesObserver() {
        if (OneSignal.smsSubscriptionStateChangesObserver == null) {
            OneSignal.smsSubscriptionStateChangesObserver = new OSObservable<OSSMSSubscriptionObserver, OSSMSSubscriptionStateChanges>("onSMSSubscriptionChanged", true);
        }
        return OneSignal.smsSubscriptionStateChangesObserver;
    }
    
    static String getSavedAppId() {
        return getSavedAppId(OneSignal.appContext);
    }
    
    private static String getSavedAppId(final Context context) {
        if (context == null) {
            return null;
        }
        return OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "GT_APP_ID", null);
    }
    
    private static String getSavedUserId(final Context context) {
        if (context == null) {
            return null;
        }
        return OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "GT_PLAYER_ID", null);
    }
    
    public static String getSdkVersionRaw() {
        return "040805";
    }
    
    static OSSessionManager.SessionListener getSessionListener() {
        return OneSignal.sessionListener;
    }
    
    static OSSessionManager getSessionManager() {
        return OneSignal.sessionManager;
    }
    
    static OSSharedPreferences getSharedPreferences() {
        return OneSignal.preferences;
    }
    
    static OSObservable<OSSubscriptionObserver, OSSubscriptionStateChanges> getSubscriptionStateChangesObserver() {
        if (OneSignal.subscriptionStateChangesObserver == null) {
            OneSignal.subscriptionStateChangesObserver = new OSObservable<OSSubscriptionObserver, OSSubscriptionStateChanges>("onOSSubscriptionChanged", true);
        }
        return OneSignal.subscriptionStateChangesObserver;
    }
    
    public static void getTags(final OSGetTagsHandler osGetTagsHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("getTags()")) {
            OneSignal.logger.error("Waiting for remote params. Moving getTags() operation to a pending queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(osGetTagsHandler) {
                final OSGetTagsHandler val$getTagsHandler;
                
                public void run() {
                    OneSignal.logger.debug("Running getTags() operation from pending queue.");
                    OneSignal.getTags(this.val$getTagsHandler);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("getTags()")) {
            return;
        }
        if (osGetTagsHandler == null) {
            OneSignal.logger.error("getTags called with null GetTagsHandler!");
            return;
        }
        new Thread((Runnable)new Runnable(osGetTagsHandler) {
            final OSGetTagsHandler val$getTagsHandler;
            
            public void run() {
                final ArrayList access$1600 = OneSignal.pendingGetTagsHandlers;
                synchronized (access$1600) {
                    OneSignal.pendingGetTagsHandlers.add((Object)this.val$getTagsHandler);
                    if (OneSignal.pendingGetTagsHandlers.size() > 1) {
                        return;
                    }
                    runGetTags();
                }
            }
        }, "OS_GETTAGS").start();
    }
    
    static OSTaskController getTaskController() {
        return OneSignal.taskController;
    }
    
    static OSTaskController getTaskRemoteController() {
        return (OSTaskController)OneSignal.taskRemoteController;
    }
    
    static OSTime getTime() {
        return OneSignal.time;
    }
    
    private static String getTimeZoneId() {
        if (Build$VERSION.SDK_INT >= 26) {
            return OneSignal$$ExternalSyntheticApiModelOutline0.m(OneSignal$$ExternalSyntheticApiModelOutline0.m());
        }
        return TimeZone.getDefault().getID();
    }
    
    private static int getTimeZoneOffset() {
        final TimeZone timeZone = Calendar.getInstance().getTimeZone();
        int rawOffset = timeZone.getRawOffset();
        if (timeZone.inDaylightTime(new Date())) {
            rawOffset += timeZone.getDSTSavings();
        }
        return rawOffset / 1000;
    }
    
    public static Object getTriggerValueForKey(final String s) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("Before calling getTriggerValueForKey, Make sure OneSignal initWithContext and setAppId is called first");
            return null;
        }
        return getInAppMessageController().getTriggerValue(s);
    }
    
    public static Map<String, Object> getTriggers() {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("Before calling getTriggers, Make sure OneSignal initWithContext and setAppId is called first");
            return (Map<String, Object>)new HashMap();
        }
        return (Map<String, Object>)getInAppMessageController().getTriggers();
    }
    
    static String getUserId() {
        if (OneSignal.userId == null) {
            final Context appContext = OneSignal.appContext;
            if (appContext != null) {
                OneSignal.userId = getSavedUserId(appContext);
            }
        }
        return OneSignal.userId;
    }
    
    private static void handleActivityLifecycleHandler(final Context context) {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
        final boolean b = context instanceof Activity;
        final boolean b2 = getCurrentActivity() == null;
        setInForeground(!b2 || b);
        final OSLogger logger = OneSignal.logger;
        final StringBuilder sb = new StringBuilder("OneSignal handleActivityLifecycleHandler inForeground: ");
        sb.append(OneSignal.inForeground);
        logger.debug(sb.toString());
        if (OneSignal.inForeground) {
            if (b2 && b && activityLifecycleHandler != null) {
                activityLifecycleHandler.setCurActivity((Activity)context);
                activityLifecycleHandler.setNextResumeIsFirstActivity(true);
            }
            OSNotificationRestoreWorkManager.beginEnqueueingWork(context, false);
            getFocusTimeController().appForegrounded();
        }
        else if (activityLifecycleHandler != null) {
            activityLifecycleHandler.setNextResumeIsFirstActivity(true);
        }
    }
    
    private static void handleAmazonPurchase() {
        try {
            Class.forName("com.amazon.device.iap.PurchasingListener");
            OneSignal.trackAmazonPurchase = new TrackAmazonPurchase(OneSignal.appContext);
        }
        catch (final ClassNotFoundException ex) {}
    }
    
    private static void handleAppIdChange() {
        final String savedAppId = getSavedAppId();
        if (savedAppId != null) {
            if (!savedAppId.equals((Object)OneSignal.appId)) {
                final LOG_LEVEL debug = LOG_LEVEL.DEBUG;
                final StringBuilder sb = new StringBuilder("App id has changed:\nFrom: ");
                sb.append(savedAppId);
                sb.append("\n To: ");
                sb.append(OneSignal.appId);
                sb.append("\nClearing the user id, app state, and remoteParams as they are no longer valid");
                Log(debug, sb.toString());
                saveAppId(OneSignal.appId);
                OneSignalStateSynchronizer.resetCurrentState();
                OneSignal.remoteParamController.clearRemoteParams();
            }
        }
        else {
            final LOG_LEVEL debug2 = LOG_LEVEL.DEBUG;
            final StringBuilder sb2 = new StringBuilder("App id set for first time:  ");
            sb2.append(OneSignal.appId);
            Log(debug2, sb2.toString());
            BadgeCountUpdater.updateCount(0, OneSignal.appContext);
            saveAppId(OneSignal.appId);
        }
    }
    
    static void handleFailedEmailLogout() {
        final EmailUpdateHandler emailLogoutHandler = OneSignal.emailLogoutHandler;
        if (emailLogoutHandler != null) {
            emailLogoutHandler.onFailure(new EmailUpdateError(EmailErrorType.NETWORK, "Failed due to network failure. Will retry on next sync."));
            OneSignal.emailLogoutHandler = null;
        }
    }
    
    static void handleFailedSMSLogout() {
        final OSSMSUpdateHandler smsLogoutHandler = OneSignal.smsLogoutHandler;
        if (smsLogoutHandler != null) {
            smsLogoutHandler.onFailure(new OSSMSUpdateError(SMSErrorType.NETWORK, "Failed due to network failure. Will retry on next sync."));
            OneSignal.smsLogoutHandler = null;
        }
    }
    
    static void handleNotificationOpen(final Activity activity, final JSONArray jsonArray, final String s) {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName(null)) {
            return;
        }
        notificationOpenedRESTCall((Context)activity, jsonArray);
        if (OneSignal.trackFirebaseAnalytics != null && getFirebaseAnalyticsEnabled()) {
            OneSignal.trackFirebaseAnalytics.trackOpenedEvent(generateNotificationOpenedResult(jsonArray));
        }
        if (shouldInitDirectSessionFromNotificationOpen(activity, jsonArray)) {
            applicationOpenedByNotification(s);
        }
        openDestinationActivity(activity, jsonArray);
        runNotificationOpenedCallback(jsonArray);
    }
    
    static void handleNotificationReceived(final OSNotificationGenerationJob osNotificationGenerationJob) {
        try {
            final JSONObject jsonObject = new JSONObject(osNotificationGenerationJob.getJsonPayload().toString());
            jsonObject.put("androidNotificationId", (Object)osNotificationGenerationJob.getAndroidId());
            final OSNotificationOpenedResult generateNotificationOpenedResult = generateNotificationOpenedResult(NotificationBundleProcessor.newJsonArray(jsonObject));
            if (OneSignal.trackFirebaseAnalytics != null && getFirebaseAnalyticsEnabled()) {
                OneSignal.trackFirebaseAnalytics.trackReceivedEvent(generateNotificationOpenedResult);
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    static void handleSuccessfulEmailLogout() {
        final EmailUpdateHandler emailLogoutHandler = OneSignal.emailLogoutHandler;
        if (emailLogoutHandler != null) {
            emailLogoutHandler.onSuccess();
            OneSignal.emailLogoutHandler = null;
        }
    }
    
    static void handleSuccessfulSMSlLogout(final JSONObject jsonObject) {
        final OSSMSUpdateHandler smsLogoutHandler = OneSignal.smsLogoutHandler;
        if (smsLogoutHandler != null) {
            smsLogoutHandler.onSuccess(jsonObject);
            OneSignal.smsLogoutHandler = null;
        }
    }
    
    static boolean hasEmailId() {
        return TextUtils.isEmpty((CharSequence)OneSignal.emailId) ^ true;
    }
    
    static boolean hasSMSlId() {
        return TextUtils.isEmpty((CharSequence)OneSignal.smsId) ^ true;
    }
    
    static boolean hasUserId() {
        return getUserId() != null;
    }
    
    private static void init(final Context context) {
        synchronized (OneSignal.class) {
            OneSignal.logger.verbose("Starting OneSignal initialization!");
            OSNotificationController.setupNotificationServiceExtension(OneSignal.appContext);
            if (requiresUserPrivacyConsent() || !OneSignal.remoteParamController.isRemoteParamsCallDone()) {
                if (!OneSignal.remoteParamController.isRemoteParamsCallDone()) {
                    OneSignal.logger.verbose("OneSignal SDK initialization delayed, waiting for remote params.");
                }
                else {
                    OneSignal.logger.verbose("OneSignal SDK initialization delayed, waiting for privacy consent to be set.");
                }
                OneSignal.delayedInitParams = new DelayedConsentInitializationParameters(OneSignal.appContext, OneSignal.appId);
                final String appId = OneSignal.appId;
                OneSignal.appId = null;
                if (appId != null && context != null) {
                    makeAndroidParamsRequest(appId, getUserId(), false);
                }
                return;
            }
            int subscribableStatus = OneSignal.subscribableStatus;
            if (subscribableStatus == Integer.MAX_VALUE) {
                subscribableStatus = OneSignal.osUtils.initializationChecker(OneSignal.appContext, OneSignal.appId);
            }
            OneSignal.subscribableStatus = subscribableStatus;
            if (isSubscriptionStatusUninitializable()) {
                return;
            }
            if (OneSignal.initDone) {
                if (OneSignal.notificationOpenedHandler != null) {
                    fireCallbackForOpenedNotifications();
                }
                OneSignal.logger.debug("OneSignal SDK initialization already completed.");
                return;
            }
            handleActivityLifecycleHandler(context);
            OneSignal.appActivity = null;
            OneSignalStateSynchronizer.initUserState();
            handleAppIdChange();
            handleAmazonPurchase();
            OSPermissionChangedInternalObserver.handleInternalChanges(getCurrentPermissionState(OneSignal.appContext));
            doSessionInit();
            if (OneSignal.notificationOpenedHandler != null) {
                fireCallbackForOpenedNotifications();
            }
            if (TrackGooglePurchase.CanTrack(OneSignal.appContext)) {
                OneSignal.trackGooglePurchase = new TrackGooglePurchase(OneSignal.appContext);
            }
            if (TrackFirebaseAnalytics.CanTrack()) {
                OneSignal.trackFirebaseAnalytics = new TrackFirebaseAnalytics(OneSignal.appContext);
            }
            OneSignal.initDone = true;
            Log(LOG_LEVEL.VERBOSE, "OneSignal SDK initialization done.");
            getOutcomeEventsController().sendSavedOutcomes();
            OneSignal.taskRemoteController.startPendingTasks();
        }
    }
    
    public static void initWithContext(final Context context) {
        if (context == null) {
            OneSignal.logger.warning("initWithContext called with null context, ignoring!");
            return;
        }
        if (context instanceof Activity) {
            OneSignal.appActivity = (WeakReference<Activity>)new WeakReference((Object)context);
        }
        final boolean b = OneSignal.appContext == null;
        OneSignal.appContext = context.getApplicationContext();
        setupContextListeners(b);
        setupPrivacyConsent(OneSignal.appContext);
        if (OneSignal.appId == null) {
            final String savedAppId = getSavedAppId();
            if (savedAppId == null) {
                OneSignal.logger.warning("appContext set, but please call setAppId(appId) with a valid appId to complete OneSignal init!");
            }
            else {
                final OSLogger logger = OneSignal.logger;
                final StringBuilder sb = new StringBuilder("appContext set and cached app id found, calling setAppId with: ");
                sb.append(savedAppId);
                logger.verbose(sb.toString());
                setAppId(savedAppId);
            }
            return;
        }
        final OSLogger logger2 = OneSignal.logger;
        final StringBuilder sb2 = new StringBuilder("initWithContext called with: ");
        sb2.append((Object)context);
        logger2.verbose(sb2.toString());
        init(context);
    }
    
    private static void internalFireGetTagsCallbacks() {
        final ArrayList<OSGetTagsHandler> pendingGetTagsHandlers = OneSignal.pendingGetTagsHandlers;
        synchronized (pendingGetTagsHandlers) {
            if (pendingGetTagsHandlers.size() == 0) {
                return;
            }
            monitorexit(pendingGetTagsHandlers);
            new Thread((Runnable)new Runnable() {
                public void run() {
                    final UserStateSynchronizer$GetTagsResult tags = OneSignalStateSynchronizer.getTags(OneSignal.getTagsCall ^ true);
                    if (tags.serverSuccess) {
                        OneSignal.getTagsCall = true;
                    }
                    final ArrayList access$1600 = OneSignal.pendingGetTagsHandlers;
                    synchronized (access$1600) {
                        for (final OSGetTagsHandler osGetTagsHandler : OneSignal.pendingGetTagsHandlers) {
                            JSONObject result;
                            if (tags.result != null && !tags.toString().equals((Object)"{}")) {
                                result = tags.result;
                            }
                            else {
                                result = null;
                            }
                            osGetTagsHandler.tagsAvailable(result);
                        }
                        OneSignal.pendingGetTagsHandlers.clear();
                    }
                }
            }, "OS_GETTAGS_CALLBACK").start();
        }
    }
    
    static boolean isAppActive() {
        return OneSignal.initDone && isInForeground();
    }
    
    public static boolean isInAppMessagingPaused() {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("Before calling isInAppMessagingPaused, Make sure OneSignal initWithContext and setAppId is called first");
            return false;
        }
        return getInAppMessageController().inAppMessagingEnabled() ^ true;
    }
    
    static boolean isInForeground() {
        return OneSignal.inForeground;
    }
    
    static boolean isInitDone() {
        return OneSignal.initDone;
    }
    
    public static boolean isLocationShared() {
        return OneSignal.remoteParamController.isLocationShared();
    }
    
    private static boolean isPastOnSessionTime() {
        final long currentTimeMillis = getTime().getCurrentTimeMillis();
        final long lastSessionTime = getLastSessionTime();
        final long n = currentTimeMillis - lastSessionTime;
        final OSLogger logger = OneSignal.logger;
        final StringBuilder sb = new StringBuilder("isPastOnSessionTime currentTimeMillis: ");
        sb.append(currentTimeMillis);
        sb.append(" lastSessionTime: ");
        sb.append(lastSessionTime);
        sb.append(" difference: ");
        sb.append(n);
        logger.debug(sb.toString());
        return n >= 30000L;
    }
    
    private static boolean isSubscriptionStatusUninitializable() {
        return OneSignal.subscribableStatus == -999;
    }
    
    static boolean isUserPrivacyConsentRequired() {
        return OneSignal.remoteParamController.isPrivacyConsentRequired();
    }
    
    private static boolean isValidOutcomeEntry(final String s) {
        if (s != null && !s.isEmpty()) {
            return true;
        }
        Log(LOG_LEVEL.ERROR, "Outcome name must not be empty");
        return false;
    }
    
    private static boolean isValidOutcomeValue(final float n) {
        if (n <= 0.0f) {
            Log(LOG_LEVEL.ERROR, "Outcome value must be greater than 0");
            return false;
        }
        return true;
    }
    
    static void logHttpError(final String s, final int n, final Throwable t, String string) {
        if (string != null && atLogLevel(LOG_LEVEL.INFO)) {
            final StringBuilder sb = new StringBuilder("\n");
            sb.append(string);
            sb.append("\n");
            string = sb.toString();
        }
        else {
            string = "";
        }
        final LOG_LEVEL warn = LOG_LEVEL.WARN;
        final StringBuilder sb2 = new StringBuilder("HTTP code: ");
        sb2.append(n);
        sb2.append(" ");
        sb2.append(s);
        sb2.append(string);
        Log(warn, sb2.toString(), t);
    }
    
    public static void logoutEmail() {
        logoutEmail(null);
    }
    
    public static void logoutEmail(final EmailUpdateHandler emailLogoutHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("logoutEmail()")) {
            OneSignal.logger.error("Waiting for remote params. Moving logoutEmail() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(emailLogoutHandler) {
                final EmailUpdateHandler val$callback;
                
                public void run() {
                    OneSignal.logger.debug("Running  logoutEmail() operation from pending task queue.");
                    OneSignal.logoutEmail(this.val$callback);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("logoutEmail()")) {
            return;
        }
        if (getEmailId() == null) {
            if (emailLogoutHandler != null) {
                emailLogoutHandler.onFailure(new EmailUpdateError(EmailErrorType.INVALID_OPERATION, "logoutEmail not valid as email was not set or already logged out!"));
            }
            OneSignal.logger.error("logoutEmail not valid as email was not set or already logged out!");
            return;
        }
        OneSignal.emailLogoutHandler = emailLogoutHandler;
        OneSignalStateSynchronizer.logoutEmail();
    }
    
    public static void logoutSMSNumber() {
        logoutSMSNumber(null);
    }
    
    public static void logoutSMSNumber(final OSSMSUpdateHandler smsLogoutHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("logoutSMSNumber()")) {
            OneSignal.logger.error("Waiting for remote params. Moving logoutSMSNumber() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(smsLogoutHandler) {
                final OSSMSUpdateHandler val$callback;
                
                public void run() {
                    OneSignal.logger.debug("Running  logoutSMSNumber() operation from pending task queue.");
                    OneSignal.logoutSMSNumber(this.val$callback);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("logoutSMSNumber()")) {
            return;
        }
        if (getSMSId() == null) {
            if (smsLogoutHandler != null) {
                smsLogoutHandler.onFailure(new OSSMSUpdateError(SMSErrorType.INVALID_OPERATION, "logoutSMSNumber() not valid as sms number was not set or already logged out!"));
            }
            OneSignal.logger.error("logoutSMSNumber() not valid as sms number was not set or already logged out!");
            return;
        }
        OneSignal.smsLogoutHandler = smsLogoutHandler;
        OneSignalStateSynchronizer.logoutSMS();
    }
    
    private static void makeAndroidParamsRequest(final String s, final String s2, final boolean b) {
        if (getRemoteParams() == null) {
            if (!OneSignal.androidParamsRequestStarted) {
                OneSignal.androidParamsRequestStarted = true;
                OneSignalRemoteParams.makeAndroidParamsRequest(s, s2, (OneSignalRemoteParams.Callback)new OneSignal$7(b));
            }
        }
    }
    
    static void notValidOrDuplicated(final Context context, final JSONObject jsonObject, final OSNotificationDataController.InvalidOrDuplicateNotificationCallback invalidOrDuplicateNotificationCallback) {
        if (OneSignal.notificationDataController == null) {
            OneSignal.notificationDataController = new OSNotificationDataController(getDBHelperInstance(context), OneSignal.logger);
        }
        OneSignal.notificationDataController.notValidOrDuplicated(jsonObject, invalidOrDuplicateNotificationCallback);
    }
    
    private static void notificationOpenedRESTCall(final Context context, final JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                final String optString = new JSONObject(jsonArray.getJSONObject(i).optString("custom", (String)null)).optString("i", (String)null);
                if (!OneSignal.postedOpenedNotifIds.contains((Object)optString)) {
                    OneSignal.postedOpenedNotifIds.add((Object)optString);
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_id", (Object)getSavedAppId(context));
                    jsonObject.put("player_id", (Object)getSavedUserId(context));
                    jsonObject.put("opened", true);
                    jsonObject.put("device_type", OneSignal.osUtils.getDeviceType());
                    final StringBuilder sb = new StringBuilder();
                    sb.append("notifications/");
                    sb.append(optString);
                    OneSignalRestClient.put(sb.toString(), jsonObject, (OneSignalRestClient.ResponseHandler)new OneSignal$26());
                }
            }
            finally {
                final Throwable t;
                Log(LOG_LEVEL.ERROR, "Failed to generate JSON to send notification opened.", t);
            }
        }
    }
    
    static void onAppFocus() {
        Log(LOG_LEVEL.DEBUG, "Application on focus");
        setInForeground(true);
        if (!OneSignal.appEntryState.equals((Object)AppEntryAction.NOTIFICATION_CLICK)) {
            callEntryStateListeners(OneSignal.appEntryState);
            if (!OneSignal.appEntryState.equals((Object)AppEntryAction.NOTIFICATION_CLICK)) {
                OneSignal.appEntryState = AppEntryAction.APP_OPEN;
            }
        }
        LocationController.onFocusChange();
        NotificationPermissionController.INSTANCE.onAppForegrounded();
        if (OSUtils.shouldLogMissingAppIdError(OneSignal.appId)) {
            return;
        }
        if (!OneSignal.remoteParamController.isRemoteParamsCallDone()) {
            Log(LOG_LEVEL.DEBUG, "Delay onAppFocus logic due to missing remote params");
            makeAndroidParamsRequest(OneSignal.appId, getUserId(), false);
            return;
        }
        onAppFocusLogic();
    }
    
    private static void onAppFocusLogic() {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("onAppFocus")) {
            return;
        }
        getFocusTimeController().appForegrounded();
        doSessionInit();
        final TrackGooglePurchase trackGooglePurchase = OneSignal.trackGooglePurchase;
        if (trackGooglePurchase != null) {
            trackGooglePurchase.trackIAP();
        }
        OSNotificationRestoreWorkManager.beginEnqueueingWork(OneSignal.appContext, false);
        refreshNotificationPermissionState();
        if (OneSignal.trackFirebaseAnalytics != null && getFirebaseAnalyticsEnabled()) {
            OneSignal.trackFirebaseAnalytics.trackInfluenceOpenEvent();
        }
        OSSyncService.getInstance().cancelSyncTask(OneSignal.appContext);
    }
    
    static void onAppLostFocus() {
        final LOG_LEVEL debug = LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("Application lost focus initDone: ");
        sb.append(OneSignal.initDone);
        Log(debug, sb.toString());
        setInForeground(false);
        OneSignal.appEntryState = AppEntryAction.APP_CLOSE;
        setLastSessionTime(getTime().getCurrentTimeMillis());
        LocationController.onFocusChange();
        if (!OneSignal.initDone) {
            if (OneSignal.taskRemoteController.shouldQueueTaskForInit("onAppLostFocus()")) {
                OneSignal.logger.error("Waiting for remote params. Moving onAppLostFocus() operation to a pending task queue.");
                OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable() {
                    public void run() {
                        OneSignal.logger.debug("Running onAppLostFocus() operation from a pending task queue.");
                        OneSignal.backgroundSyncLogic();
                    }
                });
            }
            return;
        }
        backgroundSyncLogic();
    }
    
    static void onAppStartFocusLogic() {
        refreshNotificationPermissionState();
    }
    
    static void onRemoteParamSet() {
        if (!reassignDelayedInitParams() && OneSignal.inForeground) {
            onAppFocusLogic();
        }
    }
    
    public static void onesignalLog(final LOG_LEVEL log_LEVEL, final String s) {
        Log(log_LEVEL, s);
    }
    
    static void openDestinationActivity(final Activity activity, final JSONArray jsonArray) {
        try {
            final Intent intentVisible = GenerateNotificationOpenIntentFromPushPayload.INSTANCE.create((Context)activity, jsonArray.getJSONObject(0)).getIntentVisible();
            if (intentVisible != null) {
                final OSLogger logger = OneSignal.logger;
                final StringBuilder sb = new StringBuilder("SDK running startActivity with Intent: ");
                sb.append((Object)intentVisible);
                logger.info(sb.toString());
                activity.startActivity(intentVisible);
            }
            else {
                OneSignal.logger.info("SDK not showing an Activity automatically due to it's settings.");
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void pauseInAppMessages(final boolean b) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("Waiting initWithContext. Moving pauseInAppMessages() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(b) {
                final boolean val$pause;
                
                public void run() {
                    OneSignal.logger.debug("Running pauseInAppMessages() operation from pending queue.");
                    OneSignal.pauseInAppMessages(this.val$pause);
                }
            });
            return;
        }
        getInAppMessageController().setInAppMessagingEnabled(b ^ true);
    }
    
    public static void postNotification(final String s, final PostNotificationResponseHandler postNotificationResponseHandler) {
        try {
            postNotification(new JSONObject(s), postNotificationResponseHandler);
        }
        catch (final JSONException ex) {
            final LOG_LEVEL error = LOG_LEVEL.ERROR;
            final StringBuilder sb = new StringBuilder("Invalid postNotification JSON format: ");
            sb.append(s);
            Log(error, sb.toString());
        }
    }
    
    public static void postNotification(final JSONObject jsonObject, final PostNotificationResponseHandler postNotificationResponseHandler) {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("postNotification()")) {
            return;
        }
        try {
            if (!jsonObject.has("app_id")) {
                jsonObject.put("app_id", (Object)getSavedAppId());
            }
            if (!jsonObject.has("app_id")) {
                if (postNotificationResponseHandler != null) {
                    postNotificationResponseHandler.onFailure(new JSONObject().put("error", (Object)"Missing app_id"));
                }
                return;
            }
            OneSignalRestClient.post("notifications/", jsonObject, (OneSignalRestClient.ResponseHandler)new OneSignal$21(postNotificationResponseHandler));
        }
        catch (final JSONException ex) {
            OneSignal.logger.error("HTTP create notification json exception!", (Throwable)ex);
            if (postNotificationResponseHandler != null) {
                try {
                    postNotificationResponseHandler.onFailure(new JSONObject("{'error': 'HTTP create notification json exception!'}"));
                }
                catch (final JSONException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }
    
    public static void promptForPushNotifications() {
        promptForPushNotifications(false);
    }
    
    public static void promptForPushNotifications(final boolean b) {
        promptForPushNotifications(b, null);
    }
    
    public static void promptForPushNotifications(final boolean b, final PromptForPushNotificationPermissionResponseHandler promptForPushNotificationPermissionResponseHandler) {
        NotificationPermissionController.INSTANCE.prompt(b, promptForPushNotificationPermissionResponseHandler);
    }
    
    public static void promptLocation() {
        promptLocation(null, false);
    }
    
    static void promptLocation(final OSPromptActionCompletionCallback osPromptActionCompletionCallback, final boolean b) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("promptLocation()")) {
            OneSignal.logger.error("Waiting for remote params. Moving promptLocation() operation to a pending queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(osPromptActionCompletionCallback, b) {
                final OSPromptActionCompletionCallback val$callback;
                final boolean val$fallbackToSettings;
                
                public void run() {
                    OneSignal.logger.debug("Running promptLocation() operation from pending queue.");
                    OneSignal.promptLocation(this.val$callback, this.val$fallbackToSettings);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("promptLocation()")) {
            return;
        }
        LocationController.getLocation(OneSignal.appContext, true, b, (LocationController.LocationHandler)new OneSignal$30(osPromptActionCompletionCallback));
    }
    
    public static void provideUserConsent(final boolean b) {
        final boolean userProvidedPrivacyConsent = userProvidedPrivacyConsent();
        OneSignal.remoteParamController.saveUserConsentStatus(b);
        if (!userProvidedPrivacyConsent && b && OneSignal.delayedInitParams != null) {
            Log(LOG_LEVEL.VERBOSE, "Privacy consent provided, reassigning all delayed init params and attempting init again...");
            reassignDelayedInitParams();
        }
    }
    
    private static boolean pushStatusRuntimeError(final int n) {
        return n < -6;
    }
    
    private static boolean reassignDelayedInitParams() {
        if (OneSignal.initDone) {
            return false;
        }
        final DelayedConsentInitializationParameters delayedInitParams = OneSignal.delayedInitParams;
        String appId;
        Context context;
        if (delayedInitParams == null) {
            appId = getSavedAppId();
            context = OneSignal.appContext;
            OneSignal.logger.error("Trying to continue OneSignal with null delayed params");
        }
        else {
            appId = delayedInitParams.getAppId();
            context = OneSignal.delayedInitParams.getContext();
        }
        final OSLogger logger = OneSignal.logger;
        final StringBuilder sb = new StringBuilder("reassignDelayedInitParams with appContext: ");
        sb.append((Object)OneSignal.appContext);
        logger.debug(sb.toString());
        OneSignal.delayedInitParams = null;
        setAppId(appId);
        if (!OneSignal.initDone) {
            if (context == null) {
                OneSignal.logger.error("Trying to continue OneSignal with null delayed params context");
                return false;
            }
            initWithContext(context);
        }
        return true;
    }
    
    static void refreshNotificationPermissionState() {
        getCurrentPermissionState(OneSignal.appContext).refreshAsTo();
    }
    
    private static void registerForPushToken() {
        getPushRegistrator().registerForPush(OneSignal.appContext, OneSignal.googleProjectNumber, (PushRegistrator.RegisteredHandler)new OneSignal$6());
    }
    
    private static void registerUser() {
        final OSLogger logger = OneSignal.logger;
        final StringBuilder sb = new StringBuilder("registerUser:registerForPushFired:");
        sb.append(OneSignal.registerForPushFired);
        sb.append(", locationFired: ");
        sb.append(OneSignal.locationFired);
        sb.append(", remoteParams: ");
        sb.append((Object)getRemoteParams());
        sb.append(", appId: ");
        sb.append(OneSignal.appId);
        logger.debug(sb.toString());
        if (OneSignal.registerForPushFired && OneSignal.locationFired && getRemoteParams() != null && OneSignal.appId != null) {
            new Thread((Runnable)new Runnable() {
                public void run() {
                    try {
                        registerUserTask();
                    }
                    catch (final JSONException ex) {
                        OneSignal.Log(LOG_LEVEL.FATAL, "FATAL Error registering device!", (Throwable)ex);
                    }
                }
            }, "OS_REG_USER").start();
            return;
        }
        OneSignal.logger.debug("registerUser not possible");
    }
    
    private static void registerUserTask() throws JSONException {
        final String packageName = OneSignal.appContext.getPackageName();
        final PackageManager packageManager = OneSignal.appContext.getPackageManager();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_id", (Object)getSavedAppId());
        jsonObject.put("device_os", (Object)Build$VERSION.RELEASE);
        jsonObject.put("timezone", getTimeZoneOffset());
        jsonObject.put("timezone_id", (Object)getTimeZoneId());
        jsonObject.put("language", (Object)OneSignal.languageContext.getLanguage());
        jsonObject.put("sdk", (Object)"040805");
        jsonObject.put("sdk_type", (Object)OneSignal.sdkType);
        jsonObject.put("android_package", (Object)packageName);
        jsonObject.put("device_model", (Object)Build.MODEL);
        try {
            jsonObject.put("game_version", packageManager.getPackageInfo(packageName, 0).versionCode);
        }
        catch (final PackageManager$NameNotFoundException ex) {}
        jsonObject.put("net_type", (Object)OneSignal.osUtils.getNetType());
        jsonObject.put("carrier", (Object)OneSignal.osUtils.getCarrierName());
        jsonObject.put("rooted", RootToolsInternalMethods.isRooted());
        OneSignalStateSynchronizer.updateDeviceInfo(jsonObject, null);
        final JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("identifier", (Object)OneSignal.lastRegistrationId);
        jsonObject2.put("subscribableStatus", OneSignal.subscribableStatus);
        jsonObject2.put("androidPermission", areNotificationsEnabledForSubscribedState());
        jsonObject2.put("device_type", OneSignal.osUtils.getDeviceType());
        OneSignalStateSynchronizer.updatePushState(jsonObject2);
        if (isLocationShared()) {
            final LocationController.LocationPoint lastLocationPoint = OneSignal.lastLocationPoint;
            if (lastLocationPoint != null) {
                OneSignalStateSynchronizer.updateLocation(lastLocationPoint);
            }
        }
        OneSignal.logger.debug("registerUserTask calling readyToUpdate");
        OneSignalStateSynchronizer.readyToUpdate(true);
        OneSignal.waitingToPostStateSync = false;
    }
    
    public static void removeEmailSubscriptionObserver(final OSEmailSubscriptionObserver osEmailSubscriptionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not modify email subscription observer");
            return;
        }
        getEmailSubscriptionStateChangesObserver().removeObserver(osEmailSubscriptionObserver);
    }
    
    static void removeEntryStateListener(final EntryStateListener entryStateListener) {
        OneSignal.entryStateListeners.remove((Object)entryStateListener);
    }
    
    public static void removeExternalUserId() {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("removeExternalUserId()")) {
            return;
        }
        removeExternalUserId(null);
    }
    
    public static void removeExternalUserId(final OSExternalUserIdUpdateCompletionHandler osExternalUserIdUpdateCompletionHandler) {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("removeExternalUserId()")) {
            return;
        }
        setExternalUserId("", osExternalUserIdUpdateCompletionHandler);
    }
    
    public static void removeGroupedNotifications(final String s) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("removeGroupedNotifications()") || OneSignal.notificationDataController == null) {
            OneSignal.logger.error("Waiting for remote params. Moving removeGroupedNotifications() operation to a pending queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(s) {
                final String val$group;
                
                public void run() {
                    OneSignal.logger.debug("Running removeGroupedNotifications() operation from pending queue.");
                    OneSignal.removeGroupedNotifications(this.val$group);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("removeGroupedNotifications()")) {
            return;
        }
        OneSignal.notificationDataController.removeGroupedNotifications(s, new WeakReference((Object)OneSignal.appContext));
    }
    
    public static void removeNotification(final int n) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("removeNotification()") || OneSignal.notificationDataController == null) {
            OneSignal.logger.error("Waiting for remote params. Moving removeNotification() operation to a pending queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(n) {
                final int val$id;
                
                public void run() {
                    OneSignal.logger.debug("Running removeNotification() operation from pending queue.");
                    OneSignal.removeNotification(this.val$id);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("removeNotification()")) {
            return;
        }
        OneSignal.notificationDataController.removeNotification(n, new WeakReference((Object)OneSignal.appContext));
    }
    
    public static void removePermissionObserver(final OSPermissionObserver osPermissionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not modify permission observer");
            return;
        }
        getPermissionStateChangesObserver().removeObserver(osPermissionObserver);
    }
    
    public static void removeSMSSubscriptionObserver(final OSSMSSubscriptionObserver ossmsSubscriptionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not modify sms subscription observer");
            return;
        }
        getSMSSubscriptionStateChangesObserver().removeObserver(ossmsSubscriptionObserver);
    }
    
    public static void removeSubscriptionObserver(final OSSubscriptionObserver osSubscriptionObserver) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("OneSignal.initWithContext has not been called. Could not modify subscription observer");
            return;
        }
        getSubscriptionStateChangesObserver().removeObserver(osSubscriptionObserver);
    }
    
    public static void removeTriggerForKey(final String s) {
        final ArrayList list = new ArrayList();
        list.add((Object)s);
        getInAppMessageController().removeTriggersForKeys((Collection)list);
    }
    
    public static void removeTriggersForKeys(final Collection<String> collection) {
        getInAppMessageController().removeTriggersForKeys((Collection)collection);
    }
    
    public static boolean requiresUserPrivacyConsent() {
        return OneSignal.appContext == null || (isUserPrivacyConsentRequired() && !userProvidedPrivacyConsent());
    }
    
    private static void runGetTags() {
        if (getUserId() == null) {
            OneSignal.logger.warning("getTags called under a null user!");
            return;
        }
        internalFireGetTagsCallbacks();
    }
    
    private static void runNotificationOpenedCallback(final JSONArray jsonArray) {
        if (OneSignal.notificationOpenedHandler == null) {
            OneSignal.unprocessedOpenedNotifs.add((Object)jsonArray);
            return;
        }
        final OSNotificationOpenedResult generateNotificationOpenedResult = generateNotificationOpenedResult(jsonArray);
        addEntryStateListener((EntryStateListener)generateNotificationOpenedResult, OneSignal.appEntryState);
        fireNotificationOpenedHandler(generateNotificationOpenedResult);
    }
    
    private static void saveAppId(final String s) {
        if (OneSignal.appContext == null) {
            return;
        }
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "GT_APP_ID", s);
    }
    
    static void saveEmailId(String emailId) {
        OneSignal.emailId = emailId;
        if (OneSignal.appContext == null) {
            return;
        }
        final String prefs_ONESIGNAL = OneSignalPrefs.PREFS_ONESIGNAL;
        if ("".equals((Object)OneSignal.emailId)) {
            emailId = null;
        }
        else {
            emailId = OneSignal.emailId;
        }
        OneSignalPrefs.saveString(prefs_ONESIGNAL, "OS_EMAIL_ID", emailId);
    }
    
    static void saveSMSId(String smsId) {
        OneSignal.smsId = smsId;
        if (OneSignal.appContext == null) {
            return;
        }
        final String prefs_ONESIGNAL = OneSignalPrefs.PREFS_ONESIGNAL;
        if ("".equals((Object)OneSignal.smsId)) {
            smsId = null;
        }
        else {
            smsId = OneSignal.smsId;
        }
        OneSignalPrefs.saveString(prefs_ONESIGNAL, "PREFS_OS_SMS_ID", smsId);
    }
    
    static void saveUserId(final String userId) {
        OneSignal.userId = userId;
        if (OneSignal.appContext == null) {
            return;
        }
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "GT_PLAYER_ID", OneSignal.userId);
    }
    
    private static boolean scheduleSyncService() {
        final boolean persist = OneSignalStateSynchronizer.persist();
        final OSLogger logger = OneSignal.logger;
        final StringBuilder sb = new StringBuilder("OneSignal scheduleSyncService unsyncedChanges: ");
        sb.append(persist);
        logger.debug(sb.toString());
        if (persist) {
            OSSyncService.getInstance().scheduleSyncTask(OneSignal.appContext);
        }
        final boolean scheduleUpdate = LocationController.scheduleUpdate(OneSignal.appContext);
        final OSLogger logger2 = OneSignal.logger;
        final StringBuilder sb2 = new StringBuilder("OneSignal scheduleSyncService locationScheduled: ");
        sb2.append(scheduleUpdate);
        logger2.debug(sb2.toString());
        return scheduleUpdate || persist;
    }
    
    static void sendClickActionOutcomes(final List<OSInAppMessageOutcome> list) {
        final OSOutcomeEventsController outcomeEventsController = OneSignal.outcomeEventsController;
        if (outcomeEventsController != null && OneSignal.appId != null) {
            outcomeEventsController.sendClickActionOutcomes(list);
            return;
        }
        Log(LOG_LEVEL.ERROR, "Make sure OneSignal.init is called first");
    }
    
    public static void sendOutcome(final String s) {
        sendOutcome(s, null);
    }
    
    public static void sendOutcome(final String s, final OutcomeCallback outcomeCallback) {
        if (!isValidOutcomeEntry(s)) {
            OneSignal.logger.error("Make sure OneSignal initWithContext and setAppId is called first");
            return;
        }
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("sendOutcome()") || OneSignal.outcomeEventsController == null) {
            OneSignal.logger.error("Waiting for remote params. Moving sendOutcome() operation to a pending queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(s, outcomeCallback) {
                final OutcomeCallback val$callback;
                final String val$name;
                
                public void run() {
                    OneSignal.logger.debug("Running sendOutcome() operation from pending queue.");
                    OneSignal.sendOutcome(this.val$name, this.val$callback);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("sendOutcome()")) {
            return;
        }
        OneSignal.outcomeEventsController.sendOutcomeEvent(s, outcomeCallback);
    }
    
    public static void sendOutcomeWithValue(final String s, final float n) {
        sendOutcomeWithValue(s, n, null);
    }
    
    public static void sendOutcomeWithValue(final String s, final float n, final OutcomeCallback outcomeCallback) {
        if (isValidOutcomeEntry(s)) {
            if (isValidOutcomeValue(n)) {
                if (!OneSignal.taskRemoteController.shouldQueueTaskForInit("sendOutcomeWithValue()")) {
                    final OSOutcomeEventsController outcomeEventsController = OneSignal.outcomeEventsController;
                    if (outcomeEventsController != null) {
                        outcomeEventsController.sendOutcomeEventWithValue(s, n, outcomeCallback);
                        return;
                    }
                }
                OneSignal.logger.error("Waiting for remote params. Moving sendOutcomeWithValue() operation to a pending queue.");
                OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(s, n, outcomeCallback) {
                    final OutcomeCallback val$callback;
                    final String val$name;
                    final float val$value;
                    
                    public void run() {
                        OneSignal.logger.debug("Running sendOutcomeWithValue() operation from pending queue.");
                        OneSignal.sendOutcomeWithValue(this.val$name, this.val$value, this.val$callback);
                    }
                });
            }
        }
    }
    
    static void sendPurchases(final JSONArray jsonArray, final boolean newAsExisting, final OneSignalRestClient.ResponseHandler restResponseHandler) {
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("sendPurchases()")) {
            return;
        }
        if (getUserId() == null) {
            (OneSignal.iapUpdateJob = new IAPUpdateJob(jsonArray)).newAsExisting = newAsExisting;
            OneSignal.iapUpdateJob.restResponseHandler = restResponseHandler;
            return;
        }
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_id", (Object)getSavedAppId());
            if (newAsExisting) {
                jsonObject.put("existing", true);
            }
            jsonObject.put("purchases", (Object)jsonArray);
            OneSignalStateSynchronizer.sendPurchases(jsonObject, restResponseHandler);
        }
        finally {
            final Throwable t;
            Log(LOG_LEVEL.ERROR, "Failed to generate JSON for sendPurchases.", t);
        }
    }
    
    public static void sendTag(final String s, final String s2) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("sendTag()")) {
            OneSignal.logger.error("Waiting for remote params. Moving sendTag() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(s, s2) {
                final String val$key;
                final String val$value;
                
                public void run() {
                    OneSignal.logger.debug("Running sendTag() operation from pending task queue.");
                    OneSignal.sendTag(this.val$key, this.val$value);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("sendTag()")) {
            return;
        }
        try {
            sendTags(new JSONObject().put(s, (Object)s2));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void sendTags(final String s) {
        try {
            sendTags(new JSONObject(s));
        }
        catch (final JSONException ex) {
            Log(LOG_LEVEL.ERROR, "Generating JSONObject for sendTags failed!", (Throwable)ex);
        }
    }
    
    public static void sendTags(final JSONObject jsonObject) {
        sendTags(jsonObject, null);
    }
    
    public static void sendTags(final JSONObject jsonObject, final ChangeTagsUpdateHandler changeTagsUpdateHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("sendTags()")) {
            OneSignal.logger.error("Waiting for remote params. Moving sendTags() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(jsonObject, changeTagsUpdateHandler) {
                final ChangeTagsUpdateHandler val$changeTagsUpdateHandler;
                final JSONObject val$keyValues;
                
                public void run() {
                    OneSignal.logger.debug("Running sendTags() operation from pending task queue.");
                    OneSignal.sendTags(this.val$keyValues, this.val$changeTagsUpdateHandler);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("sendTags()")) {
            return;
        }
        final Runnable runnable = (Runnable)new Runnable(jsonObject, changeTagsUpdateHandler) {
            final ChangeTagsUpdateHandler val$changeTagsUpdateHandler;
            final JSONObject val$keyValues;
            
            public void run() {
                if (this.val$keyValues == null) {
                    OneSignal.logger.error("Attempted to send null tags");
                    final ChangeTagsUpdateHandler val$changeTagsUpdateHandler = this.val$changeTagsUpdateHandler;
                    if (val$changeTagsUpdateHandler != null) {
                        val$changeTagsUpdateHandler.onFailure(new SendTagsError(-1, "Attempted to send null tags"));
                    }
                    return;
                }
                final JSONObject result = OneSignalStateSynchronizer.getTags(false).result;
                final JSONObject jsonObject = new JSONObject();
                final Iterator keys = this.val$keyValues.keys();
                while (keys.hasNext()) {
                    final String s = (String)keys.next();
                    try {
                        final Object opt = this.val$keyValues.opt(s);
                        if (!(opt instanceof JSONArray) && !(opt instanceof JSONObject)) {
                            if (!this.val$keyValues.isNull(s) && !"".equals(opt)) {
                                jsonObject.put(s, (Object)opt.toString());
                            }
                            else if (result != null && result.has(s)) {
                                jsonObject.put(s, (Object)"");
                            }
                        }
                        else {
                            final LOG_LEVEL error = LOG_LEVEL.ERROR;
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Omitting key '");
                            sb.append(s);
                            sb.append("'! sendTags DO NOT supported nested values!");
                            OneSignal.Log(error, sb.toString());
                        }
                    }
                    finally {}
                }
                if (!jsonObject.toString().equals((Object)"{}")) {
                    final OSLogger access$100 = OneSignal.logger;
                    final StringBuilder sb2 = new StringBuilder("Available tags to send: ");
                    sb2.append(jsonObject.toString());
                    access$100.debug(sb2.toString());
                    OneSignalStateSynchronizer.sendTags(jsonObject, this.val$changeTagsUpdateHandler);
                }
                else {
                    OneSignal.logger.debug("Send tags ended successfully");
                    final ChangeTagsUpdateHandler val$changeTagsUpdateHandler2 = this.val$changeTagsUpdateHandler;
                    if (val$changeTagsUpdateHandler2 != null) {
                        val$changeTagsUpdateHandler2.onSuccess(result);
                    }
                }
            }
        };
        if (OneSignal.taskRemoteController.shouldRunTaskThroughQueue()) {
            OneSignal.logger.debug("Sending sendTags() operation to pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)runnable);
            return;
        }
        ((Runnable)runnable).run();
    }
    
    public static void sendUniqueOutcome(final String s) {
        sendUniqueOutcome(s, null);
    }
    
    public static void sendUniqueOutcome(final String s, final OutcomeCallback outcomeCallback) {
        if (!isValidOutcomeEntry(s)) {
            return;
        }
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("sendUniqueOutcome()") || OneSignal.outcomeEventsController == null) {
            OneSignal.logger.error("Waiting for remote params. Moving sendUniqueOutcome() operation to a pending queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(s, outcomeCallback) {
                final OutcomeCallback val$callback;
                final String val$name;
                
                public void run() {
                    OneSignal.logger.debug("Running sendUniqueOutcome() operation from pending queue.");
                    OneSignal.sendUniqueOutcome(this.val$name, this.val$callback);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("sendUniqueOutcome()")) {
            return;
        }
        OneSignal.outcomeEventsController.sendUniqueOutcomeEvent(s, outcomeCallback);
    }
    
    public static void setAppId(final String appId) {
        if (appId == null || appId.isEmpty()) {
            final OSLogger logger = OneSignal.logger;
            final StringBuilder sb = new StringBuilder("setAppId called with id: ");
            sb.append(appId);
            sb.append(", ignoring!");
            logger.warning(sb.toString());
            return;
        }
        if (!appId.equals((Object)OneSignal.appId)) {
            OneSignal.initDone = false;
            final OSLogger logger2 = OneSignal.logger;
            final StringBuilder sb2 = new StringBuilder("setAppId called with id: ");
            sb2.append(appId);
            sb2.append(" changing id from: ");
            sb2.append(OneSignal.appId);
            logger2.verbose(sb2.toString());
        }
        OneSignal.appId = appId;
        if (OneSignal.appContext == null) {
            OneSignal.logger.warning("appId set, but please call initWithContext(appContext) with Application context to complete OneSignal init!");
            return;
        }
        final WeakReference<Activity> appActivity = OneSignal.appActivity;
        if (appActivity != null && appActivity.get() != null) {
            init((Context)OneSignal.appActivity.get());
        }
        else {
            init(OneSignal.appContext);
        }
    }
    
    public static void setEmail(final String s) {
        setEmail(s, null, null);
    }
    
    public static void setEmail(final String s, final EmailUpdateHandler emailUpdateHandler) {
        setEmail(s, null, emailUpdateHandler);
    }
    
    public static void setEmail(final String s, final String s2) {
        setEmail(s, s2, null);
    }
    
    public static void setEmail(String lowerCase, final String s, final EmailUpdateHandler emailUpdateHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("setEmail()")) {
            OneSignal.logger.error("Waiting for remote params. Moving setEmail() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(lowerCase, s, emailUpdateHandler) {
                final EmailUpdateHandler val$callback;
                final String val$email;
                final String val$emailAuthHash;
                
                public void run() {
                    OneSignal.logger.debug("Running setEmail() operation from a pending task queue.");
                    OneSignal.setEmail(this.val$email, this.val$emailAuthHash, this.val$callback);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("setEmail()")) {
            return;
        }
        if (!OSUtils.isValidEmail(lowerCase)) {
            if (emailUpdateHandler != null) {
                emailUpdateHandler.onFailure(new EmailUpdateError(EmailErrorType.VALIDATION, "Email is invalid"));
            }
            OneSignal.logger.error("Email is invalid");
            return;
        }
        if (getRemoteParams().useEmailAuth && (s == null || s.length() == 0)) {
            if (emailUpdateHandler != null) {
                emailUpdateHandler.onFailure(new EmailUpdateError(EmailErrorType.REQUIRES_EMAIL_AUTH, "Email authentication (auth token) is set to REQUIRED for this application. Please provide an auth token from your backend server or change the setting in the OneSignal dashboard."));
            }
            OneSignal.logger.error("Email authentication (auth token) is set to REQUIRED for this application. Please provide an auth token from your backend server or change the setting in the OneSignal dashboard.");
            return;
        }
        OneSignal.emailUpdateHandler = emailUpdateHandler;
        final String trim = lowerCase.trim();
        if ((lowerCase = s) != null) {
            lowerCase = s.toLowerCase();
        }
        getCurrentEmailSubscriptionState(OneSignal.appContext).setEmailAddress(trim);
        OneSignalStateSynchronizer.setEmail(trim.toLowerCase(), lowerCase);
    }
    
    public static void setExternalUserId(final String s) {
        setExternalUserId(s, null, null);
    }
    
    public static void setExternalUserId(final String s, final OSExternalUserIdUpdateCompletionHandler osExternalUserIdUpdateCompletionHandler) {
        setExternalUserId(s, null, osExternalUserIdUpdateCompletionHandler);
    }
    
    public static void setExternalUserId(final String s, final String s2) {
        setExternalUserId(s, s2, null);
    }
    
    public static void setExternalUserId(String s, final String s2, final OSExternalUserIdUpdateCompletionHandler osExternalUserIdUpdateCompletionHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("setExternalUserId()")) {
            OneSignal.logger.error("Waiting for remote params. Moving setExternalUserId() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(s, s2, osExternalUserIdUpdateCompletionHandler) {
                final OSExternalUserIdUpdateCompletionHandler val$completionCallback;
                final String val$externalId;
                final String val$externalIdAuthHash;
                
                public void run() {
                    OneSignal.logger.debug("Running setExternalUserId() operation from pending task queue.");
                    OneSignal.setExternalUserId(this.val$externalId, this.val$externalIdAuthHash, this.val$completionCallback);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("setExternalUserId()")) {
            return;
        }
        if (s == null) {
            OneSignal.logger.warning("External id can't be null, set an empty string to remove an external id");
            return;
        }
        if (!s.isEmpty() && getRemoteParams() != null && getRemoteParams().useUserIdAuth && (s2 == null || s2.length() == 0)) {
            if (osExternalUserIdUpdateCompletionHandler != null) {
                osExternalUserIdUpdateCompletionHandler.onFailure(new ExternalIdError(ExternalIdErrorType.REQUIRES_EXTERNAL_ID_AUTH, "External Id authentication (auth token) is set to REQUIRED for this application. Please provide an auth token from your backend server or change the setting in the OneSignal dashboard."));
            }
            OneSignal.logger.error("External Id authentication (auth token) is set to REQUIRED for this application. Please provide an auth token from your backend server or change the setting in the OneSignal dashboard.");
            return;
        }
        String lowerCase;
        if ((lowerCase = s2) != null) {
            lowerCase = s2.toLowerCase();
        }
        try {
            OneSignalStateSynchronizer.setExternalUserId(s, lowerCase, osExternalUserIdUpdateCompletionHandler);
        }
        catch (final JSONException ex) {
            if (s.equals((Object)"")) {
                s = "remove";
            }
            else {
                s = "set";
            }
            final OSLogger logger = OneSignal.logger;
            final StringBuilder sb = new StringBuilder("Attempted to ");
            sb.append(s);
            sb.append(" external ID but encountered a JSON exception");
            logger.error(sb.toString());
            ex.printStackTrace();
        }
    }
    
    public static void setInAppMessageClickHandler(final OSInAppMessageClickHandler inAppMessageClickHandler) {
        OneSignal.inAppMessageClickHandler = inAppMessageClickHandler;
    }
    
    public static void setInAppMessageLifecycleHandler(final OSInAppMessageLifecycleHandler inAppMessageLifecycleHandler) {
        if (OneSignal.appContext == null) {
            OneSignal.logger.error("Waiting initWithContext. Moving setInAppMessageLifecycleHandler() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(inAppMessageLifecycleHandler) {
                final OSInAppMessageLifecycleHandler val$handler;
                
                public void run() {
                    OneSignal.logger.debug("Running setInAppMessageLifecycleHandler() operation from pending queue.");
                    OneSignal.setInAppMessageLifecycleHandler(this.val$handler);
                }
            });
            return;
        }
        getInAppMessageController().setInAppMessageLifecycleHandler(inAppMessageLifecycleHandler);
    }
    
    static void setInForeground(final boolean inForeground) {
        OneSignal.inForeground = inForeground;
    }
    
    public static void setLanguage(final String s) {
        setLanguage(s, null);
    }
    
    public static void setLanguage(final String language, final OSSetLanguageCompletionHandler osSetLanguageCompletionHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("setLanguage()")) {
            OneSignal.logger.error("Waiting for remote params. Moving setLanguage() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(language, osSetLanguageCompletionHandler) {
                final OSSetLanguageCompletionHandler val$completionCallback;
                final String val$language;
                
                public void run() {
                    OneSignal.logger.debug("Running setLanguage() operation from pending task queue.");
                    OneSignal.setLanguage(this.val$language, this.val$completionCallback);
                }
            });
            return;
        }
        Object o;
        if (osSetLanguageCompletionHandler != null) {
            o = new OneSignal$16(osSetLanguageCompletionHandler);
        }
        else {
            o = null;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("setLanguage()")) {
            return;
        }
        final LanguageProviderAppDefined strategy = new LanguageProviderAppDefined(OneSignal.preferences);
        strategy.setLanguage(language);
        OneSignal.languageContext.setStrategy((LanguageProvider)strategy);
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("language", (Object)OneSignal.languageContext.getLanguage());
            OneSignalStateSynchronizer.updateDeviceInfo(jsonObject, (OneSignalStateSynchronizer.OSDeviceInfoCompletionHandler)o);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    static void setLastSessionTime(final long n) {
        final OSLogger logger = OneSignal.logger;
        final StringBuilder sb = new StringBuilder("Last session time set to: ");
        sb.append(n);
        logger.debug(sb.toString());
        OneSignalPrefs.saveLong(OneSignalPrefs.PREFS_ONESIGNAL, "OS_LAST_SESSION_TIME", n);
    }
    
    public static void setLocationShared(final boolean b) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("setLocationShared()")) {
            OneSignal.logger.error("Waiting for remote params. Moving setLocationShared() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(b) {
                final boolean val$enable;
                
                public void run() {
                    OneSignal.logger.debug("Running setLocationShared() operation from pending task queue.");
                    OneSignal.setLocationShared(this.val$enable);
                }
            });
            return;
        }
        if (getRemoteParamController().hasLocationKey()) {
            return;
        }
        startLocationShared(b);
    }
    
    public static void setLogLevel(final int n, final int n2) {
        setLogLevel(getLogLevel(n), getLogLevel(n2));
    }
    
    public static void setLogLevel(final LOG_LEVEL logCatLevel, final LOG_LEVEL visualLogLevel) {
        OneSignal.logCatLevel = logCatLevel;
        OneSignal.visualLogLevel = visualLogLevel;
    }
    
    public static void setNotificationOpenedHandler(final OSNotificationOpenedHandler notificationOpenedHandler) {
        OneSignal.notificationOpenedHandler = notificationOpenedHandler;
        if (OneSignal.initDone && notificationOpenedHandler != null) {
            fireCallbackForOpenedNotifications();
        }
    }
    
    public static void setNotificationWillShowInForegroundHandler(final OSNotificationWillShowInForegroundHandler notificationWillShowInForegroundHandler) {
        OneSignal.notificationWillShowInForegroundHandler = notificationWillShowInForegroundHandler;
    }
    
    static void setRemoteNotificationReceivedHandler(final OSRemoteNotificationReceivedHandler remoteNotificationReceivedHandler) {
        if (OneSignal.remoteNotificationReceivedHandler == null) {
            OneSignal.remoteNotificationReceivedHandler = remoteNotificationReceivedHandler;
        }
    }
    
    public static void setRequiresUserPrivacyConsent(final boolean b) {
        if (getRemoteParamController().hasPrivacyConsentKey()) {
            OneSignal.logger.warning("setRequiresUserPrivacyConsent already called by remote params!, ignoring user set");
            return;
        }
        if (requiresUserPrivacyConsent() && !b) {
            Log(LOG_LEVEL.ERROR, "Cannot change requiresUserPrivacyConsent() from TRUE to FALSE");
            return;
        }
        getRemoteParamController().savePrivacyConsentRequired(b);
    }
    
    public static void setSMSNumber(final String s) {
        setSMSNumber(s, null, null);
    }
    
    public static void setSMSNumber(final String s, final OSSMSUpdateHandler ossmsUpdateHandler) {
        setSMSNumber(s, null, ossmsUpdateHandler);
    }
    
    public static void setSMSNumber(final String s, final String s2) {
        setSMSNumber(s, s2, null);
    }
    
    public static void setSMSNumber(final String smsNumber, final String s, final OSSMSUpdateHandler smsUpdateHandler) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("setSMSNumber()")) {
            OneSignal.logger.error("Waiting for remote params. Moving setSMSNumber() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(smsNumber, s, smsUpdateHandler) {
                final OSSMSUpdateHandler val$callback;
                final String val$smsAuthHash;
                final String val$smsNumber;
                
                public void run() {
                    OneSignal.logger.debug("Running setSMSNumber() operation from a pending task queue.");
                    OneSignal.setSMSNumber(this.val$smsNumber, this.val$smsAuthHash, this.val$callback);
                }
            });
            return;
        }
        if (shouldLogUserPrivacyConsentErrorMessageForMethodName("setSMSNumber()")) {
            return;
        }
        if (TextUtils.isEmpty((CharSequence)smsNumber)) {
            if (smsUpdateHandler != null) {
                smsUpdateHandler.onFailure(new OSSMSUpdateError(SMSErrorType.VALIDATION, "SMS number is invalid"));
            }
            OneSignal.logger.error("SMS number is invalid");
            return;
        }
        if (getRemoteParams().useSMSAuth && (s == null || s.length() == 0)) {
            if (smsUpdateHandler != null) {
                smsUpdateHandler.onFailure(new OSSMSUpdateError(SMSErrorType.REQUIRES_SMS_AUTH, "SMS authentication (auth token) is set to REQUIRED for this application. Please provide an auth token from your backend server or change the setting in the OneSignal dashboard."));
            }
            OneSignal.logger.error("SMS authentication (auth token) is set to REQUIRED for this application. Please provide an auth token from your backend server or change the setting in the OneSignal dashboard.");
            return;
        }
        OneSignal.smsUpdateHandler = smsUpdateHandler;
        getCurrentSMSSubscriptionState(OneSignal.appContext).setSMSNumber(smsNumber);
        OneSignalStateSynchronizer.setSMSNumber(smsNumber, s);
    }
    
    static void setSessionManager(final OSSessionManager sessionManager) {
        OneSignal.sessionManager = sessionManager;
    }
    
    static void setSharedPreferences(final OSSharedPreferences preferences) {
        OneSignal.preferences = preferences;
    }
    
    static void setTime(final OSTime time) {
        OneSignal.time = time;
    }
    
    static void setTrackerFactory(final OSTrackerFactory trackerFactory) {
        OneSignal.trackerFactory = trackerFactory;
    }
    
    private static void setupContextListeners(final boolean b) {
        ActivityLifecycleListener.registerActivityLifecycleCallbacks((Application)OneSignal.appContext);
        if (b) {
            OneSignal.languageContext = new LanguageContext(OneSignal.preferences);
            OneSignalPrefs.startDelayedWrite();
            final OneSignalDbHelper dbHelperInstance = getDBHelperInstance();
            (OneSignal.notificationDataController = new OSNotificationDataController(dbHelperInstance, OneSignal.logger)).cleanOldCachedData();
            getInAppMessageController().cleanCachedInAppMessages();
            if (OneSignal.outcomeEventsFactory == null) {
                OneSignal.outcomeEventsFactory = new OSOutcomeEventsFactory(OneSignal.logger, OneSignal.apiClient, (OneSignalDb)dbHelperInstance, OneSignal.preferences);
            }
            OneSignal.sessionManager.initSessionFromCache();
            getOutcomeEventsController().cleanCachedUniqueOutcomes();
        }
    }
    
    private static void setupPrivacyConsent(final Context context) {
        try {
            final String string = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString("com.onesignal.PrivacyConsent");
            if (string != null) {
                setRequiresUserPrivacyConsent("ENABLE".equalsIgnoreCase(string));
            }
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    static boolean shouldFireForegroundHandlers(final OSNotificationGenerationJob osNotificationGenerationJob) {
        if (!isInForeground()) {
            onesignalLog(LOG_LEVEL.INFO, "App is in background, show notification");
            return false;
        }
        if (OneSignal.notificationWillShowInForegroundHandler == null) {
            onesignalLog(LOG_LEVEL.INFO, "No NotificationWillShowInForegroundHandler setup, show notification");
            return false;
        }
        if (osNotificationGenerationJob.isRestoring()) {
            onesignalLog(LOG_LEVEL.INFO, "Not firing notificationWillShowInForegroundHandler for restored notifications");
            return false;
        }
        return true;
    }
    
    private static boolean shouldInitDirectSessionFromNotificationOpen(final Activity activity, final JSONArray jsonArray) {
        if (OneSignal.inForeground) {
            return false;
        }
        try {
            return new OSNotificationOpenBehaviorFromPushPayload((Context)activity, jsonArray.getJSONObject(0)).getShouldOpenApp();
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return true;
        }
    }
    
    static boolean shouldLogUserPrivacyConsentErrorMessageForMethodName(final String s) {
        if (requiresUserPrivacyConsent()) {
            if (s != null) {
                final LOG_LEVEL warn = LOG_LEVEL.WARN;
                final StringBuilder sb = new StringBuilder("Method ");
                sb.append(s);
                sb.append(" was called before the user provided privacy consent. Your application is set to require the user's privacy consent before the OneSignal SDK can be initialized. Please ensure the user has provided consent before calling this method. You can check the latest OneSignal consent status by calling OneSignal.userProvidedPrivacyConsent()");
                Log(warn, sb.toString());
            }
            return true;
        }
        return false;
    }
    
    private static boolean shouldStartNewSession() {
        return isInForeground() && isPastOnSessionTime();
    }
    
    static void startLocationShared(final boolean b) {
        final OSLogger logger = OneSignal.logger;
        final StringBuilder sb = new StringBuilder("OneSignal startLocationShared: ");
        sb.append(b);
        logger.debug(sb.toString());
        getRemoteParamController().saveLocationShared(b);
        if (!b) {
            OneSignal.logger.debug("OneSignal is shareLocation set false, clearing last location!");
            OneSignalStateSynchronizer.clearLocation();
        }
    }
    
    private static void startLocationUpdate() {
        LocationController.getLocation(OneSignal.appContext, false, false, (LocationController.LocationHandler)new OneSignal$5());
    }
    
    private static void startRegistrationOrOnSession() {
        if (OneSignal.waitingToPostStateSync) {
            return;
        }
        OneSignal.waitingToPostStateSync = true;
        if (OneSignal.inForeground && OneSignalStateSynchronizer.getSyncAsNewSession()) {
            OneSignal.locationFired = false;
        }
        startLocationUpdate();
        OneSignal.registerForPushFired = false;
        if (getRemoteParams() != null) {
            registerForPushToken();
        }
        else {
            makeAndroidParamsRequest(OneSignal.appId, getUserId(), true);
        }
    }
    
    public static void unsubscribeWhenNotificationsAreDisabled(final boolean b) {
        if (OneSignal.taskRemoteController.shouldQueueTaskForInit("unsubscribeWhenNotificationsAreDisabled()")) {
            OneSignal.logger.error("Waiting for remote params. Moving unsubscribeWhenNotificationsAreDisabled() operation to a pending task queue.");
            OneSignal.taskRemoteController.addTaskToQueue((Runnable)new Runnable(b) {
                final boolean val$set;
                
                public void run() {
                    OneSignal.logger.debug("Running unsubscribeWhenNotificationsAreDisabled() operation from pending task queue.");
                    OneSignal.unsubscribeWhenNotificationsAreDisabled(this.val$set);
                }
            });
            return;
        }
        if (getRemoteParamController().hasUnsubscribeNotificationKey()) {
            OneSignal.logger.warning("unsubscribeWhenNotificationsAreDisabled already called by remote params!, ignoring user set");
            return;
        }
        getRemoteParamController().saveUnsubscribeWhenNotificationsAreDisabled(b);
    }
    
    static void updateEmailIdDependents(final String emailUserId) {
        saveEmailId(emailUserId);
        getCurrentEmailSubscriptionState(OneSignal.appContext).setEmailUserId(emailUserId);
        try {
            OneSignalStateSynchronizer.updatePushState(new JSONObject().put("parent_player_id", (Object)emailUserId));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    static void updateSMSIdDependents(final String smsUserId) {
        saveSMSId(smsUserId);
        getCurrentSMSSubscriptionState(OneSignal.appContext).setSMSUserId(smsUserId);
    }
    
    static void updateUserIdDependents(final String userId) {
        saveUserId(userId);
        internalFireGetTagsCallbacks();
        getCurrentSubscriptionState(OneSignal.appContext).setUserId(userId);
        final IAPUpdateJob iapUpdateJob = OneSignal.iapUpdateJob;
        if (iapUpdateJob != null) {
            sendPurchases(iapUpdateJob.toReport, OneSignal.iapUpdateJob.newAsExisting, OneSignal.iapUpdateJob.restResponseHandler);
            OneSignal.iapUpdateJob = null;
        }
        OneSignalStateSynchronizer.refreshSecondaryChannelState();
    }
    
    public static boolean userProvidedPrivacyConsent() {
        return OneSignal.remoteParamController.getSavedUserConsentStatus();
    }
    
    public enum AppEntryAction
    {
        private static final AppEntryAction[] $VALUES;
        
        APP_CLOSE, 
        APP_OPEN, 
        NOTIFICATION_CLICK;
        
        public boolean isAppClose() {
            return this.equals((Object)AppEntryAction.APP_CLOSE);
        }
        
        public boolean isAppOpen() {
            return this.equals((Object)AppEntryAction.APP_OPEN);
        }
        
        public boolean isNotificationClick() {
            return this.equals((Object)AppEntryAction.NOTIFICATION_CLICK);
        }
    }
    
    public interface ChangeTagsUpdateHandler
    {
        void onFailure(final SendTagsError p0);
        
        void onSuccess(final JSONObject p0);
    }
    
    public enum EmailErrorType
    {
        private static final EmailErrorType[] $VALUES;
        
        INVALID_OPERATION, 
        NETWORK, 
        REQUIRES_EMAIL_AUTH, 
        VALIDATION;
    }
    
    public static class EmailUpdateError
    {
        private String message;
        private EmailErrorType type;
        
        EmailUpdateError(final EmailErrorType type, final String message) {
            this.type = type;
            this.message = message;
        }
        
        public String getMessage() {
            return this.message;
        }
        
        public EmailErrorType getType() {
            return this.type;
        }
    }
    
    public interface EmailUpdateHandler
    {
        void onFailure(final EmailUpdateError p0);
        
        void onSuccess();
    }
    
    interface EntryStateListener
    {
        void onEntryStateChange(final AppEntryAction p0);
    }
    
    public static class ExternalIdError
    {
        private String message;
        private ExternalIdErrorType type;
        
        ExternalIdError(final ExternalIdErrorType type, final String message) {
            this.type = type;
            this.message = message;
        }
        
        public String getMessage() {
            return this.message;
        }
        
        public ExternalIdErrorType getType() {
            return this.type;
        }
    }
    
    public enum ExternalIdErrorType
    {
        private static final ExternalIdErrorType[] $VALUES;
        
        INVALID_OPERATION, 
        NETWORK, 
        REQUIRES_EXTERNAL_ID_AUTH;
    }
    
    private static class IAPUpdateJob
    {
        boolean newAsExisting;
        OneSignalRestClient.ResponseHandler restResponseHandler;
        JSONArray toReport;
        
        IAPUpdateJob(final JSONArray toReport) {
            this.toReport = toReport;
        }
    }
    
    public enum LOG_LEVEL
    {
        private static final LOG_LEVEL[] $VALUES;
        
        DEBUG, 
        ERROR, 
        FATAL, 
        INFO, 
        NONE, 
        VERBOSE, 
        WARN;
    }
    
    public interface OSExternalUserIdUpdateCompletionHandler
    {
        void onFailure(final ExternalIdError p0);
        
        void onSuccess(final JSONObject p0);
    }
    
    public interface OSGetTagsHandler
    {
        void tagsAvailable(final JSONObject p0);
    }
    
    public interface OSInAppMessageClickHandler
    {
        void inAppMessageClicked(final OSInAppMessageAction p0);
    }
    
    interface OSInternalExternalUserIdUpdateCompletionHandler
    {
        void onComplete(final String p0, final boolean p1);
    }
    
    public static class OSLanguageError
    {
        private int errorCode;
        private String message;
        
        OSLanguageError(final int errorCode, final String message) {
            this.errorCode = errorCode;
            this.message = message;
        }
        
        public int getCode() {
            return this.errorCode;
        }
        
        public String getMessage() {
            return this.message;
        }
    }
    
    public interface OSNotificationOpenedHandler
    {
        void notificationOpened(final OSNotificationOpenedResult p0);
    }
    
    public interface OSNotificationWillShowInForegroundHandler
    {
        void notificationWillShowInForeground(final OSNotificationReceivedEvent p0);
    }
    
    interface OSPromptActionCompletionCallback
    {
        void onCompleted(final PromptActionResult p0);
    }
    
    public interface OSRemoteNotificationReceivedHandler
    {
        void remoteNotificationReceived(final Context p0, final OSNotificationReceivedEvent p1);
    }
    
    public static class OSSMSUpdateError
    {
        private String message;
        private SMSErrorType type;
        
        OSSMSUpdateError(final SMSErrorType type, final String message) {
            this.type = type;
            this.message = message;
        }
        
        public String getMessage() {
            return this.message;
        }
        
        public SMSErrorType getType() {
            return this.type;
        }
    }
    
    public interface OSSMSUpdateHandler
    {
        void onFailure(final OSSMSUpdateError p0);
        
        void onSuccess(final JSONObject p0);
    }
    
    public interface OSSetLanguageCompletionHandler
    {
        void onFailure(final OSLanguageError p0);
        
        void onSuccess(final String p0);
    }
    
    public interface OutcomeCallback
    {
        void onSuccess(final OSOutcomeEvent p0);
    }
    
    public interface PostNotificationResponseHandler
    {
        void onFailure(final JSONObject p0);
        
        void onSuccess(final JSONObject p0);
    }
    
    enum PromptActionResult
    {
        private static final PromptActionResult[] $VALUES;
        
        ERROR, 
        LOCATION_PERMISSIONS_MISSING_MANIFEST, 
        PERMISSION_DENIED, 
        PERMISSION_GRANTED;
    }
    
    public interface PromptForPushNotificationPermissionResponseHandler
    {
        void response(final boolean p0);
    }
    
    public enum SMSErrorType
    {
        private static final SMSErrorType[] $VALUES;
        
        INVALID_OPERATION, 
        NETWORK, 
        REQUIRES_SMS_AUTH, 
        VALIDATION;
    }
    
    public static class SendTagsError
    {
        private int code;
        private String message;
        
        SendTagsError(final int code, final String message) {
            this.message = message;
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getMessage() {
            return this.message;
        }
    }
}
