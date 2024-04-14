package com.getcapacitor;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginMethod {
    public static final String RETURN_CALLBACK = "callback";
    public static final String RETURN_NONE = "none";
    public static final String RETURN_PROMISE = "promise";
    
    String returnType() default "promise";
}
