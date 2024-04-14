package com.onesignal;

import android.os.Handler;
import android.os.HandlerThread;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashMap;
import java.util.Queue;

abstract class UserStateSynchronizer
{
    protected static final String ANDROID_PERMISSION = "androidPermission";
    static final String APP_ID = "app_id";
    private static final String CURRENT_STATE = "CURRENT_STATE";
    protected static final String DEVICE_PLAYER_ID = "device_player_id";
    protected static final String DEVICE_TYPE = "device_type";
    static final String EMAIL_AUTH_HASH_KEY = "email_auth_hash";
    protected static final String EMAIL_KEY = "email";
    private static final String ERRORS = "errors";
    protected static final String EXTERNAL_USER_ID = "external_user_id";
    static final String EXTERNAL_USER_ID_AUTH_HASH = "external_user_id_auth_hash";
    private static final String ID = "id";
    protected static final String IDENTIFIER = "identifier";
    protected static final String LANGUAGE = "language";
    protected static final String LOGOUT_EMAIL = "logoutEmail";
    protected static final String PARENT_PLAYER_ID = "parent_player_id";
    private static final String SESSION = "session";
    static final String SMS_AUTH_HASH_KEY = "sms_auth_hash";
    protected static final String SMS_NUMBER_KEY = "sms_number";
    protected static final String SUBSCRIBABLE_STATUS = "subscribableStatus";
    protected static final String TAGS = "tags";
    private static final String TOSYNC_STATE = "TOSYNC_STATE";
    protected static final String USER_SUBSCRIBE_PREF = "userSubscribePref";
    protected final Object LOCK;
    private boolean canMakeUpdates;
    private OneSignalStateSynchronizer$UserStateSynchronizerType channel;
    private UserState currentUserState;
    private final Queue<OneSignalStateSynchronizer$OSDeviceInfoCompletionHandler> deviceInfoCompletionHandler;
    private final Queue<OneSignal$OSInternalExternalUserIdUpdateCompletionHandler> externalUserIdUpdateHandlers;
    private final Object networkHandlerSyncLock;
    HashMap<Integer, NetworkHandlerThread> networkHandlerThreads;
    private AtomicBoolean runningSyncUserState;
    private final Queue<OneSignal$ChangeTagsUpdateHandler> sendTagsHandlers;
    private UserState toSyncUserState;
    protected boolean waitingForSessionResponse;
    
    UserStateSynchronizer(final OneSignalStateSynchronizer$UserStateSynchronizerType channel) {
        this.LOCK = new Object();
        this.runningSyncUserState = new AtomicBoolean();
        this.sendTagsHandlers = (Queue<OneSignal$ChangeTagsUpdateHandler>)new ConcurrentLinkedQueue();
        this.externalUserIdUpdateHandlers = (Queue<OneSignal$OSInternalExternalUserIdUpdateCompletionHandler>)new ConcurrentLinkedQueue();
        this.deviceInfoCompletionHandler = (Queue<OneSignalStateSynchronizer$OSDeviceInfoCompletionHandler>)new ConcurrentLinkedQueue();
        this.networkHandlerThreads = (HashMap<Integer, NetworkHandlerThread>)new HashMap();
        this.networkHandlerSyncLock = new Object() {
            final UserStateSynchronizer this$0;
        };
        this.waitingForSessionResponse = false;
        this.channel = channel;
    }
    
    private void deviceInfoHandlersPerformOnFailure(final OneSignalStateSynchronizer$OSDeviceInfoError oneSignalStateSynchronizer$OSDeviceInfoError) {
        while (true) {
            final OneSignalStateSynchronizer$OSDeviceInfoCompletionHandler oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler = (OneSignalStateSynchronizer$OSDeviceInfoCompletionHandler)this.deviceInfoCompletionHandler.poll();
            if (oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler == null) {
                break;
            }
            oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler.onFailure(oneSignalStateSynchronizer$OSDeviceInfoError);
        }
    }
    
    private void deviceInfoHandlersPerformOnSuccess() {
        final String language = OneSignalStateSynchronizer.getLanguage();
        while (true) {
            final OneSignalStateSynchronizer$OSDeviceInfoCompletionHandler oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler = (OneSignalStateSynchronizer$OSDeviceInfoCompletionHandler)this.deviceInfoCompletionHandler.poll();
            if (oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler == null) {
                break;
            }
            oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler.onSuccess(language);
        }
    }
    
