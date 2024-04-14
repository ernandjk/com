package org.chromium.support_lib_boundary;

import java.lang.reflect.InvocationHandler;

public interface WebMessageCallbackBoundaryInterface extends FeatureFlagHolderBoundaryInterface
{
    void onMessage(final InvocationHandler p0, final InvocationHandler p1);
}
