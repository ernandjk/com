package com.onesignal;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.app.AlertDialog$Builder;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collection;
import java.util.List;
import java.util.Date;
import com.onesignal.language.LanguageContext;
import java.util.Set;
import java.util.ArrayList;

class OSInAppMessageController extends OSBackgroundManager implements OSDynamicTriggerController$OSDynamicTriggerControllerObserver, OSSystemConditionController$OSSystemConditionObserver
{
    public static final String IN_APP_MESSAGES_JSON_KEY = "in_app_messages";
    private static final String LIQUID_TAG_SCRIPT = "\n\n<script>\n    setPlayerTags(%s);\n</script>";
    private static final Object LOCK;
    private static final String OS_IAM_DB_ACCESS = "OS_IAM_DB_ACCESS";
    private static ArrayList<String> PREFERRED_VARIANT_ORDER;
    private final Set<String> clickedClickIds;
    private OSInAppMessagePrompt currentPrompt;
    private final Set<String> dismissedMessages;
    private final Set<String> impressionedMessages;
    private OSInAppMessageLifecycleHandler inAppMessageLifecycleHandler;
    private OSInAppMessageRepository inAppMessageRepository;
    private boolean inAppMessageShowing;
    private boolean inAppMessagingEnabled;
    private final LanguageContext languageContext;
    Date lastTimeInAppDismissed;
    private final OSLogger logger;
    private final ArrayList<OSInAppMessageInternal> messageDisplayQueue;
    private ArrayList<OSInAppMessageInternal> messages;
    private OSInAppMessageContent pendingMessageContent;
    private List<OSInAppMessageInternal> redisplayedInAppMessages;
    private OSSystemConditionController systemConditionController;
    private final OSTaskController taskController;
    OSTriggerController triggerController;
    private String userTagsString;
    private final Set<String> viewedPageIds;
    private boolean waitForTags;
    
    static {
        LOCK = new Object();
        OSInAppMessageController.PREFERRED_VARIANT_ORDER = (ArrayList<String>)new OSInAppMessageController$1();
    }
    
    protected OSInAppMessageController(final OneSignalDbHelper oneSignalDbHelper, final OSTaskController taskController, final OSLogger logger, final OSSharedPreferences osSharedPreferences, final LanguageContext languageContext) {
        this.redisplayedInAppMessages = null;
        this.currentPrompt = null;
        this.inAppMessagingEnabled = true;
        this.inAppMessageShowing = false;
        this.userTagsString = "";
        this.pendingMessageContent = null;
        this.waitForTags = false;
        this.lastTimeInAppDismissed = null;
        this.taskController = taskController;
        this.messages = (ArrayList<OSInAppMessageInternal>)new ArrayList();
        final Set concurrentSet = OSUtils.newConcurrentSet();
        this.dismissedMessages = (Set<String>)concurrentSet;
        this.messageDisplayQueue = (ArrayList<OSInAppMessageInternal>)new ArrayList();
        final Set concurrentSet2 = OSUtils.newConcurrentSet();
        this.impressionedMessages = (Set<String>)concurrentSet2;
        final Set concurrentSet3 = OSUtils.newConcurrentSet();
        this.viewedPageIds = (Set<String>)concurrentSet3;
        final Set concurrentSet4 = OSUtils.newConcurrentSet();
        this.clickedClickIds = (Set<String>)concurrentSet4;
        this.triggerController = new OSTriggerController((OSDynamicTriggerController$OSDynamicTriggerControllerObserver)this);
        this.systemConditionController = new OSSystemConditionController((OSSystemConditionController$OSSystemConditionObserver)this);
        this.languageContext = languageContext;
        this.logger = logger;
        final OSInAppMessageRepository inAppMessageRepository = this.getInAppMessageRepository(oneSignalDbHelper, logger, osSharedPreferences);
        this.inAppMessageRepository = inAppMessageRepository;
        final Set dismissedMessagesId = inAppMessageRepository.getDismissedMessagesId();
        if (dismissedMessagesId != null) {
            concurrentSet.addAll((Collection)dismissedMessagesId);
        }
        final Set impressionesMessagesId = this.inAppMessageRepository.getImpressionesMessagesId();
        if (impressionesMessagesId != null) {
            concurrentSet2.addAll((Collection)impressionesMessagesId);
        }
        final Set viewPageImpressionedIds = this.inAppMessageRepository.getViewPageImpressionedIds();
        if (viewPageImpressionedIds != null) {
            concurrentSet3.addAll((Collection)viewPageImpressionedIds);
        }
        final Set clickedMessagesId = this.inAppMessageRepository.getClickedMessagesId();
        if (clickedMessagesId != null) {
            concurrentSet4.addAll((Collection)clickedMessagesId);
        }
        final Date lastTimeInAppDismissed = this.inAppMessageRepository.getLastTimeInAppDismissed();
        if (lastTimeInAppDismissed != null) {
            this.lastTimeInAppDismissed = lastTimeInAppDismissed;
        }
        this.initRedisplayData();
    }
    
