package com.guoguoquan.mydownload.View.Uitl;

import android.content.Context;

import com.guoguoquan.mydownload.Bean.FileInfo;
import com.guoguoquan.mydownload.Bean.ThreadInfo;
import com.guoguoquan.mydownload.Model.DataBase.ThreadDao;
import com.guoguoquan.mydownload.Model.DataBase.ThreadDaoImpl;
import com.guoguoquan.mydownload.View.Service.MyDownloadService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 小段果果
 * @time 2016/8/15  13:42
 * @E-mail duanyikang@mumayi.com
 */

public class DownloadTask {

    private Context mComtext = null;
    private FileInfo mFileInfo = null;
    private ThreadDao mDao = null;
    private int mFinished = 0;
    private int mThreadCount = 1;
    public boolean mIsPause = false;
    private List<DownloadThread> mThreadlist = null;
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    public DownloadTask(Context comtext, FileInfo fileInfo, int threadCount) {
        super();
        this.mThreadCount = threadCount;
        this.mComtext = comtext;
        this.mFileInfo = fileInfo;
        this.mDao = new ThreadDaoImpl(mComtext);
    }

    public void download() {
        List<ThreadInfo> list = mDao.queryThreads(mFileInfo.getUrl());
        if (list.size() == 0) {
            int length = mFileInfo.getLength();
            int block = length / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                // 划分每个线程开始下载和结束下载的位置
                int start = i * block;
                int end = (i + 1) * block - 1;
                if (i == mThreadCount - 1) {
                    end = length - 1;
                }
                ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), start, end, 0);
                list.add(threadInfo);
            }
        }
        mThreadlist = new ArrayList<DownloadThread>();
        for (ThreadInfo info : list) {
            DownloadThread thread = new DownloadThread(info);

            DownloadTask.sExecutorService.execute(thread);
            mThreadlist.add(thread);
            mDao.insertThread(info);
        }
    }

    public synchronized void checkAllFinished() {
        boolean allFinished = true;
        for (DownloadThread thread : mThreadlist) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished == true) {
            mDao.deleteThread(mFileInfo.getUrl(),mFileInfo.getId());
            System.out.println("下载完成");
        }
    }

    class DownloadThread extends Thread {
        private ThreadInfo threadInfo = null;
        public boolean isFinished = false;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");

                int start = threadInfo.getStart() + threadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                File file = new File(MyDownloadService.DownloadPath, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                mFinished += threadInfo.getFinished();

                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_PARTIAL) {
                    is = conn.getInputStream();
                    byte[] bt = new byte[1024];
                    int len = -1;
                    System.out.println("开始下载。。。");
                    while ((len = is.read(bt)) != -1) {
                        raf.write(bt, 0, len);

                        mFinished += len;

                        threadInfo.setFinished(threadInfo.getFinished() + len);
                        if (mIsPause) {
                            mDao.updateThread(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());
                            return;
                        }
                    }
                }

                isFinished = true;
                checkAllFinished();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }

}

