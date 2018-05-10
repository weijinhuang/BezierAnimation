package com.hwj.bezieranimation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var p = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refresProgress()
    }

    private fun refresProgress() {
        if (p == 100) {
            return
        }
        waveProgress.setProgress(p++)
        waveProgress.postDelayed({ refresProgress() }, 500)
    }
}

