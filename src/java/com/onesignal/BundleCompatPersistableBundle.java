package com.onesignal;

import android.os.Parcelable;
import android.os.PersistableBundle;

class BundleCompatPersistableBundle implements BundleCompat<PersistableBundle>
{
    private PersistableBundle mBundle;
    
    BundleCompatPersistableBundle() {
        this.mBundle = new PersistableBundle();
    }
    
    BundleCompatPersistableBundle(final PersistableBundle mBundle) {
        this.mBundle = mBundle;
    }
    
    public boolean containsKey(final String s) {
        return this.mBundle.containsKey(s);
    }
    
    public boolean getBoolean(final String s) {
        return this.mBundle.getBoolean(s);
    }
    
    public boolean getBoolean(final String s, final boolean b) {
        return this.mBundle.getBoolean(s, b);
    }
    
    public PersistableBundle getBundle() {
        return this.mBundle;
    }
    
    public Integer getInt(final String s) {
        return this.mBundle.getInt(s);
    }
    
    public Long getLong(final String s) {
        return this.mBundle.getLong(s);
    }
    
    public String getString(final String s) {
        return this.mBundle.getString(s);
    }
    
    public void putBoolean(final String s, final Boolean b) {
        this.mBundle.putBoolean(s, (boolean)b);
    }
    
    public void putInt(final String s, final Integer n) {
        this.mBundle.putInt(s, (int)n);
    }
    
    public void putLong(final String s, final Long n) {
        this.mBundle.putLong(s, (long)n);
    }
    
    public void putString(final String s, final String s2) {
        this.mBundle.putString(s, s2);
    }
    
    public void setBundle(final Parcelable parcelable) {
        this.mBundle = (PersistableBundle)parcelable;
    }
}
