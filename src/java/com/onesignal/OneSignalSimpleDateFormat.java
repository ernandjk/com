package com.onesignal;

import java.util.Locale;
import java.text.SimpleDateFormat;

class OneSignalSimpleDateFormat
{
    static SimpleDateFormat iso8601Format() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    }
}
