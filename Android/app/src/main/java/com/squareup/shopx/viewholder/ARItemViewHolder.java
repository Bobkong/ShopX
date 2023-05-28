package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.ARItemActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;

public class ARItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView logo;
    private final TextView itemName;
    private final TextView itemPrice;

    public ARItemViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.image);
        itemName = itemView.findViewById(R.id.item_name);
        itemPrice = itemView.findViewById(R.id.item_price);
    }

    public void setData(GetMerchantDetailResponse.Item item, Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        Glide.with(activity)
                .load(item.getItemImage())
                .into(logo);

        itemName.setText(item.getItemName());
        itemPrice.setText("$" + String.format("%.2f", item.getItemDiscountPrice(merchantInfo) / 100.0));
        itemView.setOnClickListener(view -> {
            ((ARItemActivity)activity).showAddToCartDialog(item);
        });


    }
}
