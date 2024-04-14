package com.onesignal;

import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Date;

class OSDynamicTriggerController
{
    private static final long DEFAULT_LAST_IN_APP_TIME_AGO = 999999L;
    private static final double REQUIRED_ACCURACY = 0.3;
    private static Date sessionLaunchTime;
    private final OSDynamicTriggerControllerObserver observer;
    private final ArrayList<String> scheduledMessages;
    
    static {
        OSDynamicTriggerController.sessionLaunchTime = new Date();
    }
    
    OSDynamicTriggerController(final OSDynamicTriggerControllerObserver observer) {
        this.scheduledMessages = (ArrayList<String>)new ArrayList();
        this.observer = observer;
    }
    
    private static boolean evaluateTimeIntervalWithOperator(final double n, final double n2, final OSTrigger.OSTriggerOperator osTriggerOperator) {
        final int n3 = OSDynamicTriggerController$2.$SwitchMap$com$onesignal$OSTrigger$OSTriggerOperator[osTriggerOperator.ordinal()];
        boolean b = true;
        final boolean b2 = true;
        final boolean b3 = true;
        final boolean b4 = true;
        switch (n3) {
            default: {
                final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                final StringBuilder sb = new StringBuilder("Attempted to apply an invalid operator on a time-based in-app-message trigger: ");
                sb.append(osTriggerOperator.toString());
                OneSignal.onesignalLog(error, sb.toString());
                return false;
            }
            case 6: {
                return roughlyEqual(n, n2) ^ true;
            }
            case 5: {
                return roughlyEqual(n, n2);
            }
            case 4: {
                boolean b5 = b4;
                if (n2 < n) {
                    b5 = (roughlyEqual(n, n2) && b4);
                }
                return b5;
            }
            case 3: {
                if (n2 < n) {
                    b = false;
                }
                return b;
            }
            case 2: {
                boolean b6 = b2;
                if (n2 > n) {
                    b6 = (roughlyEqual(n, n2) && b2);
                }
                return b6;
            }
            case 1: {
                return n2 < n && b3;
            }
        }
    }
    
    static void resetSessionLaunchTime() {
        OSDynamicTriggerController.sessionLaunchTime = new Date();
    }
    
    private static boolean roughlyEqual(final double n, final double n2) {
        return Math.abs(n - n2) < 0.3;
    }
    
    boolean dynamicTriggerShouldFire(final OSTrigger osTrigger) {
        if (osTrigger.value == null) {
            return false;
        }
        final ArrayList<String> scheduledMessages = this.scheduledMessages;
        synchronized (scheduledMessages) {
            if (!(osTrigger.value instanceof Number)) {
                return false;
            }
            final int n = OSDynamicTriggerController$2.$SwitchMap$com$onesignal$OSTrigger$OSTriggerKind[osTrigger.kind.ordinal()];
            long n2 = 0L;
            Label_0152: {
                long n3;
                long n4;
                if (n != 1) {
                    if (n != 2) {
                        n2 = 0L;
                        break Label_0152;
                    }
                    if (OneSignal.getInAppMessageController().isInAppMessageShowing()) {
                        return false;
                    }
                    final Date lastTimeInAppDismissed = OneSignal.getInAppMessageController().lastTimeInAppDismissed;
                    if (lastTimeInAppDismissed == null) {
                        n2 = 999999L;
                        break Label_0152;
                    }
                    n3 = new Date().getTime();
                    n4 = lastTimeInAppDismissed.getTime();
                }
                else {
                    n3 = new Date().getTime();
                    n4 = OSDynamicTriggerController.sessionLaunchTime.getTime();
                }
                n2 = n3 - n4;
            }
            final String triggerId = osTrigger.triggerId;
            final long n5 = (long)(((Number)osTrigger.value).doubleValue() * 1000.0);
            if (evaluateTimeIntervalWithOperator((double)n5, (double)n2, osTrigger.operatorType)) {
                this.observer.messageDynamicTriggerCompleted(triggerId);
                return true;
            }
            final long n6 = n5 - n2;
            if (n6 <= 0L) {
                return false;
            }
            if (this.scheduledMessages.contains((Object)triggerId)) {
                return false;
            }
            OSDynamicTriggerTimer.scheduleTrigger(new TimerTask(this, triggerId) {
                final OSDynamicTriggerController this$0;
                final String val$triggerId;
                
                public void run() {
                    this.this$0.scheduledMessages.remove((Object)this.val$triggerId);
                    this.this$0.observer.messageTriggerConditionChanged();
                }
            }, triggerId, n6);
            this.scheduledMessages.add((Object)triggerId);
            return false;
        }
    }
    
    interface OSDynamicTriggerControllerObserver
    {
        void messageDynamicTriggerCompleted(final String p0);
        
        void messageTriggerConditionChanged();
    }
}
