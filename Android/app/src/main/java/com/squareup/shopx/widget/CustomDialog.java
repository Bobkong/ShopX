package com.squareup.shopx.widget;

import android.app.Dialog;
import android.content.Context;

import com.squareup.shopx.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context, int layoutId) {

        //使用自定义Dialog样式
        super(context, R.style.custom_dialog);

        //指定布局
        setContentView(layoutId);

        //点击外部不可消失
        //setCancelable(false);
    }
}