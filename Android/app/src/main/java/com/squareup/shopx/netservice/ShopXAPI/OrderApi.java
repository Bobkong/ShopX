package com.squareup.shopx.netservice.ShopXAPI;

import com.squareup.shopx.model.AddCustomerResponse;
import com.squareup.shopx.model.GeneralResponse;
import com.squareup.shopx.model.LoginRequest;
import com.squareup.shopx.model.PlaceOrderRequest;
import com.squareup.shopx.model.ShopXCustomer;
import com.squareup.shopx.model.VerifyPhoneRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderApi {

    @POST("/placeOrder")
    Observable<GeneralResponse> placeOrder(
            @Body PlaceOrderRequest request
    );


}

