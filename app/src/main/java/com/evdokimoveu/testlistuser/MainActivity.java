package com.evdokimoveu.testlistuser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.evdokimoveu.testlistuser.adapter.UserAdapter;
import com.evdokimoveu.testlistuser.database.UserSQLiteHelper;
import com.evdokimoveu.testlistuser.model.User;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private final String APP_PREFERENCES = "max_elements_preference";
    private final String CURRENT_MAX_ELEMENTS = "current_max_elements";
    private final int LOAD_FACTOR = 10;

    private int maxElementsInMemory;
    private ListView userListView;
    private Cursor cursor;
    private ArrayList<User> users;
    private boolean isLoading;
    private int currentFirstPosition = 0;
    private int currentLastPosition = 0;
    private int endDataBasePosition = 0;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(preferences.contains(CURRENT_MAX_ELEMENTS)) {
            maxElementsInMemory = preferences.getInt(CURRENT_MAX_ELEMENTS, 50);
        }
        else{
            maxElementsInMemory = 50;
        }

        userListView = (ListView) findViewById(R.id.user_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(userListView);

        UserSQLiteHelper userSQLiteHelper = new UserSQLiteHelper(this);
        SQLiteDatabase sqLiteDatabase = userSQLiteHelper.getReadableDatabase();

        cursor = sqLiteDatabase.query(
                UserSQLiteHelper.TABLE_USER,
                new String[]{
                        UserSQLiteHelper.FIELD_USER_ID,
                        UserSQLiteHelper.FIELD_USER_FIRST_NAME,
                        UserSQLiteHelper.FIELD_USER_LAST_NAME
                },
        null, null, null, null, null);

        users = new ArrayList<>();
        for(int i = 0; i < maxElementsInMemory; i++){
            cursor.moveToNext();
            users.add(getUser());
        }

        currentFirstPosition = 0;
        currentLastPosition = maxElementsInMemory;
        endDataBasePosition = cursor.getCount();

        UserAdapter adapter = new UserAdapter(users, this);
        userListView.setAdapter(adapter);
        userListView.setOnScrollListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_MAX_ELEMENTS, maxElementsInMemory);
        editor.apply();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (visibleItemCount + firstVisibleItem >= totalItemCount
                && currentLastPosition < endDataBasePosition
                && !isLoading) {
            isLoading = true;
            loadNextUsers();
        }
        if(firstVisibleItem == 1
                && !isLoading
                && currentFirstPosition > 0){
            isLoading = true;
            loadPreviewsUsers();
        }
    }

    /**
     * If scrolling up
     */
    private void loadNextUsers(){
        int countNewUser = maxElementsInMemory / LOAD_FACTOR;
        int i = 0;
        cursor.moveToPosition(currentLastPosition);
        while(cursor.moveToNext() && i < countNewUser){
            if(users.size() != 0){
                users.remove(0);
            }
            users.add(getUser());
            currentFirstPosition++;
            currentLastPosition++;
            i++;
        }
        isLoading = false;
        ((BaseAdapter) userListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * If scrolling down
     */
    private void loadPreviewsUsers(){
        int countNewUser = maxElementsInMemory / LOAD_FACTOR;
        int i = 0;
        cursor.moveToPosition(currentFirstPosition);
        while(cursor.moveToPrevious() && i < countNewUser){
            if(users.size() != 0){
                users.remove(maxElementsInMemory - 1);
            }
            users.add(0, getUser());
            currentFirstPosition--;
            currentLastPosition--;
            i++;
        }
        isLoading = false;
        ((BaseAdapter) userListView.getAdapter()).notifyDataSetChanged();
    }

    private User getUser(){
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndex(UserSQLiteHelper.FIELD_USER_ID)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.FIELD_USER_FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.FIELD_USER_LAST_NAME)));
        return user;
    }

    public void fabOnClick(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogView);

        final EditText maxElementsInput = (EditText) dialogView.findViewById(R.id.edit_dialog_massage);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = maxElementsInput.getText().toString();
                        if(!TextUtils.isEmpty(inputText)){
                            try{
                                maxElementsInMemory = Integer.valueOf(inputText);
                                ((BaseAdapter) userListView.getAdapter()).notifyDataSetChanged();
                            } catch (NumberFormatException ex){
                                Log.e("", ex.getMessage());
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
