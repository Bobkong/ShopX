package com.squareup.oauthexample;

import com.google.gson.Gson;
import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.api.LocationsApi;
import com.squareup.square.api.LoyaltyApi;
import com.squareup.square.api.MerchantsApi;
import com.squareup.square.models.CatalogObject;
import com.squareup.square.models.LoyaltyProgramRewardTier;

import java.util.ArrayList;

public class RetrieveMerchant {

    public static void uploadMerchantToServer(String merchantId, String accessToken) {

        SquareClient client = new SquareClient.Builder()
                .accessToken(accessToken)
                .environment(Environment.SANDBOX)
                .build();

        MerchantsApi merchantsApi = client.getMerchantsApi();
        merchantsApi.retrieveMerchantAsync(merchantId).thenAccept(result -> {
                    if (result == null) {
                        return;
                    }
                    getLocationDetail(client, merchantId, result.getMerchant().getMainLocationId());
                })
                .exceptionally(exception -> {
                    System.out.println("Failed to make the request");
                    System.out.println(String.format("Exception: %s", exception.getMessage()));
                    return null;
                });
    }

    public static void getLocationDetail(SquareClient client, String merchantId, String locationId) {
        LocationsApi locationsApi = client.getLocationsApi();
        locationsApi.retrieveLocationAsync(locationId).thenAccept(result -> {
            if (result == null) {
                return;
            }
            Gson gson=new Gson();

            String googleAddress = "address=" + result.getLocation().getAddress().getAddressLine1().replace(" ", "+") + ",+" + result.getLocation().getAddress().getLocality().replace(" ", "+") + ",+" + result.getLocation().getAddress().getAdministrativeDistrictLevel1();
            String apiKey = "key=AIzaSyCDvZl4_pp1EZLn4V3jazYa9NctjLPfNT8";
            String geoCodingRequest = HttpHelper.sendGet("https://maps.googleapis.com/maps/api/geocode/json", googleAddress + "&" + apiKey);
            GoogleGeoCoding googleGeoCoding = gson.fromJson(geoCodingRequest, GoogleGeoCoding.class);
            System.out.print(googleAddress + "&" + apiKey);

            Merchant.Address address = new Merchant.Address(result.getLocation().getAddress().getAddressLine1(),
                    result.getLocation().getAddress().getLocality(),
                    result.getLocation().getAddress().getAdministrativeDistrictLevel1(),
                    result.getLocation().getAddress().getPostalCode());


            Merchant merchant = new Merchant(merchantId, client.getAccessToken(), result.getLocation().getBusinessName(), result.getLocation().getLogoUrl(), address, googleGeoCoding.getResults().get(0).geometry.location.lat, googleGeoCoding.getResults().get(0).geometry.location.lng, result.getLocation().getId());
            getLoyaltyProgram(client, merchant);

        }).exceptionally(exception -> {
                    System.out.println("Failed to make the request");
                    System.out.println(String.format("Exception: %s", exception.getMessage()));
                    return null;
                });



    }

    public static void getLoyaltyProgram(SquareClient client, Merchant merchant) {
        LoyaltyApi loyaltyApi = client.getLoyaltyApi();
        loyaltyApi.retrieveLoyaltyProgramAsync("main")
                .thenAccept(result -> {

                    ArrayList<String> loyalPricingRules = new ArrayList<>();
                    if (result != null && result.getProgram() != null) {
                        merchant.setIfLoyalty(true);
                        // record the loyalty pricing rule id
                        for (LoyaltyProgramRewardTier rewardTire: result.getProgram().getRewardTiers()) {
                            loyalPricingRules.add(rewardTire.getPricingRuleReference().getObjectId());
                        }
                    } else {
                        System.out.print("The merchant doesn't have loyalty program.");
                        merchant.setIfLoyalty(false);
                    }
                    getPricingRules(client, merchant, loyalPricingRules);

                }).exceptionally(exception -> {
                    System.out.println("Failed to make the request");
                    System.out.println(String.format("Exception: %s", exception.getMessage()));
                    System.out.print("The merchant doesn't have loyalty program.");
                    merchant.setIfLoyalty(false);
                    getPricingRules(client, merchant, new ArrayList<>());
                    return null;
                });
    }

