package com.fengyun.http;

import android.app.Application;


import com.fengyun.http.api.MeiZhiApi;
import com.fengyun.http.api.TopNewsApi;
import com.fengyun.http.api.ZhihuApi;
import com.fengyun.util.NetWorkUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xinghongfei on 16/8/12.
 */
public class ApiManager {


    private static Application mApplication;


//    public static ApiManager getInstence() {
//        if (apiManager == null) {
//            synchronized (ApiManager.class) {
//                if (apiManager == null) {
//                    apiManager = new ApiManager();
//                }
//            }
//        }
//        return apiManager;
//    }

    private static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = null;


    private static File httpCacheDirectory;
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache;
    private static OkHttpClient client;


    private static ZhihuApi zhihuApi;
    private static TopNewsApi topNewsApi;
    private static MeiZhiApi meiZhiApi;
    private static Object monitor = new Object();


    private static void init() {
        REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                if (NetWorkUtils.isNetWorkAvailable(mApplication)) {
                    int maxAge = 60; // 在线缓存在1分钟内可读取
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
            }
        };
        httpCacheDirectory = new File(mApplication.getCacheDir(), "zhihuCache");
        cache = new Cache(httpCacheDirectory, cacheSize);
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();
    }
    public static ZhihuApi getZhihuApi() {
        if (zhihuApi == null) {
            synchronized (monitor) {
                if (zhihuApi == null) {
                    if(client == null){
                        init();
                    }
                    zhihuApi = new Retrofit.Builder()
                            .baseUrl("http://news-at.zhihu.com")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(ZhihuApi.class);
                }
            }
        }

        return zhihuApi;
    }

    public static TopNewsApi getTopNewsApi() {
        if (topNewsApi == null) {
            synchronized (monitor) {
                if (topNewsApi == null) {
                    if(client == null){
                        init();
                    }
                    topNewsApi = new Retrofit.Builder()
                            .baseUrl("http://c.m.163.com")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(TopNewsApi.class);

                }
            }
        }

        return topNewsApi;
    }


    public static MeiZhiApi getMeiZhiApi(){
        if (meiZhiApi==null){
            synchronized (monitor){
                if (meiZhiApi==null){
                    if(client == null){
                        init();
                    }
                    meiZhiApi=new Retrofit.Builder()
                            .baseUrl("http://gank.io")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(MeiZhiApi.class);

                }
            }
        }
        return meiZhiApi;
    }


    public static Application getApplication() {
        return mApplication;
    }

    public static void setApplication(Application application) {
        mApplication = application;
    }
}
