package cn.sharelink.leddemo.view.activity;

import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.download.SyncDownloadExecutor;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.sharelink.leddemo.BuildConfig;
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

import static cn.sharelink.leddemo.model.MyConfig.AC_REGION;
import static cn.sharelink.leddemo.model.MyConfig.REGION_CHINA;

public class MenuActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,DownloadListener{

    @BindView(R.id.common_toolbar_title)
    TextView commonToolbarTitle;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.menu_recycler)
    RecyclerView menuRecycler;


    private MenuAdapter menuAdapter;
    private List<MenuBean> list = new ArrayList<>();

    private Handler mHandler;
    private ProgressDialog numDialog;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"apk"+File.separator;
    private String name = "LEDDemo.apk";
    String apk_url = "http://gdown.baidu.com/data/wisegame/0d5a2f3c0e6b889c/qunaerlvxing_146.apk";
    private DownloadRequest downloadRequest;
    private String errorMsg = "下载出错了：";

    @Override
    public int getLayoutResId() {
        return R.layout.activity_menu;
    }

    @Override
    public void initView() {
        init();
        iniData();
        initHandler();
        NoHttp.initialize(this.getApplicationContext());
        downloadRequest = new DownloadRequest(apk_url, RequestMethod.GET, path, name, true, true);
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
                        numDialog = DialogList.numProgress(MenuActivity.this,getString(R.string.update),getString(R.string.updating));
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    SyncDownloadExecutor.INSTANCE.execute(0,downloadRequest,MenuActivity.this);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
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
                        if (numDialog!=null && numDialog.isShowing()){
                            numDialog.dismiss();
                        }
                        LogUtils.i("错误："+errorMsg);
                        ToastUtil.showShort(MenuActivity.this,errorMsg);
                        break;
                    case 2:
                        int progress = (int) msg.obj;
                        LogUtils.i("进度："+progress);
                        if (numDialog!=null)
                            numDialog.setProgress(progress);
                        break;
                    case 3:
                        if (numDialog!=null && numDialog.isShowing()){
                            numDialog.dismiss();
                        }
                        String apk_path = path + name;
                        if (new File(apk_path).exists()) {
                            ApkUtil.installApk(MenuActivity.this, apk_path);
                        }else {
                            ToastUtil.showShort(MenuActivity.this,getString(R.string.install_fail));
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (numDialog!=null && numDialog.isShowing()){
            numDialog.dismiss();
        }
    }

    @Override
    public void onDownloadError(int what, Exception exception) {
        if (exception instanceof ServerError) {
            errorMsg += "服务器发生内部错误";
        } else if (exception instanceof NetworkError) {
            errorMsg += "网络不可用，请检查网络";
        } else if (exception instanceof StorageReadWriteError) {
            errorMsg += "存储卡错误，请检查存储卡";
        } else if (exception instanceof StorageSpaceNotEnoughError) {
            errorMsg += "存储位置空间不足";
        } else if (exception instanceof TimeoutError) {
            errorMsg += "下载超时";
        } else if (exception instanceof UnKnownHostError) {
            errorMsg += "服务器找不到";
        } else if (exception instanceof URLError) {
            errorMsg += "url地址错误";
        } else {
            errorMsg += "未知错误";
        }
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long
            allCount) {

    }

    @Override
    public void onProgress(int what, int progress, long fileCount, long speed) {
        Message mMessage = Message.obtain();
        mMessage.what = 2;
        mMessage.obj = progress;
        mHandler.sendMessage(mMessage);
    }

    @Override
    public void onFinish(int what, String filePath) {
        mHandler.sendEmptyMessage(3);
    }

    @Override
    public void onCancel(int what) {

    }
}
