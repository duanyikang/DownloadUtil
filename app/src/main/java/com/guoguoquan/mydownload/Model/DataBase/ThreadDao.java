package com.guoguoquan.mydownload.Model.DataBase;

import com.guoguoquan.mydownload.Bean.ThreadInfo;

import java.util.List;

/**
 * 作者：小段果果 on 2016/8/15 10:45
 * 邮箱：duanyikang@mumayi.com
 */
public interface ThreadDao {

    public void insertThread(ThreadInfo info);

    public void deleteThread(String url, int thread_id);

    public void updateThread(String url, int thread_id, int finished);

    public List<ThreadInfo> queryThreads(String url);

    public boolean isExists(String url, int threadId);
}

