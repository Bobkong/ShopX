package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.AllMerchants;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.ARItemActivity;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.CartUpdateEvent;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.utils.MerchantAddressGenerator;
import com.squareup.shopx.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView logo;
    private final TextView name;
    private final TextView originalPrice;
    private final TextView discountPrice;
    private final TextView fullMenuHeader;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.item_image);
        name = itemView.findViewById(R.id.item_name);
        originalPrice = itemView.findViewById(R.id.original_price);
        discountPrice = itemView.findViewById(R.id.discount_price);
        fullMenuHeader = itemView.findViewById(R.id.menu_header);
    }

    public void setData(GetMerchantDetailResponse.Item item, Activity activity, int position, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        this.activity = activity;

        if (position == 0) {
            fullMenuHeader.setVisibility(View.VISIBLE);
        } else {
            fullMenuHeader.setVisibility(View.GONE);
        }
        Glide.with(activity)
                .load(item.getItemImage())
                .into(logo);

        name.setText(item.getItemName());
        originalPrice.setText("$" + String.format("%.2f", item.getItemPrice() / 100.0));

        if (item.getItemDiscountPrice(merchantInfo) == item.getItemPrice()) {
            discountPrice.setVisibility(View.GONE);
            originalPrice.setTextColor(activity.getResources().getColor(R.color.black_0));
            originalPrice.setTextAppearance(activity, R.style.title_small);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) originalPrice.getLayoutParams();
            params.leftMargin = UIUtils.dp2px(activity, 24f);
            originalPrice.setLayoutParams(params);
        } else {
            originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            discountPrice.setVisibility(View.VISIBLE);
            discountPrice.setText("$" + String.format("%.2f", item.getItemDiscountPrice(merchantInfo) / 100.0));
        }

//        addToCart.setOnClickListener(view -> {
//            AllMerchants.INSTANCE.addToCart(merchantInfo, item);
//            EventBus.getDefault().post(new CartUpdateEvent(merchantInfo.getAccessToken()));
//        });
//        if (item.getARLink().isEmpty()) {
//            ARlink.setVisibility(View.GONE);
//        } else {
//            ARlink.setVisibility(View.VISIBLE);
//            ARlink.setText("View AR");
//            ARlink.setOnClickListener(view -> {
//                Intent intent = new Intent(activity, ARItemActivity.class);
//                intent.putExtra("itemId", item.getItemId());
//                intent.putExtra("merchantDetail", merchantDetail);
//                activity.startActivity(intent);
//            });
//        }



    }
}
