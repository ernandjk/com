package com.getcapacitor;

public class JSExportException extends Exception
{
    public JSExportException(final String s) {
        super(s);
    }
    
    public JSExportException(final String s, final Throwable t) {
        super(s, t);
    }
    
    public JSExportException(final Throwable t) {
        super(t);
    }
}
