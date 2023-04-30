package com.squareup.shopx

import android.location.Location
import android.util.Log
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant

object AllMerchants {

    val TAG = "AllMerchants"
    var allMerchants = ArrayList<ShopXMerchant>()
    var distanceLimit = 50.0F
    var onlySeeAREnable = false
    var onlySeeDiscount = false
    var onlySeeLoyalty = false

    var myLat: Float = 0.0F
    var myLng: Float = 0.0F

    fun getDisplayMerchants(): List<ShopXMerchant> {
        var displayMerchants = ArrayList<ShopXMerchant>()
        for (merchant in allMerchants) {
            if (distanceLimit != Float.MAX_VALUE) {
                val distance = calculateDistance(merchant.lat, merchant.lng)
                Log.i(TAG, "distance from " + merchant.businessName + " is: " + distance + " miles.")
                if (distance > distanceLimit)
                    continue
            }

            if (onlySeeAREnable) {
                if (merchant.arEnable == 0)
                    continue
            }

            if (onlySeeDiscount) {
                if (merchant.discountType.isEmpty())
                    continue
            }

            if (onlySeeLoyalty) {
                if (merchant.ifLoyalty == 0)
                    continue
            }

            displayMerchants.add(merchant)
        }

        return sortMerchants(displayMerchants)

    }

    private fun sortMerchants(merchants: ArrayList<ShopXMerchant>): List<ShopXMerchant> {
        var merchantsIn50Miles = ArrayList<ShopXMerchant>()
        var merchantsNotIn50Miles = ArrayList<ShopXMerchant>()

        for (merchant in merchants) {
            if (calculateDistance(merchant.lat, merchant.lng) > 50.0) {
                merchantsNotIn50Miles.add(merchant)
            } else {
                merchantsIn50Miles.add(merchant)
            }
        }

        merchantsIn50Miles.sortByDescending {
            it.ifLoyalty
        }

        merchantsIn50Miles.sortByDescending {
            it.discountAmount
        }

        merchantsNotIn50Miles.sortByDescending {
            it.ifLoyalty
        }

        merchantsNotIn50Miles.sortByDescending {
            it.discountAmount
        }

        return merchantsIn50Miles.plus(merchantsNotIn50Miles)
    }

    private fun calculateDistance(merchantLat: Float, merchantLng: Float): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            myLat.toDouble(), myLng.toDouble(),
            merchantLat.toDouble(), merchantLng.toDouble(), results
        )
        return results[0] / 1609.0
    }


}