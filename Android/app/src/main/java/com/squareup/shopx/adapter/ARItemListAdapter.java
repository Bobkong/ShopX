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
import com.squareup.shopx.viewholder.ARItemViewHolder;
import com.squareup.shopx.viewholder.ItemViewHolder;

import java.util.List;

public class ARItemListAdapter extends RecyclerView.Adapter<ARItemViewHolder> {

    private final List<GetMerchantDetailResponse.Item> mData;
    private final Activity mActivity;
    private final AllMerchantsResponse.ShopXMerchant merchantInfo;

    @NonNull
    @Override
    public ARItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_ar_item, parent, false);
        return new ARItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ARItemViewHolder holder, int position) {
        holder.setData(mData.get(position), mActivity, merchantInfo);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ARItemListAdapter(List<GetMerchantDetailResponse.Item> data, Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        this.mData = data;
        this.mActivity = activity;
        this.merchantInfo = merchantInfo;
    }
}
