package com.spectre.unlimited.connection.wifisecure.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.spectre.unlimited.connection.wifisecure.loadad.PrepareLoadAd
import com.spectre.unlimited.connection.wifisecure.ui.Home0810Activity
import com.spectre.unlimited.connection.wifisecure.ui.Main0810Activity
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Acc0810 {
    var isFront=true
    private var job: Job?=null
    private var reload=false
    var refreshHomeNativeAd=true


    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(call)
    }

    private val call=object : Application.ActivityLifecycleCallbacks{
        private var num=0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            num++
            job?.cancel()
            job=null
            if (num==1){
                isFront=true
                if (reload){
                    refreshHomeNativeAd=true
                    if (ActivityUtils.isActivityExistsInStack(Home0810Activity::class.java)){
                        activity.startActivity(Intent(activity, Main0810Activity::class.java))
                    }
                }
                reload=false
            }
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            num--
            if (num<=0){
                isFront=false
                job= GlobalScope.launch {
                    delay(3000L)
                    reload=true
                    PrepareLoadAd.isShowingFullScreenAd=false
                    ActivityUtils.finishActivity(Main0810Activity::class.java)
                    ActivityUtils.finishActivity(AdActivity::class.java)
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}