
package com.horizon.colorpickerdemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.horizon.colorpickerdemo.R
import com.horizon.colorpicker.seekbar.Utils
import kotlinx.android.synthetic.main.fragment_color_picker.*


class PickColorFragment : Fragment(){
    var colorDispatcher: ((Int) -> Unit)? = null
    var popCustomColor : (() -> Unit)? = null

    private var resultColor: Int = 0xFF000000.toInt()

    private val colorChangeListener :((Int) -> Unit) = { color ->
        resultColor = color
        colorDispatcher?.invoke(color)
        palette?.selectedColor(color)
    }

    companion object {
        fun newInstance(color: Int) = PickColorFragment().apply {
            val bundle = Bundle()
            bundle.putInt ("color", color)
            arguments = bundle
        }
    }

    fun newCustomColorFragment() : CustomColorFragment{
       return CustomColorFragment.newInstance(resultColor, colorChangeListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_color_picker, container, false)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        arguments?.run { resultColor = getInt("color") }

        palette.selectedColor(resultColor)

        palette.setOnColorSelectListener { color->
            resultColor = (resultColor and 0xFF000000.toInt()) or (color and 0xFFFFFF)
            colorDispatcher?.invoke(resultColor)
        }

        palette.setOnCustomColorListener {
            popCustomFragment()
        }

        alphaBar.setValue(1f - (resultColor ushr 24) / 255F)
        alphaBar.setOnValueChangeListener { transparency ->
            val alpha = Utils.roundF((1f - transparency) * 255) and 0xFF
            resultColor = (alpha shl 24) or (resultColor and 0xFFFFFF)
            colorDispatcher?.invoke(resultColor)
        }
    }

    private fun popCustomFragment() {
        popCustomColor?.invoke()
    }

}
