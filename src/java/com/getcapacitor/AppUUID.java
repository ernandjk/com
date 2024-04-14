package com.getcapacitor;

import android.content.SharedPreferences$Editor;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import androidx.appcompat.app.AppCompatActivity;

public final class AppUUID
{
    private static final String KEY = "CapacitorAppUUID";
    
    private static void assertAppUUID(final AppCompatActivity appCompatActivity) throws Exception {
        if (readUUID(appCompatActivity).equals((Object)"")) {
            regenerateAppUUID(appCompatActivity);
        }
    }
    
    private static String bytesToHex(final byte[] array) {
        final byte[] bytes = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
        final byte[] array2 = new byte[array.length * 2];
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFF;
            final int n2 = i * 2;
            array2[n2] = bytes[n >>> 4];
            array2[n2 + 1] = bytes[n & 0xF];
        }
        return new String(array2, StandardCharsets.UTF_8);
    }
    
    private static String generateUUID() throws NoSuchAlgorithmException {
        final MessageDigest instance = MessageDigest.getInstance("SHA-256");
        instance.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        return bytesToHex(instance.digest());
    }
    
    public static String getAppUUID(final AppCompatActivity appCompatActivity) throws Exception {
        assertAppUUID(appCompatActivity);
        return readUUID(appCompatActivity);
    }
    
    private static String readUUID(final AppCompatActivity appCompatActivity) {
        return appCompatActivity.getPreferences(0).getString("CapacitorAppUUID", "");
    }
    
    public static void regenerateAppUUID(final AppCompatActivity appCompatActivity) throws Exception {
        try {
            writeUUID(appCompatActivity, generateUUID());
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new Exception("Capacitor App UUID could not be generated.");
        }
    }
    
    private static void writeUUID(final AppCompatActivity appCompatActivity, final String s) {
        final SharedPreferences$Editor edit = appCompatActivity.getPreferences(0).edit();
        edit.putString("CapacitorAppUUID", s);
        edit.apply();
    }
}
