package com.horizon.colorpicker.seekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.horizon.colorpicker.R;

public class NormalSeekBar extends AbstractSeekBar {
    private Paint mBarUsedPaint;
    private Paint mBarBgPaint;

    public NormalSeekBar(Context context) {
        super(context);
    }

    public NormalSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NormalSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs, int defStyle) {
        super.init(attrs, defStyle);
        Context context = getContext();
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.NormalSeekBar, defStyle, 0);
        final Resources res = context.getResources();
        mBarThickness = a.getDimensionPixelSize(R.styleable.NormalSeekBar_thickness,
                Utils.dip2px(context, 1.0F));
        int frontColor = a.getColor(R.styleable.NormalSeekBar_frontColor,
                res.getColor(R.color.frontColor));
        int backgroundColor = a.getColor(R.styleable.NormalSeekBar_backgroundColor,
                res.getColor(R.color.backgroundColor));
        a.recycle();

        mBarUsedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarUsedPaint.setColor(frontColor);
        mBarUsedPaint.setStyle(Paint.Style.STROKE);
        mBarUsedPaint.setStrokeWidth(mBarThickness);

        mBarBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarBgPaint.setColor(backgroundColor);
        mBarBgPaint.setStyle(Paint.Style.STROKE);
        mBarBgPaint.setStrokeWidth(mBarThickness);
    }

    @Override
    protected void drawLine(Canvas canvas) {
        canvas.drawLine(HALO_WIDTH, mBarY, mPointerCenterX, mBarY, mBarUsedPaint);
        canvas.drawLine(mPointerCenterX, mBarY, mWidth - HALO_WIDTH, mBarY, mBarBgPaint);
    }
}
