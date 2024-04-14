package com.onesignal;

public abstract class OSInAppMessageLifecycleHandler
{
    public void onDidDismissInAppMessage(final OSInAppMessage osInAppMessage) {
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("OSInAppMessageLifecycleHandler: IAM Did Dismiss: ");
        sb.append(osInAppMessage.getMessageId());
        OneSignal.Log(verbose, sb.toString());
    }
    
    public void onDidDisplayInAppMessage(final OSInAppMessage osInAppMessage) {
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("OSInAppMessageLifecycleHandler: IAM Did Display: ");
        sb.append(osInAppMessage.getMessageId());
        OneSignal.Log(verbose, sb.toString());
    }
    
    public void onWillDismissInAppMessage(final OSInAppMessage osInAppMessage) {
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("OSInAppMessageLifecycleHandler: IAM Will Dismiss: ");
        sb.append(osInAppMessage.getMessageId());
        OneSignal.Log(verbose, sb.toString());
    }
    
    public void onWillDisplayInAppMessage(final OSInAppMessage osInAppMessage) {
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("OSInAppMessageLifecycleHandler: IAM Will Display: ");
        sb.append(osInAppMessage.getMessageId());
        OneSignal.Log(verbose, sb.toString());
    }
}
