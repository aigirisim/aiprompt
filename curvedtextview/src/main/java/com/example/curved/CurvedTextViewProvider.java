package com.example.curved;

import androidx.annotation.NonNull;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


public class CurvedTextViewProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        // Initialization code here
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Query handling code here
        return null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        // Return the MIME type for the data at the given URI
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Insert handling code here
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Delete handling code here
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Update handling code here
        return 0;
    }
}
