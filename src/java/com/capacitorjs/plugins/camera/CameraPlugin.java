package com.capacitorjs.plugins.camera;

import java.util.concurrent.Executor;
import org.json.JSONException;
import java.util.concurrent.Executors;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory$Options;
import android.app.Activity;
import java.util.Map;
import com.getcapacitor.PluginMethod;
import java.io.FileOutputStream;
import android.content.ContentResolver;
import java.io.FileNotFoundException;
import android.provider.MediaStore$Images$Media;
import android.os.Environment;
import android.content.ContentValues;
import java.io.OutputStream;
import android.graphics.Bitmap$CompressFormat;
import com.getcapacitor.FileUtils;
import android.util.Base64;
import com.getcapacitor.annotation.ActivityCallback;
import androidx.activity.result.ActivityResult;
import android.graphics.Bitmap;
import android.content.ActivityNotFoundException;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import android.os.Bundle;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ResolveInfo;
import com.getcapacitor.Bridge$$ExternalSyntheticApiModelOutline0;
import android.os.Parcelable;
import android.content.Context;
import androidx.core.content.FileProvider;
import java.io.File;
import com.getcapacitor.annotation.PermissionCallback;
import android.os.Build$VERSION;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionState;
import com.getcapacitor.PluginCall;
import android.content.Intent;
import android.net.Uri;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(name = "Camera", permissions = { @Permission(alias = "camera", strings = { "android.permission.CAMERA" }), @Permission(alias = "photos", strings = { "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" }), @Permission(alias = "readExternalStorage", strings = { "android.permission.READ_EXTERNAL_STORAGE" }), @Permission(alias = "media", strings = { "android.permission.READ_MEDIA_IMAGES" }) })
public class CameraPlugin extends Plugin
{
    static final String CAMERA = "camera";
    private static final String IMAGE_EDIT_ERROR = "Unable to edit image";
    private static final String IMAGE_FILE_SAVE_ERROR = "Unable to create photo on disk";
    private static final String IMAGE_GALLERY_SAVE_ERROR = "Unable to save the image in the gallery";
    private static final String IMAGE_PROCESS_NO_FILE_ERROR = "Unable to process image, file not found on disk";
    private static final String INVALID_RESULT_TYPE_ERROR = "Invalid resultType option";
    static final String MEDIA = "media";
    private static final String NO_CAMERA_ACTIVITY_ERROR = "Unable to resolve camera activity";
    private static final String NO_CAMERA_ERROR = "Device doesn't have a camera available";
    private static final String NO_PHOTO_ACTIVITY_ERROR = "Unable to resolve photo activity";
    private static final String PERMISSION_DENIED_ERROR_CAMERA = "User denied access to camera";
    private static final String PERMISSION_DENIED_ERROR_PHOTOS = "User denied access to photos";
    static final String PHOTOS = "photos";
    static final String READ_EXTERNAL_STORAGE = "readExternalStorage";
    private static final String UNABLE_TO_PROCESS_IMAGE = "Unable to process image";
    private String imageEditedFileSavePath;
    private String imageFileSavePath;
    private Uri imageFileUri;
    private Uri imagePickedContentUri;
    private boolean isEdited;
    private boolean isFirstRequest;
    private boolean isSaved;
    private CameraSettings settings;
    
    public CameraPlugin() {
        this.isEdited = false;
        this.isFirstRequest = true;
        this.isSaved = false;
        this.settings = new CameraSettings();
    }
    
    @PermissionCallback
    private void cameraPermissionsCallback(final PluginCall pluginCall) {
        if (pluginCall.getMethodName().equals((Object)"pickImages")) {
            this.openPhotos(pluginCall, true, true);
        }
        else {
            if (this.settings.getSource() == CameraSource.CAMERA && this.getPermissionState("camera") != PermissionState.GRANTED) {
                final String logTag = this.getLogTag();
                final StringBuilder sb = new StringBuilder("User denied camera permission: ");
                sb.append(this.getPermissionState("camera").toString());
                Logger.debug(logTag, sb.toString());
                pluginCall.reject("User denied access to camera");
                return;
            }
            if (this.settings.getSource() == CameraSource.PHOTOS) {
                String s;
                if (Build$VERSION.SDK_INT < 30) {
                    s = "photos";
                }
                else if (Build$VERSION.SDK_INT < 33) {
                    s = "readExternalStorage";
                }
                else {
                    s = "media";
                }
                final PermissionState permissionState = this.getPermissionState(s);
                if (permissionState != PermissionState.GRANTED) {
                    final String logTag2 = this.getLogTag();
                    final StringBuilder sb2 = new StringBuilder("User denied photos permission: ");
                    sb2.append(permissionState.toString());
                    Logger.debug(logTag2, sb2.toString());
                    pluginCall.reject("User denied access to photos");
                    return;
                }
            }
            this.doShow(pluginCall);
        }
    }
    
    private boolean checkCameraPermissions(final PluginCall pluginCall) {
        final boolean permissionDeclared = this.isPermissionDeclared("camera");
        final boolean b = !permissionDeclared || this.getPermissionState("camera") == PermissionState.GRANTED;
        final boolean b2 = this.getPermissionState("photos") == PermissionState.GRANTED;
        if (this.settings.isSaveToGallery() && (!b || !b2) && this.isFirstRequest) {
            this.isFirstRequest = false;
            String[] array;
            if (permissionDeclared) {
                array = new String[] { "camera", "photos" };
            }
            else {
                array = new String[] { "photos" };
            }
            this.requestPermissionForAliases(array, pluginCall, "cameraPermissionsCallback");
            return false;
        }
        if (!b) {
            this.requestPermissionForAlias("camera", pluginCall, "cameraPermissionsCallback");
            return false;
        }
        return true;
    }
    
    private boolean checkPhotosPermissions(final PluginCall pluginCall) {
        if (Build$VERSION.SDK_INT < 30) {
            if (this.getPermissionState("photos") != PermissionState.GRANTED) {
                this.requestPermissionForAlias("photos", pluginCall, "cameraPermissionsCallback");
                return false;
            }
        }
        else if (Build$VERSION.SDK_INT < 33) {
            if (this.getPermissionState("readExternalStorage") != PermissionState.GRANTED) {
                this.requestPermissionForAlias("readExternalStorage", pluginCall, "cameraPermissionsCallback");
                return false;
            }
        }
        else if (this.getPermissionState("media") != PermissionState.GRANTED) {
            this.requestPermissionForAlias("media", pluginCall, "cameraPermissionsCallback");
            return false;
        }
        return true;
    }
    
