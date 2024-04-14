package com.onesignal;

import android.os.Build$VERSION;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class UpgradeReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) {
        if (Build$VERSION.SDK_INT == 24) {
            return;
        }
        OSNotificationRestoreWorkManager.beginEnqueueingWork(context, true);
    }
}
