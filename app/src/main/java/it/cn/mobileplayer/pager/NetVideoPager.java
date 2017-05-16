package it.cn.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import it.cn.mobileplayer.R;
import it.cn.mobileplayer.activity.SystemVideoPlayer;
import it.cn.mobileplayer.adapter.NetVideoPagerAdapter;
import it.cn.mobileplayer.base.BasePager;
import it.cn.mobileplayer.domain.MediaItem;
import it.cn.mobileplayer.utils.Constants;
import it.cn.mobileplayer.utils.LogUtil;

/**
 * Created by Administrator on 2016/12/7.
 */

public class NetVideoPager extends BasePager {
    @ViewInject(R.id.list_view)
    private ListView mListView ;
    @ViewInject(R.id.tv_no_net)
    private TextView mTv_no;
    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;
    private ArrayList<MediaItem> mMediaItems;
    private NetVideoPagerAdapter mAdapter;
    public NetVideoPager(Context context) {
        super(context);
    }


    @Override
    public View initView() {
//        LogUtil.e("网络视频页面初始化");
        View view=View.inflate(context, R.layout.net_video_pager,null);
        x.view().inject(NetVideoPager.this,view);
        //设置list_view的Item点击事件
        mListView.setOnItemClickListener( new MyOnItemClickListener());
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
//        LogUtil.e("网络视频数据初始化");
        RequestParams requestParams=new RequestParams(Constants.NET_URL);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功=="+result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败=="+ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled=="+ cex.getMessage());

            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");

            }
        });


    }

    private void processData(String json) {
        mMediaItems=parseJson(json);
        showData();
    }




        private void showData() {
            //设置适配器
            if (mMediaItems != null && mMediaItems.size() > 0) {
                mAdapter = new NetVideoPagerAdapter(context, mMediaItems);
                mListView.setAdapter(mAdapter);
                mTv_no.setVisibility(View.GONE);

            } else {
                mTv_no.setVisibility(View.VISIBLE);

            }
            mProgressBar.setVisibility(View.GONE);
        }
    /**
     * 解析Json用系统自带的或者用第三方（Gson,fastjson）
     * @param json
     * @return
     */
        private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems= new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if(jsonArray!=null&&jsonArray.length()>0){
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    if(jsonObjectItem != null){

                        MediaItem mediaItem = new MediaItem();

                        String movieName = jsonObjectItem.optString("movieName");
                       mediaItem.setDesc(movieName);
                        String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);
                        String imageUrl=jsonObjectItem.getString("coverImg");
                        mediaItem.setImageUrl(imageUrl);
                        String hightUrl=jsonObjectItem.getString("hightUrl");
                        mediaItem.setData(hightUrl);
                        mediaItems.add(mediaItem);
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
