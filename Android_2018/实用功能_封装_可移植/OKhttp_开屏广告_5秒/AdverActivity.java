package com.xinlian.xladver.Adver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xinlian.xladver.LogUtil;
import com.xinlian.xladver.MainActivity;
import com.xinlian.xladver.R;

import java.util.HashMap;
import java.util.Map;

public class AdverActivity extends AppCompatActivity{

    private TextView txt;
    private ImageView img;
    private AdverBean bean;
    MyCountDownTimer timer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_adver);
        initView();
    }

    private void initView() {
        txt = findViewById(R.id.adver_txt);
        img = findViewById(R.id.adver_icon);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToMain();
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://193.112.103.246/shareLink-1/index.html";
                if (bean != null && !TextUtils.isEmpty(bean.getAdverLink()))
                    url = bean.getAdverLink();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        LogUtil.i("该手机屏幕(宽："+getResources().getDisplayMetrics().widthPixels+",高："+getResources().getDisplayMetrics().heightPixels+")");
        final Map<String,String> map = new HashMap<>();
        map.put("appName", "YHUPS");//传递键值对参数
        map.put("resolution", "1080*1920");
        map.put("adverType", "firstPage");
        map.put("id", "0");
        MyOkhttpUtil.postRequest(map, new MyOkhttpUtil.MyCall() {
            @Override
            public void success(AdverBean bean) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = bean;
                mHandler.sendMessage(message);
            }

            @Override
            public void fail(Exception e) {
                LogUtil.i(e.toString());
            }
        });

        timer = new MyCountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txt.setText("   "+millisUntilFinished/1000 + " 跳过   ");
            }

            @Override
            public void onFinish() {
                startToMain();
            }
        };
    }

    private void startToMain(){
        startActivity(new Intent(AdverActivity.this, MainActivity.class));
        finish();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                bean = (AdverBean) msg.obj;
                LogUtil.i(bean.toString());
                String url = "https://b-ssl.duitang.com/uploads/item/201511/11/20151111103149_mrRfd.jpeg";
                if (bean!=null && !TextUtils.isEmpty(bean.getAdverIcon()))
                    url = bean.getAdverIcon();
                Glide.with(AdverActivity.this).load(url).apply(requestOptions).into(img);
                timer.start();
            }
        }
    };

    RequestOptions requestOptions = new RequestOptions()
            .placeholder(new ColorDrawable(Color.BLACK))
            .error(new ColorDrawable(Color.BLUE))
            .fallback(new ColorDrawable(Color.RED));

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
