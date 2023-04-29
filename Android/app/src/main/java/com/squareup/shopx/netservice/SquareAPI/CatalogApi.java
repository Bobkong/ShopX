package com.squareup.shopx.netservice.SquareAPI;



import com.squareup.shopx.model.ObjectsResponse;
import com.squareup.shopx.model.BatchRetrieveRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CatalogApi {

    //retrieve all items
    @GET("v2/catalog/list")
    Observable<ObjectsResponse> retrieveItems(
            @Query("types")
            String types
    );

    //batch retrieve objects
    @POST("v2/catalog/batch-retrieve")
    Observable<ObjectsResponse> batchRetrieveObjects(
            @Body BatchRetrieveRequest objectIds
    );

}

