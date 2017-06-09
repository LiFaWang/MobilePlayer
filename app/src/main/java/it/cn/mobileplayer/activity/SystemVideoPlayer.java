package it.cn.mobileplayer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.cn.mobileplayer.R;
import it.cn.mobileplayer.domain.MediaItem;
import it.cn.mobileplayer.utils.LogUtil;
import it.cn.mobileplayer.utils.Utils;
import it.cn.mobileplayer.view.VideoView;

/**
 * Created by Administrator on 2016/12/10.
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private  boolean isUseSystem=false;
    private static final int PROGRESS = 1;
    // 隐藏控制面板
    private static final int HIDE_MEDIACONTROLLER = 2;
    /**
     * 显示网速
     */
    public static final int SHOW_SPEED = 3;
    /**
     * 全屏
     */
    private static final int FULL_SCREEN =1 ;
    /**
     * 默认屏幕
     */
    private static final int DEFAULT_SCREEN =2 ;
    private VideoView video_view;
    private Uri mUri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private RelativeLayout media_controller;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnSwitchScreen;
    private Utils mUtils;
    private TextView tv_netspeed;
    private LinearLayout ll_buffer;
    private TextView tv_loading_netspeed;
    private LinearLayout ll_loading;

    //监听电量变化的广播
    private MyReceiver receiver;
    //传入进来的列表
    private ArrayList<MediaItem> mediaItems;
    //播放的列表中的具体位置
    private int position;
    //1、定义手势识别器
    private GestureDetector detector;
    //是否显示控制面板
    private boolean isshowMediaController=false;
    //是否全屏
    private boolean isFullScreen=false;
    /**设置屏幕的宽
     *
     */
    private int screenWidth=0;
    /**设置屏幕的高
     *
     */
    private int screenHeight=0;
    /**
     * 真真实视频的宽和高
     */
    private int videoWidth;
    private int videoHeight;
    /**
     * 调节声音
     */
    private AudioManager am;
    /**
     * 当前的音量
     */
    private int currentVoice;
    /**
     * 音量0~15
     */
    private int maxVoice;
    /**
     * 是否是静音
     */
    private boolean isMute=false;
    /**
     * 是否是网络的uri
     */
    private boolean isNetUri;
    /**
     * 上一次的播放进度
     */

    private int preCurrentPosition;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-12-11 20:49:33 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);
        video_view = (VideoView) findViewById(R.id.video_view);
        media_controller= (RelativeLayout) findViewById(R.id.media_controller);
        tv_netspeed= (TextView) findViewById(R.id.tv_netspeed);
        ll_buffer= (LinearLayout) findViewById(R.id.ll_buffer);
        tv_loading_netspeed= (TextView) findViewById(R.id.tv_loading_netspeed);
        ll_loading= (LinearLayout) findViewById(R.id.ll_loading);


        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);
        //设置最大音量
        seekbarVoice.setMax(maxVoice);
        //当前音量
        seekbarVoice.setProgress(currentVoice);
        //开始更新网速
        mHandler.sendEmptyMessage(SHOW_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-12-11 20:49:33 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
            isMute=!isMute;
            updataVoice(currentVoice,isMute);
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
            showSwitchPlayerDialog();
        } else if (v == btnExit) {
            // Handle clicks for btnExit
            finish();
        } else if (v == btnVideoPre) {
            playPreVideo();
            // Handle clicks for btnVideoPre
        } else if (v == btnVideoStartPause) {
            // Handle clicks for btnVideoStartPause
            startAndPause();
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if (v == btnSwitchScreen) {
            // Handle clicks for btnSwitchScreen
            setFullScreenAndDefault();
        }
        mHandler.removeMessages(HIDE_MEDIACONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);}
    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提示");
        builder.setMessage("当您播放视频有声音没有画面时，请切换万能播放器播放") ;
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }



    private void startAndPause() {
        if (video_view.isPlaying()) {
            //视频在播放—设置暂停
            video_view.pause();
            //按钮状态设置播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);

        } else {
            //视频暂停—设置播放
            video_view.start();
            //按钮状态设置暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);


        }
    }

    //播放上一个视频
    private void playPreVideo() {
        if(mediaItems!=null&&mediaItems.size()>0){
            //播放上一个
            position--;
            if(position>=0){
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName()  );
                isNetUri=mUtils.isNetUri(mediaItem.getData());
                video_view.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        }else if(mUri!=null){
            //上一个和下一个的播放按钮设置为灰色，并且不可以点击
            setButtonState();
        }
    }


    private void playNextVideo() {
        if(mediaItems!=null&&mediaItems.size()>0){
            //播放下一个
            position++;
            if(position<mediaItems.size()){
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName()  );
                isNetUri=mUtils.isNetUri(mediaItem.getData());
                video_view.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        }else if(mUri!=null){
            //上一个和下一个的播放按钮设置为灰色，并且不可以点击
            setButtonState();
        }
    }

    private void setButtonState() {
        if(mediaItems!=null&&mediaItems.size()>0){
            if(mediaItems.size()==1){
                setEnableFalse();
            }else {
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                }else if (position==mediaItems.size()-1){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                }else{
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                }
            }

        }else if(mUri!=null){
            setEnableFalse();
        }
    }

    private void setEnableFalse() {
        btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
        btnVideoPre.setEnabled(false);
        btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        btnVideoNext.setEnabled(false);
    }


    private Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_SPEED://显示网速
                    //1、得到网速
                    String netSpeed = mUtils.getNetSpeed(SystemVideoPlayer.this);
                    //显示网络速度
                    tv_loading_netspeed.setText("玩命加载中..."+netSpeed);
                    tv_netspeed.setText("缓存中..."+netSpeed);
                    //2、两秒钟调用一次

                    mHandler.removeMessages(SHOW_SPEED);
                    mHandler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);
                    break;
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESS:
                    //1、得到当前的视频播放进度
                    int currentPosition = video_view.getCurrentPosition();
                    //2、设置当前进度
                    seekbarVideo.setProgress(currentPosition);
                    //更新文本的播放进度
                    tvCurrentTime.setText(mUtils.stringForTime(currentPosition));
                    //设置系统时间
                    tvSystemTime.setText(getSystemTime());
                    //缓冲进度的更新
                    if(isNetUri){
                        //只有网络的视频有缓冲
                        int buffer = video_view.getBufferPercentage();
                        int totalBuffer=buffer* seekbarVideo.getMax();
                        int secondaryProgress=totalBuffer/100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);

                    }else {
                        //本地没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    //监听卡
                    if(!isUseSystem){
                        if(video_view.isPlaying()){
                            int buffer= currentPosition-preCurrentPosition;
                            if(buffer<500){
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            }else {
                                //视频不卡
                                ll_buffer.setVisibility(View.GONE);
                            }

                        }else {
                            ll_buffer.setVisibility(View.GONE);
                        }

                    }
                    preCurrentPosition=currentPosition;

                    //3、每秒更新一次
                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }
        }
    };

    /*
    得到系统时间
    * */
    @TargetApi(Build.VERSION_CODES.N)
    private String getSystemTime() {
        SimpleDateFormat format = null;
            format = new SimpleDateFormat("HH:mm:ss");


            return format.format(new Date());


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();

        LogUtil.e("onCreate--");

        findViews();
        video_view = (VideoView) findViewById(R.id.video_view);
        setListener();


        //得到播放器的地址
        getData();
        setData();
        //设置控制面板
//        video_view.setMediaController(new MediaController(this));

    }

    private void setData() {
        if(mediaItems!=null&&mediaItems.size()>0){
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            isNetUri=mUtils.isNetUri(mediaItem.getData());
            video_view.setVideoPath(mediaItem.getData());
        }else if(mUri!=null){
            tvName.setText(mUri.toString());
            isNetUri=mUtils.isNetUri(mUri.toString());
            video_view.setVideoURI(mUri);
        }else {
            Toast.makeText(this, "没有传递数据", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        mUri = getIntent().getData();
            mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position",0);

    }

    private void initData() {
        mUtils = new Utils();
        //注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化的时候，发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
        //实例化手势识别器，并且重写双击，单击，长按
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            detector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    Toast.makeText(SystemVideoPlayer.this, "长按", Toast.LENGTH_SHORT).show();
                    startAndPause();
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Toast.makeText(SystemVideoPlayer.this, "双击", Toast.LENGTH_SHORT).show();
                    setFullScreenAndDefault();
                    return super.onDoubleTap(e);

                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
    //                Toast.makeText(SystemVideoPlayer.this, "单击", Toast.LENGTH_SHORT).show();

                    if(isshowMediaController){
                //隐藏
                        hideMediaController();
                        //把隐藏消息移除
                        mHandler.removeMessages(HIDE_MEDIACONTROLLER);

                    }else {
                        //显示
                        showMediaController();
                        //发消息隐藏

                        mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);

                    }
                    return super.onSingleTapConfirmed(e);

                }
            });

        //得到屏幕的宽和高,过时方法
