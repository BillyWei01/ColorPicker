package com.horizon.colorpicker.seekbar;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;


public class SaturationBar extends ColorBar {
    public SaturationBar(Context context) {
        super(context);
    }

    public SaturationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SaturationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(float[] hsv) {
        float s = hsv[1];
        hsv[1] = 0f;
        colors[0] = Color.HSVToColor(hsv);
        hsv[1] = 1f;
        colors[1] = Color.HSVToColor(hsv);
        hsv[1] = s;
        setShader();
        setValue(s);
    }
}
