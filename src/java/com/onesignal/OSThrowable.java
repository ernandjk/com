package com.onesignal;

class OSThrowable
{
    static class OSMainThreadException extends RuntimeException
    {
        public OSMainThreadException(final String s) {
            super(s);
        }
    }
}
