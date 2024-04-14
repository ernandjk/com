package com.getcapacitor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.content.res.AssetManager;
import android.os.Environment;
import android.provider.MediaStore$Audio$Media;
import android.provider.MediaStore$Video$Media;
import android.provider.MediaStore$Images$Media;
import android.content.ContentUris;
import android.provider.DocumentsContract;
import java.io.InputStream;
import android.database.Cursor;
import java.io.FileOutputStream;
import java.io.File;
import android.content.Context;
import android.net.Uri;

public class FileUtils
{
    private static String CapacitorFileScheme = "/_capacitor_file_";
    
    private static String getCopyFilePath(final Uri uri, final Context context) {
        final Cursor query = context.getContentResolver().query(uri, (String[])null, (String)null, (String[])null, (String)null);
        final int columnIndex = query.getColumnIndex("_display_name");
        query.moveToFirst();
        final File file = new File(context.getFilesDir(), query.getString(columnIndex));
        try {
            final InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final byte[] array = new byte[Math.min(openInputStream.available(), 1048576)];
            while (true) {
                final int read = openInputStream.read(array);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(array, 0, read);
            }
            openInputStream.close();
            fileOutputStream.close();
            if (query != null) {
                query.close();
            }
            return file.getPath();
        }
        catch (final Exception ex) {
            return null;
        }
        finally {
            if (query != null) {
                query.close();
            }
        }
    }
    
    private static String getDataColumn(Context copyFilePath, final Uri uri, String query, String[] string) {
        Object o = null;
        final String s = null;
        final IllegalArgumentException ex = null;
        while (true) {
            try {
                Label_0125: {
                    try {
                        query = (String)copyFilePath.getContentResolver().query(uri, new String[] { "_data" }, query, (String[])(Object)string, (String)null);
                        string = ex;
                        if (query != null) {
                            string = ex;
                            try {
                                if (((Cursor)query).moveToFirst()) {
                                    string = (IllegalArgumentException)((Cursor)query).getString(((Cursor)query).getColumnIndexOrThrow("_data"));
                                }
                            }
                            catch (final IllegalArgumentException string) {
                                break Label_0104;
                            }
                            finally {
                                o = query;
                            }
                        }
                        if (query != null) {
                            ((Cursor)query).close();
                        }
                        if (string == null) {
                            return getCopyFilePath(uri, copyFilePath);
                        }
                        return (String)string;
                    }
                    finally {
                        break Label_0125;
                    }
                    copyFilePath = (Context)getCopyFilePath(uri, copyFilePath);
                    if (query != null) {
                        ((Cursor)query).close();
                    }
                    return (String)copyFilePath;
                }
                if (o != null) {
                    ((Cursor)o).close();
                }
                throw copyFilePath;
            }
            catch (final IllegalArgumentException ex2) {
                query = s;
                continue;
            }
            break;
        }
    }
    