    private Intent createEditIntent(final Uri uri) {
        try {
            final File file = new File(uri.getPath());
            final AppCompatActivity activity = this.getActivity();
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getContext().getPackageName());
            sb.append(".fileprovider");
            final Uri uriForFile = FileProvider.getUriForFile((Context)activity, sb.toString(), file);
            final Intent intent = new Intent("android.intent.action.EDIT");
            intent.setDataAndType(uriForFile, "image/*");
            this.imageEditedFileSavePath = file.getAbsolutePath();
            intent.addFlags(3);
            intent.putExtra("output", (Parcelable)uriForFile);
            List list;
            if (Build$VERSION.SDK_INT >= 33) {
                list = Bridge$$ExternalSyntheticApiModelOutline0.m(this.getContext().getPackageManager(), intent, Bridge$$ExternalSyntheticApiModelOutline0.m(65536L));
            }
            else {
                list = this.legacyQueryIntentActivities(intent);
            }
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.getContext().grantUriPermission(((ResolveInfo)iterator.next()).activityInfo.packageName, uriForFile, 3);
            }
            return intent;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private void deleteImageFile() {
        if (this.imageFileSavePath != null && !this.settings.isSaveToGallery()) {
            final File file = new File(this.imageFileSavePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
    private void doShow(final PluginCall pluginCall) {
        final int n = CameraPlugin$1.$SwitchMap$com$capacitorjs$plugins$camera$CameraSource[this.settings.getSource().ordinal()];
        if (n != 1) {
            if (n != 2) {
                this.showPrompt(pluginCall);
            }
            else {
                this.showPhotos(pluginCall);
            }
        }
        else {
            this.showCamera(pluginCall);
        }
    }
    
    private void editImage(final PluginCall pluginCall, final Uri uri, final ByteArrayOutputStream byteArrayOutputStream) {
        try {
            final Intent editIntent = this.createEditIntent(this.getTempImage(uri, byteArrayOutputStream));
            if (editIntent != null) {
                this.startActivityForResult(pluginCall, editIntent, "processEditedImage");
            }
            else {
                pluginCall.reject("Unable to edit image");
            }
        }
        catch (final Exception ex) {
            pluginCall.reject("Unable to edit image", ex);
        }
    }
    
    private ArrayList<Parcelable> getLegacyParcelableArrayList(final Bundle bundle, final String s) {
        return (ArrayList<Parcelable>)bundle.getParcelableArrayList(s);
    }
    
    private CameraResultType getResultType(final String s) {
        if (s == null) {
            return null;
        }
        try {
            return CameraResultType.valueOf(s.toUpperCase(Locale.ROOT));
        }
        catch (final IllegalArgumentException ex) {
            final String logTag = this.getLogTag();
            final StringBuilder sb = new StringBuilder("Invalid result type \"");
            sb.append(s);
            sb.append("\", defaulting to base64");
            Logger.debug(logTag, sb.toString());
            return CameraResultType.BASE64;
        }
    }
    
    private CameraSettings getSettings(final PluginCall pluginCall) {
        final CameraSettings cameraSettings = new CameraSettings();
        cameraSettings.setResultType(this.getResultType(pluginCall.getString("resultType")));
        boolean shouldResize = false;
        final Integer value = 0;
        final Boolean value2 = false;
        cameraSettings.setSaveToGallery((boolean)pluginCall.getBoolean("saveToGallery", value2));
        cameraSettings.setAllowEditing((boolean)pluginCall.getBoolean("allowEditing", value2));
        cameraSettings.setQuality((int)pluginCall.getInt("quality", Integer.valueOf(90)));
        cameraSettings.setWidth((int)pluginCall.getInt("width", value));
        cameraSettings.setHeight((int)pluginCall.getInt("height", value));
        if (cameraSettings.getWidth() > 0 || cameraSettings.getHeight() > 0) {
            shouldResize = true;
        }
        cameraSettings.setShouldResize(shouldResize);
        cameraSettings.setShouldCorrectOrientation((boolean)pluginCall.getBoolean("correctOrientation", Boolean.valueOf(true)));
        try {
            cameraSettings.setSource(CameraSource.valueOf(pluginCall.getString("source", CameraSource.PROMPT.getSource())));
        }
        catch (final IllegalArgumentException ex) {
            cameraSettings.setSource(CameraSource.PROMPT);
        }
        return cameraSettings;
    }
    
    private File getTempFile(final Uri uri) {
        String s2;
        final String s = s2 = Uri.parse(Uri.decode(uri.toString())).getLastPathSegment();
        if (!s.contains((CharSequence)".jpg")) {
            s2 = s;
            if (!s.contains((CharSequence)".jpeg")) {
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append(".");
                sb.append(new Date().getTime());
                sb.append(".jpeg");
                s2 = sb.toString();
            }
        }
        return new File(this.getContext().getCacheDir(), s2);
    }
    
    private Uri getTempImage(Uri saveImage, final ByteArrayOutputStream byteArrayOutputStream) {
        final ByteArrayInputStream byteArrayInputStream = null;
        final Uri uri = null;
        Object o;
        ByteArrayInputStream byteArrayInputStream2;
        try {
            o = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            try {
                saveImage = this.saveImage(saveImage, (InputStream)o);
                try {
                    ((ByteArrayInputStream)o).close();
                    return saveImage;
                }
                catch (final IOException ex) {
                    Logger.error(this.getLogTag(), "Unable to process image", (Throwable)ex);
                    return saveImage;
                }
            }
            catch (final IOException ex2) {}
        }
        catch (final IOException ex3) {
            o = null;
        }
        finally {
            byteArrayInputStream2 = byteArrayInputStream;
        }
        try {
            byteArrayInputStream2.close();
            goto Label_0089;
        }
        catch (final IOException ex4) {}
        saveImage = uri;
        if (o != null) {
            ((ByteArrayInputStream)o).close();
            saveImage = uri;
        }
        return saveImage;
    }
    
    private List<ResolveInfo> legacyQueryIntentActivities(final Intent intent) {
        return (List<ResolveInfo>)this.getContext().getPackageManager().queryIntentActivities(intent, 65536);
    }
    
    private void openPhotos(final PluginCall pluginCall, final boolean b, final boolean b2) {
        if (b2 || this.checkPhotosPermissions(pluginCall)) {
            final Intent intent = new Intent("android.intent.action.PICK");
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", b);
            intent.setType("image/*");
            Label_0087: {
                if (!b) {
                    break Label_0087;
                }
                try {
                    intent.putExtra("multi-pick", b);
                    intent.putExtra("android.intent.extra.MIME_TYPES", new String[] { "image/*" });
                    this.startActivityForResult(pluginCall, intent, "processPickedImages");
                    return;
                    this.startActivityForResult(pluginCall, intent, "processPickedImage");
                }
                catch (final ActivityNotFoundException ex) {
                    pluginCall.reject("Unable to resolve photo activity");
                }
            }
        }
    }
    
    private Bitmap prepareBitmap(Bitmap replaceBitmap, final Uri uri, final ExifWrapper exifWrapper) throws IOException {
        Bitmap replaceBitmap2 = replaceBitmap;
        if (this.settings.isShouldCorrectOrientation()) {
            replaceBitmap2 = this.replaceBitmap(replaceBitmap, ImageUtils.correctOrientation(this.getContext(), replaceBitmap, uri, exifWrapper));
        }
        replaceBitmap = replaceBitmap2;
        if (this.settings.isShouldResize()) {
            replaceBitmap = this.replaceBitmap(replaceBitmap2, ImageUtils.resize(replaceBitmap2, this.settings.getWidth(), this.settings.getHeight()));
        }
        return replaceBitmap;
    }
    
    @ActivityCallback
    private void processEditedImage(final PluginCall pluginCall, final ActivityResult activityResult) {
        this.isEdited = true;
        this.settings = this.getSettings(pluginCall);
        if (activityResult.getResultCode() == 0) {
            final Uri imagePickedContentUri = this.imagePickedContentUri;
            if (imagePickedContentUri != null) {
                this.processPickedImage(imagePickedContentUri, pluginCall);
            }
            else {
                this.processCameraImage(pluginCall, activityResult);
            }
        }
        else {
            this.processPickedImage(pluginCall, activityResult);
        }
    }
    
    private void processPickedImage(final Uri p0, final PluginCall p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore          4
        //     3: aconst_null    
        //     4: astore          5
        //     6: aconst_null    
        //     7: astore_3       
        //     8: aload_0        
        //     9: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getContext:()Landroid/content/Context;
        //    12: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    15: aload_1        
        //    16: invokevirtual   android/content/ContentResolver.openInputStream:(Landroid/net/Uri;)Ljava/io/InputStream;
        //    19: astore          6
        //    21: aload           6
        //    23: astore_3       
        //    24: aload           6
        //    26: astore          4
        //    28: aload           6
        //    30: astore          5
        //    32: aload           6
        //    34: invokestatic    android/graphics/BitmapFactory.decodeStream:(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
        //    37: astore          7
        //    39: aload           7
        //    41: ifnonnull       87
        //    44: aload           6
        //    46: astore_3       
        //    47: aload           6
        //    49: astore          4
        //    51: aload           6
        //    53: astore          5
        //    55: aload_2        
        //    56: ldc_w           "Unable to process bitmap"
        //    59: invokevirtual   com/getcapacitor/PluginCall.reject:(Ljava/lang/String;)V
        //    62: aload           6
        //    64: ifnull          86
        //    67: aload           6
        //    69: invokevirtual   java/io/InputStream.close:()V
        //    72: goto            86
        //    75: astore_1       
        //    76: aload_0        
        //    77: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //    80: ldc             "Unable to process image"
        //    82: aload_1        
        //    83: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //    86: return         
        //    87: aload           6
        //    89: astore_3       
        //    90: aload           6
        //    92: astore          4
        //    94: aload           6
        //    96: astore          5
        //    98: aload_0        
        //    99: aload_2        
        //   100: aload           7
        //   102: aload_1        
        //   103: invokespecial   com/capacitorjs/plugins/camera/CameraPlugin.returnResult:(Lcom/getcapacitor/PluginCall;Landroid/graphics/Bitmap;Landroid/net/Uri;)V
        //   106: aload           6
        //   108: ifnull          183
        //   111: aload           6
        //   113: invokevirtual   java/io/InputStream.close:()V
        //   116: goto            183
        //   119: astore_1       
        //   120: goto            184
        //   123: astore_1       
        //   124: aload           4
        //   126: astore_3       
        //   127: aload_2        
        //   128: ldc_w           "No such image found"
        //   131: aload_1        
        //   132: invokevirtual   com/getcapacitor/PluginCall.reject:(Ljava/lang/String;Ljava/lang/Exception;)V
        //   135: aload           4
        //   137: ifnull          183
        //   140: aload           4
        //   142: invokevirtual   java/io/InputStream.close:()V
        //   145: goto            183
        //   148: astore_1       
        //   149: aload           5
        //   151: astore_3       
        //   152: aload_2        
        //   153: ldc_w           "Out of memory"
        //   156: invokevirtual   com/getcapacitor/PluginCall.reject:(Ljava/lang/String;)V
        //   159: aload           5
        //   161: ifnull          183
        //   164: aload           5
        //   166: invokevirtual   java/io/InputStream.close:()V
        //   169: goto            183
        //   172: astore_1       
        //   173: aload_0        
        //   174: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //   177: ldc             "Unable to process image"
        //   179: aload_1        
        //   180: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   183: return         
        //   184: aload_3        
        //   185: ifnull          206
        //   188: aload_3        
        //   189: invokevirtual   java/io/InputStream.close:()V
        //   192: goto            206
        //   195: astore_2       
        //   196: aload_0        
        //   197: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //   200: ldc             "Unable to process image"
        //   202: aload_2        
        //   203: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   206: aload_1        
        //   207: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                           
        //  -----  -----  -----  -----  -------------------------------
        //  8      21     148    172    Ljava/lang/OutOfMemoryError;
        //  8      21     123    148    Ljava/io/FileNotFoundException;
        //  8      21     119    208    Any
        //  32     39     148    172    Ljava/lang/OutOfMemoryError;
        //  32     39     123    148    Ljava/io/FileNotFoundException;
        //  32     39     119    208    Any
        //  55     62     148    172    Ljava/lang/OutOfMemoryError;
        //  55     62     123    148    Ljava/io/FileNotFoundException;
        //  55     62     119    208    Any
        //  67     72     75     86     Ljava/io/IOException;
        //  98     106    148    172    Ljava/lang/OutOfMemoryError;
        //  98     106    123    148    Ljava/io/FileNotFoundException;
        //  98     106    119    208    Any
        //  111    116    172    183    Ljava/io/IOException;
        //  127    135    119    208    Any
        //  140    145    172    183    Ljava/io/IOException;
        //  152    159    119    208    Any
        //  164    169    172    183    Ljava/io/IOException;
        //  188    192    195    206    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 105 out of bounds for length 105
        //     at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
        //     at java.util.Objects.checkIndex(Objects.java:359)
        //     at java.util.ArrayList.get(ArrayList.java:434)
        //     at w5.a.o(SourceFile:31)
        //     at w5.a.j(SourceFile:218)
        //     at a6.j.j(SourceFile:23)
        //     at a6.j.i(SourceFile:28)
        //     at a6.i.n(SourceFile:7)
        //     at a6.i.m(SourceFile:174)
        //     at a6.i.c(SourceFile:67)
        //     at a6.i.r(SourceFile:328)
        //     at a6.i.s(SourceFile:17)
        //     at a6.i.q(SourceFile:29)
        //     at a6.i.b(SourceFile:33)
        //     at y5.d.e(SourceFile:6)
        //     at y5.d.b(SourceFile:1)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.decompileWithProcyon(SourceFile:306)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.doWork(SourceFile:131)
        //     at com.thesourceofcode.jadec.decompilers.BaseDecompiler.withAttempt(SourceFile:3)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.d(SourceFile:53)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.b(SourceFile:1)
        //     at e7.a.run(SourceFile:1)
        //     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        //     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
        //     at java.lang.Thread.run(Thread.java:1012)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private JSObject processPickedImages(final Uri p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   com/getcapacitor/JSObject.<init>:()V
        //     7: astore          6
        //     9: aconst_null    
        //    10: astore_3       
        //    11: aconst_null    
        //    12: astore          4
        //    14: aconst_null    
        //    15: astore_2       
        //    16: aload_0        
        //    17: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getContext:()Landroid/content/Context;
        //    20: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    23: aload_1        
        //    24: invokevirtual   android/content/ContentResolver.openInputStream:(Landroid/net/Uri;)Ljava/io/InputStream;
        //    27: astore          5
        //    29: aload           5
        //    31: astore_2       
        //    32: aload           5
        //    34: astore_3       
        //    35: aload           5
        //    37: astore          4
        //    39: aload           5
        //    41: invokestatic    android/graphics/BitmapFactory.decodeStream:(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
        //    44: astore          8
        //    46: aload           8
        //    48: ifnonnull       100
        //    51: aload           5
        //    53: astore_2       
        //    54: aload           5
        //    56: astore_3       
        //    57: aload           5
        //    59: astore          4
        //    61: aload           6
        //    63: ldc_w           "error"
        //    66: ldc_w           "Unable to process bitmap"
        //    69: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //    72: pop            
        //    73: aload           5
        //    75: ifnull          97
        //    78: aload           5
        //    80: invokevirtual   java/io/InputStream.close:()V
        //    83: goto            97
        //    86: astore_1       
        //    87: aload_0        
        //    88: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //    91: ldc             "Unable to process image"
        //    93: aload_1        
        //    94: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //    97: aload           6
        //    99: areturn        
        //   100: aload           5
        //   102: astore_2       
        //   103: aload           5
        //   105: astore_3       
        //   106: aload           5
        //   108: astore          4
        //   110: aload_0        
        //   111: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getContext:()Landroid/content/Context;
        //   114: aload           8
        //   116: aload_1        
        //   117: invokestatic    com/capacitorjs/plugins/camera/ImageUtils.getExifData:(Landroid/content/Context;Landroid/graphics/Bitmap;Landroid/net/Uri;)Lcom/capacitorjs/plugins/camera/ExifWrapper;
        //   120: astore          7
        //   122: aload           5
        //   124: astore_2       
        //   125: aload           5
        //   127: astore_3       
        //   128: aload           5
        //   130: astore          4
        //   132: aload_0        
        //   133: aload           8
        //   135: aload_1        
        //   136: aload           7
        //   138: invokespecial   com/capacitorjs/plugins/camera/CameraPlugin.prepareBitmap:(Landroid/graphics/Bitmap;Landroid/net/Uri;Lcom/capacitorjs/plugins/camera/ExifWrapper;)Landroid/graphics/Bitmap;
        //   141: astore          8
        //   143: aload           5
        //   145: astore_2       
        //   146: aload           5
        //   148: astore_3       
        //   149: aload           5
        //   151: astore          4
        //   153: new             Ljava/io/ByteArrayOutputStream;
        //   156: astore          9
        //   158: aload           5
        //   160: astore_2       
        //   161: aload           5
        //   163: astore_3       
        //   164: aload           5
        //   166: astore          4
        //   168: aload           9
        //   170: invokespecial   java/io/ByteArrayOutputStream.<init>:()V
        //   173: aload           5
        //   175: astore_2       
        //   176: aload           5
        //   178: astore_3       
        //   179: aload           5
        //   181: astore          4
        //   183: aload           8
        //   185: getstatic       android/graphics/Bitmap$CompressFormat.JPEG:Landroid/graphics/Bitmap$CompressFormat;
        //   188: aload_0        
        //   189: getfield        com/capacitorjs/plugins/camera/CameraPlugin.settings:Lcom/capacitorjs/plugins/camera/CameraSettings;
        //   192: invokevirtual   com/capacitorjs/plugins/camera/CameraSettings.getQuality:()I
        //   195: aload           9
        //   197: invokevirtual   android/graphics/Bitmap.compress:(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
        //   200: pop            
        //   201: aload           5
        //   203: astore_2       
        //   204: aload           5
        //   206: astore_3       
        //   207: aload           5
        //   209: astore          4
        //   211: aload_0        
        //   212: aload_1        
        //   213: aload           9
        //   215: invokespecial   com/capacitorjs/plugins/camera/CameraPlugin.getTempImage:(Landroid/net/Uri;Ljava/io/ByteArrayOutputStream;)Landroid/net/Uri;
        //   218: astore_1       
        //   219: aload           5
        //   221: astore_2       
        //   222: aload           5
        //   224: astore_3       
        //   225: aload           5
        //   227: astore          4
        //   229: aload           7
        //   231: aload_1        
        //   232: invokevirtual   android/net/Uri.getPath:()Ljava/lang/String;
        //   235: invokevirtual   com/capacitorjs/plugins/camera/ExifWrapper.copyExif:(Ljava/lang/String;)V
        //   238: aload_1        
        //   239: ifnull          348
        //   242: aload           5
        //   244: astore_2       
        //   245: aload           5
        //   247: astore_3       
        //   248: aload           5
        //   250: astore          4
        //   252: aload           6
        //   254: ldc_w           "format"
        //   257: ldc_w           "jpeg"
        //   260: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //   263: pop            
        //   264: aload           5
        //   266: astore_2       
        //   267: aload           5
        //   269: astore_3       
        //   270: aload           5
        //   272: astore          4
        //   274: aload           6
        //   276: ldc_w           "exif"
        //   279: aload           7
        //   281: invokevirtual   com/capacitorjs/plugins/camera/ExifWrapper.toJson:()Lcom/getcapacitor/JSObject;
        //   284: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/Object;)Lcom/getcapacitor/JSObject;
        //   287: pop            
        //   288: aload           5
        //   290: astore_2       
        //   291: aload           5
        //   293: astore_3       
        //   294: aload           5
        //   296: astore          4
        //   298: aload           6
        //   300: ldc_w           "path"
        //   303: aload_1        
        //   304: invokevirtual   android/net/Uri.toString:()Ljava/lang/String;
        //   307: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //   310: pop            
        //   311: aload           5
        //   313: astore_2       
        //   314: aload           5
        //   316: astore_3       
        //   317: aload           5
        //   319: astore          4
        //   321: aload           6
        //   323: ldc_w           "webPath"
        //   326: aload_0        
        //   327: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getContext:()Landroid/content/Context;
        //   330: aload_0        
        //   331: getfield        com/capacitorjs/plugins/camera/CameraPlugin.bridge:Lcom/getcapacitor/Bridge;
        //   334: invokevirtual   com/getcapacitor/Bridge.getLocalUrl:()Ljava/lang/String;
        //   337: aload_1        
        //   338: invokestatic    com/getcapacitor/FileUtils.getPortablePath:(Landroid/content/Context;Ljava/lang/String;Landroid/net/Uri;)Ljava/lang/String;
        //   341: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //   344: pop            
        //   345: goto            369
        //   348: aload           5
        //   350: astore_2       
        //   351: aload           5
        //   353: astore_3       
        //   354: aload           5
        //   356: astore          4
        //   358: aload           6
        //   360: ldc_w           "error"
        //   363: ldc             "Unable to process image"
        //   365: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //   368: pop            
        //   369: aload           5
        //   371: ifnull          393
        //   374: aload           5
        //   376: invokevirtual   java/io/InputStream.close:()V
        //   379: goto            393
        //   382: astore_1       
        //   383: aload_0        
        //   384: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //   387: ldc             "Unable to process image"
        //   389: aload_1        
        //   390: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   393: aload           6
        //   395: areturn        
        //   396: astore_1       
        //   397: aload           5
        //   399: astore_2       
        //   400: aload           5
        //   402: astore_3       
        //   403: aload           5
        //   405: astore          4
        //   407: aload           6
        //   409: ldc_w           "error"
        //   412: ldc             "Unable to process image"
        //   414: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //   417: pop            
        //   418: aload           5
        //   420: ifnull          442
        //   423: aload           5
        //   425: invokevirtual   java/io/InputStream.close:()V
        //   428: goto            442
        //   431: astore_1       
        //   432: aload_0        
        //   433: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //   436: ldc             "Unable to process image"
        //   438: aload_1        
        //   439: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   442: aload           6
        //   444: areturn        
        //   445: astore_1       
        //   446: goto            531
        //   449: astore_1       
        //   450: aload_3        
        //   451: astore_2       
        //   452: aload           6
        //   454: ldc_w           "error"
        //   457: ldc_w           "No such image found"
        //   460: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //   463: pop            
        //   464: aload_3        
        //   465: astore_2       
        //   466: aload_0        
        //   467: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //   470: ldc_w           "No such image found"
        //   473: aload_1        
        //   474: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   477: aload_3        
        //   478: ifnull          528
        //   481: aload_3        
        //   482: invokevirtual   java/io/InputStream.close:()V
        //   485: goto            528
        //   488: astore_1       
        //   489: aload           4
        //   491: astore_2       
        //   492: aload           6
        //   494: ldc_w           "error"
        //   497: ldc_w           "Out of memory"
        //   500: invokevirtual   com/getcapacitor/JSObject.put:(Ljava/lang/String;Ljava/lang/String;)Lcom/getcapacitor/JSObject;
        //   503: pop            
        //   504: aload           4
        //   506: ifnull          528
        //   509: aload           4
        //   511: invokevirtual   java/io/InputStream.close:()V
        //   514: goto            528
        //   517: astore_1       
        //   518: aload_0        
        //   519: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //   522: ldc             "Unable to process image"
        //   524: aload_1        
        //   525: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   528: aload           6
        //   530: areturn        
        //   531: aload_2        
        //   532: ifnull          553
        //   535: aload_2        
        //   536: invokevirtual   java/io/InputStream.close:()V
        //   539: goto            553
        //   542: astore_2       
        //   543: aload_0        
        //   544: invokevirtual   com/capacitorjs/plugins/camera/CameraPlugin.getLogTag:()Ljava/lang/String;
        //   547: ldc             "Unable to process image"
        //   549: aload_2        
        //   550: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   553: aload_1        
        //   554: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                           
        //  -----  -----  -----  -----  -------------------------------
        //  16     29     488    517    Ljava/lang/OutOfMemoryError;
        //  16     29     449    488    Ljava/io/FileNotFoundException;
        //  16     29     445    555    Any
        //  39     46     488    517    Ljava/lang/OutOfMemoryError;
        //  39     46     449    488    Ljava/io/FileNotFoundException;
        //  39     46     445    555    Any
        //  61     73     488    517    Ljava/lang/OutOfMemoryError;
        //  61     73     449    488    Ljava/io/FileNotFoundException;
        //  61     73     445    555    Any
        //  78     83     86     97     Ljava/io/IOException;
        //  110    122    488    517    Ljava/lang/OutOfMemoryError;
        //  110    122    449    488    Ljava/io/FileNotFoundException;
        //  110    122    445    555    Any
        //  132    143    396    445    Ljava/io/IOException;
        //  132    143    488    517    Ljava/lang/OutOfMemoryError;
        //  132    143    449    488    Ljava/io/FileNotFoundException;
        //  132    143    445    555    Any
        //  153    158    488    517    Ljava/lang/OutOfMemoryError;
        //  153    158    449    488    Ljava/io/FileNotFoundException;
        //  153    158    445    555    Any
        //  168    173    488    517    Ljava/lang/OutOfMemoryError;
        //  168    173    449    488    Ljava/io/FileNotFoundException;
        //  168    173    445    555    Any
        //  183    201    488    517    Ljava/lang/OutOfMemoryError;
        //  183    201    449    488    Ljava/io/FileNotFoundException;
        //  183    201    445    555    Any
        //  211    219    488    517    Ljava/lang/OutOfMemoryError;
        //  211    219    449    488    Ljava/io/FileNotFoundException;
        //  211    219    445    555    Any
        //  229    238    488    517    Ljava/lang/OutOfMemoryError;
        //  229    238    449    488    Ljava/io/FileNotFoundException;
        //  229    238    445    555    Any
        //  252    264    488    517    Ljava/lang/OutOfMemoryError;
        //  252    264    449    488    Ljava/io/FileNotFoundException;
        //  252    264    445    555    Any
        //  274    288    488    517    Ljava/lang/OutOfMemoryError;
        //  274    288    449    488    Ljava/io/FileNotFoundException;
        //  274    288    445    555    Any
        //  298    311    488    517    Ljava/lang/OutOfMemoryError;
        //  298    311    449    488    Ljava/io/FileNotFoundException;
        //  298    311    445    555    Any
        //  321    345    488    517    Ljava/lang/OutOfMemoryError;
        //  321    345    449    488    Ljava/io/FileNotFoundException;
        //  321    345    445    555    Any
        //  358    369    488    517    Ljava/lang/OutOfMemoryError;
        //  358    369    449    488    Ljava/io/FileNotFoundException;
        //  358    369    445    555    Any
        //  374    379    382    393    Ljava/io/IOException;
        //  407    418    488    517    Ljava/lang/OutOfMemoryError;
        //  407    418    449    488    Ljava/io/FileNotFoundException;
        //  407    418    445    555    Any
        //  423    428    431    442    Ljava/io/IOException;
        //  452    464    445    555    Any
        //  466    477    445    555    Any
        //  481    485    517    528    Ljava/io/IOException;
        //  492    504    445    555    Any
        //  509    514    517    528    Ljava/io/IOException;
        //  535    539    542    553    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 286 out of bounds for length 286
        //     at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
        //     at java.util.Objects.checkIndex(Objects.java:359)
        //     at java.util.ArrayList.get(ArrayList.java:434)
        //     at w5.a.o(SourceFile:31)
        //     at w5.a.o(SourceFile:733)
        //     at w5.a.j(SourceFile:218)
        //     at a6.j.j(SourceFile:23)
        //     at a6.j.i(SourceFile:28)
        //     at a6.i.n(SourceFile:7)
        //     at a6.i.m(SourceFile:174)
        //     at a6.i.c(SourceFile:67)
        //     at a6.i.r(SourceFile:328)
        //     at a6.i.s(SourceFile:17)
        //     at a6.i.q(SourceFile:29)
        //     at a6.i.b(SourceFile:33)
        //     at y5.d.e(SourceFile:6)
        //     at y5.d.b(SourceFile:1)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.decompileWithProcyon(SourceFile:306)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.doWork(SourceFile:131)
        //     at com.thesourceofcode.jadec.decompilers.BaseDecompiler.withAttempt(SourceFile:3)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.d(SourceFile:53)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.b(SourceFile:1)
        //     at e7.a.run(SourceFile:1)
        //     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        //     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
        //     at java.lang.Thread.run(Thread.java:1012)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private Bitmap replaceBitmap(final Bitmap bitmap, final Bitmap bitmap2) {
        if (bitmap != bitmap2) {
            bitmap.recycle();
        }
        return bitmap2;
    }
    
    private void returnBase64(final PluginCall pluginCall, final ExifWrapper exifWrapper, final ByteArrayOutputStream byteArrayOutputStream) {
        final String encodeToString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2);
        final JSObject jsObject = new JSObject();
        jsObject.put("format", "jpeg");
        jsObject.put("base64String", encodeToString);
        jsObject.put("exif", (Object)exifWrapper.toJson());
        pluginCall.resolve(jsObject);
    }
    
    private void returnDataUrl(final PluginCall pluginCall, final ExifWrapper exifWrapper, final ByteArrayOutputStream byteArrayOutputStream) {
        final String encodeToString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2);
        final JSObject jsObject = new JSObject();
        jsObject.put("format", "jpeg");
        final StringBuilder sb = new StringBuilder("data:image/jpeg;base64,");
        sb.append(encodeToString);
        jsObject.put("dataUrl", sb.toString());
        jsObject.put("exif", (Object)exifWrapper.toJson());
        pluginCall.resolve(jsObject);
    }
    
    private void returnFileURI(final PluginCall pluginCall, final ExifWrapper exifWrapper, final Bitmap bitmap, Uri tempImage, final ByteArrayOutputStream byteArrayOutputStream) {
        tempImage = this.getTempImage(tempImage, byteArrayOutputStream);
        exifWrapper.copyExif(tempImage.getPath());
        if (tempImage != null) {
            final JSObject jsObject = new JSObject();
            jsObject.put("format", "jpeg");
            jsObject.put("exif", (Object)exifWrapper.toJson());
            jsObject.put("path", tempImage.toString());
            jsObject.put("webPath", FileUtils.getPortablePath(this.getContext(), this.bridge.getLocalUrl(), tempImage));
            jsObject.put("saved", this.isSaved);
            pluginCall.resolve(jsObject);
        }
        else {
            pluginCall.reject("Unable to process image");
        }
    }
    
    private void returnResult(final PluginCall pluginCall, final Bitmap bitmap, final Uri uri) {
        final ExifWrapper exifData = ImageUtils.getExifData(this.getContext(), bitmap, uri);
        try {
            final Bitmap prepareBitmap = this.prepareBitmap(bitmap, uri, exifData);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            prepareBitmap.compress(Bitmap$CompressFormat.JPEG, this.settings.getQuality(), (OutputStream)byteArrayOutputStream);
            if (this.settings.isAllowEditing() && !this.isEdited) {
                this.editImage(pluginCall, uri, byteArrayOutputStream);
                return;
            }
            if (pluginCall.getBoolean("saveToGallery", Boolean.valueOf(false))) {
                String s = this.imageEditedFileSavePath;
                if (s != null || this.imageFileSavePath != null) {
                    this.isSaved = true;
                    Label_0124: {
                        if (s != null) {
                            break Label_0124;
                        }
                        try {
                            s = this.imageFileSavePath;
                            final File file = new File(s);
                            if (Build$VERSION.SDK_INT >= 29) {
                                final ContentResolver contentResolver = this.getContext().getContentResolver();
                                final ContentValues contentValues = new ContentValues();
                                contentValues.put("_display_name", file.getName());
                                contentValues.put("mime_type", "image/jpeg");
                                contentValues.put("relative_path", Environment.DIRECTORY_DCIM);
                                final Uri insert = contentResolver.insert(MediaStore$Images$Media.EXTERNAL_CONTENT_URI, contentValues);
                                if (insert == null) {
                                    throw new IOException("Failed to create new MediaStore record.");
                                }
                                final OutputStream openOutputStream = contentResolver.openOutputStream(insert);
                                if (openOutputStream == null) {
                                    throw new IOException("Failed to open output stream.");
                                }
                                if (!(boolean)prepareBitmap.compress(Bitmap$CompressFormat.JPEG, this.settings.getQuality(), openOutputStream)) {
                                    this.isSaved = false;
                                }
                            }
                            else if (MediaStore$Images$Media.insertImage(this.getContext().getContentResolver(), s, file.getName(), "") == null) {
                                this.isSaved = false;
                            }
                        }
                        catch (final IOException ex) {
                            this.isSaved = false;
                            Logger.error(this.getLogTag(), "Unable to save the image in the gallery", (Throwable)ex);
                        }
                        catch (final FileNotFoundException ex2) {
                            this.isSaved = false;
                            Logger.error(this.getLogTag(), "Unable to save the image in the gallery", (Throwable)ex2);
                        }
                    }
                }
            }
            if (this.settings.getResultType() == CameraResultType.BASE64) {
                this.returnBase64(pluginCall, exifData, byteArrayOutputStream);
            }
            else if (this.settings.getResultType() == CameraResultType.URI) {
                this.returnFileURI(pluginCall, exifData, prepareBitmap, uri, byteArrayOutputStream);
            }
            else if (this.settings.getResultType() == CameraResultType.DATAURL) {
                this.returnDataUrl(pluginCall, exifData, byteArrayOutputStream);
            }
            else {
                pluginCall.reject("Invalid resultType option");
            }
            if (this.settings.getResultType() != CameraResultType.URI) {
                this.deleteImageFile();
            }
            this.imageFileSavePath = null;
            this.imageFileUri = null;
            this.imagePickedContentUri = null;
            this.imageEditedFileSavePath = null;
        }
        catch (final IOException ex3) {
            pluginCall.reject("Unable to process image");
        }
    }
    
    private Uri saveImage(final Uri uri, final InputStream inputStream) throws IOException {
        File file;
        if (uri.getScheme().equals((Object)"content")) {
            file = this.getTempFile(uri);
        }
        else {
            file = new File(uri.getPath());
        }
        try {
            this.writePhoto(file, inputStream);
        }
        catch (final FileNotFoundException ex) {
            file = this.getTempFile(uri);
            this.writePhoto(file, inputStream);
        }
        return Uri.fromFile(file);
    }
    
    private void showCamera(final PluginCall pluginCall) {
        if (!this.getContext().getPackageManager().hasSystemFeature("android.hardware.camera.any")) {
            pluginCall.reject("Device doesn't have a camera available");
            return;
        }
        this.openCamera(pluginCall);
    }
    
    private void showPhotos(final PluginCall pluginCall) {
        this.openPhotos(pluginCall);
    }
    
    private void showPrompt(final PluginCall pluginCall) {
        final ArrayList list = new ArrayList();
        ((List)list).add((Object)pluginCall.getString("promptLabelPhoto", "From Photos"));
        ((List)list).add((Object)pluginCall.getString("promptLabelPicture", "Take Picture"));
        final CameraBottomSheetDialogFragment cameraBottomSheetDialogFragment = new CameraBottomSheetDialogFragment();
        cameraBottomSheetDialogFragment.setTitle(pluginCall.getString("promptLabelHeader", "Photo"));
        cameraBottomSheetDialogFragment.setOptions((List<String>)list, (CameraBottomSheetDialogFragment.CameraBottomSheetDialogFragment$BottomSheetOnSelectedListener)new CameraPlugin$$ExternalSyntheticLambda3(this, pluginCall), (CameraBottomSheetDialogFragment.CameraBottomSheetDialogFragment$BottomSheetOnCanceledListener)new CameraPlugin$$ExternalSyntheticLambda4(pluginCall));
        cameraBottomSheetDialogFragment.show(this.getActivity().getSupportFragmentManager(), "capacitorModalsActionSheet");
    }
    
    private void writePhoto(final File file, final InputStream inputStream) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final byte[] array = new byte[1024];
        while (true) {
            final int read = inputStream.read(array);
            if (read == -1) {
                break;
            }
            fileOutputStream.write(array, 0, read);
        }
        fileOutputStream.close();
    }
    
