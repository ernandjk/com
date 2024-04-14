package com.onesignal.language;

import com.onesignal.OSSharedPreferences;

public class LanguageContext
{
    private static LanguageContext instance;
    private LanguageProvider strategy;
    
    public LanguageContext(final OSSharedPreferences osSharedPreferences) {
        LanguageContext.instance = this;
        if (osSharedPreferences.getString(osSharedPreferences.getPreferencesName(), "PREFS_OS_LANGUAGE", (String)null) != null) {
            this.strategy = new LanguageProviderAppDefined(osSharedPreferences);
        }
        else {
            this.strategy = new LanguageProviderDevice();
        }
    }
    
    public static LanguageContext getInstance() {
        return LanguageContext.instance;
    }
    
    public String getLanguage() {
        return this.strategy.getLanguage();
    }
    
    public void setStrategy(final LanguageProvider strategy) {
        this.strategy = strategy;
    }
}
