package it.cn.mobileplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import it.cn.mobileplayer.utils.LogUtil;

/**
 * Created by Administrator on 2016/12/10.
 */
public class TestB extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("onCreate--");}
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
        super.onDestroy();
        LogUtil.e("onDestroy--");

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            Intent intent = new Intent(this,TestB.class);
            startActivity(intent);

        }
        return super.onTouchEvent(event);

    }
}


