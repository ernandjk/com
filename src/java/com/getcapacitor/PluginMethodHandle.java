package com.getcapacitor;

import java.lang.reflect.Method;

public class PluginMethodHandle
{
    private final Method method;
    private final String name;
    private final String returnType;
    
    public PluginMethodHandle(final Method method, final PluginMethod pluginMethod) {
        this.method = method;
        this.name = method.getName();
        this.returnType = pluginMethod.returnType();
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getReturnType() {
        return this.returnType;
    }
}
