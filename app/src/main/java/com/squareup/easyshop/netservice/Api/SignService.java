package com.squareup.easyshop.netservice.Api;



import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class SignService {
    private static SignService instance;
    public static synchronized SignService getInstance(){
        if(instance==null)
            instance=new SignService();
        return instance;
    }




}
