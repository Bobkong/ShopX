package com.squareup.shopx.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.viewholder.ItemViewHolder;
import com.squareup.shopx.viewholder.MerchantViewHolder;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private final List<GetMerchantDetailResponse.Item> mData;
    private final Activity mActivity;
    private final GetMerchantDetailResponse mMerchantDetail;
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        boolean isLastOne = position == mData.size() - 1;
        holder.setData(mData.get(position), mActivity, mMerchantDetail);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ItemListAdapter(List<GetMerchantDetailResponse.Item> data, Activity activity, GetMerchantDetailResponse merchantDetail) {
        this.mData = data;
        this.mActivity = activity;
        this.mMerchantDetail = merchantDetail;
    }
}
