package com.getcapacitor.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
public @interface CapacitorPlugin {
    String name() default "";
    
    Permission[] permissions() default {};
    
    int[] requestCodes() default {};
}
