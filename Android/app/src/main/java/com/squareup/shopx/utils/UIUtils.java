package com.squareup.shopx.utils;

import android.content.Context;
import android.view.WindowManager;

public class UIUtils {

    public static int getWidth(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            return wm.getDefaultDisplay().getWidth();
        }
        else return 0;
    }

    public static int getHeight(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            return wm.getDefaultDisplay().getHeight();
        }
        else return 0;
    }

    public static int dp2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
    }


}
