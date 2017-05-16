package it.cn.mobileplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import it.cn.mobileplayer.R;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    Handler mHandler =new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(new Runnable() {
           @Override
           public void run() {
            startMainActivity();
               Log.e(TAG,"当前线程的名称："+Thread.currentThread().getName());
           }
       },2000);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent ："+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    private boolean isStartMain = false;
    private void startMainActivity() {
        if (!isStartMain){
            isStartMain=true;
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();}

    }
}
