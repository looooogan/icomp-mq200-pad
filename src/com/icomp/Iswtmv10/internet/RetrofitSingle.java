package com.icomp.Iswtmv10.internet;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by Think on 2016/11/21.
 */

public  class RetrofitSingle {
    private static final int DEFAULT_TIMEOUT = 5000;
    static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    private static Retrofit mRetrofit;


    //大众接口
    public static void getmRetrofit() {
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
//        mRetrofit = new Retrofit.Builder().baseUrl("http://localhost:8080")
        mRetrofit = new Retrofit.Builder().baseUrl("http://39.106.122.167:86/")
//        mRetrofit = new Retrofit.Builder().baseUrl("http://192.168.1.152:8081")
//        mRetrofit = new Retrofit.Builder().baseUrl("http://10.216.82.241:8080")//现场本地
//        mRetrofit = new Retrofit.Builder().baseUrl("http://10.226.65.11")//服务器
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();
    }
    public static Retrofit newInstance(){
        getmRetrofit();
        return mRetrofit;
    }
}
