package it.cn.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

import java.util.List;

import it.cn.mobileplayer.R;
import it.cn.mobileplayer.domain.SearchBean;

/**
 * Created by Administrator on 2016/12/9.
 */

public class SearchAdapter extends BaseAdapter  {
    private final Context context;
    private final List<SearchBean.ItemData> mMediaItems;


    public SearchAdapter(Context context,  List<SearchBean.ItemData> mMediaItems){
        this.context=context;
        this.mMediaItems=mMediaItems;
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
            view=View.inflate(context, R.layout.item_net_video_pager,null);
            viewHolder=new ViewHolder();
            viewHolder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tv_name= (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_desc= (TextView) view.findViewById(R.id.tv_desc);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        SearchBean.ItemData mediaItem = mMediaItems.get(i);
        viewHolder.tv_name.setText(mediaItem.getItemTitle());
        viewHolder.tv_desc.setText(mediaItem.getKeywords());
        x.image().bind( viewHolder.iv_icon,mediaItem.getItemImage().getImgUrl1());
        return view;
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }
}

