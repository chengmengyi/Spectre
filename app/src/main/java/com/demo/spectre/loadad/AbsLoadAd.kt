package com.demo.spectre.loadad

import com.demo.spectre.mSpectre
import com.demo.spectre.util.printLog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions

abstract class AbsLoadAd {
    protected val loadingList= arrayListOf<String>()
    protected val adDataMap= hashMapOf<String,AdData0810Bean>()

    protected fun isLoadingAd(type:String):Boolean{
        if (loadingList.contains(type)){
            printLog("$type is loading")
            return true
        }
        return false
    }

    protected fun hasCache(type: String):Boolean{
        if (adDataMap.containsKey(type)){
            val adDataBean = adDataMap[type]
            if (null!=adDataBean?.adData){
                if (adDataBean.getIsExpired()){
                    printLog("$type ad is expired")
                    clearAdCache(type)
                }else{
                    printLog("$type ad has cache")
                    return true
                }
            }
        }
        return false
    }

    protected fun startLoadAd(
        configAd0810Bean: ConfigAd0810Bean,
        success:(ad:AdData0810Bean)->Unit,
        fail:(msg:String)->Unit
    ){
        when(configAd0810Bean.type_0810){
            "kaiping"-> {
                AppOpenAd.load(
                    mSpectre,
                    configAd0810Bean.id_0810,
                    AdRequest.Builder().build(),
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    object : AppOpenAd.AppOpenAdLoadCallback(){
                        override fun onAdLoaded(p0: AppOpenAd) {
                            super.onAdLoaded(p0)
                            success.invoke(AdData0810Bean(adData = p0,time = System.currentTimeMillis()))
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            fail.invoke(p0.message)
                        }
                    }
                )
            }
            "chaping"->{
                InterstitialAd.load(
                    mSpectre,
                    configAd0810Bean.id_0810,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback(){
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            fail.invoke(p0.message)
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            success.invoke(AdData0810Bean(adData = p0,time = System.currentTimeMillis()))
                        }
                    }
                )
            }
            "yuansheng"->{
                AdLoader.Builder(
                    mSpectre,
                    configAd0810Bean.id_0810
                ).forNativeAd {
                    success.invoke(AdData0810Bean(adData = it,time = System.currentTimeMillis()))
                }
                    .withAdListener(object : AdListener(){
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            fail.invoke(p0.message)
                        }
                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder()
                            .setAdChoicesPlacement(
                                NativeAdOptions.ADCHOICES_BOTTOM_LEFT
                            )
                            .build()
                    )
                    .build()
                    .loadAd(AdRequest.Builder().build())
            }
        }
    }

    fun clearAdCache(type: String){
        adDataMap.remove(type)
    }
}