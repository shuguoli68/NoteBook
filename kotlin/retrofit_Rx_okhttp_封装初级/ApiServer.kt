package cn.sharelink.kotlinweb.util.http

import cn.sharelink.kotlinweb.model.entity.NewsBean
import cn.xinlian.kotlinweb.model.entity.BannerBean
import retrofit2.http.GET
import retrofit2.http.Query

import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServer {
    companion object {
        val BASE_URL: String get() = "http://route.xxx.com/"
    }

    @Headers("Cache-Control: public, max-age=60 * 60 * 24 * 7")
    @POST("journalismApi")
    fun  getNews(): Observable<NewsBean>

    @GET("")
    fun getHomeTxtData(@Query("xxx") apiid: String, @Query("xxx") sign: String, @Query("currPage") page: String, @Query("maxResult") num: String): Observable<NewsBean>

}