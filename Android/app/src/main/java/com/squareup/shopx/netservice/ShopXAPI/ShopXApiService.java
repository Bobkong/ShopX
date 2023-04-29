package com.squareup.shopx.netservice.ShopXAPI;


import com.squareup.shopx.model.AddCustomerResponse;
import com.squareup.shopx.model.Customer;
import com.squareup.shopx.model.GeneralResponse;
import com.squareup.shopx.model.LoginRequest;
import com.squareup.shopx.model.ShopXCustomer;
import com.squareup.shopx.model.VerifyPhoneRequest;
import com.squareup.shopx.netservice.HttpResultFunc;
import com.squareup.shopx.netservice.ShopXServiceManager;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ShopXApiService {
    private static ShopXApiService instance;
    public static synchronized ShopXApiService getInstance(){
        if (instance == null)
            instance=new ShopXApiService();

        return instance;
    }

    private final CustomerApi customerApi= ShopXServiceManager.getInstance().create(CustomerApi.class);

    public Observable<AddCustomerResponse> addCustomer(String email, String nickname, String password){
        return customerApi.createCustomer(new ShopXCustomer(email, nickname, 0, password))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> verifyPhone(String phoneNumber){
        return customerApi.verifyPhone(new VerifyPhoneRequest(phoneNumber))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> login(String phoneNumber, String password){
        return customerApi.login(new LoginRequest(phoneNumber, password))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }


}
