package com.squareup.shopx.netservice.ShopXAPI;


import com.squareup.shopx.model.AddCustomerResponse;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.Customer;
import com.squareup.shopx.model.EnrollLoyaltyRequest;
import com.squareup.shopx.model.EnrollLoyaltyResponse;
import com.squareup.shopx.model.GeneralResponse;
import com.squareup.shopx.model.GetAllLoyaltyRecordsRequest;
import com.squareup.shopx.model.GetAllLoyaltyRecordsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoRequest;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.model.GetMerchantDetailRequest;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.model.LoginRequest;
import com.squareup.shopx.model.PlaceOrderRequest;
import com.squareup.shopx.model.ShopXCustomer;
import com.squareup.shopx.model.VerifyPhoneRequest;
import com.squareup.shopx.netservice.HttpResultFunc;
import com.squareup.shopx.netservice.ShopXServiceManager;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ShopXApiService {
    private static ShopXApiService instance;
    public static synchronized ShopXApiService getInstance(){
        if (instance == null)
            instance=new ShopXApiService();

        return instance;
    }

    private final CustomerApi customerApi = ShopXServiceManager.getInstance().create(CustomerApi.class);
    private final MerchantApi merchantApi = ShopXServiceManager.getInstance().create(MerchantApi.class);
    private final LoyaltyApi loyaltyApi = ShopXServiceManager.getInstance().create(LoyaltyApi.class);
    private final OrderApi orderApi = ShopXServiceManager.getInstance().create(OrderApi.class);

    public Observable<AddCustomerResponse> addCustomer(String phone, String nickname, String password){
        return customerApi.createCustomer(new ShopXCustomer(phone, nickname, 0, password))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> checkCustomer(String phone){
        return customerApi.checkCustomer(new ShopXCustomer(phone, null, 0, null))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> verifyPhone(String phoneNumber){
        return customerApi.verifyPhone(new VerifyPhoneRequest(phoneNumber))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> login(String phoneNumber){
        return customerApi.login(new LoginRequest(phoneNumber))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<AllMerchantsResponse> getAllMerchants(){
        return merchantApi.getAllMerchants()
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GetMerchantDetailResponse> getMerchantDetail(String accessToken, String contact){
        return merchantApi.getMerchantDetail(new GetMerchantDetailRequest(accessToken, contact))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GetLoyaltyInfoResponse> getLoyaltyinfo(String accessToken, String contact){
        return loyaltyApi.getLoyaltyInfo(new GetLoyaltyInfoRequest(contact, accessToken))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<EnrollLoyaltyResponse> enrollLoyalty(String accessToken, String contact, String programId){
        return loyaltyApi.enrollLoyaltyResponse(new EnrollLoyaltyRequest(contact, accessToken, programId))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GetAllLoyaltyRecordsResponse> getAllLoyaltyRecordsResponse(String contact){
        return loyaltyApi.getAllLoyaltyRecords(new GetAllLoyaltyRecordsRequest(contact))
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<GeneralResponse> placeOrder(PlaceOrderRequest request){
        return orderApi.placeOrder(request)
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }


}
