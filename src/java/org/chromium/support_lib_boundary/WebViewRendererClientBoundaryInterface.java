package org.chromium.support_lib_boundary;

import java.lang.reflect.InvocationHandler;
import android.webkit.WebView;

public interface WebViewRendererClientBoundaryInterface extends FeatureFlagHolderBoundaryInterface
{
    void onRendererResponsive(final WebView p0, final InvocationHandler p1);
    
    void onRendererUnresponsive(final WebView p0, final InvocationHandler p1);
}
