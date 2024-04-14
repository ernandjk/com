package com.getcapacitor.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String alias() default "";
    
    String[] strings() default {};
}
