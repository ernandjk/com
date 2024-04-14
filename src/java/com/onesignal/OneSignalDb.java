package com.onesignal;

import android.database.Cursor;
import android.database.SQLException;
import android.content.ContentValues;

public interface OneSignalDb
{
    void delete(final String p0, final String p1, final String[] p2);
    
    void insert(final String p0, final String p1, final ContentValues p2);
    
    void insertOrThrow(final String p0, final String p1, final ContentValues p2) throws SQLException;
    
    Cursor query(final String p0, final String[] p1, final String p2, final String[] p3, final String p4, final String p5, final String p6);
    
    Cursor query(final String p0, final String[] p1, final String p2, final String[] p3, final String p4, final String p5, final String p6, final String p7);
    
    int update(final String p0, final ContentValues p1, final String p2, final String[] p3);
}
