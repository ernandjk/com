package com.capacitorjs.plugins.camera;

import com.getcapacitor.Logger;
import android.content.Context;
import androidx.core.content.FileProvider;
import android.net.Uri;
import java.io.IOException;
import android.os.Environment;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import android.app.Activity;

public class CameraUtils
{
    public static File createImageFile(final Activity activity) throws IOException {
        final String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final StringBuilder sb = new StringBuilder("JPEG_");
        sb.append(format);
        sb.append("_");
        return File.createTempFile(sb.toString(), ".jpg", activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }
    
    public static Uri createImageFileUri(final Activity activity, final String s) throws IOException {
        final File imageFile = createImageFile(activity);
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(".fileprovider");
        return FileProvider.getUriForFile((Context)activity, sb.toString(), imageFile);
    }
    
    protected static String getLogTag() {
        return Logger.tags("CameraUtils");
    }
}
