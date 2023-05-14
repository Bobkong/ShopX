package com.squareup.shopx.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.shopx.R;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetLoyaltyInfoResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.viewholder.ItemViewHolder;
import com.squareup.shopx.viewholder.MerchantDiscountViewHolder;
import com.squareup.shopx.viewholder.MerchantHeaderViewHolder;

import java.util.List;

public class MerchantDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity mActivity;
    private final GetMerchantDetailResponse mMerchantItems;
    private final AllMerchantsResponse.ShopXMerchant mMerchantInfo;
    private final GetLoyaltyInfoResponse mLoyaltyInfoResponse;
    private final int MERCHANT_HEADER = 0;
    private final int MERCHANT_DISCOUNT = 1;
    private final int MERCHANT_ITEMS = 2;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MERCHANT_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_header, parent, false);
            return new MerchantHeaderViewHolder(v);
        } else if (viewType == MERCHANT_DISCOUNT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_discount, parent, false);
            return new MerchantDiscountViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_item, parent, false);
            return new ItemViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((MerchantHeaderViewHolder) holder).setData(mActivity, mMerchantInfo);
        } else if (position == 1) {
            ((MerchantDiscountViewHolder) holder).setData(mActivity, mMerchantInfo, mLoyaltyInfoResponse);
        } else {
            ((ItemViewHolder) holder).setData(mMerchantItems.getItems().get(position - 2), mActivity, position, mMerchantInfo);

        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return MERCHANT_HEADER;
            case 1:
                return MERCHANT_DISCOUNT;
            default:
                return MERCHANT_ITEMS;
        }
    }

    @Override
    public int getItemCount() {
        return mMerchantItems.getItems().size() + 2;
    }

    public MerchantDetailAdapter(Activity activity, GetMerchantDetailResponse merchantDetail, AllMerchantsResponse.ShopXMerchant merchantInfo, GetLoyaltyInfoResponse loyaltyInfo) {
        this.mActivity = activity;
        this.mMerchantItems = merchantDetail;
        this.mMerchantInfo = merchantInfo;
        this.mLoyaltyInfoResponse = loyaltyInfo;
    }
}
