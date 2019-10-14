package cn.sharelink.leddemo.util.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private Retrofit mRetrofit;
    private String baseUrl;
    private static RetrofitManager mRetrofitManager;
    public static OkHttpClient okHttpClient;

    //静态块,获取OkHttpClient对象
    static {
        getOkHttpClient();
    }

    private RetrofitManager(String baseUrl) {
        this.baseUrl = baseUrl;
        initRetrofit();
    }

    public static synchronized RetrofitManager getInstance(String baseUrl) {
        if (mRetrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (mRetrofitManager == null) {
                    mRetrofitManager = new RetrofitManager(baseUrl);
                }
            }
        }
        return mRetrofitManager;
    }

    //单例模式获取okhttp
    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
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

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    /**
     * 创建相应的服务接口
     */
    public <T> T setCreate(Class<T> reqServer) {
        return mRetrofit.create(reqServer);
    }
}
