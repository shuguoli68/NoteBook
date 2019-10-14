package cn.sharelink.kotlinweb.util.http

import cn.sharelink.kotlinweb.model.entity.NewsBean
import cn.xinlian.kotlinweb.util.LogUtils
import cn.xinlian.kotlinweb.util.http.RetrofitManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type
import java.net.URL

class HttpUtil() {
    //获取NewsBean
    fun getBanner(): Observable<NewsBean> = RetrofitManager.builder("https://www.apiopen.top/").Service()!!.getNews().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { LogUtils.i("call…………………………") }
}