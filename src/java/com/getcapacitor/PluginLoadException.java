package com.getcapacitor;

public class PluginLoadException extends Exception
{
    public PluginLoadException(final String s) {
        super(s);
    }
    
    public PluginLoadException(final String s, final Throwable t) {
        super(s, t);
    }
    
    public PluginLoadException(final Throwable t) {
        super(t);
    }
}
