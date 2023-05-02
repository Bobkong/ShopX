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
import com.squareup.shopx.model.GetMerchantDetailResponse;

public class ARItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView logo;
    private final TextView name;

    public ARItemViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.image);
        name = itemView.findViewById(R.id.name);
    }

    public void setData(GetMerchantDetailResponse.Item item, Activity activity) {
        Glide.with(activity)
                .load(item.getItemImage())
                .into(logo);

        name.setText(item.getItemName());

        itemView.setOnClickListener(view -> {
            ((ARItemActivity) activity).loadARItem(item.getARLink());
        });


    }
}
