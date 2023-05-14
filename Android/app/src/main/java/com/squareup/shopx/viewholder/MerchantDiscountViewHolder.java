package com.squareup.shopx.viewholder;

import static com.squareup.shopx.utils.MerchantAddressGenerator.generateMerchantAddress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.AllMerchants;
import com.squareup.shopx.R;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.utils.UIUtils;

import java.nio.file.Path;
import java.util.Objects;

public class MerchantDiscountViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final CardView discountCl, loyaltyCl;
    private final TextView discountTitle;
    private final TextView discountDesc;
    private final TextView loyaltyTitle;
    private final TextView loyaltyDesc;

    public MerchantDiscountViewHolder(@NonNull View itemView) {
        super(itemView);
        discountCl = itemView.findViewById(R.id.merchant_discount_info);
        loyaltyCl = itemView.findViewById(R.id.merchant_loyalty_info);
        discountTitle = itemView.findViewById(R.id.discount_title);
        discountDesc = itemView.findViewById(R.id.discount_desc);
        loyaltyTitle = itemView.findViewById(R.id.loyalty_title);
        loyaltyDesc = itemView.findViewById(R.id.loyalty_desc);

    }

    @SuppressLint("SetTextI18n")
    public void setData(Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo, GetLoyaltyInfoResponse loyaltyInfo) {
        this.activity = activity;

        if (loyaltyInfo == null) {
            loyaltyCl.setVisibility(View.GONE);
            discountCl.setVisibility(View.GONE);
            return;
        }

        if (merchantInfo.getIfLoyalty() == 1) {
            loyaltyCl.setVisibility(View.VISIBLE);
            loyaltyTitle.setText("Redeem rewards to get " + loyaltyInfo.getLoyaltyInfo().getProgram().getRewardTiers().get(0).getName());
            if (loyaltyInfo.getIsEnrolled() == 1) {
                loyaltyDesc.setText("You have got " + loyaltyInfo.getPoints() + " pts");
            } else {
                loyaltyDesc.setText("Enroll Now");
            }
        } else {
            loyaltyCl.setVisibility(View.GONE);
        }

        if (merchantInfo.getDiscountType() == null || merchantInfo.getDiscountType().isEmpty()) {
            discountCl.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) loyaltyCl.getLayoutParams();
            params.topMargin = 0;
            loyaltyCl.setLayoutParams(params);
        } else {
            discountCl.setVisibility(View.VISIBLE);
            if (Objects.equals(merchantInfo.getDiscountType(), "FIXED_PERCENTAGE")) {
                discountTitle.setText((int)merchantInfo.getDiscountAmount() + "% Off of your subtotal");
            } else {
                discountTitle.setText("$" + (int)merchantInfo.getDiscountAmount() + " Off each item/service");
            }
        }
    }
}
