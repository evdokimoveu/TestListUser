package com.evdokimoveu.testlistuser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.evdokimoveu.testlistuser.database.UserSQLiteHelper;

public class ScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        UserSQLiteHelper userSQLiteHelper = new UserSQLiteHelper(this);
        SQLiteDatabase sqLiteDatabase = userSQLiteHelper.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query(UserSQLiteHelper.TABLE_USER,
                new String[]{
                        UserSQLiteHelper.FIELD_USER_ID,
                        UserSQLiteHelper.FIELD_USER_FIRST_NAME,
                        UserSQLiteHelper.FIELD_USER_LAST_NAME
                },
                null, null, null, null, null);

        if(cursor.getCount() == 0){
            for(int i = 0; i < 1000; i++){
                ContentValues values = new ContentValues();
                values.put(UserSQLiteHelper.FIELD_USER_FIRST_NAME, "first_name"+i);
                values.put(UserSQLiteHelper.FIELD_USER_LAST_NAME, "last_name"+i);
                sqLiteDatabase.insert(UserSQLiteHelper.TABLE_USER, null, values);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
