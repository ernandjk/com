package com.onesignal;

class OSLogWrapper implements OSLogger
{
    public void debug(final String s) {
        OneSignal.Log(OneSignal$LOG_LEVEL.DEBUG, s);
    }
    
    public void error(final String s) {
        OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, s);
    }
    
    public void error(final String s, final Throwable t) {
        OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, s, t);
    }
    
    public void info(final String s) {
        OneSignal.Log(OneSignal$LOG_LEVEL.INFO, s);
    }
    
    public void verbose(final String s) {
        OneSignal.Log(OneSignal$LOG_LEVEL.VERBOSE, s);
    }
    
    public void warning(final String s) {
        OneSignal.Log(OneSignal$LOG_LEVEL.WARN, s);
    }
}
