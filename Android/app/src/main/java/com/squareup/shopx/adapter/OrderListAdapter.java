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
import com.squareup.shopx.model.PlaceOrderRequest;
import com.squareup.shopx.viewholder.LoyaltyCardViewHolder;
import com.squareup.shopx.viewholder.OrderViewHolder;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity activity;
    private final ArrayList<PlaceOrderRequest> mOrders;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((OrderViewHolder) holder).setData(activity, mOrders.get(position), position == mOrders.size() - 1);
    }


    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public OrderListAdapter(Activity activity, ArrayList<PlaceOrderRequest> orders) {
        this.activity = activity;
        this.mOrders = orders;
    }
}
