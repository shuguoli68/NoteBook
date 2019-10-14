package cn.xinlian.kotlinweb.util.http

import cn.sharelink.kotlinweb.App
import cn.sharelink.kotlinweb.util.NetworkUtil
import cn.sharelink.kotlinweb.util.http.ApiServer
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * TODO:RetrofitManager
 */
class RetrofitManager private constructor(url: String) : Interceptor {

    //短缓存有效期为10分钟
    val CACHE_STALE_SHORT = 60 * 10
    //长缓存有效期为7天
    val CACHE_STALE_LONG="60 * 60 * 24 * 7"
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    val CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_LONG
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    val CACHE_CONTROL_NETWORK = "max-age=0"

    var mOkHttpClient: OkHttpClient? = null

    var service: ApiServer? = null

    //初始化
    init {
        initOkHttpclient()
        var retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(ApiServer::class.java)

    }

    companion object {
        fun builder(url: String): RetrofitManager {
            println(RetrofitManager.javaClass.classes)
            return RetrofitManager(url)
        }
    }

    fun Service() = service

    //配置缓存策略
    fun initOkHttpclient() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        if (mOkHttpClient == null) {
            val cache = Cache(File(App.instance().cacheDir, "File_Kotlin"), 14 * 1024 * 100)
            mOkHttpClient = OkHttpClient.Builder()
                    .cache(cache)
                    .retryOnConnectionFailure(true)
                    .addNetworkInterceptor(this)
                    .addInterceptor(this)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build()
        }
    }

    //云端响应头拦截器，用来适配缓存策略
    override fun intercept(chain: Interceptor.Chain?): Response {
        var request = chain!!.request()
        val net:NetworkUtil = NetworkUtil()
        if (!net.isNetConnected(App.instance())) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
        }
        var response = chain.proceed(request)
        if (net.isNetConnected(App.instance())) {
            var cacheControl: String = request.cacheControl().toString()
            return response.newBuilder().header("Cache-Control", cacheControl)
                    .removeHeader("Pragma").build()
        } else {
            return response.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_LONG)
                    .removeHeader("Pragma").build()
        }
    }

}