package com.getcapacitor;

class PluginInvocationException extends Exception
{
    public PluginInvocationException(final String s) {
        super(s);
    }
    
    public PluginInvocationException(final String s, final Throwable t) {
        super(s, t);
    }
    
    public PluginInvocationException(final Throwable t) {
        super(t);
    }
}
