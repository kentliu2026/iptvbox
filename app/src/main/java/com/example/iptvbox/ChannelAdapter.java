package com.example.iptvbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/** 频道列表适配器：自定义 item 布局，并在焦点态高亮（遥控器导航可见） */
public class ChannelAdapter extends ArrayAdapter<Channel> {

    private final LayoutInflater inflater;

    public ChannelAdapter(Context context, List<Channel> channels) {
        super(context, R.layout.item_channel, channels);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_channel, parent, false);
        }
        Channel c = getItem(position);
        TextView name = convertView.findViewById(R.id.ch_name);
        TextView url = convertView.findViewById(R.id.ch_url);
        if (c != null) {
            name.setText(c.name);
            url.setText(c.url);
        }
        return convertView;
    }
}
