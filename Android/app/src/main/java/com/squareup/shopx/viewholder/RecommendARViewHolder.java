package com.squareup.shopx.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.MainFragment;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.utils.UIUtils;

public class RecommendARViewHolder extends RecyclerView.ViewHolder {

    private final ImageView merchantLogo;
    private final TextView recommendMerchantName;

    public RecommendARViewHolder(@NonNull View itemView) {
        super(itemView);
        merchantLogo = itemView.findViewById(R.id.merchant_logo);
        recommendMerchantName = itemView.findViewById(R.id.merchant_name);
    }

    public void setData(MainFragment fragment, AllMerchantsResponse.ShopXMerchant merchantInfo, boolean ifFirst, boolean ifLast) {
        if (!fragment.isAdded()) {
            return;
        }

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();

        if (ifFirst) {
            layoutParams.leftMargin = UIUtils.dp2px(fragment.requireActivity(), 24f);
        } else {
            layoutParams.leftMargin = 0;
        }
        if (ifLast) {
            layoutParams.rightMargin = UIUtils.dp2px(fragment.requireActivity(), 24f);
        } else {
            layoutParams.rightMargin = 0;
        }
        itemView.setLayoutParams(layoutParams);


        Glide.with(fragment.requireActivity())
                .load(merchantInfo.getLogoUrl())
                .into(merchantLogo);

        recommendMerchantName.setText(merchantInfo.getBusinessName());

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.requireActivity(), MerchantDetailActivity.class);
            intent.putExtra("merchant", merchantInfo);
            fragment.requireActivity().startActivity(intent);
        });
    }


}
