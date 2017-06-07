package it.cn.mobileplayer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import it.cn.mobileplayer.R;
import it.cn.mobileplayer.base.BasePager;
import it.cn.mobileplayer.fragment.ContentFragment;
import it.cn.mobileplayer.pager.AudioPager;
import it.cn.mobileplayer.pager.NetAudioPager;
import it.cn.mobileplayer.pager.NetVideoPager;
import it.cn.mobileplayer.pager.VideoPager;

/**
 *
 * Created by Administrator on 2016/12/7.
 */
public  class MainActivity extends FragmentActivity {
    private RadioGroup  rg_bottom_tag;
    private ArrayList<BasePager> mBasePagers;
    public int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rg_bottom_tag= (RadioGroup) findViewById(R.id.rg_bottom_tag);



        //设置RadioGroup监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video);
    }

    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "READ permission IS NOT granted...");

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                Log.d(TAG, "11111111111111");
            } else {
                // 0 是自己定义的请求coude
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//                Log.d(TAG, "222222222222");
            }
        } else {
            mBasePagers = new ArrayList<>();
            mBasePagers.add(new VideoPager(this));
            mBasePagers.add(new AudioPager(this));
            mBasePagers.add(new NetVideoPager(this));
            mBasePagers.add(new NetAudioPager(this));
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        Log.d(TAG, "requestCode=" + requestCode + "; --->" + permissions.toString()
//                + "; grantResult=" + grantResults.toString());
        switch (requestCode) {
            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // request successfully, handle you transactions
                    mBasePagers = new ArrayList<>();
                    mBasePagers.add(new VideoPager(this));
                    mBasePagers.add(new AudioPager(this));
                    mBasePagers.add(new NetVideoPager(this));
                    mBasePagers.add(new NetAudioPager(this));

                } else {

                    // permission denied
                    // request failed
                }

                return;
            }
            default:
                break;

        }
    }
 class MyCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i){
                default:position=0;
                    break;
                case R.id.rb_audio:
                    position=1;
                    break;
                case R.id.rb_video_net:
                    position=2;
                    break;
                case R.id.rb_audio_net:
                    position=3;
                    break;
            }
            setFragment();
        }
    }

    private  void setFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_main_content,new ContentFragment());
        fragmentTransaction.commit();
    }

    public BasePager getBasePager() {
        requestReadExternalPermission();
        mBasePagers = new ArrayList<>();
        mBasePagers.add(new VideoPager(this));
        mBasePagers.add(new AudioPager(this));
        mBasePagers.add(new NetVideoPager(this));
        mBasePagers.add(new NetAudioPager(this));
        BasePager basePager = null;
        if (mBasePagers != null && mBasePagers.size()!=0)
            basePager=mBasePagers.get(position);
        if (basePager!=null&&!basePager.isInitData){
            basePager.initData();
            basePager.isInitData=true;
        }
        return basePager;
    }

    /**
     * 是否已经退出
     */
    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(position!=0){
                rg_bottom_tag.check(R.id.rb_video);
                return true;
            }else if(!isExit){
                isExit=true;
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit=false;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
