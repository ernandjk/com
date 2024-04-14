package com.getcapacitor;

public class ServerPath
{
    private final String path;
    private final PathType type;
    
    public ServerPath(final PathType type, final String path) {
        this.type = type;
        this.path = path;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public PathType getType() {
        return this.type;
    }
    
    public enum PathType
    {
        private static final PathType[] $VALUES;
        
        ASSET_PATH, 
        BASE_PATH;
        
        private static /* synthetic */ PathType[] $values() {
            return new PathType[] { PathType.BASE_PATH, PathType.ASSET_PATH };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
