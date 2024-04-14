package com.getcapacitor.plugin.util;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.StrictMode;
import android.os.StrictMode$ThreadPolicy$Builder;
import java.net.URL;
import java.net.HttpURLConnection;
import androidx.core.content.FileProvider;
import android.net.Uri$Builder;
import android.net.Uri;
import java.util.UUID;
import java.io.File;
import android.content.res.Resources;
import com.getcapacitor.Logger;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.content.Context;

public final class AssetUtil
{
    public static final int RESOURCE_ID_ZERO_VALUE = 0;
    private static final String STORAGE_FOLDER = "/capacitorassets";
    private final Context context;
    
    private AssetUtil(final Context context) {
        this.context = context;
    }
    
    private void copyFile(final InputStream inputStream, final FileOutputStream fileOutputStream) {
        final byte[] array = new byte[1024];
        try {
            while (true) {
                final int read = inputStream.read(array);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(array, 0, read);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (final Exception ex) {
            Logger.error("Error copying", (Throwable)ex);
        }
    }
    
    private String getBaseName(final String s) {
        String substring;
        if (s.contains((CharSequence)"/")) {
            substring = s.substring(s.lastIndexOf(47) + 1);
        }
        else {
            substring = s;
        }
        String substring2 = substring;
        if (s.contains((CharSequence)".")) {
            substring2 = substring.substring(0, substring.lastIndexOf(46));
        }
        return substring2;
    }
    
    public static AssetUtil getInstance(final Context context) {
        return new AssetUtil(context);
    }
    
    private String getPkgName(final Resources resources) {
        String packageName;
        if (resources == Resources.getSystem()) {
            packageName = "android";
        }
        else {
            packageName = this.context.getPackageName();
        }
        return packageName;
    }
    
    private int getResId(final Resources resources, String baseName) {
        final String pkgName = this.getPkgName(resources);
        baseName = this.getBaseName(baseName);
        int n;
        if ((n = resources.getIdentifier(baseName, "mipmap", pkgName)) == 0) {
            n = resources.getIdentifier(baseName, "drawable", pkgName);
        }
        int identifier;
        if ((identifier = n) == 0) {
            identifier = resources.getIdentifier(baseName, "raw", pkgName);
        }
        return identifier;
    }
    
    public static String getResourceBaseName(final String s) {
        if (s == null) {
            return null;
        }
        if (s.contains((CharSequence)"/")) {
            return s.substring(s.lastIndexOf(47) + 1);
        }
        String substring = s;
        if (s.contains((CharSequence)".")) {
            substring = s.substring(0, s.lastIndexOf(46));
        }
        return substring;
    }
    
    public static int getResourceID(final Context context, final String s, final String s2) {
        return context.getResources().getIdentifier(s, s2, context.getPackageName());
    }
    
    private File getTmpFile() {
        return this.getTmpFile(UUID.randomUUID().toString());
    }
    
    private File getTmpFile(final String s) {
        File file;
        if ((file = this.context.getExternalCacheDir()) == null) {
            file = this.context.getCacheDir();
        }
        if (file == null) {
            Logger.error(Logger.tags("Asset"), "Missing cache dir", null);
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(file.toString());
        sb.append("/capacitorassets");
        final String string = sb.toString();
        new File(string).mkdir();
        return new File(string, s);
    }
    
    private Uri getUriForResourcePath(String replaceFirst) {
        final Resources resources = this.context.getResources();
        replaceFirst = replaceFirst.replaceFirst("res://", "");
        final int resId = this.getResId(replaceFirst);
        if (resId == 0) {
            final StringBuilder sb = new StringBuilder("File not found: ");
            sb.append(replaceFirst);
            Logger.error(sb.toString());
            return Uri.EMPTY;
        }
        return new Uri$Builder().scheme("android.resource").authority(resources.getResourcePackageName(resId)).appendPath(resources.getResourceTypeName(resId)).appendPath(resources.getResourceEntryName(resId)).build();
    }
    
    private Uri getUriFromAsset(String replaceFirst) {
        replaceFirst = replaceFirst.replaceFirst("file:/", "www").replaceFirst("\\?.*$", "");
        final File tmpFile = this.getTmpFile(replaceFirst.substring(replaceFirst.lastIndexOf(47) + 1));
        if (tmpFile == null) {
            return Uri.EMPTY;
        }
        try {
            this.copyFile(this.context.getAssets().open(replaceFirst), new FileOutputStream(tmpFile));
            return this.getUriFromFile(tmpFile);
        }
        catch (final Exception ex) {
            final StringBuilder sb = new StringBuilder("File not found: assets/");
            sb.append(replaceFirst);
            Logger.error(sb.toString());
            return Uri.EMPTY;
        }
    }
    
    private Uri getUriFromFile(final File file) {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.context.getPackageName());
            sb.append(".provider");
            return FileProvider.getUriForFile(this.context, sb.toString(), file);
        }
        catch (final IllegalArgumentException ex) {
            Logger.error("File not supported by provider", (Throwable)ex);
            return Uri.EMPTY;
        }
    }
    
    private Uri getUriFromPath(final String s) {
        final File file = new File(s.replaceFirst("file://", "").replaceFirst("\\?.*$", ""));
        if (!file.exists()) {
            final StringBuilder sb = new StringBuilder("File not found: ");
            sb.append(file.getAbsolutePath());
            Logger.error(sb.toString());
            return Uri.EMPTY;
        }
        return this.getUriFromFile(file);
    }
    
    private Uri getUriFromRemote(final String s) {
        final File tmpFile = this.getTmpFile();
        if (tmpFile == null) {
            return Uri.EMPTY;
        }
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(s).openConnection();
            StrictMode.setThreadPolicy(new StrictMode$ThreadPolicy$Builder().permitAll().build());
            httpURLConnection.setRequestProperty("Connection", "close");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();
            this.copyFile(httpURLConnection.getInputStream(), new FileOutputStream(tmpFile));
            return this.getUriFromFile(tmpFile);
        }
        catch (final IOException ex) {
            Logger.error(Logger.tags("Asset"), "No Input can be created from http Stream", (Throwable)ex);
        }
        catch (final FileNotFoundException ex2) {
            Logger.error(Logger.tags("Asset"), "Failed to create new File from HTTP Content", (Throwable)ex2);
        }
        catch (final MalformedURLException ex3) {
            Logger.error(Logger.tags("Asset"), "Incorrect URL", (Throwable)ex3);
        }
        return Uri.EMPTY;
    }
    
    public Bitmap getIconFromUri(final Uri uri) throws IOException {
        return BitmapFactory.decodeStream(this.context.getContentResolver().openInputStream(uri));
    }
    
    public int getResId(final String s) {
        int n;
        if ((n = this.getResId(this.context.getResources(), s)) == 0) {
            n = this.getResId(Resources.getSystem(), s);
        }
        return n;
    }
    
    public Uri parse(final String s) {
        if (s == null || s.isEmpty()) {
            return Uri.EMPTY;
        }
        if (s.startsWith("res:")) {
            return this.getUriForResourcePath(s);
        }
        if (s.startsWith("file:///")) {
            return this.getUriFromPath(s);
        }
        if (s.startsWith("file://")) {
            return this.getUriFromAsset(s);
        }
        if (s.startsWith("http")) {
            return this.getUriFromRemote(s);
        }
        if (s.startsWith("content://")) {
            return Uri.parse(s);
        }
        return Uri.EMPTY;
    }
}
