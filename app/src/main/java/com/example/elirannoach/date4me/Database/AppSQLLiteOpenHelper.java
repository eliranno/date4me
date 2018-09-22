package com.example.elirannoach.date4me.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.elirannoach.date4me.utils.FireBaseUtils;

public class AppSQLLiteOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "date4me_"+ FireBaseUtils.getFireBaseUserUid() +".db";

    public AppSQLLiteOpenHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TODO_TABLE =
                "CREATE TABLE " + DateContract.FavoriteEntry.TABLE_NAME + " (" +
                        DateContract.FavoriteEntry._ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DateContract.FavoriteEntry.COLUMN_UID       + " TEXT UNIQUE NOT NULL )";

        db.execSQL(SQL_CREATE_TODO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DateContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
