package com.squareup.shopx.widget;


import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/10/23
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class ARFilterItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private int spanSize;


    public ARFilterItemDecoration(int spanCount, int spanSize, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.spanSize = spanSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item c
        int width = parent.getWidth();
        int childWidth = (width/spanSize)-spacing;
        int tmp = (width-spacing*(spanCount-1)-childWidth*spanCount)/2;

        if(spanCount == 1){
            outRect.left = tmp; //
            outRect.right = tmp; //
        }else {
            if (column == 0) {
                outRect.left = tmp; //
                outRect.right = spacing / 2;
            } else if (column == (spanCount - 1)) {
                outRect.left = spacing / 2; //
                outRect.right = tmp;
            } else {
                outRect.left = spacing / 2; //
                outRect.right = spacing / 2;
            }
        }

        if (position >= spanCount) {
            outRect.top = spacing / 2; // item top
        }
    }
}
