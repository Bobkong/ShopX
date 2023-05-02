package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.ARItemActivity;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.utils.MerchantAddressGenerator;
import com.squareup.shopx.utils.UIUtils;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView logo;
    private final TextView name;
    private final TextView description;
    private final TextView ARlink;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.image);
        name = itemView.findViewById(R.id.name);
        description = itemView.findViewById(R.id.description);
        ARlink = itemView.findViewById(R.id.ar_link);
    }

    public void setData(GetMerchantDetailResponse.Item item, Activity activity, GetMerchantDetailResponse merchantDetail) {
        this.activity = activity;
        Glide.with(activity)
                .load(item.getItemImage())
                .into(logo);

        name.setText(item.getItemName());
        description.setText(item.getItemDescription());
        if (item.getARLink().isEmpty()) {
            ARlink.setVisibility(View.GONE);
        } else {
            ARlink.setVisibility(View.VISIBLE);
            ARlink.setText("View AR");
            ARlink.setOnClickListener(view -> {
                Intent intent = new Intent(activity, ARItemActivity.class);
                intent.putExtra("itemId", item.getItemId());
                intent.putExtra("merchantDetail", merchantDetail);
                activity.startActivity(intent);
            });
        }



    }
}
