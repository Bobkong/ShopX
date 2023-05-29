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
    private final TextView recommendTitle;
    private final TextView recommendMerchantName;
    private final TextView recommendTag;

    public RecommendLoyaltyViewHolder(@NonNull View itemView) {
        super(itemView);
        recommendBg = itemView.findViewById(R.id.recommend_bg);
        recommendTitle = itemView.findViewById(R.id.recommend_title);
        recommendMerchantName = itemView.findViewById(R.id.recommend_merchant_name);
        recommendTag = itemView.findViewById(R.id.recommend_tag);
    }

    public void setData(MainFragment fragment, AllMerchantsResponse.ShopXMerchant merchantInfo, boolean ifFirst, GetLoyaltyInfoResponse loyaltyInfo) {
        if (!fragment.isAdded()) {
            return;
        }

        if (ifFirst) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            layoutParams.leftMargin = UIUtils.dp2px(fragment.requireActivity(), 24f);
            itemView.setLayoutParams(layoutParams);
        }

        Glide.with(fragment.requireActivity())
                .load(merchantInfo.getRecommendBg())
                .into(recommendBg);

        recommendMerchantName.setText(merchantInfo.getBusinessName());

        if (fragment.isAdded()) {
            if (merchantInfo.getRecommendText() == 1) {
                recommendTitle.setTextColor(fragment.requireActivity().getColor(R.color.black_0));
                recommendMerchantName.setTextColor(fragment.requireActivity().getColor(R.color.black_0));
            } else {
                recommendTitle.setTextColor(fragment.requireActivity().getColor(R.color.white));
                recommendMerchantName.setTextColor(fragment.requireActivity().getColor(R.color.white));
            }
        }

        recommendTitle.setText(loyaltyInfo.getLoyaltyInfo().getProgram().getRewardTiers().get(0).getName());
        if (loyaltyInfo.getLoyaltyInfo().getProgram().getRewardTiers().get(0).getName().contains("free")) {
            recommendTag.setText("Free Item");
        } else {
            recommendTag.setText("Discount");
        }

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.requireActivity(), MerchantDetailActivity.class);
            intent.putExtra("merchant", merchantInfo);
            fragment.requireActivity().startActivity(intent);
        });
    }


}
