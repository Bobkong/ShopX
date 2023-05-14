package com.squareup.shopx.utils;

import com.squareup.shopx.model.AllMerchantsResponse;

public class MerchantAddressGenerator {

    public static String generateMerchantAddress(AllMerchantsResponse.ShopXMerchant merchant) {
        return merchant.getAddressLine1() + ", " +
                merchant.getLocality() + ", " +
                merchant.getAdministrativeDistrictLevel1() + " " + merchant.getPostalCode();
    }
}
