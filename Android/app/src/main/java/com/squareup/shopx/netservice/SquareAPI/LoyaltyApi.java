package com.squareup.shopx.netservice.SquareAPI;


import com.squareup.shopx.model.AccumulateLoyaltyPointsRequest;
import com.squareup.shopx.model.AccumulateLoyaltyPointsResponse;
import com.squareup.shopx.model.CreateLoyaltyRewardRequest;
import com.squareup.shopx.model.CreateLoyaltyRewardResponse;
import com.squareup.shopx.model.LoyaltyProgramResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoyaltyApi {

    //create a loyalty account
    @POST("v2/loyalty/rewards")
    Observable<CreateLoyaltyRewardResponse> createLoyaltyReward(
            @Body CreateLoyaltyRewardRequest createLoyaltyRewardRequest
    );

    //create a loyalty account
    @POST("v2/loyalty/accounts/{account_id}/accumulate")
    Observable<AccumulateLoyaltyPointsResponse> accumulateLoyaltyReward(
            @Path("account_id") String accountId,
            @Body AccumulateLoyaltyPointsRequest createLoyaltyRewardRequest
    );
}