    private void attemptToShowInAppMessage() {
        final ArrayList<OSInAppMessageInternal> messageDisplayQueue = this.messageDisplayQueue;
        synchronized (messageDisplayQueue) {
            if (!this.systemConditionController.systemConditionsAvailable()) {
                this.logger.warning("In app message not showing due to system condition not correct");
                return;
            }
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("displayFirstIAMOnQueue: ");
            sb.append((Object)this.messageDisplayQueue);
            logger.debug(sb.toString());
            if (this.messageDisplayQueue.size() > 0 && !this.isInAppMessageShowing()) {
                this.logger.debug("No IAM showing currently, showing first item in the queue!");
                this.displayMessage((OSInAppMessageInternal)this.messageDisplayQueue.get(0));
                return;
            }
            final OSLogger logger2 = this.logger;
            final StringBuilder sb2 = new StringBuilder("In app message is currently showing or there are no IAMs left in the queue! isInAppMessageShowing: ");
            sb2.append(this.isInAppMessageShowing());
            logger2.debug(sb2.toString());
        }
    }
    
    private void beginProcessingPrompts(final OSInAppMessageInternal osInAppMessageInternal, final List<OSInAppMessagePrompt> list) {
        if (list.size() > 0) {
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("IAM showing prompts from IAM: ");
            sb.append(osInAppMessageInternal.toString());
            logger.debug(sb.toString());
            WebViewManager.dismissCurrentInAppMessage();
            this.showMultiplePrompts(osInAppMessageInternal, list);
        }
    }
    
    private void checkRedisplayMessagesAndEvaluate(final Collection<String> collection) {
        this.makeRedisplayMessagesAvailableWithTriggers(collection);
        this.evaluateInAppMessages();
    }
    
    private void dismissCurrentMessage(final OSInAppMessageInternal osInAppMessageInternal) {
        OneSignal.getSessionManager().onDirectInfluenceFromIAMClickFinished();
        if (this.shouldWaitForPromptsBeforeDismiss()) {
            this.logger.debug("Stop evaluateMessageDisplayQueue because prompt is currently displayed");
            return;
        }
        this.inAppMessageShowing = false;
        final ArrayList<OSInAppMessageInternal> messageDisplayQueue;
        monitorenter(messageDisplayQueue = this.messageDisplayQueue);
        Label_0146: {
            if (osInAppMessageInternal == null) {
                break Label_0146;
            }
            try {
                if (!osInAppMessageInternal.isPreview && this.messageDisplayQueue.size() > 0) {
                    if (!this.messageDisplayQueue.contains((Object)osInAppMessageInternal)) {
                        this.logger.debug("Message already removed from the queue!");
                        return;
                    }
                    final String messageId = ((OSInAppMessageInternal)this.messageDisplayQueue.remove(0)).messageId;
                    final OSLogger logger = this.logger;
                    final StringBuilder sb = new StringBuilder("In app message with id: ");
                    sb.append(messageId);
                    sb.append(", dismissed (removed) from the queue!");
                    logger.debug(sb.toString());
                }
                if (this.messageDisplayQueue.size() > 0) {
                    final OSLogger logger2 = this.logger;
                    final StringBuilder sb2 = new StringBuilder("In app message on queue available: ");
                    sb2.append(((OSInAppMessageInternal)this.messageDisplayQueue.get(0)).messageId);
                    logger2.debug(sb2.toString());
                    this.displayMessage((OSInAppMessageInternal)this.messageDisplayQueue.get(0));
                }
                else {
                    this.logger.debug("In app message dismissed evaluating messages");
                    this.evaluateInAppMessages();
                }
            }
            finally {
                monitorexit(messageDisplayQueue);
            }
        }
    }
    
