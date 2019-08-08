package com.horizon.fancypicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


public class ColorPickerDialog extends Dialog
        implements ColorPicker.OnColorChangedListener, View.OnClickListener {
    private final static String ALLOWED_DIGITS = "1234567890abcdefABCDEF";

    private float[] mHSV = new float[3];

    private ColorPicker mColorPicker;
    private View mNewColorView;
    private View mCurrentColorView;
    private EditText mColorEt;

    private TextView mRedTv;
    private TextView mGreenTv;
    private TextView mBlueTv;
    private TextView mHueTv;
    private TextView mSatTv;
    private TextView mValTv;

    private OnColorPickedListener mListener;
    private TextWatcher mTextWatcher;

    public interface OnColorPickedListener {
        void onColorPicked(int color);
    }

    private ColorPickerDialog(Context context, int initialColor) {
        super(context);
        init(initialColor);
    }

    private void init(int color) {
        setContentView(R.layout.dialog_color_picker);

        findViews();

        mColorEt.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mColorPicker.setOnColorChangedListener(this);

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int color = strToColor(s.toString());
                mColorPicker.setColor(color);
                Color.colorToHSV(color, mHSV);
                updateColor(color, false);
            }
        };


        InputFilter[] filters = new InputFilter[2];
        filters[0] = new LoginFilter.UsernameFilterGeneric() {
            @Override
            public boolean isAllowed(char c) {
                return ALLOWED_DIGITS.indexOf(c) != -1;
            }

        };
        filters[1] = new InputFilter.LengthFilter(6);
        mColorEt.setFilters(filters);
        mColorEt.addTextChangedListener(mTextWatcher);


        mCurrentColorView.setBackgroundColor(color);
        mColorPicker.setColor(color);
        Color.colorToHSV(color, mHSV);
        updateColor(color, true);
    }

    private int strToColor(String str) {
        int color = 0;
        if (!TextUtils.isEmpty(str)) {
            if (str.length() == 3) {
                char[] chars = str.toCharArray();
                StringBuilder builder = new StringBuilder(6);
                builder.append(chars[0]).append(chars[0]);
                builder.append(chars[1]).append(chars[1]);
                builder.append(chars[2]).append(chars[2]);
                str = builder.toString();
            }
            try {
                color = Integer.parseInt(str, 16);
            } catch (NumberFormatException e) {
                Log.e("ColorPickerDialog", e.getMessage(), e);
            }
        }
        return color | 0xFF000000;
    }

    private void findViews() {
        mColorPicker = findViewById(R.id.color_picker);
        mNewColorView = findViewById(R.id.new_color_panel);
        mCurrentColorView = findViewById(R.id.current_color_panel);
        mColorEt = findViewById(R.id.color_et);

        mRedTv = findViewById(R.id.red_tv);
        mGreenTv = findViewById(R.id.green_tv);
        mBlueTv = findViewById(R.id.blue_tv);
        mHueTv = findViewById(R.id.hue_tv);
        mSatTv = findViewById(R.id.sat_tv);
        mValTv = findViewById(R.id.val_tv);

        View cancelBtn = findViewById(R.id.cancel_btn);
        View okBtn = findViewById(R.id.ok_btn);
        cancelBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onColorChanged(float[] hsv) {
        mHSV = hsv;
        updateColor(Color.HSVToColor(hsv), true);
    }

    @SuppressLint("SetTextI18n")
    private void updateColor(int color, boolean updateTextView) {
        color |= 0xFF000000;

        if (updateTextView) {
            mColorEt.removeTextChangedListener(mTextWatcher);
            mColorEt.setText(Integer.toHexString(color).substring(2, 8));
            mColorEt.addTextChangedListener(mTextWatcher);
        }

        mNewColorView.setBackgroundColor(color);

        mRedTv.setText("R: " + Color.red(color));
        mGreenTv.setText("G: " + Color.green(color));
        mBlueTv.setText("B: " + Color.blue(color));

        mHueTv.setText("H: " + Math.round(mHSV[0]));
        mSatTv.setText("S: " + Math.round(mHSV[1] * 100));
        mValTv.setText("B: " + Math.round(mHSV[2] * 100));
    }

    public int getColor() {
        return mColorPicker.getColor();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok_btn) {
            if (mListener != null) {
                mListener.onColorPicked(Color.HSVToColor(mHSV));
            }
        }
        dismiss();
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("old_color", ((ColorDrawable) mNewColorView.getBackground()).getColor());
        state.putInt("new_color", ((ColorDrawable) mCurrentColorView.getBackground()).getColor());
        return state;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mNewColorView.setBackgroundColor(savedInstanceState.getInt("old_color"));
        mColorPicker.setColor(savedInstanceState.getInt("new_color"));
    }

    public static class Builder {
        private Context context;
        private int initColor;
        private OnColorPickedListener listener;

        public Builder(Context context, int initColor) {
            this.context = context;
            this.initColor = initColor;
        }

        public Builder setOnColorPickedListener(OnColorPickedListener listener) {
            this.listener = listener;
            return this;
        }

        public ColorPickerDialog build() {
            ColorPickerDialog dialog = new ColorPickerDialog(context, initColor);
            dialog.mListener = listener;
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                window.setDimAmount(0f);
                int padding = Math.round(16f * context.getResources().getDisplayMetrics().density);
                Point dimens = fetchDimens(context);
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = dimens.x - padding * 2;
                lp.height = (int) (lp.width * 1.75f);
                window.setAttributes(lp);
            }
            return dialog;
        }
    }

    private static Point fetchDimens(Context context) {
        try {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                Point point = new Point();
                windowManager.getDefaultDisplay().getSize(point);
                if (point.x > 0 && point.y > 0) {
                    return point;
                }
            }
        } catch (Exception ignore) {
        }
        // should not be here, just in case
        return new Point(1080, 1920);
    }
}
