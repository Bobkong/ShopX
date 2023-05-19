package com.squareup.shopx

import android.location.Location
import android.util.Log
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.GetMerchantDetailResponse.Item
import com.squareup.shopx.model.MerchantCart

object AllMerchants {

    val TAG = "AllMerchants"
    const val OFFER_SEE_ALL = 0
    const val OFFER_SEE_DISCOUNT = 1
    const val OFFER_SEE_LOYALTY = 2
    const val OFFER_SEE_DISCOUNT_LOYALTY = 3
    var allMerchants = ArrayList<ShopXMerchant>()
    var distanceLimit = 15.0F
    var offerType = OFFER_SEE_ALL
    var onlySeeAREnable = false

    var myLat: Float = 0.0F
    var myLng: Float = 0.0F

    var allMerchantCarts = ArrayList<MerchantCart>()

    fun getDisplayMerchants(): List<ShopXMerchant> {
        var displayMerchants = ArrayList<ShopXMerchant>()
        for (merchant in allMerchants) {
            // filter distance
            if (distanceLimit != Float.MAX_VALUE) {
                val distance = calculateDistance(merchant.lat, merchant.lng)
                Log.i(TAG, "distance from " + merchant.businessName + " is: " + distance + " miles.")
                if (distance > distanceLimit)
                    continue
            }

            // filter ar
            if (onlySeeAREnable) {
                if (merchant.arEnable == 0)
                    continue
            }

            // filter offers type
            if (offerType == OFFER_SEE_LOYALTY) {
                if (merchant.ifLoyalty == 0)
                    continue
            } else if (offerType == OFFER_SEE_DISCOUNT) {
                if (merchant.discountType.isEmpty())
                    continue
            } else if (offerType == OFFER_SEE_DISCOUNT_LOYALTY) {
                if (merchant.discountType.isEmpty() || merchant.ifLoyalty == 0)
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

    fun calculateDistance(merchantLat: Float, merchantLng: Float): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            myLat.toDouble(), myLng.toDouble(),
            merchantLat.toDouble(), merchantLng.toDouble(), results
        )
        return results[0] / 1609.0
    }

    fun getMerchant(accessToken: String?): ShopXMerchant? {
        accessToken?.let {
            for (merchant in allMerchants) {
                if (merchant.accessToken == accessToken) {
                    return merchant
                }
            }
        }
        return null
    }

    fun addToCart(merchant: ShopXMerchant, item: Item) {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                merchantCart.addToCart(item)
                return
            }
        }
        val merchantCart = MerchantCart(merchant)
        merchantCart.addToCart(item)
        allMerchantCarts.add(merchantCart)
    }

    fun deleteFromCart(merchant: ShopXMerchant, item: Item) {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                merchantCart.deleteFromCart(item)
                return
            }
        }
    }

    fun getPrice(merchant: ShopXMerchant): Float {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                return merchantCart.price
            }
        }
        return 0F
    }

    fun getCartItems(merchant: ShopXMerchant): ArrayList<Item> {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                return merchantCart.cartItems
            }
        }
        return ArrayList()
    }


}