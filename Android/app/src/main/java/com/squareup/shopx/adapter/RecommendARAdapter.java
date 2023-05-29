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
import com.squareup.shopx.viewholder.RecommendARViewHolder;
import com.squareup.shopx.viewholder.RecommendLoyaltyViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendARAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final MainFragment mFragment;
    private final ArrayList<AllMerchantsResponse.ShopXMerchant> mRecommendMerchants;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_ar, parent, false);
        return new RecommendARViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecommendARViewHolder) holder).setData(mFragment, mRecommendMerchants.get(position), position == 0, position == mRecommendMerchants.size() - 1);
    }


    @Override
    public int getItemCount() {
        return mRecommendMerchants.size();
    }

    public RecommendARAdapter(MainFragment fragment, ArrayList<AllMerchantsResponse.ShopXMerchant> loyaltyPrograms) {
        this.mFragment = fragment;
        this.mRecommendMerchants = loyaltyPrograms;
    }
}
