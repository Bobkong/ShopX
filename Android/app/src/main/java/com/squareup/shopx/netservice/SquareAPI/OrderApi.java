package com.squareup.shopx.netservice.SquareAPI;


import com.squareup.shopx.model.CreateOrderRequest;
import com.squareup.shopx.model.CreateOrderResponse;
import com.squareup.shopx.model.Customer;
import com.squareup.shopx.model.CustomerResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {

    //retrieve all items
    @POST("v2/orders")
    Observable<CreateOrderResponse> createOrder(
            @Body CreateOrderRequest orderRequest
    );

    @GET("v2/orders/{order_id}")
    Observable<CreateOrderResponse> retrieveOrder(
            @Path("order_id")
            String orderId
    );

}


