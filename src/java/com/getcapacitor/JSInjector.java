package com.getcapacitor;

import java.io.Reader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

class JSInjector
{
    private String bridgeJS;
    private String cordovaJS;
    private String cordovaPluginsFileJS;
    private String cordovaPluginsJS;
    private String globalJS;
    private String localUrlJS;
    private String pluginJS;
    
    public JSInjector(final String globalJS, final String bridgeJS, final String pluginJS, final String cordovaJS, final String cordovaPluginsJS, final String cordovaPluginsFileJS, final String localUrlJS) {
        this.globalJS = globalJS;
        this.bridgeJS = bridgeJS;
        this.pluginJS = pluginJS;
        this.cordovaJS = cordovaJS;
        this.cordovaPluginsJS = cordovaPluginsJS;
        this.cordovaPluginsFileJS = cordovaPluginsFileJS;
        this.localUrlJS = localUrlJS;
    }
    
    private String readAssetStream(final InputStream inputStream) {
        try {
            final char[] array = new char[1024];
            final StringBuilder sb = new StringBuilder();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            while (true) {
                final int read = ((Reader)inputStreamReader).read(array, 0, 1024);
                if (read < 0) {
                    break;
                }
                sb.append(array, 0, read);
            }
            return sb.toString();
        }
        catch (final Exception ex) {
            Logger.error("Unable to process HTML asset file. This is a fatal error", (Throwable)ex);
            return "";
        }
    }
    
    public InputStream getInjectedStream(final InputStream inputStream) {
        final StringBuilder sb = new StringBuilder("<script type=\"text/javascript\">");
        sb.append(this.getScriptString());
        sb.append("</script>");
        final String string = sb.toString();
        String s = this.readAssetStream(inputStream);
        if (s.contains((CharSequence)"<head>")) {
            final StringBuilder sb2 = new StringBuilder("<head>\n");
            sb2.append(string);
            sb2.append("\n");
            s = s.replace((CharSequence)"<head>", (CharSequence)sb2.toString());
        }
        else if (s.contains((CharSequence)"</head>")) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append(string);
            sb3.append("\n</head>");
            s = s.replace((CharSequence)"</head>", (CharSequence)sb3.toString());
        }
        else {
            Logger.error("Unable to inject Capacitor, Plugins won't work");
        }
        return (InputStream)new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }
    
    public String getScriptString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.globalJS);
        sb.append("\n\n");
        sb.append(this.localUrlJS);
        sb.append("\n\n");
        sb.append(this.bridgeJS);
        sb.append("\n\n");
        sb.append(this.pluginJS);
        sb.append("\n\n");
        sb.append(this.cordovaJS);
        sb.append("\n\n");
        sb.append(this.cordovaPluginsFileJS);
        sb.append("\n\n");
        sb.append(this.cordovaPluginsJS);
        return sb.toString();
    }
}