    @PluginMethod
    public void getLimitedLibraryPhotos(final PluginCall pluginCall) {
        pluginCall.unimplemented("not supported on android");
    }
    
    public Map<String, PermissionState> getPermissionStates() {
        final Map permissionStates = super.getPermissionStates();
        if (!this.isPermissionDeclared("camera")) {
            permissionStates.put((Object)"camera", (Object)PermissionState.GRANTED);
        }
        if (Build$VERSION.SDK_INT >= 30) {
            String s;
            if (Build$VERSION.SDK_INT >= 33) {
                s = "media";
            }
            else {
                s = "readExternalStorage";
            }
            if (permissionStates.containsKey((Object)s)) {
                permissionStates.put((Object)"photos", (Object)permissionStates.get((Object)s));
            }
        }
        return (Map<String, PermissionState>)permissionStates;
    }
    
    @PluginMethod
    public void getPhoto(final PluginCall pluginCall) {
        this.isEdited = false;
        this.settings = this.getSettings(pluginCall);
        this.doShow(pluginCall);
    }
    
    public void openCamera(final PluginCall pluginCall) {
        if (this.checkCameraPermissions(pluginCall)) {
            final Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (intent.resolveActivity(this.getContext().getPackageManager()) != null) {
                try {
                    final String appId = this.getAppId();
                    final File imageFile = CameraUtils.createImageFile((Activity)this.getActivity());
                    this.imageFileSavePath = imageFile.getAbsolutePath();
                    final AppCompatActivity activity = this.getActivity();
                    final StringBuilder sb = new StringBuilder();
                    sb.append(appId);
                    sb.append(".fileprovider");
                    intent.putExtra("output", (Parcelable)(this.imageFileUri = FileProvider.getUriForFile((Context)activity, sb.toString(), imageFile)));
                    this.startActivityForResult(pluginCall, intent, "processCameraImage");
                    return;
                }
                catch (final Exception ex) {
                    pluginCall.reject("Unable to create photo on disk", ex);
                    return;
                }
            }
            pluginCall.reject("Unable to resolve camera activity");
        }
    }
    
