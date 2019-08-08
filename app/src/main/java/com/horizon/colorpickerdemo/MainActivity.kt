package com.horizon.colorpickerdemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.horizon.colorpickerdemo.ui.ColorPickerActivity
import com.horizon.colorpickerdemo.ui.FancyColorPickerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pickColorBtn.setOnClickListener{
            startActivity(Intent(this, ColorPickerActivity::class.java))
        }

        pickColorBtn2.setOnClickListener {
            startActivity(Intent(this, FancyColorPickerActivity::class.java))
        }
    }
}
