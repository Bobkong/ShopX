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
import com.squareup.shopx.utils.UIUtils;

import java.util.Objects;

public class RecommendDiscountViewHolder extends RecyclerView.ViewHolder {

    private final ImageView recommendBg;
    private final TextView recommendTitle;
    private final TextView recommendSubtitle;
    private final TextView recommendMerchantName;
    private final TextView recommendTag;

    public RecommendDiscountViewHolder(@NonNull View itemView) {
        super(itemView);
        recommendBg = itemView.findViewById(R.id.recommend_bg);
        recommendTitle = itemView.findViewById(R.id.recommend_title);
        recommendSubtitle = itemView.findViewById(R.id.recommend_subtitle);
        recommendMerchantName = itemView.findViewById(R.id.recommend_merchant_name);
        recommendTag = itemView.findViewById(R.id.recommend_tag);
    }

    public void setData(MainFragment fragment, AllMerchantsResponse.ShopXMerchant merchantInfo, boolean ifFirst) {

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
        if (Objects.equals(merchantInfo.getDiscountType(), "FIXED_PERCENTAGE")) {
            recommendSubtitle.setText((int)merchantInfo.getDiscountAmount() + "% Off of your subtotal");
        } else {
            recommendSubtitle.setText("$" + (int)merchantInfo.getDiscountAmount() + " Off for designated items");
        }
        recommendTitle.setText(merchantInfo.getDiscountName());
        if (Objects.equals(merchantInfo.getDiscountType(), "FIXED_PERCENTAGE")) {
            recommendTag.setText((int)merchantInfo.getDiscountAmount() + "% Off");
        } else {
            recommendTag.setText("$" + (int)merchantInfo.getDiscountAmount() + " Off EA");
        }

        if (fragment.isAdded()) {
            if (merchantInfo.getRecommendText() == 1) {
                recommendTitle.setTextColor(fragment.requireActivity().getColor(R.color.black_0));
                recommendMerchantName.setTextColor(fragment.requireActivity().getColor(R.color.black_0));
                recommendSubtitle.setTextColor(fragment.requireActivity().getColor(R.color.black_30));
            } else {
                recommendTitle.setTextColor(fragment.requireActivity().getColor(R.color.white));
                recommendMerchantName.setTextColor(fragment.requireActivity().getColor(R.color.white));
                recommendSubtitle.setTextColor(fragment.requireActivity().getColor(R.color.black_90));
            }
        }

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.requireActivity(), MerchantDetailActivity.class);
            intent.putExtra("merchant", merchantInfo);
            fragment.requireActivity().startActivity(intent);
        });



    }
}
