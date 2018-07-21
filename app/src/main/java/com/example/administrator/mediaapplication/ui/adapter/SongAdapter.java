package com.example.administrator.mediaapplication.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.mediaapplication.database.MediaItem;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */

public class SongAdapter extends BaseAdapter {

    public List<MediaItem> mList;
    Context mContext;

    public static final int TYPE_FOLDER = 0;
    public static final int TYPE_ARTIST = 1;
    public static final int TYPE_ALBUM = 2;
    int mCurrentType = TYPE_FOLDER;
    boolean mIsInDir = true;

    public SongAdapter(Context context, List<MediaItem> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null);
            vh = new ViewHolder();
            vh.tv = convertView.findViewById(android.R.id.text1);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        MediaItem m = mList.get(position);
        switch (mCurrentType) {
            case TYPE_FOLDER:
                if(mIsInDir)
                    vh.tv.setText(m.getParentPath());
                else
                    vh.tv.setText(m.getPath());
                break;
            case TYPE_ARTIST:
                if (mIsInDir)
                    vh.tv.setText(m.getArtist() != null ? m.getArtist() : "未知");
                else
                    vh.tv.setText(m.getPath());
                break;
            case TYPE_ALBUM:
                if (mIsInDir)
                    vh.tv.setText(m.getAlbum() != null ? m.getAlbum() : "未知");
                else
                    vh.tv.setText(m.getPath());
                break;
        }
        return convertView;
    }

    public void setType(int type) {
        mCurrentType = type;
    }

    class ViewHolder {
        TextView tv;
    }

    public int getCurrentType() {
        return mCurrentType;
    }

    public void setIsInDir(boolean isIn) {
        mIsInDir = isIn;
    }

    public boolean getIsInDir() {
        return mIsInDir;
    }
}
