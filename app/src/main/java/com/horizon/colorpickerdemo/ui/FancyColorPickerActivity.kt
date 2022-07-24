
package com.horizon.colorpickerdemo.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.horizon.colorpickerdemo.R
import com.horizon.fancypicker.ColorPickerDialog
import kotlinx.android.synthetic.main.activity_color_picker.*

class FancyColorPickerActivity : AppCompatActivity() {

    private var mColor = Color.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fancy_color_picker)

        colorSquare.setBackgroundColor(mColor)
        colorSquare.setOnClickListener {
            ColorPickerDialog.Builder(this@FancyColorPickerActivity, mColor)
                    .setOnColorPickedListener { color ->
                        mColor = color
                        colorSquare.setBackgroundColor(mColor)
                    }
                    .build()
                    .show()
        }
    }

}