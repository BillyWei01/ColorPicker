

package com.horizon.colorpickerdemo.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.horizon.colorpickerdemo.R
import kotlinx.android.synthetic.main.fragment_custom_color.*


class CustomColorFragment : Fragment() {
    var colorChangeListener :((Int) -> Unit) ?= null

    private var alpha : Int = 0

    private val hsv = FloatArray(3)

    companion object {
        @JvmStatic
        fun newInstance(color: Int, listener :((Int) -> Unit)) = CustomColorFragment().apply {
            val bundle = Bundle()
            bundle.putInt ("color", color)
            arguments = bundle
            colorChangeListener = listener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_custom_color, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var color = Color.RED
        arguments?.run { color = getInt("color") }
        alpha = color and 0xFF000000.toInt()

        Color.colorToHSV(color, hsv)

        hueBar.setValue(hsv[0] / 360F)
        saturationBar.setColor(hsv)
        brightnessBar.setColor(hsv)

        hueBar.setOnValueChangeListener {
            hsv[0] = it * 360F
            updateColor()
        }

        saturationBar.setOnValueChangeListener {
            hsv[1] = it
            updateColor()
        }

        brightnessBar.setOnValueChangeListener {
            hsv[2] = it
            updateColor()
        }
    }

    private fun updateColor(){
        saturationBar.setColor(hsv)
        brightnessBar.setColor(hsv)
        colorChangeListener?.invoke(alpha or (Color.HSVToColor(hsv) and 0xFFFFFF))
    }
}