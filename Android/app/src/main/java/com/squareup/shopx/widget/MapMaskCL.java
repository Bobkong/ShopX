package com.squareup.shopx.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MapMaskCL extends ConstraintLayout {

    public MapMaskCL(Context context) {
        super(context);
    }

    public MapMaskCL(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapMaskCL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapMaskCL(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }


}
