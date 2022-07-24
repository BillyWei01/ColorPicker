
package com.horizon.colorpickerdemo.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_color_picker.*
import com.horizon.colorpickerdemo.R

class ColorPickerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        val initColor = Color.RED
        val square = colorSquare
        square.setBackgroundColor(initColor)

        val pickColorFragment = PickColorFragment.newInstance(initColor).apply {
            colorDispatcher = { color ->
                square.setBackgroundColor(color)
            }
            popCustomColor = {
                switchCustomColorPicker(this)
            }
        }

        supportFragmentManager.beginTransaction()
                .add(R.id.container, pickColorFragment, "PickColorFragment")
                .commit()
    }

    private fun switchCustomColorPicker(fragment: PickColorFragment) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_left_out,
                        R.anim.slide_left_in,
                        R.anim.slide_right_out
                )
                .add(R.id.container, fragment.newCustomColorFragment(), "CustomColorFragment")
                .addToBackStack(null)
                .hide(fragment)
                .commit()
    }
}
