package com.squareup.shopx.netservice;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.shopx.BaseApplication;
import com.squareup.shopx.Util.NetworkUtil;

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

public class SquareServiceManager {
    //域名
    private static final String baseUrl="https://connect.squareupsandbox.com/";
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
    private static SquareServiceManager instance;
    private Retrofit retrofit;
    // access token
    private String authorizationToken;

    private SquareServiceManager(){
        //设置缓存目录
        File cacheFile=new File(BaseApplication.getContext().getExternalCacheDir(),CACHE_Dir);
        //设置缓存
        Cache cache=new Cache(cacheFile,CACHE_SIZE);
        //设置缓存策略
        Interceptor cacheInterceptor=new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original=chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", authorizationToken)
                        .method(original.method(), original.body())
                        .build();
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
    public static synchronized SquareServiceManager getInstance(){
        if (instance == null)
            instance=new SquareServiceManager();
        return instance;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    //获取对应service
    public <T> T create(Class<T> service){
        return retrofit.create(service);
    }
}
