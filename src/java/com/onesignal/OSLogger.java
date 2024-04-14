package com.onesignal;

public interface OSLogger
{
    void debug(final String p0);
    
    void error(final String p0);
    
    void error(final String p0, final Throwable p1);
    
    void info(final String p0);
    
    void verbose(final String p0);
    
    void warning(final String p0);
}
