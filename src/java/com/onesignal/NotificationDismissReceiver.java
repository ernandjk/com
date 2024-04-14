package com.onesignal;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class NotificationDismissReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) {
        NotificationOpenedProcessor.processFromContext(context, intent);
    }
}
