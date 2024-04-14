package com.onesignal;

import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class OSObservable<ObserverType, StateType>
{
    private boolean fireOnMainThread;
    private String methodName;
    private List<Object> observers;
    
    OSObservable(final String methodName, final boolean fireOnMainThread) {
        this.methodName = methodName;
        this.fireOnMainThread = fireOnMainThread;
        this.observers = (List<Object>)new ArrayList();
    }
    
    void addObserver(final ObserverType observerType) {
        this.observers.add((Object)new WeakReference((Object)observerType));
    }
    
    void addObserverStrong(final ObserverType observerType) {
        this.observers.add((Object)observerType);
    }
    
    boolean notifyChange(final StateType stateType) {
        final Iterator iterator = this.observers.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            Object o2;
            final Object o = o2 = iterator.next();
            if (o instanceof WeakReference) {
                o2 = ((WeakReference)o).get();
            }
            if (o2 != null) {
                final Class<?> class1 = o2.getClass();
                try {
                    final Method declaredMethod = class1.getDeclaredMethod(this.methodName, stateType.getClass());
                    declaredMethod.setAccessible(true);
                    if (this.fireOnMainThread) {
                        OSUtils.runOnMainUIThread((Runnable)new Runnable(this, declaredMethod, o2, stateType) {
                            final OSObservable this$0;
                            final Method val$method;
                            final Object val$state;
                            final Object val$strongRefObserver;
                            
                            public void run() {
                                try {
                                    this.val$method.invoke(this.val$strongRefObserver, new Object[] { this.val$state });
                                }
                                catch (final InvocationTargetException ex) {
                                    ex.printStackTrace();
                                }
                                catch (final IllegalAccessException ex2) {
                                    ex2.printStackTrace();
                                }
                            }
                        });
                    }
                    else {
                        try {
                            declaredMethod.invoke(o2, new Object[] { stateType });
                        }
                        catch (final InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                        catch (final IllegalAccessException ex2) {
                            ex2.printStackTrace();
                        }
                    }
                    b = true;
                }
                catch (final NoSuchMethodException ex3) {
                    ex3.printStackTrace();
                }
            }
        }
        return b;
    }
    
    void removeObserver(final ObserverType obj) {
        for (int i = 0; i < this.observers.size(); ++i) {
            final Object value = ((WeakReference)this.observers.get(i)).get();
            if (value != null) {
                if (value.equals(obj)) {
                    this.observers.remove(i);
                    break;
                }
            }
        }
    }
}
