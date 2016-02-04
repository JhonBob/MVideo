package com.bob.mvideo.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

import com.bob.mvideo.util.CursorUtil;

/**
 * Created by Administrator on 2016/2/3.
 */
public class SimpleQueryHandler extends AsyncQueryHandler{
    public SimpleQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        CursorUtil.printCursor(cursor);
        if (cookie!=null && cookie instanceof CursorAdapter){
            CursorAdapter adapter= (CursorAdapter) cookie;
            adapter.changeCursor(cursor);//相当于通知适配
        }
        super.onQueryComplete(token, cookie, cursor);
    }
}
