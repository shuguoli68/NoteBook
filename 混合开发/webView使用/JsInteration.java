package cn.sharelink.htmldemo;

import android.webkit.JavascriptInterface;

public class JsInteration {
    @JavascriptInterface
    public String back1(){
        return "android方法返回值";
    }
}
