package com.capacitorjs.plugins.camera;

public enum CameraSource
{
    private static final CameraSource[] $VALUES;
    
    CAMERA("CAMERA"), 
    PHOTOS("PHOTOS"), 
    PROMPT("PROMPT");
    
    private String source;
    
    private static /* synthetic */ CameraSource[] $values() {
        return new CameraSource[] { CameraSource.PROMPT, CameraSource.CAMERA, CameraSource.PHOTOS };
    }
    
    static {
        $VALUES = $values();
    }
    
    private CameraSource(final String source) {
        this.source = source;
    }
    
    public String getSource() {
        return this.source;
    }
}
