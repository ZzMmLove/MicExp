package cn.gdgst.palmtest.API.util;


import java.util.concurrent.TimeUnit;

import cn.gdgst.palmtest.API.APIService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 初始化Retrofit和RxJava以及作相关的配制
 */
public class RetrofitUtil {
    /**服务器地址,BaseURL必需要以一个'/'结束不然会抛出一个IllegalArgumentException异常*/
    private static final String BaseURL ="http://shiyan360.cn/index.php/api/";
    /**使用Jason解析请求数据*/
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    /**结合RxJava异步请求使用Retrofit*/
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    private static Retrofit mRetrofit;
    private static APIService mAPIService;

    /**抓取网页版的Http头部 然后可以自定义Android 的http头部，改成一样 */

    private static Retrofit getRetrofit() {

        if (mRetrofit == null) {
            // Log
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
           /// OkHttpClient oKHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
            OkHttpClient.Builder oKHtttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(5, TimeUnit.SECONDS);

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .client(oKHtttpClient.build())
                    .build();
        }
        return mRetrofit;
    }

    /**
     * 获取APIService的实例
     * @return  返回一个实例
     */
    public static APIService getAPIService() {
        if (mAPIService == null) {
            mAPIService = getRetrofit().create(APIService.class);
        }
        return mAPIService;
    }


}
