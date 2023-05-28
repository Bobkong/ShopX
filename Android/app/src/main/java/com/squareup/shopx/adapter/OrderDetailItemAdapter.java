package com.squareup.shopx.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.AllMerchants;
import com.squareup.shopx.R;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.CartCallback;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.viewholder.CartItemViewHolder;
import com.squareup.shopx.viewholder.OrderItemViewHolder;

import java.util.ArrayList;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderItemViewHolder> {

    private final Activity mActivity;
    private ArrayList<GetMerchantDetailResponse.Item> items;
    private AllMerchantsResponse.ShopXMerchant mMerchantInfo;
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new OrderItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        holder.setData(items.get(position), mActivity, mMerchantInfo);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public OrderDetailItemAdapter(Activity activity, ArrayList<GetMerchantDetailResponse.Item> items, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        this.mActivity = activity;
        this.items = items;
        this.mMerchantInfo = merchantInfo;
    }
}
