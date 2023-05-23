package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.R;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.utils.UIUtils;
import com.squareup.shopx.widget.shadow.ShadowLayout;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView logo;
    private final TextView name;
    private final TextView originalPrice;
    private final TextView realPrice;
    private final ConstraintLayout fullMenuHeader;
    private final ImageView addItem;
    private final ImageView viewInAR;
    private final ShadowLayout itemCard;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.item_image);
        name = itemView.findViewById(R.id.item_name);
        originalPrice = itemView.findViewById(R.id.original_price);
        realPrice = itemView.findViewById(R.id.discount_price);
        fullMenuHeader = itemView.findViewById(R.id.menu_header);
        addItem = itemView.findViewById(R.id.add_item);
        viewInAR = itemView.findViewById(R.id.view_in_ar);
        itemCard = itemView.findViewById(R.id.item_card);
    }

    public void setData(GetMerchantDetailResponse.Item item, Activity activity, int position, AllMerchantsResponse.ShopXMerchant merchantInfo, boolean isLast) {
        this.activity = activity;

        if (position == 0) {
            fullMenuHeader.setVisibility(View.VISIBLE);
            if (merchantInfo.getArEnable() == 1) {
                viewInAR.setVisibility(View.VISIBLE);
                viewInAR.setOnClickListener(view -> {
                    ((MerchantDetailActivity) activity).showARPage(0);
                });
            } else {
                viewInAR.setVisibility(View.GONE);
            }
        } else {
            fullMenuHeader.setVisibility(View.GONE);
        }
        Glide.with(activity)
                .load(item.getItemImage())
                .into(logo);

        name.setText(item.getItemName());

        if (item.getPricingType().equals("FIXED_PRICING")) {
            addItem.setVisibility(View.VISIBLE);
            originalPrice.setText("$" + String.format("%.2f", item.getItemPrice() / 100.0));

            if (item.getItemDiscountPrice(merchantInfo) == item.getItemPrice()) {
                realPrice.setVisibility(View.GONE);
                originalPrice.setTextColor(activity.getResources().getColor(R.color.black_0));
                originalPrice.setTextAppearance(activity, R.style.title_small);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) originalPrice.getLayoutParams();
                params.leftMargin = UIUtils.dp2px(activity, 24f);
                originalPrice.setLayoutParams(params);
            } else {
                originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                realPrice.setVisibility(View.VISIBLE);
                realPrice.setText("$" + String.format("%.2f", item.getItemDiscountPrice(merchantInfo) / 100.0));
            }
        } else {
            addItem.setVisibility(View.GONE);
            realPrice.setVisibility(View.GONE);
            originalPrice.setTextColor(activity.getResources().getColor(R.color.black_0));
            originalPrice.setTextAppearance(activity, R.style.title_small_secondary);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) originalPrice.getLayoutParams();
            params.leftMargin = UIUtils.dp2px(activity, 24f);
            originalPrice.setLayoutParams(params);
            originalPrice.setText("Ask merchants for price");
            originalPrice.setTextColor(activity.getResources().getColor(R.color.black_70));

        }

        itemCard.setOnClickListener(v -> {
            ((MerchantDetailActivity) activity).showItemBottomSheet(item);
        });

        if (isLast) {
            // set extra margin
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            layoutParams.bottomMargin = UIUtils.dp2px(activity, 100f);
            itemView.setLayoutParams(layoutParams);
        }

    }
}