    private void displayMessage(final OSInAppMessageInternal osInAppMessageInternal) {
        if (!this.inAppMessagingEnabled) {
            this.logger.verbose("In app messaging is currently paused, in app messages will not be shown!");
            return;
        }
        this.inAppMessageShowing = true;
        this.getTagsForLiquidTemplating(osInAppMessageInternal, false);
        this.inAppMessageRepository.getIAMData(OneSignal.appId, osInAppMessageInternal.messageId, this.variantIdForMessage(osInAppMessageInternal), (OSInAppMessageRepository$OSInAppMessageRequestResponse)new OSInAppMessageRepository$OSInAppMessageRequestResponse(this, osInAppMessageInternal) {
            final OSInAppMessageController this$0;
            final OSInAppMessageInternal val$message;
            
            public void onFailure(final String s) {
                this.this$0.inAppMessageShowing = false;
                try {
                    if (new JSONObject(s).getBoolean("retry")) {
                        this.this$0.queueMessageForDisplay(this.val$message);
                    }
                    else {
                        this.this$0.messageWasDismissed(this.val$message, true);
                    }
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
            
            public void onSuccess(final String s) {
                try {
                    final OSInAppMessageContent access$1600 = this.this$0.parseMessageContentData(new JSONObject(s), this.val$message);
                    if (access$1600.getContentHtml() == null) {
                        this.this$0.logger.debug("displayMessage:OnSuccess: No HTML retrieved from loadMessageContent");
                        return;
                    }
                    if (this.this$0.waitForTags) {
                        this.this$0.pendingMessageContent = access$1600;
                        return;
                    }
                    OneSignal.getSessionManager().onInAppMessageReceived(this.val$message.messageId);
                    this.this$0.onMessageWillDisplay(this.val$message);
                    access$1600.setContentHtml(this.this$0.taggedHTMLString(access$1600.getContentHtml()));
                    WebViewManager.showMessageContent(this.val$message, access$1600);
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    private void evaluateInAppMessages() {
        this.logger.debug("Starting evaluateInAppMessages");
        if (this.shouldRunTaskThroughQueue()) {
            this.taskController.addTaskToQueue((Runnable)new OSInAppMessageController$4(this));
            return;
        }
        for (final OSInAppMessageInternal dataForRedisplay : this.messages) {
            if (this.triggerController.evaluateMessageTriggers(dataForRedisplay)) {
                this.setDataForRedisplay(dataForRedisplay);
                if (this.dismissedMessages.contains((Object)dataForRedisplay.messageId) || dataForRedisplay.isFinished()) {
                    continue;
                }
                this.queueMessageForDisplay(dataForRedisplay);
            }
        }
    }
    
    private void fireClickAction(final OSInAppMessageAction osInAppMessageAction) {
        if (osInAppMessageAction.getClickUrl() != null && !osInAppMessageAction.getClickUrl().isEmpty()) {
            if (osInAppMessageAction.getUrlTarget() == OSInAppMessageAction$OSInAppMessageActionUrlType.BROWSER) {
                OSUtils.openURLInBrowser(osInAppMessageAction.getClickUrl());
            }
            else if (osInAppMessageAction.getUrlTarget() == OSInAppMessageAction$OSInAppMessageActionUrlType.IN_APP_WEBVIEW) {
                OneSignalChromeTab.open(osInAppMessageAction.getClickUrl(), true);
            }
        }
    }
    
    private void fireOutcomesForClick(final String s, final List<OSInAppMessageOutcome> list) {
        OneSignal.getSessionManager().onDirectInfluenceFromIAMClick(s);
        OneSignal.sendClickActionOutcomes((List)list);
    }
    
    private void firePublicClickHandler(final String s, final OSInAppMessageAction osInAppMessageAction) {
        if (OneSignal.inAppMessageClickHandler == null) {
            return;
        }
        OSUtils.runOnMainUIThread((Runnable)new OSInAppMessageController$8(this, s, osInAppMessageAction));
    }
    
    private void fireRESTCallForClick(final OSInAppMessageInternal osInAppMessageInternal, final OSInAppMessageAction osInAppMessageAction) {
        final String variantIdForMessage = this.variantIdForMessage(osInAppMessageInternal);
        if (variantIdForMessage == null) {
            return;
        }
        final String clickId = osInAppMessageAction.getClickId();
        if ((!osInAppMessageInternal.getRedisplayStats().isRedisplayEnabled() || !osInAppMessageInternal.isClickAvailable(clickId)) && this.clickedClickIds.contains((Object)clickId)) {
            return;
        }
        this.clickedClickIds.add((Object)clickId);
        osInAppMessageInternal.addClickId(clickId);
        this.inAppMessageRepository.sendIAMClick(OneSignal.appId, OneSignal.getUserId(), variantIdForMessage, new OSUtils().getDeviceType(), osInAppMessageInternal.messageId, clickId, osInAppMessageAction.isFirstClick(), (Set)this.clickedClickIds, (OSInAppMessageRepository$OSInAppMessageRequestResponse)new OSInAppMessageRepository$OSInAppMessageRequestResponse(this, clickId, osInAppMessageInternal) {
            final OSInAppMessageController this$0;
            final String val$clickId;
            final OSInAppMessageInternal val$message;
            
            public void onFailure(final String s) {
                this.this$0.clickedClickIds.remove((Object)this.val$clickId);
                this.val$message.removeClickId(this.val$clickId);
            }
            
            public void onSuccess(final String s) {
            }
        });
    }
    
    private void fireRESTCallForPageChange(final OSInAppMessageInternal osInAppMessageInternal, final OSInAppMessagePage osInAppMessagePage) {
        final String variantIdForMessage = this.variantIdForMessage(osInAppMessageInternal);
        if (variantIdForMessage == null) {
            return;
        }
        final String pageId = osInAppMessagePage.getPageId();
        final StringBuilder sb = new StringBuilder();
        sb.append(osInAppMessageInternal.messageId);
        sb.append(pageId);
        final String string = sb.toString();
        if (this.viewedPageIds.contains((Object)string)) {
            final OSLogger logger = this.logger;
            final StringBuilder sb2 = new StringBuilder("Already sent page impression for id: ");
            sb2.append(pageId);
            logger.verbose(sb2.toString());
            return;
        }
        this.viewedPageIds.add((Object)string);
        this.inAppMessageRepository.sendIAMPageImpression(OneSignal.appId, OneSignal.getUserId(), variantIdForMessage, new OSUtils().getDeviceType(), osInAppMessageInternal.messageId, pageId, (Set)this.viewedPageIds, (OSInAppMessageRepository$OSInAppMessageRequestResponse)new OSInAppMessageRepository$OSInAppMessageRequestResponse(this, string) {
            final OSInAppMessageController this$0;
            final String val$messagePrefixedPageId;
            
            public void onFailure(final String s) {
                this.this$0.viewedPageIds.remove((Object)this.val$messagePrefixedPageId);
            }
            
            public void onSuccess(final String s) {
            }
        });
    }
    
    private void fireTagCallForClick(final OSInAppMessageAction osInAppMessageAction) {
        if (osInAppMessageAction.getTags() != null) {
            final OSInAppMessageTag tags = osInAppMessageAction.getTags();
            if (tags.getTagsToAdd() != null) {
                OneSignal.sendTags(tags.getTagsToAdd());
            }
            if (tags.getTagsToRemove() != null) {
                OneSignal.deleteTags(tags.getTagsToRemove(), (OneSignal$ChangeTagsUpdateHandler)null);
            }
        }
    }
    
    private void getTagsForLiquidTemplating(final OSInAppMessageInternal osInAppMessageInternal, final boolean b) {
        this.waitForTags = false;
        if (b || osInAppMessageInternal.getHasLiquid()) {
            this.waitForTags = true;
            OneSignal.getTags((OneSignal$OSGetTagsHandler)new OneSignal$OSGetTagsHandler(this, b, osInAppMessageInternal) {
                final OSInAppMessageController this$0;
                final boolean val$isPreview;
                final OSInAppMessageInternal val$message;
                
                public void tagsAvailable(final JSONObject jsonObject) {
                    this.this$0.waitForTags = false;
                    if (jsonObject != null) {
                        this.this$0.userTagsString = jsonObject.toString();
                    }
                    if (this.this$0.pendingMessageContent != null) {
                        if (!this.val$isPreview) {
                            OneSignal.getSessionManager().onInAppMessageReceived(this.val$message.messageId);
                        }
                        final OSInAppMessageContent access$1500 = this.this$0.pendingMessageContent;
                        final OSInAppMessageController this$0 = this.this$0;
                        access$1500.setContentHtml(this$0.taggedHTMLString(this$0.pendingMessageContent.getContentHtml()));
                        WebViewManager.showMessageContent(this.val$message, this.this$0.pendingMessageContent);
                        this.this$0.pendingMessageContent = null;
                    }
                }
            });
        }
    }
    
    private boolean hasMessageTriggerChanged(final OSInAppMessageInternal osInAppMessageInternal) {
        final boolean messageHasOnlyDynamicTriggers = this.triggerController.messageHasOnlyDynamicTriggers(osInAppMessageInternal);
        final boolean b = true;
        if (messageHasOnlyDynamicTriggers) {
            return osInAppMessageInternal.isDisplayedInSession() ^ true;
        }
        final boolean b2 = !osInAppMessageInternal.isDisplayedInSession() && osInAppMessageInternal.triggers.isEmpty();
        boolean b3 = b;
        if (!osInAppMessageInternal.isTriggerChanged()) {
            b3 = (b2 && b);
        }
        return b3;
    }
    
    private void logInAppMessagePreviewActions(final OSInAppMessageAction osInAppMessageAction) {
        if (osInAppMessageAction.getTags() != null) {
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("Tags detected inside of the action click payload, ignoring because action came from IAM preview:: ");
            sb.append(osInAppMessageAction.getTags().toString());
            logger.debug(sb.toString());
        }
        if (osInAppMessageAction.getOutcomes().size() > 0) {
            final OSLogger logger2 = this.logger;
            final StringBuilder sb2 = new StringBuilder("Outcomes detected inside of the action click payload, ignoring because action came from IAM preview: ");
            sb2.append(osInAppMessageAction.getOutcomes().toString());
            logger2.debug(sb2.toString());
        }
    }
    
    private void makeRedisplayMessagesAvailableWithTriggers(final Collection<String> collection) {
        for (final OSInAppMessageInternal osInAppMessageInternal : this.messages) {
            if (!osInAppMessageInternal.isTriggerChanged() && this.redisplayedInAppMessages.contains((Object)osInAppMessageInternal) && this.triggerController.isTriggerOnMessage(osInAppMessageInternal, (Collection)collection)) {
                final OSLogger logger = this.logger;
                final StringBuilder sb = new StringBuilder("Trigger changed for message: ");
                sb.append(osInAppMessageInternal.toString());
                logger.debug(sb.toString());
                osInAppMessageInternal.setTriggerChanged(true);
            }
        }
    }
    
    private OSInAppMessageContent parseMessageContentData(final JSONObject jsonObject, final OSInAppMessageInternal osInAppMessageInternal) {
        final OSInAppMessageContent osInAppMessageContent = new OSInAppMessageContent(jsonObject);
        osInAppMessageInternal.setDisplayDuration(osInAppMessageContent.getDisplayDuration());
        return osInAppMessageContent;
    }
    
    private void persistInAppMessage(final OSInAppMessageInternal osInAppMessageInternal) {
        osInAppMessageInternal.getRedisplayStats().setLastDisplayTime(OneSignal.getTime().getCurrentTimeMillis() / 1000L);
        osInAppMessageInternal.getRedisplayStats().incrementDisplayQuantity();
        osInAppMessageInternal.setTriggerChanged(false);
        osInAppMessageInternal.setDisplayedInSession(true);
        this.runRunnableOnThread((Runnable)new BackgroundRunnable(this, osInAppMessageInternal) {
            final OSInAppMessageController this$0;
            final OSInAppMessageInternal val$message;
            
            public void run() {
                super.run();
                this.this$0.inAppMessageRepository.saveInAppMessage(this.val$message);
                this.this$0.inAppMessageRepository.saveLastTimeInAppDismissed(this.this$0.lastTimeInAppDismissed);
            }
        }, "OS_IAM_DB_ACCESS");
        final int index = this.redisplayedInAppMessages.indexOf((Object)osInAppMessageInternal);
        if (index != -1) {
            this.redisplayedInAppMessages.set(index, (Object)osInAppMessageInternal);
        }
        else {
            this.redisplayedInAppMessages.add((Object)osInAppMessageInternal);
        }
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("persistInAppMessageForRedisplay: ");
        sb.append(osInAppMessageInternal.toString());
        sb.append(" with msg array data: ");
        sb.append(this.redisplayedInAppMessages.toString());
        logger.debug(sb.toString());
    }
    
    private void processInAppMessageJson(final JSONArray jsonArray) throws JSONException {
        final Object lock = OSInAppMessageController.LOCK;
        synchronized (lock) {
            final ArrayList messages = new ArrayList();
            for (int i = 0; i < jsonArray.length(); ++i) {
                final OSInAppMessageInternal osInAppMessageInternal = new OSInAppMessageInternal(jsonArray.getJSONObject(i));
                if (osInAppMessageInternal.messageId != null) {
                    messages.add((Object)osInAppMessageInternal);
                }
            }
            this.messages = (ArrayList<OSInAppMessageInternal>)messages;
            monitorexit(lock);
            this.evaluateInAppMessages();
        }
    }
    
    private void queueMessageForDisplay(final OSInAppMessageInternal osInAppMessageInternal) {
        final ArrayList<OSInAppMessageInternal> messageDisplayQueue = this.messageDisplayQueue;
        synchronized (messageDisplayQueue) {
            if (!this.messageDisplayQueue.contains((Object)osInAppMessageInternal)) {
                this.messageDisplayQueue.add((Object)osInAppMessageInternal);
                final OSLogger logger = this.logger;
                final StringBuilder sb = new StringBuilder("In app message with id: ");
                sb.append(osInAppMessageInternal.messageId);
                sb.append(", added to the queue");
                logger.debug(sb.toString());
            }
            this.attemptToShowInAppMessage();
        }
    }
    
    private void resetRedisplayMessagesBySession() {
        final Iterator iterator = this.redisplayedInAppMessages.iterator();
        while (iterator.hasNext()) {
            ((OSInAppMessageInternal)iterator.next()).setDisplayedInSession(false);
        }
    }
    
    private void setDataForRedisplay(final OSInAppMessageInternal osInAppMessageInternal) {
        final boolean contains = this.dismissedMessages.contains((Object)osInAppMessageInternal.messageId);
        final int index = this.redisplayedInAppMessages.indexOf((Object)osInAppMessageInternal);
        if (contains && index != -1) {
            final OSInAppMessageInternal osInAppMessageInternal2 = (OSInAppMessageInternal)this.redisplayedInAppMessages.get(index);
            osInAppMessageInternal.getRedisplayStats().setDisplayStats(osInAppMessageInternal2.getRedisplayStats());
            osInAppMessageInternal.setDisplayedInSession(osInAppMessageInternal2.isDisplayedInSession());
            final boolean hasMessageTriggerChanged = this.hasMessageTriggerChanged(osInAppMessageInternal);
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("setDataForRedisplay: ");
            sb.append(osInAppMessageInternal.toString());
            sb.append(" triggerHasChanged: ");
            sb.append(hasMessageTriggerChanged);
            logger.debug(sb.toString());
            if (hasMessageTriggerChanged && osInAppMessageInternal.getRedisplayStats().isDelayTimeSatisfied() && osInAppMessageInternal.getRedisplayStats().shouldDisplayAgain()) {
                final OSLogger logger2 = this.logger;
                final StringBuilder sb2 = new StringBuilder("setDataForRedisplay message available for redisplay: ");
                sb2.append(osInAppMessageInternal.messageId);
                logger2.debug(sb2.toString());
                this.dismissedMessages.remove((Object)osInAppMessageInternal.messageId);
                this.impressionedMessages.remove((Object)osInAppMessageInternal.messageId);
                this.viewedPageIds.clear();
                this.inAppMessageRepository.saveViewPageImpressionedIds((Set)this.viewedPageIds);
                osInAppMessageInternal.clearClickIds();
            }
        }
    }
    
    private boolean shouldWaitForPromptsBeforeDismiss() {
        return this.currentPrompt != null;
    }
    
    private void showAlertDialogMessage(final OSInAppMessageInternal osInAppMessageInternal, final List<OSInAppMessagePrompt> list) {
        new AlertDialog$Builder((Context)OneSignal.getCurrentActivity()).setTitle((CharSequence)OneSignal.appContext.getString(R$string.location_permission_missing_title)).setMessage((CharSequence)OneSignal.appContext.getString(R$string.location_permission_missing_message)).setPositiveButton(17039370, (DialogInterface$OnClickListener)new OSInAppMessageController$7(this, osInAppMessageInternal, (List)list)).show();
    }
    
    private void showMultiplePrompts(final OSInAppMessageInternal osInAppMessageInternal, final List<OSInAppMessagePrompt> list) {
        for (final OSInAppMessagePrompt currentPrompt : list) {
            if (!currentPrompt.hasPrompted()) {
                this.currentPrompt = currentPrompt;
                break;
            }
        }
        if (this.currentPrompt != null) {
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("IAM prompt to handle: ");
            sb.append(this.currentPrompt.toString());
            logger.debug(sb.toString());
            this.currentPrompt.setPrompted(true);
            this.currentPrompt.handlePrompt((OneSignal$OSPromptActionCompletionCallback)new OneSignal$OSPromptActionCompletionCallback(this, osInAppMessageInternal, list) {
                final OSInAppMessageController this$0;
                final OSInAppMessageInternal val$inAppMessage;
                final List val$prompts;
                
                public void onCompleted(final OneSignal$PromptActionResult oneSignal$PromptActionResult) {
                    this.this$0.currentPrompt = null;
                    final OSLogger access$300 = this.this$0.logger;
                    final StringBuilder sb = new StringBuilder("IAM prompt to handle finished with result: ");
                    sb.append((Object)oneSignal$PromptActionResult);
                    access$300.debug(sb.toString());
                    if (this.val$inAppMessage.isPreview && oneSignal$PromptActionResult == OneSignal$PromptActionResult.LOCATION_PERMISSIONS_MISSING_MANIFEST) {
                        this.this$0.showAlertDialogMessage(this.val$inAppMessage, (List<OSInAppMessagePrompt>)this.val$prompts);
                    }
                    else {
                        this.this$0.showMultiplePrompts(this.val$inAppMessage, (List<OSInAppMessagePrompt>)this.val$prompts);
                    }
                }
            });
        }
        else {
            final OSLogger logger2 = this.logger;
            final StringBuilder sb2 = new StringBuilder("No IAM prompt to handle, dismiss message: ");
            sb2.append(osInAppMessageInternal.messageId);
            logger2.debug(sb2.toString());
            this.messageWasDismissed(osInAppMessageInternal);
        }
    }
    
    private String variantIdForMessage(final OSInAppMessageInternal osInAppMessageInternal) {
        final String language = this.languageContext.getLanguage();
        for (final String s : OSInAppMessageController.PREFERRED_VARIANT_ORDER) {
            if (!osInAppMessageInternal.variants.containsKey((Object)s)) {
                continue;
            }
            final HashMap hashMap = (HashMap)osInAppMessageInternal.variants.get((Object)s);
            if (hashMap.containsKey((Object)language)) {
                return (String)hashMap.get((Object)language);
            }
            return (String)hashMap.get((Object)"default");
        }
        return null;
    }
    
    void addTriggers(final Map<String, Object> map) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("Triggers added: ");
        sb.append(map.toString());
        logger.debug(sb.toString());
        this.triggerController.addTriggers((Map)map);
        if (this.shouldRunTaskThroughQueue()) {
            this.taskController.addTaskToQueue((Runnable)new OSInAppMessageController$16(this, (Map)map));
        }
        else {
            this.checkRedisplayMessagesAndEvaluate((Collection<String>)map.keySet());
        }
    }
    
    void cleanCachedInAppMessages() {
        this.runRunnableOnThread((Runnable)new BackgroundRunnable(this) {
            final OSInAppMessageController this$0;
            
            public void run() {
                super.run();
                this.this$0.inAppMessageRepository.cleanCachedInAppMessages();
            }
        }, "OS_IAM_DB_ACCESS");
    }
    
    void displayPreviewMessage(final String s) {
        this.inAppMessageShowing = true;
        final OSInAppMessageInternal osInAppMessageInternal = new OSInAppMessageInternal(true);
        this.getTagsForLiquidTemplating(osInAppMessageInternal, true);
        this.inAppMessageRepository.getIAMPreviewData(OneSignal.appId, s, (OSInAppMessageRepository$OSInAppMessageRequestResponse)new OSInAppMessageRepository$OSInAppMessageRequestResponse(this, osInAppMessageInternal) {
            final OSInAppMessageController this$0;
            final OSInAppMessageInternal val$message;
            
            public void onFailure(final String s) {
                this.this$0.dismissCurrentMessage(null);
            }
            
            public void onSuccess(final String s) {
                try {
                    final OSInAppMessageContent access$1600 = this.this$0.parseMessageContentData(new JSONObject(s), this.val$message);
                    if (access$1600.getContentHtml() == null) {
                        this.this$0.logger.debug("displayPreviewMessage:OnSuccess: No HTML retrieved from loadMessageContent");
                        return;
                    }
                    if (this.this$0.waitForTags) {
                        this.this$0.pendingMessageContent = access$1600;
                        return;
                    }
                    this.this$0.onMessageWillDisplay(this.val$message);
                    access$1600.setContentHtml(this.this$0.taggedHTMLString(access$1600.getContentHtml()));
                    WebViewManager.showMessageContent(this.val$message, access$1600);
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    void executeRedisplayIAMDataDependantTask(final Runnable runnable) {
        final Object lock = OSInAppMessageController.LOCK;
        synchronized (lock) {
            if (this.shouldRunTaskThroughQueue()) {
                this.logger.debug("Delaying task due to redisplay data not retrieved yet");
                this.taskController.addTaskToQueue(runnable);
            }
            else {
                runnable.run();
            }
        }
    }
    
    OSInAppMessageInternal getCurrentDisplayedInAppMessage() {
        OSInAppMessageInternal osInAppMessageInternal;
        if (this.inAppMessageShowing) {
            osInAppMessageInternal = (OSInAppMessageInternal)this.messageDisplayQueue.get(0);
        }
        else {
            osInAppMessageInternal = null;
        }
        return osInAppMessageInternal;
    }
    
    public ArrayList<OSInAppMessageInternal> getInAppMessageDisplayQueue() {
        return this.messageDisplayQueue;
    }
    
    OSInAppMessageRepository getInAppMessageRepository(final OneSignalDbHelper oneSignalDbHelper, final OSLogger osLogger, final OSSharedPreferences osSharedPreferences) {
        if (this.inAppMessageRepository == null) {
            this.inAppMessageRepository = new OSInAppMessageRepository(oneSignalDbHelper, osLogger, osSharedPreferences);
        }
        return this.inAppMessageRepository;
    }
    
    public List<OSInAppMessageInternal> getRedisplayedInAppMessages() {
        return this.redisplayedInAppMessages;
    }
    
    Object getTriggerValue(final String s) {
        return this.triggerController.getTriggerValue(s);
    }
    
    Map<String, Object> getTriggers() {
        return (Map<String, Object>)new HashMap((Map)this.triggerController.getTriggers());
    }
    
    boolean inAppMessagingEnabled() {
        return this.inAppMessagingEnabled;
    }
    
    protected void initRedisplayData() {
        this.taskController.addTaskToQueue((Runnable)new BackgroundRunnable(this) {
            final OSInAppMessageController this$0;
            
            public void run() {
                super.run();
                final Object access$000 = OSInAppMessageController.LOCK;
                synchronized (access$000) {
                    final OSInAppMessageController this$0 = this.this$0;
                    this$0.redisplayedInAppMessages = (List<OSInAppMessageInternal>)this$0.inAppMessageRepository.getCachedInAppMessages();
                    final OSLogger access$2 = this.this$0.logger;
                    final StringBuilder sb = new StringBuilder("Retrieved IAMs from DB redisplayedInAppMessages: ");
                    sb.append(this.this$0.redisplayedInAppMessages.toString());
                    access$2.debug(sb.toString());
                }
            }
        });
        this.taskController.startPendingTasks();
    }
    
    void initWithCachedInAppMessages() {
        if (!this.messages.isEmpty()) {
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("initWithCachedInAppMessages with already in memory messages: ");
            sb.append((Object)this.messages);
            logger.debug(sb.toString());
            return;
        }
        final String savedIAMs = this.inAppMessageRepository.getSavedIAMs();
        final OSLogger logger2 = this.logger;
        final StringBuilder sb2 = new StringBuilder("initWithCachedInAppMessages: ");
        sb2.append(savedIAMs);
        logger2.debug(sb2.toString());
        if (savedIAMs != null) {
            if (!savedIAMs.isEmpty()) {
                final Object lock;
                monitorenter(lock = OSInAppMessageController.LOCK);
                try {
                    Label_0156: {
                        try {
                            if (!this.messages.isEmpty()) {
                                monitorexit(lock);
                                return;
                            }
                            this.processInAppMessageJson(new JSONArray(savedIAMs));
                            break Label_0156;
                        }
                        finally {
                            monitorexit(lock);
                            monitorexit(lock);
                        }
                    }
                }
                catch (final JSONException ex) {}
            }
        }
    }
    
    boolean isInAppMessageShowing() {
        return this.inAppMessageShowing;
    }
    
    public void messageDynamicTriggerCompleted(final String s) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("messageDynamicTriggerCompleted called with triggerId: ");
        sb.append(s);
        logger.debug(sb.toString());
        final HashSet set = new HashSet();
        ((Set)set).add((Object)s);
        this.makeRedisplayMessagesAvailableWithTriggers((Collection<String>)set);
    }
    
    public void messageTriggerConditionChanged() {
        this.logger.debug("messageTriggerConditionChanged called");
        this.evaluateInAppMessages();
    }
    
    void messageWasDismissed(final OSInAppMessageInternal osInAppMessageInternal) {
        this.messageWasDismissed(osInAppMessageInternal, false);
    }
    
    void messageWasDismissed(final OSInAppMessageInternal osInAppMessageInternal, final boolean b) {
        if (!osInAppMessageInternal.isPreview) {
            this.dismissedMessages.add((Object)osInAppMessageInternal.messageId);
            if (!b) {
                this.inAppMessageRepository.saveDismissedMessagesId((Set)this.dismissedMessages);
                this.lastTimeInAppDismissed = new Date();
                this.persistInAppMessage(osInAppMessageInternal);
            }
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("OSInAppMessageController messageWasDismissed dismissedMessages: ");
            sb.append(this.dismissedMessages.toString());
            logger.debug(sb.toString());
        }
        if (!this.shouldWaitForPromptsBeforeDismiss()) {
            this.onMessageDidDismiss(osInAppMessageInternal);
        }
        this.dismissCurrentMessage(osInAppMessageInternal);
    }
    
    void onMessageActionOccurredOnMessage(final OSInAppMessageInternal osInAppMessageInternal, final JSONObject jsonObject) throws JSONException {
        final OSInAppMessageAction osInAppMessageAction = new OSInAppMessageAction(jsonObject);
        osInAppMessageAction.setFirstClick(osInAppMessageInternal.takeActionAsUnique());
        this.firePublicClickHandler(osInAppMessageInternal.messageId, osInAppMessageAction);
        this.beginProcessingPrompts(osInAppMessageInternal, (List<OSInAppMessagePrompt>)osInAppMessageAction.getPrompts());
        this.fireClickAction(osInAppMessageAction);
        this.fireRESTCallForClick(osInAppMessageInternal, osInAppMessageAction);
        this.fireTagCallForClick(osInAppMessageAction);
        this.fireOutcomesForClick(osInAppMessageInternal.messageId, (List<OSInAppMessageOutcome>)osInAppMessageAction.getOutcomes());
    }
    
    void onMessageActionOccurredOnPreview(final OSInAppMessageInternal osInAppMessageInternal, final JSONObject jsonObject) throws JSONException {
        final OSInAppMessageAction osInAppMessageAction = new OSInAppMessageAction(jsonObject);
        osInAppMessageAction.setFirstClick(osInAppMessageInternal.takeActionAsUnique());
        this.firePublicClickHandler(osInAppMessageInternal.messageId, osInAppMessageAction);
        this.beginProcessingPrompts(osInAppMessageInternal, (List<OSInAppMessagePrompt>)osInAppMessageAction.getPrompts());
        this.fireClickAction(osInAppMessageAction);
        this.logInAppMessagePreviewActions(osInAppMessageAction);
    }
    
    void onMessageDidDismiss(final OSInAppMessageInternal osInAppMessageInternal) {
        final OSInAppMessageLifecycleHandler inAppMessageLifecycleHandler = this.inAppMessageLifecycleHandler;
        if (inAppMessageLifecycleHandler == null) {
            this.logger.verbose("OSInAppMessageController onMessageDidDismiss: inAppMessageLifecycleHandler is null");
            return;
        }
        inAppMessageLifecycleHandler.onDidDismissInAppMessage((OSInAppMessage)osInAppMessageInternal);
    }
    
    void onMessageDidDisplay(final OSInAppMessageInternal osInAppMessageInternal) {
        final OSInAppMessageLifecycleHandler inAppMessageLifecycleHandler = this.inAppMessageLifecycleHandler;
        if (inAppMessageLifecycleHandler == null) {
            this.logger.verbose("OSInAppMessageController onMessageDidDisplay: inAppMessageLifecycleHandler is null");
            return;
        }
        inAppMessageLifecycleHandler.onDidDisplayInAppMessage((OSInAppMessage)osInAppMessageInternal);
    }
    
    void onMessageWasShown(final OSInAppMessageInternal osInAppMessageInternal) {
        this.onMessageDidDisplay(osInAppMessageInternal);
        if (osInAppMessageInternal.isPreview) {
            return;
        }
        if (this.impressionedMessages.contains((Object)osInAppMessageInternal.messageId)) {
            return;
        }
        this.impressionedMessages.add((Object)osInAppMessageInternal.messageId);
        final String variantIdForMessage = this.variantIdForMessage(osInAppMessageInternal);
        if (variantIdForMessage == null) {
            return;
        }
        this.inAppMessageRepository.sendIAMImpression(OneSignal.appId, OneSignal.getUserId(), variantIdForMessage, new OSUtils().getDeviceType(), osInAppMessageInternal.messageId, (Set)this.impressionedMessages, (OSInAppMessageRepository$OSInAppMessageRequestResponse)new OSInAppMessageRepository$OSInAppMessageRequestResponse(this, osInAppMessageInternal) {
            final OSInAppMessageController this$0;
            final OSInAppMessageInternal val$message;
            
            public void onFailure(final String s) {
                this.this$0.impressionedMessages.remove((Object)this.val$message.messageId);
            }
            
            public void onSuccess(final String s) {
            }
        });
    }
    
    void onMessageWillDismiss(final OSInAppMessageInternal osInAppMessageInternal) {
        final OSInAppMessageLifecycleHandler inAppMessageLifecycleHandler = this.inAppMessageLifecycleHandler;
        if (inAppMessageLifecycleHandler == null) {
            this.logger.verbose("OSInAppMessageController onMessageWillDismiss: inAppMessageLifecycleHandler is null");
            return;
        }
        inAppMessageLifecycleHandler.onWillDismissInAppMessage((OSInAppMessage)osInAppMessageInternal);
    }
    
    void onMessageWillDisplay(final OSInAppMessageInternal osInAppMessageInternal) {
        final OSInAppMessageLifecycleHandler inAppMessageLifecycleHandler = this.inAppMessageLifecycleHandler;
        if (inAppMessageLifecycleHandler == null) {
            this.logger.verbose("OSInAppMessageController onMessageWillDisplay: inAppMessageLifecycleHandler is null");
            return;
        }
        inAppMessageLifecycleHandler.onWillDisplayInAppMessage((OSInAppMessage)osInAppMessageInternal);
    }
    
    void onPageChanged(final OSInAppMessageInternal osInAppMessageInternal, final JSONObject jsonObject) {
        final OSInAppMessagePage osInAppMessagePage = new OSInAppMessagePage(jsonObject);
        if (osInAppMessageInternal.isPreview) {
            return;
        }
        this.fireRESTCallForPageChange(osInAppMessageInternal, osInAppMessagePage);
    }
    
    void receivedInAppMessageJson(final JSONArray jsonArray) throws JSONException {
        this.inAppMessageRepository.saveIAMs(jsonArray.toString());
        this.executeRedisplayIAMDataDependantTask((Runnable)new OSInAppMessageController$3(this, jsonArray));
    }
    
    void removeTriggersForKeys(final Collection<String> collection) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("Triggers key to remove: ");
        sb.append(collection.toString());
        logger.debug(sb.toString());
        this.triggerController.removeTriggersForKeys((Collection)collection);
        if (this.shouldRunTaskThroughQueue()) {
            this.taskController.addTaskToQueue((Runnable)new OSInAppMessageController$17(this, (Collection)collection));
        }
        else {
            this.checkRedisplayMessagesAndEvaluate(collection);
        }
    }
    
    void resetSessionLaunchTime() {
        OSDynamicTriggerController.resetSessionLaunchTime();
    }
    
    void setInAppMessageLifecycleHandler(final OSInAppMessageLifecycleHandler inAppMessageLifecycleHandler) {
        this.inAppMessageLifecycleHandler = inAppMessageLifecycleHandler;
    }
    
    void setInAppMessagingEnabled(final boolean inAppMessagingEnabled) {
        this.inAppMessagingEnabled = inAppMessagingEnabled;
        if (inAppMessagingEnabled) {
            this.evaluateInAppMessages();
        }
    }
    
    boolean shouldRunTaskThroughQueue() {
        final Object lock = OSInAppMessageController.LOCK;
        synchronized (lock) {
            return this.redisplayedInAppMessages == null && this.taskController.shouldRunTaskThroughQueue();
        }
    }
    
    public void systemConditionChanged() {
        this.attemptToShowInAppMessage();
    }
    
    String taggedHTMLString(final String s) {
        final String userTagsString = this.userTagsString;
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(String.format("\n\n<script>\n    setPlayerTags(%s);\n</script>", new Object[] { userTagsString }));
        return sb.toString();
    }
}
