package org.chromium.support_lib_boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

public final class ProcessGlobalConfigConstants
{
    public static final String DATA_DIRECTORY_SUFFIX = "DATA_DIRECTORY_SUFFIX";
    
    private ProcessGlobalConfigConstants() {
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.PARAMETER, ElementType.METHOD })
    public @interface ProcessGlobalConfigMapKey {
    }
}
