package com.horizon.colorpicker.seekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.horizon.colorpicker.R;
import com.horizon.colorpicker.seekbar.listener.OnStopTrackingListener;
import com.horizon.colorpicker.seekbar.listener.OnValueChangeListener;


public abstract class AbstractSeekBar extends View {
    protected OnValueChangeListener mOnValueChangeListener;
    protected OnStopTrackingListener mStopTrackingListener;
    protected float mValue = 0F;

    protected int HALO_WIDTH;
    protected int POINTER_RADIUS;
    protected int POINTER_START_X;

    protected int mPointerCenterX;
    private int mPointerEndX;

    protected int mWidth;
    protected float mBarThickness;
    protected float mBarY;

    private Drawable mPointerDrawable;
    private Rect mPointerRect;

    private boolean mHasMeasure = false;

    public AbstractSeekBar(Context context) {
        this(context, null);
    }

    public AbstractSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        mOnValueChangeListener = listener;
    }

    public void setOnStopTrackingListener(OnStopTrackingListener onStopTrackingListener) {
        mStopTrackingListener = onStopTrackingListener;
    }

    public void setValue(float value) {
        mValue = value;
        mPointerCenterX = Math.round(mValue * (mPointerEndX - POINTER_START_X)) + POINTER_START_X;
        invalidate();
    }

    protected void init(AttributeSet attrs, int defStyle) {
        Context context = getContext();
        Resources res = context.getResources();
        float pointerDimension = Utils.dip2px(context, 24f);

        HALO_WIDTH = Math.round(pointerDimension * (9f / 86f));
        POINTER_RADIUS = Utils.roundF(pointerDimension / 2f);
        POINTER_START_X = POINTER_RADIUS;

        mPointerDrawable = res.getDrawable(R.drawable.seekbar_pointer);
    }

    protected void measure() {
        mWidth = getMeasuredWidth();
        int height = getMeasuredHeight();

        mBarY = height / 2f;

        mPointerEndX = mWidth - POINTER_RADIUS;

        if (mValue != 0) {
            mPointerCenterX = Math.round(mValue * (mPointerEndX - POINTER_START_X)) + POINTER_START_X;
        } else {
            mPointerCenterX = POINTER_START_X;
        }
        int pointerY = height / 2;
        mPointerRect = new Rect(
                mPointerCenterX - POINTER_RADIUS,
                pointerY - POINTER_RADIUS,
                mPointerCenterX + POINTER_RADIUS,
                pointerY + POINTER_RADIUS);
    }

    protected abstract void drawLine(Canvas canvas);

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mHasMeasure) {
            measure();
            mHasMeasure = true;
        }

        drawLine(canvas);

        mPointerRect.left = mPointerCenterX - POINTER_RADIUS;
        mPointerRect.right = mPointerCenterX + POINTER_RADIUS;
        mPointerDrawable.setBounds(mPointerRect);
        mPointerDrawable.draw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
            case MotionEvent.ACTION_MOVE:
                int ex = (int) event.getX();
                int x;
                if (ex < POINTER_START_X) {
                    x = POINTER_START_X;
                } else if (ex > mPointerEndX) {
                    x = mPointerEndX;
                } else {
                    x = ex;
                }
                if (x != mPointerCenterX) {
                    mPointerCenterX = x;
                    mValue = (float) (mPointerCenterX - POINTER_START_X) / (float) (mPointerEndX - POINTER_START_X);
                    if (mOnValueChangeListener != null) {
                        mOnValueChangeListener.onValueChanged(mValue);
                    }

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (mStopTrackingListener != null) {
                    mStopTrackingListener.onStopTrackingTouch(mValue);
                }
                break;
            default:
                break;
        }
        return true;
    }
}
