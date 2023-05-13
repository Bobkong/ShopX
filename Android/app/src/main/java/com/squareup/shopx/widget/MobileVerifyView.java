//package com.squareup.shopx.widget;
//
//import android.content.Context;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//
//import com.squareup.shopx.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MobileVerifyView extends RelativeLayout implements View.OnClickListener {
//
//    private EditText etInput;
//
//    private List<MobileVerifyItemView> itemViewList = new ArrayList();
//    private MobileTextWatcher textWatcher;
//
//    public MobileVerifyView(Context context) {
//        this(context, null);
//    }
//
//    public MobileVerifyView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public MobileVerifyView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//        inflate(context, R.layout.view_mobile_verify, this);
//
//        etInput = findViewById(R.id.et_input);
//
//        itemViewList.add((MobileVerifyItemView) findViewById(R.id.item_view1));
//        itemViewList.add((MobileVerifyItemView) findViewById(R.id.item_view2));
//        itemViewList.add((MobileVerifyItemView) findViewById(R.id.item_view3));
//        itemViewList.add((MobileVerifyItemView) findViewById(R.id.item_view4));
//
//        // 监听EditText的文本变化，修改显示文本和光标位置
//        etInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                setSelection();
//                if (textWatcher != null) {
//                    textWatcher.onTextChanged(etInput.getText().toString());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        for (MobileVerifyItemView itemView : itemViewList) {
//            itemView.setOnClickListener(this);
//        }
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//
//        setSelection();
//        showSoftKeyBoard();
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//
//        for (MobileVerifyItemView itemView : itemViewList) {
//            itemView.setFocus(false);
//        }
//    }
//
//    // 根据输入文本显示，并移动光标位置
//    private void setSelection() {
//        String text = etInput.getText().toString();
//        int len = etInput.length();
//
//        for (int index = 0; index < itemViewList.size(); index++) {
//            if (index < len) {
//                itemViewList.get(index).setText(text.substring(index, index+1));
//            } else {
//                itemViewList.get(index).setText("");
//            }
//            itemViewList.get(index).setFocus(false);
//        }
//
//        if (len < 4) {
//            itemViewList.get(len).setFocus(true);
//        } else {
//            itemViewList.get(3).setFocus(true);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        if ((v.getId() == R.id.item_view1)
//            || (v.getId() == R.id.item_view2)
//            || (v.getId() == R.id.item_view3)
//            ||(v.getId() == R.id.item_view4)) {
//            showSoftKeyBoard();
//        }
//    }
//
//    // 显示软键盘
//    private void showSoftKeyBoard() {
//        final InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                manager.showSoftInput(etInput, 0);
//            }
//        }, 300);
//    }
//
//    // 添加文本监听器
//    public void setTextWatcher(MobileTextWatcher textWatcher) {
//        this.textWatcher = textWatcher;
//    }
//
//    public interface MobileTextWatcher {
//        void onTextChanged(String text);
//    }
//
//}
