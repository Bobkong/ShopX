package com.squareup.shopx.viewholder

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.shopx.AllMerchants
import com.squareup.shopx.R
import com.squareup.shopx.activity.MerchantDetailActivity
import com.squareup.shopx.activity.OrderDetailActivity
import com.squareup.shopx.model.PlaceOrderRequest
import com.squareup.shopx.utils.DateUtil
import com.squareup.shopx.utils.UIUtils

class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setData(activity: Activity, order: PlaceOrderRequest, isLast: Boolean) {
        val merchantLogo = itemView.findViewById<ImageView>(R.id.merchant_logo)
        val merchantName = itemView.findViewById<TextView>(R.id.merchant_name)
        val itemCountPrice = itemView.findViewById<TextView>(R.id.item_count_price)
        val timeStatus = itemView.findViewById<TextView>(R.id.time_status)
        val reorder = itemView.findViewById<TextView>(R.id.reorder)

        Glide.with(activity)
            .load(AllMerchants.getMerchant(order.accessToken)?.logoUrl)
            .into(merchantLogo)

        merchantName.text = AllMerchants.getMerchant(order.accessToken)?.businessName
        itemCountPrice.text = order.itemCount.toString() + " items · $ " + (order.finalPrice / 100f)
        timeStatus.text = order.timeString + " · Completed"

        AllMerchants.getMerchant(order.accessToken)?.let { merchant ->
            reorder.setOnClickListener {
                val intent = Intent(
                    activity,
                    MerchantDetailActivity::class.java
                )
                intent.putExtra("merchant", merchant)
                activity.startActivity(intent)
            }

            itemView.setOnClickListener {
                val intent = Intent(
                    activity,
                    OrderDetailActivity::class.java
                )
                intent.putExtra("merchantInfo", merchant)
                intent.putExtra("orderInfo", order)
                activity.startActivity(intent)
            }
        }

        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        if (isLast) {
            layoutParams.bottomMargin = UIUtils.dp2px(activity, 120f)
        } else {
            layoutParams.bottomMargin = 0;
        }
        itemView.layoutParams = layoutParams
    }


}