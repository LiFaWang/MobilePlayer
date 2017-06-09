package it.cn.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import it.cn.mobileplayer.IMyMusicPlayerService;
import it.cn.mobileplayer.R;
import it.cn.mobileplayer.activity.AudioPlayerAcivity;
import it.cn.mobileplayer.domain.MediaItem;
import it.cn.mobileplayer.utils.CacheUtile;

/**
 * Created by Administrator on 2016/12/22.
 */


public class MusicPlayerService extends Service {
    public static final String OPENAUDIO = "it.cn.mobileplay_OPENAUDIO";
    private ArrayList<MediaItem>mMediaItems;
    private int position;
    /**
     * 用于播放音乐
     */
    private MediaPlayer mMediaPlayer;
    /**
     *当前播放的音乐
     */
    private MediaItem mediaItem;
    /**
     * 顺序播放
     */
    public static final int REPEAT_NOMAL=1;
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGEL=2;
    /**
     * 全部循环
     */
    public static final int REPEAT_ALL=3;
    /**
     * 播放模式
     */
    private int playmode=REPEAT_NOMAL;




    @Override
    public void onCreate() {
        super.onCreate();
        playmode= CacheUtile.getPlayMode(this,"playmode");
        //加载音乐列表
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        mMediaItems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] obs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
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

            }
        }.start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    private IMyMusicPlayerService.Stub mStub=new IMyMusicPlayerService.Stub() {
    MusicPlayerService mMusicPlayerService =MusicPlayerService.this;
    @Override
    public void openAudio(int position) throws RemoteException {
        mMusicPlayerService.openAudio(position);
    }

    @Override
    public void start() throws RemoteException {
       mMusicPlayerService.start();
    }

    @Override
    public void pause() throws RemoteException {
        mMusicPlayerService.pause();
    }

    @Override
    public void stop() throws RemoteException {
        mMusicPlayerService.stop();
    }

    @Override
    public int getCurrentPosition() throws RemoteException {

        return mMusicPlayerService.getCurrentPosition();
    }

    @Override
    public int getDuraition() throws RemoteException {
        return mMusicPlayerService.getDuraition();
    }

    @Override
    public String getArtist() throws RemoteException {
        return mMusicPlayerService.getArtist();
    }

    @Override
    public String getName() throws RemoteException {
        return  mMusicPlayerService.getName();
    }

    @Override
    public String getAudioPath() throws RemoteException {

        return mMusicPlayerService.getAudioPath();
    }

    @Override
    public void next() throws RemoteException {
        mMusicPlayerService.next();

    }

    @Override
    public void pre() throws RemoteException {
        mMusicPlayerService.pre();

    }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
        mMusicPlayerService.setPlayMode(playMode);
        }


        @Override
    public int getPlayMode() throws RemoteException {

        return mMusicPlayerService.getPlayMode();
    }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mMusicPlayerService.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mMediaPlayer.seekTo(position);
        }
    };

    /**
     *是否在播放音频
     * @return
     */

    private boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }



    /**
     * 依据位置打开音乐,并且播放
     * @param position
     */
    private void openAudio(int position){
        this.position=position;
        if(mMediaItems!=null&&mMediaItems.size()>0){
          mediaItem =mMediaItems.get(position);
        if (mMediaPlayer!=null){
//              mMediaPlayer.release();
            mMediaPlayer.reset();
        }
            try {
            mMediaPlayer=new MediaPlayer();
                //设置 监听 播放出错，播放完成。准备好
                mMediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mMediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mMediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mMediaPlayer.setDataSource(mediaItem.getData());
                mMediaPlayer.prepareAsync();
                if(playmode==MusicPlayerService.REPEAT_SINGEL){
                    //单曲循环播放-不会触发播放完成的回调
                    mMediaPlayer.setLooping(true);
                }else {
                    //不循环播放
                    mMediaPlayer.setLooping(false);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(getApplicationContext(), "还没有数据", Toast.LENGTH_SHORT).show();
        }


    }
    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            next();
            return true;
        }
    }
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
        next();
        }
    }
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void  onPrepared(MediaPlayer mediaPlayer) {
            //通知activity获取广播
//            notifyChange(OPENAUDIO);
            EventBus.getDefault().post(mediaItem);

            start();
        }
    }

    /**
     * 根据动作发广播
     * @param action
     */

    private void notifyChange(String action) {
    Intent intent=new Intent(action);
        sendBroadcast(intent);

    }
    private NotificationManager mNotificationManager;
    /**
     * 音乐播放
     */
    private void start (){
        mMediaPlayer.start();
        mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent =new Intent(this, AudioPlayerAcivity.class);
        intent.putExtra("notification",true);
        PendingIntent pi =PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification=new Notification.Builder(this).setSmallIcon(R.drawable.logo)
                .setContentTitle("手机影音")
                .setContentText("正在播放"+mediaItem.getName())
                .setContentIntent(pi)
                .build();
        mNotificationManager.notify(1, notification);

    }
    /**
     * 音乐暂停
     */
    private void pause (){
        mMediaPlayer.pause();
        mNotificationManager.cancel(1);

    }
    /**
     * 音乐停止
     */
    private void stop (){

    }

    /**
     * 得到当前的播放进度
     * @return
     */
    private int getCurrentPosition(){
       return mMediaPlayer.getCurrentPosition();
    }
    /**
     * 得到音频的总时长
     * @return
     */
    private int getDuraition(){
       return mMediaPlayer.getDuration();
    }
    /**
     * 得到艺术家
     * @return
     */
    private String getArtist(){
       return mediaItem.getArtist();
    }
    /**
     * 得到音频的名字
     * @return
     */
    private String getName(){
       return mediaItem.getName();
    }
    /**
     * 得到音频的路径
     * @return
     */
    private String getAudioPath(){
       return mediaItem.getData();
    }

    /**
     * 播放下一个
     */
    private void next(){
        //1、根据当前播放模式，设置下一个位置
        setNextPosition();
        //2、根据当前播放模式，播放下一个
        openNextAudio();
    }

    private void openNextAudio() {
        int playmode=getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NOMAL){
            if(position<mMediaItems.size()){
                openAudio(position);
            }else {
                position=mMediaItems.size()-1;
            }
        }else if(playmode==MusicPlayerService.REPEAT_SINGEL){
            openAudio(position);
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else {
            if(position<mMediaItems.size()){
                openAudio(position);
            }else {
                position=mMediaItems.size()-1;
            }
        }


    }

    private void setNextPosition() {
        int playmode=getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NOMAL){
            position++;

        }else if(playmode==MusicPlayerService.REPEAT_SINGEL){
            position++;
            if(position>mMediaItems.size()-1){
                position=0;
            }
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            position++;
            if(position>mMediaItems.size()-1){
                position=0;
            }
        }else {

            position++;
        }
    }

    /**
     * 播放上一个
     */
    private void pre(){
        //1、根据当前播放模式，设置上一个位置
        setPryPosition();
        //2、根据当前播放模式，播放上一个
        openPryAudio();
    }

    private void openPryAudio() {
        int playmode=getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NOMAL){
            if(position>=0){
                openAudio(position);
            }else {
                position=0;
            }
        }else if(playmode==MusicPlayerService.REPEAT_SINGEL){
            openAudio(position);
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else {
            if(position>=0){
                openAudio(position);
            }else {
                position=0;
            }
        }
    }

    private void setPryPosition() {
        int playmode=getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NOMAL){
            position--;

        }else if(playmode==MusicPlayerService.REPEAT_SINGEL){
            position--;
            if(position<0){
                position=mMediaItems.size()-1;
            }
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            position--;
            if(position<0){
                position=mMediaItems.size()-1;
            }
        }else {

            position--;
        }
    }

    /**
     * 设置播放模式
     * @param playMode
     */
    private void setPlayMode(int playMode){
    this.playmode=playMode;
        CacheUtile.putPlayMode(this,"playmode",playmode);
        if(playmode==MusicPlayerService.REPEAT_SINGEL){
            //单曲循环播放-不会触发播放完成的回调
            mMediaPlayer.setLooping(true);
        }else {
            //不循环播放
            mMediaPlayer.setLooping(false);
        }
    }

    /**
     * 得到播放模式

     * @return
     */
    private int  getPlayMode(){
        return playmode;

    }
}
