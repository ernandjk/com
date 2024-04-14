package com.onesignal;

import android.content.ComponentName;
import android.os.Parcelable;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

public class FCMBroadcastReceiver extends WakefulBroadcastReceiver
{
    private static final String FCM_RECEIVE_ACTION = "com.google.android.c2dm.intent.RECEIVE";
    private static final String FCM_TYPE = "gcm";
    private static final String MESSAGE_TYPE_EXTRA_KEY = "message_type";
    
    private static boolean isFCMMessage(final Intent intent) {
        final boolean equals = "com.google.android.c2dm.intent.RECEIVE".equals((Object)intent.getAction());
        boolean b = false;
        if (equals) {
            final String stringExtra = intent.getStringExtra("message_type");
            if (stringExtra != null) {
                b = b;
                if (!"gcm".equals((Object)stringExtra)) {
                    return b;
                }
            }
            b = true;
        }
        return b;
    }
    
    private static void processOrderBroadcast(final Context context, final Intent intent, final Bundle bundle, final NotificationBundleProcessor$ProcessBundleReceiverCallback notificationBundleProcessor$ProcessBundleReceiverCallback) {
        if (!isFCMMessage(intent)) {
            notificationBundleProcessor$ProcessBundleReceiverCallback.onBundleProcessed((NotificationBundleProcessor$ProcessedBundleResult)null);
        }
        NotificationBundleProcessor.processBundleFromReceiver(context, bundle, (NotificationBundleProcessor$ProcessBundleReceiverCallback)new NotificationBundleProcessor$ProcessBundleReceiverCallback(notificationBundleProcessor$ProcessBundleReceiverCallback, context, bundle) {
            final Bundle val$bundle;
            final Context val$context;
            final NotificationBundleProcessor$ProcessBundleReceiverCallback val$fcmBundleReceiver;
            
            public void onBundleProcessed(final NotificationBundleProcessor$ProcessedBundleResult notificationBundleProcessor$ProcessedBundleResult) {
                if (notificationBundleProcessor$ProcessedBundleResult != null && notificationBundleProcessor$ProcessedBundleResult.processed()) {
                    this.val$fcmBundleReceiver.onBundleProcessed(notificationBundleProcessor$ProcessedBundleResult);
                    return;
                }
                FCMBroadcastReceiver.startFCMService(this.val$context, this.val$bundle);
                this.val$fcmBundleReceiver.onBundleProcessed(notificationBundleProcessor$ProcessedBundleResult);
            }
        });
    }
    
    private void setAbort() {
        if (this.isOrderedBroadcast()) {
            this.abortBroadcast();
            this.setResultCode(-1);
        }
    }
    
    private static BundleCompat setCompatBundleForServer(final Bundle bundle, final BundleCompat bundleCompat) {
        bundleCompat.putString("json_payload", NotificationBundleProcessor.bundleAsJSONObject(bundle).toString());
        bundleCompat.putLong("timestamp", Long.valueOf(OneSignal.getTime().getCurrentTimeMillis() / 1000L));
        return bundleCompat;
    }
    
    private void setSuccessfulResultCode() {
        if (this.isOrderedBroadcast()) {
            this.setResultCode(-1);
        }
    }
    
    static void startFCMService(final Context context, final Bundle bundle) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("startFCMService from: ");
        sb.append((Object)context);
        sb.append(" and bundle: ");
        sb.append((Object)bundle);
        OneSignal.Log(debug, sb.toString());
        if (!NotificationBundleProcessor.hasRemoteResource(bundle)) {
            OneSignal.Log(OneSignal$LOG_LEVEL.DEBUG, "startFCMService with no remote resources, no need for services");
            NotificationBundleProcessor.processFromFCMIntentService(context, setCompatBundleForServer(bundle, BundleCompatFactory.getInstance()));
            return;
        }
        if (Integer.parseInt(bundle.getString("pri", "0")) <= 9 && Build$VERSION.SDK_INT >= 26) {
            startFCMServiceWithJobIntentService(context, bundle);
        }
        else {
            try {
                startFCMServiceWithWakefulService(context, bundle);
            }
            catch (final IllegalStateException ex) {
                startFCMServiceWithJobIntentService(context, bundle);
            }
        }
    }
    
    private static void startFCMServiceWithJobIntentService(final Context context, final Bundle bundle) {
        final BundleCompat setCompatBundleForServer = setCompatBundleForServer(bundle, BundleCompatFactory.getInstance());
        final Intent intent = new Intent(context, (Class)FCMIntentJobService.class);
        intent.putExtra("Bundle:Parcelable:Extras", (Parcelable)setCompatBundleForServer.getBundle());
        FCMIntentJobService.enqueueWork(context, intent);
    }
    
    private static void startFCMServiceWithWakefulService(final Context context, final Bundle bundle) {
        startWakefulService(context, new Intent().replaceExtras((Bundle)setCompatBundleForServer(bundle, (BundleCompat)new BundleCompatBundle()).getBundle()).setComponent(new ComponentName(context.getPackageName(), FCMIntentService.class.getName())));
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            if (!"google.com/iid".equals((Object)extras.getString("from"))) {
                OneSignal.initWithContext(context);
                processOrderBroadcast(context, intent, extras, (NotificationBundleProcessor$ProcessBundleReceiverCallback)new NotificationBundleProcessor$ProcessBundleReceiverCallback(this) {
                    final FCMBroadcastReceiver this$0;
                    
                    public void onBundleProcessed(final NotificationBundleProcessor$ProcessedBundleResult notificationBundleProcessor$ProcessedBundleResult) {
                        if (notificationBundleProcessor$ProcessedBundleResult == null) {
                            this.this$0.setSuccessfulResultCode();
                            return;
                        }
                        if (!notificationBundleProcessor$ProcessedBundleResult.isDup() && !notificationBundleProcessor$ProcessedBundleResult.isWorkManagerProcessing()) {
                            this.this$0.setSuccessfulResultCode();
                            return;
                        }
                        this.this$0.setAbort();
                    }
                });
            }
        }
    }
}
