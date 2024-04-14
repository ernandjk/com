package com.getcapacitor;

import java.util.List;
import java.io.FileInputStream;
import java.io.File;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import android.util.TypedValue;
import android.content.Context;

public class AndroidProtocolHandler
{
    static final boolean $assertionsDisabled = false;
    private Context context;
    
    public AndroidProtocolHandler(final Context context) {
        this.context = context;
    }
    
    private static int getFieldId(final Context context, final String s, final String name) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        final ClassLoader classLoader = context.getClassLoader();
        final StringBuilder sb = new StringBuilder();
        sb.append(context.getPackageName());
        sb.append(".R$");
        sb.append(s);
        return classLoader.loadClass(sb.toString()).getField(name).getInt((Object)null);
    }
    
    private static int getValueType(final Context context, final int n) {
        final TypedValue typedValue = new TypedValue();
        context.getResources().getValue(n, typedValue, true);
        return typedValue.type;
    }
    
    public InputStream openAsset(final String s) throws IOException {
        return this.context.getAssets().open(s, 2);
    }
    
    public InputStream openContentUrl(Uri openInputStream) throws IOException {
        final Integer value = openInputStream.getPort();
        final StringBuilder sb = new StringBuilder();
        sb.append(openInputStream.getScheme());
        sb.append("://");
        sb.append(openInputStream.getHost());
        String s = sb.toString();
        if (value != -1) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(s);
            sb2.append(":");
            sb2.append((Object)value);
            s = sb2.toString();
        }
        final String string = openInputStream.toString();
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(s);
        sb3.append("/_capacitor_content_");
        final String replace = string.replace((CharSequence)sb3.toString(), (CharSequence)"content:/");
        try {
            openInputStream = (Uri)this.context.getContentResolver().openInputStream(Uri.parse(replace));
        }
        catch (final SecurityException ex) {
            final StringBuilder sb4 = new StringBuilder("Unable to open content URL: ");
            sb4.append((Object)openInputStream);
            Logger.error(sb4.toString(), (Throwable)ex);
            openInputStream = null;
        }
        return (InputStream)openInputStream;
    }
    
    public InputStream openFile(final String s) throws IOException {
        return (InputStream)new FileInputStream(new File(s.replace((CharSequence)"/_capacitor_file_", (CharSequence)"")));
    }
    
    public InputStream openResource(final Uri uri) {
        final List pathSegments = uri.getPathSegments();
        Object o = pathSegments.get(pathSegments.size() - 2);
        final String s = ((String)pathSegments.get(pathSegments.size() - 1)).split("\\.")[0];
        try {
            if (this.context.getApplicationContext() != null) {
                this.context = this.context.getApplicationContext();
            }
            final int fieldId = getFieldId(this.context, (String)o, s);
            if (getValueType(this.context, fieldId) == 3) {
                return this.context.getResources().openRawResource(fieldId);
            }
            o = new StringBuilder("Asset not of type string: ");
            ((StringBuilder)o).append((Object)uri);
            Logger.error(((StringBuilder)o).toString());
            return null;
        }
        catch (final NoSuchFieldException o) {}
        catch (final IllegalAccessException o) {}
        catch (final ClassNotFoundException ex) {}
        final StringBuilder sb = new StringBuilder("Unable to open resource URL: ");
        sb.append((Object)uri);
        Logger.error(sb.toString(), (Throwable)o);
        return null;
    }
}
