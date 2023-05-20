package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.graphics.Paint;
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
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.model.LoyaltyProgramResponse;
import com.squareup.shopx.utils.UIUtils;

public class RewardTiersViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final TextView rewardTiersText;
    private final ConstraintLayout rewardTiersCl;

    public RewardTiersViewHolder(@NonNull View itemView) {
        super(itemView);
        rewardTiersText = itemView.findViewById(R.id.reward_tiers_text);
        rewardTiersCl = itemView.findViewById(R.id.reward_tiers_cl);
    }

    public void setData(LoyaltyProgramResponse.RewardTier rewardTier, Activity activity, int position) {
        this.activity = activity;

        switch (position % 4) {
            case 0:
                rewardTiersCl.setBackground(activity.getResources().getDrawable(R.drawable.reward_tier_1));
                rewardTiersText.setTextColor(activity.getResources().getColor(R.color.black_0));
                setLeftRightMargin(false);
                break;
            case 1:
                rewardTiersCl.setBackground(activity.getResources().getDrawable(R.drawable.reward_tier_2));
                rewardTiersText.setTextColor(activity.getResources().getColor(R.color.white));
                setLeftRightMargin(true);
                break;
            case 2:
                rewardTiersCl.setBackground(activity.getResources().getDrawable(R.drawable.reward_tier_3));
                rewardTiersText.setTextColor(activity.getResources().getColor(R.color.black_0));
                setLeftRightMargin(false);
                break;
            default:
                rewardTiersCl.setBackground(activity.getResources().getDrawable(R.drawable.reward_tier_4));
                rewardTiersText.setTextColor(activity.getResources().getColor(R.color.black_0));
                setLeftRightMargin(true);
                break;
        }
        rewardTiersText.setText("Use " + rewardTier.getPoints() + " points to get " + rewardTier.getName());
    }

    private void setLeftRightMargin(boolean isLeft) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();

        if (isLeft) {
            params.leftMargin = UIUtils.dp2px(activity, 8f);
        } else {
            params.rightMargin = UIUtils.dp2px(activity, 8f);
        }

        itemView.setLayoutParams(params);
    }
}
