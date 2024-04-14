package com.getcapacitor;

import java.util.List;
import android.webkit.WebChromeClient$CustomViewCallback;
import android.view.View;
import android.content.DialogInterface$OnCancelListener;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.webkit.WebView;
import android.webkit.ConsoleMessage;
import android.os.Parcelable;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.webkit.PermissionRequest;
import android.widget.EditText;
import android.webkit.JsPromptResult;
import android.content.DialogInterface;
import android.webkit.JsResult;
import android.os.Build$VERSION;
import java.util.Iterator;
import java.util.Map$Entry;
import com.getcapacitor.util.PermissionHelper;
import java.util.Arrays;
import android.webkit.MimeTypeMap;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import androidx.core.content.FileProvider;
import android.net.Uri;
import java.io.IOException;
import android.os.Environment;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import android.app.Activity;
import androidx.activity.result.contract.ActivityResultContracts$StartActivityForResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts$RequestMultiplePermissions;
import java.util.Map;
import androidx.activity.result.ActivityResult;
import android.webkit.GeolocationPermissions$Callback;
import android.webkit.WebChromeClient$FileChooserParams;
import android.webkit.ValueCallback;
import androidx.activity.result.ActivityResultLauncher;
import android.webkit.WebChromeClient;

public class BridgeWebChromeClient extends WebChromeClient
{
    private ActivityResultLauncher activityLauncher;
    private ActivityResultListener activityListener;
    private Bridge bridge;
    private ActivityResultLauncher permissionLauncher;
    private PermissionListener permissionListener;
    
    public BridgeWebChromeClient(final Bridge bridge) {
        this.bridge = bridge;
        this.permissionLauncher = bridge.registerForActivityResult((androidx.activity.result.contract.ActivityResultContract<Object, Object>)new ActivityResultContracts$RequestMultiplePermissions(), (androidx.activity.result.ActivityResultCallback<Object>)new BridgeWebChromeClient$$ExternalSyntheticLambda4(this));
        this.activityLauncher = bridge.registerForActivityResult((androidx.activity.result.contract.ActivityResultContract<Object, Object>)new ActivityResultContracts$StartActivityForResult(), (androidx.activity.result.ActivityResultCallback<Object>)new BridgeWebChromeClient$$ExternalSyntheticLambda5(this));
    }
    
    private File createImageFile(final Activity activity) throws IOException {
        final String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final StringBuilder sb = new StringBuilder("JPEG_");
        sb.append(format);
        sb.append("_");
        return File.createTempFile(sb.toString(), ".jpg", activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }
    
    private Uri createImageFileUri() throws IOException {
        final AppCompatActivity activity = this.bridge.getActivity();
        final File imageFile = this.createImageFile((Activity)activity);
        final StringBuilder sb = new StringBuilder();
        sb.append(this.bridge.getContext().getPackageName());
        sb.append(".fileprovider");
        return FileProvider.getUriForFile((Context)activity, sb.toString(), imageFile);
    }
    
    private String[] getValidTypes(final String[] array) {
        final ArrayList list = new ArrayList();
        final MimeTypeMap singleton = MimeTypeMap.getSingleton();
        for (final String s : array) {
            if (s.startsWith(".")) {
                final String mimeTypeFromExtension = singleton.getMimeTypeFromExtension(s.substring(1));
                if (mimeTypeFromExtension != null && !((List)list).contains((Object)mimeTypeFromExtension)) {
                    ((List)list).add((Object)mimeTypeFromExtension);
                }
            }
            else if (!((List)list).contains((Object)s)) {
                ((List)list).add((Object)s);
            }
        }
        final Object[] array2 = ((List)list).toArray();
        return (String[])Arrays.copyOf(array2, array2.length, (Class)String[].class);
    }
    
    private boolean isMediaCaptureSupported() {
        return PermissionHelper.hasPermissions(this.bridge.getContext(), new String[] { "android.permission.CAMERA" }) || !PermissionHelper.hasDefinedPermission(this.bridge.getContext(), "android.permission.CAMERA");
    }
    
    private void showFilePicker(final ValueCallback<Uri[]> valueCallback, final WebChromeClient$FileChooserParams webChromeClient$FileChooserParams) {
        final Intent intent = webChromeClient$FileChooserParams.createIntent();
        if (webChromeClient$FileChooserParams.getMode() == 1) {
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        }
        if (webChromeClient$FileChooserParams.getAcceptTypes().length > 1 || intent.getType().startsWith(".")) {
            final String[] validTypes = this.getValidTypes(webChromeClient$FileChooserParams.getAcceptTypes());
            intent.putExtra("android.intent.extra.MIME_TYPES", validTypes);
            if (intent.getType().startsWith(".")) {
                intent.setType(validTypes[0]);
            }
        }
        try {
            this.activityListener = (ActivityResultListener)new BridgeWebChromeClient$$ExternalSyntheticLambda11((ValueCallback)valueCallback);
            this.activityLauncher.launch((Object)intent);
        }
        catch (final ActivityNotFoundException ex) {
            valueCallback.onReceiveValue((Object)null);
        }
    }
    
    private boolean showImageCapturePicker(final ValueCallback<Uri[]> valueCallback) {
        final Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(this.bridge.getActivity().getPackageManager()) == null) {
            return false;
        }
        try {
            final Uri imageFileUri = this.createImageFileUri();
            intent.putExtra("output", (Parcelable)imageFileUri);
            this.activityListener = (ActivityResultListener)new BridgeWebChromeClient$$ExternalSyntheticLambda7(imageFileUri, (ValueCallback)valueCallback);
            this.activityLauncher.launch((Object)intent);
            return true;
        }
        catch (final Exception ex) {
            final StringBuilder sb = new StringBuilder("Unable to create temporary media capture file: ");
            sb.append(ex.getMessage());
            Logger.error(sb.toString());
            return false;
        }
    }
    
