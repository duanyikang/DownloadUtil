package com.guoguoquan.mydownload.View.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.guoguoquan.mydownload.Bean.FileInfo;
import com.guoguoquan.mydownload.View.Uitl.DownloadTask;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author 小段果果
 * @time 2016/8/15  11:34
 * @E-mail duanyikang@mumayi.com
 */

public class MyDownloadService extends Service {
    public static final String ACTION_START="ACTION_START";
    public static final String ACTION_STOP="ACTION";
    private static final int MSG_INT = 0;
    public static final String DownloadPath= Environment.getExternalStorageDirectory().getAbsolutePath() +"/AA/";


    public  DownloadTask mTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(ACTION_START.equals(intent.getAction()))
        {
            FileInfo fileInfo=(FileInfo)intent.getParcelableExtra("fileInfo");
            new InitThread(fileInfo).start();
        }
        else if (ACTION_STOP.equals(intent.getAction()))
        {
            FileInfo fileInfo=(FileInfo)intent.getParcelableExtra("fileInfo");
            if (mTask != null) {
                mTask.mIsPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MSG_INT:
                    FileInfo fileInfo=(FileInfo)msg.obj;
                    mTask = new DownloadTask(MyDownloadService.this, fileInfo,4);
                    mTask.download();
                    break;
            }
        }
    };

    class InitThread extends Thread
    {
        private FileInfo mFileInfo=null;

        public InitThread(FileInfo mFileInfo)
        {
            super();
            this.mFileInfo=mFileInfo;
        }


        @Override
        public void run() {
            super.run();

            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            try {
                URL url=new URL(mFileInfo.getUrl());
                conn=(HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(5*1000);
                conn.setRequestMethod("GET");

                int code=conn.getResponseCode();
                int length=-1;
                if (code==HttpURLConnection.HTTP_OK)
                {
                    length=conn.getContentLength();
                }
                if(length<=0)
                {
                    return;
                }
                File dir=new File(DownloadPath);
                if(!dir.exists())
                {
                    dir.mkdir();
                }

                File file = new File(dir, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.setLength(length);
                // 設置文件長度
                mFileInfo.setLength(length);
                // 將FileInfo對象傳遞給Handler
                Message msg = Message.obtain();
                msg.obj = mFileInfo;
                msg.what = MSG_INT;
                mHandler.sendMessage(msg);
                msg.setTarget(mHandler);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

