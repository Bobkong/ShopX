package com.squareup.easyshop.netservice.SquareAPI;


import com.squareup.easyshop.model.Customer;
import com.squareup.easyshop.model.CustomerResponse;
import com.squareup.easyshop.model.ObjectsResponse;

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


