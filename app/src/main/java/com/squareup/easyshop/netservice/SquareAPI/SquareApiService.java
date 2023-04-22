package com.squareup.easyshop.netservice.SquareAPI;



import com.squareup.easyshop.model.LocationResponse;
import com.squareup.easyshop.model.MerchantResponse;
import com.squareup.easyshop.netservice.HttpResultFunc;
import com.squareup.easyshop.netservice.SquareServiceManager;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class SquareApiService {
    private String authorizationToken = "";
    private static SquareApiService instance;
    private final MerchantApi merchantApi;
    private final LocationApi locationApi;
    public static synchronized SquareApiService getInstance(String authorizationToken){
        if (instance == null)
            instance=new SquareApiService(authorizationToken);
        // set access token
        SquareServiceManager.getInstance().setAuthorizationToken(authorizationToken);
        return instance;
    }

    SquareApiService(String authorizationToken) {
        this.authorizationToken = authorizationToken;
        merchantApi = SquareServiceManager.getInstance().create(MerchantApi.class);
        locationApi = SquareServiceManager.getInstance().create(LocationApi.class);
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


}