    private void doCreateOrNewSession(final String s, final JSONObject jsonObject, final JSONObject jsonObject2) {
        String string;
        if (s == null) {
            string = "players";
        }
        else {
            final StringBuilder sb = new StringBuilder("players/");
            sb.append(s);
            sb.append("/on_session");
            string = sb.toString();
        }
        this.waitingForSessionResponse = true;
        this.addOnSessionOrCreateExtras(jsonObject);
        OneSignalRestClient.postSync(string, jsonObject, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, jsonObject2, jsonObject, s) {
            final UserStateSynchronizer this$0;
            final JSONObject val$dependDiff;
            final JSONObject val$jsonBody;
            final String val$userId;
            
            void onFailure(final int n, final String s, final Throwable t) {
                final Object lock = this.this$0.LOCK;
                synchronized (lock) {
                    this.this$0.waitingForSessionResponse = false;
                    final OneSignal$LOG_LEVEL warn = OneSignal$LOG_LEVEL.WARN;
                    final StringBuilder sb = new StringBuilder("Failed last request. statusCode: ");
                    sb.append(n);
                    sb.append("\nresponse: ");
                    sb.append(s);
                    OneSignal.Log(warn, sb.toString());
                    if (this.this$0.response400WithErrorsContaining(n, s, "not a valid device_type")) {
                        this.this$0.handlePlayerDeletedFromServer();
                    }
                    else {
                        this.this$0.handleNetworkFailure(n);
                    }
                }
            }
            
            void onSuccess(final String s) {
                final Object lock = this.this$0.LOCK;
                synchronized (lock) {
                    this.this$0.waitingForSessionResponse = false;
                    this.this$0.getCurrentUserState().persistStateAfterSync(this.val$dependDiff, this.val$jsonBody);
                    try {
                        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
                        final StringBuilder sb = new StringBuilder("doCreateOrNewSession:response: ");
                        sb.append(s);
                        OneSignal.onesignalLog(debug, sb.toString());
                        final JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.has("id")) {
                            final String optString = jsonObject.optString("id");
                            this.this$0.updateIdDependents(optString);
                            final OneSignal$LOG_LEVEL info = OneSignal$LOG_LEVEL.INFO;
                            final StringBuilder sb2 = new StringBuilder("Device registered, UserId = ");
                            sb2.append(optString);
                            OneSignal.Log(info, sb2.toString());
                        }
                        else {
                            final OneSignal$LOG_LEVEL info2 = OneSignal$LOG_LEVEL.INFO;
                            final StringBuilder sb3 = new StringBuilder("session sent, UserId = ");
                            sb3.append(this.val$userId);
                            OneSignal.Log(info2, sb3.toString());
                        }
                        this.this$0.getUserStateForModification().putOnDependValues("session", false);
                        this.this$0.getUserStateForModification().persistState();
                        if (jsonObject.has("in_app_messages")) {
                            OneSignal.getInAppMessageController().receivedInAppMessageJson(jsonObject.getJSONArray("in_app_messages"));
                        }
                        this.this$0.onSuccessfulSync(this.val$jsonBody);
                    }
                    catch (final JSONException ex) {
                        OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, "ERROR parsing on_session or create JSON Response.", (Throwable)ex);
                    }
                }
            }
        });
    }
    
    private void doEmailLogout(String string) {
        final StringBuilder sb = new StringBuilder("players/");
        sb.append(string);
        sb.append("/email_logout");
        string = sb.toString();
        final JSONObject jsonObject = new JSONObject();
        try {
            final ImmutableJSONObject dependValues = this.getCurrentUserState().getDependValues();
            if (dependValues.has("email_auth_hash")) {
                jsonObject.put("email_auth_hash", (Object)dependValues.optString("email_auth_hash"));
            }
            final ImmutableJSONObject syncValues = this.getCurrentUserState().getSyncValues();
            if (syncValues.has("parent_player_id")) {
                jsonObject.put("parent_player_id", (Object)syncValues.optString("parent_player_id"));
            }
            jsonObject.put("app_id", (Object)syncValues.optString("app_id"));
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        OneSignalRestClient.postSync(string, jsonObject, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this) {
            final UserStateSynchronizer this$0;
            
            void onFailure(final int n, final String s, final Throwable t) {
                final OneSignal$LOG_LEVEL warn = OneSignal$LOG_LEVEL.WARN;
                final StringBuilder sb = new StringBuilder("Failed last request. statusCode: ");
                sb.append(n);
                sb.append("\nresponse: ");
                sb.append(s);
                OneSignal.Log(warn, sb.toString());
                if (this.this$0.response400WithErrorsContaining(n, s, "already logged out of email")) {
                    this.this$0.logoutEmailSyncSuccess();
                    return;
                }
                if (this.this$0.response400WithErrorsContaining(n, s, "not a valid device_type")) {
                    this.this$0.handlePlayerDeletedFromServer();
                }
                else {
                    this.this$0.handleNetworkFailure(n);
                }
            }
            
            void onSuccess(final String s) {
                this.this$0.logoutEmailSyncSuccess();
            }
        });
    }
    
    private void doPutSync(final String s, final JSONObject jsonObject, final JSONObject jsonObject2) {
        if (s == null) {
            OneSignal.onesignalLog(this.getLogLevel(), "Error updating the user record because of the null user id");
            this.sendTagsHandlersPerformOnFailure(new OneSignal$SendTagsError(-1, "Unable to update tags: the current user is not registered with OneSignal"));
            this.externalUserIdUpdateHandlersPerformOnFailure();
            this.deviceInfoHandlersPerformOnFailure(new OneSignalStateSynchronizer$OSDeviceInfoError(-1, "Unable to set Language: the current user is not registered with OneSignal"));
            return;
        }
        final StringBuilder sb = new StringBuilder("players/");
        sb.append(s);
        OneSignalRestClient.putSync(sb.toString(), jsonObject, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, jsonObject, jsonObject2) {
            final UserStateSynchronizer this$0;
            final JSONObject val$dependDiff;
            final JSONObject val$jsonBody;
            
            void onFailure(final int n, final String s, final Throwable t) {
                final OneSignal$LOG_LEVEL error = OneSignal$LOG_LEVEL.ERROR;
                final StringBuilder sb = new StringBuilder("Failed PUT sync request with status code: ");
                sb.append(n);
                sb.append(" and response: ");
                sb.append(s);
                OneSignal.Log(error, sb.toString());
                final Object lock = this.this$0.LOCK;
                synchronized (lock) {
                    if (this.this$0.response400WithErrorsContaining(n, s, "No user with this id found")) {
                        this.this$0.handlePlayerDeletedFromServer();
                    }
                    else {
                        this.this$0.handleNetworkFailure(n);
                    }
                    monitorexit(lock);
                    if (this.val$jsonBody.has("tags")) {
                        this.this$0.sendTagsHandlersPerformOnFailure(new OneSignal$SendTagsError(n, s));
                    }
                    if (this.val$jsonBody.has("external_user_id")) {
                        final OneSignal$LOG_LEVEL error2 = OneSignal$LOG_LEVEL.ERROR;
                        final StringBuilder sb2 = new StringBuilder("Error setting external user id for push with status code: ");
                        sb2.append(n);
                        sb2.append(" and message: ");
                        sb2.append(s);
                        OneSignal.onesignalLog(error2, sb2.toString());
                        this.this$0.externalUserIdUpdateHandlersPerformOnFailure();
                    }
                    if (this.val$jsonBody.has("language")) {
                        this.this$0.deviceInfoHandlersPerformOnFailure(new OneSignalStateSynchronizer$OSDeviceInfoError(n, s));
                    }
                }
            }
            
            void onSuccess(final String s) {
                final Object lock = this.this$0.LOCK;
                synchronized (lock) {
                    this.this$0.getCurrentUserState().persistStateAfterSync(this.val$dependDiff, this.val$jsonBody);
                    this.this$0.onSuccessfulSync(this.val$jsonBody);
                    monitorexit(lock);
                    if (this.val$jsonBody.has("tags")) {
                        this.this$0.sendTagsHandlersPerformOnSuccess();
                    }
                    if (this.val$jsonBody.has("external_user_id")) {
                        this.this$0.externalUserIdUpdateHandlersPerformOnSuccess();
                    }
                    if (this.val$jsonBody.has("language")) {
                        this.this$0.deviceInfoHandlersPerformOnSuccess();
                    }
                }
            }
        });
    }
    
    private void externalUserIdUpdateHandlersPerformOnFailure() {
        while (true) {
            final OneSignal$OSInternalExternalUserIdUpdateCompletionHandler oneSignal$OSInternalExternalUserIdUpdateCompletionHandler = (OneSignal$OSInternalExternalUserIdUpdateCompletionHandler)this.externalUserIdUpdateHandlers.poll();
            if (oneSignal$OSInternalExternalUserIdUpdateCompletionHandler == null) {
                break;
            }
            oneSignal$OSInternalExternalUserIdUpdateCompletionHandler.onComplete(this.getChannelString(), false);
        }
    }
    
    private void externalUserIdUpdateHandlersPerformOnSuccess() {
        while (true) {
            final OneSignal$OSInternalExternalUserIdUpdateCompletionHandler oneSignal$OSInternalExternalUserIdUpdateCompletionHandler = (OneSignal$OSInternalExternalUserIdUpdateCompletionHandler)this.externalUserIdUpdateHandlers.poll();
            if (oneSignal$OSInternalExternalUserIdUpdateCompletionHandler == null) {
                break;
            }
            oneSignal$OSInternalExternalUserIdUpdateCompletionHandler.onComplete(this.getChannelString(), true);
        }
    }
    
    private void fireNetworkFailureEvents() {
        final JSONObject generateJsonDiff = this.getCurrentUserState().generateJsonDiff(this.toSyncUserState, false);
        if (generateJsonDiff != null) {
            this.fireEventsForUpdateFailure(generateJsonDiff);
        }
        if (this.getToSyncUserState().getDependValues().optBoolean("logoutEmail", false)) {
            OneSignal.handleFailedEmailLogout();
        }
    }
    
    private void handleNetworkFailure(final int n) {
        if (n == 403) {
            OneSignal.Log(OneSignal$LOG_LEVEL.FATAL, "403 error updating player, omitting further retries!");
            this.fireNetworkFailureEvents();
            return;
        }
        if (!this.getNetworkHandlerThread(0).doRetry()) {
            this.fireNetworkFailureEvents();
        }
    }
    
    private void handlePlayerDeletedFromServer() {
        OneSignal.Log(OneSignal$LOG_LEVEL.WARN, "Creating new player based on missing player_id noted above.");
        OneSignal.handleSuccessfulEmailLogout();
        this.resetCurrentState();
        this.updateIdDependents(null);
        this.scheduleSyncToServer();
    }
    
    private void internalSyncUserState(final boolean b) {
        final String id = this.getId();
        if (this.syncEmailLogout() && id != null) {
            this.doEmailLogout(id);
            return;
        }
        if (this.currentUserState == null) {
            this.initUserState();
        }
        final boolean b2 = !b && this.isSessionCall();
        final Object lock = this.LOCK;
        synchronized (lock) {
            final JSONObject generateJsonDiff = this.getCurrentUserState().generateJsonDiff(this.getToSyncUserState(), b2);
            final JSONObject generateJsonDiffFromDependValues = this.getCurrentUserState().generateJsonDiffFromDependValues(this.getToSyncUserState(), null);
            final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("UserStateSynchronizer internalSyncUserState from session call: ");
            sb.append(b2);
            sb.append(" jsonBody: ");
            sb.append((Object)generateJsonDiff);
            OneSignal.onesignalLog(debug, sb.toString());
            if (generateJsonDiff == null) {
                this.getCurrentUserState().persistStateAfterSync(generateJsonDiffFromDependValues, null);
                this.sendTagsHandlersPerformOnSuccess();
                this.externalUserIdUpdateHandlersPerformOnSuccess();
                this.deviceInfoHandlersPerformOnSuccess();
                return;
            }
            this.getToSyncUserState().persistState();
            monitorexit(lock);
            if (!b2) {
                this.doPutSync(id, generateJsonDiff, generateJsonDiffFromDependValues);
            }
            else {
                this.doCreateOrNewSession(id, generateJsonDiff, generateJsonDiffFromDependValues);
            }
        }
    }
    
    private boolean isSessionCall() {
        return (this.getToSyncUserState().getDependValues().optBoolean("session") || this.getId() == null) && !this.waitingForSessionResponse;
    }
    
    private void logoutEmailSyncSuccess() {
        this.getToSyncUserState().removeFromDependValues("logoutEmail");
        this.toSyncUserState.removeFromDependValues("email_auth_hash");
        this.toSyncUserState.removeFromSyncValues("parent_player_id");
        this.toSyncUserState.removeFromSyncValues("email");
        this.toSyncUserState.persistState();
        this.getCurrentUserState().removeFromDependValues("email_auth_hash");
        this.getCurrentUserState().removeFromSyncValues("parent_player_id");
        final String optString = this.getCurrentUserState().getSyncValues().optString("email");
        this.getCurrentUserState().removeFromSyncValues("email");
        OneSignalStateSynchronizer.setNewSessionForEmail();
        final OneSignal$LOG_LEVEL info = OneSignal$LOG_LEVEL.INFO;
        final StringBuilder sb = new StringBuilder("Device successfully logged out of email: ");
        sb.append(optString);
        OneSignal.Log(info, sb.toString());
        OneSignal.handleSuccessfulEmailLogout();
    }
    
    private boolean response400WithErrorsContaining(final int n, final String s, final String s2) {
        final boolean b = false;
        if (n == 400 && s != null) {
            try {
                final JSONObject jsonObject = new JSONObject(s);
                boolean b2 = b;
                if (jsonObject.has("errors")) {
                    final boolean contains = jsonObject.optString("errors").contains((CharSequence)s2);
                    b2 = b;
                    if (contains) {
                        b2 = true;
                    }
                }
                return b2;
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    
    private void sendTagsHandlersPerformOnFailure(final OneSignal$SendTagsError oneSignal$SendTagsError) {
        while (true) {
            final OneSignal$ChangeTagsUpdateHandler oneSignal$ChangeTagsUpdateHandler = (OneSignal$ChangeTagsUpdateHandler)this.sendTagsHandlers.poll();
            if (oneSignal$ChangeTagsUpdateHandler == null) {
                break;
            }
            oneSignal$ChangeTagsUpdateHandler.onFailure(oneSignal$SendTagsError);
        }
    }
    
    private void sendTagsHandlersPerformOnSuccess() {
        final JSONObject result = OneSignalStateSynchronizer.getTags(false).result;
        while (true) {
            final OneSignal$ChangeTagsUpdateHandler oneSignal$ChangeTagsUpdateHandler = (OneSignal$ChangeTagsUpdateHandler)this.sendTagsHandlers.poll();
            if (oneSignal$ChangeTagsUpdateHandler == null) {
                break;
            }
            oneSignal$ChangeTagsUpdateHandler.onSuccess(result);
        }
    }
    
    private boolean syncEmailLogout() {
        return this.getToSyncUserState().getDependValues().optBoolean("logoutEmail", false);
    }
    
    protected abstract void addOnSessionOrCreateExtras(final JSONObject p0);
    
    void clearLocation() {
        this.getToSyncUserState().clearLocation();
        this.getToSyncUserState().persistState();
    }
    
    protected abstract void fireEventsForUpdateFailure(final JSONObject p0);
    
    protected JSONObject generateJsonDiff(JSONObject generateJsonDiff, final JSONObject jsonObject, final JSONObject jsonObject2, final Set<String> set) {
        final Object lock = this.LOCK;
        synchronized (lock) {
            generateJsonDiff = JSONUtils.generateJsonDiff(generateJsonDiff, jsonObject, jsonObject2, (Set)set);
            return generateJsonDiff;
        }
    }
    
    String getChannelString() {
        return this.channel.name().toLowerCase();
    }
    
    OneSignalStateSynchronizer$UserStateSynchronizerType getChannelType() {
        return this.channel;
    }
    
    protected UserState getCurrentUserState() {
        if (this.currentUserState == null) {
            final Object lock = this.LOCK;
            synchronized (lock) {
                if (this.currentUserState == null) {
                    this.currentUserState = this.newUserState("CURRENT_STATE", true);
                }
            }
        }
        return this.currentUserState;
    }
    
    abstract String getExternalId(final boolean p0);
    
    protected abstract String getId();
    
    protected abstract OneSignal$LOG_LEVEL getLogLevel();
    
    protected NetworkHandlerThread getNetworkHandlerThread(final Integer n) {
        final Object networkHandlerSyncLock = this.networkHandlerSyncLock;
        synchronized (networkHandlerSyncLock) {
            if (!this.networkHandlerThreads.containsKey((Object)n)) {
                this.networkHandlerThreads.put((Object)n, (Object)new NetworkHandlerThread(n));
            }
            return (NetworkHandlerThread)this.networkHandlerThreads.get((Object)n);
        }
    }
    
    String getRegistrationId() {
        return this.getToSyncUserState().getSyncValues().optString("identifier", (String)null);
    }
    
    abstract boolean getSubscribed();
    
    boolean getSyncAsNewSession() {
        return this.getUserStateForModification().getDependValues().optBoolean("session");
    }
    
    abstract GetTagsResult getTags(final boolean p0);
    
    protected UserState getToSyncUserState() {
        if (this.toSyncUserState == null) {
            final Object lock = this.LOCK;
            synchronized (lock) {
                if (this.toSyncUserState == null) {
                    this.toSyncUserState = this.newUserState("TOSYNC_STATE", true);
                }
            }
        }
        return this.toSyncUserState;
    }
    
    protected UserState getUserStateForModification() {
        if (this.toSyncUserState == null) {
            this.toSyncUserState = this.getCurrentUserState().deepClone("TOSYNC_STATE");
        }
        this.scheduleSyncToServer();
        return this.toSyncUserState;
    }
    
    public abstract boolean getUserSubscribePreference();
    
    boolean hasQueuedHandlers() {
        return this.externalUserIdUpdateHandlers.size() > 0;
    }
    
    void initUserState() {
        if (this.currentUserState == null) {
            final Object lock = this.LOCK;
            synchronized (lock) {
                if (this.currentUserState == null) {
                    this.currentUserState = this.newUserState("CURRENT_STATE", true);
                }
            }
        }
        this.getToSyncUserState();
    }
    
    abstract void logoutChannel();
    
    protected abstract UserState newUserState(final String p0, final boolean p1);
    
    protected abstract void onSuccessfulSync(final JSONObject p0);
    
    boolean persist() {
        final UserState toSyncUserState = this.toSyncUserState;
        boolean b = false;
        if (toSyncUserState != null) {
            final Object lock = this.LOCK;
            synchronized (lock) {
                if (this.getCurrentUserState().generateJsonDiff(this.toSyncUserState, this.isSessionCall()) != null) {
                    b = true;
                }
                this.toSyncUserState.persistState();
                return b;
            }
        }
        return false;
    }
    
    void readyToUpdate(final boolean canMakeUpdates) {
        final boolean b = this.canMakeUpdates != canMakeUpdates;
        this.canMakeUpdates = canMakeUpdates;
        if (b && canMakeUpdates) {
            this.scheduleSyncToServer();
        }
    }
    
    void resetCurrentState() {
        this.getCurrentUserState().setSyncValues(new JSONObject());
        this.getCurrentUserState().persistState();
    }
    
    abstract void saveChannelId(final String p0);
    
    protected abstract void scheduleSyncToServer();
    
    void sendPurchases(final JSONObject jsonObject, final OneSignalRestClient$ResponseHandler oneSignalRestClient$ResponseHandler) {
        final StringBuilder sb = new StringBuilder("players/");
        sb.append(this.getId());
        sb.append("/on_purchase");
        OneSignalRestClient.post(sb.toString(), jsonObject, oneSignalRestClient$ResponseHandler);
    }
    
    void sendTags(final JSONObject jsonObject, final OneSignal$ChangeTagsUpdateHandler oneSignal$ChangeTagsUpdateHandler) {
        if (oneSignal$ChangeTagsUpdateHandler != null) {
            this.sendTagsHandlers.add((Object)oneSignal$ChangeTagsUpdateHandler);
        }
        this.getUserStateForModification().generateJsonDiffFromIntoSyncValued(jsonObject, null);
    }
    
    void setExternalUserId(final String s, final String s2, final OneSignal$OSInternalExternalUserIdUpdateCompletionHandler oneSignal$OSInternalExternalUserIdUpdateCompletionHandler) throws JSONException {
        if (oneSignal$OSInternalExternalUserIdUpdateCompletionHandler != null) {
            this.externalUserIdUpdateHandlers.add((Object)oneSignal$OSInternalExternalUserIdUpdateCompletionHandler);
        }
        final UserState userStateForModification = this.getUserStateForModification();
        userStateForModification.putOnSyncValues("external_user_id", s);
        if (s2 != null) {
            userStateForModification.putOnSyncValues("external_user_id_auth_hash", s2);
        }
    }
    
    void setNewSession() {
        try {
            final Object lock = this.LOCK;
            synchronized (lock) {
                this.getUserStateForModification().putOnDependValues("session", true);
                this.getUserStateForModification().persistState();
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public abstract void setPermission(final boolean p0);
    
    abstract void setSubscription(final boolean p0);
    
    void syncHashedEmail(final JSONObject jsonObject) {
        this.getUserStateForModification().generateJsonDiffFromIntoSyncValued(jsonObject, null);
    }
    
    void syncUserState(final boolean b) {
        this.runningSyncUserState.set(true);
        this.internalSyncUserState(b);
        this.runningSyncUserState.set(false);
    }
    
    void updateDeviceInfo(final JSONObject jsonObject, final OneSignalStateSynchronizer$OSDeviceInfoCompletionHandler oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler) {
        if (oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler != null) {
            this.deviceInfoCompletionHandler.add((Object)oneSignalStateSynchronizer$OSDeviceInfoCompletionHandler);
        }
        this.getUserStateForModification().generateJsonDiffFromIntoSyncValued(jsonObject, null);
    }
    
    abstract void updateIdDependents(final String p0);
    
    void updateLocation(final LocationController$LocationPoint location) {
        this.getUserStateForModification().setLocation(location);
    }
    
    abstract void updateState(final JSONObject p0);
    
    static class GetTagsResult
    {
        JSONObject result;
        boolean serverSuccess;
        
        GetTagsResult(final boolean serverSuccess, final JSONObject result) {
            this.serverSuccess = serverSuccess;
            this.result = result;
        }
    }
    
    class NetworkHandlerThread extends HandlerThread
    {
        static final int MAX_RETRIES = 3;
        static final int NETWORK_CALL_DELAY_TO_BUFFER_MS = 5000;
        protected static final int NETWORK_HANDLER_USERSTATE = 0;
        private static final String THREAD_NAME_PREFIX = "OSH_NetworkHandlerThread_";
        int currentRetry;
        Handler mHandler;
        int mType;
        final UserStateSynchronizer this$0;
        
        NetworkHandlerThread(final UserStateSynchronizer this$0, final int mType) {
            this.this$0 = this$0;
            final StringBuilder sb = new StringBuilder("OSH_NetworkHandlerThread_");
            sb.append((Object)this$0.channel);
            super(sb.toString());
            this.mType = mType;
            this.start();
            this.mHandler = new Handler(this.getLooper());
        }
        
        private Runnable getNewRunnable() {
            if (this.mType != 0) {
                return null;
            }
            return (Runnable)new Runnable(this) {
                final NetworkHandlerThread this$1;
                
                public void run() {
                    if (this.this$1.this$0.runningSyncUserState.get() ^ true) {
                        this.this$1.this$0.syncUserState(false);
                    }
                }
            };
        }
        
        boolean doRetry() {
            final Handler mHandler = this.mHandler;
            synchronized (mHandler) {
                final boolean b = this.currentRetry < 3;
                final boolean hasMessages = this.mHandler.hasMessages(0);
                if (b && !hasMessages) {
                    ++this.currentRetry;
                    this.mHandler.postDelayed(this.getNewRunnable(), (long)(this.currentRetry * 15000));
                }
                return this.mHandler.hasMessages(0);
            }
        }
        
        void runNewJobDelayed() {
            if (!this.this$0.canMakeUpdates) {
                return;
            }
            final Handler mHandler = this.mHandler;
            synchronized (mHandler) {
                this.currentRetry = 0;
                this.mHandler.removeCallbacksAndMessages((Object)null);
                this.mHandler.postDelayed(this.getNewRunnable(), 5000L);
            }
        }
        
        void stopScheduledRunnable() {
            this.mHandler.removeCallbacksAndMessages((Object)null);
        }
    }
}
