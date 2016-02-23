package com.bob.mvideo.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bob.mvideo.R;
import com.bob.mvideo.base.BaseActivity;
import com.bob.mvideo.bean.AudioItem;
import com.bob.mvideo.bean.Lyric;
import com.bob.mvideo.service.AudioPlayService;
import com.bob.mvideo.service.AudioPlayService.AudioServiceBinder;
import com.bob.mvideo.ui.view.LyricView;
import com.bob.mvideo.util.LyricLoader;
import com.bob.mvideo.util.LyricParser;
import com.bob.mvideo.util.StringUtil;


import java.io.File;
import java.util.ArrayList;

public class AudioPlayerActivity extends BaseActivity {

    private ImageView iv_anim;
    private AudioServiceConnection serviceConnection;
    private AudioServiceBinder audioServiceBinder;
    private AudioBroadcastReceiver audioBroadcastReceiver;

    private ImageView btn_mplay,btn_back,btn_pre,btn_next,play_mode,btn_lyrics;
    private TextView tv_title,tv_artist,tv_time;
    private SeekBar seekBar;
    private LyricView lyricView;
    private ArrayList<Lyric> list;//歌词集合
    private File lyricFile;//歌词文件

    private static final int MESSAGE_UPDATE_PROGRESS=0;
    private static final int MESSAGE_UPDATE_LYRIC=1;//更新歌词

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
          switch (msg.what){
              case MESSAGE_UPDATE_PROGRESS:
                  updatePlayProgress();
                  break;
              case MESSAGE_UPDATE_LYRIC:
                      updateLyric();
                  break;
              default:
                  break;
          }
        }
    };

    //更新歌词
    private void updateLyric(){
        lyricView.roll(audioServiceBinder.getCurrentPosition(),audioServiceBinder.getDuration());
        handler.sendEmptyMessage(MESSAGE_UPDATE_LYRIC);
    }

    //更新进度
    private void updatePlayProgress(){
        seekBar.setProgress((int) audioServiceBinder.getCurrentPosition());
        String currentTime=StringUtil.formatVideoDuration(audioServiceBinder.getCurrentPosition());
        tv_time.setText(currentTime+"/"+StringUtil.formatVideoDuration(audioServiceBinder.getDuration()));
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS,1000);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_audio_player);
        iv_anim=(ImageView)findViewById(R.id.iv_anim);
        AnimationDrawable animationDrawable=(AnimationDrawable)iv_anim.getBackground();
        animationDrawable.start();

        btn_mplay=(ImageView)findViewById(R.id.btn_mplay);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_artist=(TextView)findViewById(R.id.tv_artist);
        btn_back=(ImageView)findViewById(R.id.btn_back);
        tv_time=(TextView)findViewById(R.id.tv_time);
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        btn_pre=(ImageView)findViewById(R.id.btn_pre);
        btn_next=(ImageView)findViewById(R.id.btn_next);
        play_mode=(ImageView)findViewById(R.id.play_mode);
        btn_lyrics=(ImageView)findViewById(R.id.btn_lyrics);

        lyricView=(LyricView)findViewById(R.id.lyricView);
    }

    @Override
    protected void initListener() {
        btn_back.setOnClickListener(this);
        btn_mplay.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        play_mode.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioServiceBinder.seekTo(progress);
                    tv_time.setText(StringUtil.formatVideoDuration(progress) + "/" + StringUtil.formatVideoDuration(audioServiceBinder.getDuration()));
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

    private int currentPosition;
    private  ArrayList<AudioItem> audioList;


    @Override
    protected void initData() {
        registerAudioBroadcastReceiver();
        Intent intent=new Intent(this, AudioPlayService.class);
        boolean isFromNotification=getIntent().getBooleanExtra("isFromNotification",false);
        if (isFromNotification){
            //来自通知
            intent.putExtra("isFromNotification",isFromNotification);
            intent.putExtra("view_action", getIntent().getIntExtra("view_action",-1));
        }else {
            //来自应用列表
            currentPosition=getIntent().getExtras().getInt("currentPosition");
            audioList= (ArrayList<AudioItem>) getIntent().getExtras().getSerializable("audioList");
            Bundle bundle=new Bundle();
            bundle.putInt("currentPosition",currentPosition);
            bundle.putSerializable("audioList", audioList);
            intent.putExtras(bundle);
        }
        serviceConnection=new AudioServiceConnection();
        startService(intent);//传递数据用
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }


    //注册广播接收器
    private void registerAudioBroadcastReceiver(){
        audioBroadcastReceiver=new AudioBroadcastReceiver();
        IntentFilter filter=new IntentFilter(AudioPlayService.ACTION_MEDIA_PREPARED);
        filter.addAction(AudioPlayService.ACTION_MEDIA_COMPLETION);
        registerReceiver(audioBroadcastReceiver, filter);
    }


    @Override
    protected void processClick(View view) {
        switch (view.getId()){
            case R.id.btn_mplay:
                //System.out.println("开始播放");
                updatePlayBtnBg();
                if (audioServiceBinder.isPlaying()){
                    audioServiceBinder.pause();
                }else {
                    audioServiceBinder.start();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                audioServiceBinder.playAllRepeat();
                break;
            case R.id.btn_pre:
                audioServiceBinder.playPre();
                break;
            case R.id.play_mode:
                audioServiceBinder.switchPlayMode();
                updatePlayModeBtnBg();
                break;
        }

    }

    //更新模式
    private void updatePlayModeBtnBg(){
        switch (audioServiceBinder.getPlayMode()){
            case AudioPlayService.MODE_ORDER:
                play_mode.setBackgroundResource(R.drawable.selector_audio_mode_normal);
                break;
            case AudioPlayService.MODE_SINGLE_REPEAT:
                play_mode.setBackgroundResource(R.drawable.selector_audio_mode_single_repeat);
                break;
            case AudioPlayService.MODE_ALL_REPEAT:
                play_mode.setBackgroundResource(R.drawable.selector_audio_mode_all_repeat);
                break;
        }
    }

    //更新播放背景
    private void updatePlayBtnBg(){
        btn_mplay.setBackgroundResource(audioServiceBinder.isPlaying()?R.drawable.selector_btn_audio_play:
                R.drawable.selector_btn_audio_pause);
    }

    //音频广播接收器
    class AudioBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            AudioItem audioItem= (AudioItem) intent.getExtras().getSerializable("audioItem");
            switch (intent.getAction()){
                case AudioPlayService.ACTION_MEDIA_PREPARED:
                    seekBar.setMax((int) audioItem.getDuration());
                    tv_time.setText("00:00/" + StringUtil.formatVideoDuration(audioItem.getDuration()));
                    tv_title.setText(StringUtil.formatAudioName(audioItem.getTitle()));
                    tv_artist.setText(audioItem.getArtist());
                    btn_mplay.setBackgroundResource(R.drawable.selector_btn_audio_pause);
                    updatePlayModeBtnBg();
                    updatePlayProgress();//开始更新

                    lyricFile= LyricLoader.loadLyricFile(audioItem.getTitle());
                    if (lyricFile!=null){
                        list=LyricParser.parseLyricFromFile(lyricFile);
                        lyricView.setLyricList(list);
                        updateLyric();//歌词
                    }



                    break;
                case AudioPlayService.ACTION_MEDIA_COMPLETION:
                    seekBar.setProgress((int) audioItem.getDuration());
                    tv_time.setText(StringUtil.formatVideoDuration(audioItem.getDuration()) + "/" +
                            StringUtil.formatVideoDuration(audioItem.getDuration()));
                    btn_mplay.setBackgroundResource(R.drawable.selector_btn_audio_play);
                    break;
            }
        }
    }

    class AudioServiceConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioServiceBinder= (AudioServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
