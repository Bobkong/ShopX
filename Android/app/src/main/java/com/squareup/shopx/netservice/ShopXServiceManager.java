package com.squareup.shopx.netservice;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.shopx.BaseApplication;
import com.squareup.shopx.utils.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopXServiceManager {
    //域名
    private static final String baseUrl="http://172.190.15.5:8900";
    //连接超时
    private static final int CONNECT_TIMEOUT=60;
    //读超时
    private static final int READ_TIMEOUT=60;
    //写超时
    private static final int WRITE_TIMEOUT=60;
    //缓存目录
    private static final String CACHE_Dir="TempCache";
    //缓存大小为50M
    private static final long CACHE_SIZE=1024*1024*50;
    //有网络时，缓存超时时间为1分钟
    private static final int MAX_AGE=60;
    //无网络时，缓存超时时间为7天
    private static final int MAX_STALE=60*60*24;
    //单例模式
    private static ShopXServiceManager instance;
    private Retrofit retrofit;
    private ShopXServiceManager(){
        //设置缓存目录
        File cacheFile=new File(BaseApplication.getContext().getExternalCacheDir(),CACHE_Dir);
        //设置缓存
        Cache cache=new Cache(cacheFile,CACHE_SIZE);
        //设置缓存策略
        Interceptor cacheInterceptor=new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request=chain.request();
                //没有网络连接时设为强制缓存
                if(!NetworkUtil.isNetworkAvailable()){
                    request=request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response=chain.proceed(request);
                if(NetworkUtil.isNetworkAvailable()){
                    response.newBuilder()
                            .header("Cache-Control","public,max-age="+MAX_AGE)
                            .removeHeader("Pragma")
                            .build();
                }else{
                    response.newBuilder()
                            .header("Cache-Control","public,only-if-cached,max-stale="+MAX_STALE)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
        //OkHttpClient设置
        OkHttpClient client=new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(cacheInterceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //Retrofit设置
        retrofit=new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    //单例，懒汉式线程安全
    public static synchronized ShopXServiceManager getInstance(){
        if(instance==null)
            instance=new ShopXServiceManager();
        return instance;
    }

    //获取对应service
    public <T> T create(Class<T> service){
        return retrofit.create(service);
    }
}
