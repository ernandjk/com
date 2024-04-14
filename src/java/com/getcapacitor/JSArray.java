package com.getcapacitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.json.JSONException;
import org.json.JSONArray;

public class JSArray extends JSONArray
{
    public JSArray() {
    }
    
    public JSArray(final Object o) throws JSONException {
        super(o);
    }
    
    public JSArray(final String s) throws JSONException {
        super(s);
    }
    
    public JSArray(final Collection collection) {
        super(collection);
    }
    
    public static JSArray from(final Object o) {
        try {
            return new JSArray(o);
        }
        catch (final JSONException ex) {
            return null;
        }
    }
    
    public <E> List<E> toList() throws JSONException {
        final ArrayList list = new ArrayList();
        int i = 0;
        while (i < this.length()) {
            this.get(i);
            try {
                ((List)list).add(this.get(i));
                ++i;
                continue;
            }
            catch (final Exception ex) {
                throw new JSONException("Not all items are instances of the given type");
            }
            break;
        }
        return (List<E>)list;
    }
}
