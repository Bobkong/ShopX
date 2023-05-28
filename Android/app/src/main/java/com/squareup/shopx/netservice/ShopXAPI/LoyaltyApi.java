package com.squareup.shopx.netservice.ShopXAPI;

import com.squareup.shopx.model.EnrollLoyaltyRequest;
import com.squareup.shopx.model.EnrollLoyaltyResponse;
import com.squareup.shopx.model.GetAllRecordsRequest;
import com.squareup.shopx.model.GetAllLoyaltyRecordsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoRequest;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoyaltyApi {

    @POST("/getLoyaltyInfo")
    Observable<GetLoyaltyInfoResponse> getLoyaltyInfo(
            @Body GetLoyaltyInfoRequest request
    );

    @POST("/enrollLoyalty")
    Observable<EnrollLoyaltyResponse> enrollLoyaltyResponse(
            @Body EnrollLoyaltyRequest request
    );

    @POST("/getAllLoyaltyRecords")
    Observable<GetAllLoyaltyRecordsResponse> getAllLoyaltyRecords(
            @Body GetAllRecordsRequest request
    );

}

