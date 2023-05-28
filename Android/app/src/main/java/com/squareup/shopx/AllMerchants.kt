package com.squareup.shopx

import android.location.Location
import android.util.Log
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.GetMerchantDetailResponse.Item
import com.squareup.shopx.model.MerchantCart
import java.util.HashMap

object AllMerchants {

    val TAG = "AllMerchants"
    const val OFFER_SEE_ALL = 0
    const val OFFER_SEE_DISCOUNT = 1
    const val OFFER_SEE_LOYALTY = 2
    const val OFFER_SEE_DISCOUNT_LOYALTY = 3
    var allMerchants = ArrayList<ShopXMerchant>()
    var distanceLimit = Float.MAX_VALUE
    var offerType = OFFER_SEE_ALL
    var onlySeeAREnable = false

    var myLat: Float = 0.0F
    var myLng: Float = 0.0F

    var allMerchantCarts = ArrayList<MerchantCart>()

    fun getDisplayMerchants(): ArrayList<ShopXMerchant> {
        var displayMerchants = ArrayList<ShopXMerchant>()
        for (merchant in allMerchants) {
            // filter distance
            val distance = calculateDistance(merchant.lat, merchant.lng)
            Log.i(TAG, "distance from " + merchant.businessName + " is: " + distance + " miles.")
            if (distance > distanceLimit)
                continue

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

    fun getClosestMerchant(): ShopXMerchant? {
        var closestMerchant: ShopXMerchant? = null
        var distance = Double.MAX_VALUE
        for (i in 1 until allMerchants.size) {
            var currentDistance = calculateDistance(allMerchants[i].lat, allMerchants[i].lng)
            if (currentDistance < distance && (allMerchants[i].ifLoyalty == 1 || allMerchants[i].discountType.isNotEmpty())) {
                closestMerchant = allMerchants[i]
                distance = currentDistance
            }
        }
        return closestMerchant
    }

    fun getDisplayMerchants(merchantId: String): List<ShopXMerchant> {
        val merchants = getDisplayMerchants()
        if (merchantId.isEmpty() || merchantId.isBlank()) {
            return merchants
        }
        for (i in merchants.indices) {
            if (merchants[i].merchantId == merchantId) {
                merchants.add(0, merchants.removeAt(i))
            }
        }
        return merchants
    }

    private fun sortMerchants(merchants: ArrayList<ShopXMerchant>): ArrayList<ShopXMerchant> {
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

        return ArrayList(merchantsIn50Miles.plus(merchantsNotIn50Miles))
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

    fun updateItemNumber(merchant: ShopXMerchant, item: Item, number: Int) {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                merchantCart.updateItemCount(item, number)
                return
            }
        }
        val merchantCart = MerchantCart(merchant)
        merchantCart.updateItemCount(item, number)
        allMerchantCarts.add(merchantCart)
    }

    fun getPrice(merchant: ShopXMerchant): Float {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                return merchantCart.price
            }
        }
        return 0F
    }

    // get price before discount
    fun getOriginalPrice(merchant: ShopXMerchant): Float {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                return merchantCart.originalPrice
            }
        }
        return 0F
    }

    fun getCartItems(merchant: ShopXMerchant): HashMap<Item, Int> {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                return merchantCart.cartItems
            }
        }
        return HashMap()
    }

    fun clearCart(merchant: ShopXMerchant) {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                merchantCart.clearCart()
            }
        }
    }

    fun getCountOfAnItem(merchant: ShopXMerchant, item: Item): Int {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                return merchantCart.getCountOfAnItem(item)
            }
        }
        return 0
    }

    fun getTotalItemCount(merchant: ShopXMerchant): Int {
        for (merchantCart in allMerchantCarts) {
            if (merchantCart.merchant.equals(merchant)) {
                return merchantCart.totalItemCount
            }
        }
        return 0
    }


}