    public void openPhotos(final PluginCall pluginCall) {
        this.openPhotos(pluginCall, false, false);
    }
    
    @PluginMethod
    public void pickImages(final PluginCall pluginCall) {
        this.settings = this.getSettings(pluginCall);
        this.openPhotos(pluginCall, true, false);
    }
    
    @PluginMethod
    public void pickLimitedLibraryPhotos(final PluginCall pluginCall) {
        pluginCall.unimplemented("not supported on android");
    }
    
    @ActivityCallback
    public void processCameraImage(final PluginCall pluginCall, final ActivityResult activityResult) {
        this.settings = this.getSettings(pluginCall);
        if (this.imageFileSavePath == null) {
            pluginCall.reject("Unable to process image, file not found on disk");
            return;
        }
        final File file = new File(this.imageFileSavePath);
        final BitmapFactory$Options bitmapFactory$Options = new BitmapFactory$Options();
        final Uri fromFile = Uri.fromFile(file);
        final Bitmap decodeFile = BitmapFactory.decodeFile(this.imageFileSavePath, bitmapFactory$Options);
        if (decodeFile == null) {
            pluginCall.reject("User cancelled photos app");
            return;
        }
        this.returnResult(pluginCall, decodeFile, fromFile);
    }
    
    @ActivityCallback
    public void processPickedImage(final PluginCall pluginCall, final ActivityResult activityResult) {
        this.settings = this.getSettings(pluginCall);
        final Intent data = activityResult.getData();
        if (data == null) {
            pluginCall.reject("No image picked");
            return;
        }
        this.processPickedImage(this.imagePickedContentUri = data.getData(), pluginCall);
    }
    
