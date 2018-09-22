package com.example.elirannoach.date4me.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DateContentProvider extends ContentProvider {
    public static final int FAVORITE_CODE = 100;
    public static final int FAVORITE_CODE_WITH_ID = 101;

    AppSQLLiteOpenHelper mSQLHelper;

    /* The URI Matcher used by this content provider. */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DateContract.CONTENT_AUTHORITY;

        /*
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you no
         * they aren't going to change. In Todo, we use CODE_TODO or CODE_TODO_WITH_ID.
         */

        /* This URI is content://com.example.todo/todo/ */
        matcher.addURI(authority, DateContract.FavoriteEntry.TABLE_NAME, FAVORITE_CODE);

        /*
         * This URI would look something like content://com.example.todo/todo/1
         * The "/#" signifies to the UriMatcher that if TABLE_NAME is followed by ANY number,
         * that it should return the CODE_TODO_WITH_ID code
         */
        matcher.addURI(authority, DateContract.FavoriteEntry.TABLE_NAME + "/#", FAVORITE_CODE_WITH_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mSQLHelper = new AppSQLLiteOpenHelper(getContext());
        return mSQLHelper!=null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mSQLHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case FAVORITE_CODE_WITH_ID:
                String _ID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{_ID};
                cursor = db.query(DateContract.FavoriteEntry.TABLE_NAME,projection,DateContract.FavoriteEntry._ID+ "= ? ",
                        selectionArgs,null,null,sortOrder);
                break;
            case FAVORITE_CODE:
                cursor = db.query(DateContract.FavoriteEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case FAVORITE_CODE:
                long _id = db.insert(DateContract.FavoriteEntry.TABLE_NAME,null,values);
                if(_id!=-1){
                    getContext().getContentResolver().notifyChange(uri,null);
                    return DateContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(_id)).build();
                }
                break;
            default:
                return null;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        int result;
        switch (sUriMatcher.match(uri)){
            case FAVORITE_CODE_WITH_ID:
                String _ID = uri.getLastPathSegment();
                String[] whereArgs = new String[]{_ID};
                result = db.delete(DateContract.FavoriteEntry.TABLE_NAME,DateContract.FavoriteEntry._ID+ "= ? ",
                        whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
