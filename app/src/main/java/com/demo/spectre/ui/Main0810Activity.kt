package com.demo.spectre.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.spectre.R
import com.demo.spectre.loadad.PrepareLoadAd
import com.demo.spectre.showad.ShowFullScreenAd
import kotlinx.android.synthetic.main.activity_main.*

class Main0810Activity : AbsBaseActivity() {
    private var animator:ValueAnimator?=null
    private val fullScreen by lazy { ShowFullScreenAd(this,"sp_open"){ intentToHome() } }

    override fun layoutId(): Int = R.layout.activity_main

    override fun initView() {
        preLoadAd()
        startAnimator()
    }

    private fun preLoadAd(){
        PrepareLoadAd.preLoadAd("sp_open")
        PrepareLoadAd.preLoadAd("sp_home")
        PrepareLoadAd.preLoadAd("sp_result")
        PrepareLoadAd.preLoadAd("sp_connect")
    }

    private fun startAnimator(){
        animator = ValueAnimator.ofInt(0, 100).apply {
            duration=10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                progress_bar.progress = pro
                tv_progress.text="$pro%"
                val duration = (10 * (pro / 100.0F)).toInt()
                if (duration in 2..9){
                    if (fullScreen.hasAdData()){
                        stopAnimator()
                        progress_bar.progress = 100
                        fullScreen.show()
                    }
                }else if (duration>=10){
                    intentToHome()
                }
            }
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