package com.squareup.shopx.widget;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.shopx.R;

public class MobileVerifyItemView extends RelativeLayout {

    private TextView tvNumber;
    private View cursorView;
    private View indicatorView;

    private PropertyValuesHolder alphaHolder;
    private ObjectAnimator alphaAnimator;

    public MobileVerifyItemView(Context context) {
        this(context, null);
    }

    public MobileVerifyItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MobileVerifyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.item_view_mobile_verify, this);

        tvNumber = findViewById(R.id.tv_number);
        cursorView = findViewById(R.id.view_cursor);
        indicatorView = findViewById(R.id.view_indicator);

        Keyframe keyframe1 = Keyframe.ofFloat(0, 1);
        Keyframe keyframe2 = Keyframe.ofFloat(0.4f, 1);
        Keyframe keyframe3 = Keyframe.ofFloat(0.45f, 0);
        Keyframe keyframe4 = Keyframe.ofFloat(1, 0);
        alphaHolder = PropertyValuesHolder.ofKeyframe("alpha", keyframe1, keyframe2, keyframe3, keyframe4);
    }

    public void setText(String text) {
        tvNumber.setText(text);
    }

    public void setFocus(boolean focus) {
        // 获取焦点，显示光标
        if (focus) {
            cursorView.setVisibility(VISIBLE);

            if (alphaAnimator != null) {
                alphaAnimator.cancel();
            }

            alphaAnimator = ObjectAnimator.ofPropertyValuesHolder(cursorView, alphaHolder);
            alphaAnimator.setDuration(1500);
            alphaAnimator.setRepeatCount(-1);
            alphaAnimator.start();
        } else {
            cursorView.setVisibility(View.INVISIBLE);

            if (alphaAnimator != null) {
                alphaAnimator.cancel();
                alphaAnimator = null;
            }
        }
        indicatorView.setSelected(focus);
    }

}