    public static String getFileUrlForUri(final Context context, Uri uri) {
        final boolean documentUri = DocumentsContract.isDocumentUri(context, uri);
        final Uri uri2 = null;
        if (documentUri) {
            if (isExternalStorageDocument(uri)) {
                final String documentId = DocumentsContract.getDocumentId(uri);
                final String[] split = documentId.split(":");
                if ("primary".equalsIgnoreCase(split[0])) {
                    return legacyPrimaryPath(split[1]);
                }
                final int index = documentId.indexOf(58, 1);
                final String substring = documentId.substring(0, index);
                final String substring2 = documentId.substring(index + 1);
                final String pathToNonPrimaryVolume = getPathToNonPrimaryVolume(context, substring);
                if (pathToNonPrimaryVolume != null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(pathToNonPrimaryVolume);
                    sb.append("/");
                    sb.append(substring2);
                    final String string = sb.toString();
                    final File file = new File(string);
                    if (file.exists() && file.canRead()) {
                        return string;
                    }
                    return null;
                }
            }
            else {
                if (isDownloadsDocument(uri)) {
                    return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), (long)Long.valueOf(DocumentsContract.getDocumentId(uri))), null, null);
                }
                if (isMediaDocument(uri)) {
                    final String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                    final String s = split2[0];
                    if ("image".equals((Object)s)) {
                        uri = MediaStore$Images$Media.EXTERNAL_CONTENT_URI;
                    }
                    else if ("video".equals((Object)s)) {
                        uri = MediaStore$Video$Media.EXTERNAL_CONTENT_URI;
                    }
                    else {
                        uri = uri2;
                        if ("audio".equals((Object)s)) {
                            uri = MediaStore$Audio$Media.EXTERNAL_CONTENT_URI;
                        }
                    }
                    return getDataColumn(context, uri, "_id=?", new String[] { split2[1] });
                }
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    
    private static String getPathToNonPrimaryVolume(final Context context, final String s) {
        final File[] externalCacheDirs = context.getExternalCacheDirs();
        if (externalCacheDirs != null) {
            for (final File file : externalCacheDirs) {
                if (file != null) {
                    final String absolutePath = file.getAbsolutePath();
                    if (absolutePath != null) {
                        final int index = absolutePath.indexOf(s);
                        if (index != -1) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append(absolutePath.substring(0, index));
                            sb.append(s);
                            return sb.toString();
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static String getPortablePath(final Context context, final String s, final Uri uri) {
        String s3;
        final String s2 = s3 = getFileUrlForUri(context, uri);
        if (s2.startsWith("file://")) {
            s3 = s2.replace((CharSequence)"file://", (CharSequence)"");
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("/_capacitor_file_");
        sb.append(s3);
        return sb.toString();
    }
    
    private static boolean isDownloadsDocument(final Uri uri) {
        return "com.android.providers.downloads.documents".equals((Object)uri.getAuthority());
    }
    
    private static boolean isExternalStorageDocument(final Uri uri) {
        return "com.android.externalstorage.documents".equals((Object)uri.getAuthority());
    }
    
    private static boolean isGooglePhotosUri(final Uri uri) {
        return "com.google.android.apps.photos.content".equals((Object)uri.getAuthority());
    }
    
    private static boolean isMediaDocument(final Uri uri) {
        return "com.android.providers.media.documents".equals((Object)uri.getAuthority());
    }
    
    private static String legacyPrimaryPath(final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append((Object)Environment.getExternalStorageDirectory());
        sb.append("/");
        sb.append(s);
        return sb.toString();
    }
    
    static String readFileFromAssets(AssetManager assetManager, String string) throws IOException {
        assetManager = (AssetManager)new BufferedReader((Reader)new InputStreamReader(assetManager.open((String)string)));
        try {
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final String line = ((BufferedReader)assetManager).readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            }
            string = sb.toString();
            ((BufferedReader)assetManager).close();
            return (String)string;
        }
        finally {
            try {
                ((BufferedReader)assetManager).close();
            }
            finally {
                final Throwable t;
                ((Throwable)string).addSuppressed(t);
            }
        }
    }
    
    static String readFileFromDisk(File file) throws IOException {
        file = (File)new BufferedReader((Reader)new FileReader(file));
        try {
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final String line = ((BufferedReader)file).readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            }
            final String string = sb.toString();
            ((BufferedReader)file).close();
            return string;
        }
        finally {
            try {
                ((BufferedReader)file).close();
            }
            finally {
                final Throwable t;
                final Throwable t2;
                t.addSuppressed(t2);
            }
        }
    }
    
    public enum Type
    {
        private static final Type[] $VALUES;
        
        IMAGE("image");
        
        private String type;
        
        private static /* synthetic */ Type[] $values() {
            return new Type[] { Type.IMAGE };
        }
        
        static {
            $VALUES = $values();
        }
        
        private Type(final String type) {
            this.type = type;
        }
    }
}
