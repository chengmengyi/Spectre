package com.spectre.unlimited.connection.wifisecure.showad

import com.spectre.unlimited.connection.wifisecure.loadad.PrepareLoadAd
import com.spectre.unlimited.connection.wifisecure.ui.AbsBaseActivity
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.*

class LoopGetNativeAd(
    private val context:AbsBaseActivity,
    private val type:String,
) :ShowNativeAd(){
    private var adData:NativeAd?=null
    private var loopJob:Job?=null

    fun loopGetNativeAd(){
        PrepareLoadAd.preLoadAd(type)
        loopJob= GlobalScope.launch(Dispatchers.Main) {
            delay(300L)
            while (true){
                if (!isActive) {
                    break
                }
                if (null!=PrepareLoadAd.getAdDataByType(type)&&context.resume0810){
                    cancel()
                    val ad = PrepareLoadAd.getAdDataByType(type)
                    if (ad is NativeAd){
                        adData?.destroy()
                        adData=ad
                        showNativeAd(context,type, ad)
                    }
                }
                delay(1000L)
            }
        }
    }

    fun cancelJob(){
        loopJob?.cancel()
        loopJob=null
        adData=null
    }

}