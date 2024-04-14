package com.google.errorprone.annotations.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Deprecated
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
public @interface LockMethod {
    String[] value();
}
