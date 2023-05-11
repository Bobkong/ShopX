package com.squareup.shopx.netservice.SquareAPI;



import com.squareup.shopx.model.BatchRetrieveRequest;
import com.squareup.shopx.model.CreateLoyaltyRewardRequest;
import com.squareup.shopx.model.CreateLoyaltyRewardResponse;
import com.squareup.shopx.model.CreateOrderRequest;
import com.squareup.shopx.model.CreateOrderResponse;
import com.squareup.shopx.model.Customer;
import com.squareup.shopx.model.CustomerResponse;
import com.squareup.shopx.model.LoyaltyProgramResponse;
import com.squareup.shopx.model.ObjectsResponse;
import com.squareup.shopx.model.LocationResponse;
import com.squareup.shopx.model.MerchantResponse;
import com.squareup.shopx.netservice.HttpResultFunc;
import com.squareup.shopx.netservice.SquareServiceManager;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class SquareApiService {
    private static SquareApiService instance;
    private final MerchantApi merchantApi;
    private final LocationApi locationApi;
    private final CatalogApi catalogApi;
    private final LoyaltyApi loyaltyApi;
    private final CustomerApi customerApi;
    private final OrderApi orderApi;
    public static synchronized SquareApiService getInstance(String authorizationToken){
        if (instance == null)
            instance=new SquareApiService();
        // set access token
        SquareServiceManager.getInstance().setAuthorizationToken(authorizationToken);
        return instance;
    }

    SquareApiService() {
        merchantApi = SquareServiceManager.getInstance().create(MerchantApi.class);
        locationApi = SquareServiceManager.getInstance().create(LocationApi.class);
        catalogApi = SquareServiceManager.getInstance().create(CatalogApi.class);
        loyaltyApi = SquareServiceManager.getInstance().create(LoyaltyApi.class);
        customerApi = SquareServiceManager.getInstance().create(CustomerApi.class);
        orderApi = SquareServiceManager.getInstance().create(OrderApi.class);
    }


    public Observable<MerchantResponse> retrieveMerchant(String merchantId){
        return merchantApi.retrieveMerchant(merchantId)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<LocationResponse> retrieveLocation(String merchantId){
        return locationApi.retrieveLocation(merchantId)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ObjectsResponse> retrieveAllItems(){
        return catalogApi.retrieveItems("ITEM")
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ObjectsResponse> batchRetrieveObjects(ArrayList<String> objectIds){
        return catalogApi.batchRetrieveObjects(new BatchRetrieveRequest(objectIds))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<CustomerResponse> createCustomer(Customer customer){
        return customerApi.createCustomer(customer)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<CreateOrderResponse> createOrder(CreateOrderRequest createOrderRequest){
        return orderApi.createOrder(createOrderRequest)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<CreateLoyaltyRewardResponse> createLoyaltyReward(CreateLoyaltyRewardRequest createLoyaltyRewardRequest){
        return loyaltyApi.createLoyaltyReward(createLoyaltyRewardRequest)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<CreateOrderResponse> retrieveOrder(String orderId){
        return orderApi.retrieveOrder(orderId)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }


}
