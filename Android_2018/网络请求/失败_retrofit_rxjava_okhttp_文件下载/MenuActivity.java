package cn.sharelink.leddemo.view.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.sharelink.leddemo.BuildConfig;
import cn.sharelink.leddemo.MainActivity;
import cn.sharelink.leddemo.R;
import cn.sharelink.leddemo.adapter.MenuAdapter;
import cn.sharelink.leddemo.contract.DialogList;
import cn.sharelink.leddemo.model.MyConfig;
import cn.sharelink.leddemo.model.entity.MenuBean;
import cn.sharelink.leddemo.util.LogUtils;
import cn.sharelink.leddemo.util.MyDividerItemDecoration;
import cn.sharelink.leddemo.util.SPUtil;
import cn.sharelink.leddemo.util.ToastUtil;
import cn.sharelink.leddemo.util.http.ApkUtil;
import cn.sharelink.leddemo.util.http.MyDownLoad;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static cn.sharelink.leddemo.model.MyConfig.AC_REGION;
import static cn.sharelink.leddemo.model.MyConfig.REGION_CHINA;

public class MenuActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener{

    @BindView(R.id.common_toolbar_title)
    TextView commonToolbarTitle;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.menu_recycler)
    RecyclerView menuRecycler;


    private MenuAdapter menuAdapter;
    private List<MenuBean> list = new ArrayList<>();
    long m_start,m_stop;

    private Handler mHandler;
    private int progress = 0;
    private ProgressDialog numDialog;
    String dir ;
    String name ;
    String apk_url = "http://gdown.baidu.com/data/wisegame/0d5a2f3c0e6b889c/qunaerlvxing_146.apk";
    private boolean isStart = true;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_menu;
    }

    @Override
    public void initView() {
        init();
        iniData();
        initHandler();
    }

    private void init() {
        commonToolbarTitle.setText(getString(R.string.menu_center));
        setSupportActionBar(commonToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        commonToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void iniData() {
        menuRecycler.setLayoutManager(new LinearLayoutManager(this));
        menuRecycler.setHasFixedSize(true);
        menuRecycler.setItemAnimator(new DefaultItemAnimator());
        menuRecycler.addItemDecoration(new MyDividerItemDecoration(this, MyDividerItemDecoration
                .VERTICAL_LIST));
        String region = SPUtil.getInstance().get(AC_REGION,REGION_CHINA);
        switch (region){
            case MyConfig.REGION_CHINA:
                region = getString(R.string.China);
                break;
            case MyConfig.REGION_EUROPE:
                region = getString(R.string.Europe);
                break;
            case MyConfig.REGION_TEST:
                region = getString(R.string.Test);
                break;
        }
        list.clear();
        list.add(new MenuBean(getString(R.string.menu_account), SPUtil.getInstance().get(MyConfig.USER_ACCOUNT, MyConfig.DEFAULT_ACCOUNT),false));
        list.add(new MenuBean(getString(R.string.menu_location), region,false));
        list.add(new MenuBean(getString(R.string.menu_version), "V "+BuildConfig.VERSION_NAME,false));
        list.add(new MenuBean(getString(R.string.menu_apk), getString(R.string.apk_hint),true));
        list.add(new MenuBean(getString(R.string.menu_about), " ",true));
        list.add(new MenuBean(getString(R.string.menu_logout), " ",true));
        menuAdapter = new MenuAdapter(R.layout.item_menu,list);
        menuAdapter.setOnItemClickListener(this);
        menuRecycler.setAdapter(menuAdapter);
    }

    @Override
    public void dealAck(String payload) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position){
            case 3://检测更新
                DialogList.getDialog(MenuActivity.this, getString(R.string.menu_apk), getString(R.string.apk_msg), new DialogList.MyDialogCall() {
                    @Override
                    public void cancel() {
                    }

                    @Override
                    public void ok() {
                        m_start = System.currentTimeMillis();
//                        new Thread(){
//                            @Override
//                            public void run() {
//                                super.run();
                                ApkUtil.getNew("http://api.apiopen.top/", apk_url, new ApkUtil.MyApkCall() {
                                    @Override
                                    public void success(ResponseBody responseBody) {
                                        Message message = Message.obtain();
                                        message.what = 1;
                                        message.obj = responseBody;
                                        mHandler.sendMessage(message);
                                    }

                                    @Override
                                    public void fail(Throwable e) {
                                        ToastUtil.showShort(MenuActivity.this,getString(R.string.update_fail));
                                    }
                                });
//                            }
//                        }.start();
                    }
                });
                break;
            case 4://关于
                startTo(AboutActivity.class);
                break;
            case 5://退出登录
                if (AC.accountMgr().isLogin()) {
                    AC.accountMgr().logout();
                }
                startTo(LoginActivity.class);
                finish();
                break;
        }
    }

    private void initHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        m_stop = System.currentTimeMillis();
                        LogUtils.i("网络请求用时："+(m_stop - m_start));
                        final ResponseBody responseBody = (ResponseBody) msg.obj;
                        dir = Environment.getExternalStorageDirectory()+ File.separator+"apk"+File.separator;
                        name = "LEDDemo.apk";
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                MyDownLoad.writeResponseBodyToDisk(new File(dir), name, responseBody, new MyDownLoad.MyProgress() {
                                    @Override
                                    public void getProress(int gress) {
                                        progress = gress;
                                        sendEmptyMessage(2);
                                    }
                                });
                            }
                        }.start();
                        break;
                    case 2:
                        setProgress();
                        break;
                    case 3:
                        if (new File(dir+name).exists()) {
                            ApkUtil.installApk(MenuActivity.this, dir + name);
                        }else {
                            ToastUtil.showShort(MenuActivity.this,getString(R.string.install_fail));
                        }
                        break;
                }
            }
        };
    }

    private void setProgress(){
        if (isStart){
            numDialog = DialogList.numProgress(MenuActivity.this,getString(R.string.update),getString(R.string.updating));
        }
        isStart = false;
        LogUtils.i("进度："+progress);
        if (progress >= 100){
            progress = 100;
            numDialog.setProgress(progress);
            numDialog.dismiss();
            progress = 0;
            isStart = true;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {//等待1秒再安装App
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(3);
                }
            }.start();
        }else {
            numDialog.setProgress(progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (numDialog!=null && numDialog.isShowing()){
            numDialog.dismiss();
        }
    }

}
