package com.squareup.shopx.widget;

import androidx.recyclerview.widget.GridLayoutManager;

public class ARSpanSizeLookup extends GridLayoutManager.SpanSizeLookup{
    int spanCount =0;

    public ARSpanSizeLookup(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public int getSpanSize(int position) {
        int[][] count = {{24,0,0,0},{12,12,0,0},{8,8,8,0},{6,6,6,6}};
        int tmp = position%spanCount;
        return  count[spanCount-1][tmp];
    }
}