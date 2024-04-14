package org.chromium.support_lib_boundary;

import android.webkit.WebResourceResponse;
import android.webkit.WebResourceRequest;

public interface ServiceWorkerClientBoundaryInterface extends FeatureFlagHolderBoundaryInterface
{
    WebResourceResponse shouldInterceptRequest(final WebResourceRequest p0);
}