    public static void getPricingRules(SquareClient client, Merchant merchant, ArrayList<String> loyalPricingRules) {
        CatalogApi catalogApi = client.getCatalogApi();
        catalogApi.listCatalogAsync(
                        null,
                        "pricing_rule",
                        null)
                .thenAccept(result -> {
                    Gson gson=new Gson();
                    if (result == null) {

                        return;
                    }
                    ArrayList<String> discountId = new ArrayList<>();
                    if (result.getObjects() != null && result.getObjects().size() != 0) {
                        for (CatalogObject pricingRule:
                                result.getObjects()) {
                            if (!loyalPricingRules.contains(pricingRule.getId())) {
                                discountId.add(pricingRule.getId());
                            }
                        }
                    }

                    if (discountId.size() == 0) {
                        System.out.print("The merchant doesn't have discount.");
                        // don't have discount, just send the post request
                        String json = gson.toJson(merchant);
                        String sendPost = HttpHelper.sendPost("http://172.190.15.5:8900/saveMerchant", json);
                        System.out.print(sendPost);
                    } else {
                        getDiscountObject(client, merchant, discountId.get(0));
                    }
                })
                .exceptionally(exception -> {
                    System.out.println("Failed to make the request");
                    System.out.println(String.format("Exception: %s", exception.getMessage()));

                    Gson gson=new Gson();
                    System.out.print("The merchant doesn't have discount.");
                    // don't have discount, just send the post request
                    String json = gson.toJson(merchant);
                    String sendPost = HttpHelper.sendPost("http://172.190.15.5:8900/saveMerchant", json);
                    System.out.print(sendPost);
                    return null;
                });
    }

    public static void getDiscountObject(SquareClient client, Merchant merchant, String discountId) {
        CatalogApi catalogApi = client.getCatalogApi();
        catalogApi.retrieveCatalogObjectAsync(
                        discountId,
                        true,
                        null)
                .thenAccept(result -> {
                    if (result == null) {
                        return;
                    }

                    for (CatalogObject catalogObject :
                            result.getRelatedObjects()) {
                        if (catalogObject.getType().equals("PRODUCT_SET")) {
                            StringBuilder discountProducts = new StringBuilder();
                            for (String id:
                                    catalogObject.getProductSetData().getProductIdsAny()) {
                                discountProducts.append(id).append("|");
                            }
                            merchant.setDiscountProducts(discountProducts.toString());
                        }
                        if (catalogObject.getType().equals("DISCOUNT")) {
                            Merchant.Discount discount = null;
                            if (catalogObject.getDiscountData().getDiscountType().equals("FIXED_AMOUNT")) {
                                discount = new Merchant.Discount("FIXED_AMOUNT", (float) (catalogObject.getDiscountData().getAmountMoney().getAmount() / 100.0));
                            } else if (catalogObject.getDiscountData().getDiscountType().equals("FIXED_PERCENTAGE")) {
                                discount = new Merchant.Discount("FIXED_PERCENTAGE", Float.parseFloat(catalogObject.getDiscountData().getPercentage()));
                            }
                            merchant.setDiscount(discount);
                        }
                    }

                    Gson gson=new Gson();
                    // just send the post request
                    String json = gson.toJson(merchant);
                    String sendPost = HttpHelper.sendPost("http://172.190.15.5:8900/saveMerchant", json);
                    System.out.print(sendPost);
                })
                .exceptionally(exception -> {
                    System.out.println("Failed to make the request");
                    System.out.println(String.format("Exception: %s", exception.getMessage()));
                    return null;
                });
    }
}
