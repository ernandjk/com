package com.onesignal;

import java.util.Map;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

class OSTriggerController
{
    OSDynamicTriggerController dynamicTriggerController;
    private final ConcurrentHashMap<String, Object> triggers;
    
    OSTriggerController(final OSDynamicTriggerController.OSDynamicTriggerControllerObserver osDynamicTriggerControllerObserver) {
        this.triggers = (ConcurrentHashMap<String, Object>)new ConcurrentHashMap();
        this.dynamicTriggerController = new OSDynamicTriggerController(osDynamicTriggerControllerObserver);
    }
    
    private boolean evaluateAndTriggers(final ArrayList<OSTrigger> list) {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            if (!this.evaluateTrigger((OSTrigger)iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    private boolean evaluateTrigger(final OSTrigger osTrigger) {
        final OSTrigger.OSTriggerKind kind = osTrigger.kind;
        final OSTrigger.OSTriggerKind unknown = OSTrigger.OSTriggerKind.UNKNOWN;
        final boolean b = false;
        final boolean b2 = false;
        if (kind == unknown) {
            return false;
        }
        if (osTrigger.kind != OSTrigger.OSTriggerKind.CUSTOM) {
            return this.dynamicTriggerController.dynamicTriggerShouldFire(osTrigger);
        }
        final OSTrigger.OSTriggerOperator operatorType = osTrigger.operatorType;
        final Object value = this.triggers.get((Object)osTrigger.property);
        if (value == null) {
            if (operatorType == OSTrigger.OSTriggerOperator.NOT_EXISTS) {
                return true;
            }
            boolean b3 = b2;
            if (operatorType == OSTrigger.OSTriggerOperator.NOT_EQUAL_TO) {
                b3 = b2;
                if (osTrigger.value != null) {
                    b3 = true;
                }
            }
            return b3;
        }
        else {
            if (operatorType == OSTrigger.OSTriggerOperator.EXISTS) {
                return true;
            }
            if (operatorType == OSTrigger.OSTriggerOperator.NOT_EXISTS) {
                return false;
            }
            if (operatorType == OSTrigger.OSTriggerOperator.CONTAINS) {
                boolean b4 = b;
                if (value instanceof Collection) {
                    b4 = b;
                    if (((Collection)value).contains(osTrigger.value)) {
                        b4 = true;
                    }
                }
                return b4;
            }
            return (value instanceof String && osTrigger.value instanceof String && this.triggerMatchesStringValue((String)osTrigger.value, (String)value, operatorType)) || (osTrigger.value instanceof Number && value instanceof Number && this.triggerMatchesNumericValue((Number)osTrigger.value, (Number)value, operatorType)) || this.triggerMatchesFlex(osTrigger.value, value, operatorType);
        }
    }
    
    private boolean triggerMatchesFlex(final Object o, final Object o2, final OSTrigger.OSTriggerOperator osTriggerOperator) {
        if (o == null) {
            return false;
        }
        if (osTriggerOperator.checksEquality()) {
            final String string = o.toString();
            String s = o2.toString();
            if (o2 instanceof Number) {
                s = new DecimalFormat("0.#").format(o2);
            }
            return this.triggerMatchesStringValue(string, s, osTriggerOperator);
        }
        return o2 instanceof String && o instanceof Number && this.triggerMatchesNumericValueFlex((Number)o, (String)o2, osTriggerOperator);
    }
    
    private boolean triggerMatchesNumericValue(final Number n, final Number n2, final OSTrigger.OSTriggerOperator osTriggerOperator) {
        final double doubleValue = n.doubleValue();
        final double doubleValue2 = n2.doubleValue();
        final int n3 = OSTriggerController$1.$SwitchMap$com$onesignal$OSTrigger$OSTriggerOperator[osTriggerOperator.ordinal()];
        final boolean b = true;
        final boolean b2 = true;
        final boolean b3 = true;
        boolean b4 = true;
        final boolean b5 = true;
        final boolean b6 = true;
        switch (n3) {
            default: {
                return false;
            }
            case 9: {
                final double n4 = dcmpl(doubleValue2, doubleValue);
                boolean b7 = b6;
                if (n4 <= 0) {
                    b7 = (n4 == 0 && b6);
                }
                return b7;
            }
            case 8: {
                boolean b8 = b;
                if (doubleValue2 >= doubleValue) {
                    b8 = (doubleValue2 == doubleValue && b);
                }
                return b8;
            }
            case 7: {
                return doubleValue2 > doubleValue && b2;
            }
            case 6: {
                return doubleValue2 < doubleValue && b3;
            }
            case 3:
            case 4:
            case 5: {
                final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                final StringBuilder sb = new StringBuilder("Attempted to use an invalid operator with a numeric value: ");
                sb.append(osTriggerOperator.toString());
                OneSignal.onesignalLog(error, sb.toString());
                return false;
            }
            case 2: {
                if (doubleValue2 == doubleValue) {
                    b4 = false;
                }
                return b4;
            }
            case 1: {
                return doubleValue2 == doubleValue && b5;
            }
        }
    }
    
    private boolean triggerMatchesNumericValueFlex(final Number n, final String s, final OSTrigger.OSTriggerOperator osTriggerOperator) {
        try {
            return this.triggerMatchesNumericValue((Number)n.doubleValue(), (Number)Double.parseDouble(s), osTriggerOperator);
        }
        catch (final NumberFormatException ex) {
            return false;
        }
    }
    
    private boolean triggerMatchesStringValue(final String s, final String s2, final OSTrigger.OSTriggerOperator osTriggerOperator) {
        final int n = OSTriggerController$1.$SwitchMap$com$onesignal$OSTrigger$OSTriggerOperator[osTriggerOperator.ordinal()];
        if (n == 1) {
            return s.equals((Object)s2);
        }
        if (n != 2) {
            final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
            final StringBuilder sb = new StringBuilder("Attempted to use an invalid operator for a string trigger comparison: ");
            sb.append(osTriggerOperator.toString());
            OneSignal.onesignalLog(error, sb.toString());
            return false;
        }
        return s.equals((Object)s2) ^ true;
    }
    
    void addTriggers(final Map<String, Object> map) {
        final ConcurrentHashMap<String, Object> triggers = this.triggers;
        synchronized (triggers) {
            for (final String s : map.keySet()) {
                this.triggers.put((Object)s, map.get((Object)s));
            }
        }
    }
    
    boolean evaluateMessageTriggers(final OSInAppMessageInternal osInAppMessageInternal) {
        if (osInAppMessageInternal.triggers.size() == 0) {
            return true;
        }
        final Iterator iterator = osInAppMessageInternal.triggers.iterator();
        while (iterator.hasNext()) {
            if (this.evaluateAndTriggers((ArrayList<OSTrigger>)iterator.next())) {
                return true;
            }
        }
        return false;
    }
    
    Object getTriggerValue(final String s) {
        final ConcurrentHashMap<String, Object> triggers = this.triggers;
        synchronized (triggers) {
            if (this.triggers.containsKey((Object)s)) {
                return this.triggers.get((Object)s);
            }
            return null;
        }
    }
    
    public ConcurrentHashMap<String, Object> getTriggers() {
        return this.triggers;
    }
    
    boolean isTriggerOnMessage(final OSInAppMessageInternal osInAppMessageInternal, final Collection<String> collection) {
        if (osInAppMessageInternal.triggers == null) {
            return false;
        }
        for (final String s : collection) {
            final Iterator iterator2 = osInAppMessageInternal.triggers.iterator();
            while (iterator2.hasNext()) {
                for (final OSTrigger osTrigger : (ArrayList)iterator2.next()) {
                    if (s.equals((Object)osTrigger.property) || s.equals((Object)osTrigger.triggerId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    boolean messageHasOnlyDynamicTriggers(final OSInAppMessageInternal osInAppMessageInternal) {
        if (osInAppMessageInternal.triggers != null && !osInAppMessageInternal.triggers.isEmpty()) {
            final Iterator iterator = osInAppMessageInternal.triggers.iterator();
            while (iterator.hasNext()) {
                for (final OSTrigger osTrigger : (ArrayList)iterator.next()) {
                    if (osTrigger.kind == OSTrigger.OSTriggerKind.CUSTOM || osTrigger.kind == OSTrigger.OSTriggerKind.UNKNOWN) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    void removeTriggersForKeys(final Collection<String> collection) {
        final ConcurrentHashMap<String, Object> triggers = this.triggers;
        synchronized (triggers) {
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                this.triggers.remove((Object)iterator.next());
            }
        }
    }
}
