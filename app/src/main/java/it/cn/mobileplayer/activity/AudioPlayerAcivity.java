package it.cn.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import it.cn.mobileplayer.IMyMusicPlayerService;
import it.cn.mobileplayer.R;
import it.cn.mobileplayer.domain.MediaItem;
import it.cn.mobileplayer.service.MusicPlayerService;
import it.cn.mobileplayer.utils.LyricUtils;
import it.cn.mobileplayer.utils.Utils;
import it.cn.mobileplayer.view.ShowLyricView;


/**
 *
 * Created by Administrator on 2016/12/22.
 */

public class AudioPlayerAcivity extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;
    /**
     * 显示歌词
     */
    private static final int SHOW_LYRIC = 2;
    private int position;
    private IMyMusicPlayerService mService;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private ShowLyricView showLyricView;
//    private ShowLyricView1 showLyricView;
    private MyReceiver mReceiver;
    private Utils mUtils;
    private boolean notification;
    private  AnimationDrawable animationDrawable;



    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-12-22 20:07:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audioplayer);
        ivIcon = (ImageView) findViewById(R.id.ivIcon);
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        animationDrawable = (AnimationDrawable) ivIcon.getBackground();
        animationDrawable.start();
        tvArtist = (TextView) findViewById(R.id.tvArtist);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyrc = (Button) findViewById(R.id.btn_lyrc);
        showLyricView= (ShowLyricView) findViewById(R.id.showLyricView);
