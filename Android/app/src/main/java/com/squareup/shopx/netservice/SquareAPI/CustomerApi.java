package com.squareup.shopx.netservice.SquareAPI;


import com.squareup.shopx.model.Customer;
import com.squareup.shopx.model.CustomerResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CustomerApi {

    //retrieve all items
    @POST("v2/customers")
    Observable<CustomerResponse> createCustomer(
            @Body Customer customer
    );

}


