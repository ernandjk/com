package com.getcapacitor.plugin.util;

enum MimeType
{
    private static final MimeType[] $VALUES;
    
    APPLICATION_JSON("application/json"), 
    APPLICATION_VND_API_JSON("application/vnd.api+json"), 
    TEXT_HTML("text/html");
    
    private final String value;
    
    private static /* synthetic */ MimeType[] $values() {
        return new MimeType[] { MimeType.APPLICATION_JSON, MimeType.APPLICATION_VND_API_JSON, MimeType.TEXT_HTML };
    }
    
    static {
        $VALUES = $values();
    }
    
    private MimeType(final String value) {
        this.value = value;
    }
    
    String getValue() {
        return this.value;
    }
}
