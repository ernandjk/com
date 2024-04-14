package org.chromium.support_lib_boundary;

import java.util.Set;

public interface ServiceWorkerWebSettingsBoundaryInterface
{
    boolean getAllowContentAccess();
    
    boolean getAllowFileAccess();
    
    boolean getBlockNetworkLoads();
    
    int getCacheMode();
    
    Set<String> getRequestedWithHeaderOriginAllowList();
    
    void setAllowContentAccess(final boolean p0);
    
    void setAllowFileAccess(final boolean p0);
    
    void setBlockNetworkLoads(final boolean p0);
    
    void setCacheMode(final int p0);
    
    void setRequestedWithHeaderOriginAllowList(final Set<String> p0);
}
