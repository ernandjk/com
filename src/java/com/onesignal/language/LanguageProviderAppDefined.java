package com.onesignal.language;

import com.onesignal.OSSharedPreferences;

public class LanguageProviderAppDefined implements LanguageProvider
{
    private static final String DEFAULT_LANGUAGE = "en";
    public static final String PREFS_OS_LANGUAGE = "PREFS_OS_LANGUAGE";
    private final OSSharedPreferences preferences;
    
    public LanguageProviderAppDefined(final OSSharedPreferences preferences) {
        this.preferences = preferences;
    }
    
    @Override
    public String getLanguage() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getString(preferences.getPreferencesName(), "PREFS_OS_LANGUAGE", "en");
    }
    
    public void setLanguage(final String s) {
        final OSSharedPreferences preferences = this.preferences;
        preferences.saveString(preferences.getPreferencesName(), "PREFS_OS_LANGUAGE", s);
    }
}
