package com.squareup.shopx.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.activity.ARItemActivity
import com.squareup.shopx.activity.OrderActivity
import com.squareup.shopx.adapter.CartItemListAdapter
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.CartCallback
import com.squareup.shopx.model.GetLoyaltyInfoResponse


class CartBottomDialog @JvmOverloads constructor(context: Context, style: Int) :
    BottomSheetDialog(context, style),
    CartCallback {
    private val TAG = "CartBottomDialog"
    companion object {
    }

    private var subtotalPrice: TextView? = null
    private var merchantInfo: ShopXMerchant? = null

    @SuppressLint("SetTextI18n")
    fun init(activity: Activity, merchantInfo: ShopXMerchant, customerLoyaltyResponse: GetLoyaltyInfoResponse?) {
        this.merchantInfo = merchantInfo
        val dialogView = layoutInflater.inflate(R.layout.cart_bottom_sheet, null, false)
        this.setContentView(dialogView)

        try {
            // hack bg color of the BottomSheetDialog
            val parent = dialogView!!.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val itemList = dialogView.findViewById<RecyclerView>(R.id.item_list)
        subtotalPrice = dialogView.findViewById<TextView>(R.id.subtotal_price)
        val gotoCheckout = dialogView.findViewById<TextView>(R.id.go_to_checkout)

        itemList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        itemList?.adapter = CartItemListAdapter(activity, merchantInfo, this, CartItemListAdapter.FROM_OTHER)

        subtotalPrice?.text = "$ " + String.format(
            "%.2f",
            AllMerchants.getPrice(merchantInfo) / 100.0
        )

        gotoCheckout.setOnClickListener {
            val intent = Intent(activity, OrderActivity::class.java)
            intent.putExtra("merchant", merchantInfo)

            if (merchantInfo.ifLoyalty == 1 && customerLoyaltyResponse != null && customerLoyaltyResponse.isEnrolled == 1) {
                intent.putExtra("loyalty", customerLoyaltyResponse)
            }
            activity.startActivity(intent)
            dismiss()
            if (activity is ARItemActivity) {
                activity.finish()
            }
        }

        show()
    }

    override fun dismissCart() {
        this.dismiss()
    }

    override fun updatePrice() {
        subtotalPrice?.text = "$ " + String.format(
            "%.2f",
            AllMerchants.getPrice(merchantInfo!!) / 100.0
        )
    }


}