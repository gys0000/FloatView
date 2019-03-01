package com.gystry.afloat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class TestFloat extends FloatView {

    public static final int WIDTH_LENGTH = 112;
    public static final int HEIGHT_LENGTH = 113;
    private ImageView ivImg;

    public TestFloat(@NonNull Context context) {
        super(context);
    }

    @Override
    protected Object setContentView() {
        return R.layout.float_test;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(View decor, WindowManager.LayoutParams layoutParams) {
        super.onCreate(decor, layoutParams);
        ivImg = ((ImageView) findViewById(R.id.iv_img));
        //图片如果有点击事件，那么拖动图片区域的话，是不能拖动整个悬浮窗的。
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TestFloat", "onClick:------------------------");
                dismiss();
            }
        });
        setCanMove(true);
    }

    @Override
    protected int setHeight() {
        Log.e("TestFloat", "setHeight: " + getWH(HEIGHT_LENGTH) / 2);
        return getWH(HEIGHT_LENGTH) / 2;
    }

    @Override
    protected int setWidth() {
        Log.e("TestFloat", "setWidth: " + getWH(WIDTH_LENGTH) / 2);
        return getWH(WIDTH_LENGTH) / 2;
    }

    private int getWH(int type) {
        if (getContext() != null) {
            WindowManager dm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            switch (type) {
                case WIDTH_LENGTH:
                    return dm.getDefaultDisplay().getWidth();
                case HEIGHT_LENGTH:
                    return dm.getDefaultDisplay().getHeight();
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }
}
