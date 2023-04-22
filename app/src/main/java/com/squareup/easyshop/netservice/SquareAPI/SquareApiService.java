package com.squareup.easyshop.netservice.SquareAPI;



import com.squareup.easyshop.model.BatchRetrieveRequest;
import com.squareup.easyshop.model.LoyaltyProgramResponse;
import com.squareup.easyshop.model.ObjectsResponse;
import com.squareup.easyshop.model.LocationResponse;
import com.squareup.easyshop.model.MerchantResponse;
import com.squareup.easyshop.netservice.HttpResultFunc;
import com.squareup.easyshop.netservice.SquareServiceManager;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class SquareApiService {
    private static SquareApiService instance;
    private final MerchantApi merchantApi;
    private final LocationApi locationApi;
    private final CatalogApi catalogApi;
    private final LoyaltyApi loyaltyApi;
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

    public Observable<LoyaltyProgramResponse> retrieveLoyaltyProgram(){
        return loyaltyApi.retrieveLoyaltyProgram("main")
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }


}
