package cn.sharelink.leddemo.util.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @GET("top250")
    Observable<ResponseBody> getTopMovie(@Query("start") int start, @Query("count") int count);

    @Streaming//大文件必须添加
    @GET
    Call<ResponseBody> download(@Url String fileUrl);
}
