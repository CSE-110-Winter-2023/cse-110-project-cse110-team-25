package com.example.demo5;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

public class FriendAdapter extends BaseAdapter {

    private List<Friend> friends = Collections.emptyList();

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public List<Friend> getFriends() {
        return friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friends.get(position).getUid().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.wtf("ADAPTER", "notify data set changed");
        return convertView;
    }
}
