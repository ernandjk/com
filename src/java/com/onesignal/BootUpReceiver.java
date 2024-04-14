package com.onesignal;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class BootUpReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) {
        OSNotificationRestoreWorkManager.beginEnqueueingWork(context, true);
    }
}
