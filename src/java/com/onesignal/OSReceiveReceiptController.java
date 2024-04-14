package com.onesignal;

import androidx.work.NetworkType;
import androidx.work.Constraints$Builder;
import androidx.work.Constraints;
import androidx.work.WorkManager;
import androidx.work.ExistingWorkPolicy;
import androidx.work.Data$Builder;
import java.util.concurrent.TimeUnit;
import androidx.work.OneTimeWorkRequest$Builder;
import androidx.work.OneTimeWorkRequest;
import android.content.Context;

class OSReceiveReceiptController
{
    private static final String OS_NOTIFICATION_ID = "os_notification_id";
    private static OSReceiveReceiptController sInstance;
    private int maxDelay;
    private int minDelay;
    private final OSRemoteParamController remoteParamController;
    
    private OSReceiveReceiptController() {
        this.minDelay = 0;
        this.maxDelay = 25;
        this.remoteParamController = OneSignal.getRemoteParamController();
    }
    
    public static OSReceiveReceiptController getInstance() {
        synchronized (OSReceiveReceiptController.class) {
            if (OSReceiveReceiptController.sInstance == null) {
                OSReceiveReceiptController.sInstance = new OSReceiveReceiptController();
            }
            return OSReceiveReceiptController.sInstance;
        }
    }
    
    void beginEnqueueingWork(final Context context, final String s) {
        if (!this.remoteParamController.isReceiveReceiptEnabled()) {
            OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "sendReceiveReceipt disabled");
            return;
        }
        final int randomDelay = OSUtils.getRandomDelay(this.minDelay, this.maxDelay);
        final OneTimeWorkRequest oneTimeWorkRequest = (OneTimeWorkRequest)((OneTimeWorkRequest$Builder)((OneTimeWorkRequest$Builder)((OneTimeWorkRequest$Builder)new OneTimeWorkRequest$Builder((Class)OSReceiveReceiptController.OSReceiveReceiptController$ReceiveReceiptWorker.class).setConstraints(this.buildConstraints())).setInitialDelay((long)randomDelay, TimeUnit.SECONDS)).setInputData(new Data$Builder().putString("os_notification_id", s).build())).build();
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OSReceiveReceiptController enqueueing send receive receipt work with notificationId: ");
        sb.append(s);
        sb.append(" and delay: ");
        sb.append(randomDelay);
        sb.append(" seconds");
        OneSignal.Log(debug, sb.toString());
        final WorkManager instance = OSWorkManagerHelper.getInstance(context);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("_receive_receipt");
        instance.enqueueUniqueWork(sb2.toString(), ExistingWorkPolicy.KEEP, oneTimeWorkRequest);
    }
    
    Constraints buildConstraints() {
        return new Constraints$Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
    }
}