    @ActivityCallback
    public void processPickedImages(final PluginCall pluginCall, final ActivityResult activityResult) {
        final Intent data = activityResult.getData();
        if (data != null) {
            ((Executor)Executors.newSingleThreadExecutor()).execute((Runnable)new CameraPlugin$$ExternalSyntheticLambda5(this, data, pluginCall));
        }
        else {
            pluginCall.reject("No images picked");
        }
    }
    
    protected void requestPermissionForAliases(final String[] array, final PluginCall pluginCall, final String s) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        int i = 0;
        final int n = 0;
        if (sdk_INT >= 33) {
            for (int j = n; j < array.length; ++j) {
                if (array[j].equals((Object)"photos")) {
                    array[j] = "media";
                }
            }
        }
        else if (Build$VERSION.SDK_INT >= 30) {
            while (i < array.length) {
                if (array[i].equals((Object)"photos")) {
                    array[i] = "readExternalStorage";
                }
                ++i;
            }
        }
        super.requestPermissionForAliases(array, pluginCall, s);
    }
    
    @PluginMethod
    public void requestPermissions(final PluginCall pluginCall) {
        if (this.isPermissionDeclared("camera")) {
            super.requestPermissions(pluginCall);
            return;
        }
        final JSArray array = pluginCall.getArray("permissions");
        while (true) {
            if (array == null) {
                break Label_0037;
            }
            try {
                List list = array.toList();
                while (true) {
                    if (list != null && list.size() == 1 && list.contains((Object)"camera")) {
                        this.checkPermissions(pluginCall);
                    }
                    else {
                        this.requestPermissionForAlias("photos", pluginCall, "checkPermissions");
                    }
                    return;
                    list = null;
                    continue;
                }
            }
            catch (final JSONException ex) {
                continue;
            }
            break;
        }
    }
    
    protected void restoreState(final Bundle bundle) {
        final String string = bundle.getString("cameraImageFileSavePath");
        if (string != null) {
            this.imageFileSavePath = string;
        }
    }
    
    protected Bundle saveInstanceState() {
        final Bundle saveInstanceState = super.saveInstanceState();
        if (saveInstanceState != null) {
            saveInstanceState.putString("cameraImageFileSavePath", this.imageFileSavePath);
        }
        return saveInstanceState;
    }
}
