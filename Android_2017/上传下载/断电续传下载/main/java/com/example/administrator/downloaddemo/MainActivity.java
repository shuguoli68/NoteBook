package com.example.administrator.downloaddemo;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.downloaddemo.download.Constant;
import com.example.administrator.downloaddemo.download.DBManager;
import com.example.administrator.downloaddemo.download.DownLoaderController;
import com.example.administrator.downloaddemo.download.DownloadInfo;
import com.example.administrator.downloaddemo.download.DownloadState;
import com.example.administrator.downloaddemo.download.FileDownloader;
import com.example.administrator.downloaddemo.download.NetWorkChangeReceiver;
import com.example.administrator.downloaddemo.download.NetWorkUtil;
import com.example.administrator.downloaddemo.download.StartDownloadState;
import com.example.administrator.downloaddemo.download.StopDownloadState;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG=MainActivity.class.getSimpleName();

    private Button btn_startDownload;
    private Button btn_pauseDownload;
    private ProgressBar mProgressBar;
    private NetWorkChangeReceiver mNetWorkChangeReceiver;
TextView progress_size,progress_compelete;
    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case Constant.DOWNLOAD_START:
                    mProgressBar.setMax(msg.arg1);
                    progress_size.setText(getSize(msg.arg1));
                    break;
                case Constant.DOWNLOAD_KEEP:
                    mProgressBar.setProgress(msg.arg1);
                    progress_compelete.setText(getSize(msg.arg1));
                    break;
                case Constant.DOWNLOAD_COMPLETE:
                    Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                    String url= (String) msg.obj;
                    DBManager.getInstance(MainActivity.this).delete(url);
                    break;
                case Constant.DOWNLOAD_FAIL:
                    Toast.makeText(MainActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
                    String urlstr= (String) msg.obj;
                    FileDownloader.getInstance().pauseDownload(urlstr);
                    break;
                case Constant.DOWNLOAD_ClLEAN:
//                    do something
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        btn_pauseDownload=(Button) findViewById(R.id.stop_download);
        btn_startDownload=(Button) findViewById(R.id.start_download);
        mProgressBar=(ProgressBar) findViewById(R.id.pbSmall);
        mNetWorkChangeReceiver=new NetWorkChangeReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkChangeReceiver,filter);
        final DownloadState startstate=new StartDownloadState();
        final DownloadState pausestate=new StopDownloadState();
        final DownLoaderController controller=new DownLoaderController();
        final String filename= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"test.apk";
        Log.d(TAG,"file name: "+filename);
        btn_pauseDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkUtil.isNetworkAvailable(MainActivity.this)){
                    Toast.makeText(MainActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    return;
                }
                controller.setDownloadState(pausestate);
                controller.stopDownload(MainActivity.this,mHandler,Constant.downloadUrl,filename,1);
            }
        });
        btn_startDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkUtil.isNetworkAvailable(MainActivity.this)){
                    Toast.makeText(MainActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    return;
                }
                controller.setDownloadState(startstate);
                controller.startDownload(MainActivity.this,mHandler,Constant.downloadUrl,filename,1);
            }
        });
        progress_size = (TextView) findViewById(R.id.progress_size);
        progress_compelete = (TextView) findViewById(R.id.progress_compelete);

        List<DownloadInfo> infos = DBManager.getInstance(this).getInfos(Constant.downloadUrl);
        if (infos == null) {
        }else {
            for (DownloadInfo info : infos) {
                progress_size.setText(getSize(info.getEndPos()));
                progress_compelete.setText(getSize(info.getCompeleteSize()));
                mProgressBar.setMax(info.getEndPos());
                mProgressBar.setProgress(info.getCompeleteSize());
            }
        }
    }

    private String getSize(int str){
        //工具类 根据文件大小自动转化为KB, MB, GB
        return android.text.format.Formatter.formatFileSize(MainActivity.this,Long.valueOf(str));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetWorkChangeReceiver!=null){
            unregisterReceiver(mNetWorkChangeReceiver);
        }
        DBManager.getInstance(this).closeDb();
    }
}
