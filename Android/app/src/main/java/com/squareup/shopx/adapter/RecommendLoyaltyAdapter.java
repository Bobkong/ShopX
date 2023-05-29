package com.squareup.shopx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.activity.MainFragment;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.viewholder.RecommendLoyaltyViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendLoyaltyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final MainFragment mFragment;
    private final ArrayList<AllMerchantsResponse.ShopXMerchant> mRecommendMerchants;
    private final HashMap<AllMerchantsResponse.ShopXMerchant, GetLoyaltyInfoResponse> mLoyaltyInfos;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_loyalty, parent, false);
        return new RecommendLoyaltyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecommendLoyaltyViewHolder) holder).setData(mFragment, mRecommendMerchants.get(position), position == 0, mLoyaltyInfos.get(mRecommendMerchants.get(position)));
    }


    @Override
    public int getItemCount() {
        return mRecommendMerchants.size();
    }

    public RecommendLoyaltyAdapter(MainFragment fragment, ArrayList<AllMerchantsResponse.ShopXMerchant> loyaltyPrograms, HashMap<AllMerchantsResponse.ShopXMerchant, GetLoyaltyInfoResponse> loyaltyInfos) {
        this.mFragment = fragment;
        this.mRecommendMerchants = loyaltyPrograms;
        this.mLoyaltyInfos = loyaltyInfos;
    }
}
