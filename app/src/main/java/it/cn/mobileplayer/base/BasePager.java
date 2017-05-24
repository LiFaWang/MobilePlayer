package it.cn.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 *
 * Created by Administrator on 2016/12/7.
 */

public abstract class BasePager  {

    public final Context context;
    public View rootView;
    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootView=initView();
    }

    public abstract View initView();
    public void initData (){

    }
}
