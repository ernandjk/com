package com.onesignal;

import com.onesignal.language.LanguageContext;

class OSInAppMessageControllerFactory
{
    private static final Object LOCK;
    private OSInAppMessageController controller;
    
    static {
        LOCK = new Object();
    }
    
    public OSInAppMessageController getController(final OneSignalDbHelper oneSignalDbHelper, final OSTaskController osTaskController, final OSLogger osLogger, final OSSharedPreferences osSharedPreferences, final LanguageContext languageContext) {
        if (this.controller == null) {
            final Object lock = OSInAppMessageControllerFactory.LOCK;
            synchronized (lock) {
                if (this.controller == null) {
                    this.controller = new OSInAppMessageController(oneSignalDbHelper, osTaskController, osLogger, osSharedPreferences, languageContext);
                }
            }
        }
        return this.controller;
    }
}
