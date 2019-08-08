/*
 * Copyright 2012 Lars Werkman
 * Copyright 2019 Horizon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.horizon.fancypicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * ColorPicker with hue wheel and saturation-value panel
 *
 * hue wheel reference to
 * https://github.com/LarsWerkman/HoloColorPicker
 *
 * saturation-value panel reference to
 * https://github.com/relish-wang/ColorPicker
 */
public class ColorPicker extends View {
    /**
     * Colors to construct the color wheel using {@link SweepGradient}.
     */
    private static final int[] COLORS = new int[]{
            0xFFFF0000,
            0xFFFFFF00,
            0xFF00FF00,
            0xFF00FFFF,
            0xFF0000FF,
            0xFFFF00FF,
            0xFFFF0000,
    };

    /**
     * To make RED color in 90 degrees
     */
    private static final float WHEEL_ROTATE = 90f;

    /**
     * {@code Paint} instance used to draw the color wheel.
     */
    private Paint mColorWheelPaint;

    /**
     * {@code Paint} instance used to draw the pointer's "halo".
     */
    private Paint mPointerHaloPaint;

    /**
     * {@code Paint} instance used to draw the pointer (the selected color).
     */
    private Paint mPointerColorPaint;

    /**
     * The width of the color wheel thickness.
     */
    private int mColorWheelThickness;

    /**
     * The radius of the color wheel.
     */
    private int mColorWheelRadius;
    private int mPreferredColorWheelRadius;

    /**
     * The radius of the pointer.
     */
    private int mColorPointerRadius;

    /**
     * The radius of the halo of the pointer.
     */
    private int mColorPointerHaloRadius;

    /**
     * The rectangle enclosing the color wheel.
     */
    private RectF mColorWheelRectangle = new RectF();

    /**
     * {@code true} if the user clicked on the pointer to start the move mode. <br>
     * {@code false} once the user stops touching the screen.
     *
     * @see #onTouchEvent(MotionEvent)
     */
    private boolean mIsMovingWheelPointer = false;

    /**
     * color of wheel pointer
     */
    private int mHueColor;

    /**
     * Number of pixels the origin of this view is moved in X- and Y-direction.
     *
     * <p>
     * We use the center of this (quadratic) View as origin of our internal
     * coordinate system. Android uses the upper left corner as origin for the
     * View-specific coordinate system. So this is the value we use to translate
     * from one coordinate system to the other.
     * </p>
     *
     * <p>
     * Note: (Re)calculated in {@link #onMeasure(int, int)}.
     * </p>
     *
     * @see #onDraw(Canvas)
     */
    private float mTranslationOffset;

    /**
     * Distance between pointer and user touch in X-direction.
     */
    private float mSlopX;

    /**
     * Distance between pointer and user touch in Y-direction.
     */
    private float mSlopY;

    /**
     * The pointer's position expressed as angle (in rad).
     */
    private float mAngle;

    private float mSVTrackerRadius;
    private float mTracerBorderWidth;
    private boolean mMovingSVPointer = false;

    private float[] mHSV = new float[]{0f, 1f, 1f};
    private float[] mShaderHSV = new float[]{0f, 1f, 1f};

    private Paint mSVPaint;
    private Paint mSVTrackerPaint;
    private Paint mSVTrackerBorderPaint;
    private Paint mSVSelectorPaint;

    private RectF mSVRect = new RectF();
    private Shader mValShader;
    private ComposeShader mComposeShader;

    /**
     * {@code onColorChangedListener} instance of the onColorChangedListener
     */
    private OnColorChangedListener onColorChangedListener;

    /**
     * {@code onColorSelectedListener} instance of the onColorSelectedListener
     */
    private OnColorSelectedListener onColorSelectedListener;

    public ColorPicker(Context context) {
        super(context);
        init(null, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * An interface that is called whenever the color is changed. Currently it
     * is always called when the color is changes.
     *
     * @author lars
     */
    public interface OnColorChangedListener {
        public void onColorChanged(float[] hsv);
    }

    /**
     * An interface that is called whenever a new color has been selected.
     * Currently it is always called when the color wheel has been released.
     */
    public interface OnColorSelectedListener {
        public void onColorSelected(int color);
    }

    /**
     * Set a onColorChangedListener
     *
     * @param listener {@code OnColorChangedListener}
     */
    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.onColorChangedListener = listener;
    }

    /**
     * Set a onColorSelectedListener
     *
     * @param listener {@code OnColorSelectedListener}
     */
    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.onColorSelectedListener = listener;
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ColorPicker, defStyle, 0);
        final Resources res = getContext().getResources();

