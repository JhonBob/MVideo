package com.bob.mvideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.MotionEvent;

import com.bob.mvideo.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        delayEnterMainActivity(true);
    }

    private void delayEnterMainActivity(boolean isDelay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!hasEnter) {
                    hasEnter=true;
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

            }
        },isDelay?2000:0);
    }

    private boolean hasEnter=false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                delayEnterMainActivity(false);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
