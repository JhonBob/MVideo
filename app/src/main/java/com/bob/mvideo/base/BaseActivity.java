package com.bob.mvideo.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Administrator on 2016/2/3.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
        initData();
    }

    protected abstract void initView();
    protected abstract void initListener();
    protected abstract void initData();

    protected abstract void processClick(View view);

    @Override
    public void onClick(View v) {
        processClick(v);
    }
}
