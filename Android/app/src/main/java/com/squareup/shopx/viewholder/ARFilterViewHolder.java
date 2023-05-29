package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.MainFragment;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.utils.UIUtils;

public class ARFilterViewHolder extends RecyclerView.ViewHolder {

    private final ImageView itemLogo;
    private final TextView itemName;

    public ARFilterViewHolder(@NonNull View itemView) {
        super(itemView);
        itemLogo = itemView.findViewById(R.id.item_image);
        itemName = itemView.findViewById(R.id.item_name);
    }

    public void setData(Activity activity, GetMerchantDetailResponse.Item item) {


        Glide.with(activity)
                .load(item.getItemImage())
                .into(itemLogo);

        itemName.setText(item.getItemName());

        itemView.setOnClickListener(view -> {
            Uri uri = Uri.parse(item.getAREffectLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        });
    }


}
