package it.cn.mobileplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.ArrayList;

import it.cn.mobileplayer.domain.Lyric;
import it.cn.mobileplayer.utils.DensityUtil;

/**
 * 自定义歌词显示
 * Created by Administrator on 2016/12/26.
 */

public class ShowLyricView extends android.support.v7.widget.AppCompatTextView {
    /**
     * 歌词列表
     */
    private ArrayList<Lyric>lyrics;
    private Paint paint;
    private int width;
    private int height;
    /**
     * 每行的高
     */
    private  float textHeight;
    /**
     * 歌词列表索引
     */
    private int index;
    /**
     * 当前播放进度
     */
    private float currentPosition;
    /**
     * 高亮显示的时间
     */
    private float sleepTime;
    /**
     * 什么时刻到该歌词
     */
    private float timePoint;
    private Paint whitePaint;


    /**
     * 设置歌词列表
     * @param lyrics
     */

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context,null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
    }

    private void initView(Context context) {
        textHeight= DensityUtil.dip2px(context,18);
        //创建画笔
        paint=new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context,16));
        paint.setAntiAlias(true);
        //设置居中
        paint.setTextAlign(Paint.Align.CENTER);
        //创建白画笔
        whitePaint=new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(DensityUtil.dip2px(context,16));
        whitePaint.setAntiAlias(true);
        //设置居中
        whitePaint.setTextAlign(Paint.Align.CENTER);
//        lyrics=new ArrayList<>();
//        Lyric lyric=new Lyric();
//        for(int i=0 ;i<1000;i++){
//            Lyric lyric=new Lyric();
//            lyric.setTimePoint(1000*i);
//            lyric.setSleepTime(1500+i);
//            lyric.setContent(i+"aaaaaaaaa"+i);
//            lyrics.add(lyric);
//
//        }

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics!=null&&lyrics.size()>0){
            //往上推移
            float plush=0;
            if(sleepTime==0){
                plush=0;
            }else {
                //平移
                //
             //   float delta=((currentPsoition-timePiont)/sleepTime)*textHeight;
                plush=textHeight+((currentPosition-timePoint)/sleepTime)*textHeight;
            }
            canvas.translate(0,-plush);
            //绘制歌词：绘制当前句
            String currentText= lyrics.get(index).getContent();
            canvas.drawText(currentText,width/2,height/2,paint);
            //绘制前面部分
            float tempY =height/2;//Y轴中间坐标
            for(int i=index-1;i>=0;i --){
                //每一句歌词
                String preContent=lyrics.get(i).getContent();
                tempY=tempY-textHeight;
                if(tempY<0){
                    break;
                }
                canvas.drawText(preContent,width/2,tempY,whitePaint);

            }
            //绘制后面部分
            tempY=height/2;//Y中间坐标
            for(int i =index+1;i<lyrics.size();i++){
                //每一句歌词
                String nextContent=lyrics.get(i).getContent();
                tempY=tempY+textHeight;
                if(tempY>height){
                    break;
                }
                canvas.drawText(nextContent,width/2,tempY,whitePaint);
            }


        }else {
            //没有歌词

            canvas.drawText("没有歌词",width/2,height/2,paint);

        }
    }

    /**
     * 根据当前播放的位置，找出高亮显示的那句歌词
     * @param currentPosition
     */

    public void setShowNextLyric(int currentPosition) {
        this.currentPosition=currentPosition;
        if(lyrics==null||lyrics.size()==0)
            return;
            for(int i= 1;i<lyrics.size();i++){
                if(currentPosition<lyrics.get(i).getTimePoint()){

                int tempIndex =i-1;
                    if(currentPosition>=lyrics.get(tempIndex).getTimePoint()){
                        //当前正在播放哪句歌词
                        index=tempIndex;
                        sleepTime=lyrics.get(index).getSleepTime();
                        timePoint=lyrics.get(index).getTimePoint();

                    }
            }
        }
        //重新绘制
        invalidate();//在主线程

    }
}
