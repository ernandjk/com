package com.getcapacitor.plugin.util;

import java.io.IOException;
import java.io.InputStream;

public interface ICapacitorHttpUrlConnection
{
    InputStream getErrorStream();
    
    String getHeaderField(final String p0);
    
    InputStream getInputStream() throws IOException;
}
