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
import com.squareup.shopx.viewholder.OrderItemViewHolder;

import java.util.List;

public class OrderItemListAdapter extends RecyclerView.Adapter<OrderItemViewHolder> {

    private final List<GetMerchantDetailResponse.Item> mData;
    private final Activity mActivity;
    private final AllMerchantsResponse.ShopXMerchant mMerchantInfo;
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_merchant_item, parent, false);
        return new OrderItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        boolean isLastOne = position == mData.size() - 1;
        holder.setData(mData.get(position), mActivity, mMerchantInfo);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public OrderItemListAdapter(List<GetMerchantDetailResponse.Item> data, Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        this.mData = data;
        this.mActivity = activity;
        this.mMerchantInfo = merchantInfo;
    }
}
