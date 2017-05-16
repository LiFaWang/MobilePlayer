package it.cn.mobileplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import it.cn.mobileplayer.R;
import it.cn.mobileplayer.activity.SystemVideoPlayer;
import it.cn.mobileplayer.adapter.VideoPagerAdapter;
import it.cn.mobileplayer.base.BasePager;
import it.cn.mobileplayer.domain.MediaItem;
import it.cn.mobileplayer.utils.LogUtil;

/**
 * Created by Administrator on 2016/12/7.
 */

public class VideoPager extends BasePager {
    private TextView tv_no_media;
    private ListView list_view;
    private ProgressBar pb_loading;

    private VideoPagerAdapter videoPagerAdapter;
  private ArrayList<MediaItem> mMediaItems;
    public VideoPager(Context context) {
        super(context);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaItems != null && mMediaItems.size() > 0) {
                videoPagerAdapter = new VideoPagerAdapter(context,mMediaItems,true);
                list_view.setAdapter(videoPagerAdapter);
                tv_no_media.setVisibility(View.GONE);

            } else {
                tv_no_media.setVisibility(View.VISIBLE);

            }
            pb_loading.setVisibility(View.GONE);
        }
    };



    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        list_view = (ListView) view.findViewById(R.id.list_view);
        tv_no_media = (TextView) view.findViewById(R.id.tv_no_media);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //设置list_view的Item点击事件
        list_view.setOnItemClickListener( new MyOnItemClickListener());
        return view;
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          //  MediaItem mediaItem = mMediaItems.get(i);
//            Toast.makeText(context, "mediaItem=="+mediaItem.toString(), Toast.LENGTH_SHORT).show();
            //1.调取系统的所有播放-隐式意图调用
//            Intent intent =new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);
            //2.调用自己写的播放器-显示意图调用
//            Intent intent =new Intent(context,SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);
            //3、传递列表数据—对象-序列化
            Intent intent =new Intent(context,SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mMediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",i);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频数据初始化");
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        mMediaItems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] obs = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, obs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();

                        mMediaItems.add(mediaItem);
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(10);
            }
        }.start();
    }
}




