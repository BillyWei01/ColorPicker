package com.horizon.colorpicker.seekbar;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

public class BrightnessBar extends ColorBar {
    public BrightnessBar(Context context) {
        super(context);
    }

    public BrightnessBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BrightnessBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(float[] hsv) {
        float v = hsv[2];
        hsv[2] = 0f;
        colors[0] = Color.HSVToColor(hsv);
        hsv[2] = 1f;
        colors[1] = Color.HSVToColor(hsv);
        hsv[2] = v;
        setShader();
        setValue(v);
    }
}
