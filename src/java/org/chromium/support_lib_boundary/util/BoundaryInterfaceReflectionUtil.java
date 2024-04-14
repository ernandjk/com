package org.chromium.support_lib_boundary.util;

import java.lang.reflect.InvocationTargetException;
import android.os.Build;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

public class BoundaryInterfaceReflectionUtil
{
    static final boolean $assertionsDisabled = false;
    
    public static <T> T castToSuppLibClass(final Class<T> clazz, final InvocationHandler invocationHandler) {
        if (invocationHandler == null) {
            return null;
        }
        return clazz.cast(Proxy.newProxyInstance(BoundaryInterfaceReflectionUtil.class.getClassLoader(), new Class[] { clazz }, invocationHandler));
    }
    
    public static boolean containsFeature(final Collection<String> collection, final String s) {
        if (!collection.contains((Object)s)) {
            if (isDebuggable()) {
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append(":dev");
                if (collection.contains((Object)sb.toString())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public static boolean containsFeature(final String[] array, final String s) {
        return containsFeature((Collection<String>)Arrays.asList((Object[])array), s);
    }
    
    public static InvocationHandler createInvocationHandlerFor(final Object o) {
        if (o == null) {
            return null;
        }
        return (InvocationHandler)new InvocationHandlerWithDelegateGetter(o);
    }
    
    public static InvocationHandler[] createInvocationHandlersForArray(final Object[] array) {
        if (array == null) {
            return null;
        }
        final int length = array.length;
        final InvocationHandler[] array2 = new InvocationHandler[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = createInvocationHandlerFor(array[i]);
        }
        return array2;
    }
    
    public static Method dupeMethod(final Method method, final ClassLoader loader) throws ClassNotFoundException, NoSuchMethodException {
        return Class.forName(method.getDeclaringClass().getName(), true, loader).getDeclaredMethod(method.getName(), (Class<?>[])method.getParameterTypes());
    }
    
    public static Object getDelegateFromInvocationHandler(final InvocationHandler invocationHandler) {
        if (invocationHandler == null) {
            return null;
        }
        return ((InvocationHandlerWithDelegateGetter)invocationHandler).getDelegate();
    }
    
    public static boolean instanceOfInOwnClassLoader(final Object obj, final String name) {
        try {
            return Class.forName(name, false, obj.getClass().getClassLoader()).isInstance(obj);
        }
        catch (final ClassNotFoundException ex) {
            return false;
        }
    }
    
    private static boolean isDebuggable() {
        return "eng".equals((Object)Build.TYPE) || "userdebug".equals((Object)Build.TYPE);
    }
    
    private static class InvocationHandlerWithDelegateGetter implements InvocationHandler
    {
        private final Object mDelegate;
        
        public InvocationHandlerWithDelegateGetter(final Object mDelegate) {
            this.mDelegate = mDelegate;
        }
        
        public Object getDelegate() {
            return this.mDelegate;
        }
        
        public Object invoke(Object invoke, final Method method, final Object[] array) throws Throwable {
            final ClassLoader classLoader = this.mDelegate.getClass().getClassLoader();
            try {
                invoke = BoundaryInterfaceReflectionUtil.dupeMethod(method, classLoader).invoke(this.mDelegate, array);
                return invoke;
            }
            catch (final ReflectiveOperationException ex) {
                final StringBuilder sb = new StringBuilder("Reflection failed for method ");
                sb.append((Object)method);
                throw new RuntimeException(sb.toString(), (Throwable)ex);
            }
            catch (final InvocationTargetException ex2) {
                throw ex2.getTargetException();
            }
        }
    }
}