        mColorWheelThickness = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_wheel_thickness,
                res.getDimensionPixelSize(R.dimen.color_wheel_thickness));
        mColorWheelRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_wheel_radius,
                res.getDimensionPixelSize(R.dimen.color_wheel_radius));
        mPreferredColorWheelRadius = mColorWheelRadius;


        mColorPointerRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_pointer_radius,
                res.getDimensionPixelSize(R.dimen.color_pointer_radius));
        mColorPointerHaloRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_pointer_halo_radius,
                res.getDimensionPixelSize(R.dimen.color_pointer_halo_radius));

        a.recycle();

        initWheel();
        initSV();
    }

    private void initWheel() {
        mAngle = (float) (-Math.PI / 2);

        mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorWheelPaint.setShader(new SweepGradient(0, 0, COLORS, null));
        mColorWheelPaint.setStyle(Paint.Style.STROKE);
        mColorWheelPaint.setStrokeWidth(mColorWheelThickness);

        mPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerHaloPaint.setColor(Color.BLACK);
        mPointerHaloPaint.setAlpha(0x50);

        mPointerColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHueColor = calculateHueColor(mAngle);
        mPointerColorPaint.setColor(mHueColor);
    }

    private void initSV() {
        Context context = getContext();
        mSVPaint = new Paint();

        mSVTrackerRadius = dip2px(context, 7.5f);
        mTracerBorderWidth = dip2px(context, 1f);

        mSVTrackerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSVTrackerPaint.setColor(0xff000000);
        mSVTrackerPaint.setStyle(Paint.Style.STROKE);
        mSVTrackerPaint.setStrokeWidth(mTracerBorderWidth);

        mSVTrackerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSVTrackerBorderPaint.setColor(0x7fdddddd);
        mSVTrackerBorderPaint.setStyle(Paint.Style.STROKE);
        mSVTrackerBorderPaint.setStrokeWidth(mTracerBorderWidth);

        mSVSelectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSVSelectorPaint.setColor(0xffffffff);
        mSVSelectorPaint.setStyle(Paint.Style.STROKE);
        mSVSelectorPaint.setStrokeWidth(dip2px(context, 0.7f));
    }

    private static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dpValue * scale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int intrinsicSize = 2 * (mPreferredColorWheelRadius + mColorPointerHaloRadius);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int a;
        //int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            a = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            a = Math.min(intrinsicSize, widthSize);
        } else {
            a = intrinsicSize;
        }

