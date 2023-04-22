package com.squareup.easyshop.netservice.SquareAPI;


import com.squareup.easyshop.model.LoyaltyProgramResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LoyaltyApi {

    //retrieve all items
    @GET("v2/loyalty/programs/{program_id}")
    Observable<LoyaltyProgramResponse> retrieveLoyaltyProgram(
            @Path("program_id")
            String programId
    );

}

