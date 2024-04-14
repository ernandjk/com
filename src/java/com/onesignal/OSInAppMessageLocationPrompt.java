package com.onesignal;

class OSInAppMessageLocationPrompt extends OSInAppMessagePrompt
{
    static final String LOCATION_PROMPT_KEY = "location";
    
    String getPromptKey() {
        return "location";
    }
    
    void handlePrompt(final OneSignal$OSPromptActionCompletionCallback oneSignal$OSPromptActionCompletionCallback) {
        OneSignal.promptLocation(oneSignal$OSPromptActionCompletionCallback, true);
    }
}
