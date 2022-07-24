package com.horizon.colorpicker.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.horizon.colorpicker.R;


public abstract class ColorBar extends AbstractSeekBar {
    private Paint mBarPaint;
    private RectF mBarRect = new RectF();
    private float mRoundRadius;
    protected int[] colors;

    public ColorBar(Context context) {
        super(context);
    }

    public ColorBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs, int defStyleAttr) {
        super.init(attrs, defStyleAttr);
        Context context = getContext();
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ColorBar, defStyleAttr, 0);
        mBarThickness = a.getDimensionPixelSize(R.styleable.ColorBar_barThickness,
                Utils.dip2px(context, 5f));
        a.recycle();

        mRoundRadius = mBarThickness / 2f;
        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL);

        colors = new int[2];
    }

    @Override
    protected void measure() {
        super.measure();
        mBarRect.set(POINTER_RADIUS, mBarY - mRoundRadius, mWidth - POINTER_RADIUS, mBarY + mRoundRadius);
        setShader();
    }

    protected void setShader() {
        if (mBarRect != null) {
            mBarPaint.setShader(new LinearGradient(0, 0, mBarRect.right - mBarRect.left, 0,
                    colors, null, Shader.TileMode.MIRROR));
        }
    }

    @Override
    protected void drawLine(Canvas canvas) {
        canvas.drawRoundRect(mBarRect, mRoundRadius, mRoundRadius, mBarPaint);
    }
}
