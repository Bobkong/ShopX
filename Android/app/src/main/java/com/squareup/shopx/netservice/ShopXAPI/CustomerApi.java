package com.squareup.shopx.netservice.ShopXAPI;

import com.squareup.shopx.model.AddCustomerResponse;
import com.squareup.shopx.model.GeneralResponse;
import com.squareup.shopx.model.LoginRequest;
import com.squareup.shopx.model.LoginResponse;
import com.squareup.shopx.model.ShopXCustomer;
import com.squareup.shopx.model.UpdateNotifyRequest;
import com.squareup.shopx.model.VerifyPhoneRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CustomerApi {

    @POST("/saveCustomer")
    Observable<AddCustomerResponse> createCustomer(
            @Body ShopXCustomer customer
    );

    @POST("/verifyPhone")
    Observable<GeneralResponse> verifyPhone(
            @Body VerifyPhoneRequest customer
    );

    @POST("/login")
    Observable<LoginResponse> login(
            @Body LoginRequest loginRequest
    );

    @POST("/checkCustomer")
    Observable<GeneralResponse> checkCustomer(
            @Body ShopXCustomer customer
    );

    @POST("/updateNotify")
    Observable<GeneralResponse> updateNotify(
            @Body UpdateNotifyRequest request
    );


}

