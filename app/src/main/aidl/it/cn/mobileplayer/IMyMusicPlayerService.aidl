// IMyMusicPlayerService.aidl
package it.cn.mobileplayer;

// Declare any non-default types here with import statements

interface IMyMusicPlayerService {


 /**
     * 依据位置打开音乐
     * @param position
     */
  void openAudio(int position);

    /**
     * 音乐播放
     */
    void start ();
    /**
     * 音乐暂停
     */
     void pause ();
    /**
     * 音乐停止
     */
     void stop ();
    /**
     * 得到当前的播放进度
     * @return
     */
    int getCurrentPosition();

    /**
     * 得到音频的总时长
     * @return
     */
    int getDuraition();
    /**
     * 得到艺术家
     * @return
     */
     String getArtist();
    /**
     * 得到音频的名字
     * @return
     */
    String getName();
    /**
     * 得到音频的路径
     * @return
     */
    String getAudioPath();

    /**
     * 播放下一个
     */
     void next();
    /**
     * 播放上一个
     */
    void pre();

    /**
     * 设置播放模式
     * @param playMode
     */
     void setPlayMode(int playMode);

    /**
     * 得到播放模式

     * @return
     */
    int  getPlayMode();
    /**
    *是否正在播放
    */
    boolean isPlaying();

    /**
     * 拖动音频
     * @param position
     */
    void seekTo(int position);
    }
