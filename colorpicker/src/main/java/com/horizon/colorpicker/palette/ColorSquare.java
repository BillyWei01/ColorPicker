package com.horizon.colorpicker.palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.horizon.colorpicker.R;
import com.horizon.colorpicker.seekbar.Utils;

@SuppressLint("ViewConstructor")
public class ColorSquare extends View {
    private static final int STYLE_FILL = 0;
    private static final int STYLE_STROKE = 1;
    private static final int STYLE_CHECKED = 2;

    private Context mContext;

    private int mStyle = STYLE_FILL;
    private int mColor;

    private boolean hasMeasure = false;
    private float mRoundRadius;
    public int mCheckDimen;

    private Paint mPaint;
    private RectF mRect;
    private Paint mStrokePaint;

    private Rect mCheckBitmapRect;
    private RectF mCheckRect;

    private Bitmap mPickBitmap;

    public ColorSquare(Context context, Bitmap checkBitmap) {
        super(context, null);
        mPickBitmap = checkBitmap;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRoundRadius = Utils.dip2px(context, 4);
        mCheckDimen = Utils.dip2px(context, 18);
    }

    public void setStroke() {
        mStyle |= STYLE_STROKE;
    }

    public void setChecked(boolean checked) {
        int oldStyle = mStyle;
        if (checked) {
            mStyle |= STYLE_CHECKED;
        } else {
            mStyle &= ~STYLE_CHECKED;
        }
        if (oldStyle != mStyle) {
            invalidate();
        }
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    public int getColor() {
        return mColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        if (!hasMeasure) {
            hasMeasure = true;
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.check_padding);
            mRect = new RectF(0, 0, w - padding, h - padding);
        }

        canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mPaint);
        if ((mStyle & STYLE_STROKE) != 0) {
            if (mStrokePaint == null) {
                mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mStrokePaint.setStyle(Paint.Style.STROKE);
                mStrokePaint.setColor(0xFFF1F1F1);
                mStrokePaint.setStrokeWidth(Utils.dip2px(mContext, 1));
            }
            canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mStrokePaint);
        }

        if ((mStyle & STYLE_CHECKED) != 0) {
            Bitmap bitmap = mPickBitmap;
            if (bitmap != null) {
                if (mCheckBitmapRect == null) {
                    mCheckBitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mCheckRect = new RectF(w - mCheckDimen, h - mCheckDimen, w, h);
                }
                canvas.drawBitmap(bitmap, mCheckBitmapRect, mCheckRect, mPaint);
            }
        }
    }


}
