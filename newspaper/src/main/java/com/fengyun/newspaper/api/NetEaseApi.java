package com.fengyun.newspaper.api;

import com.fengyun.newpaper.bean.NewsList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by xinghongfei on 16/8/17.
 */
public interface NetEaseApi {

    // TODO: 16/8/17 string or int 
    @GET("http://c.m.163.com/nc/article/headline/T1348647909107/{id}-20.html")
    Observable<NewsList> getNews(@Path("id") int id);

    @GET("http://c.m.163.com/nc/article/{id}/full.html")
    Observable<String> getNewsDetail(@Path("id") String id);

}
