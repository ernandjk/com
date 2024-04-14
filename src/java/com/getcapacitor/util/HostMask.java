package com.getcapacitor.util;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public interface HostMask
{
    boolean matches(final String p0);
    
    public static class Parser
    {
        private static HostMask NOTHING;
        
        static {
            Parser.NOTHING = (HostMask)new HostMask$Nothing();
        }
        
        public static HostMask parse(final String s) {
            Object o;
            if (s == null) {
                o = Parser.NOTHING;
            }
            else {
                o = HostMask$Simple.parse(s);
            }
            return (HostMask)o;
        }
        
        public static HostMask parse(final String[] array) {
            Object o;
            if (array == null) {
                o = Parser.NOTHING;
            }
            else {
                o = HostMask$Any.parse(array);
            }
            return (HostMask)o;
        }
    }
    
    public static class Util
    {
        static boolean matches(final String s, final String s2) {
            return s != null && ("*".equals((Object)s) || (s2 != null && s.toUpperCase().equals((Object)s2.toUpperCase())));
        }
        
        static List<String> splitAndReverse(final String s) {
            if (s != null) {
                final List list = Arrays.asList((Object[])s.split("\\."));
                Collections.reverse(list);
                return (List<String>)list;
            }
            throw new IllegalArgumentException("Can not split null argument");
        }
    }
}
