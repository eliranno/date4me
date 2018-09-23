package com.example.elirannoach.date4me.async;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

public class DatingCursorLoader extends android.support.v4.content.CursorLoader {

    public DatingCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }
}
