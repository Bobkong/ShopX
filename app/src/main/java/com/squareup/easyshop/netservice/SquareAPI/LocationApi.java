package com.squareup.easyshop.netservice.SquareAPI;



import com.squareup.easyshop.model.LocationResponse;
import com.squareup.easyshop.model.MerchantResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LocationApi {

    //retrieve merchant
    @GET("v2/locations/{location_id}")
    Observable<LocationResponse> retrieveLocation(
            @Path("location_id")
            String locationId
    );

}
