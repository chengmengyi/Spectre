package com.demo.spectre.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.spectre.R
import kotlinx.android.synthetic.main.activity_main.*

class Main0810Activity : AbsBaseActivity() {
    private var animator:ValueAnimator?=null

    override fun layoutId(): Int = R.layout.activity_main

    override fun initView() {
        startAnimator()
    }

    private fun startAnimator(){
        animator = ValueAnimator.ofInt(0, 100).apply {
            duration=2000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                progress_bar.progress = pro
                tv_progress.text="$pro%"
            }
            doOnEnd { intentToHome() }
            start()
        }
    }

    private fun intentToHome(){
        if (!ActivityUtils.isActivityExistsInStack(Home0810Activity::class.java)){
            startActivity(Intent(this,Home0810Activity::class.java))
        }
        finish()
    }

    private fun stopAnimator(){
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator=null
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
    }
}