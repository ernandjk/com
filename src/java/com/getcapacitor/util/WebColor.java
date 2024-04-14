package com.getcapacitor.util;

import android.graphics.Color;

public class WebColor
{
    public static int parseColor(final String s) {
        String string = s;
        if (s.charAt(0) != '#') {
            final StringBuilder sb = new StringBuilder("#");
            sb.append(s);
            string = sb.toString();
        }
        if (string.length() != 7 && string.length() != 9) {
            throw new IllegalArgumentException("The encoded color space is invalid or unknown");
        }
        if (string.length() == 7) {
            return Color.parseColor(string);
        }
        final StringBuilder sb2 = new StringBuilder("#");
        sb2.append(string.substring(7));
        sb2.append(string.substring(1, 7));
        return Color.parseColor(sb2.toString());
    }
}
