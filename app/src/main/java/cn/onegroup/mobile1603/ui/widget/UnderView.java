package cn.onegroup.mobile1603.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.onegroup.mobile1603.ui.activity.LockActivity;

/**
 * Created by CaostGrace on 2017/12/22 0022.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class UnderView extends View {
    private float mStartX;
    private int mWidth;


    //真实view
    private View mMoveView;

    public View getmMoveView() {
        return mMoveView;
    }

    public void setmMoveView(View mMoveView) {
        this.mMoveView = mMoveView;
    }

    public UnderView(Context context) {
        this(context, null);
    }

    public UnderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWidth = getWidth();
    }


    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float nx = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = nx;
                onAnimationEnd();
            case MotionEvent.ACTION_MOVE:
                handleMoveView(nx);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                doTriggerEvent(nx);
                break;
        }
        return true;
    }


    private void handleMoveView(float x) {
        float movex = x - mStartX;
        if (movex < 0)
            movex = 0;
        mMoveView.setTranslationX(movex);
        float mWidthFloat = (float) mWidth;//屏幕显示宽度
        if (getBackground() != null) {
            getBackground().setAlpha((int) ((mWidthFloat - mMoveView.getTranslationX()) /
                    mWidthFloat * 200));//初始透明度的值为200
        }
    }


    private void doTriggerEvent(float x) {
        float movex = x - mStartX;
        if (movex > (mWidth * 0.4)) {
            moveMoveView(mWidth - mMoveView.getLeft(), true);//自动移动到屏幕右边界之外，并finish掉

        } else {
            moveMoveView(-mMoveView.getLeft(), false);//自动移动回初始位置，重新覆盖
        }
    }

    private void moveMoveView(float to, boolean exit) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationX", to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (getBackground() != null) {
                    getBackground().setAlpha((int) (((float) mWidth - mMoveView.getTranslationX()
                    ) / (float) mWidth * 200));
                }
            }
        });//随移动动画更新背景透明度
        animator.setDuration(250).start();
        if (exit) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mainHandler.obtainMessage(LockActivity.MSG_LAUNCH_HOME).sendToTarget();
                    super.onAnimationEnd(animation);
                }
            });
        }

        //监听动画结束，利用Handler通知Activity退出
    }

}
