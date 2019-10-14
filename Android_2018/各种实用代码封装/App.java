package cn.sharelink.robotdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.sharelink.robotdemo.activity.SettingActivity;
import cn.sharelink.robotdemo.model.MyConfig;
import cn.sharelink.robotdemo.model.gen.DaoMaster;
import cn.sharelink.robotdemo.model.gen.DaoSession;


/**
 * Created by WangLei on 2018/6/7.
 */

public class App extends Application{

    private static String PREF_NAME = "app_pref";
    private static  App instance;
    private List<Activity> oList;//用于存放所有启动的Activity的集合

    //数据库的初始化
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoSession daoSession;

    private static boolean sIsAtLeastGB;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sIsAtLeastGB = true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = App.this;

        helper = new DaoMaster.DevOpenHelper(this, "robot-db", null);
        db = helper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();

        Log.d("App", "onCreate");

        App.set(MyConfig.IS_LINK, false);
        oList = new ArrayList<Activity>();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
    }

    public static App getInstance(){
        return instance;
    }

    public DaoSession getDaoSession(){
        return daoSession;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void apply(SharedPreferences.Editor editor) {
        if (sIsAtLeastGB) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences getPreferences() {
        SharedPreferences pre = instance.getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }

    public static void set(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        apply(editor);
    }

    public static void set(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    public static void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        apply(editor);
    }

    public static boolean get(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String get(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static int get(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
//        System.exit(0);
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        String packName = AppUtil.getAppProcessName(this);
//        if (!TextUtils.isEmpty(packName)) {
//            manager.killBackgroundProcesses(packName);
//        }
    }
}
