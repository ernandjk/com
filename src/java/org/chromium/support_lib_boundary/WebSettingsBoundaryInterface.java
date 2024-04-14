package org.chromium.support_lib_boundary;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.util.Set;

public interface WebSettingsBoundaryInterface
{
    int getDisabledActionModeMenuItems();
    
    boolean getEnterpriseAuthenticationAppLinkPolicyEnabled();
    
    int getForceDark();
    
    int getForceDarkBehavior();
    
    boolean getOffscreenPreRaster();
    
    Set<String> getRequestedWithHeaderOriginAllowList();
    
    boolean getSafeBrowsingEnabled();
    
    int getWebAuthnSupport();
    
    boolean getWillSuppressErrorPage();
    
    boolean isAlgorithmicDarkeningAllowed();
    
    void setAlgorithmicDarkeningAllowed(final boolean p0);
    
    void setDisabledActionModeMenuItems(final int p0);
    
    void setEnterpriseAuthenticationAppLinkPolicyEnabled(final boolean p0);
    
    void setForceDark(final int p0);
    
    void setForceDarkBehavior(final int p0);
    
    void setOffscreenPreRaster(final boolean p0);
    
    void setRequestedWithHeaderOriginAllowList(final Set<String> p0);
    
    void setSafeBrowsingEnabled(final boolean p0);
    
    void setWebAuthnSupport(final int p0);
    
    void setWillSuppressErrorPage(final boolean p0);
    
    @Retention(RetentionPolicy.SOURCE)
    public @interface ForceDarkBehavior {
        public static final int FORCE_DARK_ONLY = 0;
        public static final int MEDIA_QUERY_ONLY = 1;
        public static final int PREFER_MEDIA_QUERY_OVER_FORCE_DARK = 2;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public @interface WebAuthnSupport {
        public static final int APP = 1;
        public static final int BROWSER = 2;
        public static final int NONE = 0;
    }
}
