package com.onesignal;

class BundleCompatFactory
{
    static BundleCompat getInstance() {
        return (BundleCompat)new BundleCompatPersistableBundle();
    }
}
