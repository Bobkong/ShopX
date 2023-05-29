package com.squareup.shopx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.activity.MainFragment;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.viewholder.RecommendDiscountViewHolder;

import java.util.ArrayList;

public class RecommendDiscountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final MainFragment mFragment;
    private final ArrayList<AllMerchantsResponse.ShopXMerchant> mRecommendMerchants;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_discount, parent, false);
        return new RecommendDiscountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecommendDiscountViewHolder) holder).setData(mFragment, mRecommendMerchants.get(position), position ==  0);
    }


    @Override
    public int getItemCount() {
        return mRecommendMerchants.size();
    }

    public RecommendDiscountAdapter(MainFragment fragment, ArrayList<AllMerchantsResponse.ShopXMerchant> loyaltyPrograms) {
        this.mFragment = fragment;
        this.mRecommendMerchants = loyaltyPrograms;
    }
}
