package com.getcapacitor;

import java.util.Locale;

public enum PermissionState
{
    private static final PermissionState[] $VALUES;
    
    DENIED("denied"), 
    GRANTED("granted"), 
    PROMPT("prompt"), 
    PROMPT_WITH_RATIONALE("prompt-with-rationale");
    
    private String state;
    
    private static /* synthetic */ PermissionState[] $values() {
        return new PermissionState[] { PermissionState.GRANTED, PermissionState.DENIED, PermissionState.PROMPT, PermissionState.PROMPT_WITH_RATIONALE };
    }
    
    static {
        $VALUES = $values();
    }
    
    private PermissionState(final String state) {
        this.state = state;
    }
    
    public static PermissionState byState(final String s) {
        return valueOf(s.toUpperCase(Locale.ROOT).replace('-', '_'));
    }
    
    public String toString() {
        return this.state;
    }
}
