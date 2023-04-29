package com.squareup.shopx.netservice.SquareAPI;


import com.squareup.shopx.model.LoyaltyProgramResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LoyaltyApi {

    //retrieve loyalty program
    @GET("v2/loyalty/programs/{program_id}")
    Observable<LoyaltyProgramResponse> retrieveLoyaltyProgram(
            @Path("program_id")
            String programId
    );

    //create a loyalty account
    @POST("v2/loyalty/accounts")
    Observable<LoyaltyProgramResponse> createLoyaltyAccount(
            @Body String programId
    );
}

