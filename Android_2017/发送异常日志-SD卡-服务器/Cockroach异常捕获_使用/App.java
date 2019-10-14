package com.example;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.util.Cockroach;
import com.vondear.rxtools.RxTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin on 2018/1/4.
 */
public class App extends Application implements Cockroach.ExceptionHandler{
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = App.this;

        RxTool.init(this);

        //异常捕获
        Cockroach.install(this);
    }

    public static App getInstance(){
        return instance;
    }

    public static void toast(String str){
        Toast.makeText(instance,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handlerException(final Thread thread, final  Throwable throwable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter pw = null;
                File file = null;
                try {
                    Date now = new Date(System.currentTimeMillis());
                    final Format FORMAT = new SimpleDateFormat("MM-dd HH-mm-ss", Locale.getDefault());
                    String fileName = "crash_"+FORMAT.format(now) + ".txt";
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                        File crashFolder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator, "crashLog");
                        if (!crashFolder.exists()){
                            crashFolder.mkdir();
                        }
                        file = new File(crashFolder,fileName);
                        pw = new PrintWriter(new FileWriter(file, false));
                        pw.write(getCrashHead());
                        throwable.printStackTrace(pw);
                        Throwable cause = throwable.getCause();
                        while (cause != null) {
                            cause.printStackTrace(pw);
                            cause = cause.getCause();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (pw != null) {
                        pw.close();
                    }
                }
                if (file!=null&&file.exists()){
                    //上传异常日志到服务器
                }
            }
        }).start();


        //toast提示，调试用
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Cockroach", thread + "\n" + throwable.toString());
                            throwable.printStackTrace();
                            Toast.makeText(instance, "调试：程序发生了异常！！\n" + thread + "\n"
                                    + throwable.toString(), Toast.LENGTH_LONG).show();
                        } catch (Throwable e) {

                        }
                    }
                });
    }


    public String getCrashHead() {
        int versionCode = -1;
        String versionName = "";
        try {
            PackageInfo pi = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "\n************* Crash Log Head ****************" +
                "\n设备厂商    : " + Build.MANUFACTURER +
                "\n设备型号    : " + Build.MODEL +
                "\n系统版本    : " + Build.VERSION.RELEASE +
                "\nSDK版本     : " + Build.VERSION.SDK_INT +
                "\n版本名称    : " + versionName +
                "\n版本号      : " + versionCode +
                "\n************* Crash Log Head ****************\n\n";
    }
}
