package com.horizon.colorpicker.seekbar;

import android.content.Context;

public class Utils {
    public static int roundF(float x) {
        return (int) (x + 0.5F);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Utils.roundF(dpValue * scale);
    }
}
