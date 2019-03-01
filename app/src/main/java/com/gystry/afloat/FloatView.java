package com.gystry.afloat;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ViewSwitcher;

public abstract class FloatView {
    private final WindowManager mWindowManager;
    private Context mContext;
    private WindowManager.LayoutParams mParams;
    private boolean isShowing = false;
    private boolean mCreate = false;
    private View mDecor;
    private boolean isCanMove = false;

    public FloatView(@NonNull Context context) {
        mContext = context;
        //窗口管理器
        mWindowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        //布局参数
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //        mParams.type = WindowManager.LayoutParams.TYPE_SEARCH_BAR;
        //        mParams.type = WindowManager.LayoutParams.TYPE_INPUT_METHOD;
        mParams.format = PixelFormat.RGBA_8888;

        //设置之后window永远不会获取焦点,所以用户不能给此window发送点击事件焦点会传递给在其下面的可获取焦点的windo
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; //设置之后window永远不会获取焦点,所以用户不能给此window发送点击事件焦点会传递给在其下面的可获取焦点的windo
        //WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        //WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |//当这个window对用户是可见状态,则保持设备屏幕不关闭且不变暗
        // WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |//允许window扩展值屏幕之外
        //WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;//当window被添加或者显示,系统会点亮屏幕,就好像用户唤醒屏幕一样*/
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.width = setWidth();
        mParams.height = setHeight();
        if (setCreateAnimator() != 0) {
            mParams.windowAnimations = setCreateAnimator();
        }
    }

    protected abstract Object setContentView();

    //子类实现，设置宽高
    protected abstract int setHeight();

    protected abstract int setWidth();

    private int setCreateAnimator() {
        return -1;
    }

    public void show() {
        if (isShowing) {
            if (mDecor != null) {
                mDecor.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (!mCreate) {
            dispathOnCreate();
        }
        mWindowManager.addView(mDecor, mParams);
        isShowing = true;
        onStart();
    }

    private void dispathOnCreate() {
        if (!mCreate) {
            create();
            mCreate = true;
        }
    }

    private void create() {
        mDecor = LayoutInflater.from(getContext()).inflate((Integer) setContentView(), null);

        onCreate(mDecor, mParams);
        if (isCanMove) {
            mDecor.setOnTouchListener(new FloatOnTouchListener());
        }

        //如果集成的有ButterKnife，可以在这里声明ButterKnife.bind(this, mDecor);

    }

    //留给子类修改布局参数使用。
    protected void onCreate(View decor, WindowManager.LayoutParams layoutParams) {

    }

    protected void onStart() {

    }

    public void dismiss() {
        if (mDecor == null || !isShowing) {
            return;
        }
        try {
            onStop();
            mWindowManager.removeViewImmediate(mDecor);
            //这个地方可以注销ButterKnife
        } finally {
            mDecor = null;
            isShowing = false;
            mCreate = false;
            //这里可以还原参数
        }
    }

    //获取当前悬浮窗是否展示
    public final boolean isShowing() {
        return isShowing;
    }

    public final void hide() {
        if (mDecor != null) {
            mDecor.setVisibility(View.GONE);
        }
    }

    public final WindowManager.LayoutParams getParams() {
        return mParams;
    }

    public final WindowManager getWindowManager() {
        return mWindowManager;
    }

    @Nullable
    protected final <T extends View> T findViewById(@IdRes int id) {
        return mDecor.findViewById(id);
    }

    private void onStop() {

    }

    @NonNull
    protected final Context getContext() {
        return mContext;
    }

    public void setCanMove(boolean isCan) {
        if (isCan) {
            isCanMove = true;
        } else {
            isCanMove = false;
        }
    }

    private class FloatOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.e("FloatView", "onTouch: "+ event.getRawX()+"-----------"+event.getRawY());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    mParams.x = mParams.x + movedX;
                    mParams.y = mParams.y + movedY;

                    // 更新悬浮窗控件布局
                    mWindowManager.updateViewLayout(v, mParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

}
