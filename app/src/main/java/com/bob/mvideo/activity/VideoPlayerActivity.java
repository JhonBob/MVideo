package com.bob.mvideo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import com.bob.mvideo.R;
import com.bob.mvideo.base.BaseActivity;
import com.bob.mvideo.bean.VideoItem;
import com.bob.mvideo.util.LogUtil;
import com.bob.mvideo.util.StringUtil;

public class VideoPlayerActivity extends BaseActivity {
    private VideoView videoView;
    private ImageView btn_exit,btn_pre,btn_play,btn_next,btn_fullscreen;
    private TextView tv_name,tv_time;
    private ImageView iv_battery;
    private SeekBar volume_seekbar;
    private ImageView iv_volume;

    private TextView tv_current_position,tv_total_time;
    private SeekBar play_seekbar;

    private IntentFilter intentFilter;
    private BatteryBroadcastReceiver receiver;

    private int streamMaxVolume;
    private int currentVolume;
    private AudioManager audioManager;
    private boolean isMute=false;//是否静音
    private int screenWidth,screenHight;


    private final int MESSAGE_UPDATE_TIME=0;
    private final int MESSAGE_UPDATE_PLAY_PROGRESS=1;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_UPDATE_TIME:
                    updateSystemTime();
                    break;
                case MESSAGE_UPDATE_PLAY_PROGRESS:
                    updatePlayProgress();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.activity_video_player);
        videoView=(VideoView)findViewById(R.id.videoView);
        btn_exit=(ImageView)findViewById(R.id.btn_exit);
        btn_pre=(ImageView)findViewById(R.id.btn_pre);
        btn_play=(ImageView)findViewById(R.id.btn_play);
        btn_next=(ImageView)findViewById(R.id.btn_next);
        btn_fullscreen=(ImageView)findViewById(R.id.btn_fullscreen);

        tv_name=(TextView)findViewById(R.id.tv_name);
        tv_time=(TextView)findViewById(R.id.tv_time);
        iv_battery=(ImageView)findViewById(R.id.iv_battery);

        iv_volume=(ImageView)findViewById(R.id.iv_volume);
        volume_seekbar=(SeekBar)findViewById(R.id.volume_seekbar);

        tv_current_position=(TextView)findViewById(R.id.tv_current_position);
        tv_total_time=(TextView)findViewById(R.id.tv_total_time);
        play_seekbar=(SeekBar)findViewById(R.id.play_seekbar);
    }

    @Override
    protected void initListener() {
        btn_exit.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_fullscreen.setOnClickListener(this);
        volume_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //手动
                    isMute = false;
                    currentVolume = progress;
                    updateVolume();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        iv_volume.setOnClickListener(this);

        play_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void initData() {
        screenWidth=getWindowManager().getDefaultDisplay().getWidth();
        screenHight=getWindowManager().getDefaultDisplay().getHeight();
        VideoItem videoItem=(VideoItem)getIntent().getExtras().getSerializable("videoItem");
        registerBatteryBroadcastReceiver();
        updateSystemTime();
        initVolume();



        if (videoItem!=null){
            videoView.setVideoURI(Uri.parse(videoItem.getPath()));
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                    play_seekbar.setMax(videoView.getDuration());
                    updatePlayProgress();
                }
            });
            tv_name.setText(videoItem.getTitle());
        }

        //videoView.setMediaController(new MediaController(this));

    }

    @Override
    protected void processClick(View view) {
        switch (view.getId()){
            case R.id.btn_play:
                if (videoView.isPlaying()){
                    videoView.pause();
                    handler.removeMessages(MESSAGE_UPDATE_PLAY_PROGRESS);
                }else {
                    videoView.start();
                    handler.sendEmptyMessage(MESSAGE_UPDATE_PLAY_PROGRESS);
                }
                updatePlayBtnBg();
                break;
            case R.id.iv_volume:
                isMute=!isMute;
                updateVolume();
                break;
        }
    }

    //更新播放背景
    private void updatePlayBtnBg(){
        btn_play.setBackgroundResource(videoView.isPlaying()?R.drawable.selector_btn_pause:
                R.drawable.selector_btn_play);
    }

    //更新系统时间
    private void updateSystemTime(){
        tv_time.setText(StringUtil.formatSystemTime());
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIME, 1000);
    }
    //动态注册广播接收器
    private void registerBatteryBroadcastReceiver(){
        intentFilter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        receiver=new BatteryBroadcastReceiver();
        registerReceiver(receiver,intentFilter);
    }

    //广播接收器
    private class BatteryBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int level=intent.getIntExtra("level", 0);
            LogUtil.e(this,"level:"+level);
            updateBatteryImage(level);
        }
    }

    private void updateBatteryImage(int level){
        if (level<=0){
            iv_battery.setBackground(getResources().getDrawable(R.mipmap.ic_battery_0));
        }if (level>=0 && level<10){
            iv_battery.setBackground(getResources().getDrawable(R.mipmap.ic_battery_10));
        }if (level>=10 && level<20){
            iv_battery.setBackground(getResources().getDrawable(R.mipmap.ic_battery_20));
        }if (level>=20 && level<40){
            iv_battery.setBackground(getResources().getDrawable(R.mipmap.ic_battery_40));
        }if (level>=40 && level<60){
            iv_battery.setBackground(getResources().getDrawable(R.mipmap.ic_battery_60));
        }if (level>=60 && level<80){
            iv_battery.setBackground(getResources().getDrawable(R.mipmap.ic_battery_80));
        }else if(level>=80 && level<=100) {
            iv_battery.setBackground(getResources().getDrawable(R.mipmap.ic_battery_100));
        }
    }

    //初始化音量
    private void initVolume(){
        audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume_seekbar.setMax(streamMaxVolume);
        volume_seekbar.setProgress(currentVolume);
    }

    private void updateVolume(){
        if (isMute){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            volume_seekbar.setProgress(0);
        }else {
            //第三参数为1时显示一个view指示
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,0);
            volume_seekbar.setProgress(currentVolume);
        }

    }

    private float downY;
    private float distance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY=event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY=event.getY();
                distance=moveY-downY;
                if (Math.abs(distance)<20)break;

                int totaldistance=Math.min(screenHight,screenWidth);
                float movePrecent=Math.abs(distance / totaldistance);
                float movevolume=movePrecent*streamMaxVolume;//值很小
                LogUtil.e(this,movevolume+"");

                if (distance>0){
                    //减小
                    currentVolume-=1;
                }else {
                    //增加
                    currentVolume+=1;
                }
                updateVolume();

                downY=moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void updatePlayProgress(){
        play_seekbar.setProgress(videoView.getCurrentPosition());
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PLAY_PROGRESS,1000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //优化
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(receiver);
    }
}
