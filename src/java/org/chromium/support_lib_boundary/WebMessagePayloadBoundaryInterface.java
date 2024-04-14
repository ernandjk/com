package org.chromium.support_lib_boundary;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

public interface WebMessagePayloadBoundaryInterface extends FeatureFlagHolderBoundaryInterface
{
    byte[] getAsArrayBuffer();
    
    String getAsString();
    
    int getType();
    
    @Retention(RetentionPolicy.SOURCE)
    public @interface WebMessagePayloadType {
        public static final int TYPE_ARRAY_BUFFER = 1;
        public static final int TYPE_STRING = 0;
    }
}
