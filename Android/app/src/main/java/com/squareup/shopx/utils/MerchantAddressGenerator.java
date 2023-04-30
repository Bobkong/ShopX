package com.squareup.shopx.utils;

import com.squareup.shopx.model.AllMerchantsResponse;

public class MerchantAddressGenerator {

    public static String generateMerchantAddress(AllMerchantsResponse.ShopXMerchant merchant) {
        return merchant.getAddressLine1() + "\n" +
                merchant.getLocality() + "\n" +
                merchant.getAdministrativeDistrictLevel1() + " " + merchant.getPostalCode();
    }
}
