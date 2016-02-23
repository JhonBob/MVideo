package com.bob.mvideo.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bob.mvideo.R;
import com.bob.mvideo.activity.VideoPlayerActivity;
import com.bob.mvideo.adapter.VideoListAdapter;
import com.bob.mvideo.base.BaseFragment;
import com.bob.mvideo.bean.VideoItem;
import com.bob.mvideo.db.SimpleQueryHandler;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/3.
 */
public class VideoListFragment extends BaseFragment {

    private ListView listView;
    private SimpleQueryHandler queryHandler;
    private VideoListAdapter adapter;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_video_list,null);
        listView=(ListView)view.findViewById(R.id.listView);
        return view;
    }

    @Override
    protected void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor= (Cursor) adapter.getItem(position);
                ArrayList<VideoItem> videoList=cursorToList(cursor);
                Bundle bundle=new Bundle();
                bundle.putInt("currentPosition", position);
                bundle.putSerializable("videoList",videoList);
                enterActivity(VideoPlayerActivity.class,bundle);
            }
        });
    }

    @Override
    protected void initData() {
        adapter=new VideoListAdapter(getActivity(),null);
        listView.setAdapter(adapter);
        queryHandler=new SimpleQueryHandler(getActivity().getContentResolver());
        String[] projection={MediaStore.Video.Media._ID,MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.DURATION, MediaStore.Video.Media.TITLE,
        MediaStore.Video.Media.DATA};
//        Cursor cursor=getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//        projection,null,null,null);
//        CursorUtil.printCursor(cursor);
        queryHandler.startQuery(0, adapter, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
    }

    @Override
    protected void processClick(View view) {

    }

    //解析Cursor
    private ArrayList<VideoItem> cursorToList(Cursor cursor){
        cursor.moveToPosition(-1);//将cursor移动到最初位置
        ArrayList<VideoItem> list=new ArrayList<>();
        //遍历
        while (cursor.moveToNext()){
            list.add(VideoItem.fromCursor(cursor));
        }
        return list;
    }
}
