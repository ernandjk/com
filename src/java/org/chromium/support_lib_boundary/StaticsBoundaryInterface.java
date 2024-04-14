package org.chromium.support_lib_boundary;

import java.util.List;
import java.util.Set;
import android.webkit.ValueCallback;
import android.content.Context;
import android.net.Uri;

public interface StaticsBoundaryInterface
{
    Uri getSafeBrowsingPrivacyPolicyUrl();
    
    String getVariationsHeader();
    
    void initSafeBrowsing(final Context p0, final ValueCallback<Boolean> p1);
    
    boolean isMultiProcessEnabled();
    
    void setSafeBrowsingAllowlist(final Set<String> p0, final ValueCallback<Boolean> p1);
    
    void setSafeBrowsingWhitelist(final List<String> p0, final ValueCallback<Boolean> p1);
}
