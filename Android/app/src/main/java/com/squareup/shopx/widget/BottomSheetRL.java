package com.squareup.shopx.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.squareup.shopx.activity.MainFragment;

public class BottomSheetRL extends RelativeLayout {
    public boolean isExpanded = false;
    public float DownY;
    public float moveY;
    public float lastMoveY = 0;
    public long currentMS;
    public MainFragment mainFragment;
    public BottomSheetRL(Context context) {
        super(context);
    }

    public BottomSheetRL(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSheetRL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BottomSheetRL(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                DownY = event.getY();//float DownY
                moveY = 0;
                currentMS = System.currentTimeMillis();//long currentMS     获取系统时间
                break;
            case MotionEvent.ACTION_MOVE:
                moveY += event.getY() - DownY;//y轴距离
                if (!isExpanded && moveY < 0 && moveY > -30) {
                    mainFragment.moveMapView(moveY);
                } else if (!isExpanded && moveY < -30) {
                    mainFragment.expandMapView();
                    isExpanded = true;
                }
                DownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                long moveTime = System.currentTimeMillis() - currentMS;//移动时间
                //判断是否继续传递信号
                if(moveTime>100 && moveY < -30 && !isExpanded){
                    mainFragment.expandMapView();
                    isExpanded = true;
                    return true; //不再执行后面的事件，在这句前可写要执行的触摸相关代码。点击事件是发生在触摸弹起后
                }
                break;
        }
        return false;//继续执行后面的代码
    }



}
