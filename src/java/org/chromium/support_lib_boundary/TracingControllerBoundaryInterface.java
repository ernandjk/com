package org.chromium.support_lib_boundary;

import java.util.concurrent.Executor;
import java.io.OutputStream;
import java.util.Collection;

public interface TracingControllerBoundaryInterface
{
    boolean isTracing();
    
    void start(final int p0, final Collection<String> p1, final int p2) throws IllegalStateException, IllegalArgumentException;
    
    boolean stop(final OutputStream p0, final Executor p1);
}
