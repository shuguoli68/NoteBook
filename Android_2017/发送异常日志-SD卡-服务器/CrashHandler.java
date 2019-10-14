package com.example.util.crash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;

import com.example.SiwApp;
import com.example.textslidingmenu.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 2017/11/26.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //文件夹目录
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/admin_system/";
    //文件名
    private static final String FILE_NAME = "admin_crash_log";
    //文件名后缀
    private static final String FILE_NAME_SUFFIX = ".txt";
    //上下文
    private Context mContext;

    //单例模式
    private static CrashHandler sInstance = new CrashHandler();
    private CrashHandler() {}
    public static CrashHandler getInstance() {
        return sInstance;
    }

    /**
     * 初始化方法
     *
     * @param context
     */
    public void init(Context context) {
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    /**
     * 捕获异常回掉
     *
     * @param thread 当前线程
     * @param ex     异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //导出异常信息到SD卡
        dumpExceptionToSDCard(ex);
        //上传异常信息到服务器
        uploadExceptionToServer(ex);
        //延时2秒杀死进程
        SystemClock.sleep(2000);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
//        showPatchDialog();
    }

    /**
     * 导出异常信息到SD卡
     */
    private void dumpExceptionToSDCard(Throwable ex) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //创建文件夹
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //获取当前时间
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        //以当前时间创建log文件
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            //输出流操作
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出手机信息和异常信息
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            pw.println("发生异常时间：" + time);
            pw.println("app："+ R.string.app_name);
            pw.println("登录用户："+ SiwApp.getNumber());
            pw.println("应用版本：" + pi.versionName);
            pw.println("应用版本号：" + pi.versionCode);
            pw.println("android版本号：" + Build.VERSION.RELEASE);
            pw.println("android版本号API：" + Build.VERSION.SDK_INT);
            pw.println("手机制造商:" + Build.MANUFACTURER);
            pw.println("手机型号：" + Build.MODEL);
            ex.printStackTrace(pw);
            //关闭输出流
            pw.close();
        } catch (Exception e) {

        }
    }

    /**
     * 上传异常信息到服务器
     */
    private void uploadExceptionToServer(Throwable ex) {

    }

    private void showPatchDialog() {
        Intent intent = PatchDialogActivity.newIntent(mContext, getApplicationName(mContext), null);
        mContext.startActivity(intent);
    }

    private String getApplicationName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        String name = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(
                    context.getApplicationInfo().packageName, 0);
            name = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (final PackageManager.NameNotFoundException e) {
            String[] packages = context.getPackageName().split(".");
            name = packages[packages.length - 1];
        }
        return name;
    }
}
