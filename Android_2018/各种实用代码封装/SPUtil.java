package cn.sharelink.leddemo.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import cn.sharelink.leddemo.App;

public class SPUtil {

    private static final String PREF_NAME = "app_pref";
    private static SPUtil spUtil;
    private static boolean sIsAtLeastGB;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sIsAtLeastGB = true;
        }
    }

    public SPUtil(){

    }

    public static SPUtil getInstance() {
        if (spUtil == null){
            synchronized (SPUtil.class){
                if (spUtil == null){
                    spUtil = new SPUtil();
                }
            }
        }
        return spUtil;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static void apply(SharedPreferences.Editor editor) {
        if (sIsAtLeastGB) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPreferences getPreferences() {
        SharedPreferences pre = App.getInstance().getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }

    public void set(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        apply(editor);
    }

    public void set(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        apply(editor);
    }

    public boolean get(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public String get(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public int get(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }
}
