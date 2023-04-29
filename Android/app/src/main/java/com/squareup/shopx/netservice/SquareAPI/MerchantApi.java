package com.squareup.shopx.netservice.SquareAPI;



import com.squareup.shopx.model.MerchantResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MerchantApi {

    //retrieve merchant
    @GET("v2/merchants/{merchant_id}")
    Observable<MerchantResponse> retrieveMerchant(
            @Path("merchant_id")
            String merchantID
    );

}
