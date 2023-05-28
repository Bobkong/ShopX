package com.squareup.shopx.netservice.ShopXAPI;

import com.squareup.shopx.model.AddCustomerResponse;
import com.squareup.shopx.model.GeneralResponse;
import com.squareup.shopx.model.GetAllRecordsRequest;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.model.GetOrderItemsRequest;
import com.squareup.shopx.model.GetOrderItemsResponse;
import com.squareup.shopx.model.GetOrdersResponse;
import com.squareup.shopx.model.LoginRequest;
import com.squareup.shopx.model.PlaceOrderRequest;
import com.squareup.shopx.model.ShopXCustomer;
import com.squareup.shopx.model.VerifyPhoneRequest;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderApi {

    @POST("/placeOrder")
    Observable<GeneralResponse> placeOrder(
            @Body PlaceOrderRequest request
    );

    @POST("/getAllOrders")
    Observable<GetOrdersResponse> getAllOrders(
            @Body GetAllRecordsRequest request
    );

    @POST("/getOrderItems")
    Observable<GetOrderItemsResponse> getOrderItems(
            @Body GetOrderItemsRequest request
    );


}