    private void showMediaCaptureOrFilePicker(final ValueCallback<Uri[]> valueCallback, final WebChromeClient$FileChooserParams webChromeClient$FileChooserParams, final boolean b) {
        final boolean b2 = Build$VERSION.SDK_INT >= 24;
        boolean b3;
        if (b && b2) {
            b3 = this.showVideoCapturePicker(valueCallback);
        }
        else {
            b3 = this.showImageCapturePicker(valueCallback);
        }
        if (!b3) {
            Logger.warn(Logger.tags("FileChooser"), "Media capture intent could not be launched. Falling back to default file picker.");
            this.showFilePicker(valueCallback, webChromeClient$FileChooserParams);
        }
    }
    
    private boolean showVideoCapturePicker(final ValueCallback<Uri[]> valueCallback) {
        final Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        if (intent.resolveActivity(this.bridge.getActivity().getPackageManager()) == null) {
            return false;
        }
        this.activityListener = (ActivityResultListener)new BridgeWebChromeClient$$ExternalSyntheticLambda15((ValueCallback)valueCallback);
        this.activityLauncher.launch((Object)intent);
        return true;
    }
    
    public boolean isValidMsg(final String s) {
        return !s.contains((CharSequence)"%cresult %c") && !s.contains((CharSequence)"%cnative %c") && !s.equalsIgnoreCase("[object Object]") && !s.equalsIgnoreCase("console.groupEnd");
    }
    
