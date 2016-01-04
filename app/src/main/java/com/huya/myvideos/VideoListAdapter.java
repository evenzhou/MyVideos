package com.huya.myvideos;

/**
 * Created by even on 16/1/3.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huya.myvideos.R;
import com.huya.myvideos.provider.Video;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 列表的适配器
 * Created by even on 16/1/3.
 */

class VideoListAdapter extends BaseAdapter {
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<Video> videos;
    private Context context;
    public VideoListAdapter(Context context, ArrayList<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        //// FIXME: 2016-01-04 将footer view 合并
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.vlist, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.image = (ImageView) convertView.findViewById(R.id.img);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
            holder.times = (TextView) convertView.findViewById(R.id.times);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //用videos 的值填充
        holder.title.setText(videos.get(position).getVideo_title());
        holder.dateline.setText(videos.get(position).getUpload_start_time());
        holder.times.setText(videos.get(position).getPlay_sum());
        imageLoader.displayImage(videos.get(position).getCover(), holder.image);
        return convertView;
    }
    class ViewHolder {
        public ImageView image;
        public TextView dateline;
        public TextView times;
        public TextView title;
    }
}

