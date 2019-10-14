package cn.sharelink.htmldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * WebView总结
 * 参考1：https://www.jianshu.com/p/0b986d6e2e17
 * 参考2：https://blog.csdn.net/qq_36467463/article/details/78500161
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView mainWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainWeb = findViewById(R.id.main_web);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);

        initView();
    }

    private void initView() {
        //设置为可调用js方法
        mainWeb.getSettings().setJavaScriptEnabled(true);
        mainWeb.loadUrl("file:///android_asset/test.html");

        //4、H5如何调用android中的方法
        mainWeb.addJavascriptInterface(new JsInteration(),"alias1");//参数1为本地类名，参数2为别名.h5用window.别名.类名里的方法名才能调用方法里面的内容，例如：window.alias1.back1();
        mainWeb.setWebViewClient(new WebViewClient());
        mainWeb.setWebChromeClient(new WebChromeClient());


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                //1、调用H5中无参无返回值的方法
                mainWeb.loadUrl("JavaScript:show()");
                break;
            case R.id.btn2:
                //2、调用H5中带参数无返回值的方法
                String content = "android传来的参数";
                mainWeb.loadUrl("javascript:alertMessage(\""   +content+   "\")");
                break;
            case R.id.btn3:
                //3、调用H5中带参数带返回值的方法
                mainWeb.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Toast.makeText(MainActivity.this,"js返回的结果："+s,Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
