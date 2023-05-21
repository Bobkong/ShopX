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

import java.util.ArrayList;

public class CartItemListAdapter extends RecyclerView.Adapter<CartItemViewHolder> {

    private final Activity mActivity;
    private final AllMerchantsResponse.ShopXMerchant mMerchantInfo;
    private final ArrayList<GetMerchantDetailResponse.Item> items = new ArrayList<>();
    private final CartCallback mDismissListener;
    private final int from;
    public static final int FROM_ORDER = 0;
    public static final int FROM_OTHER = 1;
    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new CartItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        holder.setData(items.get(position), mActivity, mMerchantInfo, mDismissListener, this, from);
    }

    @Override
    public int getItemCount() {
        return AllMerchants.INSTANCE.getCartItems(mMerchantInfo).size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshItems() {
        items.clear();
        items.addAll(AllMerchants.INSTANCE.getCartItems(mMerchantInfo).keySet());
        this.notifyDataSetChanged();

        if (items.size() == 0) {
            mDismissListener.dismissCart();
        }
    }

    public CartItemListAdapter(Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo, CartCallback dismissListener, int from) {
        this.mActivity = activity;
        this.mMerchantInfo = merchantInfo;
        mDismissListener = dismissListener;
        this.from = from;
        items.addAll(AllMerchants.INSTANCE.getCartItems(merchantInfo).keySet());
    }
}
