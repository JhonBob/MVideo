package com.bob.mvideo.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bob.mvideo.R;
import com.bob.mvideo.adapter.MainPagerAdapter;
import com.bob.mvideo.fragment.AudioListFragment;
import com.bob.mvideo.fragment.NetVideoFragment;
import com.bob.mvideo.fragment.VideoListFragment;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private TextView tab_video;
    private TextView tab_audio;
    private TextView tab_net_video;
    private View indicate_line;
    private ViewPager viewPager;

    private MainPagerAdapter adapter;
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private int indicateLineWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }
    public void initView(){
        tab_video=(TextView)findViewById(R.id.tab_video);
        tab_audio=(TextView)findViewById(R.id.tab_audio);
        indicate_line=findViewById(R.id.indicate_line);
        tab_net_video=(TextView)findViewById(R.id.tab_net_video);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
    }

    public void initListener(){
        tab_video.setOnClickListener(this);
        tab_audio.setOnClickListener(this);
        tab_net_video.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int targetPosition=position*indicateLineWidth+positionOffsetPixels/fragments.size();
                ViewPropertyAnimator.animate(indicate_line).translationX(targetPosition).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                lightAndScaleTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //计算指示线的宽度
    private void caculateIndicateLineWidth(){
        int screenWidth=getWindowManager().getDefaultDisplay().getWidth();
        indicateLineWidth=screenWidth/fragments.size();
        indicate_line.getLayoutParams().width=indicateLineWidth;
        indicate_line.requestLayout();
    }

    public void initData(){
        fragments.add(new VideoListFragment());
        fragments.add(new AudioListFragment());
        fragments.add(new NetVideoFragment());
        caculateIndicateLineWidth();
        adapter=new MainPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);

        lightAndScaleTitle();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_video:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_audio:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tab_net_video:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    //高亮缩放
    private void lightAndScaleTitle(){
        int currentPage=viewPager.getCurrentItem();
        tab_video.setTextColor(currentPage==0?getResources().getColor(R.color.indicate_line):
        getResources().getColor(R.color.gray_white));
        tab_audio.setTextColor(currentPage==1?getResources().getColor(R.color.indicate_line):
                getResources().getColor(R.color.gray_white));
        ViewPropertyAnimator.animate(tab_video).scaleX(currentPage==0?1.2f:1.0f).setDuration(200);
        ViewPropertyAnimator.animate(tab_video).scaleY(currentPage==0?1.2f:1.0f).setDuration(200);
        ViewPropertyAnimator.animate(tab_audio).scaleX(currentPage==1?1.2f:1.0f).setDuration(200);
        ViewPropertyAnimator.animate(tab_audio).scaleY(currentPage==1?1.2f:1.0f).setDuration(200);

    }
}
