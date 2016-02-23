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
import com.bob.mvideo.activity.AudioPlayerActivity;
import com.bob.mvideo.adapter.AudioListAdapter;
import com.bob.mvideo.base.BaseFragment;
import com.bob.mvideo.bean.AudioItem;
import com.bob.mvideo.db.SimpleQueryHandler;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/3.
 */
public class AudioListFragment extends BaseFragment {
    private ListView listView;
    private SimpleQueryHandler queryHandler;
    private AudioListAdapter adapter;
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_audio_list,null);
        listView=(ListView)view.findViewById(R.id.listView);
        return view;
    }

    @Override
    protected void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor= (Cursor) adapter.getItem(position);
                Bundle bundle=new Bundle();
                bundle.putInt("currentPosition",position);
                bundle.putSerializable("audioList",cursorToList(cursor));
                enterActivity(AudioPlayerActivity.class, bundle);
            }
        });
    }

    @Override
    protected void initData() {
        adapter=new AudioListAdapter(getActivity(),null);
        listView.setAdapter(adapter);
        queryHandler=new SimpleQueryHandler(getActivity().getContentResolver());
        String[] projection={MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,};
        queryHandler.startQuery(0, adapter, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
    }

    //将游标中的数据放入集合
    private ArrayList<AudioItem> cursorToList(Cursor cursor){
        ArrayList<AudioItem> list=new ArrayList<>();
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            list.add(AudioItem.fromCursor(cursor));
        }

        return list;
    }

    @Override
    protected void processClick(View view) {

    }
}
