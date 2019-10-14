package cn.sharelink.leddemo.util.http;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.sharelink.leddemo.App;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil implements Interceptor{

    //长缓存有效期为7天
    String CACHE_STALE_LONG="60 * 60 * 24 * 7";

    OkHttpClient mOkHttpClient = null;

    private static ApiService service = null;
    private static RetrofitUtil retrofitUtil;

    //初始化
     {
        initOkHttpclient();
        String url = "http://api.apiopen.top/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    public RetrofitUtil(){

    }

    public static RetrofitUtil builder(){
         if (null == retrofitUtil){
             synchronized (RetrofitUtil.class){
                 if (null == retrofitUtil){
                     retrofitUtil = new RetrofitUtil();
                 }
             }
         }
        return retrofitUtil;
    }


    public ApiService getService() {
         return service;
    }

    //配置缓存策略
    private void initOkHttpclient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            Cache cache = new Cache(new File(App.getInstance().getCacheDir(), "File_Kotlin"), 14 * 1024 * 100);
            mOkHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .retryOnConnectionFailure(true)
                    .addNetworkInterceptor(interceptor)
                    .addInterceptor(interceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }
    }

    //云端响应头拦截器，用来适配缓存策略
    @Override
    public Response intercept(Interceptor.Chain chain)  {
        Request request = chain.request();
        if (NetworkUtil.isConnected(App.getInstance())) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        } Response response = null;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (NetworkUtil.isConnected(App.getInstance())) {
            String cacheControl = request.cacheControl().toString();
            return response.newBuilder().header("Cache-Control", cacheControl)
                    .removeHeader("Pragma").build();
        } else {
            return response.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_LONG)
                    .removeHeader("Pragma").build();
        }
    }
}
