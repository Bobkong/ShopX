package com.squareup.shopx.widget

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.CartUpdateEvent
import com.squareup.shopx.model.GetMerchantDetailResponse
import org.greenrobot.eventbus.EventBus


class ItemBottomDialog @JvmOverloads constructor(context: Context, style: Int) :
    BottomSheetDialog(context, style) {
    private val TAG = "ItemBottomDialog"
    companion object {
        val FROM_MERCHANT_DETAIL = 0
        val FROM_CART = 1
    }


    fun init(activity: Activity, from: Int, item: GetMerchantDetailResponse.Item, merchantInfo: ShopXMerchant) {
        val dialogView = layoutInflater.inflate(R.layout.merchant_item_bottom_sheet, null, false)
        this.setContentView(dialogView)

        try {
            // hack bg color of the BottomSheetDialog
            val parent = dialogView!!.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var itemInitialCount = if (from == FROM_MERCHANT_DETAIL) {
            1
        } else {
            AllMerchants.getCountOfAnItem(merchantInfo, item)
        }

        val itemImage = dialogView.findViewById<ImageView>(R.id.item_image)
        val itemName = dialogView.findViewById<TextView>(R.id.item_name)
        val itemDesc = dialogView.findViewById<TextView>(R.id.item_desc)
        val itemOriginalPrice = dialogView.findViewById<TextView>(R.id.original_price_text)
        val itemOriginalPriceLl = dialogView.findViewById<LinearLayout>(R.id.original_price_ll)
        val itemOriginalDiscountTag = dialogView.findViewById<TextView>(R.id.discount_tag)
        val itemRealPrice = dialogView.findViewById<TextView>(R.id.real_price)
        val addItem = dialogView.findViewById<ImageView>(R.id.add_item)
        val subItem = dialogView.findViewById<ImageView>(R.id.sub_item)
        val itemCountLl = dialogView.findViewById<LinearLayout>(R.id.item_count)
        val itemCount = dialogView.findViewById<TextView>(R.id.item_count_text)
        val actionButton = dialogView.findViewById<TextView>(R.id.action_button)

        itemCount.text = itemInitialCount.toString()
        addItem.setOnClickListener {
            itemInitialCount += 1
            itemCount.text = itemInitialCount.toString()
            subItem.setImageDrawable(activity.resources.getDrawable(R.drawable.enable_sub_item))
            if (from == FROM_MERCHANT_DETAIL) {
                actionButton.text = "Add $itemInitialCount to Cart"
            } else {
                actionButton.text = "Update Cart"
            }
        }

        subItem.setOnClickListener {
            if (from == FROM_MERCHANT_DETAIL) {
                if (itemInitialCount == 1) {
                    // from merchant, can't delete to 0
                    return@setOnClickListener
                }
                itemInitialCount -= 1
                itemCount.text = itemInitialCount.toString()
                actionButton.text = "Add $itemInitialCount to Cart"
                if (itemInitialCount == 1) {
                    subItem.setImageDrawable(activity.resources.getDrawable(R.drawable.unable_sub_item))
                } else {
                    subItem.setImageDrawable(activity.resources.getDrawable(R.drawable.enable_sub_item))
                }
            } else {
                if (itemInitialCount == 0) {
                    // from merchant, can't delete to -1
                    return@setOnClickListener
                }
                itemInitialCount -= 1
                itemCount.text = itemInitialCount.toString()
                actionButton.text = "Update Cart"
                if (itemInitialCount == 0) {
                    subItem.setImageDrawable(activity.resources.getDrawable(R.drawable.unable_sub_item))
                } else {
                    subItem.setImageDrawable(activity.resources.getDrawable(R.drawable.enable_sub_item))
                }
            }

        }

        if (item.pricingType == "FIXED_PRICING") {
            if (from == FROM_MERCHANT_DETAIL) {
                actionButton.text = "Add 1 to Cart"
            } else {
                actionButton.text = "Update Cart"
            }
        } else {
            actionButton.text = "Booking"
        }


        actionButton.setOnClickListener {
            if (item.pricingType == "FIXED_PRICING") {
                var updateCount = itemInitialCount
                if (from == FROM_MERCHANT_DETAIL) {
                    // can only add when from merchant detail
                    updateCount = itemInitialCount + AllMerchants.getCountOfAnItem(merchantInfo, item)
                }
                AllMerchants.updateItemNumber(merchantInfo, item, updateCount)
                EventBus.getDefault().post(CartUpdateEvent(merchantInfo));
                dismiss()
            } else {
                showBookDialog(activity)
            }
        }

        if (item.pricingType == "FIXED_PRICING") {
            itemRealPrice.text = "$" + String.format(
                "%.2f",
                item.getItemDiscountPrice(merchantInfo) / 100.0
            )
            if (item.getItemDiscountPrice(merchantInfo) == item.itemPrice.toFloat()) {
                itemOriginalPriceLl.visibility = View.GONE
                itemRealPrice.setTextAppearance(activity, R.style.title_medium)
            } else {
                itemOriginalPriceLl.visibility = View.VISIBLE
                itemOriginalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                itemOriginalPrice.text = "$" + String.format(
                    "%.2f",
                    item.itemPrice / 100.0
                )
                if (merchantInfo.discountType == "FIXED_PERCENTAGE") {
                    itemOriginalDiscountTag.text = merchantInfo.discountAmount.toInt().toString() + "% Off"
                } else {
                    itemOriginalDiscountTag.text = "$" + merchantInfo.discountAmount.toInt() + " Off EA"
                }
            }
        } else {
            itemCountLl.visibility = View.GONE
            itemOriginalPriceLl.visibility = View.GONE
            itemRealPrice.text = "Ask merchants for price"
            itemRealPrice.setTextAppearance(activity, R.style.label_large)
            itemRealPrice.setTextColor(activity.resources.getColor(R.color.black_50))
        }

        Glide.with(activity)
            .load(item.itemImage)
            .into(itemImage)
        itemName.text = item.itemName
        if (item.itemDescription.isNullOrEmpty()) {
            itemDesc.visibility = View.GONE
        } else {
            itemDesc.text = item.itemDescription
            itemDesc.visibility = View.VISIBLE
        }
        show()
    }

    fun showBookDialog(activity: Activity) {
        val customDialog = CustomDialog(activity, R.layout.customize_dialog)
        val dialogTitle = customDialog.findViewById<TextView>(R.id.dialog_title)
        val dialogDesc = customDialog.findViewById<TextView>(R.id.dialog_desc)
        val rightAction = customDialog.findViewById<TextView>(R.id.action_right)
        val leftActivity = customDialog.findViewById<TextView>(R.id.action_left)
        leftActivity.visibility = View.GONE
        rightAction.text = "OK"
        dialogTitle.text = "Reminder"
        dialogDesc.text = "The booking feature is currently under development. Please stay tuned!"
        customDialog.show()

        rightAction.setOnClickListener {
            customDialog.dismiss()
        }
    }


}