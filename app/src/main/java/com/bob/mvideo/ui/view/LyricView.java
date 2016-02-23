package com.bob.mvideo.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bob.mvideo.bean.Lyric;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/18.
 */
public class LyricView extends TextView {

    private Paint mPaint;
    private int COLOR_LIGHT= Color.GREEN;
    private int COLOR_DEFAULT= Color.WHITE;
    private int viewWeight,viewHeight;

    private ArrayList<Lyric> lyricList;
    private int lightLyricIndex=0;
    //每行歌词行高
    private final int LYRIC_ROW_HEIGHT=40;

    private final int SIZE_LIGHT=30;//高亮歌词大小
    private final int SIZE_DEFAULT=25;//非高亮歌词大小

    private long currentAudioPosition;//挡前音乐的播放位置
    private long totalDuration;//歌曲的总时间

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWeight=w;
        viewHeight=h;
    }

    private void init(){
        mPaint=new Paint();
        mPaint.setTextSize(SIZE_DEFAULT);
        mPaint.setColor(COLOR_LIGHT);
        mPaint.setAntiAlias(true);
        //lyricList=new ArrayList<>();
//        for (int i=0;i<50;i++){
//            lyricList.add(new Lyric("我是歌词-"+i,i*2000));
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyricList==null){
            String text="没有歌词";
            float y=viewHeight/2-getTextHeight(text)/2;
            drawCenterHorizontalText(canvas,text,y,true);
        }else {
            drawLyric(canvas);
        }
    }

    //绘制歌词
    private void drawLyric(Canvas canvas){

        Lyric lightLyric=lyricList.get(lightLyricIndex);
        //平滑移动歌词
        //a.歌词总的总时间，下一行的startPoint减去我的startPoint
        long lightLyricDuration;
        if (lightLyricIndex==(lyricList.size()-1)){
           lightLyricDuration=totalDuration-lightLyric.getStartPoint();
        }else {
            lightLyricDuration=lyricList.get(lightLyricIndex+1).getStartPoint()-lightLyric.getStartPoint();
        }
        //b.当前已经唱了的秒数占总时间的百分比currentPosition-startPoint
        float percent=(float)(currentAudioPosition-lightLyric.getStartPoint())/lightLyricDuration;
        //c.计算应当移动的距离percent*LYRIC_ROW_HEIGHT
        float dy=percent*LYRIC_ROW_HEIGHT;
        canvas.translate(0,-dy);


        //1.先画高亮行，作为歌词的位置参照
        float lightLyricY=viewHeight/2+getTextHeight(lightLyric.getContent())/2;
        drawCenterHorizontalText(canvas,lightLyric.getContent(),lightLyricY,true);
        //2.高亮行之前
        for (int i=0;i<lightLyricIndex;i++){
            Lyric currentLyric=lyricList.get(i);
            float currentLyricY=lightLyricY-(lightLyricIndex-i)*LYRIC_ROW_HEIGHT;
            drawCenterHorizontalText(canvas,currentLyric.getContent(),currentLyricY,false);
        }
        //3.高亮行之后
        for (int i=lightLyricIndex+1;i<lyricList.size();i++){
            Lyric currentLyric=lyricList.get(i);
            float currentLyricY=lightLyricY+(i-lightLyricIndex)*LYRIC_ROW_HEIGHT;
            drawCenterHorizontalText(canvas,currentLyric.getContent(),currentLyricY,false);
        }
    }

    //获取文本的高度
    private int getTextHeight(String text) {
        Rect bounds=new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    //绘制水平居中的文本
    private void drawCenterHorizontalText(Canvas canvas,String text,float y,boolean isLight ){
        mPaint.setColor(isLight?COLOR_LIGHT:COLOR_DEFAULT);
        mPaint.setTextSize(isLight?SIZE_LIGHT:SIZE_DEFAULT);
        float x=viewWeight/2-mPaint.measureText(text)/2;
        canvas.drawText(text, x, y, mPaint);
    }

    //滚动歌词
    public void roll(long currentAudioPosition,long totalDuration){
        this.currentAudioPosition=currentAudioPosition;
        this.totalDuration=totalDuration;
        //1.跟据音乐的Position计算出lightLyricIndex
        calculateLightLyricIndex();
        //2.拿到新的高亮索引后跟新VIEW
        invalidate();
    }

    //计算歌词索引startPoint<position<下一行的startPoint
    private void calculateLightLyricIndex(){
        for (int i=0;i<lyricList.size();i++){
            long startPoint=lyricList.get(i).getStartPoint();
            if (i==lyricList.size()-1)
            {//如果是最后一行
                if (currentAudioPosition>startPoint){
                    lightLyricIndex=i;
                }
            }else {//不是最后一行
                        long nextStartPoint=lyricList.get(i+1).getStartPoint();
                        if (currentAudioPosition>=startPoint && currentAudioPosition<nextStartPoint){
                            lightLyricIndex=i;
                        }
            }
        }
    }

    public void setLyricList(ArrayList<Lyric> lyrics){
        this.lyricList=lyrics;
    }
}
