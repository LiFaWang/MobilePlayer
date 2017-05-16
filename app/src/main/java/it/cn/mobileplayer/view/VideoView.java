package it.cn.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/12/14.
 * 自定义VideoView
 */

public class VideoView extends android.widget.VideoView {
    //在代码中创建的时候用此方法
    public VideoView(Context context) {
        this(context,null);
    }
//当这个类在布局文件中，系统通过构造方法实例化该类
    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
//当需要设置样式的时候调用该方法
    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }
    //设置视频的宽和高

    /**
     * 设置视频的宽和高\n
     *
     * @param videoWidth 设置视频的宽
     * @param videoHeight 设置视频的高
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams params =getLayoutParams();
        params.width=videoWidth;
        params.height=videoHeight;
        setLayoutParams(params);

    }
}
