package com.example.elirannoach.date4me.Database;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

public class DateContract {
    public static final String CONTENT_AUTHORITY = "com.example.elirannoach.date4me";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String FAVORITE_PATH = "favorites";


    public static final class FavoriteEntry implements BaseColumns{
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_UID = "uid";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITE_PATH).build();
    }
}