/*        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(intrinsicSize, heightSize);
        } else {
            height = intrinsicSize;
        }
         int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        */

        // int min = Math.min(width, height);

        // make view height always equals to width
        setMeasuredDimension(a, a);

        mTranslationOffset = a * 0.5f;
        mColorWheelRadius = a / 2 - mColorWheelThickness - mColorPointerHaloRadius;
        mColorWheelRectangle.set(-mColorWheelRadius, -mColorWheelRadius,
                mColorWheelRadius, mColorWheelRadius);

        float SVRadius = (float) (mColorWheelRadius * Math.sqrt(2f) / 2f) * 0.86f;
        mSVRect.set(-SVRadius, -SVRadius, SVRadius, SVRadius);
    }

    private int ave(int s, int e, float t) {
        return s + Math.round(t * (e - s));
    }

    /**
     * Calculate the color using the supplied angle.
     *
     * @param angle The selected color's position expressed as angle (in rad).
     * @return The ARGB value of the color on the color wheel at the specified
     * angle.
     */
    private int calculateHueColor(float angle) {
        angle += Math.toRadians(WHEEL_ROTATE);
        float unit = (float) (angle / (2 * Math.PI));
        if (unit < 0) {
            unit += 1;
        }

        if (unit <= 0f) {
            mHSV[0] = 0f;
            return COLORS[0];
        }
        if (unit >= 1f) {
            mHSV[0] = 360f;
            return COLORS[COLORS.length - 1];
        }

        float p = unit * (COLORS.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = COLORS[i];
        int c1 = COLORS[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        mHSV[0] = unit * 360f;
        return Color.argb(a, r, g, b);
    }

    /**
     * Get the currently selected color.
     *
     * @return The ARGB value of the currently selected color.
     */
    public int getColor() {
        return mHueColor;
    }

    public void setColor(int color) {
        color |= 0xFF000000;
        mAngle = colorToAngle(color);
        mHueColor = calculateHueColor(mAngle);
        mPointerColorPaint.setColor(mHueColor);
        invalidate();
    }

    /**
     * Convert a color to an angle.
     *
     * @param color The RGB value of the color to "find" on the color wheel.
     * @return The angle (in rad) the "normalized" color is displayed on the
     * color wheel.
     */
    private float colorToAngle(int color) {
        Color.colorToHSV(color, mHSV);
        float degrees = mHSV[0] - WHEEL_ROTATE;
        if (degrees < -180f) {
            degrees += 360f;
        }
        return (float) Math.toRadians(degrees);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // All of our positions are using our internal coordinate system.
        // Instead of translating them,
        // we let Canvas do the work for us.
        canvas.translate(mTranslationOffset, mTranslationOffset);

        drawWheel(canvas);
        drawSVPanel(canvas);
    }

    private void drawWheel(Canvas canvas) {
        // Draw the color wheel.
        canvas.save();
        canvas.rotate(-WHEEL_ROTATE);
        canvas.drawOval(mColorWheelRectangle, mColorWheelPaint);
        canvas.restore();

        float x = (float) (mColorWheelRadius * Math.cos(mAngle));
        float y = (float) (mColorWheelRadius * Math.sin(mAngle));

        // Draw the pointer's "halo"
        canvas.drawCircle(x, y, mColorPointerHaloRadius, mPointerHaloPaint);

        // Draw the pointer (the currently selected color) slightly smaller on top.
        canvas.drawCircle(x, y, mColorPointerRadius, mPointerColorPaint);
    }

    private void drawSVPanel(Canvas canvas) {
        mSVPaint.setShader(getSVShader());
        canvas.drawRect(mSVRect, mSVPaint);

        int x = (int) (mHSV[1] * mSVRect.height() + mSVRect.left);
        int y = (int) ((1f - mHSV[2]) * mSVRect.width() + mSVRect.top);

        // Draw the sv panel's pointer
        if(mMovingSVPointer){
            canvas.drawCircle(x, y, mSVTrackerRadius + mTracerBorderWidth, mSVTrackerBorderPaint);
            canvas.drawCircle(x, y, mSVTrackerRadius - mTracerBorderWidth, mSVTrackerBorderPaint);
            canvas.drawCircle(x, y, mSVTrackerRadius , mSVTrackerPaint);
        }

        // canvas.clipRect(mSVRect);
        canvas.drawCircle(x, y, mSVTrackerRadius - mTracerBorderWidth * 2.5f , mSVSelectorPaint);
    }

    private Shader getSVShader() {
        if (mValShader == null) {
            mValShader = new LinearGradient(
                    mSVRect.left, mSVRect.top,
                    mSVRect.left, mSVRect.bottom,
                    Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        }

        if (mShaderHSV[0] != mHSV[0] || mComposeShader == null) {
            mShaderHSV[0] = mHSV[0];
            Shader satShader = new LinearGradient(
                    mSVRect.left, mSVRect.top,
                    mSVRect.right, mSVRect.top,
                    Color.WHITE, Color.HSVToColor(mShaderHSV), Shader.TileMode.CLAMP);
            mComposeShader = new ComposeShader(mValShader, satShader, PorterDuff.Mode.MULTIPLY);
        }

        return mComposeShader;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);

        // Convert coordinates to our internal coordinate system
        float x = event.getX() - mTranslationOffset;
        float y = event.getY() - mTranslationOffset;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float px = (float) (mColorWheelRadius * Math.cos(mAngle));
                float py = (float) (mColorWheelRadius * Math.sin(mAngle));
                if (x >= (px - mColorPointerHaloRadius)
                        && x <= (px + mColorPointerHaloRadius)
                        && y >= (py - mColorPointerHaloRadius)
                        && y <= (py + mColorPointerHaloRadius)) {
                    mSlopX = x - px;
                    mSlopY = y - py;
                    mIsMovingWheelPointer = true;
                    invalidate();
                } else if (mSVRect.contains(x, y)) {
                    changeSV(x, y);
                    mMovingSVPointer = true;
                } else {
                    float d = (float) Math.sqrt(x * x + y * y);
                    if (d <= mColorWheelRadius + mColorPointerHaloRadius
                            && d >= mColorWheelRadius - mColorPointerHaloRadius) {
                        mIsMovingWheelPointer = true;
                        invalidate();
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsMovingWheelPointer) {
                    mAngle = (float) Math.atan2(y - mSlopY, x - mSlopX);
                    mHueColor = calculateHueColor(mAngle);
                    mPointerColorPaint.setColor(mHueColor);
                    invokeColorChange();
                    invalidate();
                } else if (mMovingSVPointer) {
                    changeSV(x, y);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsMovingWheelPointer = false;
                mMovingSVPointer = false;
                if (onColorSelectedListener != null) {
                    onColorSelectedListener.onColorSelected(Color.HSVToColor(mHSV));
                }
                invalidate();
                break;
        }
        return true;
    }

    private void changeSV(float x, float y) {
        final RectF rect = mSVRect;
        float width = rect.width();
        float height = rect.height();

        if (x < rect.left) {
            x = 0f;
        } else if (x > rect.right) {
            x = width;
        } else {
            x = x - rect.left;
        }

        if (y < rect.top) {
            y = 0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y = y - rect.top;
        }
        mHSV[1] = x / width;
        mHSV[2] = 1f - y / height;
        invokeColorChange();
        invalidate();
    }

    public void invokeColorChange() {
        if (onColorChangedListener != null) {
            onColorChangedListener.onColorChanged(mHSV);
        }
    }
}
