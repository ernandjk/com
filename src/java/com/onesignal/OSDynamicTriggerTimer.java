package com.onesignal;

import java.util.Timer;
import java.util.TimerTask;

class OSDynamicTriggerTimer
{
    static void scheduleTrigger(final TimerTask timerTask, final String s, final long n) {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("scheduleTrigger: ");
        sb.append(s);
        sb.append(" delay: ");
        sb.append(n);
        OneSignal.onesignalLog(debug, sb.toString());
        final StringBuilder sb2 = new StringBuilder("trigger_timer:");
        sb2.append(s);
        new Timer(sb2.toString()).schedule(timerTask, n);
    }
}