    public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
        final String tags = Logger.tags("Console");
        if (consoleMessage.message() != null && this.isValidMsg(consoleMessage.message())) {
            final String format = String.format("File: %s - Line %d - Msg: %s", new Object[] { consoleMessage.sourceId(), consoleMessage.lineNumber(), consoleMessage.message() });
            final String name = consoleMessage.messageLevel().name();
            if ("ERROR".equalsIgnoreCase(name)) {
                Logger.error(tags, format, null);
            }
            else if ("WARNING".equalsIgnoreCase(name)) {
                Logger.warn(tags, format);
            }
            else if ("TIP".equalsIgnoreCase(name)) {
                Logger.debug(tags, format);
            }
            else {
                Logger.info(tags, format);
            }
        }
        return true;
    }
    
    public void onGeolocationPermissionsShowPrompt(final String s, final GeolocationPermissions$Callback geolocationPermissions$Callback) {
        super.onGeolocationPermissionsShowPrompt(s, geolocationPermissions$Callback);
        final StringBuilder sb = new StringBuilder("onGeolocationPermissionsShowPrompt: DOING IT HERE FOR ORIGIN: ");
        sb.append(s);
        Logger.debug(sb.toString());
        final String[] array = { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" };
        if (!PermissionHelper.hasPermissions(this.bridge.getContext(), array)) {
            this.permissionListener = (PermissionListener)new BridgeWebChromeClient$$ExternalSyntheticLambda3(this, geolocationPermissions$Callback, s);
            this.permissionLauncher.launch((Object)array);
        }
        else {
            geolocationPermissions$Callback.invoke(s, true, false);
            Logger.debug("onGeolocationPermissionsShowPrompt: has required permission");
        }
    }
    
    public void onHideCustomView() {
        super.onHideCustomView();
    }
    
    public boolean onJsAlert(final WebView webView, final String s, final String message, final JsResult jsResult) {
        if (this.bridge.getActivity().isFinishing()) {
            return true;
        }
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(webView.getContext());
        alertDialog$Builder.setMessage((CharSequence)message).setPositiveButton((CharSequence)"OK", (DialogInterface$OnClickListener)new BridgeWebChromeClient$$ExternalSyntheticLambda1(jsResult)).setOnCancelListener((DialogInterface$OnCancelListener)new BridgeWebChromeClient$$ExternalSyntheticLambda2(jsResult));
        alertDialog$Builder.create().show();
        return true;
    }
    
    public boolean onJsConfirm(final WebView webView, final String s, final String message, final JsResult jsResult) {
        if (this.bridge.getActivity().isFinishing()) {
            return true;
        }
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(webView.getContext());
        alertDialog$Builder.setMessage((CharSequence)message).setPositiveButton((CharSequence)"OK", (DialogInterface$OnClickListener)new BridgeWebChromeClient$$ExternalSyntheticLambda12(jsResult)).setNegativeButton((CharSequence)"Cancel", (DialogInterface$OnClickListener)new BridgeWebChromeClient$$ExternalSyntheticLambda13(jsResult)).setOnCancelListener((DialogInterface$OnCancelListener)new BridgeWebChromeClient$$ExternalSyntheticLambda14(jsResult));
        alertDialog$Builder.create().show();
        return true;
    }
    
    public boolean onJsPrompt(final WebView webView, final String s, final String message, final String s2, final JsPromptResult jsPromptResult) {
        if (this.bridge.getActivity().isFinishing()) {
            return true;
        }
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(webView.getContext());
        final EditText view = new EditText(webView.getContext());
        alertDialog$Builder.setMessage((CharSequence)message).setView((View)view).setPositiveButton((CharSequence)"OK", (DialogInterface$OnClickListener)new BridgeWebChromeClient$$ExternalSyntheticLambda8(view, jsPromptResult)).setNegativeButton((CharSequence)"Cancel", (DialogInterface$OnClickListener)new BridgeWebChromeClient$$ExternalSyntheticLambda9(jsPromptResult)).setOnCancelListener((DialogInterface$OnCancelListener)new BridgeWebChromeClient$$ExternalSyntheticLambda10(jsPromptResult));
        alertDialog$Builder.create().show();
        return true;
    }
    
    public void onPermissionRequest(final PermissionRequest permissionRequest) {
        final boolean b = Build$VERSION.SDK_INT >= 23;
        final ArrayList list = new ArrayList();
        if (Arrays.asList((Object[])permissionRequest.getResources()).contains((Object)"android.webkit.resource.VIDEO_CAPTURE")) {
            ((List)list).add((Object)"android.permission.CAMERA");
        }
        if (Arrays.asList((Object[])permissionRequest.getResources()).contains((Object)"android.webkit.resource.AUDIO_CAPTURE")) {
            ((List)list).add((Object)"android.permission.MODIFY_AUDIO_SETTINGS");
            ((List)list).add((Object)"android.permission.RECORD_AUDIO");
        }
        if (!((List)list).isEmpty() && b) {
            final String[] array = (String[])((List)list).toArray((Object[])new String[0]);
            this.permissionListener = (PermissionListener)new BridgeWebChromeClient$$ExternalSyntheticLambda6(permissionRequest);
            this.permissionLauncher.launch((Object)array);
        }
        else {
            permissionRequest.grant(permissionRequest.getResources());
        }
    }
    
    public void onShowCustomView(final View view, final WebChromeClient$CustomViewCallback webChromeClient$CustomViewCallback) {
        webChromeClient$CustomViewCallback.onCustomViewHidden();
        super.onShowCustomView(view, webChromeClient$CustomViewCallback);
    }
    
    public boolean onShowFileChooser(final WebView webView, final ValueCallback<Uri[]> valueCallback, final WebChromeClient$FileChooserParams webChromeClient$FileChooserParams) {
        final List list = Arrays.asList((Object[])webChromeClient$FileChooserParams.getAcceptTypes());
        final boolean captureEnabled = webChromeClient$FileChooserParams.isCaptureEnabled();
        final boolean b = false;
        final boolean b2 = captureEnabled && list.contains((Object)"image/*");
        boolean b3 = b;
        if (captureEnabled) {
            b3 = b;
            if (list.contains((Object)"video/*")) {
                b3 = true;
            }
        }
        if (!b2 && !b3) {
            this.showFilePicker(valueCallback, webChromeClient$FileChooserParams);
        }
        else if (this.isMediaCaptureSupported()) {
            this.showMediaCaptureOrFilePicker(valueCallback, webChromeClient$FileChooserParams, b3);
        }
        else {
            this.permissionListener = (PermissionListener)new BridgeWebChromeClient$$ExternalSyntheticLambda0(this, (ValueCallback)valueCallback, webChromeClient$FileChooserParams, b3);
            this.permissionLauncher.launch((Object)new String[] { "android.permission.CAMERA" });
        }
        return true;
    }
    
    private interface ActivityResultListener
    {
        void onActivityResult(final ActivityResult p0);
    }
    
    private interface PermissionListener
    {
        void onPermissionSelect(final Boolean p0);
    }
}