//         screenWidth= getWindowManager().getDefaultDisplay().getWidth();
//         screenHeight= getWindowManager().getDefaultDisplay().getHeight();
        //得到屏幕的宽和高,新方法
        DisplayMetrics displayMetrics=new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        screenWidth=displayMetrics.widthPixels;
        screenHeight=displayMetrics.heightPixels;
        //得到音量
        am=(AudioManager)getSystemService(AUDIO_SERVICE);
        currentVoice=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void setFullScreenAndDefault() {
        if(isFullScreen){
            //设置为默认
            setVideoType(DEFAULT_SCREEN);

        }else {
            //设置为全屏
            setVideoType(FULL_SCREEN);

        }
    }

    private void setVideoType(int defaultScreen) {
       switch (defaultScreen){
           case FULL_SCREEN:
               //1、设置视频画面的大小-屏幕多大就设置多大
                video_view.setVideoSize(screenWidth,screenHeight);
              //2、设置按钮的状态-默认
               btnSwitchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
               isFullScreen=true;
               break;
           case DEFAULT_SCREEN:
               //1、设置视频画面的大小
               //视频真实宽和高
                int mVideoWidth=videoWidth;
               int mVideoHeight=videoHeight;
               int width=screenWidth;
               int height=screenHeight;
               if(mVideoWidth*height<width*mVideoHeight){
                   width=height*mVideoWidth/mVideoHeight;
               }else if(width*mVideoHeight<mVideoWidth*height){
                   height=width*mVideoHeight/mVideoWidth;
               }
               video_view.setVideoSize(width,height);
               //2、设置按钮的状态 -全屏
               btnSwitchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);

               isFullScreen=false;
               break;
       }
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);


        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);

        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }


    private void setListener() {
        //准备好的监听
        video_view.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错的监听
        video_view.setOnErrorListener(new MyOnErrorListener());

        // 播放完成的监听
        video_view.setOnCompletionListener(new MyOnCompletionListener());
        //设置seekBar状态变化监听
        seekbarVideo.setOnSeekBarChangeListener(new videoSeekBarChangeListener());
        seekbarVoice.setOnSeekBarChangeListener(new voiceOnSeekBarChangeListener());

        //监听视频卡顿
        if(isUseSystem){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                video_view.setOnInfoListener(new MyOnInfoListener());
            }
        }



    }
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    class MyOnInfoListener implements MediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
            switch(what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    Toast.makeText(SystemVideoPlayer.this, "拼命加载中...", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                    Toast.makeText(SystemVideoPlayer.this, "缓冲完成", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.GONE);

                    break;
            }
            return true;
        }
    }
    class voiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                if(progress>0){
                    isMute=false;
                }else{
                    isMute=true;
                }
                updataVoice(progress,isMute);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);


        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);


        }
    }

    /**
     * 设置音量的大小
     * @param progress
     */

    private void updataVoice(int progress,boolean isMute) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);

        }else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            seekbarVoice.setProgress(progress);
            currentVoice=progress;
        }

    }

    class videoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                video_view.seekTo(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);

        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
           videoWidth= mediaPlayer.getVideoWidth();
           videoHeight= mediaPlayer.getVideoHeight();
            video_view.start();
            int duration = video_view.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(mUtils.stringForTime(duration));
            mHandler.sendEmptyMessage(PROGRESS);
            hideMediaController();//默认隐藏控制面板
     //       video_view.setVideoSize(200,200);
