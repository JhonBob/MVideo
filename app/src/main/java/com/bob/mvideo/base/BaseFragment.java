package com.bob.mvideo.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/2/3.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=initView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        initData();
    }

    protected  abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void initListener();
    protected abstract void initData();
    protected abstract void processClick(View view);


    @Override
    public void onClick(View v) {
        processClick(v);
    }

    protected void enterActivity(Class<?>targetActivity){
        Intent intent=new Intent(getActivity(),targetActivity);
        startActivity(intent);
    }

    protected void enterActivity(Class<?>targetActivity,Bundle bundle){
        Intent intent=new Intent(getActivity(),targetActivity);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
