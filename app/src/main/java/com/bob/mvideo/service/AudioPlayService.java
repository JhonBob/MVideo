package com.bob.mvideo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.bob.mvideo.R;
import com.bob.mvideo.activity.AudioPlayerActivity;
import com.bob.mvideo.bean.AudioItem;
import com.bob.mvideo.util.LogUtil;
import com.bob.mvideo.util.StringUtil;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.logging.Level;

public class AudioPlayService extends Service {
    private AudioServiceBinder audioServiceBinder;
    private MediaPlayer mediaPlayer;
    private ArrayList<AudioItem> audioList;
    private int currentPosition;
    private SharedPreferences sp;

    //广播动作
    public static final String ACTION_MEDIA_PREPARED="ACTION_MEDIA_PREPARED";
    public static final String ACTION_MEDIA_COMPLETION="ACTION_MEDIA_COMPLETION";

    //播放模式
    public static final int MODE_ORDER=0;
    public static final int MODE_SINGLE_REPEAT=1;
    public static final int MODE_ALL_REPEAT=2;
    public static int currentPlayMode=MODE_ORDER;

    private static final int VIEW_PRE = 1;//通知栏的上一个
    private static final int VIEW_NEXT = 2;//通知栏的下一个
    private static final int VIEW_CONTAINER = 3;//通知栏的整体布局

    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
       return audioServiceBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioServiceBinder=new AudioServiceBinder();
        sp=getSharedPreferences("playmode.cfg",MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null && intent.getExtras()!=null){
            boolean isFromNotification = intent.getBooleanExtra("isFromNotification", false);
            if(isFromNotification){
                int viewAction = intent.getIntExtra("view_action", -1);
                switch (viewAction) {
                    case VIEW_PRE:
                        audioServiceBinder.playPre();
                        break;
                    case VIEW_NEXT:
                        audioServiceBinder.playNext();
                        break;
                    case VIEW_CONTAINER:
                        notifyPrepared();
                        break;
                }
            }else {
                audioList = (ArrayList<AudioItem>) intent.getExtras().getSerializable("audioList");
                currentPosition = intent.getExtras().getInt("currentPosition");
                audioServiceBinder.openAudio();
            }
        }
        currentPlayMode = getPlayMode();
        return START_STICKY;//当服务被杀死后，会自动重启
    }

    //代理类
    public class AudioServiceBinder extends Binder {
        public void openAudio(){
            //LogUtil.e(this,"开始播放");
            System.out.println("开始播放");
            if(mediaPlayer!=null){
                mediaPlayer.release();
                mediaPlayer=null;
            }
            mediaPlayer=new MediaPlayer();
            try {
                mediaPlayer.setOnPreparedListener(mPreparedListener);
                mediaPlayer.setOnCompletionListener(onCompletionListener);
                mediaPlayer.setDataSource(audioList.get(currentPosition).getPath());
                mediaPlayer.prepareAsync();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        public boolean isPlaying(){
            return mediaPlayer!=null&&mediaPlayer.isPlaying();
        }

        public void pause(){
            if (mediaPlayer!=null){
                mediaPlayer.pause();
            }
           // notificationManager.cancel(1);
            stopForeground(true);
        }

        public void start(){
            if (mediaPlayer!=null){
                mediaPlayer.start();
            }
            sendNotification();
        }

        //获取当前媒体播放进度
        public long getCurrentPosition(){
            return mediaPlayer!=null?mediaPlayer.getCurrentPosition():0;
        }

        //获取媒体总进度
        public long getDuration(){
            return mediaPlayer!=null?mediaPlayer.getDuration():0;
        }

        //seekBar拖动改变媒体进度
        public void seekTo(int position){
            if (mediaPlayer!=null){
                mediaPlayer.seekTo(position);
            }
        }

        //下一首
        public void playNext(){
           if (currentPosition<(audioList.size()-1)){
               currentPosition++;
               openAudio();
           }

        }

        //所有循环
        public void playAllRepeat(){
            currentPosition++;
            if (currentPosition>(audioList.size()-1)){
                currentPosition=0;
            }
            openAudio();
        }

        //上一首
        public void playPre(){
            currentPosition--;
            if (currentPosition<0){
                currentPosition=audioList.size()-1;
            }
            openAudio();
        }
        //切换播放模式
        public void switchPlayMode(){
            if(currentPlayMode==MODE_ORDER){
                currentPlayMode=MODE_SINGLE_REPEAT;
            }else if (currentPlayMode==MODE_SINGLE_REPEAT){
                currentPlayMode=MODE_ALL_REPEAT;
            }else if (currentPlayMode==MODE_ALL_REPEAT){
                currentPlayMode=MODE_ORDER;
            }
            savePlayMode();
        }
        //获取当前播放模式
        public int getPlayMode(){
            return currentPlayMode;
        }

    }

    private MediaPlayer.OnPreparedListener mPreparedListener=new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
           audioServiceBinder.start();
            notifyPrepared();
        }
    };

    private MediaPlayer.OnCompletionListener  onCompletionListener=new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            notifyCompletion();
            autoPlayByMode();
        }
    };

    //通知准备完成
    private void notifyPrepared(){
        Intent intent=new Intent(ACTION_MEDIA_PREPARED);
        Bundle bundle=new Bundle();
        bundle.putSerializable("audioItem",audioList.get(currentPosition));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    //通知播放完成
    private void notifyCompletion(){
        Intent intent=new Intent(ACTION_MEDIA_COMPLETION);
        Bundle bundle=new Bundle();
        bundle.putSerializable("audioItem",audioList.get(currentPosition));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void autoPlayByMode(){
        switch (currentPlayMode){
            case MODE_ORDER:
                audioServiceBinder.playNext();
                break;
            case MODE_SINGLE_REPEAT:
                audioServiceBinder.openAudio();
                break;
            case MODE_ALL_REPEAT:
                audioServiceBinder.playAllRepeat();
                break;
        }
    }

    private void savePlayMode(){
        sp.edit().putInt("playMode",currentPlayMode).apply();
    }

    private int getPlayMode(){
        return sp.getInt("playMode",currentPlayMode);
    }

    //发送通知
    private void sendNotification(){
        AudioItem audioItem = audioList.get(currentPosition);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(true)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setTicker("正在播放:"+ StringUtil.formatAudioName(audioItem.getTitle()))
                .setWhen(System.currentTimeMillis())
                .setContent(getRemoteViews());

        //notificationManager.notify(1, builder.build());
        startForeground(1,builder.build());
    }

    //远程视图
    private RemoteViews getRemoteViews(){
        AudioItem audioItem = audioList.get(currentPosition);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tv_song_name, StringUtil.formatAudioName(audioItem.getTitle()));
        remoteViews.setTextViewText(R.id.tv_artist_name, audioItem.getArtist());
        //上一首
        Intent preIntent = createNotificationIntent(VIEW_PRE);
        PendingIntent preContentIntent = PendingIntent.getService(this, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_pre, preContentIntent);
        //下一首
        Intent nextIntent = createNotificationIntent(VIEW_NEXT);
        PendingIntent nextContentIntent = PendingIntent.getService(this, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next, nextContentIntent);

        Intent containerIntent = createNotificationIntent(VIEW_CONTAINER);
        PendingIntent containerContentIntent = PendingIntent.getActivity(this, 2, containerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_container, containerContentIntent);


        return remoteViews;
    }

    //创建INTENT
    private Intent createNotificationIntent(int viewAction){
        Intent intent = new Intent(AudioPlayService.this,viewAction==VIEW_CONTAINER?
                AudioPlayerActivity.class:AudioPlayService.class);
        intent.putExtra("isFromNotification", true);
        intent.putExtra("view_action", viewAction);
        return intent;
    }

}
