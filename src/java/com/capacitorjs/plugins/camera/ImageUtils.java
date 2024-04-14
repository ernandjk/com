package com.capacitorjs.plugins.camera;

import java.io.InputStream;
import androidx.exifinterface.media.ExifInterface;
import java.io.IOException;
import android.graphics.Matrix;
import android.net.Uri;
import android.graphics.Bitmap;
import android.content.Context;

public class ImageUtils
{
    public static Bitmap correctOrientation(final Context context, final Bitmap bitmap, final Uri uri, final ExifWrapper exifWrapper) throws IOException {
        final int orientation = getOrientation(context, uri);
        if (orientation != 0) {
            final Matrix matrix = new Matrix();
            matrix.postRotate((float)orientation);
            exifWrapper.resetOrientation();
            return transform(bitmap, matrix);
        }
        return bitmap;
    }
    
    public static ExifWrapper getExifData(final Context p0, final Bitmap p1, final Uri p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //     4: aload_2        
        //     5: invokevirtual   android/content/ContentResolver.openInputStream:(Landroid/net/Uri;)Ljava/io/InputStream;
        //     8: astore_1       
        //     9: aload_1        
        //    10: astore_0       
        //    11: new             Landroidx/exifinterface/media/ExifInterface;
        //    14: astore_2       
        //    15: aload_1        
        //    16: astore_0       
        //    17: aload_2        
        //    18: aload_1        
        //    19: invokespecial   androidx/exifinterface/media/ExifInterface.<init>:(Ljava/io/InputStream;)V
        //    22: aload_1        
        //    23: astore_0       
        //    24: new             Lcom/capacitorjs/plugins/camera/ExifWrapper;
        //    27: dup            
        //    28: aload_2        
        //    29: invokespecial   com/capacitorjs/plugins/camera/ExifWrapper.<init>:(Landroidx/exifinterface/media/ExifInterface;)V
        //    32: astore_2       
        //    33: aload_1        
        //    34: ifnull          41
        //    37: aload_1        
        //    38: invokevirtual   java/io/InputStream.close:()V
        //    41: aload_2        
        //    42: areturn        
        //    43: astore_2       
        //    44: goto            56
        //    47: astore_1       
        //    48: aconst_null    
        //    49: astore_0       
        //    50: goto            82
        //    53: astore_2       
        //    54: aconst_null    
        //    55: astore_1       
        //    56: aload_1        
        //    57: astore_0       
        //    58: ldc             "Error loading exif data from image"
        //    60: aload_2        
        //    61: invokestatic    com/getcapacitor/Logger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    64: aload_1        
        //    65: ifnull          72
        //    68: aload_1        
        //    69: invokevirtual   java/io/InputStream.close:()V
        //    72: new             Lcom/capacitorjs/plugins/camera/ExifWrapper;
        //    75: dup            
        //    76: aconst_null    
        //    77: invokespecial   com/capacitorjs/plugins/camera/ExifWrapper.<init>:(Landroidx/exifinterface/media/ExifInterface;)V
        //    80: areturn        
        //    81: astore_1       
        //    82: aload_0        
        //    83: ifnull          90
        //    86: aload_0        
        //    87: invokevirtual   java/io/InputStream.close:()V
        //    90: aload_1        
        //    91: athrow         
        //    92: astore_0       
        //    93: goto            41
        //    96: astore_0       
        //    97: goto            72
        //   100: astore_0       
        //   101: goto            90
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      9      53     56     Ljava/io/IOException;
        //  0      9      47     53     Any
        //  11     15     43     47     Ljava/io/IOException;
        //  11     15     81     82     Any
        //  17     22     43     47     Ljava/io/IOException;
        //  17     22     81     82     Any
        //  24     33     43     47     Ljava/io/IOException;
        //  24     33     81     82     Any
        //  37     41     92     96     Ljava/io/IOException;
        //  58     64     81     82     Any
        //  68     72     96     100    Ljava/io/IOException;
        //  86     90     100    104    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 63 out of bounds for length 63
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
    
    private static int getOrientation(Context openInputStream, final Uri uri) throws IOException {
        openInputStream = (Context)openInputStream.getContentResolver().openInputStream(uri);
        try {
            final int attributeInt = new ExifInterface((InputStream)openInputStream).getAttributeInt("Orientation", 1);
            int n;
            if (attributeInt == 6) {
                n = 90;
            }
            else if (attributeInt == 3) {
                n = 180;
            }
            else if (attributeInt == 8) {
                n = 270;
            }
            else {
                n = 0;
            }
            if (openInputStream != null) {
                ((InputStream)openInputStream).close();
            }
            return n;
        }
        finally {
            if (openInputStream != null) {
                try {
                    ((InputStream)openInputStream).close();
                }
                finally {
                    final Throwable t;
                    ((Throwable)uri).addSuppressed(t);
                }
            }
        }
    }
    
    public static Bitmap resize(final Bitmap bitmap, final int n, final int n2) {
        return resizePreservingAspectRatio(bitmap, n, n2);
    }
    
    private static Bitmap resizePreservingAspectRatio(final Bitmap bitmap, final int n, int n2) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int n3 = n2;
        if (n2 == 0) {
            n3 = height;
        }
        if ((n2 = n) == 0) {
            n2 = width;
        }
        float n4 = (float)Math.min(width, n2);
        final float n5 = height * n4 / width;
        final float n6 = (float)n3;
        float n7 = n5;
        if (n5 > n6) {
            n4 = (float)(width * n3 / height);
            n7 = n6;
        }
        return Bitmap.createScaledBitmap(bitmap, Math.round(n4), Math.round(n7), false);
    }
    
    private static Bitmap transform(final Bitmap bitmap, final Matrix matrix) {
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
