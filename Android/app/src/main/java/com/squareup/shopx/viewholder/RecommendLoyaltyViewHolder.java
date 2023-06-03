package com.squareup.shopx.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.MainFragment;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.netservice.ShopXAPI.ShopXApiService;
import com.squareup.shopx.utils.PreferenceUtils;
import com.squareup.shopx.utils.UIUtils;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RecommendLoyaltyViewHolder extends RecyclerView.ViewHolder {

    private final ImageView recommendBg;

    public RecommendLoyaltyViewHolder(@NonNull View itemView) {
        super(itemView);
        recommendBg = itemView.findViewById(R.id.recommend_bg);

    }

    public void setData(MainFragment fragment, AllMerchantsResponse.ShopXMerchant merchantInfo, boolean ifFirst, GetLoyaltyInfoResponse loyaltyInfo) {
        if (!fragment.isAdded()) {
            return;
        }

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (ifFirst) {
            layoutParams.leftMargin = UIUtils.dp2px(fragment.requireActivity(), 24f);
        } else {
            layoutParams.leftMargin = 0;
        }
        itemView.setLayoutParams(layoutParams);

        Glide.with(fragment.requireActivity())
                .load(merchantInfo.getRecommendBg())
                .into(recommendBg);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.requireActivity(), MerchantDetailActivity.class);
            intent.putExtra("merchant", merchantInfo);
            fragment.requireActivity().startActivity(intent);
        });
    }


}
