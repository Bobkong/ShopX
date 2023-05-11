package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.AllMerchants;
import com.squareup.shopx.R;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.CartUpdateEvent;
import com.squareup.shopx.model.GetMerchantDetailResponse;

import org.greenrobot.eventbus.EventBus;

public class OrderItemViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView logo;
    private final TextView name;
    private final TextView description;
    private final TextView discountPrice;

    public OrderItemViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.image);
        name = itemView.findViewById(R.id.name);
        description = itemView.findViewById(R.id.description);
        discountPrice = itemView.findViewById(R.id.discount_price);
    }

    public void setData(GetMerchantDetailResponse.Item item, Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        this.activity = activity;
        Glide.with(activity)
                .load(item.getItemImage())
                .into(logo);

        name.setText(item.getItemName());
        description.setText(item.getItemDescription());

        if (item.getItemDiscountPrice(merchantInfo) == item.getItemPrice()) {
            discountPrice.setText(String.valueOf(item.getItemPrice()));
        } else {
            discountPrice.setText(String.valueOf(item.getItemDiscountPrice(merchantInfo)));
        }



    }
}
