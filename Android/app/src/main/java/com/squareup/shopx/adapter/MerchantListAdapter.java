package com.squareup.shopx.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.viewholder.MerchantViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MerchantListAdapter extends RecyclerView.Adapter<MerchantViewHolder> {

    private final List<AllMerchantsResponse.ShopXMerchant> mData;
    private final Activity mActivity;
    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homepage_merchant, parent, false);
        return new MerchantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
        holder.setData(mData.get(position), mActivity, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public MerchantListAdapter(List<AllMerchantsResponse.ShopXMerchant> data, Activity activity) {
        this.mData = data;
        this.mActivity = activity;
    }
}
