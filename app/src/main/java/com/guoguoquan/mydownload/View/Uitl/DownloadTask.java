package com.guoguoquan.mydownload.View.Uitl;

import android.content.Context;

import com.guoguoquan.mydownload.Bean.FileInfo;
import com.guoguoquan.mydownload.Bean.ThreadInfo;
import com.guoguoquan.mydownload.Model.DataBase.ThreadDao;
import com.guoguoquan.mydownload.Model.DataBase.ThreadDaoImpl;
import com.guoguoquan.mydownload.View.Service.MyDownloadService;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author 小段果果
 * @time 2016/8/15  13:42
 * @E-mail duanyikang@mumayi.com
 */

public class DownloadTask {

    private Context mContext = null;
    private FileInfo mFileInfo = null;
    private ThreadDao mDao = null;
    private int mFinished = 0;
    public boolean mIsPause = false;

    public DownloadTask(Context mContext, FileInfo fileInfo) {
        super();
        this.mContext = mContext;
        this.mFileInfo = fileInfo;
        this.mDao = new ThreadDaoImpl(mContext);
    }

    public void download() {
        List<ThreadInfo> list = mDao.queryThreads(mFileInfo.getUrl());
        ThreadInfo mThreadInfo = null;
        if (list.size() == 0) {
            mThreadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
        } else {
            mThreadInfo = list.get(0);
        }
        new DownloadThread(mThreadInfo).start();
    }

    class DownloadThread extends Thread {
        private ThreadInfo mThreadInfo;

        public DownloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        @Override
        public void run() {
            if (mDao.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
                mDao.insertThread(mThreadInfo);
            }

            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream is = null;

            try {
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());

                File file = new File(MyDownloadService.DownloadPath, mFileInfo.getFileName());
                raf=new RandomAccessFile(file,"rwd");
                raf.seek(start);

                int code=conn.getResponseCode();
                if (code==HttpURLConnection.HTTP_PARTIAL)
                {
                    is=conn.getInputStream();
                    byte[] bt=new byte[1024];
                    int len=-1;
                    while ((len=is.read(bt))!=-1)
                    {
                        System.out.println("下载中。。。。。。。。。。。");
                        raf.write(bt, 0, len);
                        mFinished += len;
                        if (mIsPause) {
                            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);
                            System.out.println("下载暂停。。。。。。。。。。。");
                            return;
                        }
                    }
                    System.out.println("下载完成。。。。。。。。。。。");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}

