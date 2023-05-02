package com.squareup.shopx.netservice.ShopXAPI;

import com.squareup.shopx.model.AddCustomerResponse;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GeneralResponse;
import com.squareup.shopx.model.GetMerchantDetailRequest;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.model.LoginRequest;
import com.squareup.shopx.model.ShopXCustomer;
import com.squareup.shopx.model.VerifyPhoneRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MerchantApi {

    @POST("/getAllMerchants")
    Observable<AllMerchantsResponse> getAllMerchants();


    @POST("/getMerchantDetail")
    Observable<GetMerchantDetailResponse> getMerchantDetail(
            @Body GetMerchantDetailRequest request
    );
}

