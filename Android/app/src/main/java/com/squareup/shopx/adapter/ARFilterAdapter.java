package com.squareup.shopx.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.activity.MainFragment;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.viewholder.ARFilterViewHolder;
import com.squareup.shopx.viewholder.RecommendARViewHolder;

import java.util.ArrayList;

public class ARFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity mActivity;
    private final ArrayList<GetMerchantDetailResponse.Item> mItems;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ar_filter, parent, false);
        return new ARFilterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ARFilterViewHolder) holder).setData(mActivity, mItems.get(position));
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ARFilterAdapter(Activity activity, ArrayList<GetMerchantDetailResponse.Item> items) {
        this.mActivity = activity;
        this.mItems = items;
    }
}
