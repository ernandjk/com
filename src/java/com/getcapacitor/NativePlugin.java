package com.getcapacitor;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
public @interface NativePlugin {
    String name() default "";
    
    int permissionRequestCode() default 9000;
    
    String[] permissions() default {};
    
    int[] requestCodes() default {};
}
