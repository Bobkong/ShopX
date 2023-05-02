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
import com.squareup.shopx.R;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.utils.MerchantAddressGenerator;
import com.squareup.shopx.utils.UIUtils;

public class MerchantViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView logo;
    private final TextView businessName;
    private final TextView businessAddress;

    public MerchantViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.logo);
        businessName = itemView.findViewById(R.id.business_name);
        businessAddress = itemView.findViewById(R.id.business_address);
    }

    public void setData(AllMerchantsResponse.ShopXMerchant merchant, Activity activity, boolean isLast) {
        this.activity = activity;
        Glide.with(activity)
                .load(merchant.getLogoUrl())
                .into(logo);

        businessName.setText(merchant.getBusinessName());
        businessAddress.setText(MerchantAddressGenerator.generateMerchantAddress((merchant)));

        if (isLast) {
            RecyclerView.LayoutParams layoutParam =(RecyclerView.LayoutParams)itemView.getLayoutParams();
            layoutParam.rightMargin = (UIUtils.getWidth(activity) - UIUtils.dp2px(activity, 300)) / 2;
        }

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, MerchantDetailActivity.class);
            intent.putExtra("accessToken", merchant.getAccessToken());
            activity.startActivity(intent);
        });


    }
}
