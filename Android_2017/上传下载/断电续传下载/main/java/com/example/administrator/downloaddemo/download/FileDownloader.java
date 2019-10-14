package com.example.administrator.downloaddemo.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * Created by Jim on 2017/7/21.
 */

public class FileDownloader {

    private final String TAG = FileDownloader.class.getSimpleName();

    public static FileDownloader sFileDownloader;
    private Context context;
    private Handler handler;
    private String downloadurl;
    private int filesize;
    private String filename;
    private int threadCount;
    private HashMap<String, Integer> downloadStatemap;

    private int state = Constant.DOWNLOAD_STATE_INIT;

    public static FileDownloader getInstance() {
        if (sFileDownloader == null) {
            synchronized (FileDownloader.class) {
                if (sFileDownloader == null) {
                    sFileDownloader = new FileDownloader();
                }
            }
        }
        return sFileDownloader;
    }

    public synchronized FileDownloader init(Context context, Handler handler,
                                            String downloadurl, int filesize,
                                            String filename, int threadCount) {
        Log.d(TAG, "Run in init");
        this.context = context;
        this.handler = handler;
        this.downloadurl = downloadurl;
        this.filesize = filesize;
        this.filename = filename;
        this.threadCount = threadCount;
        if (downloadStatemap == null) {
            downloadStatemap = new HashMap<>();
        }
        initDatas();
        return this;
    }


    private void initDatas() {
        RandomAccessFile accessFile = null;
        File file;
        int block = (filesize % threadCount == 0) ? filesize / threadCount : filesize / threadCount + 1;
        try {
            file = new File(filename);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!DBManager.getInstance(context).isHasInfos(downloadurl)) {
                Log.d(TAG, "run download info");
                for (int i = 0; i < threadCount; i++) {
                    DownloadInfo info = new DownloadInfo(i, i * block, (i + 1) * block, 0, downloadurl);
                    DBManager.getInstance(context).saveInfo(info);
                }
            }
            accessFile = new RandomAccessFile(file, "rw");
            if (accessFile.length() == filesize) {
                return;
            }
            accessFile.setLength(filesize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (accessFile != null) {
                    accessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void pauseDownload(String downloadurl) {
        downloadStatemap.put(downloadurl, Constant.DOWNLOAD_STATE_PAUSE);
    }

    public void pauseAll() {
        if (downloadStatemap == null) {
            return;
        }
        for (String key:downloadStatemap.keySet()){
            downloadStatemap.put(key,Constant.DOWNLOAD_STATE_PAUSE);
        }

    }

    public int getDownloadState(String downloadurl) {
        int downloadstate = -1;
        if (downloadStatemap != null) {
            downloadstate = downloadStatemap.get(downloadurl);
        }
        return downloadstate;
    }

    public void putDownloadState(String downloadurl, int state) {
        if (downloadStatemap != null) {
            downloadStatemap.put(downloadurl, state);
        }
    }

    private void sendMessage(int what, int arg1, int arg2, Object obj) {
        Message message = new Message();
        message.what = what;
        if (arg1 != -1) {
            message.arg1 = arg1;
        }
        if (arg2 != -1) {
            message.arg2 = arg2;
        }
        if (obj != null) {
            message.obj = obj;
        }
        message.setTarget(handler);
        message.sendToTarget();
    }

    public synchronized void startDownload() {
        if (downloadStatemap.get(downloadurl) != null && downloadStatemap.get(downloadurl) == Constant.DOWNLOAD_STATE_START) {
            Log.d(TAG, "download return");
            return;
        }
        sendMessage(Constant.DOWNLOAD_START, filesize, -1, null);
        for (int i = 0; i < threadCount; i++) {
            ThreadPoolsUtil.getInstance().getCachedThreadPool().execute(new DownloadTask(context, handler, downloadurl, filesize, filename, i));
        }
    }
}