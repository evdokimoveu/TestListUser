package com.evdokimoveu.testlistuser.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evdokimoveu.testlistuser.R;
import com.evdokimoveu.testlistuser.model.User;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

    private ArrayList<User> users;
    private Context context;
    private LayoutInflater layoutInflater;

    public UserAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserViewHolder holder;
        View view = convertView;

        if(view == null){
            view = layoutInflater.inflate(R.layout.user_item, parent, false);
            holder = new UserViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.user_text);
            view.setTag(holder);
        }
        else{
            holder = (UserViewHolder)view.getTag();
        }

        holder.textView.setText(
                String.format(
                        context.getResources().getString(R.string.user_item_format),
                        users.get(position).getFirstName(),
                        users.get(position).getLastName()
                ));
        return view;
    }

    private class UserViewHolder{
        private TextView textView;
    }
}
