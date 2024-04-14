package com.getcapacitor;

public class ProcessedRoute
{
    private boolean ignoreAssetPath;
    private boolean isAsset;
    private String path;
    
    public String getPath() {
        return this.path;
    }
    
    public boolean isAsset() {
        return this.isAsset;
    }
    
    public boolean isIgnoreAssetPath() {
        return this.ignoreAssetPath;
    }
    
    public void setAsset(final boolean isAsset) {
        this.isAsset = isAsset;
    }
    
    public void setIgnoreAssetPath(final boolean ignoreAssetPath) {
        this.ignoreAssetPath = ignoreAssetPath;
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
}
