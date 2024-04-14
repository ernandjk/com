package com.onesignal;

public class OSInAppMessagePushPrompt extends OSInAppMessagePrompt
{
    static final String PUSH_PROMPT_KEY = "push";
    
    String getPromptKey() {
        return "push";
    }
    
    void handlePrompt(final OneSignal$OSPromptActionCompletionCallback oneSignal$OSPromptActionCompletionCallback) {
        OneSignal.promptForPushNotifications(true, (OneSignal$PromptForPushNotificationPermissionResponseHandler)new OSInAppMessagePushPrompt$$ExternalSyntheticLambda0(oneSignal$OSPromptActionCompletionCallback));
    }
}
