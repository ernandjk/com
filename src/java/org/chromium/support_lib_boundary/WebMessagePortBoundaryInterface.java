package org.chromium.support_lib_boundary;

import android.os.Handler;
import java.lang.reflect.InvocationHandler;

public interface WebMessagePortBoundaryInterface
{
    void close();
    
    void postMessage(final InvocationHandler p0);
    
    void setWebMessageCallback(final InvocationHandler p0);
    
    void setWebMessageCallback(final InvocationHandler p0, final Handler p1);
}
