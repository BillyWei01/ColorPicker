package com.horizon.colorpicker.palette;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.horizon.colorpicker.R;
import com.horizon.colorpicker.seekbar.Utils;


public class ColorPalette extends HorizontalScrollView implements OnClickListener {
    // TODO collect color
    private static final int[] COLORS = {
            0xFFFFFFFF,
            0xFF000000,
            0xFF9C27B0,
            0xFF673AB7,
            0xFF3FB1F5,
            0xFF2196F3,
            0xFF00BCD4,
            0xFFFFA000,
            0xFFB8C500,
            0xFF8BC34A,
            0xFF4CAF50,
            0xFF009688,
            0xFFF57C00,
            0xFFF44336,
            0xFFE91E63,
            0xFFC2185B,
    };

    private OnCustomColorListener mOnCustomColorListener;
    private OnColorSelectListener mOnColorSelectListener;

    private ColorSquare mLastCheckedView;
    private Context mContext;
    private int mDimension;
    private int mMarginRight;
    private Bitmap mPickBitmap;

    public ColorPalette(Context context) {
        this(context, null, 0);
    }

    public ColorPalette(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPalette(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnCustomColorListener(OnCustomColorListener customColorListener) {
        mOnCustomColorListener = customColorListener;
    }

    public void setOnColorSelectListener(OnColorSelectListener colorSelectListener) {
        mOnColorSelectListener = colorSelectListener;
    }

    private void init(Context context) {
        mContext = context;
        Resources res = context.getResources();

        mPickBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_check);

        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

        mDimension = res.getDimensionPixelSize(R.dimen.square_width);

        mMarginRight = Utils.dip2px(context, 8);
        int lastMarginRight = Utils.dip2px(context, 16);

        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.HORIZONTAL);
        addView(container);

        for (int color : COLORS) {
            container.addView(makeColorView(color));
        }

        int imageDimension = mDimension - res.getDimensionPixelSize(R.dimen.check_padding);
        ImageView customColorView = new ImageView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageDimension, imageDimension);
        lp.setMargins(0, 0, lastMarginRight, 0);
        customColorView.setLayoutParams(lp);
        customColorView.setImageResource(R.drawable.custom_color);
        customColorView.setOnClickListener(this);
        container.addView(customColorView);
    }

    private View makeColorView(int color) {
        ColorSquare colorView = new ColorSquare(mContext, mPickBitmap);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mDimension, mDimension);
        lp.setMargins(0, 0, mMarginRight, 0);
        colorView.setLayoutParams(lp);
        colorView.setColor(color);
        if (color == Color.WHITE) {
            colorView.setStroke();
        }
        colorView.setOnClickListener(this);
        return colorView;
    }

    private void checkSquare(ColorSquare square) {
        square.setChecked(true);
        if (mLastCheckedView != null && square != mLastCheckedView) {
            mLastCheckedView.setChecked(false);
        }
        mLastCheckedView = square;
    }

    public void selectedColor(int color) {
        LinearLayout linearLayout = (LinearLayout) getChildAt(0);
        for (int i = 0; i < linearLayout.getChildCount() - 1; i++) {
            ColorSquare square = (ColorSquare) linearLayout.getChildAt(i);
            int item = square.getColor();
            if ((color & 0xFFFFFF) == (item & 0xFFFFFF)) {
                square.setChecked(true);
            } else {
                square.setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ColorSquare) {
            ColorSquare colorSquare = (ColorSquare) v;
            checkSquare(colorSquare);
            if (mOnColorSelectListener != null) {
                mOnColorSelectListener.onColorSelect(colorSquare.getColor());
            }
        } else {
            if (mOnCustomColorListener != null) {
                mOnCustomColorListener.onCustomColor();
            }
        }
    }

    public interface OnCustomColorListener {
        void onCustomColor();
    }

    public interface OnColorSelectListener {
        void onColorSelect(int color);
    }
}