//        showLyricView= (ShowLyricView1) findViewById(R.id.showLyricView);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);
        //设置音乐的拖动
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

    }
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            //拖动进度
            try {
                mService.seekTo(progress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-12-22 20:07:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {


        if ( v == btnAudioPlaymode ) {
            // Handle clicks for btnAudioPlaymode
            setPlaymode();
        } else if ( v == btnAudioPre ) {
            // Handle clicks for btnAudioPre
            if(mService!=null){
                try {
                    mService.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        } else if ( v == btnAudioStartPause ) {
            // Handle clicks for btnAudioStartPause
            if(mService!=null){
                try {
                    if(mService.isPlaying()){
                        //暂停
                        mService.pause();
                        animationDrawable.stop();

                        //按钮设置为播放
                        btnAudioStartPause.setBackgroundResource(R.drawable.widget_control_play_hover);
                    }else {
                        //播放
                        mService.start();
                        animationDrawable.start();

                        //按钮设置为暂停
                        btnAudioStartPause.setBackgroundResource(R.drawable.widget_control_pause_hover);

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnAudioNext ) {
            // Handle clicks for btnAudioNext
            if(mService!=null){
                try {
                    mService.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnLyrc ) {
            // Handle clicks for btnLyrc
        }
    }

    private void setPlaymode() {
        try {
            int playmode=mService.getPlayMode();
            if(playmode==MusicPlayerService.REPEAT_NOMAL){
                playmode=MusicPlayerService.REPEAT_SINGEL;
            }else if(playmode==MusicPlayerService.REPEAT_SINGEL){
                playmode=MusicPlayerService.REPEAT_ALL;
            }else if(playmode==MusicPlayerService.REPEAT_ALL){
                playmode=MusicPlayerService.REPEAT_NOMAL;
            }else {
             playmode=MusicPlayerService.REPEAT_NOMAL;

            }
            mService.setPlayMode(playmode);
            //设置图片
            showPlaymode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {
        try {
            int playmode=mService.getPlayMode();
            if(playmode==MusicPlayerService.REPEAT_NOMAL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
                Toast.makeText(AudioPlayerAcivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }else if(playmode==MusicPlayerService.REPEAT_SINGEL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayerAcivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            }else if(playmode==MusicPlayerService.REPEAT_ALL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayerAcivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            }else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
                Toast.makeText(AudioPlayerAcivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验播放模式
     */

    private void checkPlaymode() {

        try {
            int playmode=mService.getPlayMode();
            if(playmode==MusicPlayerService.REPEAT_NOMAL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
            }else if(playmode==MusicPlayerService.REPEAT_SINGEL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            }else if(playmode==MusicPlayerService.REPEAT_ALL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            }else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
            }
            //校验播放和暂停的按钮

            if(mService.isPlaying()){
                btnAudioStartPause.setBackgroundResource(R.drawable.widget_control_play_hover);
            }else {
                btnAudioStartPause.setBackgroundResource(R.drawable.widget_control_pause_hover);

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LYRIC:
                    //显示歌词
                    //1、得到当前的进度
                    try {
                        int currentPosition =mService.getCurrentPosition();
                        //2、把进度传入ShowLyricView控件，并且计算亮那一句
                        showLyricView.setShowNextLyric(currentPosition);
                        //3、实时的发消息
                        mHandler.removeMessages(SHOW_LYRIC);
                        mHandler.sendEmptyMessage(SHOW_LYRIC);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } break;

                case PROGRESS:

                    try {
                        //1.得到当前进度
                        int currentPosition =mService.getCurrentPosition();
                        //2.设置seekBar.setProgress
                        seekbarAudio.setProgress(currentPosition);
                        //3.时间进度更新
                        tvTime.setText(mUtils.stringForTime(currentPosition)+"/"+mUtils.stringForTime(mService.getDuraition()));
                        //4.每秒更新一次
                        mHandler.removeMessages(PROGRESS);
                        mHandler.sendEmptyMessageDelayed(PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        findViews();
        getData();
        bindAndStartService();
    }

    private void initData() {
        mUtils=new Utils();
//        注册广播
//        mReceiver=new MyReceiver();
//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
//        registerReceiver(mReceiver, intentFilter);
        //1、EventBus注册
        EventBus.getDefault().register(this);

    }


    private ServiceConnection conn=new ServiceConnection() {
        /**
         * 当链接成功，回调这个方法
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService=IMyMusicPlayerService.Stub.asInterface(iBinder);
            if(mService!=null){
                try {
                    if(!notification){
                        mService.openAudio(position);
                    }else {
                        showViewData();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }



        /**
         * 当链接断开时回调这个方法
         * @param componentName
         */

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                if(mService!=null){
                    mService.stop();
                    mService=null;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            showData(null);
        }
    }
    //3、订阅方法
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
    public void showData(MediaItem mediaItem) {
//        发消息开始歌词同步
        showLyric();
        showViewData();
        checkPlaymode();
    }


    public void onEventMainThread(MediaItem mediaItem){
//        发消息开始歌词同步

        showLyric();
        showViewData();
        checkPlaymode();
    }


    private void showLyric() {
        //解析歌词
        LyricUtils lyricUtils =new LyricUtils();
        try {
            String path = mService.getAudioPath();
            //传歌词文件
            path=path.substring(0,path.lastIndexOf("."));
            File file=new File(path+".lrc");
            if(!file.exists()){
                file=new File(path+".txt");
            }
            lyricUtils.readLyricFile(file);
            showLyricView.setLyrics(lyricUtils.getLyrics());



        } catch (RemoteException e) {
            e.printStackTrace();
         }

        if(lyricUtils.isExistsLyric()){
            mHandler.sendEmptyMessage(SHOW_LYRIC);

        }
    }


    private void showViewData() {
        try {
            tvArtist.setText(mService.getArtist());
            tvName.setText(mService.getName());
            //设置进度条的最大值
            seekbarAudio.setMax(mService.getDuraition());
            //发消息
            mHandler.sendEmptyMessage(PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent=new Intent(this, MusicPlayerService.class);
        intent.setAction("it.cn.mobileplay_OPENAUDIO");
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);// bindAndStartService不会多次实例化服务
    }

    /**
     * 得到数据
     */

    private void getData() {
        notification=getIntent().getBooleanExtra("notification",false);
        if(!notification){
            position=getIntent().getIntExtra("position",0);
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
//        取消注册广播
//        if(mReceiver!=null){
//            unregisterReceiver(mReceiver);
//            mReceiver=null;
//        }
        //2、EvenBus取消注册
        EventBus.getDefault().unregister(this);

        //解绑服务
        if(conn!=null){
            unbindService(conn);
            conn=null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
