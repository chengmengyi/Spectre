package com.spectre.unlimited.connection.wifisecure.showad

import com.spectre.unlimited.connection.wifisecure.loadad.PrepareLoadAd
import com.spectre.unlimited.connection.wifisecure.ui.AbsBaseActivity
import com.spectre.unlimited.connection.wifisecure.util.printLog
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowFullScreenAd(
    private val context:AbsBaseActivity,
    private val type:String,
    private val onShowFinish:()->Unit
) {

    fun show(){
        if (PrepareLoadAd.isShowingFullScreenAd||!context.resume0810){
            onShowFinish.invoke()
            return
        }
        printLog("start show  $type ad")
        val ad = PrepareLoadAd.getAdDataByType(type)
        PrepareLoadAd.isShowingFullScreenAd=true
        when(ad){
            is AppOpenAd ->{
                ad.fullScreenContentCallback=callback
                ad.show(context)
            }
            is InterstitialAd ->{
                ad.fullScreenContentCallback=callback
                ad.show(context)
            }
            else-> PrepareLoadAd.isShowingFullScreenAd=false
        }
    }


    fun hasAdData()=null!=PrepareLoadAd.getAdDataByType(type)

    private val callback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            PrepareLoadAd.isShowingFullScreenAd=false
            if (type=="sp_connect"){
                PrepareLoadAd.preLoadAd(type)
            }
            delayCallback()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            PrepareLoadAd.isShowingFullScreenAd=true
            PrepareLoadAd.clearAdCache(type)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            PrepareLoadAd.isShowingFullScreenAd=false
            PrepareLoadAd.clearAdCache(type)
            delayCallback()
        }
    }

    private fun delayCallback(){
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (context.resume0810){
                onShowFinish.invoke()
            }
        }
    }
}