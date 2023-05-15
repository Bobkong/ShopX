package com.squareup.shopx.viewholder;

import static com.squareup.shopx.utils.MerchantAddressGenerator.generateMerchantAddress;

import android.annotation.SuppressLint;
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

import java.util.Objects;

public class MerchantHeaderViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView merchantLogo;
    private final TextView merchantName;
    private final TextView merchantOpenDistance;
    private final TextView merchantLocation;
    private final TextView discountTag;
    private final TextView loyaltyTag;
    private final TextView ARTag;

    public MerchantHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        merchantLogo = itemView.findViewById(R.id.merchant_logo);
        merchantName = itemView.findViewById(R.id.merchant_name);
        merchantOpenDistance = itemView.findViewById(R.id.merchant_open_distance);
        merchantLocation = itemView.findViewById(R.id.merchant_location);
        discountTag = itemView.findViewById(R.id.discount_tag);
        loyaltyTag = itemView.findViewById(R.id.loyalty_tag);
        ARTag = itemView.findViewById(R.id.AR_tag);
    }

    @SuppressLint("SetTextI18n")
    public void setData(Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        this.activity = activity;
        Glide.with(activity)
                .load(merchantInfo.getLogoUrl())
                .into(merchantLogo);
        merchantName.setText(merchantInfo.getBusinessName());
        String openDistanceString = "Open · 11 AM - 9 PM · ";
        merchantOpenDistance.setText(openDistanceString + String.format("%.2f", AllMerchants.INSTANCE.calculateDistance(merchantInfo.getLat(), merchantInfo.getLng())) + "mile");
        merchantLocation.setText(generateMerchantAddress(merchantInfo));
        if (merchantInfo.getIfLoyalty() == 1) {
            loyaltyTag.setVisibility(View.VISIBLE);
        } else {
            loyaltyTag.setVisibility(View.GONE);
        }

        if (merchantInfo.getDiscountType() == null || merchantInfo.getDiscountType().isEmpty()) {
            discountTag.setVisibility(View.GONE);
        } else {
            discountTag.setVisibility(View.VISIBLE);
            if (Objects.equals(merchantInfo.getDiscountType(), "FIXED_PERCENTAGE")) {
                discountTag.setText((int)merchantInfo.getDiscountAmount() + "% Off");
            } else {
                discountTag.setText("$" + (int)merchantInfo.getDiscountAmount() + " Off EA");
            }
        }

        if (merchantInfo.getArEnable() == 1) {
            ARTag.setVisibility(View.VISIBLE);
        } else {
            ARTag.setVisibility(View.GONE);
        }
    }
}
