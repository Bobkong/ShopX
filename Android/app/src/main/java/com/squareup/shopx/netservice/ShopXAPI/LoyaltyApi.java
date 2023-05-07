package com.squareup.shopx.netservice.ShopXAPI;

import com.squareup.shopx.model.AddCustomerResponse;
import com.squareup.shopx.model.EnrollLoyaltyRequest;
import com.squareup.shopx.model.GeneralResponse;
import com.squareup.shopx.model.GetAllLoyaltyRecordsRequest;
import com.squareup.shopx.model.GetAllLoyaltyRecordsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoRequest;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.model.LoginRequest;
import com.squareup.shopx.model.ShopXCustomer;
import com.squareup.shopx.model.VerifyPhoneRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoyaltyApi {

    @POST("/getLoyaltyInfo")
    Observable<GetLoyaltyInfoResponse> getLoyaltyInfo(
            @Body GetLoyaltyInfoRequest request
    );

    @POST("/enrollLoyalty")
    Observable<GeneralResponse> enrollLoyaltyResponse(
            @Body EnrollLoyaltyRequest request
    );

    @POST("/getAllLoyaltyRecords")
    Observable<GetAllLoyaltyRecordsResponse> getAllLoyaltyRecords(
            @Body GetAllLoyaltyRecordsRequest request
    );


}

