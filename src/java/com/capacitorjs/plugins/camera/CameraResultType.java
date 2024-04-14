package com.capacitorjs.plugins.camera;

public enum CameraResultType
{
    private static final CameraResultType[] $VALUES;
    
    BASE64("base64"), 
    DATAURL("dataUrl"), 
    URI("uri");
    
    private String type;
    
    private static /* synthetic */ CameraResultType[] $values() {
        return new CameraResultType[] { CameraResultType.BASE64, CameraResultType.URI, CameraResultType.DATAURL };
    }
    
    static {
        $VALUES = $values();
    }
    
    private CameraResultType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
