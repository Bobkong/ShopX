package com.squareup.shopx.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.model.LoyaltyProgramResponse;
import com.squareup.shopx.viewholder.ItemViewHolder;
import com.squareup.shopx.viewholder.MerchantDiscountViewHolder;
import com.squareup.shopx.viewholder.MerchantHeaderViewHolder;
import com.squareup.shopx.viewholder.RewardTiersViewHolder;

public class RewardTiersAdapter extends RecyclerView.Adapter<RewardTiersViewHolder> {

    private final Activity mActivity;
    private final GetLoyaltyInfoResponse mLoyaltyInfoResponse;

    @NonNull
    @Override
    public RewardTiersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_tiers, parent, false);
            return new RewardTiersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardTiersViewHolder holder, int position) {
        holder.setData(mLoyaltyInfoResponse.getLoyaltyInfo().getProgram().getRewardTiers().get(position), mActivity, position);
    }

    @Override
    public int getItemCount() {
        return mLoyaltyInfoResponse.getLoyaltyInfo().getProgram().getRewardTiers().size();
    }

    public RewardTiersAdapter(Activity activity, GetLoyaltyInfoResponse loyaltyInfo) {
        this.mActivity = activity;
        this.mLoyaltyInfoResponse = loyaltyInfo;
    }
}
