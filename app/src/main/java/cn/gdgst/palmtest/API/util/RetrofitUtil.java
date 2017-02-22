package cn.gdgst.palmtest.API.util;


import cn.gdgst.palmtest.API.APIService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    /**
     * 服务器地址
     */
    private static final String BaseURL ="http://www.shiyan360.cn/index.php/api/";
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    private static Retrofit mRetrofit;
    private static APIService mAPIService;


    private static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            // Log
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient oKHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .client(oKHttpClient)
                    .build();
        }
        return mRetrofit;
    }

    public static APIService getAPIService() {
        if (mAPIService == null) {
            mAPIService = getRetrofit().create(APIService.class);
        }
        return mAPIService;
    }


}
