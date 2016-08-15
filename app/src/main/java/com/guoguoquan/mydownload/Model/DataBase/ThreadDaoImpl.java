package com.guoguoquan.mydownload.Model.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guoguoquan.mydownload.Bean.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 小段果果
 * @time 2016/8/15  10:50
 * @E-mail duanyikang@mumayi.com
 */

public class ThreadDaoImpl implements ThreadDao {

    private DbHelper dbHelper = null;

    public ThreadDaoImpl(Context context) {
        super();
        this.dbHelper = new DbHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo info) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("thread_id", info.getId());
        values.put("url", info.getUrl());
        values.put("start", info.getStart());
        values.put("end", info.getEnd());
        values.put("finished", info.getFinished());
        sqLiteDatabase.insert("thread_info", null, values);
        sqLiteDatabase.close();
    }

    @Override
    public void deleteThread(String url, int thread_id) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete("thread_info", "url = ? and thread_id = ?", new String[]{url, thread_id + ""});
        sqLiteDatabase.close();
    }

    @Override
    public void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("update thread_info set finished = ? where url = ? and thread_id = ?",
                new Object[]{finished,url,thread_id});
        sqLiteDatabase.close();
    }

    @Override
    public List<ThreadInfo> queryThreads(String url) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<ThreadInfo> list = new ArrayList<ThreadInfo>();

        Cursor cursor = db.query("thread_info", null, "url = ?", new String[] { url }, null, null, null);
        while (cursor.moveToNext()) {
            ThreadInfo thread = new ThreadInfo();
            thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(thread);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int threadId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("thread_info", null, "url = ? and thread_id = ?", new String[] { url, String.valueOf(threadId) + "" }, null, null, null);
        boolean exists = cursor.moveToNext();

        db.close();

        return exists;
    }
}

