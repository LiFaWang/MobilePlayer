package it.cn.mobileplayer.pager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import it.cn.mobileplayer.R;
import it.cn.mobileplayer.adapter.NetAudioPagerAdapter;
import it.cn.mobileplayer.base.BasePager;
import it.cn.mobileplayer.domain.NetAudioPagerData;
import it.cn.mobileplayer.utils.CacheUtile;
import it.cn.mobileplayer.utils.Constants;
import it.cn.mobileplayer.utils.LogUtil;

/**
 * Created by Administrator on 2016/12/7.
 */

public class NetAudioPager extends BasePager {
    @ViewInject(R.id.list_view)
    private ListView mListView;
    @ViewInject(R.id.tv_no_net)
    private TextView tv_no_net;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;
    /**
     * 页面的数据
     */
    private List<NetAudioPagerData.ListBean> datas;
    private NetAudioPagerAdapter adapter;


    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络音乐页面初始化");
        View view=View.inflate(context, R.layout.net_video_pager,null);
        x.view().inject(NetAudioPager.this,view);


        return view;
}

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音乐数据初始化");
        String saveJson= CacheUtile.getString(context,Constants.ALL_RES_URL);
        if(!TextUtils.isEmpty(saveJson)){
            //解析数据
            processData(saveJson);
        }
        //联网
        getDataFromNet();

    }


    private void getDataFromNet() {

        RequestParams params=new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功=="+result);
                //保存数据
                CacheUtile.putString(context,Constants.ALL_RES_URL,result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败=="+ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 解析json数据和显示数据
     * 1、用GsonFormat生成Bean对象2、用gson
     * @param json
     */
    private void processData(String json) {
        NetAudioPagerData data=parsedJson(json);
     datas = data.getList();
        if(datas!=null&&datas.size()>0){
            //有数据
            tv_no_net.setVisibility(View.GONE);
            //设置适配器
            adapter=new NetAudioPagerAdapter(context,datas);
            mListView.setAdapter(adapter);

        }else{
            tv_no_net.setText("没有对应的数据...");
            //无数据
            tv_no_net.setVisibility(View.VISIBLE);
        }
        pb_loading.setVisibility(View.GONE);
    }

    /**
     * Gson解析数据
     * @param json
     * @return
     */
    private NetAudioPagerData parsedJson(String json) {
        return new Gson().fromJson(json,NetAudioPagerData.class);
    }

}
