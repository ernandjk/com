package com.onesignal;

import java.util.Set;

class OSSharedPreferencesWrapper implements OSSharedPreferences
{
    public boolean getBool(final String s, final String s2, final boolean b) {
        return OneSignalPrefs.getBool(s, s2, b);
    }
    
    public int getInt(final String s, final String s2, final int n) {
        return OneSignalPrefs.getInt(s, s2, n);
    }
    
    public long getLong(final String s, final String s2, final long n) {
        return OneSignalPrefs.getLong(s, s2, n);
    }
    
    public Object getObject(final String s, final String s2, final Object o) {
        return OneSignalPrefs.getObject(s, s2, o);
    }
    
    public String getOutcomesV2KeyName() {
        return "PREFS_OS_OUTCOMES_V2";
    }
    
    public String getPreferencesName() {
        return OneSignalPrefs.PREFS_ONESIGNAL;
    }
    
    public String getString(final String s, final String s2, final String s3) {
        return OneSignalPrefs.getString(s, s2, s3);
    }
    
    public Set<String> getStringSet(final String s, final String s2, final Set<String> set) {
        return (Set<String>)OneSignalPrefs.getStringSet(s, s2, (Set)set);
    }
    
    public void saveBool(final String s, final String s2, final boolean b) {
        OneSignalPrefs.saveBool(s, s2, b);
    }
    
    public void saveInt(final String s, final String s2, final int n) {
        OneSignalPrefs.saveInt(s, s2, n);
    }
    
    public void saveLong(final String s, final String s2, final long n) {
        OneSignalPrefs.saveLong(s, s2, n);
    }
    
    public void saveObject(final String s, final String s2, final Object o) {
        OneSignalPrefs.saveObject(s, s2, o);
    }
    
    public void saveString(final String s, final String s2, final String s3) {
        OneSignalPrefs.saveString(s, s2, s3);
    }
    
    public void saveStringSet(final String s, final String s2, final Set<String> set) {
        OneSignalPrefs.saveStringSet(s, s2, (Set)set);
    }
}
