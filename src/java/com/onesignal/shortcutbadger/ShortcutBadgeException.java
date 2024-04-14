package com.onesignal.shortcutbadger;

public class ShortcutBadgeException extends Exception
{
    public ShortcutBadgeException(final String s) {
        super(s);
    }
    
    public ShortcutBadgeException(final String s, final Exception ex) {
        super(s, (Throwable)ex);
    }
}
