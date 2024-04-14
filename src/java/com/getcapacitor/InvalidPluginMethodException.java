package com.getcapacitor;

class InvalidPluginMethodException extends Exception
{
    public InvalidPluginMethodException(final String s) {
        super(s);
    }
    
    public InvalidPluginMethodException(final String s, final Throwable t) {
        super(s, t);
    }
    
    public InvalidPluginMethodException(final Throwable t) {
        super(t);
    }
}
