package com.xinlian.xladver.Adver;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xinlian.xladver.LogUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class MyOkhttpUtil {
    static String url = "http://193.112.103.246/app_advertising_manage/Advertising";
    private static OkHttpClient okHttpClient;

    public static void postRequest(final Map<String, String> hashMap, final MyCall myCall){
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : hashMap.keySet()) {
            builder.add(key, hashMap.get(key));
        }
        RequestBody formBody = builder.build();
        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtil.i("onFailure: "+e.toString());
                myCall.fail(e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                for (int i=0;i<hashMap.size();i++) {
                    LogUtil.i("call: " + ((FormBody) call.request().body()).value(i).toString());
                }
                String gsonString = response.body().string();
                LogUtil.i("onResponse: "+gsonString);
                Gson gson = new Gson();
                Type type = new TypeToken<List<AdverBean>>(){}.getType();
                List<AdverBean> list = gson.fromJson(gsonString,type);
                LogUtil.i("onResponse: "+list.get(0).getAdverIcon());
                myCall.success(list.get(0));
            }
        });
    }

    public interface MyCall{
        void success(AdverBean bean);
        void fail(Exception e);
    }

    //单例模式获取okhttp
    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            //body采用UTF-8编码
                            /*.addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request();
                                    Request.Builder requestBuilder = request.newBuilder();
                                    request = requestBuilder
                                            .addHeader("Content-Type", "application/json;charset=UTF-8")
                                            .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),
                                                    bodyToString(request.body())))//关键部分，设置requestBody的编码格式为json
                                            .build();
                                    return chain.proceed(request);
                                }
                            })*/
                            //打印拦截器日志
                            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            .connectTimeout(15, TimeUnit.SECONDS)//设置连接超时时间
                            .readTimeout(15, TimeUnit.SECONDS)//设置读取超时时间
                            .writeTimeout(15, TimeUnit.SECONDS)//设置写入超时时间
                            .build();
                }
            }
        }
        return okHttpClient;
    }
}
