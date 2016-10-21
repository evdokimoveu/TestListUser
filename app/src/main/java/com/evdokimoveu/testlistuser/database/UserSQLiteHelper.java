package com.evdokimoveu.testlistuser.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSQLiteHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "com.evdokimoveu.testlistuser.database.db";
    public static final String TABLE_USER = "table_user";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_USER_FIRST_NAME = "user_first_name";
    public static final String FIELD_USER_LAST_NAME = "user_last_name";

    public UserSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_FILM_CREATE = "CREATE TABLE " + TABLE_USER + " (" +
                FIELD_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                FIELD_USER_FIRST_NAME + " TEXT NOT NULL, " +
                FIELD_USER_LAST_NAME + " TEXT NOT NULL " +
                ");";

        db.execSQL(SQL_FILM_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
