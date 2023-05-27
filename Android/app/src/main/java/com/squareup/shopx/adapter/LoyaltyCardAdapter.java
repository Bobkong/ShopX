package com.squareup.shopx.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.activity.LoyaltyCardsFragment;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.viewholder.ItemViewHolder;
import com.squareup.shopx.viewholder.LoyaltyCardViewHolder;
import com.squareup.shopx.viewholder.MerchantDiscountViewHolder;
import com.squareup.shopx.viewholder.MerchantHeaderViewHolder;

import java.util.ArrayList;

public class LoyaltyCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LoyaltyCardsFragment mFragment;
    private final ArrayList<AllMerchantsResponse.ShopXMerchant> mLoyaltyPrograms;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loyalty_card, parent, false);
        return new LoyaltyCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((LoyaltyCardViewHolder) holder).setData(mFragment, mLoyaltyPrograms.get(position));
    }


    @Override
    public int getItemCount() {
        return mLoyaltyPrograms.size();
    }

    public LoyaltyCardAdapter(LoyaltyCardsFragment fragment, ArrayList<AllMerchantsResponse.ShopXMerchant> loyaltyPrograms) {
        this.mFragment = fragment;
        this.mLoyaltyPrograms = loyaltyPrograms;
    }
}