//            video_view.setVideoSize(mediaPlayer.getVideoWidth(),mediaPlayer.getVideoHeight());
            setVideoType(DEFAULT_SCREEN);
            //加载的页面消失掉
            ll_loading.setVisibility(View.GONE);
//            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mediaPlayer) {
//                    Toast.makeText(SystemVideoPlayer.this, "拖动完成", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//            Toast.makeText(SystemVideoPlayer.this, "播放出错了哦", Toast.LENGTH_SHORT).show();
//            播放格式不支持跳转万能播放器
            startVitamioPlayer();
            return true;
        }

    }

    /**
     * 1、把数据传入Vitamio播放器
     * 2、关闭系统播放器
     */
    private void startVitamioPlayer() {
        if(video_view!=null){
            video_view.stopPlayback();
        }
        Intent intent =new Intent(this,VitamioVideoPlayer.class);

        if (mediaItems!=null&&mediaItems.size()>0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);

        }else if(mUri!=null){
        intent.setData(mUri);
        }
        this.startActivity(intent);
        finish();
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            playNextVideo();
//            Toast.makeText(SystemVideoPlayer.this, "播放完成=" + mUri, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart--");

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause--");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop--");

    }

    @Override
    protected void onDestroy() {
//      释放资源的时候，先释放子类，在释放父类
        mHandler.removeCallbacksAndMessages(null);
        if (receiver != null) {

            unregisterReceiver(receiver);
            receiver = null;

        }
        LogUtil.e("onDestroy--");
        super.onDestroy();

    }
    private float startY;
    private float startX;
    /**
     * 屏幕高，也就是总距离
     */
    private float touchRang;
    /**
     * 当按下的音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件传给递手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                //1、按下的时候记录值
                startY=event.getY();
                startX=event.getX();
                mVol=am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang=Math.min(screenHeight,screenWidth);
                mHandler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2、移动的时候记录相关值
                float endY=event.getY();
                float endX=event.getX();
                float distanceY=startY-endY;

                if(endX<screenWidth/2){
                    //左边屏幕调节亮度
                    final double FLING_MIN_DISTANCE=0.5;
                    final double FLING_MIN_VELOCITY=0.5;
                    if(distanceY>FLING_MIN_DISTANCE && Math.abs(distanceY)>FLING_MIN_VELOCITY){
                        setBrightness(20);

                    }
                    if(distanceY<FLING_MIN_DISTANCE && Math.abs(distanceY)>FLING_MIN_VELOCITY){
                        setBrightness(-20);
                    }

                }else {
                    //右边屏幕调节声音

                    //改变的音量=（滑动屏幕的距离：总距离）*音量最大值
                    float delta =(distanceY/touchRang)*maxVoice;
                    //最终声音=原来的+改变的声音
                    int voice = (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                    if(delta!=0){
                        isMute=false;
                        updataVoice(voice,isMute);
                    }
                }

                break;
            case MotionEvent.ACTION_UP://手指抬起
                mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                break;
        }
        return super.onTouchEvent(event);

    }
    private Vibrator vibrator;
    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public void setBrightness(float brightness){
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.screenBrightness=lp.screenBrightness+brightness/255.0f;
        if(lp.screenBrightness>1){
            lp.screenBrightness=1;
//            vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
//            long[] pattern={10,200};
//            vibrator.vibrate(pattern,-1);
        }else if(lp.screenBrightness<0){
            lp.screenBrightness=(float)0;
//            vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
//            long[] pattern={10,200};
//            vibrator.vibrate(pattern,-1);
        }
        getWindow().setAttributes(lp);

    }

    //显示控制面板
    private void showMediaController(){
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController=true;
    }
    //隐藏控制面板
    private void hideMediaController(){
        media_controller.setVisibility(View.GONE);
        isshowMediaController=false;
    }
    //监听物理键实现声音变化

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updataVoice(currentVoice,false);
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }else  if (keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updataVoice(currentVoice,false);
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
