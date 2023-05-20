package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.AllMerchants;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.utils.MerchantAddressGenerator;
import com.squareup.shopx.utils.UIUtils;

import java.util.Objects;

public class MerchantViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView logo;
    private final TextView businessName;
    private final TextView merchantOpenDistance;
    private final TextView discountTag;
    private final TextView loyaltyTag;
    private final TextView ARTag;


    public MerchantViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.merchant_logo);
        businessName = itemView.findViewById(R.id.merchant_name);
        merchantOpenDistance = itemView.findViewById(R.id.merchant_open_distance);
        discountTag = itemView.findViewById(R.id.discount_tag);
        loyaltyTag = itemView.findViewById(R.id.loyalty_tag);
        ARTag = itemView.findViewById(R.id.AR_tag);
    }

    public void setData(AllMerchantsResponse.ShopXMerchant merchant, Activity activity, int position) {
        this.activity = activity;
        Glide.with(activity)
                .load(merchant.getLogoUrl())
                .into(logo);

        businessName.setText(merchant.getBusinessName());

        String openDistanceString = "Open · 11 AM - 9 PM · ";
        merchantOpenDistance.setText(openDistanceString + String.format("%.2f", AllMerchants.INSTANCE.calculateDistance(merchant.getLat(), merchant.getLng())) + "mile");

        if (merchant.getIfLoyalty() == 1) {
            loyaltyTag.setVisibility(View.VISIBLE);
        } else {
            loyaltyTag.setVisibility(View.GONE);
        }

        if (merchant.getDiscountType() == null || merchant.getDiscountType().isEmpty()) {
            discountTag.setVisibility(View.GONE);
        } else {
            discountTag.setVisibility(View.VISIBLE);
            if (Objects.equals(merchant.getDiscountType(), "FIXED_PERCENTAGE")) {
                discountTag.setText((int)merchant.getDiscountAmount() + "% Off");
            } else {
                discountTag.setText("$" + (int)merchant.getDiscountAmount() + " Off EA");
            }
        }

        if (merchant.getArEnable() == 1) {
            ARTag.setVisibility(View.VISIBLE);
        } else {
            ARTag.setVisibility(View.GONE);
        }

        if (position == 0) {
            RecyclerView.LayoutParams layoutParam =(RecyclerView.LayoutParams)itemView.getLayoutParams();
            layoutParam.leftMargin = UIUtils.dp2px(activity, 24);
            itemView.setLayoutParams(layoutParam);
        }

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, MerchantDetailActivity.class);
            intent.putExtra("merchant", merchant);
            activity.startActivity(intent);
        });


    }
}
