package com.getcapacitor;

import java.util.List;
import com.getcapacitor.util.HostMask;
import android.net.Uri;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class UriMatcher
{
    private static final int EXACT = 0;
    private static final int MASK = 3;
    static final Pattern PATH_SPLIT_PATTERN;
    private static final int REST = 2;
    private static final int TEXT = 1;
    private ArrayList<UriMatcher> mChildren;
    private Object mCode;
    private String mText;
    private int mWhich;
    
    static {
        PATH_SPLIT_PATTERN = Pattern.compile("/");
    }
    
    private UriMatcher() {
        this.mCode = null;
        this.mWhich = -1;
        this.mChildren = (ArrayList<UriMatcher>)new ArrayList();
        this.mText = null;
    }
    
    public UriMatcher(final Object mCode) {
        this.mCode = mCode;
        this.mWhich = -1;
        this.mChildren = (ArrayList<UriMatcher>)new ArrayList();
        this.mText = null;
    }
    
    public void addURI(final String s, final String s2, String mText, final Object mCode) {
        final String s3 = mText;
        if (mCode != null) {
            String[] split;
            if (s3 != null) {
                Object substring = s3;
                if (!mText.isEmpty()) {
                    substring = s3;
                    if (s3.charAt(0) == '/') {
                        substring = s3.substring(1);
                    }
                }
                split = UriMatcher.PATH_SPLIT_PATTERN.split((CharSequence)substring);
            }
            else {
                split = null;
            }
            int length;
            if (split != null) {
                length = split.length;
            }
            else {
                length = 0;
            }
            UriMatcher uriMatcher = this;
            for (int i = -2; i < length; ++i) {
                if (i == -2) {
                    mText = s;
                }
                else if (i == -1) {
                    mText = s2;
                }
                else {
                    mText = split[i];
                }
                final ArrayList<UriMatcher> mChildren = uriMatcher.mChildren;
                final int size = mChildren.size();
                int n = 0;
                Object o;
                while (true) {
                    o = uriMatcher;
                    if (n >= size) {
                        break;
                    }
                    o = mChildren.get(n);
                    if (mText.equals((Object)((UriMatcher)o).mText)) {
                        break;
                    }
                    ++n;
                }
                uriMatcher = (UriMatcher)o;
                if (n == size) {
                    uriMatcher = new UriMatcher();
                    if (i == -1 && mText.contains((CharSequence)"*")) {
                        uriMatcher.mWhich = 3;
                    }
                    else if (mText.equals((Object)"**")) {
                        uriMatcher.mWhich = 2;
                    }
                    else if (mText.equals((Object)"*")) {
                        uriMatcher.mWhich = 1;
                    }
                    else {
                        uriMatcher.mWhich = 0;
                    }
                    uriMatcher.mText = mText;
                    ((UriMatcher)o).mChildren.add((Object)uriMatcher);
                }
            }
            uriMatcher.mCode = mCode;
            return;
        }
        throw new IllegalArgumentException("Code can't be null");
    }
    
    public Object match(final Uri uri) {
        final List pathSegments = uri.getPathSegments();
        final int size = pathSegments.size();
        if (size == 0 && uri.getAuthority() == null) {
            return this.mCode;
        }
        UriMatcher uriMatcher = this;
        for (int i = -2; i < size; ++i) {
            String s;
            if (i == -2) {
                s = uri.getScheme();
            }
            else if (i == -1) {
                s = uri.getAuthority();
            }
            else {
                s = (String)pathSegments.get(i);
            }
            final ArrayList<UriMatcher> mChildren = uriMatcher.mChildren;
            if (mChildren == null) {
                break;
            }
            final int size2 = mChildren.size();
            int n = 0;
            UriMatcher uriMatcher2 = null;
            UriMatcher uriMatcher3;
            while (true) {
                uriMatcher3 = uriMatcher2;
                if (n >= size2) {
                    break;
                }
                final UriMatcher uriMatcher4 = (UriMatcher)mChildren.get(n);
                final int mWhich = uriMatcher4.mWhich;
                Label_0211: {
                    if (mWhich != 0) {
                        if (mWhich != 1) {
                            if (mWhich == 2) {
                                return uriMatcher4.mCode;
                            }
                            if (mWhich != 3) {
                                break Label_0211;
                            }
                            if (!HostMask.Parser.parse(uriMatcher4.mText).matches(s)) {
                                break Label_0211;
                            }
                        }
                    }
                    else if (!uriMatcher4.mText.equals((Object)s)) {
                        break Label_0211;
                    }
                    uriMatcher2 = uriMatcher4;
                }
                if (uriMatcher2 != null) {
                    uriMatcher3 = uriMatcher2;
                    break;
                }
                ++n;
            }
            uriMatcher = uriMatcher3;
            if (uriMatcher == null) {
                return null;
            }
        }
        return uriMatcher.mCode;
    }
}
