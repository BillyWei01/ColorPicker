package com.horizon.colorpicker.seekbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;



public class HueBar extends ColorBar {
    private static final int[] COLORS = new int[] {
            0xFFFF0000,
            0xFFFFFF00,
            0xFF00FF00,
            0xFF00FFFF,
            0xFF0000FF,
            0xFFFF00FF,
            0xFFFF0000,
    };

    public HueBar(Context context) {
        super(context);
    }

    public HueBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HueBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs, int defStyleAttr) {
        super.init(attrs, defStyleAttr);
        colors = COLORS;
    }
}
