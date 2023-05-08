package com.squareup.shopx.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.squareup.shopx.activity.MainFragment;

public class MaskRL extends RelativeLayout {

    public MaskRL(Context context) {
        super(context);
    }

    public MaskRL(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskRL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaskRL(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }


}
