package it.cn.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.cn.mobileplayer.R;
import it.cn.mobileplayer.domain.MediaItem;

import it.cn.mobileplayer.utils.Utils;

/**
 * Created by Administrator on 2016/12/9.
 */

public class VideoPagerAdapter extends BaseAdapter  {
    private final Context context;
    private final ArrayList<MediaItem> mMediaItems;
    private final boolean isVideo;
    private Utils mUtils;
    

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mMediaItems,boolean isVideo){
        this.context=context;
        this.mMediaItems=mMediaItems;
        this.isVideo=isVideo;
        mUtils = new Utils();
    }

    @Override
    public int getCount() {
        return mMediaItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view=View.inflate(context, R.layout.item_video_pager,null);
            viewHolder=new ViewHolder();
            viewHolder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tv_name= (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_time= (TextView) view.findViewById(R.id.tv_time);
            viewHolder.tv_size= (TextView) view.findViewById(R.id.tv_size);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        MediaItem mediaItem = mMediaItems.get(i);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_size.setText(android.text.format.Formatter.formatFileSize(context,mediaItem.getSize()));
        viewHolder.tv_time.setText(mUtils.stringForTime((int) mediaItem.getDuration()));
        if(!isVideo){
            //音频
            viewHolder.iv_icon.setImageResource(R.drawable.qplay_auto_qqmusic_logo);
        }
        return view;
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}

