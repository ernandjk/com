package com.capacitorjs.plugins.camera;

public class CameraSettings
{
    public static final boolean DEFAULT_CORRECT_ORIENTATION = true;
    public static final int DEFAULT_QUALITY = 90;
    public static final boolean DEFAULT_SAVE_IMAGE_TO_GALLERY = false;
    private boolean allowEditing;
    private int height;
    private int quality;
    private CameraResultType resultType;
    private boolean saveToGallery;
    private boolean shouldCorrectOrientation;
    private boolean shouldResize;
    private CameraSource source;
    private int width;
    
    public CameraSettings() {
        this.resultType = CameraResultType.BASE64;
        this.quality = 90;
        this.shouldResize = false;
        this.shouldCorrectOrientation = true;
        this.saveToGallery = false;
        this.allowEditing = false;
        this.width = 0;
        this.height = 0;
        this.source = CameraSource.PROMPT;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getQuality() {
        return this.quality;
    }
    
    public CameraResultType getResultType() {
        return this.resultType;
    }
    
    public CameraSource getSource() {
        return this.source;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public boolean isAllowEditing() {
        return this.allowEditing;
    }
    
    public boolean isSaveToGallery() {
        return this.saveToGallery;
    }
    
    public boolean isShouldCorrectOrientation() {
        return this.shouldCorrectOrientation;
    }
    
    public boolean isShouldResize() {
        return this.shouldResize;
    }
    
    public void setAllowEditing(final boolean allowEditing) {
        this.allowEditing = allowEditing;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public void setQuality(final int quality) {
        this.quality = quality;
    }
    
    public void setResultType(final CameraResultType resultType) {
        this.resultType = resultType;
    }
    
    public void setSaveToGallery(final boolean saveToGallery) {
        this.saveToGallery = saveToGallery;
    }
    
    public void setShouldCorrectOrientation(final boolean shouldCorrectOrientation) {
        this.shouldCorrectOrientation = shouldCorrectOrientation;
    }
    
    public void setShouldResize(final boolean shouldResize) {
        this.shouldResize = shouldResize;
    }
    
    public void setSource(final CameraSource source) {
        this.source = source;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
}
