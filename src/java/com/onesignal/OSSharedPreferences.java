package com.onesignal;

import java.util.Set;

public interface OSSharedPreferences
{
    boolean getBool(final String p0, final String p1, final boolean p2);
    
    int getInt(final String p0, final String p1, final int p2);
    
    long getLong(final String p0, final String p1, final long p2);
    
    Object getObject(final String p0, final String p1, final Object p2);
    
    String getOutcomesV2KeyName();
    
    String getPreferencesName();
    
    String getString(final String p0, final String p1, final String p2);
    
    Set<String> getStringSet(final String p0, final String p1, final Set<String> p2);
    
    void saveBool(final String p0, final String p1, final boolean p2);
    
    void saveInt(final String p0, final String p1, final int p2);
    
    void saveLong(final String p0, final String p1, final long p2);
    
    void saveObject(final String p0, final String p1, final Object p2);
    
    void saveString(final String p0, final String p1, final String p2);
    
    void saveStringSet(final String p0, final String p1, final Set<String> p2);
}
