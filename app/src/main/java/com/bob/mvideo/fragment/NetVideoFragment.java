package com.bob.mvideo.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.bob.mvideo.R;
import com.bob.mvideo.activity.VideoPlayerActivity;
import com.bob.mvideo.base.BaseFragment;

/**
 * Created by Administrator on 2016/3/1.
 */
public class NetVideoFragment extends BaseFragment {


    private Button play_net;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.net_video,null);
         play_net=(Button)view.findViewById(R.id.play_net);
        return view;
    }

    @Override
    protected void initListener() {
        play_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), VideoPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse("http://192.168.56.1:8080/oppo.mp4"),"video/*");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void processClick(View view) {

    }
}
